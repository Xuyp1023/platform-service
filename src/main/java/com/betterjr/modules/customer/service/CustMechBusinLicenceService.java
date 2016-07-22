package com.betterjr.modules.customer.service;

import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.customer.dao.CustMechBusinLicenceMapper;
import com.betterjr.modules.customer.entity.CustMechBusinLicence;
import com.betterjr.modules.customer.entity.CustMechBusinLicence;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechBusinLicenceService extends BaseService<CustMechBusinLicenceMapper,CustMechBusinLicence> {
    private static final String CUST_NO = "custNo";
    
    private static Logger logger = LoggerFactory.getLogger(CustMechBusinLicenceService.class);

    /**
     * 查询营业执照信息
     * 
     * @param anCustNo
     * @return
     */
    public CustMechBusinLicence findCustMechBusinLicenceByCustNo(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        final List<CustMechBusinLicence> BusinLicences = this.selectByProperty(CUST_NO, anCustNo);
        return Collections3.getFirst(BusinLicences);
    }

    /**
     * 修改营业执照信息
     * 
     * @param anCustMechBusinLicence
     * @return
     */
    public CustMechBusinLicence saveCustMechBusinLicence(CustMechBusinLicence anCustMechBusinLicence, Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空");
        
        final Collection<CustMechBusinLicence> custMechBusinLicences = this.selectByProperty(CUST_NO, anCustNo);
        final CustMechBusinLicence tempCustMechBusinLicence = Collections3.getFirst(custMechBusinLicences);
        BTAssert.notNull(tempCustMechBusinLicence, "对应的营业执照信息没有找到！");
        
        tempCustMechBusinLicence.initModifyValue(anCustMechBusinLicence);
        this.updateByPrimaryKeySelective(tempCustMechBusinLicence);
        return tempCustMechBusinLicence;
    }
    
    /**
     * 添加营业执照信息
     * 
     * @param anCustMechBusinLicence
     * @return
     */
    public CustMechBusinLicence addCustMechBusinLicence(CustMechBusinLicence anCustMechBusinLicence) {
        BTAssert.notNull(anCustMechBusinLicence, "营业执照信息不允许为空！");
        
        anCustMechBusinLicence.initAddValue();
        this.insert(anCustMechBusinLicence);
        return anCustMechBusinLicence;
    }
}