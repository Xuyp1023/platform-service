package com.betterjr.modules.customer.service;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.customer.dao.CustMechLawMapper;
import com.betterjr.modules.customer.entity.CustMechLaw;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechLawService extends BaseService<CustMechLawMapper, CustMechLaw> {
    private static final String CUST_NO = "custNo";

    private static Logger logger = LoggerFactory.getLogger(CustMechLawService.class);

    /**
     * 查询法人信息
     * 
     * @param anCustNo
     * @return
     */
    public CustMechLaw findCustMechLaw(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        final List<CustMechLaw> lawes = this.selectByProperty(CUST_NO, anCustNo);
        return Collections3.getFirst(lawes);
    }

    /**
     * 修改法人信息
     * 
     * @param anCustMechLaw
     * @return
     */
    public CustMechLaw saveCustMechLaw(CustMechLaw anCustMechLaw, Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空");
        
        final Collection<CustMechLaw> custMechLaws = this.selectByProperty(CUST_NO, anCustNo);
        final CustMechLaw tempCustMechLaw = Collections3.getFirst(custMechLaws);
        BTAssert.notNull(tempCustMechLaw, "对应的公司法人信息没有找到！");
        
        tempCustMechLaw.initModifyValue(anCustMechLaw);
        this.updateByPrimaryKeySelective(tempCustMechLaw);
        return tempCustMechLaw;
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
