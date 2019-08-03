package com.wemall.foundation.service;

import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.RelatedAccount;

import java.util.List;
import java.util.Map;

public abstract interface RelatedAccountService {
    public abstract boolean save(RelatedAccount paramAccessory);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(RelatedAccount paramAccessory);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract RelatedAccount getObjById(Long paramLong);

    public abstract RelatedAccount getObjByProperty(String paramString1, String paramString2);

    public abstract List<RelatedAccount> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




