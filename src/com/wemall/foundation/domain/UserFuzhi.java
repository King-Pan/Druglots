package com.wemall.foundation.domain;

import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class UserFuzhi {
	
	private  int id;//用户名id
	//用户名
    private String userName;
    //电话
    private String telephone;
    //地址
    private String address;
    //注册时间
    @JSONField(format = "yyyy-MM-dd")
    private Date  addTime;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public Date getAddTime() { 
		return addTime;
	}
	public void setAddTime(Date addTime) {
		this.addTime = addTime;
	}
	@Override
	public String toString() {
		return "User [id=" + id + ", userName=" + userName + ", telephone=" + telephone + ", address=" + address
				+ ", addTime=" + addTime + "]";
	}
	
    

}
