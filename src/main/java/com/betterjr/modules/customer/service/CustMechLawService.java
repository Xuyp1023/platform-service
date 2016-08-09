package com.betterjr.modules.customer.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechLawMapper;
import com.betterjr.modules.customer.entity.CustMechLaw;
import com.betterjr.modules.customer.entity.CustMechLawTmp;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechLawService extends BaseService<CustMechLawMapper, CustMechLaw> {
    @Resource
    private CustAccountService accountService;
    
    @Resource
    private CustMechLawTmpService lawTmpService;
    
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
     * 法人信息-修改 代录/变更
     * 
     * @param anCustMechLaw
     * @return
     */
    public CustMechLaw saveCustMechLaw(CustMechLawTmp anCustMechLawTmp) {
        BTAssert.notNull(anCustMechLawTmp, "法人流水信息不允许为空！");

        // 根据 类型区别保存数据方式
        String tmpType = anCustMechLawTmp.getTmpType();
        Long custNo = anCustMechLawTmp.getRefId();
        CustMechLaw tempCustMechLaw = this.findCustMechLawByCustNo(custNo);

        BTAssert.notNull(tempCustMechLaw, "没有找到法人信息!");

        tempCustMechLaw.initModifyValue(anCustMechLawTmp);
        this.updateByPrimaryKeySelective(tempCustMechLaw);

        return tempCustMechLaw;
    }

    /**
     * 法人信息-添加
     * 
     * @param anCustMechLaw
     * @return
     */
    public CustMechLaw addCustMechLaw(CustMechLaw anCustMechLaw, Long anCustNo) {
        BTAssert.notNull(anCustMechLaw, "法人信息不允许为空！");
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");

        final CustInfo custInfo = accountService.selectByPrimaryKey(anCustNo);
        anCustMechLaw.initAddValue(anCustNo, custInfo.getCustName(), custInfo.getRegOperId(), custInfo.getRegOperName(), custInfo.getOperOrg());
        this.insert(anCustMechLaw);
        
        lawTmpService.addCustMechLawTmp(anCustMechLaw);
        
        return anCustMechLaw;
    }

}
