package com.oo58.livevideoassist.dialog;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import cn.nodemedia.nodemediaclient.R;
import com.oo58.livevideoassist.adapter.RankListAdapter;
import com.oo58.livevideoassist.common.AppUrl;
import com.oo58.livevideoassist.db.GlobalData;
import com.oo58.livevideoassist.entity.RankListVo;
import com.oo58.livevideoassist.rpc.RpcEvent;
import com.oo58.livevideoassist.util.ThreadPoolWrap;
import com.oo58.livevideoassist.util.HideViewListener;
import com.oo58.livevideoassist.util.Util;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongxf
 * @Description 显示排行榜的对话框
 * @Date 2016/5/12.
 */
public class RankListDialog extends PopupWindow {

    private static final int M_Handler_GetRankListSuccess = 1; //获取排行版成功
    private static final int M_Handler_GetRankListFailed = 2; //获取排行榜失败

    private View conentView;//弹出框的布局
    private PopupWindow pop;//该Popuwindow对象
    private ListView listView;//排行榜的ListView
    private List<RankListVo> list;//排行榜的Vo
    private TranslateAnimation animation;//平移动画
    private LayoutAnimationController controller;//ListVeiw的动画控制
    private Context mContext;

    public RankListDialog(final Activity context) {
        pop = this;
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.rank_list, null);
        int h = context.getWindowManager().getDefaultDisplay().getHeight();
        int w = context.getWindowManager().getDefaultDisplay().getWidth();
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        listView = (ListView) conentView.findViewById(R.id.rank_list);
        list = new ArrayList<RankListVo>();

        animation = new TranslateAnimation(-200f, 0f, 0f, 0f);//移动的距离（从Item的-200Px移动到Item的正确位置）
        animation.setDuration(500);//动画持续时间
        //0.2f是每个Item出现动画的延迟时间
        controller = new LayoutAnimationController(animation, 0.2f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        listView.setLayoutAnimation(controller);//给ListView设置动画控制

        ImageButton closeBtn = (ImageButton) conentView.findViewById(R.id.close);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        listView.setAdapter(new RankListAdapter(list, context));
        LinearLayout rootView = (LinearLayout) conentView.findViewById(R.id.pop_root_view);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(context.getResources().getColor(R.color.black50));
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimationPreview);

        LinearLayout linearLayout_rankListDialog = (LinearLayout) conentView.findViewById(R.id.linearLayout_rankListDialog);
        LinearLayout linearLayout_rankView = (LinearLayout) conentView.findViewById(R.id.linearLayout_rankView);

        HideViewListener hideListener  =  new HideViewListener(linearLayout_rankListDialog, new HideViewListener.HideViewImp() {
            @Override
            public void hideView() {
                dismiss();
            }

            @Override
            public void showView() {

            }
        });
        conentView.setOnTouchListener(hideListener);
        linearLayout_rankView.setOnTouchListener(hideListener);
//        listView.setOnTouchListener(hideListener);

    }

    //显示弹出框
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            showAtLocation(parent, Gravity.TOP | Gravity.RIGHT, 0, 0);
            loadRankList();
        } else {
            this.dismiss();
        }
    }

    //加载排行榜数据
    private void loadRankList() {
        list.clear();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String uid = GlobalData.getInstance(mContext).getUID();
                    String usc = GlobalData.getInstance(mContext).getUSecert();
                    String roomid = GlobalData.getInstance(mContext).getURoomId();
                    String result = Util.addRpcEvent(RpcEvent.GetRoomRankList, uid, usc, roomid, "day", 20);
                    JSONObject obj = new JSONObject(result);
                    int s = obj.getInt("s");
                    if (s == 1) {
                        JSONArray array = obj.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject rankInfo = array.getJSONObject(i);
                            String name = rankInfo.getString("nickname");
                            String beans = rankInfo.getString("num");
                            String icon = rankInfo.getString("icon");
                            RankListVo vo = new RankListVo();
                            vo.setSort(i + 1);
                            vo.setName(name);
                            vo.setMoney(beans);
                            String fillpath = AppUrl.USER_SMALL_HEAD_PATH + icon;
                            vo.setFace(fillpath);
                            list.add(vo);
                        }
                        handler.sendEmptyMessage(M_Handler_GetRankListSuccess);
                    } else {
                        handler.sendEmptyMessage(M_Handler_GetRankListFailed);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessage(M_Handler_GetRankListFailed);
                }
                ThreadPoolWrap.getThreadPool().removeTask(this);
            }
        };
        ThreadPoolWrap.getThreadPool().executeTask(runnable);

    }


    Handler handler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case M_Handler_GetRankListSuccess: {
                    RankListAdapter adapter = (RankListAdapter) listView.getAdapter();
                    adapter.notifyDataSetChanged();
                }
                break;
                case M_Handler_GetRankListFailed: {
                    RankListAdapter adapter = (RankListAdapter) listView.getAdapter();
                    adapter.notifyDataSetChanged();
                    Toast.makeText(mContext, "获取排行榜失败", Toast.LENGTH_SHORT).show();
                }
                break;

            }
        }
    };

}
