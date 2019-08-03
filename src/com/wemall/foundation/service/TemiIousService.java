package com.wemall.foundation.service;

import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.TemiIous;

import java.util.List;
import java.util.Map;

public abstract interface TemiIousService {
    public abstract boolean save(TemiIous paramAccessory);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(TemiIous paramAccessory);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract TemiIous getObjById(Long paramLong);

    public abstract TemiIous getObjByProperty(String paramString1, String paramString2);

    public abstract List<TemiIous> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




