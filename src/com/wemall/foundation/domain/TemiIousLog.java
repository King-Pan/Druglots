package com.wemall.foundation.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.wemall.core.domain.IdEntity;

//特米白条消费记录明细
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "wemall_TemiIousLog")
public class TemiIousLog extends IdEntity{
	
	// 用户名
	@Lob
	private String userName;
	//消费类型
	@Lob
	private String consumptionType;//消费，充值,还款，借款
	// 消费金额
	@Lob
	private String sumOfConsumption;	

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getConsumptionType() {
		return consumptionType;
	}

	public void setConsumptionType(String consumptionType) {
		this.consumptionType = consumptionType;
	}

	public String getSumOfConsumption() {
		return sumOfConsumption;
	}

	public void setSumOfConsumption(String sumOfConsumption) {
		this.sumOfConsumption = sumOfConsumption;
	}
 
 
	

}
