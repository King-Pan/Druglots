package com.wemall.foundation.service.impl;

import com.wemall.core.dao.IGenericDAO;
import com.wemall.core.query.GenericPageList;
import com.wemall.core.query.PageObject;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.Accessory;
import com.wemall.foundation.domain.IOU;
import com.wemall.foundation.service.IOUService;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IOUServiceImpl
    implements IOUService {
    @Resource(name = "IOUDAO")
    private IGenericDAO<IOU> iouDAO;

    public boolean delete(Long id){
        try {
            this.iouDAO.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public IOU getObjById(Long id){
        return (IOU)this.iouDAO.get(id);
    }

    public boolean save(IOU iou){
        try {
            this.iouDAO.save(iou);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public boolean update(IOU iou){
        try {
            this.iouDAO.update(iou);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public List<IOU> query(String query, Map params, int begin, int max){
        return this.iouDAO.query(query, params, begin, max);
    }

    public IPageList list(IQueryObject properties){
        if (properties == null){
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(Accessory.class, query,
                params, this.iouDAO);
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

    public IOU getObjByProperty(String propertyName, String value){
        return (IOU)this.iouDAO.getBy(propertyName, value);
    }
}




