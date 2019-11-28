package com.newland.intelligentserver.util;

import android.util.Log;
/**
 * Created by Administrator on 2019/6/20.
 */
public class LoggerUtil
{
    private final static String TAG = "Socket";
    /**
     * 是否开启debug
     */
    public static boolean isDebug = true;

    /**
     * 打印错误
     * @param msg
     */
    public static void e(String msg)
    {
        if(isDebug)
            Log.e(TAG, msg);
    }

    /**
     * 打印信息
     * @param msg
     */
    public static void i(String msg)
    {
        if(isDebug)
            Log.i(TAG, msg);
    }

    /**
     * 打印警告
     * @param msg
     */
    public static void w(String msg)
    {
        if(isDebug)
            Log.w(TAG, msg);
    }

    /**
     * 打印debug
     * @param msg
     */
    public static void d(String msg)
    {
        if(isDebug)
            Log.d(TAG, msg);
    }

    public static void v(String msg)
    {
        if(isDebug)
            Log.v(TAG, msg);
    }
}
