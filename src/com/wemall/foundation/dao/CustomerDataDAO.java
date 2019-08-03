package com.wemall.foundation.dao;

import com.wemall.core.base.GenericDAO;
import com.wemall.foundation.domain.CustomerData;
import org.springframework.stereotype.Repository;

@Repository("CustomerDataDAO")
public class CustomerDataDAO extends GenericDAO<CustomerData> {
}
