package com.wemall.foundation.domain;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import com.wemall.core.domain.IdEntity;

//   特米白条认证信息
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "wemall_IOU")
public class IOU extends IdEntity{

    /**
     * UID
     */
    private static final long serialVersionUID = -6716674058148213758L;
	 
	//用户名
	@Lob
    private String userName;
    //特米白条开通状态  0未开通，1已开通
    private int temiiOpeningStatus;
    
	@Lob
    private String certificate;//营业执照
	@Lob
    private String license;//药品经营许可证
	@Lob
    private String GSPCertificate;//GSP证书
	@Lob
    private String letterOfInstruction;//采购委托书
	@Lob
    private String CopyOfIdCard;//法人身份证复印件正反面
	private String addtime; //添加时间
    
 
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public int getTemiiOpeningStatus() {
		return temiiOpeningStatus;
	}
	public void setTemiiOpeningStatus(int temiiOpeningStatus) {
		this.temiiOpeningStatus = temiiOpeningStatus;
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
	public String getGSPCertificate() {
		return GSPCertificate;
	}
	public void setGSPCertificate(String gSPCertificate) {
		GSPCertificate = gSPCertificate;
	}
	public String getLetterOfInstruction() {
		return letterOfInstruction;
	}
	public void setLetterOfInstruction(String letterOfInstruction) {
		this.letterOfInstruction = letterOfInstruction;
	}
	public String getCopyOfIdCard() {
		return CopyOfIdCard;
	}
	public void setCopyOfIdCard(String copyOfIdCard) {
		CopyOfIdCard = copyOfIdCard;
	}
	public String getAddtime() {
		return addtime;
	}
	public void setAddtime(String addtime) {
		this.addtime = addtime;
	}
    
    

}
