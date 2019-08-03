package com.wemall.foundation.service.impl;

import com.wemall.core.dao.IGenericDAO;
import com.wemall.core.query.GenericPageList;
import com.wemall.core.query.PageObject;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject; 
import com.wemall.foundation.domain.CustomerData;
import com.wemall.foundation.domain.ProcurementRelationship; 
import com.wemall.foundation.service.ProcurementRelationshipService;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
 
import org.springframework.stereotype.Service;

@Service
public class ProcurementRelationshipServiceImpl
    implements ProcurementRelationshipService {
    @Resource(name = "ProcurementRelationshipDAO")
    private IGenericDAO<ProcurementRelationship> procurementRelationshipDAO;

    public boolean delete(Long id){
        try {
            this.procurementRelationshipDAO.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public ProcurementRelationship getObjById(Long id){
        return (ProcurementRelationship)this.procurementRelationshipDAO.get(id);
    }

    public boolean save(ProcurementRelationship cus){
        try {
            this.procurementRelationshipDAO.save(cus);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public boolean update(ProcurementRelationship iou){
        try {
            this.procurementRelationshipDAO.update(iou);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public List<ProcurementRelationship> query(String query, Map params, int begin, int max){
        return this.procurementRelationshipDAO.query(query, params, begin, max);
    }

    public IPageList list(IQueryObject properties){
        if (properties == null){
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(ProcurementRelationship.class, query,
                params, this.procurementRelationshipDAO);
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

    public ProcurementRelationship getObjByProperty(String propertyName, String value){
        return (ProcurementRelationship)this.procurementRelationshipDAO.getBy(propertyName, value);
    }

     
}




