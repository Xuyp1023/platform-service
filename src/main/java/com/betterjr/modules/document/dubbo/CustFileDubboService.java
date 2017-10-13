package com.betterjr.modules.document.dubbo;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.mq.core.RocketMQProducer;
import com.betterjr.common.mq.message.MQMessage;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.document.ICustFileService;
import com.betterjr.modules.document.data.AccountAduitData;
import com.betterjr.modules.document.data.FileStoreType;
import com.betterjr.modules.document.entity.CustFileAduit;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.document.entity.CustResolveFile;
import com.betterjr.modules.document.service.CustFileAuditService;
import com.betterjr.modules.document.service.CustFileInfoService;
import com.betterjr.modules.document.service.CustFileItemService;
import com.betterjr.modules.document.service.CustResolveFileService;
import com.betterjr.modules.document.utils.CustFileUtils;

@Service(interfaceClass = ICustFileService.class)
public class CustFileDubboService implements ICustFileService {

    @Autowired
    private CustFileAuditService custFileAuditService;
    @Autowired
    private CustFileInfoService custFileInfoService;
    @Autowired
    private CustFileItemService custFileItemService;

    @Autowired
    private CustResolveFileService resolveFileService;
    @Resource
    private RocketMQProducer betterProducer;

    @Override
    public String webUpdateCustFileAuditInfo(final Map<String, String[]> anParamMap, final Enumeration<String> anParamNames, final Long anCustNo) {

        custFileAuditService.updateCustFileAuditInfo(anParamMap, anParamNames, anCustNo);
        return AjaxObject.newOk("新增用户认证文件审核信息成功").toJson();
    }

    @Override
    public String webFindCustFileAuditInfo(final Long anCustNo) {

        final List<CustFileAduit> auditList = custFileAuditService.findCustFileAuditInfo(anCustNo);
        return AjaxObject.newOk("查询用户认证文件审核信息成功", auditList).toJson();
    }

    @Override
    public List<Long> findBatchNo(final Long anCustNo, final List<String> anFileBusinType) {

        return custFileAuditService.findBatchNo(anCustNo, anFileBusinType);
    }

    @Override
    public boolean updateAuditFileGroup(final AccountAduitData anAduitData) {

        return custFileAuditService.updateAuditFileGroup(anAduitData);
    }

    /***
     * 添加客户审核附件
     * 
     * @param anCustFileAduit
     * @return
     */
    @Override
    public boolean addCustFileAduit(final CustFileAduit anCustFileAduit) {
        return custFileAuditService.addCustFileAduit(anCustFileAduit);
    }

    @Override
    public String webFindDeficiencyFileInfoList(final Long anCustNo, final String anAgencyNos) {

        final String businFlag = "01";
        final Set<String> noticeMsg = custFileAuditService.findDeficiencyFileInfoList(anCustNo, anAgencyNos, businFlag);
        if (Collections3.isEmpty(noticeMsg)) {
            return AjaxObject.newOk("ok", noticeMsg).toJson();
        }
        else {
            return AjaxObject.newOk("fail", noticeMsg).toJson();
        }
    }

    @Override
    public List<CustFileItem> findUploadFileByAgency(final String anRequestNo, final String anBusinFlag, final String anAgecyNo) {

        return custFileInfoService.findUploadFileByAgency(anRequestNo, anBusinFlag, anAgecyNo);
    }

    @Override
    public List<CustFileItem> findUploadFiles(final String anRequestNo, final String anBusinFlag) {

        return custFileInfoService.findUploadFiles(anRequestNo, anBusinFlag);
    }

    @Override
    public List<CustFileItem> findUploadFileByCustNo(final Long custNo, final String anBusinFlag) {

        return custFileInfoService.findUploadFileByCustNo(custNo, anBusinFlag);
    }

    @Override
    public CustFileItem findOne(final Long id) {

        return custFileItemService.findOne(id);
    }

    @Override
    public boolean updateFileItems(final Long batchNo, final Long fileItemId) {

        return custFileItemService.updateFileItems(batchNo, fileItemId);
    }

    @Override
    public Long updateCustFileItemInfo(final String anFileList, final Long anBatchNo) {

        return custFileItemService.updateCustFileItemInfo(anFileList, anBatchNo);
    }

    @Override
    public CustFileItem findOneByBatchNo(final Long anBatchNo) {

        return custFileItemService.findOneByBatchNo(anBatchNo, "");
    }

    @Override
    public Map<String, CustFileItem> findItems(final Map<String, Long> anMap) {

        return custFileItemService.findItems(anMap);
    }

    @Override
    public List<CustFileItem> findCustFiles(final Long anBatchNo) {

        return custFileItemService.findCustFiles(anBatchNo);
    }

    @Override
    public List<CustFileItem> findCustFilesByBatch(final List<Long> anBatchNoList, final List<String> anbusinTypeList) {

        return custFileItemService.findCustFilesByBatch(anBatchNoList, anbusinTypeList);
    }

    @Override
    public List<CustFileItem> findCustFilesByBatch(final List<Long> anBatchNoList) {

        return custFileItemService.findCustFilesByBatch(anBatchNoList);
    }

