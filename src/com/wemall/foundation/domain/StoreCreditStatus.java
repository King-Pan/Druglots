package com.wemall.foundation.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.wemall.core.domain.IdEntity;

/**
 * 店铺赊销开启状态
 * **/

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "wemall_StoreCreditStatus")
public class StoreCreditStatus extends IdEntity{
 
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7510168827843285307L;
	
	private String storeId;//店铺ID
	private String userName;//店主用户名称
	private	String state;//  店铺的赊销开启状态    0不开启，1开启

	public String getStoreId() {
		return storeId;
	}

	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
	
	 
	

}
