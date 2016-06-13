package com.oo58.livevideoassist.activity;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.oo58.livevideoassist.adapter.LHRecordAdapter;
import com.oo58.livevideoassist.adapter.LivingHelperViewAdapter;
import com.oo58.livevideoassist.db.GlobalData;
import com.oo58.livevideoassist.entity.RecordVo;
import com.oo58.livevideoassist.rpc.RpcEvent;
import com.oo58.livevideoassist.util.DLog;
import com.oo58.livevideoassist.util.ThreadPoolWrap;
import com.oo58.livevideoassist.util.UIUtil;
import com.oo58.livevideoassist.util.Util;
import com.oo58.livevideoassist.view.PagerSlidingTabStrip;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.nodemedia.nodemediaclient.R;

/**
 * @author zhongxf
 * @Description 直播助手界面
 * @Date 2016/5/10.
 */
public class LivingHelperActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private static final int M_Handler_LoadDataSucces = 1;//载入数据成功
    private static final int M_Handler_LoadDataFailed = 2;//载入数据失败

    public static final int DATA_TYPE_DAY = 0; //日榜
    public static final int DATA_TYPE_WEEK = 1;//周榜
    public static final int DATA_TYPE_MONTH = 2;//月榜

    private PagerSlidingTabStrip pst;//顶部切换的Tab
    private ViewPager pager;//ViewPager
    private List<View> viewList;//所有的View的List

    private List<RecordVo>[] rankListDataSource;//榜数据源
    private ListView[] rankListView;//日榜的ListView
    private View[] rankView;//日榜的View

    private TranslateAnimation animation;//平移动画
    private LayoutAnimationController controller;//ListVeiw的动画控制

    private ImageButton closeBtn;//关闭按钮

    private LinearLayout topLayer ; //滑动层
    @Override
    protected void initContentView(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.living_helper);
        viewList = new ArrayList<View>();
        pst = (PagerSlidingTabStrip) findViewById(R.id.helper_indicator);
        int width = UIUtil.dip2px(LivingHelperActivity.this, 12.0f);//设置左右的分割线的高度
        pst.setTabPaddingLeftRight(width);
        pager = (ViewPager) findViewById(R.id.helper_pager);
        pager.setAdapter(new LivingHelperViewAdapter(viewList));
        pst.setViewPager(pager);
        pst.setSelectedTextColorResource(R.color.blackwords);//设置选中的Tab的颜色
        pst.setIndicatorColorResource(R.color.yellowbg);//设置下面的滑动条的颜色
        pst.setTextSize(getResources().getDimensionPixelSize(R.dimen.livehall_tab_textsize));//字体的大小

        pst.setOnPageChangeListener(this);


        //设置ListView出现的动画
        animation = new TranslateAnimation(-200f, 0f, 0f, 0f);//移动的距离（从Item的-200Px移动到Item的正确位置）
        animation.setDuration(500);//动画持续时间
        //0.2f是每个Item出现动画的延迟时间
        controller = new LayoutAnimationController(animation, 0.2f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);

        closeBtn = (ImageButton) findViewById(R.id.close);
        closeBtn.setOnClickListener(this);

        topLayer = (LinearLayout) findViewById(R.id.linearLayout_livingHelpTop);

        initView();//初始化View
    }

    //初始化View
    private void initView() {
        rankListDataSource = new ArrayList[3];
        rankListView = new ListView[3];
        rankView = new View[3];
        for (int i = 0; i < rankListDataSource.length; i++) {
            rankView[i] = LayoutInflater.from(LivingHelperActivity.this).inflate(R.layout.helper_record, null);
            rankListDataSource[i] = new ArrayList<RecordVo>();
            rankListView[i] = (ListView) rankView[i].findViewById(R.id.record_list);
            rankListView[i].setAdapter(new LHRecordAdapter(rankListDataSource[i], LivingHelperActivity.this));
            rankListView[i].setLayoutAnimation(controller);
            viewList.add(rankView[i]);
        }
        rlWidth = Util.getDisplayMetrics(mContext).widthPixels;
    }


    @Override
    protected void handler(Message msg) {
        switch (msg.what) {
            case M_Handler_LoadDataSucces:
                int type = msg.arg1;
                LHRecordAdapter adapter = (LHRecordAdapter) rankListView[type].getAdapter();
                adapter.notifyDataSetChanged();
                break;
            case M_Handler_LoadDataFailed:
                showToast("载入数据异常");
                break;
        }
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
    protected void initData() {
        loadData(DATA_TYPE_DAY);
        loadData(DATA_TYPE_WEEK);
        loadData(DATA_TYPE_MONTH);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 根据类型载入数据
     *
     * @param type
     */
    private void loadData(final int type) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String uid = GlobalData.getInstance(mContext).getUID();
                    String usc = GlobalData.getInstance(mContext).getUSecert();
                    String roomid = GlobalData.getInstance(mContext).getURoomId();

                    String[] dataType = {"day", "week", "month"};
                    String result = Util.addRpcEvent(RpcEvent.GetRoomGiftRankList, uid, usc, roomid, dataType[type]);
                    DLog.e("unfind","加载礼物排行的返回数据："+result);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        rankListDataSource[type].clear();
                        JSONArray giftlist = obj.getJSONArray("data");
                        for (int i = 0; i < giftlist.length(); i++) {
                            JSONObject gift = giftlist.optJSONObject(i);
                            String name = gift.getString("name");
                            String count = gift.getString("num");
                            RecordVo vo = new RecordVo();
                            vo.setName(name);
                            vo.setNum(count);
                            rankListDataSource[type].add(vo);
                        }
                        Message msg = new Message();
                        msg.what = M_Handler_LoadDataSucces;
                        msg.arg1 = type;
                        handler.sendEmptyMessage(M_Handler_LoadDataSucces);
                    } else {
                        handler.sendEmptyMessage(M_Handler_LoadDataFailed);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(M_Handler_LoadDataFailed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);
    }

    private boolean isScrolling = false;
    private int lastValue = -1;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (isScrolling) {
            if (lastValue == positionOffsetPixels) {
                if (position == 0) {
                    //滑到到第一页继续往左滑动 关闭界面
                    isScrolling = false;
                    this.finish();
                }
            }
        }
        lastValue = positionOffsetPixels;
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if (state == 1) {
            isScrolling = true;
        } else {
            isScrolling = false;
        }
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
