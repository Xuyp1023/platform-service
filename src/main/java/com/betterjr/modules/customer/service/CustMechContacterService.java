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
import com.betterjr.modules.customer.dao.CustMechContacterMapper;
import com.betterjr.modules.customer.entity.CustMechContacter;
import com.betterjr.modules.customer.entity.CustMechContacter;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechContacterService extends BaseService<CustMechContacterMapper, CustMechContacter> {
    private static final String CUST_NO = "custNo";

    private static Logger logger = LoggerFactory.getLogger(CustMechContacterService.class);
    
    @Resource
    private CustMechContacterTmpService contacterTmpService;

    /**
     * 查询联系人信息
     * 
     * @param anCustNo
     * @return
     */
    public CustMechContacter findCustMechContacterByCustNo(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        final List<CustMechContacter> Contacters = this.selectByProperty(CUST_NO, anCustNo);
        return Collections3.getFirst(Contacters);
    }

    /**
     * 修改联系人信息
     * 
     * @param anCustMechContacter
     * @return
     */
    public CustMechContacter saveCustMechContacter(CustMechContacter anCustMechContacter, Long anId) {
        BTAssert.notNull(anId, "联系人编号不允许为空");
        
        final CustMechContacter tempCustMechContacter = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechContacter, "对应的公司联系人信息没有找到！");

        tempCustMechContacter.initModifyValue(anCustMechContacter);
        this.updateByPrimaryKeySelective(tempCustMechContacter);
        return tempCustMechContacter;
    }

    /**
     * 添加联系人信息
     * 
     * @param anCustMechContacter
     * @return
     */
    public CustMechContacter addCustMechContacter(CustMechContacter anCustMechContacter) {
        BTAssert.notNull(anCustMechContacter, "联系人信息不允许为空！");
        
        anCustMechContacter.initAddValue();
        this.insert(anCustMechContacter);
        return anCustMechContacter;
    }

}