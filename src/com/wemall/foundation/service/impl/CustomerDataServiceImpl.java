package com.wemall.foundation.service.impl;

import com.wemall.core.dao.IGenericDAO;
import com.wemall.core.query.GenericPageList;
import com.wemall.core.query.PageObject;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.Accessory;
import com.wemall.foundation.domain.CustomerData;
import com.wemall.foundation.service.CustomerDataService;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;

import org.apache.poi.hssf.record.formula.functions.T;
import org.springframework.stereotype.Service;

@Service
public class CustomerDataServiceImpl
    implements CustomerDataService {
    @Resource(name = "CustomerDataDAO")
    private IGenericDAO<CustomerData> customerDataDAO;

    public boolean delete(Long id){
        try {
            this.customerDataDAO.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public CustomerData getObjById(Long id){
        return (CustomerData)this.customerDataDAO.get(id);
    }

    public boolean save(CustomerData cus){
        try {
            this.customerDataDAO.save(cus);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public boolean update(CustomerData iou){
        try {
            this.customerDataDAO.update(iou);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public List<CustomerData> query(String query, Map params, int begin, int max){
        return this.customerDataDAO.query(query, params, begin, max);
    }

    public IPageList list(IQueryObject properties){
        if (properties == null){
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(CustomerData.class, query,
                params, this.customerDataDAO);
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

    public CustomerData getObjByProperty(String propertyName, String value){
        return (CustomerData)this.customerDataDAO.getBy(propertyName, value);
    }

     
}




