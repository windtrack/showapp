package com.oo58.livevideoassist.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.oo58.livevideoassist.common.APPConstants;
import com.oo58.livevideoassist.common.AppAction;
import com.oo58.livevideoassist.db.GlobalData;
import com.oo58.livevideoassist.dialog.LoadingDailog;
import com.oo58.livevideoassist.rpc.RpcEvent;
import com.oo58.livevideoassist.util.ThreadPoolWrap;
import com.oo58.livevideoassist.util.UIUtil;
import com.oo58.livevideoassist.util.UpdateManager;
import com.oo58.livevideoassist.util.Util;

import org.json.JSONObject;

import cn.nodemedia.nodemediaclient.R;

/**
 * @author zhongxf
 * @Description 用户登录界面
 * @Date 2016/5/9.
 */
public class LoginActivity extends BaseActivity {

    private Button loginBtn;//登录按钮
    private EditText userInput;//用户名输入
    private EditText pwdInput;//密码输入
    private CheckBox mCheckBoxSave; //是否记住密码

    private String mUname; //用户名
    private String mUPwd; //密码
    private boolean mSaveNameAndPwd;//是否记住密码

    private LinearLayout loginCon;//登录所有的View布局
    private int SCREEN_HEIGHT;//屏幕的高度

    private static final int M_Handler_Login_Success = 1;//登录成功
    private static final int M_Handler_Login_Failed = 2;//登录失败
    private static final int M_Handler_Login_Error = 3;//登录出错
    private static final int M_Handler_UpdateVersion = 4;//版本更新

    private LoadingDailog ld;//等待动画

    private TextView versionName;//显示版本名字


    private LinearLayout allViewCon;


