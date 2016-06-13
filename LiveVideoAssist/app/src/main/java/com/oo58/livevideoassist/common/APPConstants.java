package com.oo58.livevideoassist.common;

/**
 * 应用全局配置类
 * Created by zhongxf on 2016/5/9.
 */
public class APPConstants {
    //获取项目中是否是Debug的配置
    public final static boolean isDebug = true;

    /**
     * 软件基本数据
     */
    public  static int localVersionCode = 0; //本地版本
    public  static String localVersionName = "1.0.0" ; //本地版本号
    public  static String localToken = "1.0.0" ; //手机唯一识别码

    public static int serverVersionCode = 0 ; //服务器版本
    public static String serverVersionName; //服务器版本号
    public static String apkDownLoadUrl  ; //apk下载地址
    public static String updateTips;//版本更新提示文字

    /**
     * 用户的相关信息
     */
    public static final String OPEN_ID = "openid";
    public static final String OPENKEY = "openkey";
    public static final String TIMESTAMP = "timestamp" ;//时间戳

    public static final String NICKNAME = "nickname"; //玩家昵称
    public static final String USER_SECRET = "secret" ;//secret
    public static final String USER_ICON = "icon" ;//icon
    public static final String USER_TYPE = "user_type" ;//用户类型  游客  主播 普通用户
    public static final String GENDER = "man"; //性别  0男  1女
    public static final String USER_ROOMID = "roomid"; //房间号
    public static final String USER_FANS = "fans"; //粉丝数量
    public static final String ROOM_NOTICE = "notice"; //房间公告

    public static final String USER_LOGIN = "login";//是否登录

    public static final String USER_TOKEN = "access_token";//token
    public static final String USER_BEANS = "beans"; //乐币
    public static final String USER_VIPLV = "vip_lv";//vip等级

    /**
     * 登录界面信息
     */
    public static final String USER = "login_user";//用户名
    public static final String PASS = "login_pass";//密码
    public static final String Save = "login_savepwd" ;//是否保存密码
    /**
     *  视频设置
     */
    public static final String VIDEO_CODE = "video_code" ;//码率
    public static final String VIDEO_DEFINITION = "video_definition" ;//清晰度
    public static final String VIDEO_FRAME = "video_frame" ;//帧数


    /**
     * 设置输出视频参数 宽 640 高 360 fps 15 码率 300kbps 以下建议分辨率及比特率 不用超过1280x720
     * 320X180@15 ~~ 200kbps 480X272@15 ~~ 250kbps 568x320@15 ~~ 300kbps
     * 640X360@15 ~~ 400kbps 720x405@15 ~~ 500kbps 854x480@15 ~~ 600kbps
     * 960x540@15 ~~ 700kbps 1024x576@15 ~~ 800kbps 1280x720@15 ~~ 1000kbps
     * 使用main profile
     */
    //分辨率
    public static int[][] Setting_Defintion = {
            {320,180},
            {960,540},
            {1280,720},
    } ;
    //码率
    public static int[] Setting_Code = {300,600,800} ;
    //帧数
    public static int[] Setting_Frame = {15,20,20} ;


}
