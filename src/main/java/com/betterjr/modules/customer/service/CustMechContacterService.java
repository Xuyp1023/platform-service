package com.betterjr.modules.customer.service;

import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.customer.dao.CustMechContacterMapper;
import com.betterjr.modules.customer.entity.CustMechContacter;
import com.betterjr.modules.customer.entity.CustMechContacter;
import com.betterjr.modules.customer.entity.CustMechContacterTmp;
import com.betterjr.modules.customer.entity.CustMechContacter;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechContacterService extends BaseService<CustMechContacterMapper, CustMechContacter> {
    
    @Resource
    private CustMechContacterTmpService contacterTmpService;

    /**
     * 查询联系人列表
     * @param anCustNo
     * @return
     */
    public List<CustMechContacter> queryCustMechContacter(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        return this.selectByProperty("custNo", anCustNo);
    }
    
    /**
     * 查询联系人信息
     */
    public CustMechContacter findContacter(Long anId) {
        BTAssert.notNull(anId, "联系人编号不允许为空！");
        
        CustMechContacter contacter = this.selectByPrimaryKey(anId);
        return contacter;
    }
    
    /**
     * 添加联系人信息
     */
    public CustMechContacter addCustMechContacter(CustMechContacterTmp anContacterTmp) {
        BTAssert.notNull(anContacterTmp, "联系人流水信息不允许为空！");
        
        CustMechContacter contacter = new CustMechContacter();
        contacter.initAddValue(anContacterTmp);
        
        this.insert(contacter);
        return contacter;
    }
    
    /**
     * 保存联系人信息
     */
    public CustMechContacter saveCustMechContacter(CustMechContacter anContacter, Long anId) {
        BTAssert.notNull(anId, "联系人编号不允许为空！");
        BTAssert.notNull(anContacter, "联系人信息不允许为空！");
        
        final CustMechContacter tempCustMechContacter = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechContacter, "对应的联系人信息没有找到！");
        
        tempCustMechContacter.initModifyValue(anContacter);
        this.updateByPrimaryKeySelective(tempCustMechContacter);
        return tempCustMechContacter;
    }

    /**
     * 通过联系人流水修改联系人 
     */
    public CustMechContacter saveCustMechContacter(CustMechContacterTmp anContacterTmp) {
        BTAssert.notNull(anContacterTmp, "联系人流水编号不允许为空！");
        
        final CustMechContacter tempCustMechContacter = this.selectByPrimaryKey(anContacterTmp.getRefId());
        BTAssert.notNull(tempCustMechContacter, "对应的联系人信息没有找到！");
        
        tempCustMechContacter.initModifyValue(anContacterTmp);
        this.updateByPrimaryKeySelective(tempCustMechContacter);
        return tempCustMechContacter;
    }

}