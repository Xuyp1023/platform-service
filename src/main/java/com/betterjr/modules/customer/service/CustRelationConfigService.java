package com.betterjr.modules.customer.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.rocketmq.client.producer.SendResult;
import com.betterjr.common.data.PlatformBaseRuleType;
import com.betterjr.common.data.SimpleDataEntity;
import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.mapper.BeanMapper;
import com.betterjr.common.mq.core.RocketMQProducer;
import com.betterjr.common.mq.message.MQMessage;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.mapper.pagehelper.PageHelper;
import com.betterjr.modules.account.dubbo.interfaces.ICustInfoService;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.cert.entity.CustCertInfo;
import com.betterjr.modules.cert.service.CustCertService;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.data.FactorBusinessRequestData;
import com.betterjr.modules.customer.entity.CustMajor;
import com.betterjr.modules.customer.entity.CustMechBankAccount;
import com.betterjr.modules.customer.entity.CustMechBase;
import com.betterjr.modules.customer.entity.CustMechBusinLicence;
import com.betterjr.modules.customer.entity.CustRelation;
import com.betterjr.modules.customer.entity.CustRelationAudit;
import com.betterjr.modules.document.IAgencyAuthFileGroupService;
import com.betterjr.modules.document.ICustFileAduitTempService;
import com.betterjr.modules.document.ICustFileService;
import com.betterjr.modules.document.entity.AgencyAuthorFileGroup;
import com.betterjr.modules.document.entity.CustFileAduit;
import com.betterjr.modules.document.entity.CustFileAduitTemp;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.document.service.CustFileAuditService;
import com.betterjr.modules.operator.dubbo.IOperatorService;
import com.betterjr.modules.wechat.service.CustWeChatService;
/****
 * 客户关系添加
 * @author hubl
 *
 */
@Service
public class CustRelationConfigService {

    private static final Logger logger=LoggerFactory.getLogger(CustRelationConfigService.class);

    @Autowired
    private CustAccountService custAccountService;
    @Autowired
    private CustCertService  custCertService;
    @Autowired
    private CustRelationService custRelationService;
    @Autowired
    private CustMajorService custMajorService;
    @Reference(interfaceClass=IAgencyAuthFileGroupService.class)
    private IAgencyAuthFileGroupService agencyAuthFileGroupService;

    @Reference(interfaceClass=ICustFileAduitTempService.class)
    private  ICustFileAduitTempService custFileAduitTempService;
    @Autowired
    private CustWeChatService custWeChatService;
    @Autowired
    private CustOpenAccountTmpService custOpenAccountTmpService;
    @Autowired
    private CustFileAuditService custFileAuditService;
    @Autowired
    private CustRelationAuditService custRelationAuditService;
    @Reference(interfaceClass = ICustFileService.class)
    private ICustFileService custFileItemService;
    @Autowired
    private CustMechLawService custMechLawService;
    @Reference(interfaceClass=ICustInfoService.class)
    private ICustInfoService custInfoService;
    @Reference(interfaceClass=IOperatorService.class)
    private IOperatorService operatorService;
    @Autowired
    private CustMechBaseService custMechBaseService;
    @Autowired
    private CustMechBankAccountService custMechBankAccountService;
    @Autowired
    private CustMechBusinLicenceService custMechBusinLicenceService;
    @Resource
    private RocketMQProducer betterProducer;
    @Autowired
    private ScfPushWechatService scfPushWechatService;
    

    /***
     * 判断当前登录的身份信息，并返回需要关联选择的客户类型
     * @return
     */
    public List<SimpleDataEntity> findCustByPlatform(final String anCustType){
        final List<CustMajor> list = custMajorService.findCustMajorByType(anCustType);
        final List<SimpleDataEntity> custTypeList=new ArrayList<SimpleDataEntity>();
        for (final CustMajor cust : list) {
            custTypeList.add(new SimpleDataEntity(cust.getCustName(), cust.getCustNo().toString()));
        }
        return custTypeList;
    }

