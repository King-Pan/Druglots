package com.wemall.foundation.service.impl;

import com.wemall.core.dao.IGenericDAO;
import com.wemall.core.query.GenericPageList;
import com.wemall.core.query.PageObject;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.Accessory;
import com.wemall.foundation.domain.TemiIousLog;
import com.wemall.foundation.service.TemiIousLogService;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class TemiIousLogServiceImpl
    implements TemiIousLogService {
    @Resource(name = "TemiIousLogDAO")
    private IGenericDAO<TemiIousLog> temiIousLogDAO;

    public boolean delete(Long id){
        try {
            this.temiIousLogDAO.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public TemiIousLog getObjById(Long id){
        return (TemiIousLog)this.temiIousLogDAO.get(id);
    }

    public boolean save(TemiIousLog iou){
        try {
            this.temiIousLogDAO.save(iou);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public boolean update(TemiIousLog iou){
        try {
            this.temiIousLogDAO.update(iou);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public List<TemiIousLog> query(String query, Map params, int begin, int max){
        return this.temiIousLogDAO.query(query, params, begin, max);
    }

    public IPageList list(IQueryObject properties){
        if (properties == null){
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(TemiIousLog.class, query,
                params, this.temiIousLogDAO);
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

    public TemiIousLog getObjByProperty(String propertyName, String value){
        return (TemiIousLog)this.temiIousLogDAO.getBy(propertyName, value);
    }
}




