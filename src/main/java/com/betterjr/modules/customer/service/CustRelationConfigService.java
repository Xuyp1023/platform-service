package com.betterjr.modules.customer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.data.PlatformBaseRuleType;
import com.betterjr.common.data.SimpleDataEntity;
import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.mapper.BeanMapper;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.mapper.pagehelper.PageHelper;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.cert.entity.CustCertInfo;
import com.betterjr.modules.cert.service.CustCertService;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.entity.CustMajor;
import com.betterjr.modules.customer.entity.CustRelation;
import com.betterjr.modules.document.IAgencyAuthFileGroupService;
import com.betterjr.modules.document.ICustFileAduitTempService;
import com.betterjr.modules.document.ICustFileService;
import com.betterjr.modules.document.entity.AgencyAuthorFileGroup;
import com.betterjr.modules.document.entity.CustFileAduit;
import com.betterjr.modules.document.entity.CustFileAduitTemp;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.document.service.CustFileAuditService;
import com.betterjr.modules.wechat.service.CustWeChatService;
/****
 * 客户关系添加
 * @author hubl
 *
 */
@Service
public class CustRelationConfigService {
    
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
    private CustOpenAccountTmp2Service custOpenAccountTmp2Service;
    @Autowired
    private CustFileAuditService custFileAuditService;
    @Autowired
    private CustRelationAuditService custRelationAuditService;
    @Reference(interfaceClass = ICustFileService.class)
    private ICustFileService custFileItemService;

