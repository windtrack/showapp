package com.oo58.livevideoassist.util;

import android.util.Log;

import com.oo58.livevideoassist.common.APPConstants;

/**
 * 打印工具类
 * Created by zhongxf on 2016/5/9.
 */
public class DLog {

    public static String TAG = "LiveVideoAssist";

    //打印工具类
    public static void e(String TAG, String msg) {
        if (APPConstants.isDebug) {
            Log.e(TAG, msg);
        }
    }

    //打印工具类
    public static void e(String msg) {
        if (APPConstants.isDebug) {
            Log.e(TAG, msg);
        }
    }

    //打印工具类
    public static void i(String TAG, String msg) {
        if (APPConstants.isDebug) {
            Log.i(TAG, msg);
        }
    }

    //打印工具类
    public static void i(String msg) {
        if (APPConstants.isDebug) {
            Log.i(TAG, msg);
        }
    }

}
