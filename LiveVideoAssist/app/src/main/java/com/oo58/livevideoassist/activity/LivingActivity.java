package com.oo58.livevideoassist.activity;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.oo58.livevideoassist.adapter.ChatAdapter;
import com.oo58.livevideoassist.adapter.ChatGiftAdapter;
import com.oo58.livevideoassist.common.APPConstants;
import com.oo58.livevideoassist.common.AppAction;
import com.oo58.livevideoassist.db.GlobalData;
import com.oo58.livevideoassist.dialog.CloseLivingTipsDialog;
import com.oo58.livevideoassist.dialog.CountDownDialog;
import com.oo58.livevideoassist.dialog.CountDownInterface;
import com.oo58.livevideoassist.dialog.FinishLivingDialog;
import com.oo58.livevideoassist.dialog.RankListDialog;
import com.oo58.livevideoassist.dialog.ShowPromptDialog;
import com.oo58.livevideoassist.entity.AnchorInfo;
import com.oo58.livevideoassist.entity.ChatMessage;
import com.oo58.livevideoassist.entity.RoomBaseInfo;
import com.oo58.livevideoassist.rpc.RpcEvent;
import com.oo58.livevideoassist.socket.BaseClient;
import com.oo58.livevideoassist.util.ActivityMsg;
import com.oo58.livevideoassist.util.DLog;
import com.oo58.livevideoassist.util.LivingTopBtnAnimUtil;
import com.oo58.livevideoassist.util.NetWorkUtil;
import com.oo58.livevideoassist.util.ParseMessage;
import com.oo58.livevideoassist.util.ThreadPoolWrap;
import com.oo58.livevideoassist.util.Util;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.nodemedia.LivePublisher;
import cn.nodemedia.nodemediaclient.R;

/**
 * @author zhongxf
 * @Description 直播界面
 * @Date 2016/5/9.
 */
public class LivingActivity extends Activity implements LivePublisher.LivePublishDelegate {


    private static final int M_Handler_GetRoomInfoSuccess = 1001; //获取房间信息
    private static final int M_Handler_GetRoomInfoFailed = 1002;
    private static final int M_Handler_GetRoomUrlSuccess = 1003; //获取房间地址信息
    private static final int M_Handler_GetRoomUrlFailed = 1004;
    private static final int M_Handler_CallStartLivingSuccess = 1005;//开始直播
    private static final int M_Handler_CallStartLivingFailed = 1006;
    private static final int M_Handler_CallEndLivingSuccess = 1007;//结束直播
    private static final int M_Handler_CallEndLivingFailed = 1008;

    private static final int M_Handler_InitPlayer = 1009;//初始化播放器
    private static final int M_Handler_UpdateAnchorTime = 1010;//更新麦时的任务

    private static final int M_Handler_GetSocketMessage = 7;//聊天服务器返回消息
    private static final int M_Handler_GetSocketMessageException = 8;//聊天服务器错误消息
    private static final int M_Handler_ShowGiftAnimation = 10;//礼物动画显示


    private SurfaceView sv;//直播预览的SurfaceView
    private boolean isStarting = false;//是否已经开始播放
    private boolean isFrontCamera = true;//true 代表正在使用前置摄像头   false代表使用后置摄像头
    private RelativeLayout rootView;//直播界面的根布局
    private boolean isShowBtn = true;//是否顶部按钮显示

    //顶部的按钮变量
    private ImageButton topBtnControl;//顶部所有按钮的控制按钮（控制顶部所有按钮的隐藏和显示）
    private ImageButton lightBtn;//是否开启闪光灯的按钮   0：代表后置摄像头   1：代表前置摄像头 （利用View的tag存储）
    private ImageButton checkCaremaBtn;
    private ImageButton downInfoControlBtn;//下面的聊天消息等内容的隐藏和显示 0:代表显示聊天等消息   1：代表不显示等消息（利用View的Tag存储）
    private ImageButton meiyanBtn;//美颜设置的按钮


    //开始直播 和 关闭直播按钮
    private Button stratLivingBtn;//开始直播按钮
    private ImageButton closeLivingBtn;//关闭直播按钮

    //直播界面上显示的内容布局
    private LinearLayout bottomBtnsCon;//底部按钮的父类容器

    private RelativeLayout rl_chat;//消息的容器
    private RelativeLayout rl_gift;//礼物的容器

    private ImageView sendMsgBtn;//主播聊天的按钮
    private TextView guanzhongNum;//显示观众人数
    private ImageButton phBtn;//排行榜的按钮

    private Context mContext;

    private RoomBaseInfo mRoomInfo; //房间的基础数据
    private NetWorkStateChangeReceive netReceive;//网络连接变化的广播

    /**
     * 聊天消息的界面以及数据
     */
    private List<ChatMessage> messages; //聊天消息数据源
    public static List<ChatMessage> giftmessages; //礼物消息数据源

