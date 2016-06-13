package com.oo58.livevideoassist.entity;

/**
 * @author zhongxf
 * @Description 排行榜的实体类
 * @Date 2016/5/13.
 */
public class RankListVo {

    private int sort;//排名
    private String name;//名字
    private String face;//头像图片
    private String money;//乐币

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFace() {
        return face;
    }

    public void setFace(String face) {
        this.face = face;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }
}
