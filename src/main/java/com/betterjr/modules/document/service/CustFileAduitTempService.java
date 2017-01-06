package com.betterjr.modules.document.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.data.PlatformBaseRuleType;
import com.betterjr.common.mapper.BeanMapper;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.account.dubbo.interfaces.ICustInfoService;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.document.IAgencyAuthFileGroupService;
import com.betterjr.modules.document.dao.CustFileAduitTempMapper;
import com.betterjr.modules.document.entity.AgencyAuthorFileGroup;
import com.betterjr.modules.document.entity.CustFileAduit;
import com.betterjr.modules.document.entity.CustFileAduitTemp;
import com.betterjr.modules.document.entity.CustFileItem;

/****
 * 临时附件审核表
 * @author hubl
 *
 */
@Service
public class CustFileAduitTempService extends BaseService<CustFileAduitTempMapper, CustFileAduitTemp>{

    @Autowired
    private CustFileItemService custFileItemService;
    @Autowired
    private CustFileAuditService custFileAuditService;
    @Reference(interfaceClass=ICustInfoService.class)
    private ICustInfoService custInfoService;
    @Reference(interfaceClass=IAgencyAuthFileGroupService.class)
    private IAgencyAuthFileGroupService agencyAuthFileGroupService;
      
    /***
     * 获取类型附件列表
     * @param anCustNo 客户号
     * @param anAgencyAuthorFileGroupList 文件类型
     * @return
     */
    public List<CustFileItem> findCustFileAduitTempByCustNoAndType(Long anCustNo,List<AgencyAuthorFileGroup> anAgencyAuthorFileGroupList){
        List<CustFileItem>  custFileItemList=new ArrayList<CustFileItem>();
        for(AgencyAuthorFileGroup agencyAuthorFileGroup:anAgencyAuthorFileGroupList){
            CustFileAduitTemp custFileAduitTemp=findCustFileAduitTempByType(anCustNo,agencyAuthorFileGroup.getFileInfoType());
            if(custFileAduitTemp!=null){
                CustFileItem custFileItem=custFileItemService.findOneByBatchNo(custFileAduitTemp.getId());
                if(custFileItem!=null && BetterStringUtils.equalsIgnoreCase(custFileAduitTemp.getWorkType(), custFileItem.getFileInfoType())){
                    custFileItem.setFileDescription(agencyAuthorFileGroup.getDescription());
                    custFileItem.setBusinStatus(custFileAduitTemp.getAuditStatus());
                    custFileItemList.add(custFileItem);
                }else{
                    custFileItem=new CustFileItem();
                    custFileItem.setFileInfoType(agencyAuthorFileGroup.getFileInfoType());
                    custFileItem.setFileDescription(agencyAuthorFileGroup.getDescription());
                    custFileItemList.add(custFileItem);
                }
            }else{
                // 临时文件里面是空的，则再从正式文件关系表中获取开户时上传的附件
                CustFileItem custFileItem=findCustFileAduitByFileType(agencyAuthorFileGroup.getFileInfoType());
                if(custFileItem==null){
                    custFileItem=new CustFileItem();
                    custFileItem.setFileInfoType(agencyAuthorFileGroup.getFileInfoType());
                    custFileItem.setFileDescription(agencyAuthorFileGroup.getDescription());
                }
                custFileItemList.add(custFileItem);
            }
        }
        return custFileItemList;
    }
    
    /***
     * 获取当前登录时开户的附件
     * @param anFileType
     * @return
     */
    public CustFileItem findCustFileAduitByFileType(String anFileType){
        List<String> fileTypeList=new ArrayList<String>();
        fileTypeList.add(anFileType);
        List<Long> batchNos=custFileAuditService.findBatchNo(custInfoService.findCustNo(),fileTypeList);
        CustFileItem custFileItem=custFileItemService.findOneByBatchNo(Collections3.getFirst(batchNos));
        if(custFileItem!=null){
            custFileItem.setFileDescription(agencyAuthFileGroupService.findAuthFileGroup(custFileItem.getFileInfoType()).getDescription());
        }
        return custFileItem;
    }
    
    public CustFileAduitTemp findCustFileAduitTempByType(Long anCustNo,String anType){
        Map<String, Object> anMap=setLoginParam(anCustNo);
        anMap.put("workType", anType);
        return Collections3.getFirst(this.selectByProperty(anMap));
    }
    
    private Map<String, Object> setLoginParam(Long anCustNo){
        Map<String, Object> anMap=new HashMap<String, Object>();
        // 当前登录的用户是保理公司则传进查询条件要变动
        if(UserUtils.factorUser()){
            anMap.put("custNo", anCustNo);
            anMap.put("aduitCustNo", custInfoService.findCustNo());   
        }else{
            anMap.put("custNo", custInfoService.findCustNo());
            anMap.put("aduitCustNo", anCustNo);   
        }
        return anMap;
    }
    
    /***
     * 保存临时文件
     * @param anCustFileAduitTemp
     */
    public boolean addCustFileAduitTemp(CustFileAduitTemp anCustFileAduitTemp){
        return this.insert(anCustFileAduitTemp)>0;
    }
    
