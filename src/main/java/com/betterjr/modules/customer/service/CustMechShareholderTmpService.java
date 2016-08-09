package com.betterjr.modules.customer.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechShareholderTmpMapper;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustMechShareholderTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechShareholderTmpService extends BaseService<CustMechShareholderTmpMapper, CustMechShareholderTmp> implements IFormalDataService {
    @Resource
    private CustMechShareholderService shareholderService;

    @Resource
    private CustInsteadRecordService insteadRecordService;

    @Resource
    private CustChangeApplyService changeApplyService;

    /**
     * 查询公司股东流水信息
     * 
     * @param anCustNo
     * @return
     */
    public CustMechShareholderTmp findCustMechShareholderTmpByCustNo(Long anId) {
        BTAssert.notNull(anId, "公司股东流水信息编号不允许为空！");

        return this.selectByPrimaryKey(anId);
    }

    /**
     * 保存公司股东流水信息
     * 
     * @param anCustMechShareholderTmp
     * @return
     */
    public int saveCustMechShareholderTmp(CustMechShareholderTmp anCustMechShareholderTmp, Long anId) {
        BTAssert.notNull(anId, "公司股东流水编号不允许为空！");

        final CustMechShareholderTmp tempCustMechShareholderTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechShareholderTmp, "没有找到对应的公司股东流水信息！");

        tempCustMechShareholderTmp.initModifyValue(anCustMechShareholderTmp);
        return this.updateByPrimaryKey(tempCustMechShareholderTmp);
    }

    /**
     * 添加公司股东流水信息
     * 
     * @param anCustMechShareholderTmp
     * @return
     */
    public int addCustMechShareholderTmp(CustMechShareholderTmp anCustMechShareholderTmp) {
        BTAssert.notNull(anCustMechShareholderTmp, "公司股东流水信息编号不允许为空！");

        return this.insert(anCustMechShareholderTmp);
    }

    /**
     * 添加公司股东流水信息
     * 
     * @param anCustMechLawTmp
     * @return
     */
    public CustChangeApply addCustChangeApply(CustMechShareholderTmp anMechShareholderTmp) {
        BTAssert.notNull(anMechShareholderTmp, "基本信息变更申请不能为空");

        anMechShareholderTmp.initAddValue(CustomerConstants.TMP_TYPE_CHANGE);
        this.insert(anMechShareholderTmp);

        // 发起变更申请
        CustChangeApply custChangeApply = changeApplyService.addChangeApply(anMechShareholderTmp.getRefId(), CustomerConstants.ITEM_SHAREHOLDER,
                String.valueOf(anMechShareholderTmp.getId()));

        return custChangeApply;
    }

    /**
     * 回写正式表记录
     */
    @Override
    public void saveFormalData(String... anTmpIds) {

    }
    
    /**
     * 回写作废记录
     * @param anTmpIds
     */
    @Override
    public void saveCancelData(String... anTmpIds) {
        
    }
}