    @Override
    public String webDeleteFileItem(final Long anId, final Long anBatchNo) {

        final boolean result = custFileItemService.deleteFileItem(anId, anBatchNo);
        if (result) {
            return AjaxObject.newOk("文件删除成功").toJson();
        }
        return AjaxObject.newOk("文件删除失败").toJson();
    }

    @Override
    public boolean deleteFileItem(final Long anId, final Long anBatchNo) {

        return custFileItemService.deleteFileItem(anId, anBatchNo);
    }

    @Override
    public CustFileItem saveAndUpdateFileItem(final String filePath, final Long fileLength, final String anWorkType, final String anFileName,
            final FileStoreType anStoreType, final boolean anWithBatchNo) {

        final CustFileItem fileItem = CustFileUtils.createDefFileItemForStore(filePath, fileLength, anWorkType, anFileName);
        fileItem.setStoreType(anStoreType.getValue());
        if (anWithBatchNo) {
            fileItem.setBatchNo(CustFileUtils.findBatchNo());
        }

        if (this.custFileItemService.saveAndUpdateFileItem(fileItem, UserUtils.getOperatorInfo())) {
            return fileItem;
        }
        else {
            return null;
        }
    }

    @Override
    public String webSaveAndUpdateFileItem(final String filePath, final Long fileLength, final String anWorkType, final String anFileName,
            final FileStoreType anStoreType) {

        final CustFileItem fileItem = CustFileUtils.createDefFileItemForStore(filePath, fileLength, anWorkType, anFileName);
        fileItem.setStoreType(anStoreType.getValue());
        final boolean result = this.custFileItemService.saveAndUpdateFileItem(fileItem, UserUtils.getOperatorInfo());
        if (result) {
            return AjaxObject.newOk("上传文件成功", fileItem).toJson();
        }
        return AjaxObject.newOk("上传文件失败").toJson();
    }

    @Override
    public Long updateAndDelCustFileItemInfo(final String anFileList, final Long anBatchNo) {
        return this.custFileItemService.updateAndDelCustFileItemInfo(anFileList, anBatchNo);
    }

    /***
     * 删除审核表中的附件关联
     * 
     * @param anId
     * @return
     */
    @Override
    public boolean delCustFileAduit(final Long anId) {
        return this.custFileAuditService.delCustFileAduit(anId);
    }

    /****
     * 审核通过查询的附件来源为审核正式表
     * 
     * @param anCustNo
     * @param anRelateCustNo
     * @return
     */
    @Override
    public List<CustFileItem> findCustFileAduit(final Long anCustNo, final Long anRelateCustNo) {
        return this.custFileAuditService.findCustFileAduit(anCustNo, anRelateCustNo);
    }

    @Override
    public List<CustFileItem> findFileListByIds(final String[] anIds) {
        return this.custFileItemService.findFileListByIds(anIds);
    }

    @Override
    public CustResolveFile webSaveAddResolveFile(final CustResolveFile anResolveFile) {

        // AjaxObject.newOk("文件解析日志插入成功", resolveFileService.saveAddResolveFile(anResolveFile)).toJson();
        return resolveFileService.saveAddResolveFile(anResolveFile);
    }

    @Override
    public String webfindResolveFile(final Long anResolveFileId) {

        return AjaxObject.newOk(resolveFileService.findOne(anResolveFileId)).toJson();
    }

    @Override
    public boolean sendResolveMessage(final CustResolveFile anResolveFile) {

        // 发消息
        final MQMessage anMessage = new MQMessage("FILE_RESOLVE_CUST_TOPIC");

        try {
            anMessage.setObject(anResolveFile);
            anMessage.addHead("id", anResolveFile.getId());
            anMessage.addHead("infoType", anResolveFile.getInfoType());
            betterProducer.sendMessage(anMessage);
            return true;
        }
        catch (final Exception e) {

            return false;
        }

    }

    @Override
    public void saveModifyResolveFile(final Map<String, Object> anResolveFileMap) {

        resolveFileService.saveUpdateOnlyStatus(anResolveFileMap);
    }

    @Override
    public void saveModifyResolveFile(final CustResolveFile anResolveFile) {

        BTAssert.notNull(anResolveFile, "修改记录失败，数据为空");
        BTAssert.notNull(anResolveFile.getId(), "修改记录失败，数据为空");
        resolveFileService.updateByPrimaryKeySelective(anResolveFile);

    }

    @Override
    public CustFileItem findOneAndButchId(final Long anId) {
        final CustFileItem fileItem = custFileItemService.findOne(anId);
        if (fileItem.getBatchNo() == null || fileItem.getBatchNo() < 0) {
            final Long butchId = custFileItemService.updateCustFileItemInfo(anId + "", 0l);
            fileItem.setBatchNo(butchId);
        }
        return fileItem;
    }

    @Override
    public void savePlatformAduitFile(final Long anCustNo, final Long anBatchNo) {

        custFileAuditService.savePlatformAduitFile(anCustNo, anBatchNo);
    }

}
