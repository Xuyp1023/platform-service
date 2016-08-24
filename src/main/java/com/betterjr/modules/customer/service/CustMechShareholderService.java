package com.betterjr.modules.customer.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.dao.CustMechShareholderMapper;
import com.betterjr.modules.customer.entity.CustMechShareholder;
import com.betterjr.modules.customer.entity.CustMechShareholderTmp;

/**
 * 股东信息管理
 * @author liuwl
 *
 */
@Service
public class CustMechShareholderService extends BaseService<CustMechShareholderMapper, CustMechShareholder> {
    
    @Resource
    private CustMechShareholderTmpService shareholderTmpService;

    /**
     * 查询股东列表
     * @param anCustNo
     * @return
     */
    public List<CustMechShareholder> queryShareholder(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        return this.selectByProperty("custNo", anCustNo);
    }
    
    /**
     * 查询股东信息
     */
    public CustMechShareholder findShareholder(Long anId) {
        BTAssert.notNull(anId, "股东编号不允许为空！");
        
        CustMechShareholder Shareholder = this.selectByPrimaryKey(anId);
        return Shareholder;
    }
    
    /**
     * 添加股东信息
     */
    public CustMechShareholder addCustMechShareholder(CustMechShareholderTmp anShareholderTmp) {
        BTAssert.notNull(anShareholderTmp, "股东流水信息不允许为空！");
        
        CustMechShareholder shareholder = new CustMechShareholder();
        shareholder.initAddValue(anShareholderTmp);
        
        this.insert(shareholder);
        return shareholder;
    }
    
    /**
     * 保存股东信息
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

    /**
     * 通过股东流水修改股东 
     */
    public CustMechShareholder saveCustMechShareholder(CustMechShareholderTmp anShareholderTmp) {
        BTAssert.notNull(anShareholderTmp, "股东流水编号不允许为空！");
        
        final CustMechShareholder tempCustMechShareholder = this.selectByPrimaryKey(anShareholderTmp.getRefId());
        BTAssert.notNull(tempCustMechShareholder, "对应的股东信息没有找到！");
        
        tempCustMechShareholder.initModifyValue(anShareholderTmp);
        this.updateByPrimaryKeySelective(tempCustMechShareholder);
        return tempCustMechShareholder;
    }

}
