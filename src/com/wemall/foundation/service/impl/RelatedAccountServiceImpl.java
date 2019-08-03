package com.wemall.foundation.service.impl;

import com.wemall.core.dao.IGenericDAO;
import com.wemall.core.query.GenericPageList;
import com.wemall.core.query.PageObject;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.RelatedAccount;
import com.wemall.foundation.service.RelatedAccountService;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RelatedAccountServiceImpl
    implements RelatedAccountService {
    @Resource(name = "RelatedAccountDAO")
    private IGenericDAO<RelatedAccount> relatedAccountDAO;

    public boolean save(RelatedAccount advert){
        try {
            this.relatedAccountDAO.save(advert);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public RelatedAccount getObjById(Long id){
    	RelatedAccount advert = (RelatedAccount)this.relatedAccountDAO.get(id);
        if (advert != null){
            return advert;
        }

        return null;
    }

    public boolean delete(Long id){
        try {
            this.relatedAccountDAO.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public boolean batchDelete(List<Serializable> advertIds){
        for (Serializable id : advertIds){
            delete((Long)id);
        }

        return true;
    }

    public IPageList list(IQueryObject properties){
        if (properties == null){
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(RelatedAccount.class, query,
                params, this.relatedAccountDAO);
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

    public boolean update(RelatedAccount advert){
        try {
            this.relatedAccountDAO.update(advert);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public List<RelatedAccount> query(String query, Map params, int begin, int max){
        return this.relatedAccountDAO.query(query, params, begin, max);
    }

	public RelatedAccount getObjByProperty(String paramString1,
			String paramString2) {
		return (RelatedAccount)this.relatedAccountDAO.getBy(paramString1, paramString2);		 
	}
}




