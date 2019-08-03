package com.wemall.foundation.service.impl;

import com.wemall.core.dao.IGenericDAO;
import com.wemall.core.query.GenericPageList;
import com.wemall.core.query.PageObject;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.Accessory;
import com.wemall.foundation.domain.TemiIous;
import com.wemall.foundation.service.TemiIousService;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TemiIousServiceImpl
    implements TemiIousService {
    @Resource(name = "TemiIousDAO")
    private IGenericDAO<TemiIous> temiIousDAO;

    public boolean delete(Long id){
        try {
            this.temiIousDAO.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public TemiIous getObjById(Long id){
        return (TemiIous)this.temiIousDAO.get(id);
    }

    public boolean save(TemiIous iou){
        try {
            this.temiIousDAO.save(iou);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public boolean update(TemiIous iou){
        try {
            this.temiIousDAO.update(iou);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public List<TemiIous> query(String query, Map params, int begin, int max){
        return this.temiIousDAO.query(query, params, begin, max);
    }

    public IPageList list(IQueryObject properties){
        if (properties == null){
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(Accessory.class, query,
                params, this.temiIousDAO);
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

    public TemiIous getObjByProperty(String propertyName, String value){
        return (TemiIous)this.temiIousDAO.getBy(propertyName, value);
    }
}




