package com.betterjr.modules.customer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.dao.CustMechShareholderTmpMapper;
import com.betterjr.modules.customer.entity.CustMechShareholderTmp;
import com.betterjr.modules.customer.entity.CustMechShareholderTmp;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechShareholderTmpService extends BaseService<CustMechShareholderTmpMapper, CustMechShareholderTmp> {
    private static Logger logger = LoggerFactory.getLogger(CustMechShareholderTmpService.class);
    
    /**
     * 查询公司股东流水信息
     * 
     * @param anCustNo
     * @return
     */
    public CustMechShareholderTmp findCustMechShareholderTmpByCustNo(Long anId) {
        BTAssert.notNull(anId, "公司股东流水信息编号不允许为空！");
        
        return this.selectByPrimaryKey(anId);
    }

    /**
     * 保存公司股东流水信息
     * 
     * @param anCustMechShareholderTmp
     * @return
     */
    public int saveCustMechShareholderTmp(CustMechShareholderTmp anCustMechShareholderTmp, Long anId) {
        BTAssert.notNull(anId, "公司股东流水编号不允许为空！");
        
        final CustMechShareholderTmp tempCustMechShareholderTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechShareholderTmp, "没有找到对应的公司股东流水信息！");
        
        tempCustMechShareholderTmp.initModifyValue(anCustMechShareholderTmp);
        return this.updateByPrimaryKey(tempCustMechShareholderTmp);
    }

    /**
     * 添加公司股东流水信息
     * 
     * @param anCustMechShareholderTmp
     * @return
     */
    public int addCustMechShareholderTmp(CustMechShareholderTmp anCustMechShareholderTmp) {
        BTAssert.notNull(anCustMechShareholderTmp, "公司股东流水信息编号不允许为空！");
        
        anCustMechShareholderTmp.initAddValue();
        return this.insert(anCustMechShareholderTmp);
    }
}