    /***
     * 检查文件是否都已上传
     * @param anCustNo 关联上传文件的客户号
     * @param anAgencyAuthorFileGroupList 文件类型列表
     * @return
     */
    public boolean checkCustFileAduitTempExist(Long anCustNo,List<AgencyAuthorFileGroup> anAgencyAuthorFileGroupList){
        boolean bool=false;
        for(AgencyAuthorFileGroup agencyAuthorFileGroup:anAgencyAuthorFileGroupList){
            CustFileAduitTemp custFileAduitTemp=findCustFileAduitTempByType(anCustNo,agencyAuthorFileGroup.getFileInfoType());
            if(custFileAduitTemp==null){ // 临时表若为空，则在正式表中查询出备份到临时表一份
                CustFileItem custFileItem=findCustFileAduitByFileType(agencyAuthorFileGroup.getFileInfoType());
                if(custFileItem!=null){
                    custFileAduitTemp=new CustFileAduitTemp();
                    custFileAduitTemp.setCustNo(custInfoService.findCustNo());
                    custFileAduitTemp.setAduitCustNo(anCustNo);
                    custFileAduitTemp.setId(custFileItemService.updateCustFileItemInfo(custFileItem.getId().toString(), null));
                    custFileAduitTemp.setWorkType(custFileItem.getFileInfoType());
                    custFileAduitTemp.setAuditStatus("1");
                    final CustOperatorInfo custOperator = (CustOperatorInfo) UserUtils.getPrincipal().getUser();
                    custFileAduitTemp.setOperNo(String.valueOf(custOperator.getId()));
                    custFileAduitTemp.initValue();
                    this.insert(custFileAduitTemp);
                }
            }
            if(custFileAduitTemp!=null){
                CustFileItem custFileItem=custFileItemService.findOneByBatchNo(custFileAduitTemp.getId());
                if(custFileItem==null){
                    bool=true;
                    break;
                }
            }else{
                bool=true;
                break;
            }
        }
        return bool;
    }
    
    /***
     * 删除临时审核附件关系表数据
     * @param anId 附件 id
     * @return
     */
    public boolean saveDeleteFileAduitTemp(Long anId){
        CustFileItem anFile = custFileItemService.selectByPrimaryKey(anId);
        BTAssert.notNull(anFile, "无法获取相应附件!");
        List<CustFileAduitTemp> custFileAduitTempList=this.selectByProperty("id", anFile.getBatchNo());
        CustFileAduitTemp fileAduitTemp=Collections3.getFirst(custFileAduitTempList);
        if(fileAduitTemp!=null){
            fileAduitTemp.setWorkType("-"+fileAduitTemp.getWorkType());
            fileAduitTemp.setAduitCustNo(-fileAduitTemp.getAduitCustNo());
            Map<String, Object> anMap=new HashMap<String, Object>();
            anMap.put("id", fileAduitTemp.getId());
            return this.updateByExample(fileAduitTemp, anMap)>0;
        }

        return true;
    }
    
    /***
     * 添加客户文件关系
     * @param anRelationCustNo 关联的客户号
     * @param fileIds 上传的文件列表(以,分隔)
     */
    public void saveCustFileAduitTemp(Long anCustNo,Long anRelateCustNo,String anFileIds,String anCustType){
        for(String fileId:anFileIds.split(",")){
            if(BetterStringUtils.isNoneBlank(fileId)){
                CustFileItem anFile = custFileItemService.selectByPrimaryKey(Long.parseLong(fileId));
    
                CustFileAduitTemp custFileAduitTemp=findCustFileAduitTempByType(anRelateCustNo,anFile.getFileInfoType());
                if(custFileAduitTemp==null){ //添加绑定关系
                    addCustFileRelate(anCustNo,anRelateCustNo,anFile,anCustType);
                }else if(BetterStringUtils.equalsIgnoreCase("0", custFileAduitTemp.getAuditStatus()) && BetterStringUtils.equalsIgnoreCase(String.valueOf(custFileAduitTemp.getId()), String.valueOf(anFile.getBatchNo()))){ // 审核失败且没有重新上传情况，将原来的文件状态改回成已上传状态
                    custFileAduitTemp.setAuditStatus("2");
                    Map<String, Object> anMap=new HashMap<String, Object>();
                    anMap.put("id", custFileAduitTemp.getId());
                    this.updateByExample(custFileAduitTemp, anMap);
                }else if(!BetterStringUtils.equalsIgnoreCase(String.valueOf(custFileAduitTemp.getId()), String.valueOf(anFile.getBatchNo()))){ // 将原来的废弃，添加新的绑定
                    custFileAduitTemp.setWorkType("-"+custFileAduitTemp.getWorkType());
                    custFileAduitTemp.setAduitCustNo(-custFileAduitTemp.getAduitCustNo());
                    Map<String, Object> anMap=new HashMap<String, Object>();
                    anMap.put("id", custFileAduitTemp.getId());
                    this.updateByExample(custFileAduitTemp, anMap);
                    // 附件修改
                    anFile.setBatchNo(-anFile.getBatchNo());
                    custFileItemService.updateByPrimaryKey(anFile);
                    // 删除沃通关系审核附件表
                    if(BetterStringUtils.equalsIgnoreCase(String.valueOf(PlatformBaseRuleType.WOS), anCustType)){
                        custFileAuditService.deleteByPrimaryKey(custFileAduitTemp.getId());
                    }
                    // 添加新关系
                    addCustFileRelate(anCustNo,anRelateCustNo,anFile,anCustType);
                }
            }
        }
    }
    
