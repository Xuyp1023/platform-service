package com.betterjr.modules.customer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.dao.CustMechBaseMapper;
import com.betterjr.modules.customer.entity.CustMechBase;

/**
 * 客户基本信息管理
 * @author liuwl
 *
 */
@Service
public class CustMechBaseService extends BaseService<CustMechBaseMapper, CustMechBase> {
    private static Logger logger = LoggerFactory.getLogger(CustMechBaseService.class);

    /**
     * 查询客户基本信息
     * @param anCustNo
     * @return
     */
    public CustMechBase findCustMechBase(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        return this.selectByPrimaryKey(anCustNo);
    }

    /**
     * 保存客户基本信息
     * @param anCustMechBase
     * @return
     */
    public CustMechBase saveCustMechBase(CustMechBase anCustMechBase, Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        final CustMechBase tempCustMechBase = this.selectByPrimaryKey(anCustNo);
        BTAssert.notNull(tempCustMechBase, "对应的客户基本信息没有找到！");
        
        tempCustMechBase.initModifyValue(anCustMechBase);
        this.updateByPrimaryKey(tempCustMechBase);
        
        return tempCustMechBase;
    }

    /**
     * 添加客户基本信息
     * @param anCustMechBase
     * @return
     */
    public CustMechBase addCustMechBase(CustMechBase anCustMechBase, Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        BTAssert.notNull(anCustMechBase, "客户基本信息不允许为空！");
        
        anCustMechBase.initAddValue(anCustNo);
        this.insert(anCustMechBase);
        return anCustMechBase;
    }
}