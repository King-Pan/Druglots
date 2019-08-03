package com.wemall.foundation.service.impl;

import com.wemall.core.dao.IGenericDAO;
import com.wemall.core.query.GenericPageList;
import com.wemall.core.query.PageObject;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.CreditLlineManagement;
import com.wemall.foundation.domain.RecordCreditLog;
import com.wemall.foundation.service.CreditLlineManagementService;
import com.wemall.foundation.service.RecordCreditLogService;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RecordCreditLogServiceimpl
    implements RecordCreditLogService {
    @Resource(name = "RecordCreditLogDAO")
    private IGenericDAO<RecordCreditLog> recordCreditLogDAO;

    public boolean delete(Long id){
        try {
            this.recordCreditLogDAO.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public RecordCreditLog getObjById(Long id){
        return (RecordCreditLog)this.recordCreditLogDAO.get(id);
    }

    public boolean save(RecordCreditLog acc){
        try {
            this.recordCreditLogDAO.save(acc);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public boolean update(RecordCreditLog acc){
        try {
            this.recordCreditLogDAO.update(acc);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public List<RecordCreditLog> query(String query, Map params, int begin, int max){
        return this.recordCreditLogDAO.query(query, params, begin, max);
    }

    public IPageList list(IQueryObject properties){
        if (properties == null){
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(RecordCreditLog.class, query,
                params, this.recordCreditLogDAO);
        if (properties != null){
            PageObject pageObj = properties.getPageObj();
            if (pageObj != null)
                pList.doList(pageObj.getCurrentPage() == null ? 0 : pageObj
                             .getCurrentPage().intValue(), pageObj.getPageSize() == null ? 0 :
                             pageObj.getPageSize().intValue());
        }else{
            pList.doList(0, -1);
        }

        return pList;
    }

    public RecordCreditLog getObjByProperty(String propertyName, String value){
        return (RecordCreditLog)this.recordCreditLogDAO.getBy(propertyName, value);
    }
}




