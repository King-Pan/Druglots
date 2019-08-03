package com.wemall.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.wemall.core.domain.IdEntity;

/*
 * 用来封装数据为json来交给接口查询用
 * */

public class Invoice {
	
	private int id;//id
	private String  name ;//发票提交人
	private String company  ;//公司名称
	private String comaddress  ;//公司地址
	private String  comphone ;//公司号码
	private String  taxnumber ;//纳税人号码
	private String  bank  ;//开户银行
	private String  bankaccount  ;//银行账户
	private String rise;//发票抬头
	private int mark;//标识，0为个人，1为企业
	public String getRise() {
		return rise;
	}
	public void setRise(String rise) {
		this.rise = rise;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getComaddress() {
		return comaddress;
	}
	public void setComaddress(String comaddress) {
		this.comaddress = comaddress;
	}
	public String getComphone() {
		return comphone;
	}
	public void setComphone(String comphone) {
		this.comphone = comphone;
	}
	public String getTaxnumber() {
		return taxnumber;
	}
	public void setTaxnumber(String taxnumber) {
		this.taxnumber = taxnumber;
	}
	public String getBank() {
		return bank;
	}
	public void setBank(String bank) {
		this.bank = bank;
	}
	public String getBankaccount() {
		return bankaccount;
	}
	public void setBankaccount(String bankaccount) {
		this.bankaccount = bankaccount;
	}
	public int getMark() {
		return mark;
	}
	public void setMark(int mark) {
		this.mark = mark;
	}
	@Override
	public String toString() {
		return "Invoice [id=" + id + ", name=" + name + ", company=" + company + ", comaddress=" + comaddress
				+ ", comphone=" + comphone + ", taxnumber=" + taxnumber + ", bank=" + bank + ", bankaccount="
				+ bankaccount + ", rise=" + rise + ", mark=" + mark + "]";
	}

	

}
