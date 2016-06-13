package com.oo58.livevideoassist.util;

import android.view.MotionEvent;
import android.view.View;

/**
 * Desc: 控制view滑动出屏幕的监听
 * Created by sunjinfang on 2016/5/19 10:23.
 *
 * 留待后期添加通用方法
 */
public class HideViewListener implements View.OnTouchListener {
    public static final int MOVEHIDE_LEFT = 1; //向左滑动隐藏
    public static final int MOVEHIDE_RIGHT = 2;//向右滑动隐藏

    private static final int MAX_OFFSET = 5;// 5个像素误差，滑动小于5个像素就没有动画
    private float downX;// 按下时的点
    private float viewXdown;// 按下时View的位置
    private boolean lastSlidePull = false;// 最后一次滑动的方向
    private float maxOffset = 0;// 最大的滑动距离
    private int rlWidth;// 布局的宽度
    private View topLayer;
    private HideViewImp mHideImp; //通知接口
    private int curMoveDir;
    public HideViewListener(View view, HideViewImp listener) {
        topLayer = view;
        mHideImp = listener;
        rlWidth = Util.getDisplayMetrics(view.getContext()).widthPixels;
        curMoveDir = MOVEHIDE_RIGHT ;//暂时默认向右
    }
    private void slideToHide() {
        mHideImp.hideView();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

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

                if(curMoveDir == MOVEHIDE_LEFT){
                    if (offset>0) {// pull to show
//                        topLayer.setX(posX < 0 ? posX : 0);
//                        if (posX >= 0) {// 防止不跟手，更新downX的值
//                            this.downX += posX;
//                        }
                        lastSlidePull = false;
                    } else {// push to hide
//                        topLayer.setX(posX > -rlWidth ? posX : -rlWidth);
//                        if (posX <= -rlWidth) {// 防止不跟手，更新downX的值
//                            this.downX += (posX + rlWidth);
//                        }
                        lastSlidePull = true;
                    }
                }
                if(curMoveDir == MOVEHIDE_RIGHT){
                    if (offset<0) {// pull to show
//                        topLayer.setX(posX < 0 ? posX : 0);
//                        if (posX >= 0) {// 防止不跟手，更新downX的值
//                            this.downX += posX;
//                        }
                        lastSlidePull = true;
                    } else {// push to hide
//                        topLayer.setX(posX > -rlWidth ? posX : -rlWidth);
//                        if (posX <= -rlWidth) {// 防止不跟手，更新downX的值
//                            this.downX += (posX + rlWidth);
//                        }
                        lastSlidePull = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (maxOffset < MAX_OFFSET) {// 防止抖动
                    return false;
                }
                // 使用动画滑动到指定位置
                if (lastSlidePull) {
                } else {
                    slideToHide();
                }
                break;
            default:
                break;
        }
        return true;
    }

    /**
     * 隐藏的通知借口
     */
    public interface HideViewImp {
        public void hideView();
        public void showView();
    }

}

