package com.betterjr.modules.document.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.document.dao.CustFileAduitTempMapper;
import com.betterjr.modules.document.entity.AgencyAuthorFileGroup;
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
    
    /****
     * 查找类型附件 - 方法暂时废弃
     * @param anCustNo 客户号
     * @param anType   文件类型
     */
    public Map<String, Object> findCustFileAduitTempByCustNoAndTypeReturnMap(Long anCustNo,List<AgencyAuthorFileGroup> anAgencyAuthorFileGroupList){
        Map<String, Object> custFileMap=new HashMap<String, Object>();
        for(AgencyAuthorFileGroup agencyAuthorFileGroup:anAgencyAuthorFileGroupList){
            CustFileAduitTemp custFileAduitTemp=findCustFileAduitTempByType(anCustNo,agencyAuthorFileGroup.getFileInfoType());
            if(custFileAduitTemp!=null){
                CustFileItem custFileItem=custFileItemService.findOneByBatchNo(custFileAduitTemp.getId());
                if(BetterStringUtils.equalsIgnoreCase(custFileAduitTemp.getWorkType(), custFileItem.getFileInfoType())){
                    custFileMap.put(custFileAduitTemp.getWorkType(), custFileItem);
                }else{
                    custFileItem=new CustFileItem();
                    custFileItem.setFileInfoType(agencyAuthorFileGroup.getFileInfoType());
                    custFileItem.setFileName(agencyAuthorFileGroup.getDescription());
                    custFileMap.put(agencyAuthorFileGroup.getFileInfoType(), custFileItem);
                }
            }else{
                CustFileItem custFileItem=new CustFileItem();
                custFileItem.setFileInfoType(agencyAuthorFileGroup.getFileInfoType());
                custFileItem.setFileName(agencyAuthorFileGroup.getDescription());
                custFileMap.put(agencyAuthorFileGroup.getFileInfoType(), custFileItem);
            }
        }
        return custFileMap;
    }
    
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
                    custFileItemList.add(custFileItem);
                }else{
                    custFileItem=new CustFileItem();
                    custFileItem.setFileInfoType(agencyAuthorFileGroup.getFileInfoType());
                    custFileItem.setFileName(agencyAuthorFileGroup.getDescription());
                    custFileItem.setFileDescription(agencyAuthorFileGroup.getDescription());
                    custFileItemList.add(custFileItem);
                }
            }else{
                CustFileItem custFileItem=new CustFileItem();
                custFileItem.setFileInfoType(agencyAuthorFileGroup.getFileInfoType());
                custFileItem.setFileName(agencyAuthorFileGroup.getDescription());
                custFileItem.setFileDescription(agencyAuthorFileGroup.getDescription());
                custFileItemList.add(custFileItem);
            }
        }
        return custFileItemList;
    }
    
    public CustFileAduitTemp findCustFileAduitTempByType(Long anCustNo,String anType){
        Map<String, Object> anMap=new HashMap<String, Object>();
        anMap.put("aduitCustNo", anCustNo);
        anMap.put("workType", anType);
        anMap.put("auditStatus","1");
        return Collections3.getFirst(this.selectByProperty(anMap));
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
        BTAssert.notNull(fileAduitTemp, "未找到临时文件关联关系:"+fileAduitTemp.getId());
        fileAduitTemp.setWorkType("-"+fileAduitTemp.getWorkType());
        fileAduitTemp.setAduitCustNo(-fileAduitTemp.getAduitCustNo());
        Map<String, Object> anMap=new HashMap<String, Object>();
        anMap.put("id", fileAduitTemp.getId());
//        anMap.put("type", "-"+fileAduitTemp.getType());
        
        boolean bool=this.updateByExample(fileAduitTemp, anMap)>0;
        return bool;
    }
    
}
