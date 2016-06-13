package com.oo58.livevideoassist.entity;

/**
 * Desc: 主播个人消息基础类
 * Created by sunjinfang on 2016/5/17 .
 */
public class AnchorInfo {
	private String id;
	private String poster_url;
	private String poster_url2;
	private String end_time;
	private String status;
	private String nickname;
	private String anchor_id;
	private String maxonline;
	private String start_time;
	private String icon;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPoster_url() {
		return poster_url;
	}
	public void setPoster_url(String poster_url) {
		this.poster_url = poster_url;
	}
	public String getPoster_url2() {
		return poster_url2;
	}
	public void setPoster_url2(String poster_url2) {
		this.poster_url2 = poster_url2;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getAnchor_id() {
		return anchor_id;
	}
	public void setAnchor_id(String anchor_id) {
		this.anchor_id = anchor_id;
	}
	public String getMaxonline() {
		return maxonline;
	}
	public void setMaxonline(String maxonline) {
		this.maxonline = maxonline;
	}
	public String getStart_time() {
		return start_time;
	}
	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}
	public String getIcon() {
		return icon;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public AnchorInfo(String id, String poster_url, String poster_url2,
			String end_time, String status, String nickname, String anchor_id,
			String maxonline, String start_time, String icon) {
		super();
		this.id = id;
		this.poster_url = poster_url;
		this.poster_url2 = poster_url2;
		this.end_time = end_time;
		this.status = status;
		this.nickname = nickname;
		this.anchor_id = anchor_id;
		this.maxonline = maxonline;
		this.start_time = start_time;
		this.icon = icon;
	}
	public AnchorInfo() {
		super();
	}
	@Override
	public String toString() {
		return "AnchorInfo [id=" + id + ", poster_url=" + poster_url
				+ ", poster_url2=" + poster_url2 + ", end_time=" + end_time
				+ ", status=" + status + ", nickname=" + nickname
				+ ", anchor_id=" + anchor_id + ", maxonline=" + maxonline
				+ ", start_time=" + start_time + ", icon=" + icon + "]";
	}
	
	@Override
	public boolean equals(Object obj) {
		  if (obj == null) {  
	            return false;  
	        }  
	        if (this == obj) {  
	            return true;  
	        }  
		AnchorInfo anchor = (AnchorInfo) obj;
		return this.id.equals(anchor.id);
	}
	
	
}
