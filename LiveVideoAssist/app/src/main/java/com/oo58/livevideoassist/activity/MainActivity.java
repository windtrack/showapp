package com.oo58.livevideoassist.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.oo58.livevideoassist.common.APPConstants;
import com.oo58.livevideoassist.common.AppAction;
import com.oo58.livevideoassist.common.AppUrl;
import com.oo58.livevideoassist.db.GlobalData;
import com.oo58.livevideoassist.dialog.LoadingDailog;
import com.oo58.livevideoassist.dialog.LogoutDialog;
import com.oo58.livevideoassist.dialog.ShowPromptDialog;
import com.oo58.livevideoassist.rpc.RpcEvent;
import com.oo58.livevideoassist.util.ThreadPoolWrap;
import com.oo58.livevideoassist.util.Util;

import org.json.JSONObject;

import cn.nodemedia.nodemediaclient.R;

/**
 * @author zhongxf
 * @Description 主界面
 * @Date 2016/5/9.
 */
public class MainActivity extends BaseActivity {

    private static final int M_Handler_UpdateNoticeSuccess = 1;//设置公告成功
    private static final int M_Handler_UpdateNoticeFailed = 2;//设置公告失败
    private static final int M_Handler_CallLoginAgian = 3;//重复登录


    private Button startLivingBtn;//开始直播按钮
    private LinearLayout livingHelperBtn;//直播助手按钮
    private LinearLayout livingSettingBtn;//直播设置按钮
    private LinearLayout settingCon;//设置的容器
    private ImageView settingFlag;//设置的图片标志（向右 或者 向下 箭头）
    private Bitmap downJtou;//向下箭头
    private Bitmap rightJtou;//向右箭头
    private boolean isOpenSetting = false;//打开视频设置按钮容器标志
    private boolean isOpenHelper = false;//打开直播助手功能按钮标志
    private Button logoutBtn;//注销登录按钮

    private TextView mTextUserName;//玩家昵称
    private TextView mTextRoomID;//房间id
    private TextView mTextFans;//关注数
    private EditText mTextNotice;//房间公告

    public DisplayImageOptions mOptions;
    private ImageView editBtn;//房间公告编辑按钮

    private ImageView helperFlag;//直播助手的箭头
    private LinearLayout helperCon;//直播助手按钮容器
    private LinearLayout giftRankBtn;//礼物排行榜的按钮
    private LinearLayout queryBeansBtn;//查询乐豆的按钮


    private Button lchBtn;//设置流畅
    private Button pqBtn;//普清设置
    private Button hdBtn;//高清设置

    private LoadingDailog ld;//loading界面

    private LinearLayout topCon;//顶部布局的容器