    /***
     * 判断当前登录的身份信息，并返回需要关联选择的客户类型
     * @return
     */
    public List<SimpleDataEntity> findCustType(){
        final List<SimpleDataEntity> custTypeList=new ArrayList<SimpleDataEntity>();
        if(UserUtils.coreUser()){
            custTypeList.add(new SimpleDataEntity(PlatformBaseRuleType.SUPPLIER_USER.getTitle(),PlatformBaseRuleType.SUPPLIER_USER.toString()));
            custTypeList.add(new SimpleDataEntity(PlatformBaseRuleType.SELLER_USER.getTitle(),PlatformBaseRuleType.SELLER_USER.toString()));
            custTypeList.add(new SimpleDataEntity(PlatformBaseRuleType.FACTOR_USER.getTitle(),PlatformBaseRuleType.FACTOR_USER.toString()));
        }else if(UserUtils.supplierUser() || UserUtils.sellerUser()){
            custTypeList.add(new SimpleDataEntity(PlatformBaseRuleType.CORE_USER.getTitle(),PlatformBaseRuleType.CORE_USER.toString()));
            custTypeList.add(new SimpleDataEntity(PlatformBaseRuleType.FACTOR_USER.getTitle(),PlatformBaseRuleType.FACTOR_USER.toString()));
        }
        return custTypeList;
    }

    /****
     * 添加关联关系
     * @param anCustType
     * @param anCustNo
     * @param anRelationCustNo
     * @return
     */
    public boolean addCustRelation(final String anCustType,final Long anCustNo,final String anRelationCustStr){
        logger.info("addCustRelation 入参：anCustType="+anCustType+",anCustNo="+anCustNo+",anRelationCustStr="+anRelationCustStr);
        BTAssert.notNull(anCustType, "类型不能为空");
        BTAssert.notNull(anCustNo, "客户号不能为空");
        BTAssert.notNull(anRelationCustStr, "关联客户号不能为空");
        boolean bool=false;
        for(final String relationCust:anRelationCustStr.split(",")){
            final CustRelation custRelation=findCustRelation(anCustType, anCustNo, Long.parseLong(relationCust));
            if(custRelationService.findCustRelation(custRelation.getCustNo(), custRelation.getRelateCustno(), custRelation.getRelateType())==null){
                custRelation.initAddValue();
                custRelationService.insert(custRelation);
                bool=true;
                // 发送微信消息
                if(UserUtils.coreUser() && BetterStringUtils.equalsIgnoreCase(custRelation.getRelateType(), CustomerConstants.RELATE_TYPE_SUPPLIER_CORE)){
                    Map<String,Object> param=new HashMap<String, Object>();
                    param.put("custNo", custRelation.getCustNo());
                    param.put("custName", custRelation.getCustName());
                    param.put("coreCustNo", custRelation.getRelateCustno());
                    param.put("coreCustName", custRelation.getRelateCustname());
                    param.put("operName", custRelation.getOperName());
                    scfPushWechatService.pushVerifySend(param);
                }
            }
        }
        return bool;
    }


    /****
     * 查询选择的客户类型的客户信息
     * @param anCustType 需要关联的客户类型
     * @param anCustNo   关联客户类型的客户号
     * @return
     */
    public List<SimpleDataEntity> findCustInfoOld(final String anCustType,Long anCustNo,final String custName){
        if(anCustNo==null){ // 如果查询的客户类型为空，则获取当前登录的客户号
            anCustNo=custInfoService.findCustNo();
        }
        BTAssert.notNull(anCustNo, "查询的客户号不能为空");
        final List<SimpleDataEntity> custList=new ArrayList<SimpleDataEntity>();
        final Map<String, Object> anMap=new HashMap<String, Object>();
        if(BetterStringUtils.isNotBlank(custName)){
            anMap.put("LIKEcustName", "%" + custName + "%");
        }
        logger.info("findCustInfo anMap:"+anMap);
        for(final CustInfo custInfo:custAccountService.findValidCustInfo(anMap)){
            if(BetterStringUtils.isNoneBlank(custInfo.getOperOrg())){
                final CustCertInfo certInfo=custCertService.findCertByOperOrg(custInfo.getOperOrg());
                if(certInfo!=null && BetterStringUtils.equalsIgnoreCase(certInfo.getRuleList(), anCustType) && checkExist(anCustType,anCustNo,custInfo.getCustNo())){
                    custList.add(new SimpleDataEntity(custInfo.getCustName(), String.valueOf(custInfo.getCustNo())));
                }
            }
        }
        return custList;
    }
    
