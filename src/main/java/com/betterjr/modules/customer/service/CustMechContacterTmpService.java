package com.betterjr.modules.customer.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechContacterTmpMapper;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustMechContacterTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechContacterTmpService extends BaseService<CustMechContacterTmpMapper, CustMechContacterTmp> implements IFormalDataService {
    @Resource
    private CustMechContacterService contacterService;

    @Resource
    private CustInsteadRecordService insteadRecordService;

    @Resource
    private CustChangeApplyService changeApplyService;

    /**
     * 查询联系人流水信息
     * 
     * @param anCustNo
     * @return
     */
    public CustMechContacterTmp findCustMechContacterTmpByCustNo(Long anId) {
        BTAssert.notNull(anId, "联系人流水信息编号不允许为空！");

        return this.selectByPrimaryKey(anId);
    }

    /**
     * 保存联系人流水信息
     * 
     * @param anCustMechContacterTmp
     * @return
     */
    public int saveCustMechContacterTmp(CustMechContacterTmp anCustMechContacterTmp, Long anId) {
        BTAssert.notNull(anId, "联系人流水编号不允许为空！");

        final CustMechContacterTmp tempCustMechContacterTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechContacterTmp, "没有找到对应的联系人流水信息！");

        tempCustMechContacterTmp.initModifyValue(anCustMechContacterTmp);
        return this.updateByPrimaryKey(tempCustMechContacterTmp);
    }

    /**
     * 添加联系人流水信息
     * 
     * @param anCustMechContacterTmp
     * @return
     */
    public int addCustMechContacterTmp(CustMechContacterTmp anCustMechContacterTmp) {
        BTAssert.notNull(anCustMechContacterTmp, "联系人流水信息编号不允许为空！");

        // anCustMechContacterTmp.initAddValue();
        return this.insert(anCustMechContacterTmp);
    }

    /**
     * 法人信息变更申请
     * 
     * @param anCustMechLawTmp
     * @return
     */
    public CustChangeApply addCustChangeApply(CustMechContacterTmp anCustMechContacterTmp) {
        BTAssert.notNull(anCustMechContacterTmp, "基本信息变更申请不能为空");

        anCustMechContacterTmp.initAddValue(CustomerConstants.TMP_TYPE_CHANGE);
        this.insert(anCustMechContacterTmp);

        // 发起变更申请
        CustChangeApply custChangeApply = changeApplyService.addChangeApply(anCustMechContacterTmp.getRefId(), CustomerConstants.ITEM_CONTACTER,
                String.valueOf(anCustMechContacterTmp.getId()));

        return custChangeApply;
    }

    @Override
    public void saveFormalData(String... anTmpIds) {

    }
}