package com.wemall.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.wemall.core.domain.IdEntity;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "wemall_iou")
public class WhiteCertification extends IdEntity{
	private static final long serialVersionUID = -6659991558380376145L;
	
	//用户mz
    private String userName;
    //认证状态 默认为1审核中，2审核不通，3审核通过
    private int certifiedStatus = 1;
    //营业执照号码
    private String certificateNumber;
    //营业执照
    private String certificate;
    //药品经营许可证
    private String license;
    //药品经营许可证号码
    private String drugNumber;
    //gsp证书
    private String gSPCertificate;
    //gsp证书号码
    private String gSPNumber;
    //采购委托书
    private String letterOfInstruction;
    //采购委托书号码
    private String purchaseNumber;
    //身份证复印件正反面
    private String copyOfIdCard;
    //身份证号码
    private String iDcardNumber;
    //手持身份证
    private String handIDcard;
    //身份证有效期 过期时
    private String endIDcardDate;
    //营业执照有效期结束
    private String endBusinessDate;
    //药品经营许可证有效期结束
    private String endDrugDate;
    //gsp证书证书有效期结束
    private String endGSPDate;
    //采购委托书有效期结束
    private String endPurchaseDate;
    //认证时间
    private String newAuthenticationDate;
    //税号
    private String dutyParagraph;
    //对公账号
    private String publicAccount;
    //买家1卖家2
    private  int features;
    //特米白条开通状态 0未开通 1已开通
    private int temiiOpeningStatus=0;
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getCertifiedStatus() {
		return certifiedStatus;
	}
	public void setCertifiedStatus(int certifiedStatus) {
		this.certifiedStatus = certifiedStatus;
	}
	public String getCertificateNumber() {
		return certificateNumber;
	}
	public void setCertificateNumber(String certificateNumber) {
		this.certificateNumber = certificateNumber;
	}
	public String getCertificate() {
		return certificate;
	}
	public void setCertificate(String certificate) {
		this.certificate = certificate;
	}
	public String getLicense() {
		return license;
	}
	public void setLicense(String license) {
		this.license = license;
	}
	public String getDrugNumber() {
		return drugNumber;
	}
	public void setDrugNumber(String drugNumber) {
		this.drugNumber = drugNumber;
	}
	public String getgSPCertificate() {
		return gSPCertificate;
	}
	public void setgSPCertificate(String gSPCertificate) {
		this.gSPCertificate = gSPCertificate;
	}
	public String getgSPNumber() {
		return gSPNumber;
	}
	public void setgSPNumber(String gSPNumber) {
		this.gSPNumber = gSPNumber;
	}
	public String getLetterOfInstruction() {
		return letterOfInstruction;
	}
	public void setLetterOfInstruction(String letterOfInstruction) {
		this.letterOfInstruction = letterOfInstruction;
	}
	public String getPurchaseNumber() {
		return purchaseNumber;
	}
	public void setPurchaseNumber(String purchaseNumber) {
		this.purchaseNumber = purchaseNumber;
	}
	public String getCopyOfIdCard() {
		return copyOfIdCard;
	}
	public void setCopyOfIdCard(String copyOfIdCard) {
		this.copyOfIdCard = copyOfIdCard;
	}
	public String getiDcardNumber() {
		return iDcardNumber;
	}
	public void setiDcardNumber(String iDcardNumber) {
		this.iDcardNumber = iDcardNumber;
	}
	public String getHandIDcard() {
		return handIDcard;
	}
	public void setHandIDcard(String handIDcard) {
		this.handIDcard = handIDcard;
	}
	public String getEndIDcardDate() {
		return endIDcardDate;
	}
	public void setEndIDcardDate(String endIDcardDate) {
		this.endIDcardDate = endIDcardDate;
	}
	public String getEndBusinessDate() {
		return endBusinessDate;
	}
	public void setEndBusinessDate(String endBusinessDate) {
		this.endBusinessDate = endBusinessDate;
	}
	public String getEndDrugDate() {
		return endDrugDate;
	}
	public void setEndDrugDate(String endDrugDate) {
		this.endDrugDate = endDrugDate;
	}
	public String getEndGSPDate() {
		return endGSPDate;
	}
	public void setEndGSPDate(String endGSPDate) {
		this.endGSPDate = endGSPDate;
	}
	public String getEndPurchaseDate() {
		return endPurchaseDate;
	}
	public void setEndPurchaseDate(String endPurchaseDate) {
		this.endPurchaseDate = endPurchaseDate;
	}
	public String getNewAuthenticationDate() {
		return newAuthenticationDate;
	}
	public void setNewAuthenticationDate(String newAuthenticationDate) {
		this.newAuthenticationDate = newAuthenticationDate;
	}
	public String getDutyParagraph() {
		return dutyParagraph;
	}
	public void setDutyParagraph(String dutyParagraph) {
		this.dutyParagraph = dutyParagraph;
	}
	public String getPublicAccount() {
		return publicAccount;
	}
	public void setPublicAccount(String publicAccount) {
		this.publicAccount = publicAccount;
	}
	public int getFeatures() {
		return features;
	}
	public void setFeatures(int features) {
		this.features = features;
	}
	public int getTemiiOpeningStatus() {
		return temiiOpeningStatus;
	}
	public void setTemiiOpeningStatus(int temiiOpeningStatus) {
		this.temiiOpeningStatus = temiiOpeningStatus;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	@Override
	public String toString() {
		return "WhiteCertification [userName=" + userName + ", certifiedStatus=" + certifiedStatus
				+ ", certificateNumber=" + certificateNumber + ", certificate=" + certificate + ", license=" + license
				+ ", drugNumber=" + drugNumber + ", gSPCertificate=" + gSPCertificate + ", gSPNumber=" + gSPNumber
				+ ", letterOfInstruction=" + letterOfInstruction + ", purchaseNumber=" + purchaseNumber
				+ ", copyOfIdCard=" + copyOfIdCard + ", iDcardNumber=" + iDcardNumber + ", handIDcard=" + handIDcard
				+ ", endIDcardDate=" + endIDcardDate + ", endBusinessDate=" + endBusinessDate + ", endDrugDate="
				+ endDrugDate + ", endGSPDate=" + endGSPDate + ", endPurchaseDate=" + endPurchaseDate
				+ ", newAuthenticationDate=" + newAuthenticationDate + ", dutyParagraph=" + dutyParagraph
				+ ", publicAccount=" + publicAccount + ", features=" + features + ", temiiOpeningStatus="
				+ temiiOpeningStatus + "]";
	}
    
}
