package com.wemall.foundation.service;

import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.CreditLlineManagement;

import java.util.List;
import java.util.Map;

public abstract interface CreditLlineManagementService {
    public abstract boolean save(CreditLlineManagement paramAccessory);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(CreditLlineManagement paramAccessory);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract CreditLlineManagement getObjById(Long paramLong);

    public abstract CreditLlineManagement getObjByProperty(String paramString1, String paramString2);

    public abstract List<CreditLlineManagement> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

}




