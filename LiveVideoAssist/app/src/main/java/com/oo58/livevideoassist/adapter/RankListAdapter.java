package com.oo58.livevideoassist.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import cn.nodemedia.nodemediaclient.R;
import com.oo58.livevideoassist.db.GlobalData;
import com.oo58.livevideoassist.entity.RankListVo;
import com.oo58.livevideoassist.view.CircleImageView;

import java.util.List;

/**
 * @author zhongxf
 * @Description 本场直播排名的List
 * @Date 2016/5/13.
 */
public class RankListAdapter extends BaseAdapter {

    private List<RankListVo> list;
    private Context cxt;
    private DisplayImageOptions mOptions;

    public RankListAdapter(List<RankListVo> list, Context cxt) {

        this.list = list;
        this.cxt = cxt;
        mOptions = new DisplayImageOptions.Builder()
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showStubImage(R.mipmap.logo)
                .showImageForEmptyUri(R.mipmap.logo)
                .showImageOnFail(R.mipmap.logo)
                .showImageOnLoading(R.mipmap.logo).cacheOnDisc()
                .imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
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
            convertView = LayoutInflater.from(cxt).inflate(R.layout.rank_list_item, null);
            viewHolder.sort = (TextView) convertView.findViewById(R.id.show_sort);
            viewHolder.face = (CircleImageView) convertView.findViewById(R.id.face);
            viewHolder.money = (TextView) convertView.findViewById(R.id.show_money);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        RankListVo vo = list.get(position);
        if (vo != null) {
            int sort = vo.getSort();
            viewHolder.sort.setText(String.valueOf(sort));
            if (sort == 1) {
                viewHolder.sort.setTextColor(cxt.getResources().getColor(R.color.yellowbg));
            } else if (sort == 2) {
                viewHolder.sort.setTextColor(cxt.getResources().getColor(R.color.sort2));
            } else if (sort == 3) {
                viewHolder.sort.setTextColor(cxt.getResources().getColor(R.color.sort3));
            } else {
                viewHolder.sort.setTextColor(cxt.getResources().getColor(R.color.settingbtnfgx));
            }
            viewHolder.name.setText(vo.getName());
            viewHolder.money.setText(vo.getMoney() + "乐币");
            if (vo.getFace() != null && !"".equals(vo.getFace())) {
                GlobalData.getInstance(cxt).getmImageLoader().displayImage(vo.getFace(), viewHolder.face, mOptions, null);
            }
        }
        return convertView;
    }

    static class ViewHolder {

        TextView sort;//排名
        CircleImageView face;//头像
        TextView name;//名称
        TextView money;//乐币数

    }
}
