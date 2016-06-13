package com.oo58.livevideoassist.application;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.telephony.TelephonyManager;

import com.oo58.livevideoassist.common.APPConstants;

/**
 * 中视手机直播伴侣全局Application
 * Created by zhongxf on 2016/5/9.
 */
public class ChinaVideoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        if(!APPConstants.isDebug){//如果是正式的上线包就启动全局异常捕获处理
            LAExHandler exceptionHandler = LAExHandler.getInstance();
            exceptionHandler.init(getApplicationContext());
        }
        initVersion() ;
    }

    /**
     * 获取软件版本信息
     */
    public void initVersion() {
        try {
            PackageInfo packageInfo = getApplicationContext()
                    .getPackageManager().getPackageInfo(getPackageName(), 0);
            APPConstants.localVersionCode = packageInfo.versionCode;
            APPConstants.localVersionName = packageInfo.versionName;

            /**
             * 获取手机识别码
             */
            TelephonyManager telephonemanager = (TelephonyManager)getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
            APPConstants.localToken =telephonemanager.toString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

}
