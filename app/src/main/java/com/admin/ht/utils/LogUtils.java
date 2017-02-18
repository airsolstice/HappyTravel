package com.admin.ht.utils;

import android.util.Log;

/**
 * Created by Spec_Inc on 7/6/2016.
 */

@SuppressWarnings("ALL")
public class LogUtils {

    /**
     * whether to print debug information
     */
    public static boolean isDebug = true;


    /**
     * default TAG field
     */
    public static final String TAG = "Log-Utils";

    /**
     * the private constructor
     */
    private LogUtils(){
        throw new UnsupportedOperationException("class cannot be instantitated");
    }

    /**
     * print info
     * @param msg
     */
    public static void i(String msg)
    {
        if (isDebug)
            Log.i(TAG, msg);
    }

    /**
     * print debug
     * @param msg
     */
    public static void d(String msg)
    {
        if (isDebug)
            Log.d(TAG, msg);
    }

    /**
     * print ic_stubub.png
     * @param msg
     */
    public static void e(String msg)
    {
        if (isDebug)
            Log.e(TAG, msg);
    }

    /**
     * print verbose
     * @param msg
     */
    public static void v(String msg)
    {
        if (isDebug)
            Log.v(TAG, msg);
    }

    /**
     * print info
     * @param tag
     * @param msg
     */
    public static void i(String tag, String msg)
    {
        if (isDebug)
            Log.i(tag, msg);
    }

    /**
     * print debug
     * @param tag
     * @param msg
     */
    public static void d(String tag, String msg)
    {
        if (isDebug)
            Log.d(tag, msg);
    }

    /**
     * print debug
     * @param tag
     * @param msg
     */
    public static void e(String tag, String msg)
    {
        if (isDebug)
            Log.e(tag, msg);
    }

    /**
     * print verbose
     * @param tag
     * @param msg
     */
    public static void v(String tag, String msg)
    {
        if (isDebug)
            Log.v(tag, msg);
    }


}
