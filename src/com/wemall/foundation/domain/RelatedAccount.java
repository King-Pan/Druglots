package com.wemall.foundation.domain;
 
import java.io.Serializable;

import javax.persistence.Entity; 
import javax.persistence.Table; 
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
 
import com.wemall.core.annotation.Lock;
import com.wemall.core.domain.IdEntity;
/**
 * 关联客户经理
 * @author
 *
 */
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
@Table(name = "wemall_RelatedAccount")
public class RelatedAccount extends IdEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = -644132260096239866L;
   
	//用户
	@Lock
	private String userID;
	//客户经理ID
	@Lock
	private String customerManager;
	
	
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getCustomerManager() {
		return customerManager;
	}
	public void setCustomerManager(String customerManager) {
		this.customerManager = customerManager;
	}
	
    
}
