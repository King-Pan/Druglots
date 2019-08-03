package com.wemall.foundation.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Transient;

import com.alibaba.fastjson.annotation.JSONField;

public class InvoiceRecord implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id;
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	private Date submitdate;//提交日期
	private String buyname ;//买家账户名
	private String sellername ;//卖家账户名
	private String storename ;//卖家店铺名
	private String storeid ;//订单编号
	private String invamount ;//发票金额
	private String invoicetype ;//发票类型
	private int invoiceid ;//发票id
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	@Transient
	private String auth;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Date getSubmitdate() {
		return submitdate;
	}
	public void setSubmitdate(Date submitdate) {
		this.submitdate = submitdate;
	}
	public String getBuyname() {
		return buyname;
	}
	public void setBuyname(String buyname) {
		this.buyname = buyname;
	}
	public String getSellername() {
		return sellername;
	}
	public void setSellername(String sellername) {
		this.sellername = sellername;
	}
	public String getStorename() {
		return storename;
	}
	public void setStorename(String storename) {
		this.storename = storename;
	}
	public String getStoreid() {
		return storeid;
	}
	public void setStoreid(String storeid) {
		this.storeid = storeid;
	}
	public String getInvamount() {
		return invamount;
	}
	public void setInvamount(String invamount) {
		this.invamount = invamount;
	}
	public String getInvoicetype() {
		return invoicetype;
	}
	public void setInvoicetype(String invoicetype) {
		this.invoicetype = invoicetype;
	}
	public int getInvoiceid() {
		return invoiceid;
	}
	public void setInvoiceid(int invoiceid) {
		this.invoiceid = invoiceid;
	}
	@Override
	public String toString() {
		return "InvoiceRecord [id=" + id + ", submitdate=" + submitdate + ", buyname=" + buyname + ", sellername="
				+ sellername + ", storename=" + storename + ", storeid=" + storeid + ", invamount=" + invamount
				+ ", invoicetype=" + invoicetype + ", invoiceid=" + invoiceid + "]";
	}
	
	
}
