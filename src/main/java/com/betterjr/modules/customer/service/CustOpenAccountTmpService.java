package com.betterjr.modules.customer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.modules.customer.dao.CustOpenAccountTmpMapper;
import com.betterjr.modules.customer.entity.CustOpenAccountTmp;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustOpenAccountTmpService extends BaseService<CustOpenAccountTmpMapper, CustOpenAccountTmp> {
    private static Logger logger = LoggerFactory.getLogger(CustOpenAccountTmpService.class);

}