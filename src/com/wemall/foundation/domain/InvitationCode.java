package com.wemall.foundation.domain;

import java.io.Serializable;
import java.util.Date;

import com.alibaba.fastjson.annotation.JSONField;

public class InvitationCode implements Serializable{
	private Integer id;
	private String sellername;//店家名字
	private String salesman;//业务员名字
	private String phone;//邀请码
	private double royalty;
	@JSONField(format = "yyyy-MM-dd")
	private Date addtime;//当前时间
	private Integer cumulative;//注册数
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSellername() {
		return sellername;
	}
	public void setSellername(String sellername) {
		this.sellername = sellername;
	}
	public String getSalesman() {
		return salesman;
	}
	public void setSalesman(String salesman) {
		this.salesman = salesman;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public Date getAddtime() {
		return addtime;
	}
	public void setAddtime(Date addtime) {
		this.addtime = addtime;
	}
	public Integer getCumulative() {
		return cumulative;
	}
	public void setCumulative(Integer cumulative) {
		this.cumulative = cumulative;
	}
	public double getRoyalty() {
		return royalty;
	}
	public void setRoyalty(double royalty) {
		this.royalty = royalty;
	}
	@Override
	public String toString() {
		return "InvitationCode [id=" + id + ", sellername=" + sellername + ", salesman=" + salesman + ", phone=" + phone
				+ ", royalty=" + royalty + ", addtime=" + addtime + ", cumulative=" + cumulative + "]";
	}
	
	
}
