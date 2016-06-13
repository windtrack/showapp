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
 * @Description  关闭直播的提示的弹出框
 * @Date 2016/5/12.
 */
public class CloseLivingTipsDialog {


    private Dialog dialog;
    private View.OnClickListener finishListen;

    /**
     * 显示一个提示框
     */
    public void show(Context context) {
        creatDialog(context);
    }

    public CloseLivingTipsDialog setFinishListren(View.OnClickListener listen){
        this.finishListen = listen;
        return this;
    }


    private void creatDialog(Context context) {
        dialog = new Dialog(context, R.style.show_dialog);// 创建自定义样式dialog
        View main = LayoutInflater.from(context).inflate(
                R.layout.close_living_tips, null);

        Button finishBtn = (Button) main.findViewById(R.id.finish_living_btn);
        if(finishListen!=null){
            finishBtn.setOnClickListener(finishListen);
        }

        Button continuBtn = (Button) main.findViewById(R.id.continue_living_btn);
        continuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        LinearLayout.LayoutParams fill_parent = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);
        dialog.setContentView(main, fill_parent);// 设置布局
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 关闭提示框
     * */
    public void closeDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
