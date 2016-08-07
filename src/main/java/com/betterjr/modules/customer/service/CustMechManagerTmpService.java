package com.betterjr.modules.customer.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechManagerTmpMapper;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustMechManagerTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;
import com.betterjr.modules.customer.helper.VersionHelper;

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
     * 删除临时列表
     * 
     * @param anCustNo
     * @return
     */
    public Boolean saveDelCustMechManagerTmpList(Long anCustNo) {

        return Boolean.FALSE;
    }

    /**
     * 添加一个变更申请
     * 
     * @return
     */
    public CustChangeApply addChangeApply(Map<String, Object> anParam, Long anCustNo) {
        BTAssert.notNull(anCustNo, "公司编号不允许为空！");
        BTAssert.notNull(anParam, "参数不允许为空！");

        String tmpChangeIds = (String) anParam.get("changeIds");

        // 根据 changeIds 处理状态
        String[] strChangeIds = BetterStringUtils.split(tmpChangeIds, ",");

        Long[] changeIds = new Long[strChangeIds.length];
        for (int i = 0; i < strChangeIds.length; i++) {
            changeIds[i] = Long.valueOf(strChangeIds[i]);
        }

        // 检查是否还有不包含在列表里面的未处理项目,如果有,发生错误
        Collection<CustMechManagerTmp> managerTmpes = queryCustMechManagerTmpList(anCustNo);

        if (checkMatch(changeIds, managerTmpes) == false) {
            throw new BytterTradeException("代录编号列表不正确,请检查.");
        }

        for (Long changeId : changeIds) {
            saveCustMechManagerTmpStatus(changeId, CustomerConstants.TMP_STATUS_USEING);
        }

        // 发起变更申请
        CustChangeApply changeApply = changeApplyService.addChangeApply(anCustNo, CustomerConstants.ITEM_MANAGER, tmpChangeIds);

        return changeApply;
    }

    private boolean checkMatch(Long[] anChangeIds, Collection<CustMechManagerTmp> anManagerTmpes) {
        if (anChangeIds.length != anManagerTmpes.size()) {
            return false;
        }
        Set<Long> tempSet = new HashSet<>();

        for (CustMechManagerTmp managerTmp : anManagerTmpes) {
            Long id = managerTmp.getId();
            boolean flag = false;
            for (Long changeId : anChangeIds) {
                if (id.equals(changeId) == true) {
                    flag = true;
                    tempSet.add(changeId);
                }
            }
        }

        if (tempSet.size() == anChangeIds.length) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 加载未使用的流水列表
     * 
     * @param anCustNo
     * @return
     */
    public Collection<CustMechManagerTmp> queryCustMechManagerTmpList(Long anCustNo) {
        Map<String, Object> conditionMap = new HashMap<>();

        conditionMap.put("custNo", anCustNo);
        conditionMap.put("businStatus", CustomerConstants.TMP_STATUS_NEW);

        return this.selectByProperty(conditionMap);
    }

    /**
     * 检查是否有未使用的流水列表
     * 
     * @param anCustNo
     * @return
     */
    public Boolean checkCustMechManagerTmpList(Long anCustNo) {

        return Boolean.FALSE;
    }

    /**
     * 添加公司高管流水信息
     * 
     * @param anCustMechManagerTmp
     * @return
     */
    public CustMechManagerTmp addCustMechManagerTmp(CustMechManagerTmp anCustMechManagerTmp, String anTmpType) {
        BTAssert.notNull(anCustMechManagerTmp, "公司高管流水信息不允许为空！");
        Long custNo = anCustMechManagerTmp.getCustNo();
        Long version = VersionHelper.generateVersion(this.mapper, custNo);

        anCustMechManagerTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, anTmpType, version);
        this.insert(anCustMechManagerTmp);
        return anCustMechManagerTmp;
    }

    /**
     * 修改公司高管流水信息
     * 
     * @param anCustMechManagerTmp
     * @return
     */
    public int saveCustMechManagerTmp(CustMechManagerTmp anCustMechManagerTmp) {
        BTAssert.notNull(anCustMechManagerTmp, "公司高管流水信息不允许为空！");
        Long custNo = anCustMechManagerTmp.getCustNo();
        Long version = VersionHelper.generateVersion(this.mapper, custNo);

        // anCustMechManagerTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW, version);
        return this.updateByPrimaryKeySelective(anCustMechManagerTmp);
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
        CustChangeApply custChangeApply = changeApplyService.addChangeApply(anCustMechManagerTmp.getCustNo(), CustomerConstants.ITEM_MANAGER,
                String.valueOf(anCustMechManagerTmp.getId()));

        return custChangeApply;
    }

    /**
     * 保存状态
     * 
     * @param anId
     * @param anBusinStatus
     * @return
     */
    public CustMechManagerTmp saveCustMechManagerTmpStatus(Long anId, String anBusinStatus) {
        CustMechManagerTmp managerTmp = this.selectByPrimaryKey(anId);

        managerTmp.setBusinStatus(anBusinStatus);

        this.updateByPrimaryKeySelective(managerTmp);

        return managerTmp;
    }

    @Override
    public void saveFormalData(String... anTmpIds) {

    }
}