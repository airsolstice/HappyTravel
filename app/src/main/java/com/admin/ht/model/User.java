
package com.admin.ht.model;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

public class User implements Serializable{
	//用户id，在移动端使用手机号
	private String id;
	//用户名
	private String name;
	//密码
	private String pwd;
	//邮箱
	private String email;
	//0-保密，1-男，2-女
	private int sex;
	//手机号码
	private String phone;
	//激活状态 1-已激活 ，0-未激活
	private int status;
	//头像链接
	private String url;

	private String note;
	public User(){}

	//用户个人信息获取
	public User(String id, String name, String email, String url){
		this.id = id;
		this.name = name;
		this.email = email;
		this.url = url;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return JSONObject.toJSONString(this);
	}
}
