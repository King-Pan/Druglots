package com.wemall.foundation.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.wemall.core.domain.IdEntity;

/**
 * 采购关系
 * **/

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "wemall_ProcurementRelationship")
public class ProcurementRelationship extends IdEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3894820012907874704L;
     
	private String storeId;//店铺id    --store表id
	
	private String storeName;//店铺名称     --store表店铺名称
	
	private String shopkeeperId;//店主id   --user表ID
	
	private String shopkeeperName;//店主名称   --user表username
	 
	private String buyerId;//买家id     userId

	private String buyerName;//买家名称   --user表username
	
	private String buyerbusinessLicense;//买家营业执照号码
	
    private String buyerDrugLicense;//买家药品经营许可证
    
    private String buyerGSPCertificate;//买家gsp证书
    
    private String buyerPurchaseOrders;//买家采购委托书
    
    private String storeBusinessLicense;//店主营业执照号码
	
    private String storeDrugLicense;//店主药品经营许可证
    
    private String storeGSPCertificate;//店主gsp证书
    
    private String storePurchaseOrders;//店主采购委托书
	
	private Date auditAddTime;//审核时间
	private String zhcause;//审核不通过原因  
	private int auditStatus;//审核状态      0买家申请   审核中；1店主审核通过；2店主审核不通过； 
	
	private String invitationcodeid;//业务员id
	
 //与卖家 建立采购关系的店铺名	@Transient
	@Transient
	private String auth;
	
	@Transient
	private String buyamout;//赊销总额度
	
	
	@Transient
	private int accountDate;//出账日
	
	@Transient
	private int limitState;//授额状态
	
	
	@Transient
	private int repaymentDate;//还款日
	
	@Transient
	private double interest;//计息
	
	@Transient
	private Date startTime;//赊销开始时间
	
	@Transient
	private int orderCount;//订单数量
	
	@Transient
	private Date monthClean;//赊销结束时间
	
	@Transient
	private Long sxid;//赊销id
	
	
	
	public Long getSxid() {
		return sxid;
	}
	public void setSxid(Long sxid) {
		this.sxid = sxid;
	}
	public int getOrderCount() {
		return orderCount;
	}
	public void setOrderCount(int orderCount) {
		this.orderCount = orderCount;
	}
	public int getLimitState() {
		return limitState;
	}
	public void setLimitState(int limitState) {
		this.limitState = limitState;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public Date getMonthClean() {
		return monthClean;
	}
	public void setMonthClean(Date monthClean) {
		this.monthClean = monthClean;
	}
	public int getAccountDate() {
		return accountDate;
	}
	public void setAccountDate(int accountDate) {
		this.accountDate = accountDate;
	}
	public int getRepaymentDate() {
		return repaymentDate;
	}
	public void setRepaymentDate(int repaymentDate) {
		this.repaymentDate = repaymentDate;
	}
	public double getInterest() {
		return interest;
	}
	public void setInterest(double interest) {
		this.interest = interest;
	}
	public String getBuyamout() {
		return buyamout;
	}
	public void setBuyamout(String buyamout) {
		this.buyamout = buyamout;
	}
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public String getInvitationcodeid() {
		return invitationcodeid;
	}
	public void setInvitationcodeid(String invitationcodeid) {
		this.invitationcodeid = invitationcodeid;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	public String getStoreId() {
		return storeId;
	}
	public void setStoreId(String storeId) {
		this.storeId = storeId;
	}
	public String getBuyerId() {
		return buyerId;
	}
	public void setBuyerId(String buyerId) {
		this.buyerId = buyerId;
	}
	public Date getAuditAddTime() {
		return auditAddTime;
	}
	public void setAuditAddTime(Date auditAddTime) {
		this.auditAddTime = auditAddTime;
	}
	public int getAuditStatus() {
		return auditStatus;
	}
	public void setAuditStatus(int auditStatus) {
		this.auditStatus = auditStatus;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public String getBuyerbusinessLicense() {
		return buyerbusinessLicense;
	}
	public void setBuyerbusinessLicense(String buyerbusinessLicense) {
		this.buyerbusinessLicense = buyerbusinessLicense;
	}
	public String getBuyerDrugLicense() {
		return buyerDrugLicense;
	}
	public void setBuyerDrugLicense(String buyerDrugLicense) {
		this.buyerDrugLicense = buyerDrugLicense;
	}
	public String getBuyerGSPCertificate() {
		return buyerGSPCertificate;
	}
	public void setBuyerGSPCertificate(String buyerGSPCertificate) {
		this.buyerGSPCertificate = buyerGSPCertificate;
	}
	public String getBuyerPurchaseOrders() {
		return buyerPurchaseOrders;
	}
	public void setBuyerPurchaseOrders(String buyerPurchaseOrders) {
		this.buyerPurchaseOrders = buyerPurchaseOrders;
	}
	public String getStoreBusinessLicense() {
		return storeBusinessLicense;
	}
	public void setStoreBusinessLicense(String storeBusinessLicense) {
		this.storeBusinessLicense = storeBusinessLicense;
	}
	public String getStoreDrugLicense() {
		return storeDrugLicense;
	}
	public void setStoreDrugLicense(String storeDrugLicense) {
		this.storeDrugLicense = storeDrugLicense;
	}
	public String getStoreGSPCertificate() {
		return storeGSPCertificate;
	}
	public void setStoreGSPCertificate(String storeGSPCertificate) {
		this.storeGSPCertificate = storeGSPCertificate;
	}
	public String getStorePurchaseOrders() {
		return storePurchaseOrders;
	}
	public void setStorePurchaseOrders(String storePurchaseOrders) {
		this.storePurchaseOrders = storePurchaseOrders;
	}
	
	public String getShopkeeperName() {
		return shopkeeperName;
	}
	public void setShopkeeperName(String shopkeeperName) {
		this.shopkeeperName = shopkeeperName;
	}
	public String getBuyerName() {
		return buyerName;
	}
	public void setBuyerName(String buyerName) {
		this.buyerName = buyerName;
	}
	public String getShopkeeperId() {
		return shopkeeperId;
	}
	public void setShopkeeperId(String shopkeeperId) {
		this.shopkeeperId = shopkeeperId;
	}
	public String getZhcause() {
		return zhcause;
	}
	public void setZhcause(String zhcause) {
		this.zhcause = zhcause;
	}
	 
	 
}
