package com.oo58.livevideoassist.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import cn.nodemedia.nodemediaclient.R;

/**
 * @author zhongxf
 * @Description 弹出等待层
 * @Date 2016/5/17.
 */
public class LoadingDailog {


    private Dialog loadingDialog;

    /**
     * 显示一个等待框
     * context上下文环境
     * isCancel是否能用返回取消
     * isRighttrue文字在右边false在下面
     */
    public void show(Context context, boolean isCancel) {
        creatDialog(context, isCancel);
    }


    private void creatDialog(Context context, boolean isCancel) {
        LinearLayout.LayoutParams wrap_content = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams wrap_content0 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout main = new LinearLayout(context);
        main.setOrientation(LinearLayout.VERTICAL);
        wrap_content.setMargins(10, 5, 10, 15);
        wrap_content0.setMargins(35, 25, 35, 0);
        main.setGravity(Gravity.CENTER);
        ImageView spaceshipImage = new ImageView(context);
        spaceshipImage.setImageResource(R.mipmap.waittingicon);
        // 加载旋转动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(context, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        loadingDialog = new Dialog(context, R.style.loading_dialog);// 创建自定义样式dialog
        loadingDialog.setCancelable(isCancel);// 是否可以用返回键取消
        main.addView(spaceshipImage, wrap_content0);
        LinearLayout.LayoutParams fill_parent = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.FILL_PARENT,
                LinearLayout.LayoutParams.FILL_PARENT);
        loadingDialog.setContentView(main, fill_parent);// 设置布局
        loadingDialog.show();
    }

    /**
     * 关闭等待层
     */
    public void closeDialog() {
        loadingDialog.dismiss();
    }


}
