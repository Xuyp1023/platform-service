package com.betterjr.modules.customer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.data.SimpleDataEntity;
import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.DictUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustAndOperatorRelaService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustRelationMapper;
import com.betterjr.modules.customer.entity.CustRelation;
import com.betterjr.modules.sys.entity.DictItemInfo;

@Service
public class CustRelationService extends BaseService<CustRelationMapper, CustRelation> {

    @Autowired
    private CustAccountService custAccountService;

    @Autowired
    private CustRelationAuditService custRelationAuditService;

    @Autowired
    private CustAndOperatorRelaService custAndOperatorRelaService;

    /**
     * 开通保理融资业务状态
     * 
     * @param anCustNo
     * @return
     */
    public String findFactorStatus(Long anCustNo) {
        int relateIndex = 0;
        int refuseIndex = 0;
        BTAssert.notNull(anCustNo, "请选择机构");
        // 获取电子合同服务商信息
        List<DictItemInfo> anProviderDict = DictUtils.getDictList("ScfElecAgreementGroup");
        for (DictItemInfo anDictItem : anProviderDict) {
            CustRelation anCustRelation = findCustRelation(anCustNo, Long.valueOf(anDictItem.getItemValue()),
                    CustomerConstants.RELATE_TYPE_ELEC_CONTRACT);
            if (null == anCustRelation) {
                relateIndex++;
            }
        }
        // 获取保理机构信息
        List<String> anOperatorInnerRuleList = findOperatorInnerRuleList();
        List<DictItemInfo> anFactorDict = DictUtils.getDictList("ScfFactorGroup");
        for (String anRelateType : anOperatorInnerRuleList) {
            for (DictItemInfo anDictItem : anFactorDict) {
                CustRelation anCustRelation = findCustRelation(anCustNo, Long.valueOf(anDictItem.getItemValue()), anRelateType);
                if (null == anCustRelation) {
                    relateIndex++;
                }
                else {
                    if (BetterStringUtils.equals(anCustRelation.getBusinStatus(), CustomerConstants.RELATE_STATUS_REFUSE)) {
                        refuseIndex++;
                    }
                }
            }
        }

        // 当前状态:0-未开通保理融资业务
        if (relateIndex != 0 && refuseIndex == 0) {
            return CustomerConstants.FACTOR_STATUS_UNDO;
        }
        // 当前状态:1-可开通保理融资业务
        else if (relateIndex != 0 || refuseIndex != 0) {
            return CustomerConstants.FACTOR_STATUS_WAIT;
        }
        // 当前状态:2-已开通保理融资业务 relateIndex == 0 && refuseIndex == 0
        else {
            return CustomerConstants.FACTOR_STATUS_DONE;
        }
    }

