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
        // TODO Auto-generated method stub
        custFileAuditService.updateCustFileAuditInfo(anParamMap, anParamNames, anCustNo);
        return AjaxObject.newOk("新增用户认证文件审核信息成功").toJson();
    }

    @Override
    public String webFindCustFileAuditInfo(Long anCustNo) {
        // TODO Auto-generated method stub
        List<CustFileAduit> auditList = custFileAuditService.findCustFileAuditInfo(anCustNo);
        return AjaxObject.newOk("查询用户认证文件审核信息成功", auditList).toJson();
    }

    @Override
    public List<Long> findBatchNo(Long anCustNo, List<String> anFileBusinType) {
        // TODO Auto-generated method stub
        return custFileAuditService.findBatchNo(anCustNo, anFileBusinType);
    }

    @Override
    public boolean updateAuditFileGroup(AccountAduitData anAduitData) {
        // TODO Auto-generated method stub
        return custFileAuditService.updateAuditFileGroup(anAduitData);
    }

    @Override
    public String webFindDeficiencyFileInfoList(Long anCustNo, String anAgencyNos) {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        return custFileInfoService.findUploadFileByAgency(anRequestNo, anBusinFlag, anAgecyNo);
    }

    @Override
    public List<CustFileItem> findUploadFiles(String anRequestNo, String anBusinFlag) {
        // TODO Auto-generated method stub
        return custFileInfoService.findUploadFiles(anRequestNo, anBusinFlag);
    }

    @Override
    public List<CustFileItem> findUploadFileByCustNo(Long custNo, String anBusinFlag) {
        // TODO Auto-generated method stub
        return custFileInfoService.findUploadFileByCustNo(custNo, anBusinFlag);
    }

    @Override
    public CustFileItem findOne(Long id) {
        // TODO Auto-generated method stub
        return custFileItemService.findOne(id);
    }

    @Override
    public boolean updateFileItems(Long batchNo, Long fileItemId) {
        // TODO Auto-generated method stub
        return custFileItemService.updateFileItems(batchNo, fileItemId);
    }

    @Override
    public Long updateCustFileItemInfo(String anFileList, Long anBatchNo) {
        // TODO Auto-generated method stub
        return custFileItemService.updateCustFileItemInfo(anFileList, anBatchNo);
    }

    @Override
    public CustFileItem findOneByBatchNo(Long anBatchNo) {
        // TODO Auto-generated method stub
        return custFileItemService.findOneByBatchNo(anBatchNo);
    }

    @Override
    public Map<String, CustFileItem> findItems(Map<String, Long> anMap) {
        // TODO Auto-generated method stub
        return custFileItemService.findItems(anMap);
    }

    @Override
    public List<CustFileItem> findCustFiles(Long anBatchNo) {
        // TODO Auto-generated method stub
        return custFileItemService.findCustFiles(anBatchNo);
    }

    @Override
    public List<CustFileItem> findCustFilesByBatch(List<Long> anBatchNoList, List<String> anbusinTypeList) {
        // TODO Auto-generated method stub
        return custFileItemService.findCustFilesByBatch(anBatchNoList,anbusinTypeList);
    }

    @Override
    public List<CustFileItem> findCustFilesByBatch(List<Long> anBatchNoList) {
        // TODO Auto-generated method stub
        return custFileItemService.findCustFilesByBatch(anBatchNoList);
    }

    @Override
    public String webDeleteFileItem(Long anId, Long anBatchNo) {
        // TODO Auto-generated method stub
        boolean result= custFileItemService.deleteFileItem(anId, anBatchNo);
        if(result){
            return AjaxObject.newOk("文件删除成功").toJson();
        }
        return AjaxObject.newOk("文件删除失败").toJson();
    }
    
    @Override
    public boolean deleteFileItem(Long anId, Long anBatchNo) {
        // TODO Auto-generated method stub
        return custFileItemService.deleteFileItem(anId, anBatchNo);
    }

    @Override
    public String webSaveAndUpdateFileItem(String filePath,Long fileLength, String anWorkType, String anFileName) {
        // TODO Auto-generated method stub
        CustFileItem fileItem = CustFileUtils.createDefFileItemForStore(filePath,fileLength, anWorkType, anFileName);
        boolean result=this.custFileItemService.saveAndUpdateFileItem(fileItem, UserUtils.getOperatorInfo());
        if(result){
            return AjaxObject.newOk("上传文件成功", fileItem).toJson();
        }
        return AjaxObject.newOk("上传文件失败").toJson();
    }

    @Override
    public String findFileBasePath() {
        // TODO Auto-generated method stub
        return SysConfigService.getString(ParamNames.OPENACCO_FILE_DOWNLOAD_PATH);
    }
    
    @Override
    public Long updateAndDelCustFileItemInfo(String anFileList, Long anBatchNo) {
        return this.custFileItemService.updateAndDelCustFileItemInfo(anFileList, anBatchNo);
    }

}
