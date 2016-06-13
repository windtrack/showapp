package com.oo58.livevideoassist.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.nodemedia.nodemediaclient.R;


/**
 * @author zhongxf
 * @Description 倒计时对话框
 * @Date 2016/5/17.
 */
public class CountDownDialog {
    private Dialog dialog;
    private int time = 30;//倒计时的总时间
    private Handler handler;//消息处理
    private int COUNT_DOWN_TIME = 0;//handler的消息what
    private boolean isCanCountDown = true;//是否可以倒计时

    /**
     * 显示一个提示框
     */
    public void show(Context context, CountDownInterface countDownInterface) {
        creatDialog(context, countDownInterface);
    }

    private void creatDialog(Context context, final CountDownInterface countDownInterface) {
        dialog = new Dialog(context, R.style.show_dialog);// 创建自定义样式dialog
        View main = LayoutInflater.from(context).inflate(
                R.layout.count_down, null);

        final TextView showTime = (TextView) main.findViewById(R.id.time);
        showTime.setText(String.valueOf(time));
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == COUNT_DOWN_TIME) {
                    if (isCanCountDown) {
                        time = time - 1;
                        if (time < 0) {
                            closeDialog();//关闭对话框
                            countDownInterface.finishTime();//调用接口的回调方法
                        } else {
                            showTime.setText(String.valueOf(time));
                            handler.sendEmptyMessageDelayed(COUNT_DOWN_TIME, 1000);//再次发送倒计时按钮
                        }
                    }
                }
            }
        };
        handler.sendEmptyMessageAtTime(COUNT_DOWN_TIME, 1000);//发送倒计时消息
        LinearLayout.LayoutParams fill_parent = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        dialog.setContentView(main, fill_parent);// 设置布局
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 关闭提示框
     */
    public void closeDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

    /**
     * 停止倒计时
     */
    public void stopCounDown() {
        isCanCountDown = false;
        if (dialog != null) {
            dialog.dismiss();
        }
    }


}
