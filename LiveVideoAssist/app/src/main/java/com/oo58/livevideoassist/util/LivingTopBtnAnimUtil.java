package com.oo58.livevideoassist.util;

import android.content.Context;
import android.media.Image;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;

/**
 * @author zhongxf
 * @Description 直播界面的顶部的动画辅助类
 * @Date 2016/5/12.
 */
public class LivingTopBtnAnimUtil {


    private TranslateAnimation closeAnim;//关闭菜单的平移动画
    private TranslateAnimation openAnim;//开启菜单的平移动画

    public static int time = 200;

    private float toX ;
    private float toY;
    private float fromX;
    private float fromY;
    private ImageButton btn;

    //动画初始化
    public LivingTopBtnAnimUtil(final ImageButton btn,ImageButton desBtn){

        toX = desBtn.getX();
        toY = desBtn.getY();
        fromX = btn.getX();
        fromY = btn.getY();

        this.btn = btn;
        closeAnim = new TranslateAnimation(0,toX-fromX,0,0);
        closeAnim.setDuration(time);
        closeAnim.setRepeatCount(0);
        closeAnim.setInterpolator(new LinearInterpolator());
        closeAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btn.clearAnimation();
                btn.setVisibility(View.INVISIBLE);
                btn.setX(toX);
                btn.setY(toY);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        openAnim = new TranslateAnimation(0 ,fromX-toX,0,0);
        openAnim.setDuration(time);
        openAnim.setRepeatCount(0);
        openAnim.setInterpolator(new LinearInterpolator());
        openAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                btn.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btn.clearAnimation();
                btn.setX(fromX);
                btn.setY(fromY);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    public void close(){
        this.btn.startAnimation(closeAnim);
    }
    public void open(){
        this.btn.startAnimation(openAnim);
    }

}
