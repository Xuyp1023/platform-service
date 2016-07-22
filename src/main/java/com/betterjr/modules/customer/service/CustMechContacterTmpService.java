package com.betterjr.modules.customer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.dao.CustMechContacterTmpMapper;
import com.betterjr.modules.customer.entity.CustMechContacterTmp;
import com.betterjr.modules.customer.entity.CustMechContacterTmp;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechContacterTmpService extends BaseService<CustMechContacterTmpMapper, CustMechContacterTmp> {
    private static Logger logger = LoggerFactory.getLogger(CustMechContacterTmpService.class);

    /**
     * 查询联系人流水信息
     * 
     * @param anCustNo
     * @return
     */
    public CustMechContacterTmp findCustMechContacterTmpByCustNo(Long anId) {
        BTAssert.notNull(anId, "联系人流水信息编号不允许为空！");

        return this.selectByPrimaryKey(anId);
    }

    /**
     * 保存联系人流水信息
     * 
     * @param anCustMechContacterTmp
     * @return
     */
    public int saveCustMechContacterTmp(CustMechContacterTmp anCustMechContacterTmp, Long anId) {
        BTAssert.notNull(anId, "联系人流水编号不允许为空！");

        final CustMechContacterTmp tempCustMechContacterTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechContacterTmp, "没有找到对应的联系人流水信息！");

        tempCustMechContacterTmp.initModifyValue(anCustMechContacterTmp);
        return this.updateByPrimaryKey(tempCustMechContacterTmp);
    }

    /**
     * 添加联系人流水信息
     * 
     * @param anCustMechContacterTmp
     * @return
     */
    public int addCustMechContacterTmp(CustMechContacterTmp anCustMechContacterTmp) {
        BTAssert.notNull(anCustMechContacterTmp, "联系人流水信息编号不允许为空！");

        anCustMechContacterTmp.initAddValue();
        return this.insert(anCustMechContacterTmp);
    }
}