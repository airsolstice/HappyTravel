package com.admin.ht.utils.loader;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by Holy-Spirit on 2016/5/30.
 */
@SuppressWarnings("ALL")
public class LruDiskCache {

    private File mCacheDir = null;

    public LruDiskCache(Context context) {

        String packageNmae = context.getPackageName();
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            mCacheDir = new File(Environment.getExternalStorageDirectory(),packageNmae);
        } else {
            mCacheDir = new File(context.getCacheDir(),packageNmae);
        }

        if (!mCacheDir.exists()) {
            mCacheDir.mkdirs();
        }

    }

    public File get(String url) {
        return new File(mCacheDir, String.valueOf(url.hashCode() + ".png"));
    }

    public void cleanCache() {

        File[] files = mCacheDir.listFiles();

        if (files == null || files.length <= 0) {
            System.out.println("-->>file direction is empty");
            return;
        }

        for (File f : files) {
            System.out.println("-->>name="+f.getName());
            f.delete();
        }

    }

}
