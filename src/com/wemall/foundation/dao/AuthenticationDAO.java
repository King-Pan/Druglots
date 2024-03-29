package com.wemall.foundation.dao;

import org.springframework.stereotype.Repository;

import com.wemall.core.base.GenericDAO;
import com.wemall.foundation.domain.Authentication;

@Repository("AuthenticationDAO")
public class AuthenticationDAO extends GenericDAO<Authentication>{

}
