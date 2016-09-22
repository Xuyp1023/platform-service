package com.betterjr.modules.customer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.data.SimpleDataEntity;
import com.betterjr.common.data.WebServiceErrorCode;
import com.betterjr.common.exception.BytterTradeException;

import com.betterjr.common.mapper.BeanMapper;

import com.betterjr.common.exception.BytterWebServiceException;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.DictUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustAndOperatorRelaService;
import com.betterjr.modules.account.service.CustCertService;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.dao.CustRelationMapper;
import com.betterjr.modules.customer.data.CustRelationData;
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

    @Resource
    private CustCertService custCertService;

    /**
     * 开通保理融资业务状态
     *
     * @param anCustNo
     * @return
     */
    public String findFactorStatus(final Long anCustNo) {
        int relateIndex = 0;
        int refuseIndex = 0;
        BTAssert.notNull(anCustNo, "请选择机构");
        // 获取电子合同服务商信息
        final List<DictItemInfo> anProviderDict = DictUtils.getDictList("ScfElecAgreementGroup");
        for (final DictItemInfo anDictItem : anProviderDict) {
            final CustRelation anCustRelation = findCustRelation(anCustNo, Long.valueOf(anDictItem.getItemValue()),
                    CustomerConstants.RELATE_TYPE_ELEC_CONTRACT);
            if (null == anCustRelation) {
                relateIndex++;
            }
        }
        // 获取保理机构信息
        final List<String> anOperatorInnerRuleList = findOperatorInnerRuleList();
        final List<DictItemInfo> anFactorDict = DictUtils.getDictList("ScfFactorGroup");
        for (final String anRelateType : anOperatorInnerRuleList) {
            for (final DictItemInfo anDictItem : anFactorDict) {
                final CustRelation anCustRelation = findCustRelation(anCustNo, Long.valueOf(anDictItem.getItemValue()), anRelateType);
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
    public List<SimpleDataEntity> queryCoreKeyAndValue(final Long anCustNo) {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anCustNo) {
            return result;
        }
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_CORE, CustomerConstants.RELATE_TYPE_SELLER_CORE });
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        for (final CustRelation relation : this.selectByProperty(anMap)) {
            result.add(new SimpleDataEntity(relation.getRelateCustname(), String.valueOf(relation.getRelateCustno())));
        }
        return result;
    }

    /**
     * 供应商下拉列表查询,使用于核心企业查询
     *
     * @param anCoreCustNo
     * @return
     */
    public List<SimpleDataEntity> querySupplierByCore(final Long anCoreCustNo) {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anCoreCustNo) {
            return result;
        }
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("relateCustno", anCoreCustNo);
        anMap.put("relateType", CustomerConstants.RELATE_TYPE_SUPPLIER_CORE);
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        for (final CustRelation relation : this.selectByProperty(anMap)) {
            result.add(new SimpleDataEntity(relation.getCustName(), String.valueOf(relation.getCustNo())));
        }
        return result;
    }

    /**
     * 经销商下拉列表查询,使用于核心企业查询
     *
     * @param anCoreCustNo
     * @return
     */
    public List<SimpleDataEntity> querySellerByCore(final Long anCoreCustNo) {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anCoreCustNo) {
            return result;
        }
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("relateCustno", anCoreCustNo);
        anMap.put("relateType", CustomerConstants.RELATE_TYPE_SELLER_CORE);
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        for (final CustRelation relation : this.selectByProperty(anMap)) {
            result.add(new SimpleDataEntity(relation.getCustName(), String.valueOf(relation.getCustNo())));
        }
        return result;
    }

    /**
     * 保理公司与核心企业关系查询
     *
     * @param anFactorNo
     * @return
     */
    public List<SimpleDataEntity> queryFactorCoreRelation(final Long anFactorNo) {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anFactorNo) {
            return result;
        }
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("relateCustno", anFactorNo);
        anMap.put("relateType", CustomerConstants.RELATE_TYPE_CORE_FACTOR);
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        for (final CustRelation relation : this.selectByProperty(anMap)) {
            result.add(new SimpleDataEntity(relation.getCustName(), String.valueOf(relation.getCustNo())));
        }
        return result;
    }

    /**
     * 保理机构下拉列表查询,适用于供应商/经销商/核心企业相关查询
     *
     * @param anCustNo
     * @return
     */
    public List<SimpleDataEntity> queryFactorKeyAndValue(final Long anCustNo) {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anCustNo) {
            return result;
        }
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR, CustomerConstants.RELATE_TYPE_CORE_FACTOR,
                CustomerConstants.RELATE_TYPE_SELLER_FACTOR });
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        for (final CustRelation relation : this.selectByProperty(anMap)) {
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
    public List<SimpleDataEntity> queryProviderRelation(final Long anCustNo) {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anCustNo) {
            return result;
        }
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("relateType", CustomerConstants.RELATE_TYPE_ELEC_CONTRACT);
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        for (final CustRelation relation : this.selectByProperty(anMap)) {
            result.add(new SimpleDataEntity(relation.getRelateCustname(), String.valueOf(relation.getRelateCustno())));
        }
        return result;
    }

    /**
     * 客户关系查询
     *
     * @param anCustNo
     * @return
     */
    public List<SimpleDataEntity> queryCustRelation(final Long anCustNo) {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anCustNo) {
            return result;
        }
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        for (final CustRelation relation : this.selectByProperty(anMap)) {
            final SimpleDataEntity entity = new SimpleDataEntity(relation.getRelateCustname(), String.valueOf(relation.getRelateCustno()));
            if (!result.contains(entity)) {
                result.add(entity);
            }
        }
        anMap = new HashMap<String, Object>();
        anMap.put("relateCustno", anCustNo);
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        for (final CustRelation relation : this.selectByProperty(anMap)) {
            final SimpleDataEntity entity = new SimpleDataEntity(relation.getCustName(), String.valueOf(relation.getCustNo()));
            if (!result.contains(entity)) {
                result.add(entity);
            }
        }
        return result;
    }

    /**
     * 保理机构关系客户查询
     *
     * @param anFactorNo:保理机构
     * @param anRelateType:授信对象(1:供应商;2:经销商;3:核心企业;)
     * @return
     */
    public List<SimpleDataEntity> queryFactorCustRelation(final Long anFactorNo, final String anCreditType) {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anFactorNo) {
            return result;
        }
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("relateCustno", anFactorNo);
        String anRelateType = anCreditType;
        if (BetterStringUtils.equals(anRelateType, "1")) {
            anRelateType = CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR;
        }
        anMap.put("relateType", anRelateType);
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        for (final CustRelation relation : this.selectByProperty(anMap)) {
            result.add(new SimpleDataEntity(relation.getCustName(), String.valueOf(relation.getCustNo())));
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
    public Page<CustRelation> queryRelationAccept(final String anBusinStatus, final String anFlag, final int anPageNum, final int anPageSize) {
        final Long factorNo = findCustNoByOperator();
        final Map<String, Object> anMap = new HashMap<String, Object>();
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
    public CustRelation saveRelationAccept(final Long anId, final String anAuditOpinion) {
        BTAssert.notNull(anId, "请选择客户");
        BTAssert.notNull(anAuditOpinion, "请填写处理意见");
        final CustRelation anCustRelation = this.selectByPrimaryKey(anId);
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
    public CustRelation saveRefuseAcceptRelation(final Long anId, final String anAuditOpinion) {
        BTAssert.notNull(anId, "请选客户");
        BTAssert.notNull(anAuditOpinion, "请填写处理意见");
        final CustRelation anCustRelation = this.selectByPrimaryKey(anId);
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
    public Page<CustRelation> queryRelationAudit(final String anBusinStatus, final String anFlag, final int anPageNum, final int anPageSize) {
        final Long operId = UserUtils.getOperatorInfo().getId();
        final String operOrg = UserUtils.getOperatorInfo().getOperOrg();
        final Long factorNo = Collections3.getFirst(custAndOperatorRelaService.findCustNoList(operId, operOrg));
        final Map<String, Object> anMap = new HashMap<String, Object>();
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
    public CustRelation saveRelationAudit(final Long anId, final String anAuditOpinion) {
        BTAssert.notNull(anId, "请选客户");
        BTAssert.notNull(anAuditOpinion, "请填写处理意见");
        final CustRelation anCustRelation = this.selectByPrimaryKey(anId);
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
    public CustRelation saveRefuseAuditRelation(final Long anId, final String anAuditOpinion) {
        BTAssert.notNull(anId, "请选客户");
        BTAssert.notNull(anAuditOpinion, "请填写处理意见");
        final CustRelation anCustRelation = this.selectByPrimaryKey(anId);
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
    public String saveCustRelation(final Long anCustNo, final String anProviderCustList, final String anFactorCustList, final String anPostscript) {
        try {
            BTAssert.notNull(anCustNo, "请选择操作机构");
            BTAssert.notNull(anProviderCustList, "请选择电子合同服务商");
            BTAssert.notNull(anFactorCustList, "请选择保理机构");
            BTAssert.notNull(anPostscript, "请填写附言");
            final CustInfo anCustInfo = custAccountService.selectByPrimaryKey(anCustNo);
            if (anCustInfo.getIdentValid() == false) {
                logger.warn("客户未进行实名验证,不允许提交");
                throw new BytterTradeException(40001, "客户未进行实名验证,不允许提交");
            }
            saveProviderRelation(anCustInfo, anProviderCustList);
            saveFactorRelation(anCustInfo, anFactorCustList, anPostscript);
        }
        catch (final Exception e) {
            logger.error("开通保理融资业务申请", e);
            return CustomerConstants.FACTOR_STATUS_FAILD;
        }
        return CustomerConstants.FACTOR_STATUS_SUCCESS;
    }

    private void saveProviderRelation(final CustInfo anCustInfo, final String anProviderCustList) {
        final String[] anProviderCustNoList = anProviderCustList.split(",");
        for (final String anProviderCustNo : anProviderCustNoList) {
            final Long anRelateCustNo = Long.valueOf(anProviderCustNo);
            // 检查是否已存在关联关系
            final CustRelation anCustRelation = findCustRelation(anCustInfo.getCustNo(), anRelateCustNo, CustomerConstants.RELATE_TYPE_ELEC_CONTRACT);
            if (null == anCustRelation) {
                addCustRelation(anCustInfo, anRelateCustNo, CustomerConstants.RELATE_TYPE_ELEC_CONTRACT, CustomerConstants.RELATE_STATUS_AUDIT);
            }
        }
    }

    private void saveFactorRelation(final CustInfo anCustInfo, final String anFactorCustList, final String anPostscript) {
        final String[] anFactorCustNoList = anFactorCustList.split(",");
        final List<String> anOperatorInnerRuleList = findOperatorInnerRuleList();
        for (final String anFactorCustNo : anFactorCustNoList) {
            final Long anRelateCustNo = Long.valueOf(anFactorCustNo);
            for (final String anRelateType : anOperatorInnerRuleList) {
                // 检查是否已存在关联关系
                final CustRelation anTempCustRelation = findCustRelation(anCustInfo.getCustNo(), anRelateCustNo, anRelateType);
                if (null == anTempCustRelation) {
                    final CustRelation anCustRelation = addCustRelation(anCustInfo, anRelateCustNo, anRelateType, CustomerConstants.RELATE_STATUS_APPLY);
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

    public CustRelation addCustRelation(final CustInfo anCustInfo, final Long anRelateCustNo, final String anRelateType, final String anBusinStatus) {
        final CustRelation relation = new CustRelation();
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

    private CustRelation findCustRelation(final Long anCustNo, final Long relateCustno, final String anRelateType) {
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("relateCustno", relateCustno);
        anMap.put("relateType", anRelateType);
        return Collections3.getFirst(this.selectByProperty(anMap));
    }
    
    

    private Long findCustNoByOperator() {
        final Long operId = UserUtils.getOperatorInfo().getId();
        final String operOrg = UserUtils.getOperatorInfo().getOperOrg();
        return Collections3.getFirst(custAndOperatorRelaService.findCustNoList(operId, operOrg));
    }

    private List<String> findOperatorInnerRuleList() {
        final List<String> anRuleList = new ArrayList<String>();
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
    

    /****
     * 查询客户号根据类型返回关联关系信息
     * @param anCustNo 关系客户号
     * @param anCreditType 关系类型
     * @return 关系列表
     */
    public List<CustRelationData> queryCustRelationData(Long anCustNo,String anCreditType){
        List<CustRelationData> dataList=new ArrayList<CustRelationData>();
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("relateType", anCreditType);
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        for(CustRelation custRelation:this.selectByProperty(anMap)){
            CustRelationData relationData=BeanMapper.map(custRelation,CustRelationData.class);
            dataList.add(relationData);
        }
        return dataList;
    }

    /**
     * @return
     */
    public List<SimpleDataEntity> queryFactorRelation(final Long anCustNo) {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anCustNo) {
            return result;
        }
        if (UserUtils.supplierUser()) {
            final Map<String, Object> anMap = new HashMap<String, Object>();
            anMap.put("custNo", anCustNo);
            anMap.put("relateType", CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR);
            anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);

            final List<CustRelation> relations = this.selectByProperty(anMap);
            for (final CustRelation relation : relations) {
                result.add(new SimpleDataEntity(relation.getRelateCustname(), String.valueOf(relation.getRelateCustno())));
            }
        } else if (UserUtils.coreUser()) {
            final Map<String, Object> anMap = new HashMap<String, Object>();
            anMap.put("custNo", anCustNo);
            anMap.put("relateType", CustomerConstants.RELATE_TYPE_CORE_FACTOR);
            anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);

            final List<CustRelation> relations = this.selectByProperty(anMap);
            for (final CustRelation relation : relations) {
                result.add(new SimpleDataEntity(relation.getRelateCustname(), String.valueOf(relation.getRelateCustno())));
            }
        }
        return result;

    /**
     * 检查客户保理, 
     * 客户只能是供应商|经销商|核心企业
     * relateType为 ： 0,2,3
     * 
     * @param anCustNo
     *            客户号
     * @param anRelateCustno
     *            保理公司编号，对应接口中定义的relateCustno
     * @param anPartnerCustNo
     *            客户在保理公司的客户号
     * @return
     */
    public CustRelation findOneRelation(Long anCustNo, Long anRelateCustno,String anPartnerCustNo) {
        String relateTypes="0,2,3";
        List<CustRelation> dataList=this.mapper.findOneRelation(anCustNo, anRelateCustno, anPartnerCustNo, relateTypes);
        if (!Collections3.isEmpty(dataList)) {
            return Collections3.getFirst(dataList);
        }else{
            logger.error("[Not exists record: anCustNo = " + anCustNo + ", anRelateCustno =" + anRelateCustno + ", anPartnerCustNo =" + anPartnerCustNo + ", relateTypes =" + relateTypes + "]");
            throw new BytterWebServiceException(WebServiceErrorCode.E1006);
        }
    }
    
    /**
     * 更新关联关系的状态
     * 
     * @param anCustNo
     * @param anScfId
     * @param anStatus
     * @param anFactorNo
     */
    public boolean saveFactorRelationStatus(Long anCustNo, String anScfId, String anStatus, String anFactorNo) {
        CustRelation factorRel = findOneRelation(anCustNo, Long.parseLong(anFactorNo), anScfId);
        if (factorRel != null) {
            factorRel.setBusinStatus(anStatus);
            factorRel.setModiDate(BetterDateUtils.getNumDate());
//            factorRel.setLicenseDate(BetterDateUtils.getNumDate());
            return this.updateByPrimaryKey(factorRel) == 1;
        }

        return false;

    }

}
