package com.admin.ht.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Holy-Spirit on 2016/4/13.
 */
public class SPUtils {

    protected SharedPreferences mPrefences;
    protected SharedPreferences.Editor mEditor;

    public SPUtils(Context context, String fileName) {


        mPrefences = context.getSharedPreferences(fileName, Context.MODE_PRIVATE);
        mEditor = mPrefences.edit();
    }


    public SharedPreferences getPrefences(){
        return  mPrefences;
    }


    public synchronized String getString(String key) {

        return mPrefences.getString(key, "");
    }



    public synchronized boolean getBoolean(String key) {

        return mPrefences.getBoolean(key, true);
    }



    public synchronized boolean put(String key, String value) {

        if (key.equals("") || value.equals("")) {
            return false;
        }
        mEditor.putString(key, value);
        mEditor.commit();
        return true;
    }


    public synchronized boolean put(String key, boolean value) {

        if (key.equals("")) {
            return false;
        }

        mEditor.putBoolean(key, value);
        mEditor.commit();
        return true;
    }


    public synchronized boolean delete(String key){
        if(key.equals(""))
            return false;

        mEditor.remove(key);
        mEditor.commit();
        return true;
    }


}
