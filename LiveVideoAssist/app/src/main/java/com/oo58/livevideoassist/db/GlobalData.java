package com.oo58.livevideoassist.db;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.oo58.livevideoassist.common.APPConstants;


/**
 * Desc:  常用的数据
 * Created by sunjinfang on 2016/5/10.
 */
public class GlobalData {

    private static GlobalData instance;
    private SharedPreferences preferences; //本地数据
    private Editor edit; //修改本地数据
    private ImageLoader mImageLoader; //图片加载器

    public boolean isBeShoutUp ;

    /**
     * 初始化
     * @param context
     * @return
     */
    public static GlobalData getInstance(Context context) {
        if (instance == null) {
            instance = new GlobalData(context);
        }
        return instance;
    }

    /**
     * 获取图片加载器
     * @return
     */
    public ImageLoader getmImageLoader() {
        return mImageLoader;
    }

    /**
     * 初始化
     * @param context
     */
    private GlobalData(Context context) {
        preferences = context.getSharedPreferences(GlobalData.class.getSimpleName(), Context.MODE_PRIVATE);
        edit = preferences.edit();
        mImageLoader = ImageLoader.getInstance();
        mImageLoader.init(ImageLoaderConfiguration.createDefault(context));
    }

    /**
     * 获取 SharedPreferences
     * @return
     */
    public SharedPreferences getSharedPreferences() {
        return preferences;
    }

    /**
     * 获取 Editor
     * @return
     */
    public Editor getEditor() {
        return edit;
    }

    /**
     * 提交修改
     */
    public void commit() {
        edit.commit();
    }

    /**
     * 获取用户id
     * @return
     */
    public String getUID(){
        return preferences.getString(APPConstants.OPEN_ID,"") ;
    }
    /**
     * 获取用户登录秘钥
     * @return
     */
    public String getUSecert(){
        return preferences.getString(APPConstants.USER_SECRET,"") ;
    }
    /**
     * 获取主播房间id
     * @return
     */
    public String getURoomId(){
        return preferences.getString(APPConstants.USER_ROOMID,"") ;
    }
    /**
     * 获取主播名称
     * @return
     */
    public String getUName(){
        return preferences.getString(APPConstants.NICKNAME,"") ;
    }
    /**
     * 获取主播icon地址
     * @return
     */
    public String getUIcon(){
        return preferences.getString(APPConstants.USER_ICON,"") ;
    }

    /**
     * 判断主播是否在线
     * @return
     */
    public boolean checkLogin(){
         return preferences.getBoolean(APPConstants.USER_LOGIN,false) ;
    } ;
}
