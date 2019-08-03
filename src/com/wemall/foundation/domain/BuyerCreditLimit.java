package com.wemall.foundation.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.wemall.core.domain.IdEntity;

/**
 * 买家 赊销额度信息
 * **/

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "wemall_buyerCreditLimit")
public class BuyerCreditLimit extends IdEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8983070168908075497L;
	
	
	private int storeId;// 店铺id
	@Lob
	private String storeName;// 店铺名称
	private int sellerId;// 卖家id
	@Lob
	private String sellerName;// 卖家名称
	private int buyerId;// 买家Id
	@Lob
	private String buyerName;// 买家名称
	@Lob
	private String buyerCombination;// 买家总额度
	@Lob
	private String buyerRemainingUndrawn;// 买家剩余额度
	
	private Date monthClean;//授权有效期 
	
	private Double interest;//计息
	
	private int zstatus;//1、未申请 2、审核中 3、审核成功 4、失败(驳回) 5、待线下核实 6、待上传合同 7、已关闭
	
	private String refReason;
	
	private Date startTime;//申请开始时间
	
	private int repaymentDate;//还款日
	
	private int apTime;
	
	private int limitState;//授额状态 1 进行中 2 已到期 3 已冻结 4待授额

	@Transient
	private String authString;
	
	@Transient
	private int  orderCount;
	
	private int accountDate;//出账日
	
	
	private String fileUrl;//文件路径
	
	
	
	
	public int getOrderCount() {
		return orderCount;
	}

	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}

	public String getFileUrl() {
		return fileUrl;
	}

	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	
	public int getLimitState() {
		return limitState;
	}

	public void setLimitState(int limitState) {
		this.limitState = limitState;
	}

	public int getAccountDate() {
		return accountDate;
	}

	public void setAccountDate(int accountDate) {
		this.accountDate = accountDate;
	}

	public String getAuthString() {
		return authString;
	}

	public void setAuthString(String authString) {
		this.authString = authString;
	}

	public int getStoreId() {
		return storeId;
	}

	public void setStoreId(int storeId) {
		this.storeId = storeId;
	}

	public String getStoreName() {
		return storeName;
	}

	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}

	public int getSellerId() {
		return sellerId;
	}

	public void setSellerId(int sellerId) {
		this.sellerId = sellerId;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public int getBuyerId() {
		return buyerId;
	}

	public void setBuyerId(int buyerId) {
		this.buyerId = buyerId;
	}

	public String getBuyerName() {
		return buyerName;
	}

	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}

	public String getBuyerCombination() {
		return buyerCombination;
	}

	public void setBuyerCombination(String buyerCombination) {
		this.buyerCombination = buyerCombination;
	}

	public String getBuyerRemainingUndrawn() {
		return buyerRemainingUndrawn;
	}

	public void setBuyerRemainingUndrawn(String buyerRemainingUndrawn) {
		this.buyerRemainingUndrawn = buyerRemainingUndrawn;
	}


	public Date getMonthClean() {
		return monthClean;
	}

	public void setMonthClean(Date monthClean) {
		this.monthClean = monthClean;
	}

	public Double getInterest() {
		return interest;
	}

	public void setInterest(Double interest) {
		this.interest = interest;
	}

	public int getZstatus() {
		return zstatus;
	}

	public void setZstatus(int zstatus) {
		this.zstatus = zstatus;
	}

	public String getRefReason() {
		return refReason;
	}

	public void setRefReason(String refReason) {
		this.refReason = refReason;
	}


	public int getApTime() {
		return apTime;
	}

	public void setApTime(int apTime) {
		this.apTime = apTime;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public int getRepaymentDate() {
		return repaymentDate;
	}

	public void setRepaymentDate(int repaymentDate) {
		this.repaymentDate = repaymentDate;
	}

	
}
