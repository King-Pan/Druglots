package com.wemall.foundation.service.impl;

import com.wemall.core.dao.IGenericDAO;
import com.wemall.core.query.GenericPageList;
import com.wemall.core.query.PageObject;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.StoreCreditStatus;
import com.wemall.foundation.service.StoreCreditStatusService;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StoreCreditStatusServiceimpl
    implements StoreCreditStatusService {
    @Resource(name = "StoreCreditStatusDAO")
    private IGenericDAO<StoreCreditStatus> StoreCreditStatusDAO;

    public boolean delete(Long id){
        try {
            this.StoreCreditStatusDAO.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public StoreCreditStatus getObjById(Long id){
        return (StoreCreditStatus)this.StoreCreditStatusDAO.get(id);
    }

    public boolean save(StoreCreditStatus acc){
        try {
            this.StoreCreditStatusDAO.save(acc);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public boolean update(StoreCreditStatus acc){
        try {
            this.StoreCreditStatusDAO.update(acc);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public List<StoreCreditStatus> query(String query, Map params, int begin, int max){
        return this.StoreCreditStatusDAO.query(query, params, begin, max);
    }

    public IPageList list(IQueryObject properties){
        if (properties == null){
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(StoreCreditStatus.class, query,
                params, this.StoreCreditStatusDAO);
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

    public StoreCreditStatus getObjByProperty(String propertyName, String value){
        return (StoreCreditStatus)this.StoreCreditStatusDAO.getBy(propertyName, value);
    }
}