    /***
     * 判断当前登录的身份信息，并返回需要关联选择的客户类型
     * @return
     */
    public List<SimpleDataEntity> findCustType(){
        List<SimpleDataEntity> custTypeList=new ArrayList<SimpleDataEntity>();
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
    public boolean addCustRelation(String anCustType,Long anCustNo,String anRelationCustStr){
        BTAssert.notNull(anCustType, "类型不能为空");
        BTAssert.notNull(anCustNo, "客户号不能为空");
        BTAssert.notNull(anRelationCustStr, "关联客户号不能为空");
        boolean bool=false;
        for(String relationCust:anRelationCustStr.split(",")){
            CustRelation custRelation=findCustRelation(anCustType, anCustNo, Long.parseLong(relationCust));
            if(custRelationService.findCustRelation(custRelation.getCustNo(), custRelation.getRelateCustno(), custRelation.getRelateType())==null){
                custRelation.initAddValue();
                custRelationService.insert(custRelation);
                bool=true;
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
    public List<SimpleDataEntity> findCustInfo(String anCustType,Long anCustNo,String custName){
        BTAssert.notNull(anCustNo, "查询的客户号不能为空");
        List<SimpleDataEntity> custList=new ArrayList<SimpleDataEntity>();
        Map<String, Object> anMap=new HashMap<String, Object>();
        if(BetterStringUtils.isNotBlank(custName)){
            anMap.put("LIKEcustName", "%" + custName + "%");
        }
        for(CustInfo custInfo:custAccountService.findValidCustInfo(anMap)){
            if(BetterStringUtils.isNoneBlank(custInfo.getOperOrg())){
                CustCertInfo certInfo=custCertService.findCertByOperOrg(custInfo.getOperOrg());
                if(certInfo!=null && BetterStringUtils.equalsIgnoreCase(certInfo.getRuleList(), anCustType) && checkExist(anCustType,anCustNo,custInfo.getCustNo())){
                    custList.add(new SimpleDataEntity(custInfo.getCustName(), String.valueOf(custInfo.getCustNo())));
                }
            }
        }
        return custList;
    }
    
    public Page<CustRelation> queryCustRelationInfo(final Long anCustNo,final String anRelationType,final String anFlag, final int anPageNum, final int anPageSize) {
        BTAssert.notNull(anCustNo, "查询的客户号不能为空");
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        if(BetterStringUtils.isNotBlank(anRelationType)){
            anMap.put("relateType", anRelationType);
        }
        
        if(UserUtils.coreUser()){// 如果是核心企业，还要查出以核心企业没条件的保理公司和电子合同信息
            PageHelper.startPage(anPageNum, anPageSize, Integer.parseInt(anFlag) == 1);
            Page<CustRelation> page=custRelationService.findCustRelationInfo(anCustNo,anRelationType);
            for (CustRelation custRelation:page) {
                if(StringUtils.equalsIgnoreCase(CustomerConstants.RELATE_TYPE_CORE_FACTOR, custRelation.getRelateType()) || StringUtils.equalsIgnoreCase(CustomerConstants.RELATE_TYPE_ELEC_CONTRACT, custRelation.getRelateType())){
                    String custName=custRelation.getCustName();
                    custRelation.setCustName(custRelation.getRelateCustname());
                    custRelation.setRelateCustname(custName);
                }
            }
            return page;
        }else{
            Page<CustRelation> page= custRelationService.queryCustRelationInfo(anMap,anFlag,anPageNum,anPageSize);
            return page;
        }
    }
    
    
    /****
     * 过滤已经关联的客户
     * @param anCustType
     * @param anCustNo
     * @return
     */
    public boolean checkExist(String anCustType,Long anCustNo,Long anCustRelationNo){
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
    public CustRelation findCustRelation(String anCustType,Long anCustNo,Long anCustRelationNo){
        CustRelation custRelation=new CustRelation();
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
        }else if(UserUtils.supplierUser() && BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.WOS.toString())){ // 供应商与电子合同服务商的关系
            custRelation.setRelateType(CustomerConstants.RELATE_TYPE_ELEC_CONTRACT);
        }
        return custRelation;
    }
    
    /***
     * 查询电子合同服务商客户号 
     * @return
     */
    public List<SimpleDataEntity> findElecAgreementServiceCust(){
        List<SimpleDataEntity> custTypeList=new ArrayList<SimpleDataEntity>();
        for(CustMajor custMajor:custMajorService.findCustMajorByCustCorp("wos")){
            custTypeList.add(new SimpleDataEntity(custMajor.getCustName(), String.valueOf(custMajor.getCustNo()))); 
        }
        return custTypeList;
    }
    
    /****
     * 查询要上传的文件类型
     * @param anFactorNo 关联附件公司客户号
     * @param anCustNo 需要上传文件关联的客户号
     * @return
     */
    public List<CustFileItem> findCustAduitTemp(Long anRelateCustNo,Long anCustNo){
        CustMajor custMajor=custMajorService.findCustMajorByCustNo(anRelateCustNo);
        if(custMajor!=null){
            return custFileAduitTempService.findCustAduitTemp(anRelateCustNo, agencyAuthFileGroupService.findAuthorFileGroup(custMajor.getCustCorp(), "01"));
        }else{
            throw new BytterTradeException("无记录");
        }
    }
    
    /****
     * 
     * @param anRelateCustNo 关联客户号
     * @param anCustNo  上传的文件客户号
     * @param anFileTypeName 文件类型名称
     * @param anFileMediaId  微信标识
     */
    public CustFileItem addCustTempFile(Long anRelateCustNo,Long anCustNo,String anFileTypeName, String anFileMediaId,String anCustType){
        // 查询文件类型名称
        CustMajor custMajor=custMajorService.findCustMajorByCustNo(anRelateCustNo);
        Map<String,Object> anMap=new HashMap<String, Object>();
        anMap.put("agencyNo", custMajor.getCustCorp());
        anMap.put("fileInfoType", anFileTypeName);
        AgencyAuthorFileGroup authorFIleGroup=agencyAuthFileGroupService.findAuthorFileGroupByMap(anMap);
        CustFileItem fileItem = custWeChatService.saveWechatFile(anFileTypeName, anFileMediaId);
        fileItem.setFileDescription(authorFIleGroup.getDescription());
        fileItem.setBatchNo(custFileItemService.updateCustFileItemInfo(fileItem.getId().toString(), null));
        CustFileAduitTemp custFileAduitTemp=new CustFileAduitTemp();
        custFileAduitTemp.setCustNo(anCustNo);
        custFileAduitTemp.setAduitCustNo(anRelateCustNo);
        custFileAduitTemp.setId(fileItem.getBatchNo());
        custFileAduitTemp.setWorkType(anFileTypeName);
        custFileAduitTemp.setAuditStatus("1");
        final CustOperatorInfo custOperator = (CustOperatorInfo) UserUtils.getPrincipal().getUser();
        custFileAduitTemp.setOperNo(String.valueOf(custOperator.getId()));
        custFileAduitTemp.initValue(custFileAduitTemp);
        if(BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.WOS.toString())){ // 沃通附件不需要审核，直接保存到正式表中
            CustFileAduit custFileAduit=new CustFileAduit();        
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
    public boolean saveDeleteCustAduitTempFile(Long anId){
        custFileItemService.delCustFileAduit(anId);// 删除审核表
        custFileAduitTempService.saveDeleteFileAduitTemp(anId); // 删除审核临时表
        return custOpenAccountTmp2Service.saveDeleteSingleFile(anId)>0; // 删除附件
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
    public boolean addFactorCustRelation(String anFactorCustType,String anWosCustType,Long anCustNo,String anFactorCustStr,String anWosCustStr){
        BTAssert.notNull(anFactorCustStr, "关联保理公司客户号不能为空");
        BTAssert.notNull(anWosCustStr, "关联电子合同服务商客户号不能为空");
        addFactorCustRelation(anWosCustType,anWosCustStr,anCustNo); // 添加电子服务商关系
        return addFactorCustRelation(anFactorCustType,anFactorCustStr,anCustNo); // 添加保理关系 
    }
    
    /***
     * 检查附件是否全部上传
     * @param anCustNo
     * @param anRelateCustNo
     */
    public void checkAduitFileExist(Long anCustNo,Long anRelateCustNo){
        CustMajor custMajor=custMajorService.findCustMajorByCustNo(anRelateCustNo);
        if(custFileAduitTempService.checkCustFileAduitTempExist(anRelateCustNo, agencyAuthFileGroupService.findAuthorFileGroup(custMajor.getCustCorp(), "01"))){
            throw new BytterTradeException("资料未上传完全");
        } 
    }
    
    /***
     * 添加保理公司/电子合同服务关联关系
     * @param anCustType
     * @param anRelationCustStr
     * @param anCustNo
     * @return
     */
    public boolean addFactorCustRelation(String anCustType,String anRelationCustStr,Long anCustNo){
        boolean bool=false;
        for(String relationCust:anRelationCustStr.split(",")){
            // 如果是保理公司或电子合同服务商，则要判断附件是否都已上传，有未上传的附件，则提示绑定失败，重新上传
            checkAduitFileExist(anCustNo, Long.parseLong(relationCust));
            
            CustRelation custRelation=findCustRelation(anCustType, anCustNo, Long.parseLong(relationCust));
            if(custRelationService.findCustRelation(custRelation.getCustNo(), custRelation.getRelateCustno(), custRelation.getRelateType())==null){
                custRelation.initAddValue();
                if(BetterStringUtils.equalsIgnoreCase(anCustType,PlatformBaseRuleType.FACTOR_USER.toString())){ // 保理公司初始添加状态为未开通，电子服务合同直接通过
                    custRelation.setBusinStatus("1");// 初始值为已申请
                    // 添加关系审核记录表
                    addAuditCustRelation(custRelation,"申请开通保理融资业务","申请开通保理融资业务");
                }
                custRelationService.insert(custRelation); // 添加关系
                bool=true;
            }
        }
        return bool;
    }
    
    /***
     * 添加审核记录
     * @param anCustRelation
     * @param anAduitOpinion
     * @param anTaskName
     */
    public void addAuditCustRelation(CustRelation anCustRelation,String anAduitOpinion,String anTaskName){
        custRelationAuditService.addAuditCustRelation(anCustRelation, anCustRelation.getRelateCustname(), anAduitOpinion, anTaskName);
    }
}
