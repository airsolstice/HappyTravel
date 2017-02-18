package com.admin.ht.utils.loader;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

/**
 * Created by Holy-Spirit on 2016/5/30.
 */
public class LruMemoryCache {

    private LruCache<String,Bitmap> mCache = null;

    public LruMemoryCache() {

        int blockSize = (int) (Runtime.getRuntime().maxMemory() / 8 / 1024);
        mCache = new LruCache<String, Bitmap>(blockSize) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount() / 1024;
            }
        };
    }


    public void put(String url,Bitmap bm){

        if(bm == null){
            return;
        }

        mCache.put(url,bm);

    }

    public Bitmap get(String url){
        return  mCache.get(url);
    }

}
