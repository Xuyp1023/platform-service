package com.betterjr.modules.customer.service;

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
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechBusinLicenceMapper;
import com.betterjr.modules.customer.entity.CustMechBusinLicence;
import com.betterjr.modules.customer.entity.CustMechBusinLicenceTmp;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechBusinLicenceService extends BaseService<CustMechBusinLicenceMapper, CustMechBusinLicence> {
    @Resource
    private CustAccountService accountService;
    
    @Resource
    private CustMechBusinLicenceTmpService businLicenceTmpService;
    
    /**
     * 营业执照信息-查询详情
     * 
     * @param anCustNo
     * @return
     */
    public CustMechBusinLicence findBusinLicenceByCustNo(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");

        final List<CustMechBusinLicence> businLicences = this.selectByProperty(CustomerConstants.CUST_NO, anCustNo);
        return Collections3.getFirst(businLicences);
    }

    /**
     * 营业执照信息-查询详情
     * 
     * @param anCustNo
     * @return
     */
    public CustMechBusinLicence findBusinLicence(Long anId) {
        BTAssert.notNull(anId, "客户编号不允许为空！");

        final CustMechBusinLicence businLicence = this.selectByPrimaryKey(anId);
        BTAssert.notNull(businLicence, "没有找到营业执照信息!");

        return businLicence;
    }

    /**
     * 营业执照信息-修改 代录/变更
     * 
     * @param anCustMechBusinLicence
     * @return
     */
    public CustMechBusinLicence saveBusinLicence(CustMechBusinLicenceTmp anBusinLicenceTmp) {
        BTAssert.notNull(anBusinLicenceTmp, "营业执照流水信息不允许为空！");

        Long custNo = anBusinLicenceTmp.getRefId();
        CustMechBusinLicence tempCustMechBusinLicence = this.findBusinLicenceByCustNo(custNo);
        BTAssert.notNull(tempCustMechBusinLicence, "没有找到营业执照信息!");

        tempCustMechBusinLicence.initModifyValue(anBusinLicenceTmp);
        this.updateByPrimaryKeySelective(tempCustMechBusinLicence);

        return tempCustMechBusinLicence;
    }

    /**
     * 营业执照信息-添加
     * 
     * @param anBusinLicence
     * @return
     */
    public CustMechBusinLicence addBusinLicence(CustMechBusinLicence anBusinLicence, Long anCustNo) {
        BTAssert.notNull(anBusinLicence, "营业执照信息不允许为空！");

        final CustInfo custInfo = accountService.selectByPrimaryKey(anCustNo);
        anBusinLicence.initAddValue(anCustNo, custInfo.getCustName(), custInfo.getRegOperId(), custInfo.getRegOperName(), custInfo.getOperOrg());
        this.insert(anBusinLicence);
        
        // 建立初始流水记录
        businLicenceTmpService.addBusinLicenceTmp(anBusinLicence);
        
        return anBusinLicence;
    }

}