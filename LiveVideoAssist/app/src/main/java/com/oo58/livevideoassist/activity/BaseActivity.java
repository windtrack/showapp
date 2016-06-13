package com.oo58.livevideoassist.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Toast;

import com.oo58.livevideoassist.util.UIHandler;

/**
 * @author zhongxf
 * @Description activity的基础类
 * @Date 2016/5/9.
 *
 * update by sunjinfang
 * 1、设置底层handler回调
 * 2、抽象initContentView(),onClickView（）
  * @Date 2016/5/10.
 *
 * update by sunjinfang
 * 1、新增showToast打印数据
 * 2、复写destory 加入 handler销毁
 * 3、新增抽象initData（）
 * @Date 2016/5/11.
 *
 */
public abstract class BaseActivity extends Activity implements View.OnClickListener {

    protected UIHandler handler = new UIHandler(Looper.getMainLooper());
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this.getApplicationContext();
        initContentView(savedInstanceState);
        initData();
        setHandler();
    }

    /**
     * Desc: 设置handler
     */
    private void setHandler() {
        handler.setHandler(new UIHandler.IHandler() {
            public void handleMessage(Message msg) {
                handler(msg);
            }
        });
    }

    @Override
    public void onClick(View v) {
        onClickView(v);
    }

    /**
     * 获取全局的Context
     */
    public Context getContext() {
        return mContext;
    }

    /**
     * 设置 setContentView   当做onCreat()使用
     *
     * @param savedInstanceState
     */
    protected abstract void initContentView(Bundle savedInstanceState);

    /**
     * 初始化基础数据
     */
    protected abstract void initData();

    /**
     * handler消息接收
     *
     * @param msg
     */
    protected abstract void handler(Message msg);

    /**
     * 事件监听
     *
     * @param v
     */
    protected abstract void onClickView(View v);

    /**
     * 打印一条屏幕消息
     *
     * @param msg 消息内容
     */
    public void showToast(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.destory();
    }
}
