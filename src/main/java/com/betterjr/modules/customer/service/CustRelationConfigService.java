package com.betterjr.modules.customer.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.data.PlatformBaseRuleType;
import com.betterjr.common.data.SimpleDataEntity;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.cert.entity.CustCertInfo;
import com.betterjr.modules.cert.service.CustCertService;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.entity.CustRelation;
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

    /***
     * 判断当前登录的身份信息，并返回需要关联选择的客户类型
     * @return
     */
    public List<SimpleDataEntity> findCustType(){
        List<SimpleDataEntity> custTypeList=new ArrayList<SimpleDataEntity>();
        if(UserUtils.coreUser()){
            custTypeList.add(new SimpleDataEntity(PlatformBaseRuleType.SUPPLIER_USER.getTitle(),PlatformBaseRuleType.SUPPLIER_USER.toString()));
            custTypeList.add(new SimpleDataEntity(PlatformBaseRuleType.SELLER_USER.getTitle(),PlatformBaseRuleType.SELLER_USER.toString()));
//            custTypeList.add(new SimpleDataEntity(PlatformBaseRuleType.FACTOR_USER.getTitle(),PlatformBaseRuleType.FACTOR_USER.toString()));
        }else if(UserUtils.supplierUser() || UserUtils.sellerUser()){
            custTypeList.add(new SimpleDataEntity(PlatformBaseRuleType.CORE_USER.getTitle(),PlatformBaseRuleType.CORE_USER.toString()));
//            custTypeList.add(new SimpleDataEntity(PlatformBaseRuleType.FACTOR_USER.getTitle(),PlatformBaseRuleType.FACTOR_USER.toString()));
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
        return custRelationService.queryCustRelationInfo(anMap,anFlag,anPageNum,anPageSize);
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
        custRelation.setBusinStatus("1");
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
        }
        return custRelation;
    }
    
}
