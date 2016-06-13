package com.oo58.livevideoassist.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LayoutAnimationController;
import android.view.animation.TranslateAnimation;
import android.widget.ListView;

import cn.nodemedia.nodemediaclient.R;
import com.oo58.livevideoassist.adapter.LHRecordAdapter;
import com.oo58.livevideoassist.entity.RecordVo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhongxf
 * @Description 直播助手的流水记录的ViewHelper
 * @Date 2016/5/13.
 */
public class LHRecordViewManager {

    private Context cxt;//上下文
    private ListView listView;//显示数据的ListView
    private List<RecordVo> list;//数据源

    private TranslateAnimation animation;//平移动画
    private LayoutAnimationController controller;//ListVeiw的动画控制

    public LHRecordViewManager(Context cxt) {
        this.cxt = cxt;
    }

    /**
     * 获取流水记录的View
     */
    public View getRecordView() {
        View v = LayoutInflater.from(cxt).inflate(R.layout.helper_record, null);
        listView = (ListView) v.findViewById(R.id.record_list);
        list = new ArrayList<RecordVo>();
        listView.setAdapter(new LHRecordAdapter(list,cxt));

        //设置ListView出现的动画
        animation = new TranslateAnimation(-200f, 0f, 0f, 0f);//移动的距离（从Item的-200Px移动到Item的正确位置）
        animation.setDuration(500);//动画持续时间
        //0.2f是每个Item出现动画的延迟时间
        controller = new LayoutAnimationController(animation, 0.2f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        listView.setLayoutAnimation(controller);//给ListView设置动画控制

        return v;
    }

    //加载数据
    public void loadData() {
        list.clear();
        for (int i = 1; i <= 10; i++) {
            RecordVo vo = new RecordVo();
            vo.setName("测试" + i);
            vo.setNum("2000");
            list.add(vo);
        }
        LHRecordAdapter adapter = (LHRecordAdapter) listView.getAdapter();
        adapter.notifyDataSetChanged();
    }
}