    private Receiver mReceiver;//广播消息接收


    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.main);

        startLivingBtn = (Button) findViewById(R.id.start_living_btn);
        startLivingBtn.setOnClickListener(this);

        livingHelperBtn = (LinearLayout) findViewById(R.id.living_helper_btn);
        livingHelperBtn.setOnClickListener(this);

        livingSettingBtn = (LinearLayout) findViewById(R.id.setting_btn);
        livingSettingBtn.setOnClickListener(this);

        downJtou = BitmapFactory.decodeResource(getResources(), R.mipmap.xiajiantou);
        rightJtou = BitmapFactory.decodeResource(getResources(), R.mipmap.youjiantou);

        settingCon = (LinearLayout) findViewById(R.id.setting_con);
        settingCon.setVisibility(View.GONE);//默认设置按钮容器隐藏
        settingFlag = (ImageView) findViewById(R.id.setting_flag);
        settingFlag.setImageBitmap(rightJtou);//默认显示向右箭头


        helperFlag = (ImageView) findViewById(R.id.helper_seeting_flag);
        helperCon = (LinearLayout) findViewById(R.id.helper_con);
        helperCon.setVisibility(View.GONE);
        giftRankBtn = (LinearLayout) findViewById(R.id.rank_list_btn);
        giftRankBtn.setOnClickListener(this);
        queryBeansBtn = (LinearLayout) findViewById(R.id.qurey_beans);
        queryBeansBtn.setOnClickListener(this);


        logoutBtn = (Button) findViewById(R.id.logout_btn);
        logoutBtn.setOnClickListener(this);

        mTextUserName = (TextView) findViewById(R.id.username);
        mTextRoomID = (TextView) findViewById(R.id.show_room_id);
        mTextFans = (TextView) findViewById(R.id.show_guanzhu_num);
        mTextNotice = (EditText) findViewById(R.id.showroomnotice);

        editBtn = (ImageView) findViewById(R.id.room_notice_edit_btn);


        View.OnTouchListener onTouchListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mTextNotice.setFocusable(true);
                    mTextNotice.setFocusableInTouchMode(true);
                    mTextNotice.requestFocus();
                    mTextNotice.setCursorVisible(true);
                    mTextNotice.setSelection(mTextNotice.getText().length());
                }
                return false;
            }
        };

        mTextNotice.setOnTouchListener(onTouchListener);
        editBtn.setOnTouchListener(onTouchListener);

        View.OnKeyListener onKeyListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                /**
                 * 回车并且按键弹起时 隐藏键盘 更新公告
                 */
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                /*隐藏软键盘*/
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager.isActive()) {
                        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                    }

                    String text = mTextNotice.getText().toString().trim();
                    String curNotice = GlobalData.getInstance(mContext).getSharedPreferences().getString(APPConstants.ROOM_NOTICE, "");
                    /**
                     * 内容无更改不更新数据
                     */
                    if (!curNotice.equals(text)) {
                        if (!"".equals(text)) {
                            updateRoomNotice(text);
                        } else {
                            showToast("公告内容为空");
                            mTextNotice.setText(curNotice);
                            mTextNotice.setSelection(mTextNotice.getText().length());
                        }
                    }
                    mTextNotice.setCursorVisible(false);
                    return true;
                }
                return false;
            }
        };
        mTextNotice.setOnKeyListener(onKeyListener);

        lchBtn = (Button) findViewById(R.id.setting_lch_btn);
        lchBtn.setOnClickListener(videoClickListen);
        pqBtn = (Button) findViewById(R.id.setting_pq_btn);
        pqBtn.setOnClickListener(videoClickListen);
        hdBtn = (Button) findViewById(R.id.setting_hd_btn);
        hdBtn.setOnClickListener(videoClickListen);
        VideoInit();

        topCon = (LinearLayout) findViewById(R.id.top_con);
        topCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });
    }

    //初始化视频设置显示
    private void VideoInit() {
        //当前的清晰度
        int curSelectDif = GlobalData.getInstance(MainActivity.this).getSharedPreferences().getInt(APPConstants.VIDEO_DEFINITION, 1);
        videoBtnsInit();
        switch (curSelectDif) {
            case 0:
                Drawable lchClick = getResources().getDrawable(R.mipmap.lchclicked);
                lchClick.setBounds(0, 0, lchClick.getMinimumWidth(), lchClick.getMinimumHeight());
                lchBtn.setCompoundDrawables(null, lchClick, null, null);
                lchBtn.setBackgroundColor(getResources().getColor(R.color.grayfgx));
                break;
            case 1:
                Drawable pqClick = getResources().getDrawable(R.mipmap.mlclicked);
                pqClick.setBounds(0, 0, pqClick.getMinimumWidth(), pqClick.getMinimumHeight());
                pqBtn.setCompoundDrawables(null, pqClick, null, null);
                pqBtn.setBackgroundColor(getResources().getColor(R.color.grayfgx));
                break;
            case 2:
                Drawable hdClick = getResources().getDrawable(R.mipmap.hdclicked);
                hdClick.setBounds(0, 0, hdClick.getMinimumWidth(), hdClick.getMinimumHeight());
                hdBtn.setCompoundDrawables(null, hdClick, null, null);
                hdBtn.setBackgroundColor(getResources().getColor(R.color.grayfgx));
                break;
        }

    }

    //初始化视频设置按钮
    private void videoBtnsInit() {
        Drawable lchNormal = getResources().getDrawable(R.mipmap.lchnormal);
        Drawable pqNormal = getResources().getDrawable(R.mipmap.mlnormal);
        Drawable hdNormal = getResources().getDrawable(R.mipmap.hdnormal);

        lchNormal.setBounds(0, 0, lchNormal.getMinimumWidth(), lchNormal.getMinimumHeight());
        lchBtn.setCompoundDrawables(null, lchNormal, null, null);
        lchBtn.setBackgroundColor(getResources().getColor(R.color.sbc1));

        pqNormal.setBounds(0, 0, pqNormal.getMinimumWidth(), pqNormal.getMinimumHeight());
        pqBtn.setCompoundDrawables(null, pqNormal, null, null);
        pqBtn.setBackgroundColor(getResources().getColor(R.color.sbc1));

        hdNormal.setBounds(0, 0, hdNormal.getMinimumWidth(), hdNormal.getMinimumHeight());
        hdBtn.setCompoundDrawables(null, hdNormal, null, null);
        hdBtn.setBackgroundColor(getResources().getColor(R.color.sbc1));
    }

    //视频参数设置的点击监听
    private View.OnClickListener videoClickListen = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            switch (v.getId()) {
                case R.id.setting_lch_btn://点击了流畅按钮
                    videoBtnsInit();//三个按钮的初始化
                    Drawable lchClick = getResources().getDrawable(R.mipmap.lchclicked);
                    lchClick.setBounds(0, 0, lchClick.getMinimumWidth(), lchClick.getMinimumHeight());
                    lchBtn.setCompoundDrawables(null, lchClick, null, null);
                    lchBtn.setBackgroundColor(getResources().getColor(R.color.grayfgx));
                    GlobalData.getInstance(MainActivity.this).getEditor().putInt(APPConstants.VIDEO_DEFINITION, 0).commit();
                    break;
                case R.id.setting_pq_btn://点击了普清按钮
                    videoBtnsInit();//三个按钮的初始化
                    Drawable pqClick = getResources().getDrawable(R.mipmap.mlclicked);
                    pqClick.setBounds(0, 0, pqClick.getMinimumWidth(), pqClick.getMinimumHeight());
                    pqBtn.setCompoundDrawables(null, pqClick, null, null);
                    pqBtn.setBackgroundColor(getResources().getColor(R.color.grayfgx));
                    GlobalData.getInstance(MainActivity.this).getEditor().putInt(APPConstants.VIDEO_DEFINITION, 1).commit();
                    break;
                case R.id.setting_hd_btn://设置了高清按钮
                    videoBtnsInit();//三个按钮的初始化
                    Drawable hdClick = getResources().getDrawable(R.mipmap.hdclicked);
                    hdClick.setBounds(0, 0, hdClick.getMinimumWidth(), hdClick.getMinimumHeight());
                    hdBtn.setCompoundDrawables(null, hdClick, null, null);
                    hdBtn.setBackgroundColor(getResources().getColor(R.color.grayfgx));
                    GlobalData.getInstance(MainActivity.this).getEditor().putInt(APPConstants.VIDEO_DEFINITION, 2).commit();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void initData() {
        SharedPreferences share = GlobalData.getInstance(mContext).getSharedPreferences();
        String uname = share.getString(APPConstants.NICKNAME, "");
        String roomid = "房间号：" + share.getString(APPConstants.USER_ROOMID, "");
        String notice = share.getString(APPConstants.ROOM_NOTICE, "");
        int fans = share.getInt(APPConstants.USER_FANS, 0);
        String allFans = "关注数：" + String.valueOf(fans);

        mTextRoomID.setText(roomid);
        mTextFans.setText(allFans);
        mTextUserName.setText(uname);
        mTextNotice.setText(notice);
        mTextNotice.setSelection(mTextNotice.getText().length());
        //更新头像
        mOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showStubImage(R.mipmap.logo)
                .showImageForEmptyUri(R.mipmap.logo)
                .showImageOnFail(R.mipmap.logo)
                .showImageOnLoading(R.mipmap.logo).cacheOnDisc()
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();

        com.oo58.livevideoassist.view.CircleImageView imgHead = (com.oo58.livevideoassist.view.CircleImageView) findViewById(R.id.showHead);
        String headUrl = GlobalData.getInstance(mContext).getSharedPreferences().getString(APPConstants.USER_ICON, "");
        String realPath = AppUrl.USER_LOGO_ROOT + headUrl;
        GlobalData.getInstance(mContext).getmImageLoader().displayImage(realPath, imgHead, mOptions, null);

        mTextNotice.setCursorVisible(false);
        ld = new LoadingDailog();

        /**
         * 注册一个监听重复登录的广播
         */
        mReceiver = new Receiver() ;
        IntentFilter filter = new IntentFilter();
        filter.addAction(AppAction.RECVIVE_LOGINAGIAN);
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(mReceiver, filter);
    }


    @Override
    protected void onClickView(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
        switch (v.getId()) {
            case R.id.living_helper_btn:
                if (isOpenHelper) {//如果已经显示设置按钮  隐藏
                    helperCon.setVisibility(View.GONE);
                    isOpenHelper = false;
                    helperFlag.setImageBitmap(rightJtou);
                } else {
                    helperCon.setVisibility(View.VISIBLE);
                    isOpenHelper = true;
                    helperFlag.setImageBitmap(downJtou);
                }


                break;
            case R.id.rank_list_btn:
                startActivity(new Intent(AppAction.ACTION_LIVING_HELPER));
                break;
            case R.id.qurey_beans:
                startActivity(new Intent(AppAction.ACTION_QUERY_BEANS));
                break;
            case R.id.setting_btn://点击了设置的顶部按钮
                if (isOpenSetting) {
                    settingCon.setVisibility(View.GONE);
                    isOpenSetting = false;
                    settingFlag.setImageBitmap(rightJtou);
                } else {
                    settingCon.setVisibility(View.VISIBLE);
                    isOpenSetting = true;
                    settingFlag.setImageBitmap(downJtou);
                }
                break;
            case R.id.logout_btn:
                logout();
                break;
            case R.id.start_living_btn:
                startActivity(new Intent(AppAction.ACTION_LIVING));
                break;
            default:
                break;
        }
    }

    //打开和关闭摄像头设置、直播助手
    private void closeBtns(int id) {
        if (id == R.id.setting_btn) {
            helperCon.setVisibility(View.GONE);
            isOpenHelper = false;
            helperFlag.setImageBitmap(rightJtou);

            if (isOpenSetting) {//如果已经显示设置按钮  隐藏
                settingCon.setVisibility(View.GONE);
                isOpenSetting = false;
                settingFlag.setImageBitmap(rightJtou);
            } else {
                settingCon.setVisibility(View.VISIBLE);
                isOpenSetting = true;
                settingFlag.setImageBitmap(downJtou);
            }


        } else {
            settingCon.setVisibility(View.GONE);
            isOpenSetting = false;
            settingFlag.setImageBitmap(rightJtou);
            if (isOpenHelper) {//如果已经显示设置按钮  隐藏
                helperCon.setVisibility(View.GONE);
                isOpenHelper = false;
                helperFlag.setImageBitmap(rightJtou);
            } else {
                helperCon.setVisibility(View.VISIBLE);
                isOpenHelper = true;
                helperFlag.setImageBitmap(downJtou);
            }
        }

    }

    //退出登录
    private void logout() {
        final LogoutDialog logoutDialog = new LogoutDialog();

        logoutDialog.setCancleListen(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
                logoutDialog.closeDialog();
            }
        });
        logoutDialog.setSureListen(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.closeDialog();
                startActivity(new Intent(AppAction.ACTION_LOGIN));
                finish();
            }
        });
        logoutDialog.show(MainActivity.this);


    }

    @Override
    protected void handler(Message msg) {
        switch (msg.what) {
            case M_Handler_UpdateNoticeSuccess:
                String tips = (String) msg.obj;
                mTextNotice.setText(tips);
                GlobalData.getInstance(mContext).getEditor().putString(APPConstants.ROOM_NOTICE, tips).commit();
                mTextNotice.setSelection(mTextNotice.getText().length());
                showToast("公告设置成功");
                break;
            case M_Handler_UpdateNoticeFailed:
                showToast("公告设置失败");
                break;
            case M_Handler_CallLoginAgian:
                final ShowPromptDialog dia = new ShowPromptDialog() ;
                dia.setSureListen(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dia.closeDialog();
                    }
                }).show(MainActivity.this,"账号重复登录");
                break ;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //回收图片资源
        if (rightJtou != null) {
            rightJtou.recycle();
        }
        if (downJtou != null) {
            downJtou.recycle();
        }
        if(mReceiver!=null){
            unregisterReceiver(mReceiver); //注销广播
        }
    }

    private long currentBackPressedTime = 0;// 按下返回键的当前手机系统时间
    private static final int BACK_PRESSED_INTERVAL = 2000; // 两次按下返回键的在这个时间间隔内才会退出

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - currentBackPressedTime > BACK_PRESSED_INTERVAL) {
            currentBackPressedTime = System.currentTimeMillis();
            Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        } else {
            // 退出
            android.os.Process.killProcess(android.os.Process.myPid());//Kill进程
            System.exit(0);
            super.onBackPressed();
        }
    }

    /**
     * 更新房间公告
     */
    private void updateRoomNotice(final String notice) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String uid = GlobalData.getInstance(mContext).getUID();
                    String secert = GlobalData.getInstance(mContext).getUSecert();
                    String roomid = GlobalData.getInstance(mContext).getSharedPreferences().getString(APPConstants.USER_ROOMID, "");
                    String result = Util.addRpcEvent(RpcEvent.CallUpdateRoomNotice, uid, secert, roomid, notice);

                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        Message msg = new Message();
                        msg.what = M_Handler_UpdateNoticeSuccess;
                        msg.obj = notice;
                        handler.sendMessage(msg);
                    } else {
                        handler.sendEmptyMessage(M_Handler_UpdateNoticeFailed);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(M_Handler_UpdateNoticeFailed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }


    /**
     * 接收重读登录的广播
     */
    public class Receiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean flag = intent.getBooleanExtra("loginagain",false);
            if(flag){
                handler.sendEmptyMessage(M_Handler_CallLoginAgian) ;
            }
        }
    }
}
