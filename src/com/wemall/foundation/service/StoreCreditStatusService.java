package com.wemall.foundation.service;

import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.CustomerData; 
import com.wemall.foundation.domain.StoreCreditStatus;

import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.record.formula.functions.T;

public abstract interface StoreCreditStatusService {
    public abstract boolean save(StoreCreditStatus paramAccessory);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(StoreCreditStatus paramAccessory);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract StoreCreditStatus getObjById(Long paramLong);

    public abstract StoreCreditStatus getObjByProperty(String paramString1, String paramString2);

    public abstract List<StoreCreditStatus> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

}