    private ChatAdapter chatadapter;// 消息适配器
    private ChatGiftAdapter chatGiftAdapter; //礼物适配器

    private ListView pub_chat_listview;// 聊天界面
    private ListView chat_gift_item; //礼物界面

    private BaseClient client; //聊天服务器
    private Timer timer;//礼物显示的定时器

    private Timer timerAnchorTime;//主播麦时

    private LinearLayout topLayer; //悬浮的最上层layer，可滑动

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.living);
        mContext = getApplicationContext();
        sv = (SurfaceView) findViewById(R.id.living_preview);

        initPlayer();//初始化视频
        initView();//初始化界面的View
        initData();//初始化基础数据
        initRoomBaseInfo();//载入房间数据
        netReceive = new NetWorkStateChangeReceive();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (netReceive == null) {
            netReceive = new NetWorkStateChangeReceive();
        }
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netReceive, filter);//注册监听网络变化的广播
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (netReceive != null) {
            unregisterReceiver(netReceive);
        }
    }

    //初始化View
    private void initView() {
        topBtnControl = (ImageButton) findViewById(R.id.close_top_btns);
        topBtnControl.setOnClickListener(topListen);
        lightBtn = (ImageButton) findViewById(R.id.is_on_shgd);
        lightBtn.setOnClickListener(topListen);
        checkCaremaBtn = (ImageButton) findViewById(R.id.check_camera);
        checkCaremaBtn.setOnClickListener(topListen);
        downInfoControlBtn = (ImageButton) findViewById(R.id.is_show_msg);
        downInfoControlBtn.setOnClickListener(topListen);
        meiyanBtn = (ImageButton) findViewById(R.id.meiyan);
        meiyanBtn.setOnClickListener(topListen);

        bottomBtnsCon = (LinearLayout) findViewById(R.id.bottoms_btns_con);
        stratLivingBtn = (Button) findViewById(R.id.start_living_btn);
        stratLivingBtn.setOnClickListener(livingListen);
        closeLivingBtn = (ImageButton) findViewById(R.id.close_living_btn);
        closeLivingBtn.setOnClickListener(livingListen);

        sendMsgBtn = (ImageView) findViewById(R.id.send_msg_btn);
        sendMsgBtn.setOnClickListener(bottonListen);
        phBtn = (ImageButton) findViewById(R.id.show_paihangbang);
        phBtn.setOnClickListener(bottonListen);
        guanzhongNum = (TextView) findViewById(R.id.gzh_num);
        rootView = (RelativeLayout) findViewById(R.id.root_view);

        pub_chat_listview = (ListView) findViewById(R.id.listView_chat);
        chat_gift_item = (ListView) findViewById(R.id.listView_gift);

        rl_chat = (RelativeLayout) findViewById(R.id.rl_chat);
        rl_gift = (RelativeLayout) findViewById(R.id.rl_gift);

        topLayer = (LinearLayout) findViewById(R.id.linearLayout_screen);
        rlWidth = Util.getDisplayMetrics(mContext).widthPixels;
    }

    private void initData() {
        mRoomInfo = new RoomBaseInfo();
        messages = new ArrayList<ChatMessage>();
        giftmessages = new ArrayList<ChatMessage>();

        messages.clear();
        chatadapter = new ChatAdapter(mContext, messages);// 初始化聊天
        // 公聊不为空
        if (pub_chat_listview != null) {
            pub_chat_listview.setAdapter(chatadapter);
        }
        chatGiftAdapter = new ChatGiftAdapter(mContext, giftmessages);
        //礼物
        if (chat_gift_item != null) {
            chat_gift_item.setAdapter(chatGiftAdapter);
        }

    }

    /**
     * 请求房间数据
     */
    private void initRoomBaseInfo() {
        getRoomInfo();
        getRoomUrl();
    }

    //底部按钮的点击监听
    private View.OnClickListener bottonListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.send_msg_btn:
                    Toast.makeText(LivingActivity.this, "功能正在开发中", Toast.LENGTH_LONG).show();
//                    showChatView(!isChatViewShow);
                    break;

                case R.id.show_paihangbang:
                    if (isStarting) {//如果是开播才能查看排行榜
                        RankListDialog dia = new RankListDialog(LivingActivity.this);
                        dia.showPopupWindow(rootView);
                    }
                    break;
                default:
                    break;
            }
        }
    };

    //开始直播 和 关闭直播的点击监听
    private View.OnClickListener livingListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.start_living_btn://开始直播
