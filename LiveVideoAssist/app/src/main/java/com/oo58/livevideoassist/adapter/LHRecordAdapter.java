package com.oo58.livevideoassist.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import cn.nodemedia.nodemediaclient.R;
import com.oo58.livevideoassist.entity.RecordVo;


import java.util.List;

/**
 * @author zhongxf
 * @Description 直播助手的流水记录的适配器
 * @Date 2016/5/13.
 */
public class LHRecordAdapter extends BaseAdapter {

    private List<RecordVo> list;
    private Context cxt;

    public LHRecordAdapter(List<RecordVo> list, Context cxt) {
        this.list = list;
        this.cxt = cxt;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(cxt).inflate(R.layout.record_list_item, null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.num = (TextView) convertView.findViewById(R.id.show_num);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        RecordVo vo = list.get(position);
        if (vo != null) {
            viewHolder.name.setText(vo.getName());
            viewHolder.num.setText(vo.getNum()+"个");
        }

        return convertView;
    }

    static class ViewHolder {
        TextView name;//礼物名称
        TextView num;//礼物数量
    }
}