    /***
     * 添加客户文件关系
     * @param anCustNo
     * @param anRelateCustNo
     * @param anFile
     */
    public void addCustFileRelate(Long anCustNo,Long anRelateCustNo,CustFileItem anFile,String anCustType){
        final CustOperatorInfo custOperator = (CustOperatorInfo) UserUtils.getPrincipal().getUser();
        CustFileAduitTemp custFileAduitTemp=new CustFileAduitTemp();
        custFileAduitTemp.setCustNo(anCustNo);
        custFileAduitTemp.setAduitCustNo(anRelateCustNo);
        custFileAduitTemp.setId(custFileItemService.updateDuplicateCustFileItemInfo(anFile.getId(), custOperator));
        custFileAduitTemp.setWorkType(anFile.getFileInfoType());
        custFileAduitTemp.setAuditStatus("2");
        custFileAduitTemp.setOperNo(String.valueOf(custOperator.getId()));
        custFileAduitTemp.initValue();
        this.insert(custFileAduitTemp);
        if(BetterStringUtils.equalsIgnoreCase(String.valueOf(PlatformBaseRuleType.WOS), anCustType)){ // 如果是沃通服务，在正式关系表中添加文件关系
            CustFileAduit custFileAduit=new CustFileAduit();        
            BeanMapper.copy(custFileAduitTemp,custFileAduit);
            custFileAuditService.addCustFileAduit(custFileAduit);
        }
    }
    
    /***
     * 查询关系审核附件
     * @param anCustNo
     * @return
     */
    public List<CustFileItem> findRelateAduitTempFile(Long anCustNo){
        List<CustFileItem>  custFileItemList=new ArrayList<CustFileItem>();
        Map<String, Object> anMap=setLoginParam(anCustNo);
        anMap.put("auditStatus",new String[]{"0","1","2"});
        
        for(CustFileAduitTemp custFileAduitTemp:this.selectByProperty(anMap)){
            CustFileItem custFileItem=custFileItemService.findOneByBatchNo(custFileAduitTemp.getId());
            if(custFileItem!=null){
                custFileItem.setFileDescription(agencyAuthFileGroupService.findAuthFileGroup(custFileItem.getFileInfoType()).getDescription());
                custFileItemList.add(custFileItem);
            }
        }
        return custFileItemList;
    }
    
    /***
     * 保存附件关系
     * @param passFiles 审核通过的文件列表
     * @param failFiles 审核不通过的文件列表
     */
    public void saveAcceptFileTemp(String anPassFiles,String anFailFiles){
        for(String batchNo:anPassFiles.split(",")){ // 处理通过的文件列表
            List<CustFileAduitTemp> tempList=this.selectByProperty("id", batchNo);
            if(tempList!=null && tempList.size()>0){
                CustFileAduitTemp custFileAduitTemp=Collections3.getFirst(tempList);
                custFileAduitTemp.setAuditStatus("1");
                custFileAduitTemp.saveInitValue();
                Map<String, Object> anMap=new HashMap<String, Object>();
                anMap.put("id", custFileAduitTemp.getId());
                this.updateByExample(custFileAduitTemp, anMap);
            }
        }
        for(String batchNo:anFailFiles.split(",")){ // 处理不通过的文件列表
            List<CustFileAduitTemp> tempList=this.selectByProperty("id", batchNo);
            if(tempList!=null && tempList.size()>0){
                CustFileAduitTemp custFileAduitTemp=Collections3.getFirst(tempList);
                custFileAduitTemp.setAuditStatus("0");
                custFileAduitTemp.saveInitValue();
                Map<String, Object> anMap=new HashMap<String, Object>();
                anMap.put("id", custFileAduitTemp.getId());
                this.updateByExample(custFileAduitTemp, anMap);
            }
        }
    }
    
    /***
     * 查询关系附件保存到正式表中
     * @param anCustNo
     * @param anRelateCustNo
     */
    public void saveAduitFile(Long anCustNo,Long anRelateCustNo){
        Map<String, Object> anMap=new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("aduitCustNo", custInfoService.findCustNo());   
        anMap.put("auditStatus","1");
        for(CustFileAduitTemp custFileAduitTemp:this.selectByProperty(anMap)){
            CustFileAduit aduit=custFileAuditService.selectByPrimaryKey(custFileAduitTemp.getId());
            if(aduit==null){
                CustFileAduit custFileAduit=new CustFileAduit();        
                BeanMapper.copy(custFileAduitTemp,custFileAduit);
                custFileAuditService.addCustFileAduit(custFileAduit);
            }
        }
    }
}
