package com.oo58.livevideoassist.common;

/**
 * 登录的URL
 * Created by zhongxf on 2016/5/9.
 */
public class AppUrl {

    //服务器地址
    public final static String SERVER_IP = "http://www.0058.com/";

    //rpc入口
//    public static  String PATH_RPC = "http://livev.0058.com/api/rpc/mpc.php";
//    public static  String PATH_RPC = "http://mobile.0058.com/api/rpc/pc.php";
    public static  String PATH_RPC = "http://60.174.249.98:8002/api/rpc/mpc.php";
    //登录接口
    public final static String LOGIN = SERVER_IP+"login";

    //java服务器借口
    public final static String ChAT_PATH = "192.168.1.201";

    //用户头像
    public static final String USER_LOGO_ROOT = "http://static.0058.com/xtuserlogo/200x200/";
    public static final String USER_SMALL_HEAD_PATH = "http://static.0058.com/xtuserlogo/45x45/";
    public static final String GIFT_PATH = "http://static.0058.com/static/img/gift/mobile/";
    public static  final String CAR_PATH = "http://static.0058.com/static/img/mount/mobile/sit_";
}
