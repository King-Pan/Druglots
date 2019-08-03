package com.wemall.foundation.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.wemall.core.domain.IdEntity;

/**
 * 赊销消费记录
 * **/

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "wemall_RecordCreditLog")
public class RecordCreditLog extends IdEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8951610724925825464L;

	@Lob
	private String userId;// 用户Id
	@Lob
	private String userName;// 用户名称
	@Lob
	private String consumptionType;//消费类型：提现，还款，消费
	@Lob
	private String consumptionQuota;// 额度
	@Lob
	private String ordernumber;// 订单号
	@Lob
	private String usertype;// 用户类型 0买家；1卖家

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getOrdernumber() {
		return ordernumber;
	}

	public void setOrdernumber(String ordernumber) {
		this.ordernumber = ordernumber;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getConsumptionQuota() {
		return consumptionQuota;
	}

	public void setConsumptionQuota(String consumptionQuota) {
		this.consumptionQuota = consumptionQuota;
	}

	public String getUsertype() {
		return usertype;
	}

	public void setUsertype(String usertype) {
		this.usertype = usertype;
	}

	public String getConsumptionType() {
		return consumptionType;
	}

	public void setConsumptionType(String consumptionType) {
		this.consumptionType = consumptionType;
	}

}
