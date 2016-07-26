package com.betterjr.modules.customer.service;

import java.util.Collection;
import java.util.List;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechLawMapper;
import com.betterjr.modules.customer.entity.CustMechLaw;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechLawService extends BaseService<CustMechLawMapper, CustMechLaw> {

    /**
     * 查询法人信息
     * 
     * @param anCustNo
     * @return
     */
    public CustMechLaw findCustMechLawByCustNo(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        final List<CustMechLaw> lawes = this.selectByProperty(CustomerConstants.CUST_NO, anCustNo);
        return Collections3.getFirst(lawes);
    }
    
    /**
     * 查询法人信息
     * 
     * @param anCustNo
     * @return
     */
    public CustMechLaw findCustMechLaw(Long anId) {
        BTAssert.notNull(anId, "客户编号不允许为空！");
        
        return this.selectByPrimaryKey(anId);
    }

    /**
     * 修改法人信息
     * 
     * @param anCustMechLaw
     * @return
     */
    public CustMechLaw saveCustMechLaw(CustMechLaw anCustMechLaw, Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空");
        
        final Collection<CustMechLaw> custMechLaws = this.selectByProperty(CustomerConstants.CUST_NO, anCustNo);
        final CustMechLaw tempCustMechLaw = Collections3.getFirst(custMechLaws);
        BTAssert.notNull(tempCustMechLaw, "对应的公司法人信息没有找到！");
        
        tempCustMechLaw.initModifyValue(anCustMechLaw);
        this.updateByPrimaryKeySelective(tempCustMechLaw);
        return tempCustMechLaw;
    }
    
    /**
     * 
     * @param anCustMechLaw
     * @return
     */
    public CustMechLaw saveCustMechLaw(CustMechLaw anCustMechLaw) {
        BTAssert.notNull(anCustMechLaw, "法人信息不允许为空");
        
        this.updateByPrimaryKeySelective(anCustMechLaw);
        return anCustMechLaw;
    }
    
    /**
     * 添加法人信息
     * 
     * @param anCustMechLaw
     * @return
     */
    public CustMechLaw addCustMechLaw(CustMechLaw anCustMechLaw) {
        BTAssert.notNull(anCustMechLaw, "法人信息不允许为空！");
        
        anCustMechLaw.initAddValue();
        this.insert(anCustMechLaw);
        return anCustMechLaw;
    }


}
