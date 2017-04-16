package com.admin.ht.model;

import java.util.Date;

import com.alibaba.fastjson.JSONObject;

public class Position {

	private int no;
	
	private String id;
	
	private Double lat;
	
	private Double lng;
	
	private Date time;

	public int getNo() {
		return no;
	}

	public void setNo(int no) {
		this.no = no;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLng() {
		return lng;
	}

	public void setLng(Double lng) {
		this.lng = lng;
	}

	public Date getTime() {
		return time;
	}

	public void setTime(Date time) {
		this.time = time;
	}
	
	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}
