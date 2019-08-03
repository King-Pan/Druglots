package com.wemall.foundation.service;

import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.TemiIousLog;

import java.util.List;
import java.util.Map;

public abstract interface TemiIousLogService {
    public abstract boolean save(TemiIousLog paramAccessory);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(TemiIousLog paramAccessory);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract TemiIousLog getObjById(Long paramLong);

    public abstract TemiIousLog getObjByProperty(String paramString1, String paramString2);

    public abstract List<TemiIousLog> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




