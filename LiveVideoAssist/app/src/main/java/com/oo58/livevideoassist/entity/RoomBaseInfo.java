package com.oo58.livevideoassist.entity;

/**
 * Desc: 记录房间的基本数据
 * Created by sunjinfang on 2016/5/13 14:33.
 */
public class RoomBaseInfo {

    public String name ;//房间名称
    public String id ;//房间id
    public String timestamp ;//时间戳

    public String openid;
    public int is_guard;
    public String openkey;
    public int is_follow;
    public String anchor_name;
    public String anchor_id;
    public String anchor_received_level;
    public String anchor_icon;
    public String anchor_head_icon;
    public String status;
    public AnchorInfo anchor_current;
    public String mMommonUrl;
    public int mMommonPort;
    public int maxOnline; //在线人数

    public String chat_url;
    public int port;
    public String live_url;
    public String stream;

    public int flag;//视频源  0 web/1 android


}
