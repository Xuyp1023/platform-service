package com.betterjr.modules.document.dubbo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.modules.document.ICustFileAduitTempService;
import com.betterjr.modules.document.entity.AgencyAuthorFileGroup;
import com.betterjr.modules.document.entity.CustFileAduitTemp;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.document.service.CustFileAduitTempService;

@Service
public class CustFileAduitTempDubboService implements ICustFileAduitTempService {

    @Autowired
    public CustFileAduitTempService custFileAduitTempService;
    
    @Override
    public List<CustFileItem> findCustAduitTemp(Long anCustNo,Long anSelectCustNo, List<AgencyAuthorFileGroup> anAgencyAuthorFileGroupList) {
        
        return custFileAduitTempService.findCustFileAduitTempByCustNoAndType(anCustNo,anSelectCustNo, anAgencyAuthorFileGroupList);
    }
    
    /***
     * 保存临时文件
     * @param anCustFileAduitTemp
     */
    public boolean addCustFileAduitTemp(CustFileAduitTemp anCustFileAduitTemp){
        return custFileAduitTempService.addCustFileAduitTemp(anCustFileAduitTemp);
    }
    
    /***
     * 检查文件是否都已上传
     * @param anCustNo 关联上传文件的客户号
     * @param anAgencyAuthorFileGroupList 文件类型列表
     * @return
     */
    public boolean checkCustFileAduitTempExist(Long anCustNo,Long anSelectCustNo,List<AgencyAuthorFileGroup> anAgencyAuthorFileGroupList){
        return custFileAduitTempService.checkCustFileAduitTempExist(anCustNo,anSelectCustNo,anAgencyAuthorFileGroupList);
    }
    
    /***
     * 删除临时审核附件关系表数据
     * @param anId 附件 id
     * @return
     */
    public boolean saveDeleteFileAduitTemp(Long anId){
        return custFileAduitTempService.saveDeleteFileAduitTemp(anId);
    }
    
    /***
     * 添加客户文件关系
     * @param anRelationCustNo 关联的客户号
     * @param fileIds 上传的文件列表(以,分隔)
     */
    public void saveCustFileAduitTemp(Long anCustNo,Long anRelateCustNo,String anFileIds,String anCustType){
        custFileAduitTempService.saveCustFileAduitTemp(anCustNo,anRelateCustNo,anFileIds,anCustType);
    }
    

    /***
     * 查询关系审核附件
     * @param anCustNo
     * @return
     */
    public List<CustFileItem> findRelateAduitTempFile(Long anCustNo){
        return custFileAduitTempService.findRelateAduitTempFile(anCustNo);
    }
    
    /***
     * 保存附件关系
     * @param passFiles 审核通过的文件列表
     * @param failFiles 审核不通过的文件列表
     * @param anBusinStatus 关系状态
     */
    public void saveAcceptFileTemp(String anPassFiles,String anFailFiles){
        custFileAduitTempService.saveAcceptFileTemp(anPassFiles, anFailFiles);
    }
    public void saveAduitFile(Long anCustNo,Long anRelateCustNo){
        custFileAduitTempService.saveAduitFile(anCustNo, anRelateCustNo);
    }

}
