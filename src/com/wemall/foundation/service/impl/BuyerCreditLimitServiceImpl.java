package com.wemall.foundation.service.impl;

import com.wemall.core.dao.IGenericDAO;
import com.wemall.core.query.GenericPageList;
import com.wemall.core.query.PageObject;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.BuyerCreditLimit;
import com.wemall.foundation.service.BuyerCreditLimitService;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BuyerCreditLimitServiceImpl
    implements BuyerCreditLimitService {
    @Resource(name = "BuyerCreditLimitDao")
    private IGenericDAO<BuyerCreditLimit> buyercreditLimitDao;

    public boolean delete(Long id){
        try {
            this.buyercreditLimitDao.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public BuyerCreditLimit getObjById(Long id){
        return (BuyerCreditLimit)this.buyercreditLimitDao.get(id);
    }

    public boolean save(BuyerCreditLimit acc){
        try {
            this.buyercreditLimitDao.save(acc);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public boolean update(BuyerCreditLimit acc){
        try {
            this.buyercreditLimitDao.update(acc);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public List<BuyerCreditLimit> query(String query, Map params, int begin, int max){
        return this.buyercreditLimitDao.query(query, params, begin, max);
    }

    public IPageList list(IQueryObject properties){
        if (properties == null){
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(BuyerCreditLimit.class, query,
                params, this.buyercreditLimitDao);
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

    public BuyerCreditLimit getObjByProperty(String propertyName, String value){
        return (BuyerCreditLimit)this.buyercreditLimitDao.getBy(propertyName, value);
    }
    
}




