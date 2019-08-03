package com.wemall.foundation.service;

import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.IOU;

import java.util.List;
import java.util.Map;

public abstract interface IOUService {
    public abstract boolean save(IOU paramAccessory);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(IOU paramAccessory);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract IOU getObjById(Long paramLong);

    public abstract IOU getObjByProperty(String paramString1, String paramString2);

    public abstract List<IOU> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




