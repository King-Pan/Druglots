package com.wemall.foundation.domain;
 

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.wemall.core.domain.IdEntity; 


/**   认证信息*/
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "wemall_Authentication")
public class Authentication  extends IdEntity {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = -6659991558380376145L;
	
	//状态：0 未发短信  1 已发短信 默认为0   
	private int note;
	//用户id
    private String userId;
	//用户mz
    private String userName;
    //认证状态 默认为1审核中，2审核不通，3审核通过
    private int certifiedStatus = 1;//作废的认证状态
    //营业执照
    private String businessLicense;
    //营业执照号码
    private String businessNumber;
    //药品经营许可证
    private String drugLicense;
    //药品经营许可证号码
    private String drugNumber;
    //gsp证书
    private String gSPCertificate;
    //gsp证书号码
    private String gSPNumber;
    //手持身份证
    private String purchaseOrders;
    //采购委托书号码
    private String purchaseNumber;
    //身份证复印件正反面
    private String iDcard;
    //身份证号码
    private String iDcardNumber;
    //身份证复印件反面
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
    //首营资料审核状态  1代表审核中，2代表审核成功，3代表审核失败
    private int examine = 0;
  //特米白条开通状态 1未开通 0审核中2审核通过3审核失败
   private int  temiiOpeningStatus=0;
   //特米白条申请时间
   private String newtemiiOpeningDate;
   //企业名称
   private String enterpriseName;
   //营业执照注册号
   private String businesRegsNumber;
   //所在地区
   private String areaId;
   //详细街道
   private String detailedStreet;
   
   //所在地区id
   private String  address;
   
   //真实姓名
   private String trueName;
   
   

 public String getTrueName() {
	return trueName;
}

public void setTrueName(String trueName) {
	this.trueName = trueName;
}

public String getAddress() {
	return address;
 }

public void setAddress(String address) {
	this.address = address;
}

public String getEnterpriseName() {
	return enterpriseName;
}

public void setEnterpriseName(String enterpriseName) {
	this.enterpriseName = enterpriseName;
}

public String getBusinesRegsNumber() {
	return businesRegsNumber;
}

public void setBusinesRegsNumber(String businesRegsNumber) {
	this.businesRegsNumber = businesRegsNumber;
}





public String getUserId() {
	return userId;
}

public void setUserId(String userId) {
	this.userId = userId;
}

public String getAreaId() {
	return areaId;
}

public void setAreaId(String areaId) {
	this.areaId = areaId;
}

public String getDetailedStreet() {
	return detailedStreet;
}

public void setDetailedStreet(String detailedStreet) {
	this.detailedStreet = detailedStreet;
}

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

    public String getBusinessLicense() {
        return businessLicense;
    }

    public void setBusinessLicense(String businessLicense) {
        this.businessLicense = businessLicense;
    }

    public String getBusinessNumber() {
        return businessNumber;
    }

    public void setBusinessNumber(String businessNumber) {
        this.businessNumber = businessNumber;
    }

    public String getDrugLicense() {
        return drugLicense;
    }

    public void setDrugLicense(String drugLicense) {
        this.drugLicense = drugLicense;
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

    public String getPurchaseOrders() {
        return purchaseOrders;
    }

    public void setPurchaseOrders(String purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }

    public String getPurchaseNumber() {
        return purchaseNumber;
    }

    public void setPurchaseNumber(String purchaseNumber) {
        this.purchaseNumber = purchaseNumber;
    }

    public String getiDcard() {
        return iDcard;
    }

    public void setiDcard(String iDcard) {
        this.iDcard = iDcard;
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

   

    public int getExamine() {
		return examine;
	}

	public void setExamine(int examine) {
		this.examine = examine;
	}

	

	public int getTemiiOpeningStatus() {
		return temiiOpeningStatus;
	}

	public void setTemiiOpeningStatus(int temiiOpeningStatus) {
		this.temiiOpeningStatus = temiiOpeningStatus;
	}

	public String getNewtemiiOpeningDate() {
		return newtemiiOpeningDate;
	}

	public void setNewtemiiOpeningDate(String newtemiiOpeningDate) {
		this.newtemiiOpeningDate = newtemiiOpeningDate;
	}
	
	public int getNote() {
		return note;
	}

	public void setNote(int note) {
		this.note = note;
	}

	@Override
	public String toString() {
		return "Authentication [userName=" + userName + ", certifiedStatus=" + certifiedStatus + ", businessLicense="
				+ businessLicense + ", businessNumber=" + businessNumber + ", drugLicense=" + drugLicense
				+ ", drugNumber=" + drugNumber + ", gSPCertificate=" + gSPCertificate + ", gSPNumber=" + gSPNumber
				+ ", purchaseOrders=" + purchaseOrders + ", purchaseNumber=" + purchaseNumber + ", iDcard=" + iDcard
				+ ", iDcardNumber=" + iDcardNumber + ", handIDcard=" + handIDcard + ", endIDcardDate=" + endIDcardDate
				+ ", endBusinessDate=" + endBusinessDate + ", endDrugDate=" + endDrugDate + ", endGSPDate=" + endGSPDate
				+ ", endPurchaseDate=" + endPurchaseDate + ", newAuthenticationDate=" + newAuthenticationDate
				+ ", dutyParagraph=" + dutyParagraph + ", publicAccount=" + publicAccount + ", features=" + features
				+ ", examine=" + examine + ", temiiOpeningStatus=" + temiiOpeningStatus + ", newtemiiOpeningDate="
				+ newtemiiOpeningDate + ", enterpriseName=" + enterpriseName + ", businesRegsNumber="
				+ businesRegsNumber + ", areaId=" + areaId + ", detailedStreet=" + detailedStreet + "]";
	}


	
}