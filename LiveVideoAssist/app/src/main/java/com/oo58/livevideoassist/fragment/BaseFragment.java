package com.oo58.livevideoassist.fragment;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.oo58.livevideoassist.util.UIHandler;

/**
 * Desc: Fragment 基础抽象类
 * Created by sunjinfang on 2016/5/12 9:11.
 */
public abstract  class BaseFragment extends Fragment implements View.OnClickListener{

    protected UIHandler handler = new UIHandler(Looper.getMainLooper());
    public Context mContext ;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHandler();
    }

    @Override
    public void onClick(View v) {
        onClickView(v) ;
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
    public void onDestroyView() {
        super.onDestroyView();
        handler.destory();
    }

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
}
