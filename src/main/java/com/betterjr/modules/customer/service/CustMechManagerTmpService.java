package com.betterjr.modules.customer.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechManagerTmpMapper;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustMechManagerTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechManagerTmpService extends BaseService<CustMechManagerTmpMapper, CustMechManagerTmp> implements IFormalDataService {
    @Resource
    private CustMechManagerService managerService;

    @Resource
    private CustInsteadRecordService insteadRecordService;

    @Resource
    private CustChangeApplyService changeApplyService;

    /**
     * 查询公司高管流水信息
     * 
     * @param anCustNo
     * @return
     */
    public CustMechManagerTmp findCustMechManagerTmpByCustNo(Long anId) {
        BTAssert.notNull(anId, "公司高管流水信息编号不允许为空！");

        return this.selectByPrimaryKey(anId);
    }

    /**
     * 保存公司高管流水信息
     * 
     * @param anCustMechManagerTmp
     * @return
     */
    public int saveCustMechManagerTmp(CustMechManagerTmp anCustMechManagerTmp, Long anId) {
        BTAssert.notNull(anId, "公司高管流水编号不允许为空！");

        final CustMechManagerTmp tempCustMechManagerTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechManagerTmp, "没有找到对应的公司高管流水信息！");

        tempCustMechManagerTmp.initModifyValue(anCustMechManagerTmp);
        return this.updateByPrimaryKey(tempCustMechManagerTmp);
    }

    /**
     * 添加公司高管流水信息
     * 
     * @param anCustMechManagerTmp
     * @return
     */
    public int addCustMechManagerTmp(CustMechManagerTmp anCustMechManagerTmp) {
        BTAssert.notNull(anCustMechManagerTmp, "公司高管流水信息编号不允许为空！");

        return this.insert(anCustMechManagerTmp);
    }

    /**
     * 法人信息变更申请
     * 
     * @param anCustMechLawTmp
     * @return
     */
    public CustChangeApply addCustChangeApply(CustMechManagerTmp anCustMechManagerTmp) {
        BTAssert.notNull(anCustMechManagerTmp, "基本信息变更申请不能为空");

        anCustMechManagerTmp.initAddValue(CustomerConstants.TMP_TYPE_CHANGE);
        this.insert(anCustMechManagerTmp);

        // 发起变更申请
        CustChangeApply custChangeApply = changeApplyService.addChangeApply(anCustMechManagerTmp.getRefId(), CustomerConstants.ITEM_MANAGER,
                String.valueOf(anCustMechManagerTmp.getId()));

        return custChangeApply;
    }

    @Override
    public void saveFormalData(String... anTmpIds) {

    }
}