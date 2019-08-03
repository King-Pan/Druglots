package com.wemall.foundation.service;

import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.ProcurementRelationship;

import java.util.List;
import java.util.Map;


public abstract interface ProcurementRelationshipService {
    public abstract boolean save(ProcurementRelationship paramAccessory);

    public abstract boolean delete(Long paramLong);

    public abstract boolean update(ProcurementRelationship paramAccessory);

    public abstract IPageList list(IQueryObject paramIQueryObject);

    public abstract ProcurementRelationship getObjById(Long paramLong);

    public abstract ProcurementRelationship getObjByProperty(String paramString1, String paramString2);

    public abstract List<ProcurementRelationship> query(String paramString, Map paramMap, int paramInt1, int paramInt2);

}




