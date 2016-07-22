package com.betterjr.modules.customer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.dao.CustMechLawTmpMapper;
import com.betterjr.modules.customer.entity.CustMechLawTmp;
import com.betterjr.modules.customer.entity.CustMechLawTmp;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechLawTmpService extends BaseService<CustMechLawTmpMapper, CustMechLawTmp> {
    private static Logger logger = LoggerFactory.getLogger(CustMechLawTmpService.class);

    /**
     * 查询公司法人流水信息
     * 
     * @param anCustNo
     * @return
     */
    public CustMechLawTmp findCustMechLawTmpByCustNo(Long anId) {
        BTAssert.notNull(anId, "公司法人流水信息编号不允许为空！");
        
        return this.selectByPrimaryKey(anId);
    }

    /**
     * 保存公司法人流水信息
     * 
     * @param anCustMechLawTmp
     * @return
     */
    public int saveCustMechLawTmp(CustMechLawTmp anCustMechLawTmp, Long anId) {
        BTAssert.notNull(anId, "公司法人流水编号不允许为空！");
        
        final CustMechLawTmp tempCustMechLawTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechLawTmp, "没有找到对应的公司法人流水信息！");
        
        tempCustMechLawTmp.initModifyValue(anCustMechLawTmp);
        return this.updateByPrimaryKey(tempCustMechLawTmp);
    }

    /**
     * 添加公司法人流水信息
     * 
     * @param anCustMechLawTmp
     * @return
     */
    public int addCustMechLawTmp(CustMechLawTmp anCustMechLawTmp) {
        BTAssert.notNull(anCustMechLawTmp, "公司法人流水信息编号不允许为空！");
        
        anCustMechLawTmp.initAddValue();
        return this.insert(anCustMechLawTmp);
    }
}