    /**
     * 核心企业下拉列表查询,适用于供应商/经销商相关查询
     * 
     * @param anCustNo
     * @return
     */
    public List<SimpleDataEntity> queryCoreKeyAndValue(Long anCustNo) {
        List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anCustNo) {
            return result;
        }
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_CORE, CustomerConstants.RELATE_TYPE_SELLER_CORE });
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        for (CustRelation relation : this.selectByProperty(anMap)) {
            result.add(new SimpleDataEntity(relation.getRelateCustname(), String.valueOf(relation.getRelateCustno())));
        }
        return result;
    }

    /**
     * 保理机构下拉列表查询,适用于供应商/经销商/核心企业相关查询
     * 
     * @param anCustNo
     * @return
     */
    public List<SimpleDataEntity> queryFactorKeyAndValue(Long anCustNo) {
        List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anCustNo) {
            return result;
        }
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR, CustomerConstants.RELATE_TYPE_CORE_FACTOR,
                CustomerConstants.RELATE_TYPE_SELLER_FACTOR });
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        for (CustRelation relation : this.selectByProperty(anMap)) {
            result.add(new SimpleDataEntity(relation.getRelateCustname(), String.valueOf(relation.getRelateCustno())));
        }
        return result;
    }

    /**
     * 客户与电子合同服务商关系查询
     * 
     * @param anCustNo
     * @return
     */
    public List<SimpleDataEntity> queryProviderRelation(Long anCustNo) {
        List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anCustNo) {
            return result;
        }
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("relateType", CustomerConstants.RELATE_TYPE_ELEC_CONTRACT);
        for (CustRelation relation : this.selectByProperty(anMap)) {
            result.add(new SimpleDataEntity(relation.getRelateCustname(), String.valueOf(relation.getRelateCustno())));
        }
        return result;
    }

    /**
     * 客户与保理机构关系查询
     * 
     * @param anCustNo
     * @return
     */
    public List<SimpleDataEntity> queryFactorRelation(Long anCustNo) {
        List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anCustNo) {
            return result;
        }
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR, CustomerConstants.RELATE_TYPE_CORE_FACTOR,
                CustomerConstants.RELATE_TYPE_SELLER_FACTOR });
        for (CustRelation relation : this.selectByProperty(anMap)) {
            result.add(new SimpleDataEntity(relation.getRelateCustname(), String.valueOf(relation.getRelateCustno())));
        }
        return result;
    }

    /**
     * 客户白名单受理列表
     * 
     * @param anBusinStatus
     * @param anFlag
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<CustRelation> queryRelationAccept(String anBusinStatus, String anFlag, int anPageNum, int anPageSize) {
        Long factorNo = findCustNoByOperator();
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("relateCustno", factorNo);
        anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR, CustomerConstants.RELATE_TYPE_CORE_FACTOR,
                CustomerConstants.RELATE_TYPE_SELLER_FACTOR });
        if (BetterStringUtils.isBlank(anBusinStatus) == true) {
            anMap.put("businStatus", new String[] { CustomerConstants.RELATE_STATUS_APPLY, CustomerConstants.RELATE_STATUS_ACCEPT });
        }
        else {
            anMap.put("businStatus", anBusinStatus);
        }
        return this.selectPropertyByPage(CustRelation.class, anMap, anPageNum, anPageSize, "1".equals(anFlag));
    }

    /**
     * 客户白名单受理
     * 
     * @param anId
     * @param anAuditOpinion
     * @return
     */
    public CustRelation saveRelationAccept(Long anId, String anAuditOpinion) {
        BTAssert.notNull(anId, "请选择客户");
        BTAssert.notNull(anAuditOpinion, "请填写处理意见");
        CustRelation anCustRelation = this.selectByPrimaryKey(anId);
        if (BetterStringUtils.equals(anCustRelation.getBusinStatus(), "1") == false) {
            logger.warn("当前状态不允许执行受理操作");
            throw new BytterTradeException("当前状态不允许执行受理操作");
        }
        anCustRelation.setBusinStatus(CustomerConstants.RELATE_STATUS_ACCEPT);
        anCustRelation.setLastStatus(CustomerConstants.RELATE_STATUS_ACCEPT);
        this.updateByPrimaryKeySelective(anCustRelation);
        custRelationAuditService.addAuditCustRelation(anCustRelation, anAuditOpinion, "开通保理融资业务受理");
        return anCustRelation;
    }

    /**
     * 客户白名单受理-驳回
     * 
     * @param anId
     * @param anAuditOpinion
     * @return
     */
    public CustRelation saveRefuseAcceptRelation(Long anId, String anAuditOpinion) {
        BTAssert.notNull(anId, "请选客户");
        BTAssert.notNull(anAuditOpinion, "请填写处理意见");
        CustRelation anCustRelation = this.selectByPrimaryKey(anId);
        if (BetterStringUtils.equals(anCustRelation.getBusinStatus(), "1") == false) {
            logger.warn("当前状态不允许执行受理操作");
            throw new BytterTradeException("当前状态不允许执行受理操作");
        }
        anCustRelation.setBusinStatus(CustomerConstants.RELATE_STATUS_REFUSE);
        anCustRelation.setLastStatus(CustomerConstants.RELATE_STATUS_REFUSE);
        this.updateByPrimaryKeySelective(anCustRelation);
        custRelationAuditService.addRefuseCustRelation(anCustRelation, anAuditOpinion, "开通保理融资业务受理");
        return anCustRelation;
    }

    /**
     * 客户白名单审批列表
     * 
     * @param anBusinStatus
     * @param anFlag
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<CustRelation> queryRelationAudit(String anBusinStatus, String anFlag, int anPageNum, int anPageSize) {
        Long operId = UserUtils.getOperatorInfo().getId();
        String operOrg = UserUtils.getOperatorInfo().getOperOrg();
        Long factorNo = Collections3.getFirst(custAndOperatorRelaService.findCustNoList(operId, operOrg));
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("relateCustno", factorNo);
        anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR, CustomerConstants.RELATE_TYPE_CORE_FACTOR,
                CustomerConstants.RELATE_TYPE_SELLER_FACTOR });
        if (BetterStringUtils.isBlank(anBusinStatus) == true) {
            anMap.put("businStatus", new String[] { CustomerConstants.RELATE_STATUS_ACCEPT, CustomerConstants.RELATE_STATUS_AUDIT });
        }
        else {
            anMap.put("businStatus", anBusinStatus);
        }
        return this.selectPropertyByPage(CustRelation.class, anMap, anPageNum, anPageSize, "1".equals(anFlag));
    }

    /**
     * 客户白名单审批
     * 
     * @param anId
     * @param anAuditOpinion
     * @return
     */
    public CustRelation saveRelationAudit(Long anId, String anAuditOpinion) {
        BTAssert.notNull(anId, "请选客户");
        BTAssert.notNull(anAuditOpinion, "请填写处理意见");
        CustRelation anCustRelation = this.selectByPrimaryKey(anId);
        if (BetterStringUtils.equals(anCustRelation.getBusinStatus(), "2") == false) {
            logger.warn("当前状态不允许执行审批操作");
            throw new BytterTradeException("当前状态不允许执行审批操作");
        }
        anCustRelation.setBusinStatus(CustomerConstants.RELATE_STATUS_AUDIT);
        anCustRelation.setLastStatus(CustomerConstants.RELATE_STATUS_AUDIT);
        this.updateByPrimaryKeySelective(anCustRelation);
        custRelationAuditService.addAuditCustRelation(anCustRelation, anAuditOpinion, "开通保理融资业务审批");
        return anCustRelation;
    }

    /**
     * 客户白名单审批-驳回
     * 
     * @param anId
     * @param anAuditOpinion
     * @return
     */
    public CustRelation saveRefuseAuditRelation(Long anId, String anAuditOpinion) {
        BTAssert.notNull(anId, "请选客户");
        BTAssert.notNull(anAuditOpinion, "请填写处理意见");
        CustRelation anCustRelation = this.selectByPrimaryKey(anId);
        if (BetterStringUtils.equals(anCustRelation.getBusinStatus(), "2") == false) {
            logger.warn("当前状态不允许执行审批操作");
            throw new BytterTradeException("当前状态不允许执行审批操作");
        }
        anCustRelation.setBusinStatus(CustomerConstants.RELATE_STATUS_REFUSE);
        anCustRelation.setLastStatus(CustomerConstants.RELATE_STATUS_REFUSE);
        this.updateByPrimaryKeySelective(anCustRelation);
        custRelationAuditService.addRefuseCustRelation(anCustRelation, anAuditOpinion, "开通保理融资业务审批");
        return anCustRelation;
    }

    /**
     * 开通保理融资业务申请
     * 
     * @param anCustNo
     * @param anProviderCustList
     * @param anFactorCustList
     * @param anPostscript
     * @return
     */
    public String saveCustRelation(Long anCustNo, String anProviderCustList, String anFactorCustList, String anPostscript) {
        try {
            BTAssert.notNull(anCustNo, "请选择操作机构");
            BTAssert.notNull(anProviderCustList, "请选择电子合同服务商");
            BTAssert.notNull(anFactorCustList, "请选择保理机构");
            BTAssert.notNull(anPostscript, "请填写附言");
            CustInfo anCustInfo = custAccountService.selectByPrimaryKey(anCustNo);
            if (anCustInfo.getIdentValid() == false) {
                logger.warn("客户未进行实名验证,不允许提交");
                throw new BytterTradeException(40001, "客户未进行实名验证,不允许提交");
            }
            saveProviderRelation(anCustInfo, anProviderCustList);
            saveFactorRelation(anCustInfo, anFactorCustList, anPostscript);
        }
        catch (Exception e) {
            logger.error("开通保理融资业务申请", e);
            return CustomerConstants.FACTOR_STATUS_FAILD;
        }
        return CustomerConstants.FACTOR_STATUS_SUCCESS;
    }

    private void saveProviderRelation(CustInfo anCustInfo, String anProviderCustList) {
        String[] anProviderCustNoList = anProviderCustList.split(",");
        for (String anProviderCustNo : anProviderCustNoList) {
            Long anRelateCustNo = Long.valueOf(anProviderCustNo);
            // 检查是否已存在关联关系
            CustRelation anCustRelation = findCustRelation(anCustInfo.getCustNo(), anRelateCustNo, CustomerConstants.RELATE_TYPE_ELEC_CONTRACT);
            if (null == anCustRelation) {
                addCustRelation(anCustInfo, anRelateCustNo, CustomerConstants.RELATE_TYPE_ELEC_CONTRACT, CustomerConstants.RELATE_STATUS_AUDIT);
            }
        }
    }

    private void saveFactorRelation(CustInfo anCustInfo, String anFactorCustList, String anPostscript) {
        String[] anFactorCustNoList = anFactorCustList.split(",");
        List<String> anOperatorInnerRuleList = findOperatorInnerRuleList();
        for (String anFactorCustNo : anFactorCustNoList) {
            Long anRelateCustNo = Long.valueOf(anFactorCustNo);
            for (String anRelateType : anOperatorInnerRuleList) {
                // 检查是否已存在关联关系
                CustRelation anTempCustRelation = findCustRelation(anCustInfo.getCustNo(), anRelateCustNo, anRelateType);
                if (null == anTempCustRelation) {
                    CustRelation anCustRelation = addCustRelation(anCustInfo, anRelateCustNo, anRelateType, CustomerConstants.RELATE_STATUS_APPLY);
                    custRelationAuditService.addAuditCustRelation(anCustRelation, anPostscript, "开通保理融资业务申请");
                }
                else {
                    if (BetterStringUtils.equals(anTempCustRelation.getBusinStatus(), CustomerConstants.RELATE_STATUS_REFUSE) == true) {
                        anTempCustRelation.setBusinStatus(CustomerConstants.RELATE_STATUS_APPLY);
                        anTempCustRelation.setLastStatus(CustomerConstants.RELATE_STATUS_APPLY);
                        this.updateByPrimaryKeySelective(anTempCustRelation);
                        custRelationAuditService.addAuditCustRelation(anTempCustRelation, anPostscript, "开通保理融资业务申请");
                    }
                }
            }
        }
    }

    private CustRelation addCustRelation(CustInfo anCustInfo, Long anRelateCustNo, String anRelateType, String anBusinStatus) {
        CustRelation relation = new CustRelation();
        relation.initAddValue();
        relation.setCustNo(anCustInfo.getCustNo());
        relation.setCustName(anCustInfo.getCustName());
        relation.setCustType(anCustInfo.getCustType());
        relation.setRelateCustno(anRelateCustNo);
        relation.setRelateCustname(custAccountService.queryCustName(anRelateCustNo));
        relation.setRelateType(anRelateType);
        relation.setBusinStatus(anBusinStatus);
        relation.setLastStatus(relation.getBusinStatus());
        this.insert(relation);
        return relation;
    }

    private CustRelation findCustRelation(Long anCustNo, Long relateCustno, String anRelateType) {
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("relateCustno", relateCustno);
        anMap.put("relateType", anRelateType);
        return Collections3.getFirst(this.selectByProperty(anMap));
    }

    private Long findCustNoByOperator() {
        Long operId = UserUtils.getOperatorInfo().getId();
        String operOrg = UserUtils.getOperatorInfo().getOperOrg();
        return Collections3.getFirst(custAndOperatorRelaService.findCustNoList(operId, operOrg));
    }

    private List<String> findOperatorInnerRuleList() {
        List<String> anRuleList = new ArrayList<String>();
        if (UserUtils.supplierUser()) {
            anRuleList.add(CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR);
        }
        if (UserUtils.coreUser()) {
            anRuleList.add(CustomerConstants.RELATE_TYPE_CORE_FACTOR);
        }
        if (UserUtils.sellerUser()) {
            anRuleList.add(CustomerConstants.RELATE_TYPE_SELLER_FACTOR);
        }
        return anRuleList;
    }

}
