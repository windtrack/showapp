package com.oo58.livevideoassist.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.DisplayMetrics;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.oo58.livevideoassist.common.AppUrl;
import com.oo58.livevideoassist.rpc.JsonRpcOkHttpClient;
import com.oo58.livevideoassist.rpc.RpcEvent;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Desc: 一个工具类
 * Created by sunjinfang on 2016/5/10 .
 */
public class Util {


    /**
     * @param event  事件
     * @param params 不定量的参数
     * @return 从服务器的回调结果
     */
    public static String addRpcEvent(RpcEvent event, Object... params) {

        String s = "";
        try {
            String uri = AppUrl.PATH_RPC;
            JsonRpcOkHttpClient client = new JsonRpcOkHttpClient(uri);
            client.setConnectTimeout(30, TimeUnit.SECONDS);
            String data = client.doRequest(event.name, params);
            JSONObject jsonObject = new JSONObject(data);
            s = jsonObject.getString("result");
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return s;
    }


    /**
     * 拼接数组
     *
     * @param
     * @return
     */
    public static byte[] getBytes(String body, int tid) {
        // 包长
        byte[] package_length = intToByteArray(body.getBytes().length + 12);
        byte[] message_package = new byte[body.getBytes().length + 12];
        // 方法
        byte[] id = intToByteArray(tid);
        // 包体长度
        byte[] body_length = intToByteArray(body.getBytes().length);
        // 包体
        byte[] body_bytes = body.getBytes();

        System.arraycopy(package_length, 0, message_package, 0, 4);
        System.arraycopy(id, 0, message_package, 4, 4);
        System.arraycopy(body_length, 0, message_package, 8, 4);
        System.arraycopy(body_bytes, 0, message_package, 12,
                body.getBytes().length);
        return message_package;
    }

    public static byte[] intToByteArray(int i) {
        byte[] result = new byte[4];
        // 由高位到低位
        result[0] = (byte) ((i >> 24) & 0xFF);
        result[1] = (byte) ((i >> 16) & 0xFF);
        result[2] = (byte) ((i >> 8) & 0xFF);
        result[3] = (byte) (i & 0xFF);
        return result;
    }


    /**
     * 获取屏幕尺寸与密度.
     *
     * @param context the context
     * @return mDisplayMetrics
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        Resources mResources;
        if (context == null) {
            mResources = Resources.getSystem();

        } else {
            mResources = context.getResources();
        }
        //DisplayMetrics{density=1.5, width=480, height=854, scaledDensity=1.5, xdpi=160.421, ydpi=159.497}
        //DisplayMetrics{density=2.0, width=720, height=1280, scaledDensity=2.0, xdpi=160.42105, ydpi=160.15764}
        DisplayMetrics mDisplayMetrics = mResources.getDisplayMetrics();
        return mDisplayMetrics;
    }


    /**
     * 打开键盘.
     *
     * @param context the context
     */
    public static void showSoftInput(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 关闭键盘事件.
     *
     * @param context the context
     */
    public static void closeSoftInput(Context context) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && ((Activity) context).getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(((Activity) context).getCurrentFocus()
                    .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * udicode字符串转汉字
     *
     * @param str
     * @return
     */
    public static String uncodeParser(String str) {
        String v = "'" + str + "'";
        try {
            String result = new JSONTokener(v).nextValue().toString();
            return result;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 判断字符串是否为空
     */
    public static boolean isEmpty(String str) {
        // str=str.trim();
        if (str == null || "".equals(str)) {
            return true;
        } else {
            return false;
        }
    }

    /***
     * 判断 String 是否是 int
     *
     * @param input
     * @return
     */
    public static boolean isInteger(String input) {
        Matcher mer = Pattern.compile("^[+-]?[0-9]+$").matcher(input);
        return mer.find();
    }

    /**
     * 设置财富等级
     *
     * @param lev
     * @param view
     * @param context
     */
    public static void setCostLv(int lev, ImageView view, Context context) {
        if (lev >= ImageUrl.Img_Cost.length) {
            return;
        }
        Bitmap received_img = BitmapFactory.decodeResource(context.getResources(), ImageUrl.Img_Cost[lev]);
        view.setImageBitmap(received_img);
    }

    /**
     * 设置vip等级
     *
     * @param lev
     * @param view
     * @param context
     */
    public static void setVipLv(int lev, ImageView view, Context context) {
        if (lev >= ImageUrl.Img_Vip.length) {
            return;
        }
        Bitmap received_img = BitmapFactory.decodeResource(context.getResources(), ImageUrl.Img_Vip[lev]);
        view.setImageBitmap(received_img);
    }

    /**
     * 返回当前程序版本名
     */
    public static String getAppVersionName(Context context) {
        String versionName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            versionName = pi.versionName;
            if (versionName == null || versionName.length() <= 0) {
                return "";
            }
        } catch (Exception e) {
            DLog.e("Get Version Exception");
        }
        return versionName;
    }
}
