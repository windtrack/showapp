package com.oo58.livevideoassist.rpc;

/**
 * Desc: rpc协议配置
 * RpcEvent 最后一个是 分号 其他的 逗号隔开
 * Created by sunjinfang on 2016/5/10.
 */
public enum RpcEvent {
    CallUserLogin("m_user_login"), //登录
    CallUpdateRoomNotice("m_set_notice"),//修改房间公告
    GetSplashUrl("m_open_config"),//获取启动页图片
    GetRoomGiftRankList("m_room_rankgift"),//获取礼物排名  day,week,month)=>(当天,本周,本月)
    GetRoomRankList("m_room_fans_rank"),//当前房间 本日  本周 本月的粉丝排行版
    GetRoomInfo("m_room_info"),//房间信息
    GetUrlInfo("m_room_resurl"),//房间直播地址信息
    GetResultInfoByEnd("m_room_over"),//直播结束后的新增乐豆和观众人数
    CallStartLiving("m_room_start_live"),//开始直播
    CallFinishLiving("m_room_end_live"),//结束直播
    GetAnchorBeans("m_get_anchor_beans"),//查询乐豆
    CallUpdateAnchorTime("m_room_update_endtime"),//更新主播麦时
    CheckUpdateVersion("m_software_update"),//版本更新检测
    ;

    public String name;
    private RpcEvent(String name) {
        this.name = name;
    }
}


