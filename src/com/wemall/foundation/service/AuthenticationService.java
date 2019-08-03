package com.wemall.foundation.service;

import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.Authentication;

import java.util.List;
import java.util.Map;

public abstract interface AuthenticationService {
    public abstract boolean save(Authentication paramAccessory);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(Authentication paramAccessory);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract Authentication getObjById(Long paramLong);

    public abstract Authentication getObjByProperty(String paramString1, String paramString2);

    public abstract List<Authentication> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
 
}




