package com.oo58.livevideoassist.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import cn.nodemedia.nodemediaclient.R;

/**
 * @author zhongxf
 * @Description 注销登录的对话框
 * @Date 2016/5/16.
 */
public class LogoutDialog {

    private Dialog dialog;
    private View.OnClickListener cancleListen;//取消按钮的监听
    private View.OnClickListener sureListen;//确定按钮的执行方法

    /**
     * 显示一个提示框
     */
    public void show(Context context) {
        creatDialog(context);
    }

    public LogoutDialog setCancleListen(View.OnClickListener cancleListen) {
        this.cancleListen = cancleListen;
        return this;
    }

    public LogoutDialog setSureListen(View.OnClickListener sureListen) {
        this.sureListen = sureListen;
        return this;
    }


    private void creatDialog(Context context) {
        dialog = new Dialog(context, R.style.show_dialog);// 创建自定义样式dialog
        View main = LayoutInflater.from(context).inflate(
                R.layout.louout_dialog, null);

        Button cancleBtn = (Button) main.findViewById(R.id.cancle_btn);
        if (cancleListen != null) {
            cancleBtn.setOnClickListener(cancleListen);
        }

        Button sureBtn = (Button) main.findViewById(R.id.suer_btn);
        if (sureListen != null) {
            sureBtn.setOnClickListener(sureListen);
        }
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
}
