package com.betterjr.modules.customer.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.dao.CustMechShareholderMapper;
import com.betterjr.modules.customer.entity.CustMechShareholder;

/**
 * 公司股东
 * @author liuwl
 *
 */
@Service
public class CustMechShareholderService extends BaseService<CustMechShareholderMapper, CustMechShareholder> {
    private static Logger logger = LoggerFactory.getLogger(CustMechShareholderService.class);

    /**
     * 查询股东列表
     * @param anCustNo
     * @return
     */
    public List<CustMechShareholder> queryCustMechShareholderByCustNo(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        return this.selectByProperty("custNo", anCustNo);
    }
    
    /**
     * 查询股东信息
     */
    public CustMechShareholder findCustMechShareholder(Long anId) {
        BTAssert.notNull(anId, "股东编号不允许为空！");
        
        return this.selectByPrimaryKey(anId);
    }
    
    /**
     * 添加股东信息
     * @param anCustMechShareholder
     * @return
     */
    public CustMechShareholder addCustMechShareholder(CustMechShareholder anCustMechShareholder) {
        BTAssert.notNull(anCustMechShareholder, "股东信息不允许为空！");
        
        anCustMechShareholder.initAddValue();
        this.insert(anCustMechShareholder);
        return anCustMechShareholder;
    }
    
    /**
     * 保存股东信息
     * @param anCustMechShareholder
     * @param anId
     * @return
     */
    public CustMechShareholder saveCustMechShareholder(CustMechShareholder anCustMechShareholder, Long anId) {
        BTAssert.notNull(anId, "股东编号不允许为空！");
        BTAssert.notNull(anCustMechShareholder, "股东信息不允许为空！");
        
        final CustMechShareholder tempCustMechShareholder = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechShareholder, "对应的股东信息没有找到！");
        
        tempCustMechShareholder.initModifyValue(anCustMechShareholder);
        this.updateByPrimaryKeySelective(tempCustMechShareholder);
        return tempCustMechShareholder;
    }
}