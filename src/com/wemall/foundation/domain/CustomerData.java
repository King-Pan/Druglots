package com.wemall.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.wemall.core.domain.IdEntity;

/**
 * 客户   药房资料
 * **/

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "wemall_CustomerData")
public class CustomerData extends IdEntity{

	 /**
     * UID
     */ 
	private static final long serialVersionUID = -6903807203164314195L;
	
	//用户名称
	@Lob
	private String userName;
	
	//客户名称
	@Lob
	private String customerName;
	
	//药房名称
	@Lob
	private String pharmacyName;
	
	//药房地址
	@Lob
	private String pharmacyAddress;
	
	//营业执照
	@Lob
	private String business;
	
	//药品经营许可证
	@Lob
	private String DrugDistributionLicense;
	
	//GSP证
	@Lob
	private String GSPCertificate;
	
	//采购委托书
	@Lob
	private String PurchaseOrder;
	
	//身份证号码
	@Lob
	private String IdCard;
	
	@Transient
	private String auth;
	 
	public String getAuth() {
		return auth;
	}
	public void setAuth(String auth) {
		this.auth = auth;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	public String getPharmacyName() {
		return pharmacyName;
	}
	public void setPharmacyName(String pharmacyName) {
		this.pharmacyName = pharmacyName;
	}
	public String getPharmacyAddress() {
		return pharmacyAddress;
	}
	public void setPharmacyAddress(String pharmacyAddress) {
		this.pharmacyAddress = pharmacyAddress;
	}
	public String getBusiness() {
		return business;
	}
	public void setBusiness(String business) {
		this.business = business;
	}
	public String getDrugDistributionLicense() {
		return DrugDistributionLicense;
	}
	public void setDrugDistributionLicense(String drugDistributionLicense) {
		DrugDistributionLicense = drugDistributionLicense;
	}
	public String getGSPCertificate() {
		return GSPCertificate;
	}
	public void setGSPCertificate(String gSPCertificate) {
		GSPCertificate = gSPCertificate;
	}
	public String getPurchaseOrder() {
		return PurchaseOrder;
	}
	public void setPurchaseOrder(String purchaseOrder) {
		PurchaseOrder = purchaseOrder;
	}
	public String getIdCard() {
		return IdCard;
	}
	public void setIdCard(String idCard) {
		IdCard = idCard;
	}
	
	

}
