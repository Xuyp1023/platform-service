package com.betterjr.modules.document.dubbo;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.config.ParamNames;
import com.betterjr.common.data.KeyAndValueObject;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.document.ICustFileService;
import com.betterjr.modules.document.data.AccountAduitData;
import com.betterjr.modules.document.data.FileStoreType;
import com.betterjr.modules.document.entity.CustFileAduit;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.document.service.CustFileAuditService;
import com.betterjr.modules.document.service.CustFileInfoService;
import com.betterjr.modules.document.service.CustFileItemService;
import com.betterjr.modules.document.utils.CustFileUtils;
import com.betterjr.modules.sys.service.SysConfigService;

@Service(interfaceClass=ICustFileService.class)
public class CustFileDubboService implements ICustFileService{
    
    @Autowired
    private CustFileAuditService custFileAuditService;
    @Autowired
    private CustFileInfoService custFileInfoService;
    @Autowired
    private CustFileItemService custFileItemService;

    @Override
    public String webUpdateCustFileAuditInfo(Map<String, String[]> anParamMap, Enumeration<String> anParamNames, Long anCustNo) {

        custFileAuditService.updateCustFileAuditInfo(anParamMap, anParamNames, anCustNo);
        return AjaxObject.newOk("新增用户认证文件审核信息成功").toJson();
    }

    @Override
    public String webFindCustFileAuditInfo(Long anCustNo) {

        List<CustFileAduit> auditList = custFileAuditService.findCustFileAuditInfo(anCustNo);
        return AjaxObject.newOk("查询用户认证文件审核信息成功", auditList).toJson();
    }

    @Override
    public List<Long> findBatchNo(Long anCustNo, List<String> anFileBusinType) {

        return custFileAuditService.findBatchNo(anCustNo, anFileBusinType);
    }

    @Override
    public boolean updateAuditFileGroup(AccountAduitData anAduitData) {

        return custFileAuditService.updateAuditFileGroup(anAduitData);
    }

    @Override
    public String webFindDeficiencyFileInfoList(Long anCustNo, String anAgencyNos) {

        String businFlag = "01";
        Set<String> noticeMsg = custFileAuditService.findDeficiencyFileInfoList(anCustNo, anAgencyNos, businFlag);
        if (Collections3.isEmpty(noticeMsg)) {
            return AjaxObject.newOk("ok", noticeMsg).toJson();
        }
        else {
            return AjaxObject.newOk("fail", noticeMsg).toJson();
        }
    }

    @Override
    public List<CustFileItem> findUploadFileByAgency(String anRequestNo, String anBusinFlag, String anAgecyNo) {

        return custFileInfoService.findUploadFileByAgency(anRequestNo, anBusinFlag, anAgecyNo);
    }

    @Override
    public List<CustFileItem> findUploadFiles(String anRequestNo, String anBusinFlag) {

        return custFileInfoService.findUploadFiles(anRequestNo, anBusinFlag);
    }

    @Override
    public List<CustFileItem> findUploadFileByCustNo(Long custNo, String anBusinFlag) {

        return custFileInfoService.findUploadFileByCustNo(custNo, anBusinFlag);
    }

    @Override
    public CustFileItem findOne(Long id) {

        return custFileItemService.findOne(id);
    }

    @Override
    public boolean updateFileItems(Long batchNo, Long fileItemId) {

        return custFileItemService.updateFileItems(batchNo, fileItemId);
    }

    @Override
    public Long updateCustFileItemInfo(String anFileList, Long anBatchNo) {

        return custFileItemService.updateCustFileItemInfo(anFileList, anBatchNo);
    }

    @Override
    public CustFileItem findOneByBatchNo(Long anBatchNo) {

        return custFileItemService.findOneByBatchNo(anBatchNo);
    }

    @Override
    public Map<String, CustFileItem> findItems(Map<String, Long> anMap) {

        return custFileItemService.findItems(anMap);
    }

    @Override
    public List<CustFileItem> findCustFiles(Long anBatchNo) {

        return custFileItemService.findCustFiles(anBatchNo);
    }

    @Override
    public List<CustFileItem> findCustFilesByBatch(List<Long> anBatchNoList, List<String> anbusinTypeList) {

        return custFileItemService.findCustFilesByBatch(anBatchNoList,anbusinTypeList);
    }

    @Override
    public List<CustFileItem> findCustFilesByBatch(List<Long> anBatchNoList) {

        return custFileItemService.findCustFilesByBatch(anBatchNoList);
    }

    @Override
    public String webDeleteFileItem(Long anId, Long anBatchNo) {

        boolean result= custFileItemService.deleteFileItem(anId, anBatchNo);
        if(result){
            return AjaxObject.newOk("文件删除成功").toJson();
        }
        return AjaxObject.newOk("文件删除失败").toJson();
    }
    
    @Override
    public boolean deleteFileItem(Long anId, Long anBatchNo) {

        return custFileItemService.deleteFileItem(anId, anBatchNo);
    }

    @Override
    public CustFileItem saveAndUpdateFileItem(String filePath,Long fileLength, String anWorkType, String anFileName, FileStoreType anStoreType, boolean anWithBatchNo) {

        CustFileItem fileItem = CustFileUtils.createDefFileItemForStore(filePath,fileLength, anWorkType, anFileName);
        fileItem.setStoreType(anStoreType.getValue());
        if (anWithBatchNo){
            fileItem.setBatchNo( CustFileUtils.findBatchNo() );
        }
        
        if (this.custFileItemService.saveAndUpdateFileItem(fileItem, UserUtils.getOperatorInfo())){
           return fileItem; 
        }
        else{
            return null;
        }
    }
    
    @Override
    public String webSaveAndUpdateFileItem(String filePath,Long fileLength, String anWorkType, String anFileName, FileStoreType anStoreType) {

        CustFileItem fileItem = CustFileUtils.createDefFileItemForStore(filePath,fileLength, anWorkType, anFileName);
        fileItem.setStoreType(anStoreType.getValue());
        boolean result=this.custFileItemService.saveAndUpdateFileItem(fileItem, UserUtils.getOperatorInfo());
        if(result){
            return AjaxObject.newOk("上传文件成功", fileItem).toJson();
        }
        return AjaxObject.newOk("上传文件失败").toJson();
    }

    @Override
    public Long updateAndDelCustFileItemInfo(String anFileList, Long anBatchNo) {
        return this.custFileItemService.updateAndDelCustFileItemInfo(anFileList, anBatchNo);
    }
 
}
