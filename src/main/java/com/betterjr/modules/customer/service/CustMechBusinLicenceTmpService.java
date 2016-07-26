package com.betterjr.modules.customer.service;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.dao.CustMechBusinLicenceTmpMapper;
import com.betterjr.modules.customer.entity.CustMechBusinLicenceTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;

/**
 * 营业执照流水
 * @author liuwl
 *
 */
@Service
public class CustMechBusinLicenceTmpService extends BaseService<CustMechBusinLicenceTmpMapper, CustMechBusinLicenceTmp> implements IFormalDataService{

    /**
     * 查询营业执照流水信息
     * @param anCustNo
     * @return
     */
    public CustMechBusinLicenceTmp findCustMechBusinLicenceTmpByCustNo(Long anId) {
        BTAssert.notNull(anId, "营业执照流水信息编号不允许为空！");
        
        return this.selectByPrimaryKey(anId);
    }
    
    /**
     * 保存营业执照流水信息
     * @param anCustMechBusinLicenceTmp
     * @return
     */
    public int saveCustMechBusinLicenceTmp(CustMechBusinLicenceTmp anCustMechBusinLicenceTmp, Long anId) {
        BTAssert.notNull(anId, "营业执照流水编号不允许为空！");
        
        final CustMechBusinLicenceTmp tempCustMechBusinLicenceTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechBusinLicenceTmp, "没有找到对应的营业执照流水信息！");
        
        tempCustMechBusinLicenceTmp.initModifyValue(anCustMechBusinLicenceTmp);
        return this.updateByPrimaryKey(tempCustMechBusinLicenceTmp);
    }
    
    /**
     * 添加营业执照流水信息
     * @param anCustMechBusinLicenceTmp
     * @return
     */
    public int addCustMechBusinLicenceTmp(CustMechBusinLicenceTmp anCustMechBusinLicenceTmp) {
        BTAssert.notNull(anCustMechBusinLicenceTmp, "营业执照流水信息编号不允许为空！");
        
        anCustMechBusinLicenceTmp.initAddValue();
        return this.insert(anCustMechBusinLicenceTmp);
    }

    @Override
    public void saveFormalData(String ... anTmpIds) {
    }
}