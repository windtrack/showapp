package com.oo58.livevideoassist.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import java.util.List;

/**
 * @author zhongxf
 * @Description 直播助手的的顶部切换的适配器
 * @Date 2016/5/13.
 */
public class LivingHelperViewAdapter extends PagerAdapter {
    private String[] TITLES = {"日榜", "周榜","月榜"};
    private List<View> viewLists;
    public LivingHelperViewAdapter(List<View> viewList) {
        this.viewLists = viewList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return TITLES.length;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        // TODO Auto-generated method stub
        return arg0 == arg1;
    }


    @Override
    public CharSequence getPageTitle(int position) {
        // TODO Auto-generated method stub
        return TITLES[position];
    }

    public String[] getTITLES() {
        return TITLES;
    }

    @Override
    public void destroyItem(View view, int position, Object object) { // 销毁Item
        ((ViewPager) view).removeView(viewLists.get(position));
    }

    @Override
    public Object instantiateItem(View view, int position) { // 实例化Item
        ((ViewPager) view).addView(viewLists.get(position), 0);
        return viewLists.get(position);
    }

}
