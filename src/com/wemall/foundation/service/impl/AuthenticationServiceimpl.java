package com.wemall.foundation.service.impl;

import com.wemall.core.dao.IGenericDAO;
import com.wemall.core.query.GenericPageList;
import com.wemall.core.query.PageObject;
import com.wemall.core.query.support.IPageList;
import com.wemall.core.query.support.IQueryObject;
import com.wemall.foundation.domain.Authentication;
import com.wemall.foundation.service.AuthenticationService;

import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthenticationServiceimpl
    implements AuthenticationService {
    @Resource(name = "AuthenticationDAO")
    private IGenericDAO<Authentication> authenticationDAO;

    public boolean delete(Long id){
        try {
            this.authenticationDAO.remove(id);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public Authentication getObjById(Long id){
        return (Authentication)this.authenticationDAO.get(id);
    }

    public boolean save(Authentication acc){
        try {
            this.authenticationDAO.save(acc);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public boolean update(Authentication acc){
        try {
            this.authenticationDAO.update(acc);
            return true;
        } catch (Exception e){
        }

        return false;
    }

    public List<Authentication> query(String query, Map params, int begin, int max){
        return this.authenticationDAO.query(query, params, begin, max);
    }

    public IPageList list(IQueryObject properties){
        if (properties == null){
            return null;
        }
        String query = properties.getQuery();
        Map params = properties.getParameters();
        GenericPageList pList = new GenericPageList(Authentication.class, query,
                params, this.authenticationDAO);
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

    public Authentication getObjByProperty(String propertyName, String value){
        return (Authentication)this.authenticationDAO.getBy(propertyName, value);
    }

    
}




