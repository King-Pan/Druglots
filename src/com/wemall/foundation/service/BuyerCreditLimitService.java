package com.wemall.foundation.service;

import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.BuyerCreditLimit;

import java.util.List;
import java.util.Map;

public abstract interface BuyerCreditLimitService {
    public abstract boolean save(BuyerCreditLimit paramAccessory);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(BuyerCreditLimit paramAccessory);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract BuyerCreditLimit getObjById(Long paramLong);

    public abstract BuyerCreditLimit getObjByProperty(String paramString1, String paramString2);

    public abstract List<BuyerCreditLimit> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