    /****
     * 查询选择的客户类型的客户信息
     * @param anCustType 需要关联的客户类型
     * @param anCustNo   关联客户类型的客户号
     * @return
     */
    public List<SimpleDataEntity> findCustInfo(final String anCustType,Long anCustNo,final String custName){
        if(anCustNo==null){ // 如果查询的客户类型为空，则获取当前登录的客户号
            anCustNo=custInfoService.findCustNo();
        }
        BTAssert.notNull(anCustNo, "查询的客户号不能为空");
        final List<SimpleDataEntity> custList=new ArrayList<SimpleDataEntity>();
        final Map<String, Object> anMap=new HashMap<String, Object>();
        if(BetterStringUtils.isNotBlank(custName)){
            anMap.put("LIKEcustName", "%" + custName + "%");
        }
        String type=convertType(anCustType);
        if(type!=null){
            anMap.put("custType", type);
        }
        logger.info("findCustInfo anMap:"+anMap);
        
        for(final CustMajor custMajorInfo:custMajorService.findCustMajorByMap(anMap)){
            if(checkExist(anCustType,anCustNo,custMajorInfo.getCustNo())){
                custList.add(new SimpleDataEntity(custMajorInfo.getCustName(), String.valueOf(custMajorInfo.getCustNo())));
            }
        }
        return custList;
    }
    
    public String convertType(String anCustType){
        switch(anCustType){
            case "CORE_USER":
                anCustType="2";
                break;
            case "FACTOR_USER":
                anCustType="3";
                break;
            case "PLATFORM_USER":
                anCustType="0";
                break;
            case "WOS":
                anCustType="1";
                break;
            default:
                anCustType=null;
                break;
        }
        return anCustType;
    }

    public Page<CustRelation> queryCustRelationInfo(final Long anCustNo, final PlatformBaseRuleType anRole, final String anRelationType,final String anFlag, final int anPageNum, final int anPageSize) {
        BTAssert.notNull(anCustNo, "查询的客户号不能为空");

        PageHelper.startPage(anPageNum, anPageSize, Integer.parseInt(anFlag) == 1);



        final Page<CustRelation> page=custRelationService.findCustRelationInfo(anCustNo,anRelationType, anRole);
        for (final CustRelation custRelation:page) {
            if(StringUtils.equalsIgnoreCase(CustomerConstants.RELATE_TYPE_CORE_FACTOR, custRelation.getRelateType()) || StringUtils.equalsIgnoreCase(CustomerConstants.RELATE_TYPE_ELEC_CONTRACT, custRelation.getRelateType())){
                final String custName=custRelation.getCustName();
                custRelation.setCustName(custRelation.getRelateCustname());
                custRelation.setRelateCustname(custName);
            }
        }
        return page;
    }


