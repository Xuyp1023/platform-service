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
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustMechManagerTmpMapper;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustMechManager;
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
    public CustMechManagerTmp findCustMechManagerTmp(Long anId) {
        BTAssert.notNull(anId, "公司高管流水信息编号不允许为空！");

        return this.selectByPrimaryKey(anId);
    }

    /**
     * 添加新增变更记录
     * 
     * @param anRefId
     * @return
     */
    public CustMechManagerTmp addChangeManagerTmp(CustMechManagerTmp anCustMechManagerTmp) {
        BTAssert.notNull(anCustMechManagerTmp, "公司高管流水信息不允许为空！");

        final Long refId = anCustMechManagerTmp.getRefId();
        BTAssert.isNull(refId, "引用编号需要为空!");

        anCustMechManagerTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW);
        anCustMechManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_ADD);

        return addCustMechManagerTmp(anCustMechManagerTmp, CustomerConstants.TMP_TYPE_CHANGE);
    }

    /**
     * 添加修改变更记录
     * 
     * @param anRefId
     * @return
     */
    public CustMechManagerTmp saveSaveChangeManagerTmp(CustMechManagerTmp anCustMechManagerTmp) {
        BTAssert.notNull(anCustMechManagerTmp, "公司高管流水信息不允许为空！");

        final Long refId = anCustMechManagerTmp.getRefId();
        BTAssert.notNull(refId, "引用编号不允许为空!");

        // 检查变更引用记录是否存在
        CustMechManager manager = managerService.findCustMechManager(refId);
        BTAssert.notNull(manager, "没有找到引用的记录!");

        CustMechManagerTmp tempManagerTmp = findCustMechManagerTmpByRefId(refId, CustomerConstants.TMP_TYPE_CHANGE);
        if (tempManagerTmp == null) {
            anCustMechManagerTmp.initAddValue(manager, CustomerConstants.TMP_STATUS_NEW);
            anCustMechManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            return addCustMechManagerTmp(anCustMechManagerTmp, CustomerConstants.TMP_TYPE_CHANGE);
        }
        else {
            tempManagerTmp.initModifyValue(anCustMechManagerTmp);
            tempManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            return saveCustMechManagerTmp(tempManagerTmp, tempManagerTmp.getId());
        }
    }

    /**
     * 添加删除变更记录
     * 
     * @param anCustNo
     * @return
     */
    public CustMechManagerTmp saveDelChangeManagerTmp(Long anRefId) {
        BTAssert.notNull(anRefId, "公司高管号不允许为空！");

        // 检查变更引用记录是否存在
        CustMechManager manager = managerService.findCustMechManager(anRefId);
        BTAssert.notNull(manager, "没有找到引用的记录!");

        // 创建一个新的流水(如果已经存在变更流水,或者删除流水 则在原有基础上修改)
        CustMechManagerTmp managerTmp = findCustMechManagerTmpByRefId(anRefId, CustomerConstants.TMP_TYPE_CHANGE);
        if (managerTmp == null) {
            managerTmp = new CustMechManagerTmp();
            managerTmp.initAddValue(manager, CustomerConstants.TMP_STATUS_NEW);
            managerTmp.setRefId(anRefId);
            managerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return addCustMechManagerTmp(managerTmp, CustomerConstants.TMP_TYPE_CHANGE);
        }
        else {
            managerTmp.initAddValue(manager, CustomerConstants.TMP_STATUS_NEW);
            managerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return saveCustMechManagerTmp(managerTmp, managerTmp.getId());
        }
    }

    /**
     * 撤销变更记录
     * 
     * @param anId
     * @return
     */
    public int saveCancelChangeManagerTmp(Long anId) {
        // 检查是否可撤销
        CustMechManagerTmp managerTmp = this.findCustMechManagerTmp(anId);

        Long tmpVersion = managerTmp.getVersion();
        Long maxVersion = VersionHelper.generateVersion(this.mapper, managerTmp.getCustNo());

        if (tmpVersion.equals(maxVersion) == false) {
            throw new BytterTradeException("流水信息不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(managerTmp.getTmpType(), CustomerConstants.TMP_TYPE_CHANGE) == false) {
            throw new BytterTradeException("流水信息类型不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(managerTmp.getBusinStatus(), CustomerConstants.TMP_STATUS_NEW) == false) {
            throw new BytterTradeException("流水信息状态不正确,不可撤销.");
        }

        return this.deleteByPrimaryKey(anId);
    }

    /**
     * 加载变更列表中的流水列表
     * 
     * @param anCustNo
     * @return
     */
    public Collection<CustMechManagerTmp> queryCustMechManagerTmpByChangeApply(Long anApplyId) {
        BTAssert.notNull(anApplyId, "公司编号不允许为空！");
        CustChangeApply changeApply = changeApplyService.findChangeApply(anApplyId);
        String[] tmpIds = BetterStringUtils.split(changeApply.getTmpIds(), ",");

        Map<String, Object> conditionMap = new HashMap<>();

        conditionMap.put("id", tmpIds);

        return this.selectByProperty(conditionMap);
    }

    /**
     * 
     * @param anRefId
     * @param anTmpType
     * @return
     */
    private CustMechManagerTmp findCustMechManagerTmpByRefId(Long anRefId, String anTmpType) {
        Map<String, Object> conditionMap = new HashMap<>();

        conditionMap.put("refId", anRefId);
        conditionMap.put("tmpType", anTmpType);
        conditionMap.put("businStatus", CustomerConstants.TMP_STATUS_NEW);

        return Collections3.getFirst(this.selectByProperty(conditionMap));
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
        if (checkMatch(changeIds, anCustNo, CustomerConstants.TMP_TYPE_CHANGE) == false) {
            throw new BytterTradeException("代录编号列表不正确,请检查.");
        }

        for (Long id : changeIds) {
            saveCustMechManagerTmpStatus(id, CustomerConstants.TMP_STATUS_USEING);
        }

        // 发起变更申请
        CustChangeApply changeApply = changeApplyService.addChangeApply(anCustNo, CustomerConstants.ITEM_MANAGER, tmpChangeIds);

        return changeApply;
    }

    /**
     * 修改一个变更申请
     * 
     * @return
     */
    public CustChangeApply saveChangeApply(Map<String, Object> anParam, Long anApplyId) {
        BTAssert.notNull(anApplyId, "变更申请编号不允许为空！");
        BTAssert.notNull(anParam, "参数不允许为空！");

        CustChangeApply changeApply = checkChangeApply(anApplyId);

        Long custNo = changeApply.getCustNo();

        String tmpChangeIds = (String) anParam.get("changeIds");

        // 根据 changeIds 处理状态
        String[] strChangeIds = BetterStringUtils.split(tmpChangeIds, ",");

        Long[] changeIds = new Long[strChangeIds.length];
        for (int i = 0; i < strChangeIds.length; i++) {
            changeIds[i] = Long.valueOf(strChangeIds[i]);
        }

        // 检查是否还有不包含在列表里面的未处理项目,如果有,发生错误
        if (checkMatch(changeIds, custNo, CustomerConstants.TMP_TYPE_CHANGE) == false) {
            throw new BytterTradeException("代录编号列表不正确,请检查.");
        }

        for (Long id : changeIds) {
            saveCustMechManagerTmpStatus(id, CustomerConstants.TMP_STATUS_USEING);
        }

        // 重新发起变更申请
        changeApplyService.saveChangeApplyStatus(anApplyId, CustomerConstants.CHANGE_APPLY_STATUS_NEW);

        return changeApply;
    }

    /**
     * 加载未使用的流水列表
     * 
     * @param anCustNo
     * @return
     */
    public Collection<CustMechManagerTmp> queryNewCustMechManagerTmp(Long anCustNo, String anTmpType) {
        Map<String, Object> conditionMap = new HashMap<>();

        Long version = VersionHelper.generateVersion(this.mapper, anCustNo);

        conditionMap.put("custNo", anCustNo);
        conditionMap.put("version", version);
        conditionMap.put("tmpType", anTmpType);
        conditionMap.put("businStatus", CustomerConstants.TMP_STATUS_NEW);

        return this.selectByProperty(conditionMap);
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
     * @param anId
     * @return
     */
    public CustMechManagerTmp saveCustMechManagerTmp(CustMechManagerTmp anCustMechManagerTmp, Long anId) {
        BTAssert.notNull(anCustMechManagerTmp, "公司高管流水信息不允许为空！");
        BTAssert.notNull(anId, "公司高管流水编号不允许为空！");

        Long custNo = anCustMechManagerTmp.getCustNo();
        Long version = VersionHelper.generateVersion(this.mapper, custNo);

        CustMechManagerTmp tempCustMechManagerTmp = this.selectByPrimaryKey(anId);

        tempCustMechManagerTmp.initModifyValue(anCustMechManagerTmp, CustomerConstants.TMP_STATUS_NEW, version);

        this.updateByPrimaryKeySelective(tempCustMechManagerTmp);

        return tempCustMechManagerTmp;
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

    public CustMechManagerTmp addInsteadManagerTmp(CustMechManagerTmp anCustMechManagerTmp) {
        BTAssert.notNull(anCustMechManagerTmp, "公司高管流水信息不允许为空！");

        final Long refId = anCustMechManagerTmp.getRefId();
        BTAssert.isNull(refId, "引用编号需要为空!");

        anCustMechManagerTmp.initAddValue(CustomerConstants.TMP_STATUS_NEW);
        anCustMechManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_ADD);

        return addCustMechManagerTmp(anCustMechManagerTmp, CustomerConstants.TMP_TYPE_INSTEAD);
    }

    public CustMechManagerTmp saveSaveInsteadManagerTmp(CustMechManagerTmp anCustMechManagerTmp, Long anId) {
        BTAssert.notNull(anCustMechManagerTmp, "公司高管流水信息不允许为空！");

        final Long refId = anCustMechManagerTmp.getRefId();
        BTAssert.notNull(refId, "引用编号不允许为空!");

        // 检查变更引用记录是否存在
        CustMechManager manager = managerService.findCustMechManager(refId);
        BTAssert.notNull(manager, "没有找到引用的记录!");

        CustMechManagerTmp tempManagerTmp = findCustMechManagerTmpByRefId(refId, CustomerConstants.TMP_TYPE_INSTEAD);
        if (tempManagerTmp == null) {
            anCustMechManagerTmp.initAddValue(manager, CustomerConstants.TMP_STATUS_NEW);
            anCustMechManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            return addCustMechManagerTmp(anCustMechManagerTmp, CustomerConstants.TMP_TYPE_INSTEAD);
        }
        else {
            tempManagerTmp.initModifyValue(anCustMechManagerTmp);
            tempManagerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_MODIFY);
            return saveCustMechManagerTmp(tempManagerTmp, tempManagerTmp.getId());
        }
    }

    public CustMechManagerTmp saveDelInsteadManagerTmp(Long anRefId) {
        BTAssert.notNull(anRefId, "公司高管号不允许为空！");

        // 检查变更引用记录是否存在
        CustMechManager manager = managerService.findCustMechManager(anRefId);
        BTAssert.notNull(manager, "没有找到引用的记录!");

        // 创建一个新的流水(如果已经存在变更流水,或者删除流水 则在原有基础上修改)
        CustMechManagerTmp managerTmp = findCustMechManagerTmpByRefId(anRefId, CustomerConstants.TMP_TYPE_INSTEAD);
        if (managerTmp == null) {
            managerTmp = new CustMechManagerTmp();
            managerTmp.initAddValue(manager, CustomerConstants.TMP_STATUS_NEW);
            managerTmp.setRefId(anRefId);
            managerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return addCustMechManagerTmp(managerTmp, CustomerConstants.TMP_TYPE_INSTEAD);
        }
        else {
            managerTmp.initAddValue(manager, CustomerConstants.TMP_STATUS_NEW);
            managerTmp.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_DELETE);
            return saveCustMechManagerTmp(managerTmp, managerTmp.getId());
        }
    }

    public int saveCancelInsteadManagerTmp(Long anId) {
        // 检查是否可撤销
        CustMechManagerTmp managerTmp = this.findCustMechManagerTmp(anId);

        Long tmpVersion = managerTmp.getVersion();
        Long maxVersion = VersionHelper.generateVersion(this.mapper, managerTmp.getCustNo());

        if (tmpVersion.equals(maxVersion) == false) {
            throw new BytterTradeException("流水信息不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(managerTmp.getTmpType(), CustomerConstants.TMP_TYPE_INSTEAD) == false) {
            throw new BytterTradeException("流水信息类型不正确,不可撤销.");
        }

        if (BetterStringUtils.equals(managerTmp.getBusinStatus(), CustomerConstants.TMP_STATUS_NEW) == false) {
            throw new BytterTradeException("流水信息状态不正确,不可撤销.");
        }

        return this.deleteByPrimaryKey(anId);
    }

    /**
     * 检查并返回变更申请
     * 
     * @param anApplyId
     * @return
     */
    public CustChangeApply checkChangeApply(Long anApplyId, String... anBusinStatus) {
        BTAssert.notNull(anApplyId, "变更申请-编号 不能为空");
        // 查询 变更申请
        CustChangeApply changeApply = changeApplyService.findChangeApply(anApplyId);
        if (BetterStringUtils.equals(changeApply.getChangeItem(), CustomerConstants.ITEM_MANAGER) == false) {
            throw new BytterTradeException(20074, "");
        }
        return changeApply;
    }

    /**
     * 检查是否匹配
     * 
     * @param anChangeIds
     * @param anCustNo
     * @return
     */
    private boolean checkMatch(Long[] anChangeIds, Long anCustNo, String anTmpType) {
        Collection<CustMechManagerTmp> managerTmps = queryNewCustMechManagerTmp(anCustNo, anTmpType);
        if (anChangeIds.length != managerTmps.size()) {
            return false;
        }
        Set<Long> tempSet = new HashSet<>();

        for (CustMechManagerTmp managerTmp : managerTmps) {
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
     * 回写正式数据
     */
    @Override
    public void saveFormalData(String... anTmpIds) {
        for (String tempTmpId : anTmpIds) {
            Long tmpId = Long.valueOf(tempTmpId);

            CustMechManagerTmp managerTmp = this.findCustMechManagerTmp(tmpId);

            BTAssert.notNull(managerTmp, "没有找到对应的高管临时流水!");

            String tmpOperType = managerTmp.getTmpOperType();
            switch (tmpOperType) {
            case CustomerConstants.TMP_OPER_TYPE_ADD:
                managerService.addCustMechManager(managerTmp);
                break;
            case CustomerConstants.TMP_OPER_TYPE_DELETE:
                managerService.deleteByPrimaryKey(managerTmp.getRefId());
                break;
            case CustomerConstants.TMP_OPER_TYPE_MODIFY:
                managerService.saveCustMechManager(managerTmp);
                break;
            case CustomerConstants.TMP_OPER_TYPE_NORMAL: // 不作处理
            default:
                break;
            }

            saveCustMechManagerTmpStatus(tmpId, CustomerConstants.TMP_STATUS_USED);
        }
    }

    /**
     * 回写作废记录
     * 
     * @param anTmpIds
     */
    @Override
    public void saveCancelData(String... anTmpIds) {

    }

    public Object addInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object saveInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId) {
        // TODO Auto-generated method stub
        return null;
    }
}