//                    startLiving(v);
                    final ShowPromptDialog dia = new ShowPromptDialog();//提示用户网络情况对话框
                    String netWorkMsg = "您当前未处于WIFI环境下直播，会消耗自身手机流量哦。";
                    NetWorkUtil.NetType TYPE = NetWorkUtil.isWifiConned(LivingActivity.this);
                    if (TYPE == NetWorkUtil.NetType.NO_NetWork) {//没有网络
                        netWorkMsg = "您还没有联网哦,快去联网吧！";
                        dia.setCancleListen(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dia.closeDialog();
                            }
                        }).setSureListen(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dia.closeDialog();
                                //打开网络设置界面
                                //判断手机系统的版本  即API大于10 就是3.0或以上版本
                                if (android.os.Build.VERSION.SDK_INT > 10) {
//                                    intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
                                    startActivity(new Intent(android.provider.Settings.ACTION_SETTINGS));
                                } else {
                                    startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                                }
                            }
                        }).show(LivingActivity.this, netWorkMsg);//显示对话框
                    } else {
                        if (TYPE == NetWorkUtil.NetType.NO_WIFI) {//非wifi状态下
                            dia.setCancleListen(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dia.closeDialog();
                                }
                            }).setSureListen(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dia.closeDialog();
                                    callLivingStart();
                                }
                            }).show(LivingActivity.this, netWorkMsg);//显示提示对话框
                        } else {//wifi状态下，直接直播
                            callLivingStart();
                        }
                    }
                    break;
                case R.id.close_living_btn://关闭直播
                    closeLiving(v);
                    break;

                default:
                    break;
            }
        }
    };

    //开始播放视频
    private void startLiving(View v) {
        isStarting = true;
        v.setVisibility(View.GONE);//设置开始直播按钮隐藏

        String pubUrl = mRoomInfo.live_url + "/" + mRoomInfo.stream;
        LivePublisher.startPublish(pubUrl);

    }

    //关闭直播方法
    private void closeLiving(View v) {

        if (!isStarting) {
            finish();
        } else {
            final CloseLivingTipsDialog tipDia = new CloseLivingTipsDialog();
            tipDia.setFinishListren(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callLivingFinish();
                    tipDia.closeDialog();//关闭对话框
                    LivePublisher.stopPreview();//关闭预览
                    LivePublisher.stopPublish();//关闭推流
                    final FinishLivingDialog finishInfoDia = new FinishLivingDialog();
                    finishInfoDia.show(LivingActivity.this);
                }
            }).show(LivingActivity.this);
        }

    }

    //顶部按钮的动画
    private LivingTopBtnAnimUtil shgdAnim, checkcameraAnim, msgConAnim, meiyanAnim;

    private final static int OPEN_MENU = 301;//关闭菜单
    private final static int CLOSE_MENU = 302;//关闭菜单
    private Handler topHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case OPEN_MENU:
                    topBtnControl.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.livingmenuopen));
                    break;
                case CLOSE_MENU:
                    topBtnControl.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.livingmenuclose));
                    break;
                default:
                    break;
            }
        }
    };

    //顶部的几个按钮点击监听
    private View.OnClickListener topListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.close_top_btns:
                    if (shgdAnim == null) {
                        shgdAnim = new LivingTopBtnAnimUtil(lightBtn, topBtnControl);
                    }
                    if (checkcameraAnim == null) {
                        checkcameraAnim = new LivingTopBtnAnimUtil(checkCaremaBtn, topBtnControl);
                    }

                    if (msgConAnim == null) {
                        msgConAnim = new LivingTopBtnAnimUtil(downInfoControlBtn, topBtnControl);
                    }
                    if (meiyanAnim == null) {
                        meiyanAnim = new LivingTopBtnAnimUtil(meiyanBtn, topBtnControl);
                    }
                    if (isShowBtn) {
                        shgdAnim.close();
                        checkcameraAnim.close();
                        msgConAnim.close();
                        meiyanAnim.close();
                        isShowBtn = false;
                        topHandler.sendEmptyMessageDelayed(CLOSE_MENU, LivingTopBtnAnimUtil.time);
                    } else {
                        shgdAnim.open();
                        checkcameraAnim.open();
                        msgConAnim.open();
                        meiyanAnim.open();
                        isShowBtn = true;
                        topHandler.sendEmptyMessageDelayed(OPEN_MENU, LivingTopBtnAnimUtil.time);
                    }
                    break;
                case R.id.is_on_shgd:
                    if (!isFrontCamera) {//如果是切换到后置摄像头  才能使用闪光灯
                        Object lightObj = v.getTag();
                        int lightFlag = 0;
                        if (lightObj != null) {
                            lightFlag = (int) lightObj;
                        }
                        if (lightFlag == 0) {//代表闪光灯没有打开   则打开闪光灯
                            v.setTag(1);
                            LivePublisher.setFlashEnable(true);//打开闪光灯
                            lightBtn.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.closesgd));
                        } else {//代表闪光灯已经打开  关闭闪光灯
                            v.setTag(0);
                            LivePublisher.setFlashEnable(false);//关闭闪光灯
                            lightBtn.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.shanguangdeng));
                        }
                    }
                    break;
                case R.id.check_camera://点击切换摄像头按钮
                    LivePublisher.switchCamera();// 切换前后摄像头
                    isFrontCamera = !isFrontCamera;
                    break;
                case R.id.is_show_msg:
                    if (isStarting) {//只有开启直播才能显示和关闭消息框和底部的按钮
                        int showMsgFlag = 0;
                        Object showMsgObj = v.getTag();
                        if (showMsgObj != null) {
                            showMsgFlag = (int) showMsgObj;
                        }
                        if (showMsgFlag == 0) {//关闭下面的聊天信息
                            bottomBtnsCon.setVisibility(View.GONE);
                            rl_chat.setVisibility(View.GONE);
                            rl_gift.setVisibility(View.GONE);
                            v.setTag(1);
                            downInfoControlBtn.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.showinfo));
                        } else {
                            bottomBtnsCon.setVisibility(View.VISIBLE);
                            rl_chat.setVisibility(View.VISIBLE);
                            rl_gift.setVisibility(View.VISIBLE);
                            v.setTag(0);
                            downInfoControlBtn.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.closeinfo));
                        }
                    }
                    break;
                case R.id.meiyan:
                    Toast.makeText(LivingActivity.this, "功能正在开发中", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }

    };


    //开始初始化视频的直播  并且开始预览
    public void initPlayer() {
        LivePublisher.init(this); // 1.初始化
        LivePublisher.setDelegate(this); // 2.设置事件回调
        /**
         * 设置输出音频参数 码率 32kbps 使用HE-AAC ,部分服务端不支持HE-AAC,会导致发布失败
         */
        LivePublisher.setAudioParam(32 * 1000, LivePublisher.AAC_PROFILE_HE);

        /**
         * 设置输出视频参数 宽 640 高 360 fps 15 码率 300kbps 以下建议分辨率及比特率 不用超过1280x720
         * 320X180@15 ~~ 200kbps 480X272@15 ~~ 250kbps 568x320@15 ~~ 300kbps
         * 640X360@15 ~~ 400kbps 720x405@15 ~~ 500kbps 854x480@15 ~~ 600kbps
         * 960x540@15 ~~ 700kbps 1024x576@15 ~~ 800kbps 1280x720@15 ~~ 1000kbps
         * 使用main profile
         */
        int curDef = GlobalData.getInstance(mContext).getSharedPreferences().getInt(APPConstants.VIDEO_DEFINITION, 1);
        LivePublisher.setVideoParam(
                APPConstants.Setting_Defintion[curDef][0],
                APPConstants.Setting_Defintion[curDef][1],
                APPConstants.Setting_Frame[curDef],
                APPConstants.Setting_Code[curDef] * 1000,
                LivePublisher.AVC_PROFILE_BASELINE);
        /**
         * 是否开启背景噪音抑制
         */
        LivePublisher.setDenoiseEnable(true);
        /**
         * 开始视频预览， cameraPreview ： 用以回显摄像头预览的SurfaceViewd对象，如果此参数传入null，则只发布音频
         * interfaceOrientation ： 程序界面的方向，也做调整摄像头旋转度数的参数， camId：
         * 摄像头初始id，LivePublisher.CAMERA_BACK 后置，LivePublisher.CAMERA_FRONT 前置
         */
        LivePublisher.startPreview(sv, getWindowManager().getDefaultDisplay().getRotation(), LivePublisher.CAMERA_FRONT); // 开始预览 如果传null 则只发布音频
        LivePublisher.setVideoOrientation(LivePublisher.VIDEO_ORI_PORTRAIT);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LivePublisher.stopPreview();
        LivePublisher.stopPublish();
        mRoomInfo = null;
        destoryChatSocket();
        if (timerAnchorTime != null) {
            timerAnchorTime.cancel();
        }
    }

    /**
     * 关闭聊天服务器
     */
    private void destoryChatSocket() {
        if (client != null) {
            client.disconnect();
            client = null;
        }
    }

    @Override
    public void onEventCallback(int i, String s) {

    }

    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case M_Handler_GetRoomInfoSuccess:

                    break;
                case M_Handler_GetRoomInfoFailed:
                    break;
                case M_Handler_GetRoomUrlSuccess:
                    break;
                case M_Handler_GetRoomUrlFailed:
                    break;
                case M_Handler_CallStartLivingSuccess:
                    doStartLivingSuccess();
                    break;
                case M_Handler_CallStartLivingFailed:
                    Toast.makeText(mContext, "开始直播失败", Toast.LENGTH_SHORT).show();
                    break;
                case M_Handler_CallEndLivingSuccess:
                    break;
                case M_Handler_CallEndLivingFailed:
                    Toast.makeText(mContext, "结束直播失败", Toast.LENGTH_SHORT).show();
                    break;
                case M_Handler_GetSocketMessage:
                    doChatSocketMessage(msg);
                    break;
                case M_Handler_GetSocketMessageException:
                    SocketStart();
                    break;
                case M_Handler_ShowGiftAnimation:
                    doShowGiftAnimation();
                    break;
                case M_Handler_InitPlayer:
                    break;
                case M_Handler_UpdateAnchorTime:
                    callUpdateAnchorTime();
                    break;

            }
        }
    };

    private void doShowGiftAnimation() {
        Animation push_left_out = AnimationUtils.loadAnimation(mContext, R.anim.push_left_out);
        chat_gift_item.startAnimation(push_left_out);
        push_left_out.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                giftmessages.clear();
                chatGiftAdapter.clearMap();
                chat_gift_item.setVisibility(View.GONE);
            }
        });
    }


    /**
     * 通知服务器直播结束
     */
    private void callLivingFinish() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String uid = GlobalData.getInstance(mContext).getUID();
                    String usc = GlobalData.getInstance(mContext).getUSecert();
                    String roomid = GlobalData.getInstance(mContext).getURoomId();
                    String result = Util.addRpcEvent(RpcEvent.CallFinishLiving, uid, usc, roomid);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        //结束
                        handler.sendEmptyMessage(M_Handler_CallEndLivingSuccess);
                    } else {
                        handler.sendEmptyMessage(M_Handler_CallEndLivingFailed);
                    }

                } catch (Exception e) {
                    handler.sendEmptyMessage(M_Handler_CallEndLivingFailed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    /**
     * 通知服务器开始直播
     */
    private void callLivingStart() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String uid = GlobalData.getInstance(mContext).getUID();
                    String usc = GlobalData.getInstance(mContext).getUSecert();
                    String roomid = GlobalData.getInstance(mContext).getURoomId();
                    int videodpi = GlobalData.getInstance(mContext).getSharedPreferences().getInt(APPConstants.VIDEO_DEFINITION, 1);
                    String result = Util.addRpcEvent(RpcEvent.CallStartLiving, uid, usc, roomid, videodpi,1);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        handler.sendEmptyMessage(M_Handler_CallStartLivingSuccess);
                    } else {
                        handler.sendEmptyMessage(M_Handler_CallStartLivingFailed);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(M_Handler_CallStartLivingFailed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    /**
     * 获取房间信息
     */
    private void getRoomInfo() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String uid = GlobalData.getInstance(mContext).getUID();
                    String usc = GlobalData.getInstance(mContext).getUSecert();
                    String roomid = GlobalData.getInstance(mContext).getURoomId();
                    String result = Util.addRpcEvent(RpcEvent.GetRoomInfo, uid, usc, roomid);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        JSONObject data = obj.getJSONObject("data");
                        mRoomInfo.timestamp = data.getString("timestamp");
                        mRoomInfo.openid = data.getString("openid");
                        mRoomInfo.is_guard = data.getInt("is_guard");
                        mRoomInfo.openkey = data.getString("openkey");
                        mRoomInfo.is_follow = data.getInt("is_followed");

                        JSONObject anchor = data.getJSONObject("anchor");
                        mRoomInfo.anchor_head_icon = anchor.getString("icon");
                        mRoomInfo.anchor_name = anchor.getString("nickname");
                        mRoomInfo.anchor_id = anchor.getString("id");
                        mRoomInfo.anchor_received_level = anchor.getString("received_level");

                        JSONObject room = data.getJSONObject("room");
                        mRoomInfo.anchor_icon = room.getString("poster_url");
                        mRoomInfo.status = room.getString("status");
                        mRoomInfo.maxOnline = room.getInt("maxonline");
                        mRoomInfo.flag = room.getInt("flag");
                        Gson gson = new Gson();
                        mRoomInfo.anchor_current = gson.fromJson(room.toString(), AnchorInfo.class);

                        JSONObject mgmsvever = data.getJSONObject("gserver");
                        mRoomInfo.mMommonUrl = mgmsvever.getString("ip");
                        mRoomInfo.mMommonPort = mgmsvever.getInt("port");

                        handler.sendEmptyMessage(M_Handler_GetRoomInfoSuccess);
                    } else {
                        handler.sendEmptyMessage(M_Handler_GetRoomInfoFailed);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(M_Handler_GetRoomInfoFailed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    /**
     * 获取房间推流地址
     */
    private void getRoomUrl() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String uid = GlobalData.getInstance(mContext).getUID();
                    String usc = GlobalData.getInstance(mContext).getUSecert();
                    String roomid = GlobalData.getInstance(mContext).getURoomId();
                    String result = Util.addRpcEvent(RpcEvent.GetUrlInfo, uid, usc, roomid);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        JSONObject data = obj.getJSONObject("data");
                        String chaturl = data.getString("chat_url");
                        mRoomInfo.chat_url = chaturl.split(":")[0];
                        mRoomInfo.port = Integer.parseInt(chaturl.split(":")[1]);
                        mRoomInfo.live_url = data.getString("live_url");
                        mRoomInfo.stream = data.getString("stream");
                        handler.sendEmptyMessage(M_Handler_GetRoomUrlSuccess);
                    } else {
                        handler.sendEmptyMessage(M_Handler_GetRoomUrlFailed);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(M_Handler_GetRoomUrlFailed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    /**
     * 获取房间信息后  初始化界面相关数据
     * 连接服务器
     */
    private void doStartLivingSuccess() {
        updateOnlineNum(mRoomInfo.maxOnline + ""); //更新在线人数
        SocketStart(); //连接聊天服务器
        addAdminTips();// 给主播的系统公告
        startLiving(stratLivingBtn);  //开始推流
        callUpdateAnchorTime();//开始计算麦时

        //设置底部三个控件显示
        phBtn.setVisibility(View.VISIBLE);
        guanzhongNum.setVisibility(View.VISIBLE);
        sendMsgBtn.setVisibility(View.VISIBLE);
        downInfoControlBtn.setImageBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.closeinfo));
    }

    private CountDownDialog countDownDialog;

    //网络状态变化的监听
    public class NetWorkStateChangeReceive extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            NetworkInfo.State wifiState = null;
            NetworkInfo.State mobileState = null;
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
            mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
            if (wifiState != null && mobileState != null
                    && NetworkInfo.State.CONNECTED != wifiState
                    && NetworkInfo.State.CONNECTED == mobileState) {
                // 打开移动数据网络连接成功
                DLog.e("打开移动数据网络");
                if (countDownDialog != null && isStarting) {//如果正在直播，连上网了，就关闭倒计时的对话框
                    countDownDialog.stopCounDown();
                    countDownDialog = null;
                }
            } else if (wifiState != null && mobileState != null
                    && NetworkInfo.State.CONNECTED != wifiState
                    && NetworkInfo.State.CONNECTED != mobileState) {
                // 手机没有任何的网络
                DLog.e("断网了");
                if (isStarting) {//如果正在直播 出现断网，那么久弹出倒计时的对话框
                    if (countDownDialog == null) {
                        countDownDialog = new CountDownDialog();
                    }
                    countDownDialog.show(LivingActivity.this, new CountDownInterface() {//提醒用户倒计时
                        @Override
                        public void finishTime() {
                            LivePublisher.stopPreview();//关闭预览
                            LivePublisher.stopPublish();//关闭推流
                            final FinishLivingDialog finishInfoDia = new FinishLivingDialog();
                            finishInfoDia.show(LivingActivity.this);
                        }
                    });
                }
            } else if (wifiState != null && NetworkInfo.State.CONNECTED == wifiState) {
                // 无线网络连接成功
                DLog.e("wifi连接成功");
                if (countDownDialog != null && isStarting) {//如果正在直播，连上网了，就关闭倒计时的对话框
                    countDownDialog.stopCounDown();
                    countDownDialog = null;
                }

            }

        }
    }

    // 连接聊天服务器
    public void SocketStart() {
        Runnable socketstartrun = new Runnable() {
            @Override
            public void run() {
                try {
                    // 连接socket服务器
                    destoryChatSocket();

                    client = new BaseClient();
                    client.start(mRoomInfo.chat_url, mRoomInfo.port, handler,
                            M_Handler_GetSocketMessage,
                            M_Handler_GetSocketMessageException);
                    JsonObject data = new JsonObject();
                    data.addProperty("userid", mRoomInfo.openid);
                    data.addProperty("roomid", GlobalData.getInstance(mContext).getURoomId());
                    data.addProperty("timestamp", mRoomInfo.timestamp + "");
                    data.addProperty("openkey", mRoomInfo.openkey);
                    data.addProperty("clienttype", 1);
                    String message = data.toString();
                    byte[] request = Util.getBytes(message, M_Handler_GetSocketMessage);
                    client.sendmsg(request);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(socketstartrun);
    }

    /**
     * 聊天服务器返回的消息处理
     */
    private void doChatSocketMessage(android.os.Message msg) {
        String anchor_name = mRoomInfo.anchor_name;
        ActivityMsg activityMsg = (ActivityMsg) msg.obj;
        DLog.e("sjf", "收到消息：---------" + activityMsg.getTid() + "---------" + activityMsg.getMsg());

        // 账号重复登录
        if (activityMsg.getTid() == 10) {
            callFinishByLoginAgain();
            return;
        }
        ParseMessage m = new ParseMessage(activityMsg, null, mContext, anchor_name);
        ChatMessage charMessage = m.getMessage();
        // 主播开启连麦
        if (activityMsg.getTid() == 53) {
//            return ;
        }

        // 主播关闭连麦
        if (activityMsg.getTid() == 54) {
//            return ;
        }

        // 寄语消息
        if (activityMsg.getTid() == 14) {
//            return ;
        }

        //财神消息
        if (activityMsg.getTid() == 3) {
//            return ;
        }

        //中奖消息
        if (activityMsg.getTid() == 230) {
//            return ;
        }

        // 主播上播
        if (activityMsg.getTid() == 27) {
//            return ;
        }
        // 主播下播
        if (activityMsg.getTid() == 28) {
//            return ;

        }
        // 广播
        if (activityMsg.getTid() == 36) {
//            return ;
        }
        // tid==42和tid==50（是系统消息和抽魔幻卡牌中奖的消息）
        if (activityMsg.getTid() == 42 || activityMsg.getTid() == 50) {
//            mRunwayManager.handleMessage(m.getMessage());// 显示系统消息
            return;
        }

        // 被踢出房间
        if (activityMsg.getTid() == 4) {
//            return ;
        }

        // 在线人数
        if (activityMsg.getTid() == 8) {
            String chat = activityMsg.getMsg();
            JSONObject chat_object = null;
            try {
                chat_object = new JSONObject(chat);
                updateOnlineNum(chat_object.getString("onlinenum"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return;
        }

        // 不为空，并且不为礼物，聊天
        if (charMessage != null) {
            if (charMessage.getTid() == 33) {// 礼物消息
                // 如果是幸运礼物中奖了，就刷新
                // Log.i("lvjian","-----消息-中奖内容-------->"+m.getMessage().getTname());
                if (!Util.isEmpty(charMessage.getTname())) {
                    messages.add(charMessage);
                    chatadapter.notifyDataSetChanged();
                }

                if (giftmessages.size() == 0) {
                    giftmessages.add(charMessage);
                    if (chat_gift_item == null) {
                        return;
                    }
                    if (chat_gift_item.getChildAt(0) != null) {
                        chatGiftAdapter
                                .AppearAnimPosition(chat_gift_item.getChildAt(0));
                    }
                } else if (giftmessages.size() == 1) {
                    // 如果是同一人就移除旧的直接加载新的
                    if (giftmessages.get(0).getSname()
                            .equals(charMessage.getSname())) {
                        giftmessages.remove(0);
                        giftmessages.add(charMessage);
                    } else {
                        giftmessages.add(charMessage);
                        if (chat_gift_item.getChildAt(1) != null) {
                            chatGiftAdapter.AppearAnimPosition(chat_gift_item
                                    .getChildAt(1));
                        }
                    }
                } else if (giftmessages.size() == 2) {
                    // 如果有两个用户刷，新来的用户是其中之一，直接覆盖，如果不是其中之一移除第一个，添加到最后
                    if (giftmessages.get(0).getSname()
                            .equals(charMessage.getSname())) {
                        // 名字相同，礼物也相同就累计（替换这个位置的）
                        if (giftmessages.get(0).getGift_id()
                                .equals(charMessage.getGift_id())) {
                            giftmessages.set(0, m.getMessage());
                        } else {
                            giftmessages.remove(0);
                            giftmessages.add(charMessage);
                            chatGiftAdapter.AppearAnimPosition(chat_gift_item.getChildAt(1));
                        }

                    } else if (giftmessages.get(1).getSname()
                            .equals(charMessage.getSname())) {
                        if (giftmessages.get(1).getGift_id()
                                .equals(charMessage.getGift_id())) {
                            giftmessages.set(1, charMessage);
                        }
                    } else {
                        giftmessages.remove(0);
                        giftmessages.add(charMessage);
                        chatGiftAdapter.AppearAnimPosition(chat_gift_item.getChildAt(1));
                    }
                }
                chat_gift_item.setVisibility(View.VISIBLE);
                // 设置礼物显示定时器
                if (timer != null) {
                    timer.cancel();
                }
                timer();

                chatGiftAdapter.notifyDataSetChanged();
                            /*
                             * if (chat_gift_item != null) { chat_gift_item .setTranscriptMode (ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
							 * chat_gift_item.setCacheColorHint(0); }
							 */
            } else {
                //聊天消息
                messages.add(charMessage);
                chatadapter.doAutoDelete();
                chatadapter.notifyDataSetChanged();

                pub_chat_listview.smoothScrollToPosition(chatadapter.getCount() - 1);
            }
        }


    }

    /**
     * 更新当前人数
     *
     * @param num
     */
    private void updateOnlineNum(String num) {
        guanzhongNum.setText(num);
    }

    // 第一种方法：设定指定任务task在指定时间time执行 schedule(TimerTask task, Date time)
    public void timer() {
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                System.out.println("----------------设定要指定任务------------------");
                handler.sendEmptyMessage(M_Handler_ShowGiftAnimation);
            }
        }, 3000);// 设定指定的时间time
    }

    /**
     * 缩放动画显示聊天
     *
     * @param flag
     */
    private void showChatView(final boolean flag) {
        if (rl_chat != null) {
            rl_chat.setVisibility(View.VISIBLE);
            final ScaleAnimation animation;
            final AlphaAnimation alphaAnimation;
            if (flag) {
                animation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);

                alphaAnimation = new AlphaAnimation(0, 1);
            } else {
                animation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
                        Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
                alphaAnimation = new AlphaAnimation(1, 0);
            }

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    if (!flag) {
                        rl_chat.setVisibility(View.INVISIBLE);
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            animation.setRepeatCount(0);
            alphaAnimation.setRepeatCount(0);
            animation.setDuration(300);//设置动画持续时间
            alphaAnimation.setDuration(300);//设置动画持续时间
            AnimationSet allAni = new AnimationSet(true);
            allAni.addAnimation(animation);
            allAni.addAnimation(alphaAnimation);
            rl_chat.startAnimation(allAni);
        }
    }

    private static final int MAX_OFFSET = 5;// 5个像素误差，滑动小于5个像素就没有动画
    private float downX;// 按下时的点
    private float viewXdown;// 按下时View的位置
    private boolean lastSlidePull = false;// 最后一次滑动的方向
    private float maxOffset = 0;// 最大的滑动距离
    private int rlWidth;// 布局的宽度

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                this.downX = x;
                this.viewXdown = topLayer.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float offset = (event.getX() - downX);// 滑动距离
                float posX = viewXdown + offset;// 计算可能的位置
                maxOffset = maxOffset > Math.abs(offset) ? maxOffset : Math.abs(offset);
                maxOffset = Math.abs(offset);

                if (offset < 0) {// pull to show

                    if (posX >= 0) {// 防止不跟手，更新downX的值
                        this.downX += posX;
                    }
                    lastSlidePull = true;
                } else {// push to hide
                    topLayer.setX(posX > -rlWidth ? posX : -rlWidth);
                    if (posX <= -rlWidth) {// 防止不跟手，更新downX的值
                        this.downX += (posX + rlWidth);
                    }
                    lastSlidePull = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (maxOffset < MAX_OFFSET) {// 防止抖动
                    return super.onTouchEvent(event);
                }
                // 使用动画滑动到指定位置
                if (lastSlidePull) {
                    slideToShow();
                } else {
                    slideToHide();
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }


    /**
     * 使用ValueAnimator将rl_left以动画的形式弹入到界面
     */
    private void slideToShow() {
        float startX = topLayer.getX();

        ValueAnimator valueAnimator = ValueAnimator.ofInt(rlWidth, 0);

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int offset = (Integer) animation.getAnimatedValue();
                topLayer.setX(offset);
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        float fraction = Math.abs(startX / rlWidth);
        valueAnimator.setDuration((long) (600 * fraction));
        valueAnimator.start();
    }

    /**
     * 使用ValueAnimator将rl_left以动画的形式弹出去
     */
    private void slideToHide() {
        float startX = topLayer.getX();
        ValueAnimator valueAnimator = ValueAnimator.ofInt((int) startX,

                rlWidth);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int offset = (Integer) animation.getAnimatedValue();
                topLayer.setX(offset);
            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        float fraction = Math.abs((rlWidth + startX) / rlWidth);
        valueAnimator.setDuration((long) (400 * fraction));
        valueAnimator.start();
    }

    /**
     * 定时器控制主播播放时间
     */
    private void callUpdateAnchorTime() {
        updateAnchorTime();
        timerAnchorTime = new Timer();
        timerAnchorTime.schedule(new TimerTask() {
            public void run() {
                System.out.println("----------------设定要指定任务------------------");
                handler.sendEmptyMessage(M_Handler_UpdateAnchorTime);
            }
        }, 46000);// 设定指定的时间time
    }

    /**
     * 更新主播麦时
     */
    private void updateAnchorTime() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String uid = GlobalData.getInstance(mContext).getUID();
                    String usc = GlobalData.getInstance(mContext).getUSecert();
                    String roomid = GlobalData.getInstance(mContext).getURoomId();
                    String result = Util.addRpcEvent(RpcEvent.CallUpdateAnchorTime, uid, usc, roomid);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {

                    }

                } catch (Exception e) {

                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };

        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    /**
     * 账号重读登录
     */
    private void callFinishByLoginAgain() {
        Intent intent = new Intent();
        intent.setAction(AppAction.RECVIVE_LOGINAGIAN);
        intent.putExtra("loginagain", true);
        sendBroadcast(intent);
        this.finish();
    }

    /**
     * 主播开播时的 系统通知
     */
    private void addAdminTips() {
        String tips = "系统公告：我国有关法律法规禁止直播涉黄、涉政、涉暴等内容，中视24小时实时监管，发现违禁内容会立即封号并上传依法机关哦";
        ChatMessage charMessage = new ChatMessage();
        // 恭喜升级通知
        charMessage.setSay(tips);
        charMessage.setTid(42);
        messages.add(charMessage);
    }

    @Override
    public void onBackPressed() {

        closeLiving(null);

    }
}
