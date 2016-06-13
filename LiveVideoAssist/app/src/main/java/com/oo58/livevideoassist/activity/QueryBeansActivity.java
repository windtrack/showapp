package com.oo58.livevideoassist.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.nodemedia.nodemediaclient.R;
import com.oo58.livevideoassist.db.GlobalData;
import com.oo58.livevideoassist.rpc.RpcEvent;
import com.oo58.livevideoassist.util.ThreadPoolWrap;
import com.oo58.livevideoassist.util.Util;

import org.json.JSONObject;

/**
 * @author zhongxf
 * @Description 查询乐豆的界面
 * @Date 2016/5/16.
 */
public class QueryBeansActivity extends BaseActivity {

    private static final int M_Handler_GetBeansSuccess = 1 ;//获取乐豆
    private static final int M_Handler_GetBeansFailed = 2 ;

    private ImageButton closeBtn;//关闭按钮
    private TextView showBeansNum;//显示乐购数目
    private LinearLayout topLayer ; //根节点 //滑动层
    @Override
    protected void initContentView(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.query_beans);
        closeBtn = (ImageButton) findViewById(R.id.close);
        closeBtn.setOnClickListener(this);
        showBeansNum = (TextView) findViewById(R.id.beans_num);
        topLayer = (LinearLayout) findViewById(R.id.linear_beans);
        rlWidth = Util.getDisplayMetrics(mContext).widthPixels;
    }

    @Override
    protected void onClickView(View v) {
        switch (v.getId()) {
            case R.id.close:
                finish();
                break;
            default:
                break;
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void initData() {
        UpdateBeans() ;
    }

    @Override
    protected void handler(Message msg) {
            switch (msg.what){
                case M_Handler_GetBeansFailed:
                    showToast("获取乐豆失败");
                    showBeansNum.setText("0");
                    break ;
                case M_Handler_GetBeansSuccess:
                    String beans = (String)msg.obj;
                    showBeansNum.setText(beans);
                    break ;
            }
    }


    /**
     * 更新乐豆
     */
    private void UpdateBeans(){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String uid = GlobalData.getInstance(mContext).getUID();
                    String usc = GlobalData.getInstance(mContext).getUSecert();
                    String result = Util.addRpcEvent(RpcEvent.GetAnchorBeans,uid,usc);
                    JSONObject obj = new JSONObject(result) ;
                    int s = obj.getInt("s") ;
                    if(s == 1 ){
                        String  beans = obj.getString("data");
                        Message msg = new Message() ;
                        msg.obj = beans ;
                        msg.what = M_Handler_GetBeansSuccess ;
                        handler.sendMessage(msg);
                    }else{
                        handler.sendEmptyMessage(M_Handler_GetBeansFailed);
                    }
                }catch (Exception e){
                    handler.sendEmptyMessage(M_Handler_GetBeansFailed) ;
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);

    }


    private static final int MAX_OFFSET = 5;// 5个像素误差，滑动小于5个像素就没有动画
    private float downX;// 按下时的点
    private float viewXdown;// 按下时View的位置
    private boolean lastSlidePull = false;// 最后一次滑动的方向
    private float maxOffset = 0;// 最大的滑动距离
    private int rlWidth;// 布局的宽度
    @Override
    public boolean onTouchEvent(MotionEvent event) {
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
                maxOffset =  Math.abs(offset);
                if (offset < 0) {// pull to show
                    lastSlidePull = true;
                } else {// push to hide
                    topLayer.setX(posX > -rlWidth ? posX : -rlWidth);
                    if (posX <= -rlWidth) {// 防止不跟手，更新downX的值
                        this.downX += (posX + rlWidth);
                    }
                    lastSlidePull = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (maxOffset < MAX_OFFSET) {// 防止抖动
                    return super.onTouchEvent(event);
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
        return super.onTouchEvent(event);
    }


    /**
     * 使用ValueAnimator将rl_left以动画的形式弹出去
     */
    private void slideToHide() {
        float startX = topLayer.getX();
        ValueAnimator valueAnimator = ValueAnimator.ofInt((int) startX,
                rlWidth);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int offset = (Integer) animation.getAnimatedValue();
                topLayer.setX(offset);
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        float fraction = Math.abs((rlWidth + startX) / rlWidth);
        valueAnimator.setDuration((long) (400 * fraction));
        valueAnimator.start();
    }
}
