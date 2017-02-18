package com.admin.ht.utils.loader;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.view.View;
import android.widget.ImageView;

import com.admin.ht.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Holy-Spirit on 2016/5/30.
 */

public class ImageLoader {
    /*单例模式内嵌对象*/
    private static ImageLoader mInstance = null;
    /*一级缓存：内存缓存*/
    private LruMemoryCache mMemCache = null;
    /*二级缓存：磁盘缓存*/
    private LruDiskCache mDiskCache = null;
    /*线程池*/
    private ExecutorService mService = null;
    /*图片容器缓存*/
    private Map<View, String> mViewCache = null;

    public Map<View,String> getViewCache(){
        return mViewCache;
    }

    public Bitmap getFromMemCache(String url){
        return mMemCache.get(url);
    }

    public void putToMemCache(String url ,Bitmap bm){
        mMemCache.put(url,bm);
    }

    /**
     * 初始化两个缓存容器和一个线程池
     *
     * @param context 用于获得手机本身磁盘缓存对象
     */
    private ImageLoader(Context context) {

        mMemCache = new LruMemoryCache();
        mDiskCache = new LruDiskCache(context);
        mService = Executors.newFixedThreadPool(5);
        mViewCache = Collections.synchronizedMap(new WeakHashMap<View, String>());
    }



    /**
     * 单例模式，意图在统一下载资源管理
     * @param context
     * @return
     */
    public static ImageLoader newInstance(Context context){

        if(mInstance == null){

            synchronized (ImageLoader.class)
            {
                if (mInstance == null)
                {
                    mInstance = new ImageLoader(context);
                }
            }
        }
        return mInstance;
    }



    /**
     * 第一级缓存：内存缓存、此方法检测缓存中是否存在目标图片
     *
     * @param url  目标链接--缓存唯一标识
     * @param view 目标容器
     */

    public void display(String url, ImageView view) {
        mViewCache.put(view, url);
        Bitmap bm = mMemCache.get(url);
        if (bm != null) {
            view.setImageBitmap(bm);
        } else {
            view.setBackgroundResource(R.mipmap.ic_empty);
            loadFromDisk(url, view);
        }
    }

    /**
     * 开启线程任务，而mService是一个有限的线程池，可以同时执行5个线程
     *
     * @param url  目标图片链接
     * @param view 目标容器
     */
    private void loadFromDisk(String url, ImageView view) {

        ImageLoadingRunnable ilr = new ImageLoadingRunnable(url, view);
        mService.submit(ilr);
    }

    /**
     * 清除SDCard中的图片缓存
     */
    public void cleanDiskCache(){
        System.out.println("--->>");
        mDiskCache.cleanCache();
    }

    /**
     * 此方法使用为了对url和view配对的验证
     *
     * @param url  目标链接
     * @param view 目标容器
     * @return 是否配对真确
     */

    private boolean validateView(String url, ImageView view) {

        String tag = mViewCache.get(view);
        /*如果目标url为空，则说明这个容器未被绑定任务*/
        /*如果缓存中的tag不等于现在持有的url，则说明这个view和url不匹配*/
        if (tag == null && !tag.equals(url)) {
            return true;
        }
        return false;
    }

    /**
     * 拉伸位图的宽高，使能够自适应View的大小
     * 使用图片显示中心位置
     *
     * @param srcBitmap
     * @param tagWidth
     * @param tagHeight
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap srcBitmap, int tagWidth, int tagHeight) {

        final int width = srcBitmap.getWidth();
        final int height = srcBitmap.getHeight();
        float scaleWidth = ((float) tagWidth) / width;
        float scaleHeight = ((float) tagHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        return Bitmap.createBitmap(srcBitmap, 0, 0, width, height, matrix, true);
    }


    /**
     * 通过容器的宽高，获取合适的位图，这可以有效防止图片失真
     * @param res
     * @param srcId
     * @param view
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static Bitmap adjustBitmap(Resources res,int srcId, ImageView view){

            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, srcId, opt);
            opt.inSampleSize = calculateInSampleSize(opt, view.getMaxWidth(), view.getMaxHeight()+6);
            opt.inJustDecodeBounds = false;
            Bitmap bm = BitmapFactory.decodeResource(res, srcId, opt);
            return bm;
    }


    /**
     * 通过容器的宽高，获取合适的位图，这可以有效防止图片失真
     *
     * @param f    目标文件
     * @param view 目标容器
     * @return 重新绘制的位图
     */
    public  static Bitmap adjustBitmap(File f, ImageView view) {
        try {
            BitmapFactory.Options opt = new BitmapFactory.Options();
            opt.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, opt);
            opt.inSampleSize = calculateInSampleSize(opt, view.getWidth(), view.getHeight());
            opt.inJustDecodeBounds = false;
            opt.inPurgeable = true;// 同时设置才会有效
            opt.inInputShareable = true;//。当系统内存不够时候图片自动被回收
            Bitmap bm = BitmapFactory.decodeStream(new FileInputStream(f), null, opt);
            return zoomBitmap(bm, view.getWidth(), view.getHeight());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 计算降低分辨率的比例
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            while ((halfHeight / inSampleSize) > reqHeight &&
                    (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }


    /**
     * 第三级缓存：从网络下载图片
     * @param f 目标文件对象
     * @param url 目标链接
     * @return 目标位图
     */

    private File loadFromNetwork(File f,String url){

        try {
            URL httpUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) httpUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setReadTimeout(3000);
            conn.setDoInput(true);
            InputStream is = conn.getInputStream();
            FileOutputStream fos = new FileOutputStream(f);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = is.read(buffer))!=-1){
                fos.write(buffer,0,len);
            }

            fos.close();
            is.close();

            return f;

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 第二级缓存查找，如果磁盘不存在目标图片则进行网络下载---这就是三级缓存的过程
     */
    class ImageLoadingRunnable implements Runnable {

        private String url = null;
        private Bitmap bm = null;
        private ImageView view = null;

        public ImageLoadingRunnable(String url, ImageView view) {
            this.url = url;
            this.view = view;
        }

        @Override
        public void run() {

            if (validateView(url, view)) {
                return;
            }
            File f = mDiskCache.get(url);
            /*如果为空，则说明本地没有目标图片，这时需要进行网络加载--第三级缓存*/
            if (bm == null) {
               f =  loadFromNetwork(f,url);
            }
            bm = adjustBitmap(f, view);
            mMemCache.put(url, bm);

            if (validateView(url, view)) {
                return;
            }

            Activity a = (Activity) view.getContext();
            a.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (validateView(url,view)){
                        return;
                    }
                    if(bm == null){
                        System.err.println("cause a ic_stubub.png in image loader thread!");
                        return;
                    }

                    view.setImageBitmap(bm);
                    view.setAdjustViewBounds(true);
                }
            });

        }
    }
}
