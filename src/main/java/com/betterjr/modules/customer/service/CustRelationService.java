package com.betterjr.modules.customer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.rocketmq.client.producer.SendResult;
import com.alibaba.rocketmq.client.producer.SendStatus;
import com.betterjr.common.data.PlatformBaseRuleType;
import com.betterjr.common.data.SimpleDataEntity;
import com.betterjr.common.data.WebServiceErrorCode;
import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.exception.BytterWebServiceException;
import com.betterjr.common.mapper.BeanMapper;
import com.betterjr.common.mq.codec.MQCodecType;
import com.betterjr.common.mq.core.RocketMQProducer;
import com.betterjr.common.mq.message.MQMessage;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.DictUtils;
import com.betterjr.common.utils.MathExtend;
import com.betterjr.common.utils.QueryTermBuilder;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.dubbo.CustInfoDubboService;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustAndOperatorRelaService;
import com.betterjr.modules.cert.service.CustCertService;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.dao.CustRelationMapper;
import com.betterjr.modules.customer.data.CustRelationData;
import com.betterjr.modules.customer.entity.CustRelation;
import com.betterjr.modules.customer.entity.PlatformAgencyInfo;
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

    @Autowired
    private PlatformAgencyService agencyService;

    @Resource(name = "betterProducer")
    private RocketMQProducer betterProducer;

    @Autowired
    private CustInfoDubboService custOperatorDubboClientService;

    /**
     * 修改编号对应的公司名称
     *
     * @param anCustNo
     * @param anCustName
     */
    public void saveUpdateCustName(final Long anCustNo, final String anCustName) {
        final Map<String, Object> conditionMap = new HashMap<>();

        conditionMap.put("custNo", anCustNo);

        List<CustRelation> custRelations = this.selectByProperty(conditionMap);
        for (final CustRelation custRelation : custRelations) {
            custRelation.setCustName(anCustName);
            this.updateByPrimaryKeySelective(custRelation);
        }

        conditionMap.clear();
        conditionMap.put("relateCustno", anCustNo);

        custRelations = this.selectByProperty(conditionMap);
        for (final CustRelation custRelation : custRelations) {
            custRelation.setRelateCustname(anCustName);
            this.updateByPrimaryKeySelective(custRelation);
        }
    }

    /**
     * 微信端查询当前客户信息
     *
     * @return
     */
    public CustInfo findWechatCurrentCustInfo() {
        final Long custNo = Collections3.getFirst(UserUtils.findCustNoList());

        return custAccountService.selectByPrimaryKey(custNo);
    }

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
     * 供应商查询核心企业
     *
     * @param anCustNo
     * @return
     */
    public List<CustRelation> queryCoreList(final Long anCustNo) {
        if (null == anCustNo) {
            return null;
        }
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_CORE, CustomerConstants.RELATE_TYPE_SELLER_CORE });
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        return this.selectByProperty(anMap);
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
    public List<SimpleDataEntity> queryFactorCoreRelation(Long anFactorNo) {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (MathExtend.smallValue(anFactorNo)) {
            anFactorNo = Collections3.getFirst(UserUtils.findCustNoList());
        }
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
     * @param anRole
     * @return
     */
    public List<SimpleDataEntity> queryFactorKeyAndValue(final Long anCustNo, final PlatformBaseRuleType anRole) {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anCustNo) {
            return result;
        }
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);

        if (PlatformBaseRuleType.CORE_USER.equals(anRole)) {
            anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_CORE_FACTOR });
        }
        else if (PlatformBaseRuleType.SUPPLIER_USER.equals(anRole)) {
            anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR });
        }
        else if (PlatformBaseRuleType.SELLER_USER.equals(anRole)) {
            anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SELLER_FACTOR });
        }

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
    public List<SimpleDataEntity> queryCustRelation(final Long anCustNo, final PlatformBaseRuleType anRole) {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anCustNo) {
            return result;
        }

        if (PlatformBaseRuleType.CORE_USER.equals(anRole)) {
            Map<String, Object> anMap = new HashMap<String, Object>();
            anMap.put("custNo", anCustNo);
            anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
            anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_CORE_FACTOR });
            for (final CustRelation relation : this.selectByProperty(anMap)) {
                final SimpleDataEntity entity = new SimpleDataEntity(relation.getRelateCustname(), String.valueOf(relation.getRelateCustno()));
                if (!result.contains(entity)) {
                    result.add(entity);
                }
            }

            anMap = new HashMap<String, Object>();
            anMap.put("relateCustno", anCustNo);
            anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
            anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_CORE, CustomerConstants.RELATE_TYPE_SELLER_CORE });
            for (final CustRelation relation : this.selectByProperty(anMap)) {
                final SimpleDataEntity entity = new SimpleDataEntity(relation.getCustName(), String.valueOf(relation.getCustNo()));
                if (!result.contains(entity)) {
                    result.add(entity);
                }
            }

        } else if (PlatformBaseRuleType.SUPPLIER_USER.equals(anRole)) {
            Map<String, Object> anMap = new HashMap<String, Object>();
            anMap.put("custNo", anCustNo);
            anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
            anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR });
            for (final CustRelation relation : this.selectByProperty(anMap)) {
                final SimpleDataEntity entity = new SimpleDataEntity(relation.getRelateCustname(), String.valueOf(relation.getRelateCustno()));
                if (!result.contains(entity)) {
                    result.add(entity);
                }
            }

            anMap = new HashMap<String, Object>();
            anMap.put("custNo", anCustNo);
            anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
            anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_CORE });
            for (final CustRelation relation : this.selectByProperty(anMap)) {
                final SimpleDataEntity entity = new SimpleDataEntity(relation.getCustName(), String.valueOf(relation.getCustNo()));
                if (!result.contains(entity)) {
                    result.add(entity);
                }
            }
        } else if (PlatformBaseRuleType.SELLER_USER.equals(anRole)) {
            Map<String, Object> anMap = new HashMap<String, Object>();
            anMap.put("custNo", anCustNo);
            anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
            anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SELLER_FACTOR });
            for (final CustRelation relation : this.selectByProperty(anMap)) {
                final SimpleDataEntity entity = new SimpleDataEntity(relation.getRelateCustname(), String.valueOf(relation.getRelateCustno()));
                if (!result.contains(entity)) {
                    result.add(entity);
                }
            }

            anMap = new HashMap<String, Object>();
            anMap.put("custNo", anCustNo);
            anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
            anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SELLER_CORE });
            for (final CustRelation relation : this.selectByProperty(anMap)) {
                final SimpleDataEntity entity = new SimpleDataEntity(relation.getCustName(), String.valueOf(relation.getCustNo()));
                if (!result.contains(entity)) {
                    result.add(entity);
                }
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
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        if (BetterStringUtils.equals(anCreditType, "1")) {
            anMap.put("relateType", CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR);
            for (final CustRelation relation : this.selectByProperty(anMap)) {
                result.add(new SimpleDataEntity(relation.getCustName(), String.valueOf(relation.getCustNo())));
            }
        }
        if (BetterStringUtils.equals(anCreditType, "2")) {
            anMap.put("relateType", CustomerConstants.RELATE_TYPE_SELLER_FACTOR);
            for (final CustRelation relation : this.selectByProperty(anMap)) {
                result.add(new SimpleDataEntity(relation.getCustName(), String.valueOf(relation.getCustNo())));
            }
        }
        if (BetterStringUtils.equals(anCreditType, "3")) {
            anMap.put("relateType", CustomerConstants.RELATE_TYPE_CORE_FACTOR);
            for (final CustRelation relation : this.selectByProperty(anMap)) {
                result.add(new SimpleDataEntity(relation.getCustName(), String.valueOf(relation.getCustNo())));
            }
        }
        return result;
    }

    /**
     * 查询保理结构所有关系客户
     *
     * @param anFactorNo
     * @return
     */
    public List<SimpleDataEntity> webQueryFactorAllCust(final Long anFactorNo) {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anFactorNo) {
            return result;
        }
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("relateCustno", anFactorNo);
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR, CustomerConstants.RELATE_TYPE_SELLER_FACTOR,
                CustomerConstants.RELATE_TYPE_CORE_FACTOR });
        for (final CustRelation relation : this.selectByProperty(anMap, "relateType, custNo")) {
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

    /***
     * 根据map 的参数分页查询
     *
     * @param anMap
     * @param anFlag
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<CustRelation> queryCustRelationInfo(final Map<String, Object> anMap, final String anFlag, final int anPageNum, final int anPageSize) {
        return this.selectPropertyByPage(CustRelation.class, anMap, anPageNum, anPageSize, "1".equals(anFlag));
    }


    public Page<CustRelation> findCustRelationInfo(final Long anCustNo, final String anRelateType, final PlatformBaseRuleType anRole) {
        if (BetterStringUtils.isNotBlank(anRelateType)) {
            return mapper.findCustRelationListByRelateType(anCustNo, anRelateType);
        }
        // TODO 需要改
        if (PlatformBaseRuleType.CORE_USER.equals(anRole)) {
            return mapper.findCustCoreRelationList(anCustNo);
        } else if (PlatformBaseRuleType.SUPPLIER_USER.equals(anRole)) {
            return mapper.findCustSupplierRelationList(anCustNo);
        } else if (PlatformBaseRuleType.SELLER_USER.equals(anRole)) {
            return mapper.findCustSellerRelationList(anCustNo);
        }

        return null;

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
        custRelationAuditService.addAuditCustRelation(anCustRelation, anCustRelation.getRelateCustname(), anAuditOpinion, "保理公司受理");
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
        custRelationAuditService.addRefuseCustRelation(anCustRelation, anCustRelation.getRelateCustname(), anAuditOpinion, "保理公司受理");
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
        anCustRelation.setRelateCustCorp(DictUtils.getDictCode("ScfFactorGroup", String.valueOf(anCustRelation.getRelateCustno())));
        this.updateByPrimaryKeySelective(anCustRelation);
        custRelationAuditService.addAuditCustRelation(anCustRelation, anCustRelation.getRelateCustname(), anAuditOpinion, "保理公司审批");
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
        custRelationAuditService.addRefuseCustRelation(anCustRelation, anCustRelation.getRelateCustname(), anAuditOpinion, "保理公司审批");
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

    /**
     * 微信端开通保理融资业务申请
     *
     * @param anCustNo
     * @param anFactorCustList
     * @return
     */
    public String saveCustRelation(final Long anCustNo, final String anFactorCustList) {
        try {
            BTAssert.notNull(anCustNo, "请选择操作机构");
            BTAssert.notNull(anFactorCustList, "请选择保理机构");
            final CustInfo anCustInfo = custAccountService.selectByPrimaryKey(anCustNo);
            if (anCustInfo.getIdentValid() == false) {
                logger.warn("您未进行实名认证,不允许认证");
                throw new BytterTradeException(40001, "您未进行实名认证,不允许认证");
            }

            // 获取电子合同服务商信息
            final StringBuffer provider = new StringBuffer();
            final List<DictItemInfo> anProviderDict = DictUtils.getDictList("ScfElecAgreementGroup");
            for (final DictItemInfo anDictItem : anProviderDict) {
                provider.append(anDictItem.getItemValue());
                provider.append(",");
            }

            final String anProviderCustList = provider.toString();
            saveProviderRelation(anCustInfo, anProviderCustList.substring(0, anProviderCustList.length() - 1));
            saveFactorRelation(anCustInfo, anFactorCustList, "微信端申请开通融资保理业务");
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
                    final CustRelation anCustRelation = addCustRelation(anCustInfo, anRelateCustNo, anRelateType,
                            CustomerConstants.RELATE_STATUS_APPLY);
                    String message = anPostscript;
                    if (BetterStringUtils.isNotBlank(anPostscript)) {
                        message = "申请对象：" + anCustRelation.getRelateCustname() + ", 附言：" + message;
                    }
                    custRelationAuditService.addAuditCustRelation(anCustRelation, anCustInfo.getCustName(), message, "企业开户申请");
                }
                else {
                    if (BetterStringUtils.equals(anTempCustRelation.getBusinStatus(), CustomerConstants.RELATE_STATUS_REFUSE) == true) {
                        anTempCustRelation.setBusinStatus(CustomerConstants.RELATE_STATUS_APPLY);
                        anTempCustRelation.setLastStatus(CustomerConstants.RELATE_STATUS_APPLY);
                        this.updateByPrimaryKeySelective(anTempCustRelation);
                        String message = anPostscript;
                        if (BetterStringUtils.isNotBlank(anPostscript)) {
                            message = "申请对象：" + anTempCustRelation.getRelateCustname() + ", 附言：" + message;
                        }
                        custRelationAuditService.addAuditCustRelation(anTempCustRelation, anCustInfo.getCustName(), anPostscript, "企业开户申请");
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

    public CustRelation findCustRelation(final Long anCustNo, final Long relateCustno, final String anRelateType) {
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
     *
     * @param anCustNo
     *            关系客户号
     * @param anCreditType
     *            关系类型
     * @return 关系列表
     */
    public List<CustRelationData> queryCustRelationData(final Long anCustNo, final String anCreditType) {
        final List<CustRelationData> dataList = new ArrayList<CustRelationData>();
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("relateType", anCreditType);
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        for (final CustRelation custRelation : this.selectByProperty(anMap)) {
            final CustRelationData relationData = BeanMapper.map(custRelation, CustRelationData.class);
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
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        String relateType = "";
        if (UserUtils.supplierUser()) {
            relateType = CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR;
        }
        else if (UserUtils.sellerUser()) {
            relateType = CustomerConstants.RELATE_TYPE_SELLER_FACTOR;
        }
        else if (UserUtils.coreUser()) {
            relateType = CustomerConstants.RELATE_TYPE_CORE_FACTOR;
        }
        if (BetterStringUtils.isNotBlank(relateType)) {
            anMap.put("relateType", relateType);
            final List<CustRelation> relations = this.selectByProperty(anMap);
            for (final CustRelation relation : relations) {
                result.add(new SimpleDataEntity(relation.getRelateCustname(), String.valueOf(relation.getRelateCustno())));
            }
        }
        return result;
    }

    /**
     * @return
     */
    public List<CustRelation> queryOpenedFactor(final Long anCustNo) {
        if (null == anCustNo) {
            return null;
        }
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        String relateType = "";
        if (UserUtils.supplierUser()) {
            relateType = CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR;
        }
        else if (UserUtils.sellerUser()) {
            relateType = CustomerConstants.RELATE_TYPE_SELLER_FACTOR;
        }
        else if (UserUtils.coreUser()) {
            relateType = CustomerConstants.RELATE_TYPE_CORE_FACTOR;
        }
        if (BetterStringUtils.isNotBlank(relateType)) {
            anMap.put("relateType", relateType);
            final List<CustRelation> relations = this.selectByProperty(anMap);
            return relations;
        }
        return null;
    }

    /**
     * 微信端,获取当前客户的保理公司
     *
     * @return
     */
    public List<SimpleDataEntity> queryFactorRelation() {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        final Map<String, Object> anMap = new HashMap<String, Object>();
        final Long custNo = UserUtils.getDefCustInfo().getCustNo();
        if (null == custNo) {
            return result;
        }
        anMap.put("custNo", custNo);
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR, CustomerConstants.RELATE_TYPE_SELLER_FACTOR,
                CustomerConstants.RELATE_TYPE_CORE_FACTOR });
        final List<CustRelation> relations = this.selectByProperty(anMap);
        for (final CustRelation relation : relations) {
            result.add(new SimpleDataEntity(relation.getRelateCustname(), String.valueOf(relation.getRelateCustno())));
        }
        return result;
    }

    /**
     * 检查客户保理, 客户只能是供应商|经销商|核心企业 relateType为 ： 0,2,3
     *
     * @param anCustNo
     *            客户号
     * @param anRelateCustno
     *            保理公司编号，对应接口中定义的relateCustno
     * @param anPartnerCustNo
     *            客户在保理公司的客户号
     * @return
     */
    public CustRelation findOneRelation(final Long anCustNo, final Long anRelateCustno, final String anPartnerCustNo) {
        final String relateTypes = "0,2,3";
        final List<CustRelation> dataList = this.mapper.findOneRelation(anCustNo, anRelateCustno, anPartnerCustNo, relateTypes);
        if (!Collections3.isEmpty(dataList)) {
            return Collections3.getFirst(dataList);
        }
        else {
            logger.error("[Not exists record: anCustNo = " + anCustNo + ", anRelateCustno =" + anRelateCustno + ", anPartnerCustNo ="
                    + anPartnerCustNo + ", relateTypes =" + relateTypes + "]");
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
    public boolean saveFactorRelationStatus(final Long anCustNo, final String anScfId, final String anStatus, final String anFactorNo) {
        final CustRelation factorRel = findRelationWithCustCorp(anCustNo, anScfId, anFactorNo);
        if (factorRel != null) {
            factorRel.setBusinStatus(anStatus);
            factorRel.setModiDate(BetterDateUtils.getNumDate());
            // factorRel.setLicenseDate(BetterDateUtils.getNumDate());
            return this.updateByPrimaryKey(factorRel) == 1;
        }

        return false;
    }

    /**
     * 按银行账户信息查询供应商与核心企业关系
     *
     * @param anBankAccountName
     * @param anBankAccount
     * @return
     */
    public Long findCustNoByBankInfo(final String anBankAccountName, final String anBankAccount) {
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("bankAcco", anBankAccount);
        anMap.put("bankAccoName", anBankAccountName);
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        anMap.put("relateType", CustomerConstants.RELATE_TYPE_SUPPLIER_CORE);
        final CustRelation custRelation = Collections3.getFirst(this.selectByProperty(anMap));
        if (custRelation != null) {
            return custRelation.getCustNo();
        }
        else {
            return 0L;
        }
    }

    /**
     * 根据从核心企业上传的客户信息，保存客户与核心企业的关系；数据来自对象 CoreSupplierInfo <BR>
     * 处理逻辑：检查核心企业编码加上其余的熟悉 先检查客户号是否存在，如果存在，根据核心企业编码和客户号来检查，<BR>
     * 如果记录存在，则更新核心企业的内部编码 如果不存在，则根据核心企业内部编码来检查，如果存在则忽略；如果不存在，<BR>
     * 则根据企业名称来检查，如果都不存在，则增加记录
     *
     * @param anValues
     *            上传来的数据
     * @param anCoreCustName
     *            核心企业名称
     * @param anCoreCustNo
     *            核心企业编码
     * @return
     */
    public boolean saveAndCheckCust(final Map<String, Object> anValues, final String anCoreCustName, final Long anCoreCustNo) {
        final Map<String, Object> termMap = QueryTermBuilder.newInstance().put("coreCustNo", anCoreCustNo)
                .put("relateType", CustomerConstants.RELATE_TYPE_SUPPLIER_CORE).build();
        final String btNo = (String) anValues.get("btNo");
        final Long custNo = (Long) anValues.get("custNo");
        boolean isok = false;
        Object dataValue;
        for (final String tmpKey : new String[] { "custNo", "btNo", "custName" }) {
            isok = false;
            dataValue = anValues.get(tmpKey);
            if ("custNo".equals(tmpKey)) {
                isok = (MathExtend.smallValue((Long) dataValue) == false);
            }
            else {
                isok = BetterStringUtils.isNotBlank((String) dataValue);
            }

            if (isok) {
                termMap.put(tmpKey, dataValue);
                if (saveUploadModifyValue(termMap, btNo, custNo)) {
                    return true;
                }
                else {
                    termMap.remove(tmpKey);
                }
            }
        }
        final CustRelation custRelation = BeanMapper.map(anValues, CustRelation.class);
        custRelation.initUploadInfo(anCoreCustName, anCoreCustNo);
        this.insert(custRelation);
        return true;
    }

    private boolean saveUploadModifyValue(final Map anTermMap, final String anBtNo, final Long anCustNo) {
        final List<CustRelation> tmpList = this.selectByProperty(anTermMap);
        final CustRelation custRelation = Collections3.getFirst(tmpList);
        if (custRelation != null) {
            if (BetterStringUtils.isNotBlank(anBtNo)) {
                custRelation.setBtNo(anBtNo);
            }
            if (MathExtend.smallValue(anCustNo) == false) {
                custRelation.setCustNo(anCustNo);
            }
            custRelation.modifyValue((CustOperatorInfo) null);
            custRelation.setLastStatus(custRelation.getBusinStatus());
            custRelation.setBusinStatus(CustomerConstants.RELATE_STATUS_AUDIT);
            this.updateByPrimaryKey(custRelation);
            return true;
        }

        return false;
    }

    public CustRelation addWeChatCustAndCoreRelation(final CustInfo anCustInfo, final Long anRelateCustNo, final CustOperatorInfo anOperator) {
        final CustRelation relation = new CustRelation();
        relation.initWeChatValue(anOperator);
        relation.setCustNo(anCustInfo.getCustNo());
        relation.setCustName(anCustInfo.getCustName());
        relation.setCustType(anCustInfo.getCustType());
        relation.setRelateCustno(anRelateCustNo);
        relation.setRelateCustname(custAccountService.queryCustName(anRelateCustNo));
        relation.setRelateType(CustomerConstants.RELATE_TYPE_SUPPLIER_CORE);
        relation.setBusinStatus("3");
        relation.setLastStatus(relation.getBusinStatus());
        this.insert(relation);
        return relation;
    }

    /**
     * 保存保理公司与企业之间的关系
     *
     * @param anRelation
     */
    public void saveOrUpdateCustFactor(final CustRelation anRelation) {
        final Map termMap = QueryTermBuilder.newInstance().put("custNo", anRelation.getCustNo()).put("relateCustCorp", anRelation.getRelateCustCorp())
                .put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR, CustomerConstants.RELATE_TYPE_CORE_FACTOR,
                        CustomerConstants.RELATE_TYPE_SELLER_FACTOR })
                .build();
        final List<CustRelation> tmpList = this.selectByProperty(termMap);
        final CustRelation tmpRelation = Collections3.getFirst(tmpList);
        anRelation.setRelateType(CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR);
        final PlatformAgencyInfo agencyInfo = agencyService.findSaleAgency(anRelation.getRelateCustCorp());
        if (agencyInfo != null) {
            anRelation.setRelateCustname(agencyInfo.getName());
            if (BetterStringUtils.isNotBlank(agencyInfo.getRelaCustNo())) {
                anRelation.setRelateCustno(Long.parseLong(agencyInfo.getRelaCustNo()));
            }
        }

        if (tmpRelation != null) {
            anRelation.initModifyValue(tmpRelation);
            this.updateByPrimaryKey(anRelation);
        }
        else {
            anRelation.initAddValue();
            anRelation.setBusinStatus("2");
            this.insert(anRelation);
        }
    }

    /**
     * 根据保理公司客户号，查找系统中的客户号
     *
     * @param anScfId
     *            保理公司客户号
     * @param anAgencyNo
     *            保理公司编码
     * @return
     */
    public Long findCustNoByScfId(final String anScfId, final String anAgencyNo) {
        final Map workCondition = new HashMap();
        workCondition.put("partnerCustNo", anScfId);
        workCondition.put("relateCustCorp", anAgencyNo);
        logger.info("findCustNoByScfId parameter: scfId= " + anScfId + ", factorNo=" + anAgencyNo);
        final List<CustRelation> workScfFactorList = this.selectByProperty(workCondition);
        if (Collections3.isEmpty(workScfFactorList)) {
            logger.info("not find CustNoByScfId");
            return 0L;
        }
        else {
            return Collections3.getFirst(workScfFactorList).getCustNo();
        }
    }

    /**
     * 根据客户号获得在保理公司的关联号
     *
     * @param anCustNo
     *            我方系统客户号
     * @param anAgencyNo
     *            保理公司编码
     * @return
     */
    public String findScfIdByCustNo(final Long anCustNo, final String anAgencyNo) {
        final Map termMap = new HashMap();
        termMap.put("custNo", anCustNo);
        termMap.put("relateCustCorp", anAgencyNo);
        termMap.put("businStatus", new String[] { "0", "1", "2" });
        logger.info("findCustNoByScfId parameter: custNo= " + anCustNo + ", factorNo=" + anAgencyNo);
        final List<CustRelation> tmpList = this.selectByProperty(termMap);
        final CustRelation workCustRelation = Collections3.getFirst(tmpList);
        if (workCustRelation == null) {
            logger.info("not find findScfIdByCustNo");
            return " ";
        }
        else {
            return workCustRelation.getPartnerCustNo();
        }
    }

    /**
     * 查询核心企业关联的保理机构，用于获取核心企业额度信息
     *
     * @param anAgencyNo
     * @return
     */
    public List<CustRelation> findFactorRelaByCoreCustNo(final String anAgencyNo) {
        final Map workCondition = new HashMap();
        workCondition.put("custNo", DictUtils.findCoreCustNoList());
        workCondition.put("relateCustCorp", anAgencyNo);

        return this.selectByProperty(workCondition);
    }

    /**
     * 查询状态为处理中的业务，包括1：已申请和5：取消中的关联关系
     *
     * @return
     */
    public List<CustRelation> findFactorRelaByRough(final String anAgencyNo) {
        final Map workCondition = new HashMap();
        workCondition.put("businStatus", new String[] { "2", "5" });
        workCondition.put("relateCustCorp", anAgencyNo);

        return this.selectByProperty(workCondition);
    }

    /**
     * 查询需要调用远程开户接口的信息
     *
     * @return
     */
    public List<CustRelation> findAppAccountRequest() {
        final Map termMap = QueryTermBuilder.newInstance().put("businStatus", "1").put("relateType", new String[] { "0", "1", "2" }).build();

        return selectByProperty(termMap);
    }

    /**
     * 根据ID，查找单个关联信息
     *
     * @param anRelationId
     * @return
     */
    public CustRelation findByRelationId(final Long anRelationId) {

        return this.selectByPrimaryKey(anRelationId);
    }

    private void processOpenScf(final CustRelation anCustRelation) {
        final MQMessage message = new MQMessage(CustomerConstants.CUSTOMER_OPEN_SCF_ACCOUNT, MQCodecType.FST);
        message.setObject(anCustRelation);
        try {
            final SendResult sendResult = betterProducer.sendMessage(message);

            if (sendResult.getSendStatus().equals(SendStatus.SEND_OK) == false) {
                logger.warn("消息通知发送失败 SendResult=" + sendResult.toString());
            }
        }
        catch (final Exception e) {
            logger.error("消息通知发送错误", e);
        }
    }

    /**
     * 查找企业对应在资金方核心企业开户的信息
     *
     * @param anCustNo
     * @param anAgencyNo
     * @return
     */
    public String findCustCoreNoByCustNo(final Long anCustNo, final String anAgencyNo) {
        Map termMap = QueryTermBuilder.newInstance().put("custNo", anCustNo)
                .put("businStatus",
                        new String[] { CustomerConstants.RELATE_STATUS_APPLY, CustomerConstants.RELATE_STATUS_ACCEPT,
                                CustomerConstants.RELATE_STATUS_AUDIT })
                .put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_CORE, CustomerConstants.RELATE_TYPE_SELLER_CORE }).build();
        List<CustRelation> tmpList = this.selectByProperty(termMap);
        final Set<Long> tmpCustNoSet = new HashSet<>();
        for (final CustRelation tmpRelation : tmpList) {
            tmpCustNoSet.add(tmpRelation.getRelateCustno());
        }
        if (Collections3.isEmpty(tmpCustNoSet)) {
            return "";
        }
        termMap = QueryTermBuilder.newInstance().put("custNo", tmpCustNoSet)
                .put("relateCustCorp", anAgencyNo).put("businStatus", new String[] { CustomerConstants.RELATE_STATUS_APPLY,
                        CustomerConstants.RELATE_STATUS_ACCEPT, CustomerConstants.RELATE_STATUS_AUDIT })
                .put("relateType", CustomerConstants.RELATE_TYPE_CORE_FACTOR).build();
        tmpList = this.selectByProperty(termMap);
        final StringBuilder sb = new StringBuilder();
        for (final CustRelation tmpRelation : tmpList) {
            sb.append(String.valueOf(tmpRelation.getCustNo())).append(",");
        }
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 判断开户的是否是核心企业；客户类型：1 供应商开户 2 核心企业开户
     *
     * @param anCustNo
     *            企业编码
     * @param anAgencyNo
     *            合作机构代码
     * @return
     */
    public String checkCoreCustomer(final Long anCustNo, final String anAgencyNo) {
        final Map termMap = QueryTermBuilder.newInstance().put("relateCustCorp", anAgencyNo).put("custNo", anCustNo)
                .put("relateType", CustomerConstants.RELATE_TYPE_CORE_FACTOR).build();
        final List tmpList = this.selectByProperty(termMap);
        if (Collections3.isEmpty(tmpList)) {
            return "1";
        }
        else {
            return "2";
        }
    }

    /**
     * 保存远程调用的接口信息
     *
     * @param anId
     *            关系ID
     * @param anScfId
     *            远端的客户信息
     * @param anStatus
     *            处理状态
     */
    public void saveFactorRelationInfo(final Long anId, final String anScfId, final String anStatus) {
        final CustRelation custRelation = this.selectByPrimaryKey(anId);
        if (custRelation != null) {
            custRelation.setPartnerCustNo(anScfId);
            custRelation.setBusinStatus(anStatus);
            custRelation.modifyValue((CustOperatorInfo) null);
            this.updateByPrimaryKey(custRelation);
        }
    }

    /**
     * 保理机构客户查询,适用于根据核心企业，查询关联保理机构的供应商或经销商
     *
     * @param anCoreCustNo
     * @return
     */
    public List<SimpleDataEntity> querySimpleDataByFactorAndCore(final Long anCoreCustNo) {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anCoreCustNo) {
            return result;
        }
        final List<Long> custNoList = UserUtils.findCustNoList();
        String tmpFactorNo = "-1";
        if (Collections3.isEmpty(custNoList) == false) {
            tmpFactorNo = BetterStringUtils.join(custNoList.toArray(), ",");
        }
        for (final CustRelation relation : mapper.findDataByFactorAndCore(anCoreCustNo, tmpFactorNo)) {
            result.add(new SimpleDataEntity(relation.getCustName(), String.valueOf(relation.getCustNo())));
        }
        return result;
    }

    /**
     * 查询核心企业下面所有供应商、经销商
     *
     * @param anCoreCustNo
     * @return
     */
    public List<SimpleDataEntity> queryCoreCust(final Long anCoreCustNo) {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anCoreCustNo) {
            return result;
        }
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("relateCustno", anCoreCustNo);
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        anMap.put("relateType", new String[] { CustomerConstants.RELATE_TYPE_SUPPLIER_CORE, CustomerConstants.RELATE_TYPE_SELLER_CORE });
        for (final CustRelation relation : this.selectByProperty(anMap)) {
            final SimpleDataEntity entity = new SimpleDataEntity(relation.getCustName(), String.valueOf(relation.getCustNo()));
            if (!result.contains(entity)) {
                result.add(entity);
            }
        }
        return result;
    }
    
    /**
     * 查询所有供应商和核心企业
     * @return
     */
    public List<SimpleDataEntity> queryAllCust() {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        
        
        for (final CustRelation relation : this.mapper.queryAllCust()) {
            final SimpleDataEntity entity = new SimpleDataEntity(relation.getCustName(), String.valueOf(relation.getCustNo()));
            if (!result.contains(entity)) {
                result.add(entity);
            }
        }
        return result;
    }

    /**
     * 根据关联方简称和关联方客户编号查询客户关联关系信息
     *
     * @param anCustNo
     *            客户号
     * @param anCustCorp
     *            关联方简称
     * @param anPartnerCustNo
     *            关联方客户号
     * @return
     */
    public CustRelation findRelationWithCustCorp(final Long anCustNo, final String anPartnerCustNo, final String anCustCorp) {
        final Map<String, Object> tmpMap = QueryTermBuilder.newInstance().put("custNo", anCustNo).put("relateCustCorp", anCustCorp)
                .put("partnerCustNo", anPartnerCustNo).build();
        final List<CustRelation> tmpList = this.selectByProperty(tmpMap);

        return Collections3.getFirst(tmpList);
    }

    /**
     * 保理公司查询客户信息
     */
    public Page<CustRelation> queryCustInfoByFactor(final String anRelateType, final String anFlag, final int anPageNum, final int anPageSize) {
        if (!UserUtils.factorUser()) {
            throw new BytterTradeException("无相应权限操作！");
        }
        final Map<String, Object> anMap = QueryTermBuilder.newInstance().put("relateType", anRelateType.split(","))
                .put("relateCustno", custOperatorDubboClientService.findCustNo()).build();
        final Page<CustRelation> result = this.selectPropertyByPage(anMap, anPageNum, anPageSize, "1".equals(anFlag));
        return result;
    }
    
    
    /**
     * 通过核心企业查询保理公司 并且 是内部结算中心
     * @param anCoreCustNo
     * @return
     */
    public List<SimpleDataEntity> queryFactoryByCore(final Long anCoreCustNo) {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anCoreCustNo) {
            return result;
        }
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCoreCustNo);
        anMap.put("relateType", CustomerConstants.RELATE_TYPE_CORE_FACTOR);
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        for (final CustRelation relation : this.selectByProperty(anMap)) {
            if(StringUtils.isNoneBlank(relation.getIsInside()) && "1".equals(relation.getIsInside())){
                result.add(new SimpleDataEntity(relation.getRelateCustname(), String.valueOf(relation.getRelateCustno())));
            }
        }
        return result;
    }
    

    /**
     * 企业合作银行下拉列表查询
     *
     * @param anCustNo
     * @return
     */
    public List<SimpleDataEntity> queryBankInfoKeyAndValue(final Long anCustNo) {
        final List<SimpleDataEntity> result = new ArrayList<SimpleDataEntity>();
        if (null == anCustNo) {
            return result;
        }
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("relateType", CustomerConstants.RELATE_TYPE_BANK_CONTRACT);
        anMap.put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT);
        for (final CustRelation relation : this.selectByProperty(anMap)) {
            result.add(new SimpleDataEntity(relation.getRelateCustname(), String.valueOf(relation.getRelateCustno())));
        }
        return result;
    }
    
}
