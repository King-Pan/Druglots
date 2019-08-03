package com.wemall.foundation.service.impl;

import com.wemall.core.dao.IGenericDAO;
import com.wemall.core.query.GenericPageList;
import com.wemall.core.query.PageObject;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.CreditLlineManagement;
import com.wemall.foundation.service.CreditLlineManagementService;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreditLlineManagementServiceimpl
    implements CreditLlineManagementService {
    @Resource(name = "creditLlineManagementDAO")
    private IGenericDAO<CreditLlineManagement> creditLlineManagementDAO;

    public boolean delete(Long id){
        try {
            this.creditLlineManagementDAO.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public CreditLlineManagement getObjById(Long id){
        return (CreditLlineManagement)this.creditLlineManagementDAO.get(id);
    }

    public boolean save(CreditLlineManagement acc){
        try {
            this.creditLlineManagementDAO.save(acc);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public boolean update(CreditLlineManagement acc){
        try {
            this.creditLlineManagementDAO.update(acc);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public List<CreditLlineManagement> query(String query, Map params, int begin, int max){
        return this.creditLlineManagementDAO.query(query, params, begin, max);
    }

    public IPageList list(IQueryObject properties){
        if (properties == null){
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(CreditLlineManagement.class, query,
                params, this.creditLlineManagementDAO);
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

    public CreditLlineManagement getObjByProperty(String propertyName, String value){
        return (CreditLlineManagement)this.creditLlineManagementDAO.getBy(propertyName, value);
    }
}




