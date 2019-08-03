package com.wemall.foundation.domain;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.wemall.core.domain.IdEntity;

/**
 * 店铺  卖家   赊销额度信息
 * **/

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "wemall_creditllinemanagement")
public class CreditLlineManagement extends IdEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = -512027433461599452L;

	private int storeId;// 店铺id
	private String storeName;// 店铺名称
	private int sellerId;// 卖家id
	private String sellerName;// 卖家名称
	private String storeCredits;// 店铺总额度
	private String storeSurplus;// 店铺剩余额度
	private String mayWithdrawalAmount;//可提现额度
	private String yetWithdrawalAmount;//已提现完成额度

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

	public String getStoreCredits() {
		return storeCredits;
	}

	public void setStoreCredits(String storeCredits) {
		this.storeCredits = storeCredits;
	}

	public String getStoreSurplus() {
		return storeSurplus;
	}

	public void setStoreSurplus(String storeSurplus) {
		this.storeSurplus = storeSurplus;
	}

	public String getMayWithdrawalAmount() {
		return mayWithdrawalAmount;
	}

	public void setMayWithdrawalAmount(String mayWithdrawalAmount) {
		this.mayWithdrawalAmount = mayWithdrawalAmount;
	}

	public String getYetWithdrawalAmount() {
		return yetWithdrawalAmount;
	}

	public void setYetWithdrawalAmount(String yetWithdrawalAmount) {
		this.yetWithdrawalAmount = yetWithdrawalAmount;
	}

	@Override
	public String toString() {
		return "CreditLlineManagement [storeId=" + storeId + ", storeName="
				+ storeName + ", sellerId=" + sellerId + ", sellerName="
				+ sellerName + ", storeCredits=" + storeCredits
				+ ", storeSurplus=" + storeSurplus + "]";
	}

}
