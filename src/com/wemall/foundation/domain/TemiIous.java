package com.wemall.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.wemall.core.domain.IdEntity;

//  特米白条用户余额
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "wemall_TemiIous")
public class TemiIous extends IdEntity {

	// 用户名
	@Lob
	private String userName;
	// 余额
	@Lob
	private String balance;
	// 特米用户状态
	@Lob
	private String state;//0再用，1废弃

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getBalance() {
		return balance;
	}

	public void setBalance(String balance) {
		this.balance = balance;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	 

	 
 

}
