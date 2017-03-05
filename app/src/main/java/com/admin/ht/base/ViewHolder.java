package com.admin.ht.base;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.admin.ht.utils.BitmapUtils;


/**
 * Created by Holy-Spirit on 2015/12/29.
 * <p/>
 * This is a common ViewHolder
 */
public class ViewHolder {

    private SparseArray<View> mViews;
    private int mPosition;
    private LayoutInflater mInflater;
    private View mConvertView;
    private Context mContext;

    public ViewHolder(Context context, int layoutId, ViewGroup parent, int position) {

        this.mPosition = position;
        this.mContext = context;
        mViews = new SparseArray<View>();
        mInflater = LayoutInflater.from(context);
        mConvertView = mInflater.inflate(layoutId, parent, false);
        mConvertView.setTag(this);
    }



    public static ViewHolder get(View convertView, Context context,
                                 int layoutId, ViewGroup parent, int position) {


        if (convertView == null)
        {
            return new ViewHolder(context, layoutId, parent, position);
        } else {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.mPosition = position;
            System.out.println("position_" + holder.mPosition);
            return holder;

        }
    }



    public <T extends View> T getView(int viewId) {

        View view = mViews.get(viewId);
        if (view == null) {
            view = mConvertView.findViewById(viewId);
            mViews.put(viewId, view);
        }
        return (T) view;
    }


    public View getConvertView() {
        return mConvertView;
    }



    public ViewHolder setText(int viewId, String str) {
        TextView mTxt = getView(viewId);
        mTxt.setText(str);
        return this;
    }


    public ViewHolder setImageResource(int viewId, int resId) {
        ImageView mImg = getView(viewId);
        mImg.setImageResource(resId);

        return this;
    }


    public ViewHolder setImageResource(int viewId, int resId, int reqWidth, int reqJHeight) {
        ImageView mImg = getView(viewId);

        mImg.setImageBitmap(BitmapUtils.decodeResource(mContext.getResources(),
                resId, reqWidth, reqJHeight));

        return this;
    }




    public ViewHolder setImageBitmap(int viewId, Bitmap bitmap) {
        ImageView mImg = getView(viewId);
        mImg.setImageBitmap(bitmap);
        return this;
    }




    public ViewHolder setImageURL(int viewId, final String url) {
        final ImageView mImg = getView(viewId);
        //Bitmap bitmap = ImageLoader.getInstance.loading(url);
        //mImg.setImageBitmap(bitmap);

        //ImageLoaderSample loader = new ImageLoaderSample(mContext);
        //loader.DisplayImage(url,mImg);

       // ImageLoader loader = new ImageLoader(mContext);
       // loader.display(mImg, url);

        return this;
    }

}
