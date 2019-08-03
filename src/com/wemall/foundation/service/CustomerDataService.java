package com.wemall.foundation.service;

import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.CustomerData; 

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.record.formula.functions.T;

public abstract interface CustomerDataService {
    public abstract boolean save(CustomerData paramAccessory);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(CustomerData paramAccessory);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract CustomerData getObjById(Long paramLong);

    public abstract CustomerData getObjByProperty(String paramString1, String paramString2);

    public abstract List<CustomerData> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

}




