package com.oo58.livevideoassist.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author zhongxf
 * @Description 应用网络的工具类
 * @Date 2016/5/16.
 */
public class NetWorkUtil {

    public enum NetType {
        NO_NetWork,WIFI,NO_WIFI;
    }

    /**
     *判断手机现在是否是wifi联网   返回true是wifi联网，返回false 不是wifi联网
     */
    public static NetType isWifiConned(Context cxt){
        ConnectivityManager connectMgr = (ConnectivityManager) cxt.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectMgr.getActiveNetworkInfo();
        if(info!=null ){//如果info不是空（有网络连接）
            if(info.getType() == ConnectivityManager.TYPE_WIFI){//wifi状态
                return NetType.WIFI;
            }else{
                return NetType.NO_WIFI;//非wifi状态
            }
        }else{
            return NetType.NO_NetWork;
        }
    }
}