    /****
     * 过滤已经关联的客户
     * @param anCustType
     * @param anCustNo
     * @return
     */
    public boolean checkExist(final String anCustType,final Long anCustNo,final Long anCustRelationNo){
        CustRelation custRelation=null;
        if(UserUtils.supplierUser() && BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.CORE_USER.toString())){ // 供应商与核心企业的关系
            custRelation=custRelationService.findCustRelation(anCustNo, anCustRelationNo, CustomerConstants.RELATE_TYPE_SUPPLIER_CORE);
        }else if(UserUtils.supplierUser() && BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.FACTOR_USER.toString())){ // 供应商与保理公司的关系
            custRelation=custRelationService.findCustRelation(anCustNo, anCustRelationNo, CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR);
        }else if(UserUtils.coreUser() && BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.SUPPLIER_USER.toString())){ // 核心企业与供应商的关系
            custRelation=custRelationService.findCustRelation(anCustRelationNo, anCustNo, CustomerConstants.RELATE_TYPE_SUPPLIER_CORE);
        }else if(UserUtils.coreUser() && BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.FACTOR_USER.toString())){ // 核心企业与保理公司的关系
            custRelation=custRelationService.findCustRelation(anCustNo, anCustRelationNo, CustomerConstants.RELATE_TYPE_CORE_FACTOR);
        }else if(UserUtils.coreUser() && BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.SELLER_USER.toString())){ // 核心企业与经销商的关系
            custRelation=custRelationService.findCustRelation(anCustRelationNo, anCustNo, CustomerConstants.RELATE_TYPE_SELLER_CORE);
        }else if(UserUtils.sellerUser()  && BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.CORE_USER.toString())){ // 经销商与核心企业关系
            custRelation=custRelationService.findCustRelation(anCustNo, anCustRelationNo, CustomerConstants.RELATE_TYPE_SELLER_CORE);
        }else if(UserUtils.sellerUser()  && BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.FACTOR_USER.toString())){ // 经销商与保理公司关系
            custRelation=custRelationService.findCustRelation(anCustNo, anCustRelationNo, CustomerConstants.RELATE_TYPE_SELLER_FACTOR);
        }else if(UserUtils.sellerUser()  && BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.WOS.toString())){ // 与电子合同服务商关系
            custRelation=custRelationService.findCustRelation(anCustNo, anCustRelationNo, CustomerConstants.RELATE_TYPE_ELEC_CONTRACT);
        }
        if(custRelation==null){
            return true;
        }else{
            return false;
        }
    }

    /****
     * 添加关联信息
     * @param anCustType
     * @param anCustNo
     * @return
     */
    public CustRelation findCustRelation(final String anCustType,final Long anCustNo,final Long anCustRelationNo){
        final CustRelation custRelation=new CustRelation();
        custRelation.setCustNo(anCustNo);
        custRelation.setCustName(custAccountService.queryCustName(anCustNo));
        custRelation.setRelateCustno(anCustRelationNo);
        custRelation.setRelateCustname(custAccountService.queryCustName(anCustRelationNo));
        custRelation.setCustType("0");
        custRelation.setBusinStatus("3");
        custRelation.setLastStatus("1");
        if(UserUtils.supplierUser() && BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.CORE_USER.toString())){ // 供应商与核心企业的关系
            custRelation.setRelateType(CustomerConstants.RELATE_TYPE_SUPPLIER_CORE);
        }else if(UserUtils.supplierUser() && BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.FACTOR_USER.toString())){ // 供应商与保理公司的关系
            custRelation.setRelateType(CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR);
        }else if(UserUtils.coreUser() && BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.SUPPLIER_USER.toString())){ // 核心企业与供应商的关系
            custRelation.setCustNo(anCustRelationNo);
            custRelation.setCustName(custAccountService.queryCustName(anCustRelationNo));
            custRelation.setRelateCustno(anCustNo);
            custRelation.setRelateCustname(custAccountService.queryCustName(anCustNo));
            custRelation.setRelateType(CustomerConstants.RELATE_TYPE_SUPPLIER_CORE);
        }else if(UserUtils.coreUser() && BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.FACTOR_USER.toString())){ // 核心企业与保理公司的关系
            custRelation.setRelateType(CustomerConstants.RELATE_TYPE_CORE_FACTOR);
        }else if(UserUtils.coreUser() && BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.SELLER_USER.toString())){ // 核心企业与经销商的关系
            custRelation.setCustNo(anCustRelationNo);
            custRelation.setCustName(custAccountService.queryCustName(anCustRelationNo));
            custRelation.setRelateCustno(anCustNo);
            custRelation.setRelateCustname(custAccountService.queryCustName(anCustNo));
            custRelation.setRelateType(CustomerConstants.RELATE_TYPE_SELLER_CORE);
        }else if(UserUtils.sellerUser()  && BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.CORE_USER.toString())){ // 经销商与核心企业关系
            custRelation.setRelateType(CustomerConstants.RELATE_TYPE_SELLER_CORE);
        }else if(UserUtils.sellerUser()  && BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.FACTOR_USER.toString())){ // 经销商与保理公司关系
            custRelation.setRelateType(CustomerConstants.RELATE_TYPE_SELLER_FACTOR);
        }else if(BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.WOS.toString())){ // 与电子合同服务商的关系
            custRelation.setRelateType(CustomerConstants.RELATE_TYPE_ELEC_CONTRACT);
        }
        return custRelation;
    }

    /***
     * 查询电子合同服务商客户号
     * @return
     */
    public List<SimpleDataEntity> findElecAgreementServiceCust(){
        final List<SimpleDataEntity> custTypeList=new ArrayList<SimpleDataEntity>();
        for(final CustMajor custMajor:custMajorService.findCustMajorByCustCorp("wos")){
            custTypeList.add(new SimpleDataEntity(custMajor.getCustName(), String.valueOf(custMajor.getCustNo())));
        }
        return custTypeList;
    }

    /****
     * 查询要上传的文件类型
     * @param anFactorNo 关联附件公司客户号
     * @return
     */
    public List<CustFileItem> findCustAduitTemp(final Long anRelateCustNo,final Long anSelectCustNo){
        logger.info("findCustAduitTemp,anRelateCustNo:"+anRelateCustNo);
        final Map<String, Object> anMap=setParam(anRelateCustNo,anSelectCustNo);
        anMap.put("businStatus", CustomerConstants.RELATION_STATUS_ADUIT);
        final CustRelation custRelation=Collections3.getFirst(custRelationService.selectByProperty(anMap));
        if(custRelation!=null){ // 审核通则取正式表中的附件数据
            return custFileItemService.findCustFileAduit(custRelation.getCustNo(), custRelation.getRelateCustno());
        }else{
            final CustMajor custMajor=custMajorService.findCustMajorByCustNo(anRelateCustNo);
            if(custMajor!=null){
                return custFileAduitTempService.findCustAduitTemp(anRelateCustNo,anSelectCustNo, agencyAuthFileGroupService.findAuthorFileGroup(custMajor.getCustCorp(), "01"));
            }else{
                throw new BytterTradeException("无记录");
            }
        }
    }

    /****
     * 保存附件（微信端）
     * @param anRelateCustNo 关联客户号
     * @param anCustNo  上传的文件客户号
     * @param anFileTypeName 文件类型名称
     * @param anFileMediaId  微信标识
     */
    public CustFileItem addCustTempFile(final Long anRelateCustNo,final String anFileTypeName, final String anFileMediaId,final String anCustType){
        final Long anCustNo=custInfoService.findCustNo(); // 获取当前登录的客户号
        logger.info("当前登录客户号是："+anCustNo);
        // 查询文件类型名称
        final CustMajor custMajor=custMajorService.findCustMajorByCustNo(anRelateCustNo);
        logger.info("custMajor:"+custMajor);
        final Map<String,Object> anMap=new HashMap<String, Object>();
        anMap.put("agencyNo", custMajor.getCustCorp());
        anMap.put("fileInfoType", anFileTypeName);
        final AgencyAuthorFileGroup authorFIleGroup=agencyAuthFileGroupService.findAuthorFileGroupByMap(anMap);
        logger.info("authorFIleGroup:"+authorFIleGroup);
        final CustFileItem fileItem = custWeChatService.saveWechatFile(anFileTypeName, anFileMediaId);
        fileItem.setFileDescription(authorFIleGroup.getDescription());
        fileItem.setBatchNo(custFileItemService.updateCustFileItemInfo(fileItem.getId().toString(), null));
        logger.info("fileItem:"+fileItem);
        final CustFileAduitTemp custFileAduitTemp=new CustFileAduitTemp();
        custFileAduitTemp.setCustNo(anCustNo);
        custFileAduitTemp.setAduitCustNo(anRelateCustNo);
        custFileAduitTemp.setId(fileItem.getBatchNo());
        custFileAduitTemp.setWorkType(anFileTypeName);
        custFileAduitTemp.setAuditStatus("1");
        final CustOperatorInfo custOperator = (CustOperatorInfo) UserUtils.getPrincipal().getUser();
        custFileAduitTemp.setOperNo(String.valueOf(custOperator.getId()));
        custFileAduitTemp.initValue();
        if(BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.WOS.toString())){ // 沃通附件不需要审核，直接保存到正式表中
            final CustFileAduit custFileAduit=new CustFileAduit();
            BeanMapper.copy(custFileAduitTemp,custFileAduit);
            custFileItemService.addCustFileAduit(custFileAduit);
        }

        custFileAduitTempService.addCustFileAduitTemp(custFileAduitTemp);

        return fileItem;
    }

    /***
     * 删除附件
     * @param anId 附件id
     * @return
     */
    public boolean saveDeleteCustAduitTempFile(final Long anId){
        custFileItemService.delCustFileAduit(anId);// 删除审核表
        custFileAduitTempService.saveDeleteFileAduitTemp(anId); // 删除审核临时表
        return custOpenAccountTmpService.saveDeleteSingleFile(anId)>0; // 删除附件
    }

    /***
     * 添加保理公司/电子合同服务商 关联关系
     * @param anFactorCustType 保理公司关联类型
     * @param anWosCustType    电子合同服务商关联类型
     * @param anCustNo    申请的客户号
     * @param anFactorCustStr 保理公司客户号
     * @param anWosCustStr  电子合同服务商客户号
     * @return
     *      保理公司添加成功返回，电子合同服务商直接通过
     */
    public boolean addFactorCustRelation(final String anFactorCustType,final String anWosCustType,final String anFactorCustStr,final String anWosCustStr,Long anCustNo){
        synchronized(this){
            if(anCustNo==null){
                anCustNo=custInfoService.findCustNo(); // 获取当前登录的客户号
            }
            BTAssert.notNull(anFactorCustStr, "关联保理公司客户号不能为空");
            BTAssert.notNull(anWosCustStr, "关联电子合同服务商客户号不能为空");
            addFactorCustRelation(anWosCustType,anWosCustStr,anCustNo); // 添加电子服务商关系
            addFactorCustRelation(anFactorCustType,anFactorCustStr,anCustNo); // 添加保理关系
        }
        return true;
    }

    /***
     * 检查附件是否全部上传
     * @param anCustNo
     * @param anRelateCustNo
     */
    public void checkAduitFileExist(final Long anCustNo,final Long anRelateCustNo){
        final CustMajor custMajor=custMajorService.findCustMajorByCustNo(anRelateCustNo);
        if(custFileAduitTempService.checkCustFileAduitTempExist(anRelateCustNo,anCustNo, agencyAuthFileGroupService.findAuthorFileGroup(custMajor.getCustCorp(), "01"))){
            throw new BytterTradeException(custMajor.getCustName()+"\n资料不全");
        }
    }

    /***
     * 添加保理公司/电子合同服务关联关系
     * @param anCustType
     * @param anRelationCustStr
     * @param anCustNo
     * @return
     */
    public void addFactorCustRelation(final String anCustType,final String anRelationCustStr,final Long anCustNo){
        for(final String relationCust:anRelationCustStr.split(",")){
            // 如果是保理公司或电子合同服务商，则要判断附件是否都已上传，有未上传的附件，则提示绑定失败，重新上传
            checkAduitFileExist(anCustNo, Long.parseLong(relationCust));

            final CustRelation custRelation=findCustRelation(anCustType, anCustNo, Long.parseLong(relationCust)); // 初始类型数据
            CustRelation requestCustRelation= custRelationService.findCustRelation(custRelation.getCustNo(), custRelation.getRelateCustno(), custRelation.getRelateType());
            logger.info("custRelation:"+custRelation);
            logger.info("requestCustRelation:"+requestCustRelation);
            if(requestCustRelation!=null && BetterStringUtils.equalsIgnoreCase(requestCustRelation.getBusinStatus(), CustomerConstants.RELATION_STATUS_BACK)){ // 存在并且已经驳回的，将关系删除
                custRelationService.delete(requestCustRelation);
                requestCustRelation=null;
            }
            if(requestCustRelation==null){
                custRelation.initAddValue();
                custRelation.setBusinStatus(CustomerConstants.RELATION_STATUS_REQUEST);// 初始值为已申请
                if(BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.FACTOR_USER.toString())){ // 保理公司初始添加状态为未开通，电子服务合同直接通过
                    // 添加关系审核记录表
                    addAuditCustRelation(custRelation,"申请开通业务","申请开通业务");
                }
                final int insertValue=custRelationService.insert(custRelation); // 添加关系
                logger.info("添加关系值："+insertValue);
                if(insertValue>0){
                    // 调用开通保理业务的消息推送
                    final MQMessage anMessage = new MQMessage("CUSTOMER_OPEN_SCF_ACCOUNT");
                    logger.info("调用消息发送接口："+anMessage);
                    try {
                        anMessage.setObject(custRelation);
                        anMessage.addHead("type", CustomerConstants.RELATION_STATUS_REQUEST);// 开通申请
                        anMessage.addHead("operator", UserUtils.getOperatorInfo());
                        final SendResult result=betterProducer.sendMessage(anMessage);
                        logger.info("result:"+result);
                    }
                    catch (final Exception e) {
                        logger.error("异步消息发送失败！", e);
                    }
                }
            }
        }
    }

    /***
     * 添加审核记录
     * @param anCustRelation
     * @param anAduitOpinion
     * @param anTaskName
     */
    public void addAuditCustRelation(final CustRelation anCustRelation,final String anAduitOpinion,final String anTaskName){
        if(BetterStringUtils.equalsIgnoreCase("4", anCustRelation.getBusinStatus())){ // 驳回
            custRelationAuditService.addRefuseCustRelation(anCustRelation, anCustRelation.getRelateCustname(), anAduitOpinion, anTaskName);
        }else{
            String anAuditAgency=anCustRelation.getRelateCustname();
            if(BetterStringUtils.equalsIgnoreCase("1", anCustRelation.getBusinStatus())){
                anAuditAgency=anCustRelation.getCustName();
            }
            custRelationAuditService.addAuditCustRelation(anCustRelation, anAuditAgency, anAduitOpinion, anTaskName);
        }
    }

    /***
     * 查询客户申请基本信息
     * @param anCustNo 申请客户号
     */
    public FactorBusinessRequestData findFactorRequestInfo(final Long anCustNo){
        // 得到当前登录用户
        CustInfo custInfo=UserUtils.getDefCustInfo();
        if(anCustNo!=null){
            custInfo=custAccountService.findCustInfo(anCustNo);// 查询企业基本信息
        }
        if(custInfo==null){ // 判断注册情况下，不退出后绑定的客户信息
            final Collection<CustInfo> custInfoList=custMechBaseService.queryCustInfo();
            custInfo=Collections3.getFirst(custInfoList);
        }
        final FactorBusinessRequestData businessRequestData=new FactorBusinessRequestData();
        businessRequestData.setCustNo(custInfo.getCustNo());
        businessRequestData.setCustName(custInfo.getCustName());
        businessRequestData.setIdentNo(custInfo.getIdentNo());
        businessRequestData.setIdentType(custInfo.getIdentType());

        final CustMechBase custMechBase=custMechBaseService.findCustMechBaseByCustNo(custInfo.getCustNo());
        businessRequestData.setOrgCode(custMechBase.getOrgCode());
        businessRequestData.setLawName(custMechBase.getLawName());

        final CustMechBankAccount mechBankAccount=custMechBankAccountService.findDefaultCustMechBankAccount(custInfo.getCustNo());
        if(mechBankAccount!=null){
            businessRequestData.setBankAccount(mechBankAccount.getBankAcco());
            businessRequestData.setBankAccountName(mechBankAccount.getBankAccoName());
            businessRequestData.setBankName(mechBankAccount.getBankName());
        }
        final CustMechBusinLicence custMechBusinLicence=custMechBusinLicenceService.findBusinLicenceByCustNo(custInfo.getCustNo());
        if(custMechBusinLicence!=null){
            businessRequestData.setTaxCode(custMechBusinLicence.getTaxNo());
        }

        CustOperatorInfo custOperator =operatorService.findCustClerkMan(custInfo.getOperOrg(),"1");
        if(custOperator==null){
            custOperator =operatorService.findCustClerkMan(custInfo.getOperOrg(),"0");
        }
        businessRequestData.setOperName(custOperator.getName());
        businessRequestData.setOperIdentNo(custOperator.getIdentNo());
        businessRequestData.setOperIdentType(custOperator.getIdentType());
        businessRequestData.setAddress(custOperator.getAddress());
        businessRequestData.setZipCode(custOperator.getZipCode());
        businessRequestData.setPost(custOperator.getZipCode());
        businessRequestData.setPhone(custOperator.getPhone());
        businessRequestData.setMobileNo(custOperator.getMobileNo());
        businessRequestData.setEmail(custOperator.getEmail());
        businessRequestData.setFax(custOperator.getFaxNo());
        return businessRequestData;
    }

    /***
     * 添加客户文件关系
     * @param anRelationCustNo 关联的客户号
     * @param anFileIds 上传的文件列表(以,分隔)
     * @param anCustType 客户类型
     */
    public void saveCustFileAduitTemp(final Long anRelateCustNo,final String anFileIds,final String anCustType,Long anCustNo){
        if(anCustNo==null){
            anCustNo=custInfoService.findCustNo(); // 获取当前登录的客户号
        }
        custFileAduitTempService.saveCustFileAduitTemp(anCustNo, anRelateCustNo, anFileIds, anCustType);
    }

    /***
     * 查询附件
     * @param anCustNo
     * @return
     */
    public List<CustFileItem> findRelateAduitTempFile(final Long anCustNo){
        return custFileAduitTempService.findRelateAduitTempFile(anCustNo);
    }

    /***
     * 保存受理审核
     * @param anMap
     */
    public void saveAcceptAduit(final Map<String, Object> anMap){
        final Long relateCustNo=custInfoService.findCustNo(); // 获取当前登录的客户号
        // 查询选择的客户号是供应商还是核心企业
        final Long custNo=Long.parseLong(anMap.get("custNo").toString());
        final String relateType=(String)anMap.get("relateType");
        // 查询关联的对象
        final CustRelation custRelation=custRelationService.findCustRelation(custNo, relateCustNo,relateType);
        custRelation.setLastStatus(custRelation.getBusinStatus());
        custRelation.setBusinStatus(anMap.get("aduitStatus").toString());
        custRelationService.updateByPrimaryKey(custRelation);
        String taskName="";
        if(BetterStringUtils.equalsIgnoreCase(CustomerConstants.RELATION_STATUS_ACCEPT, anMap.get("aduitStatus").toString())){ // 受理
            taskName="保理机构受理";
        }else if(BetterStringUtils.equalsIgnoreCase(CustomerConstants.RELATION_STATUS_ADUIT, anMap.get("aduitStatus").toString())){ // 审批
            taskName="保理机构审批";
        }else if(BetterStringUtils.equalsIgnoreCase(CustomerConstants.RELATION_STATUS_BACK, anMap.get("aduitStatus").toString())){ // 驳回
            taskName="保理机构审批";
        }
        // 添加审核记录
        addAuditCustRelation(custRelation,anMap.get("aduitOpinion").toString(),taskName);
        // 判断审核通过的文件
        if(BetterStringUtils.isNoneBlank((String)anMap.get("passFiles")) || BetterStringUtils.isNoneBlank((String)anMap.get("failFiles"))){
            custFileAduitTempService.saveAcceptFileTemp(String.valueOf(anMap.get("passFiles")), String.valueOf(anMap.get("failFiles")));
        }
        // 审核通过时将文件添加到审核附件关系表中
        if(BetterStringUtils.equalsIgnoreCase(anMap.get("aduitStatus").toString(), CustomerConstants.RELATION_STATUS_ADUIT)){
            custFileAduitTempService.saveAduitFile(custNo,relateCustNo);
        }
    }

    /***
     * 查询审核/受理记录
     * @param anCustNo
     * @return
     */
    public List<CustRelationAudit> findCustRelateAduitRecord(final Long anCustNo,final Long anSelectCustNo,final String anRelateType){
        Map<String, Object> anMap=new HashMap<String, Object>();
        if(anSelectCustNo!=null){
            anMap=setParam(anCustNo,anSelectCustNo);
        }else{
            anMap=setLoginParam(anCustNo);
        }
        if(BetterStringUtils.isNotBlank(anRelateType)){
            anMap.put("relateType", anRelateType);
        }
        return custRelationAuditService.selectByProperty(anMap);
    }

    private Map<String, Object> setParam(final Long anCustNo,final Long anSelectCustNo){
        final Map<String, Object> anMap=new HashMap<String, Object>();
        if(UserUtils.factorUser()){
            anMap.put("custNo", anCustNo);
            anMap.put("relateCustno", anSelectCustNo);
//            anMap.put("relateType", findRelateType(anCustNo));
        }else{
            anMap.put("custNo", anSelectCustNo);
            anMap.put("relateCustno", anCustNo);
//            anMap.put("relateType", findRelateType(anSelectCustNo));
        }
        return anMap;
    }
    
    private Map<String, Object> setLoginParam(final Long anCustNo){
        final Map<String, Object> anMap=new HashMap<String, Object>();
        if(UserUtils.factorUser()){
            anMap.put("custNo", anCustNo);
            anMap.put("relateCustno", custInfoService.findCustNo());
//            anMap.put("relateType", findRelateType(anCustNo));
        }else{
            anMap.put("custNo", custInfoService.findCustNo());
            anMap.put("relateCustno", anCustNo);
//            anMap.put("relateType", findRelateType(custInfoService.findCustNo()));
        }
        return anMap;
    }

    /***
     * 获取客户号的关系类型
     * @param anCustNo
     * @return
     */
    public String findRelateType(final Long anCustNo){
        final CustInfo custInfo=custAccountService.findCustInfo(anCustNo);
        final CustCertInfo certInfo=custCertService.findCertByOperOrg(custInfo.getOperOrg());
        String relateType="";
        if(BetterStringUtils.equalsIgnoreCase(certInfo.getRuleList(),PlatformBaseRuleType.SUPPLIER_USER.toString())){ // 供应商
            relateType=CustomerConstants.RELATE_TYPE_SUPPLIER_FACTOR;
        }else if(BetterStringUtils.equalsIgnoreCase(certInfo.getRuleList(),PlatformBaseRuleType.CORE_USER.toString())){ // 核心企业
            relateType=CustomerConstants.RELATE_TYPE_CORE_FACTOR;
        }else if(BetterStringUtils.equalsIgnoreCase(certInfo.getRuleList(),PlatformBaseRuleType.SELLER_USER.toString())){ // 经销商
            relateType=CustomerConstants.RELATE_TYPE_SELLER_FACTOR;
        }
        return relateType;
    }
}
