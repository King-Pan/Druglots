package com.wemall.foundation.service;

import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.RecordCreditLog;

import java.util.List;
import java.util.Map;

public abstract interface RecordCreditLogService {
    public abstract boolean save(RecordCreditLog paramAccessory);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(RecordCreditLog paramAccessory);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract RecordCreditLog getObjById(Long paramLong);

    public abstract RecordCreditLog getObjByProperty(String paramString1, String paramString2);

    public abstract List<RecordCreditLog> query(String paramString, Map paramMap, int paramInt1, int paramInt2);
}




