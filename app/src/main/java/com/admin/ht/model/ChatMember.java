package com.admin.ht.model;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class ChatMember implements Serializable{
	
	private int groupId;
	
	private String memberId;
	
	private int role;
	
	private String groupName;

	public int getGroupId() {
		return groupId;
	}

	public void setGroupId(int groupId) {
		this.groupId = groupId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}
	
	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}