    @Override
    protected void initContentView(Bundle savedInstanceState) {
        setContentView(R.layout.login);
        loginBtn = (Button) findViewById(R.id.login_btn);
        loginBtn.setOnClickListener(this);
        userInput = (EditText) findViewById(R.id.user_input);
        pwdInput = (EditText) findViewById(R.id.pwd_input);
        mCheckBoxSave = (CheckBox) findViewById(R.id.checkbox_savePwd);
        //以下代码是为了设置ScrollView撑开的的高度，这样键盘出现的时候，底下提示版本的问题不会飘起来
        DisplayMetrics dm = getResources().getDisplayMetrics();
        SCREEN_HEIGHT = dm.heightPixels;
        loginCon = (LinearLayout) findViewById(R.id.login_con);
        ViewGroup.LayoutParams lp = loginCon.getLayoutParams();
        loginCon.setMinimumHeight(SCREEN_HEIGHT - UIUtil.dip2px(LoginActivity.this, 75f));

        ld = new LoadingDailog();


        allViewCon = (LinearLayout) findViewById(R.id.all_view_con);
        allViewCon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
            }
        });

        versionName = (TextView) findViewById(R.id.show_version);
        versionName.setText("当前版本:"+Util.getAppVersionName(LoginActivity.this));
    }

    @Override
    protected void initData() {
        boolean isSavePwd = GlobalData.getInstance(mContext).getSharedPreferences().getBoolean(APPConstants.Save, false);
        mCheckBoxSave.setChecked(isSavePwd);
        mUname = GlobalData.getInstance(mContext).getSharedPreferences().getString(APPConstants.USER, "");
        mUPwd = GlobalData.getInstance(mContext).getSharedPreferences().getString(APPConstants.PASS, "");
        userInput.setText(mUname);
        pwdInput.setText(mUPwd);
        GlobalData.getInstance(mContext).getEditor().putBoolean(APPConstants.USER_LOGIN, false).commit();
        userInput.setSelection(userInput.getText().length());
        pwdInput.setSelection(pwdInput.getText().length());
        checkUpdate() ;
    }

    @Override
    protected void handler(Message msg) {
        switch (msg.what) {
            case M_Handler_Login_Success://登录成功
                ld.closeDialog();
                startActivity(new Intent(AppAction.ACTION_MAIN));
                finish();
                break;
            case M_Handler_Login_Failed://登录失败
                ld.closeDialog();
                String tips = msg.obj.toString();
                showToast(tips);
                break;
            case M_Handler_Login_Error://登录出错
                ld.closeDialog();
                showToast("登录失败");
                break;
            case M_Handler_UpdateVersion://版本更新
                UpdateManager mUpdateManager = new UpdateManager(LoginActivity.this, APPConstants.apkDownLoadUrl);
                if(mUpdateManager!=null){
                    mUpdateManager.checkUpdateInfo();
                }
                break;
        }
    }

    @Override
    protected void onClickView(View v) {
        switch (v.getId()) {
            case R.id.login_btn:
                login();
                break;
            case R.id.user_input:
                break;
            case R.id.pwd_input:
                break;
            default:
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    //登录方法
    private void login() {
        mUname = userInput.getText().toString().trim();
        mUPwd = pwdInput.getText().toString().trim();
        if ("".equals(mUname) || "".equals(mUPwd)) {
            showToast("用户名或密码不能为空！");
        } else {
            doLogin();
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
     * 发送登录请求
     */
    private void doLogin() {
        ld.show(LoginActivity.this, false);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String result = Util.addRpcEvent(RpcEvent.CallUserLogin, mUname, mUPwd, APPConstants.localToken, "", "anchor");
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        JSONObject data = obj.getJSONObject("data");
                        SharedPreferences.Editor editor = GlobalData.getInstance(mContext).getEditor();

                        String id = data.getString("id");
                        String secret = obj.getString("secret");
                        String icon = data.getString("icon");
                        String user_name = data.getString("username");
                        String password = data.getString("password");
                        String gender = data.getString("gender");
                        String pos = data.getString("pos");
                        String phone = data.getString("phone");
                        String nickname = data.getString("nickname");

                        String userType = data.getString("user_type");
                        int fans = data.getInt("fans");
                        String roomid = data.getString("room_id");
                        String notice = data.getString("notice");

                        JSONObject userdata = obj.getJSONObject("auth");
                        String timestamp = userdata.getString("timestamp");
                        String openkey = userdata.getString("openkey");

                        //界面登录数据
                        mSaveNameAndPwd = mCheckBoxSave.isChecked();
                        if (mSaveNameAndPwd) {
                            editor.putString(APPConstants.USER, mUname);
                            editor.putString(APPConstants.PASS, mUPwd);
                            editor.putBoolean(APPConstants.Save, mSaveNameAndPwd);
                        } else {
                            editor.putString(APPConstants.USER, "");
                            editor.putString(APPConstants.PASS, "");
                            editor.putBoolean(APPConstants.Save, false);
                        }

                        editor.putString(APPConstants.OPEN_ID, id);
                        editor.putString(APPConstants.OPENKEY, openkey);
                        editor.putString(APPConstants.TIMESTAMP, timestamp);

                        editor.putString(APPConstants.GENDER, gender);
                        editor.putString(APPConstants.NICKNAME, nickname);
                        editor.putString(APPConstants.USER_SECRET, secret);
                        editor.putString(APPConstants.USER_ICON, icon);
                        editor.putString(APPConstants.USER_ROOMID, roomid);
                        editor.putString(APPConstants.USER_TYPE, userType);
                        editor.putInt(APPConstants.USER_FANS, fans);
                        editor.putString(APPConstants.ROOM_NOTICE, notice);
                        editor.putBoolean(APPConstants.USER_LOGIN, true);
                        editor.commit();

                        handler.sendEmptyMessage(M_Handler_Login_Success);
                    } else {
                        String tips = obj.getString("data");
                        Message msg = new Message();
                        msg.what = M_Handler_Login_Failed;
                        msg.obj = tips;
                        handler.sendMessage(msg);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(M_Handler_Login_Error);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }


    /**
     * 版本更新检测
     */
    private void checkUpdate(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String result = Util.addRpcEvent(RpcEvent.CheckUpdateVersion);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if(s == 1){
                        JSONObject data = obj.getJSONObject("data");
                        APPConstants.serverVersionCode = data.getInt("androidversioncode");//版本
                        //是否需要更新
                        if(APPConstants.localVersionCode <  APPConstants.serverVersionCode){
                            APPConstants.serverVersionName = data.getString("aver");//版本号
                            APPConstants.apkDownLoadUrl = data.getString("aurl"); //下载地址
                            APPConstants.updateTips = data.getString("updateinfo");//更新内容
                            handler.sendEmptyMessage(M_Handler_UpdateVersion) ;
                        }

                    }else{

                    }
                }catch (Exception e){
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    /**
     * 关闭软件
     */
    public void doFinish(){
        android.os.Process.killProcess(android.os.Process.myPid());//Kill进程
        System.exit(0);
    }
}
