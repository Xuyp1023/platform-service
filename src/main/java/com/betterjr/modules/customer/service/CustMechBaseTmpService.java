package com.betterjr.modules.customer.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechBaseTmpMapper;
import com.betterjr.modules.customer.entity.CustMechBaseTmp;

/**
 * 客户基本信息流水信息管理
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechBaseTmpService extends BaseService<CustMechBaseTmpMapper, CustMechBaseTmp> {
    private static Logger logger = LoggerFactory.getLogger(CustMechBaseTmpService.class);

    /**
     * 查询客户基本信息流水信息
     * 
     * @param anId
     * @param anCustNo
     * @return
     */
    public CustMechBaseTmp findCustMechBaseTmp(Long anCustNo, Long anId) {
        BTAssert.notNull(anId, "客户基本信息流水信息编号不允许为空！");
        final CustMechBaseTmp custMechBaseTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(custMechBaseTmp, "没有找到对应的流水记录！");
        if (custMechBaseTmp.getRefId().equals(anCustNo)) {
            throw new BytterTradeException(20001, "客户编号不匹配！");
        }

        return custMechBaseTmp;
    }

    /**
     * 保存客户基本信息流水信息
     * 
     * @param anCustMechBaseTmp
     * @return
     */
    public int saveCustMechBaseTmp(CustMechBaseTmp anCustMechBaseTmp, Long anId) {
        BTAssert.notNull(anId, "客户基本信息流水编号不允许为空！");
        final CustMechBaseTmp tempCustMechBaseTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechBaseTmp, "没有找到对应的客户基本信息流水信息！");

        tempCustMechBaseTmp.initModifyValue(anCustMechBaseTmp);
        return this.updateByPrimaryKey(tempCustMechBaseTmp);
    }

    /**
     * 添加客户基本信息流水信息
     * 
     * @param anCustMechBaseTmp
     * @return
     */
    public CustMechBaseTmp addCustMechBaseTmp(CustMechBaseTmp anCustMechBaseTmp, String anTmpType) {
        BTAssert.notNull(anCustMechBaseTmp, "客户基本信息流水信息编号不允许为空！");

        anCustMechBaseTmp.initAddValue(anTmpType);
        this.insert(anCustMechBaseTmp);
        return anCustMechBaseTmp;
    }
}