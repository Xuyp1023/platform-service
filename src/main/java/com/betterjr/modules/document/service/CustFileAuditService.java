package com.betterjr.modules.document.service;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.QueryTermBuilder;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.utils.reflection.ReflectionUtils;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.document.IAgencyAuthFileGroupService;
import com.betterjr.modules.document.dao.CustFileAduitMapper;
import com.betterjr.modules.document.data.AccountAduitData;
import com.betterjr.modules.document.data.AuthDocumentStatus;
import com.betterjr.modules.document.entity.AgencyAuthorFileGroup;
import com.betterjr.modules.document.entity.AuthorFileGroup;
import com.betterjr.modules.document.entity.CustFileAduit;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.document.utils.CustFileUtils;
import com.betterjr.modules.rule.RuleCheckResult;
import com.betterjr.modules.rule.service.RuleServiceAspect;

@Service
public class CustFileAuditService extends BaseService<CustFileAduitMapper, CustFileAduit> {

    private static final Logger logger = LoggerFactory.getLogger(CustFileAuditService.class);
    @Autowired
    private CustFileItemService custFileItemService;

    @Reference(interfaceClass = IAgencyAuthFileGroupService.class)
    private IAgencyAuthFileGroupService agencyAuthFileGroupService;
    @Autowired
    private AuthorFileGroupService authorFileGroupService;

    /**
     * 更新用户认证文件信息，包括增删改（由上传的文件数量决定）
     * 
     * @param request
     */
    public void updateCustFileAuditInfo(final Map<String, String[]> anParamMap, final Enumeration<String> anParamNames, final Long anCustNo) {
        logger.info("Begin to update customer file audit information.");
        logger.debug("Get param map:" + anParamMap);
        final Map<String, String> numMap = getTypeNumber(anParamMap, anParamNames);
        // Long custNo = Collections3.getFirst(UserUtils.findCustNoList());
        // get current upload file audit information
        final List<CustFileAduit> fileAuditInfos = getUploadFileAuditInfo(anParamMap, numMap, anCustNo);
        final List<CustFileAduit> dbAuditInfos = this.selectByProperty("custNo", anCustNo);
        final Map<String, CustFileAduit> dbAuditMap = ReflectionUtils.listConvertToMapKeyObj(dbAuditInfos, "workType");
        final List<Long> delItemIds = new ArrayList<Long>();
        for (final CustFileAduit fileAudit : fileAuditInfos) {
            final String type = fileAudit.getWorkType();
            final CustFileAduit dbAuditInfo = dbAuditMap.get(type);
            RuleServiceAspect.clearMarket();
            if (null == dbAuditInfo) {
                if (fileAudit.getFileCount() > 0) {
                    fileAudit.setId(CustFileUtils.findBatchNo());
                    logger.debug("insert new audit information:" + fileAudit.toString());
                    this.insert(fileAudit);
                }
            }
            else {
                if (!dbAuditInfo.getAuditStatus().equals("0")) {
                    logger.error("Customer:" + anCustNo + " type:" + type + " can't modify for status.");
                    continue;
                }
                fileAudit.setId(dbAuditInfo.getId());
                if (fileAudit.getFileCount() == 0) {
                    logger.debug("delete audit information:" + fileAudit.toString());
                    this.deleteByPrimaryKey(fileAudit.getId());
                }
                else {
                    logger.debug("update audit information:" + fileAudit.toString());
                    this.updateByPrimaryKey(fileAudit);
                }
            }
            final RuleCheckResult result = RuleServiceAspect.getCheckResult();
            if (!result.isOk()) {
                logger.error(result.toString());
                throw new BytterTradeException(40001, "保存用户认证信息失败，请检查。");
            }

            final Long batchNo = fileAudit.getId();
            // 当前类型的文件未入库，不需要更新相关文件处理
            if (null == batchNo) {
                logger.debug("Type " + type + " don't have any file item to update.");
                continue;
            }
            final List<CustFileItem> dbFileItems = custFileItemService.findCustFiles(batchNo);

            final String[] currentIds = fileAudit.getFileIds().split(",");
            final List<Long> addItemIds = getAddFileItemIds(dbFileItems, currentIds);
            getDelFileItemIds(delItemIds, dbFileItems, currentIds);

            logger.debug("add item:" + addItemIds.size() + " " + addItemIds);
            for (final Long id : addItemIds) {
                final CustFileItem item = custFileItemService.selectByPrimaryKey(id);
                item.setBatchNo(batchNo);
                custFileItemService.updateByPrimaryKeySelective(item);
            }
        }
        logger.debug("delete item:" + delItemIds.size() + " " + delItemIds);
        for (final Long id : delItemIds) {
            final CustFileItem item = custFileItemService.selectByPrimaryKey(id);
            item.setBatchNo((long) 0);
            custFileItemService.updateByPrimaryKeySelective(item);
        }
    }

    /**
     * 获取当前上传的用户认证文件信息
     * 
     * @param paramMap
     * @param numMap
     * @param custNo
     * @return
     */
    private List<CustFileAduit> getUploadFileAuditInfo(final Map<String, String[]> paramMap, final Map<String, String> numMap, final Long custNo) {
        logger.info("Begin to get upload file audit information to customer:" + custNo);
        final List<CustFileAduit> fileAuditInfos = new ArrayList<CustFileAduit>();
        final List<AuthorFileGroup> fileGroups = authorFileGroupService.findCustFileGroupList();
        for (final AuthorFileGroup fileGroup : fileGroups) {
            final String fileInfoType = fileGroup.getFileInfoType();
            final String number = numMap.get(fileInfoType);
            if (BetterStringUtils.isBlank(number)) {
                logger.error("请求参数中无类型为" + fileInfoType + "的数据");
                continue;
                // throw new BytterTradeException(40001, "认证资料不全，请检查。");
            }
            final String status = paramMap.get("param[" + number + "][status]")[0];
            final String itemIds = paramMap.get("param[" + number + "][id]")[0];
            logger.debug("type:" + fileInfoType + " id:" + itemIds);
            final String[] itArray = itemIds.split(",");
            int count = itArray.length;
            if (BetterStringUtils.isBlank(itemIds)) {
                logger.debug("Customer:" + custNo + " file type:" + fileInfoType + " don't have audit file upload.");
                count = 0;
            }
            final CustFileAduit audit = new CustFileAduit();
            CustFileAduit.init(audit, fileInfoType, custNo);
            audit.setAuditStatus(status);
            audit.setFileCount(count);
            audit.setFileIds(itemIds);
            fileAuditInfos.add(audit);
        }
        return fileAuditInfos;
    }

    /**
     * 查询用户认证文件信息
     * 
     * @return
     */
    public List<CustFileAduit> findCustFileAuditInfo(final Long anCustNo) {
        logger.info("Begin to find customer file audit information.");
        final List<CustFileAduit> auditInfos = this.selectByProperty("custNo", anCustNo);

        for (final CustFileAduit auditInfo : auditInfos) {
            final Long batchNo = auditInfo.getId();
            final List<CustFileItem> itemList = custFileItemService.findCustFiles(batchNo);
            final StringBuilder fileInfos = new StringBuilder();
            for (final CustFileItem item : itemList) {
                if (fileInfos.length() > 0) {
                    fileInfos.append(",");
                }
                fileInfos.append(item.getId()).append(":").append(item.getFileName());
            }
            final String[] fileArray = fileInfos.toString().split(",");
            auditInfo.setFileList(fileArray);
        }

        return auditInfos;
    }

    /**
     * 获取需要删除（与用户认证信息去关联）的附件ID列表
     * 
     * @param delItemIds
     * @param dbFileItems
     * @param currentIds
     */
    private void getDelFileItemIds(final List<Long> delItemIds, final List<CustFileItem> dbFileItems, final String[] currentIds) {
        for (final CustFileItem item : dbFileItems) {
            boolean isExist = false;
            for (final String currentID : currentIds) {
                if (BetterStringUtils.isBlank(currentID)) {
                    continue;
                }
                if (currentID.equals(item.getId().toString())) {
                    isExist = true;
                    break;
                }
            }
            if (!(isExist || delItemIds.contains(item.getId()))) {
                delItemIds.add(item.getId());
            }
        }
    }

    /**
     * 获取需要关联用户认证信息的附件ID列表
     * 
     * @param dbFileItems
     * @param currentIds
     * @return
     */
    private List<Long> getAddFileItemIds(final List<CustFileItem> dbFileItems, final String[] currentIds) {
        final List<Long> addItemIds = new ArrayList<Long>();
        for (final String currentID : currentIds) {
            if (BetterStringUtils.isBlank(currentID)) {
                continue;
            }
            boolean isExist = false;
            for (final CustFileItem item : dbFileItems) {
                if (currentID.equals(item.getId().toString())) {
                    isExist = true;
                    break;
                }
            }
            if (!isExist) {
                addItemIds.add(Long.valueOf(currentID));
            }
        }
        return addItemIds;
    }

    /**
     * 获取传入参数中各个文件业务类型的序号Map
     * 
     * @param paramMap
     * @param paramNames
     * @return Map<workType, number>
     */
    private Map<String, String> getTypeNumber(final Map<String, String[]> paramMap, final Enumeration<String> paramNames) {
        final Map<String, String> numMap = new HashMap<String, String>();
        while (paramNames.hasMoreElements()) {
            final String paramName = paramNames.nextElement();
            if (paramName.contains("type")) {
                final String key = paramMap.get(paramName)[0];
                final int beginPos = paramName.indexOf("[") + 1;
                final int endPos = paramName.indexOf("]");
                final String value = paramName.substring(beginPos, endPos);
                numMap.put(key, value);
            }
        }
        return numMap;
    }

    /**
     * 根据客户号和文件业务类型查找文件的批次号
     * 
     * @param anCustNo
     *            客户号
     * @param anFileBusinType
     *            文件业务类型
     * @return
     */
    public List<Long> findBatchNo(final Long anCustNo, final List<String> anFileBusinType) {
        final List<Long> batchNoList = new ArrayList<Long>();
        for (final CustFileAduit fileAduit : findCustFileInfo(anCustNo, anFileBusinType)) {
            batchNoList.add(fileAduit.getId());
        }

        return batchNoList;
    }

    private List<CustFileAduit> findCustFileInfo(final Long anCustNo, final List<String> anFileBusinType) {
        final Map<String, Object> map = new HashMap();
        map.put("custNo", anCustNo);
        map.put("workType", anFileBusinType);

        return this.selectByProperty(map);
    }

    /**
     * 更新文件审批情况信息
     * 
     * @param anAduitData
     *            审批方数据
     * @return
     */
    public boolean updateAuditFileGroup(final AccountAduitData anAduitData) {
        final AuthDocumentStatus authoStatus = AuthDocumentStatus.checking(anAduitData.getAuditStatus());
        final Map map = new HashMap();
        map.put("custNo", anAduitData.getCustNo());
        if (authoStatus != AuthDocumentStatus.AUTHED) {
            final List<String> tmpFileTypes = BetterStringUtils.splitTrim(anAduitData.getAttachFalseList());
            map.put("workType", tmpFileTypes);
        }
        final List<CustFileAduit> tmpList = this.selectByProperty(map);
        if (Collections3.isEmpty(tmpList)) {
            return false;
        }
        for (final CustFileAduit fileAduit : tmpList) {
            fileAduit.setAuditStatus(authoStatus.getValue());
            fileAduit.setAuthorTime(anAduitData.getAduitDate());
            this.updateByPrimaryKey(fileAduit);
        }

        return true;
    }

    /**
     * 查找客户认证材料缺少的文件业务类型
     * 
     * @param anCustNo
     *            客户编号
     * @param anAgencyNo
     *            合作伙伴代码
     * @param anBusinFlag
     *            业务类型
     * @return
     */
    public Set<String> findDeficiencyFileInfoList(final Long anCustNo, final String anAgencyNos, final String anBusinFlag) {
        final Set<String> noticeMsg = new HashSet<String>();
        if (BetterStringUtils.isNotBlank(anAgencyNos)) {
            final List<AgencyAuthorFileGroup> agencyFileGroupList = this.agencyAuthFileGroupService.findAuthorFileGroup(anAgencyNos.split(","),
                    anBusinFlag);
            final Map<String, CustFileAduit> custAduitFileMap = ReflectionUtils.listConvertToMap(findCustFileInfo(anCustNo, null), "workType");
            final Map<String, AuthorFileGroup> allAuthorFile = authorFileGroupService.findAllFileGroup();
            AuthorFileGroup tmpFileGroup;
            for (final AgencyAuthorFileGroup authorFileGroup : agencyFileGroupList) {
                if (custAduitFileMap.containsKey(authorFileGroup.getFileInfoType()) == false) {
                    tmpFileGroup = allAuthorFile.get(authorFileGroup.getFileInfoType());
                    if (tmpFileGroup != null) {
                        noticeMsg.add(tmpFileGroup.getDeficiencyInfo());
                    }
                }
            }
        }

        return noticeMsg;
    }

    /***
     * 添加附件
     * 
     * @param anCustFileAduit
     * @return
     */
    public boolean addCustFileAduit(final CustFileAduit anCustFileAduit) {
        return this.insert(anCustFileAduit) > 0;
    }

    /***
     * 删除附件关联审核表
     * 
     * @param anId
     * @return
     */
    public boolean delCustFileAduit(final Long anId) {
        final CustFileItem custFileItem = custFileItemService.findOne(anId);
        if (custFileItem != null) {
            return this.deleteByPrimaryKey(custFileItem.getBatchNo()) > 0;
        }
        else {
            return false;
        }
    }

    /****
     * 审核通过查询的附件来源为审核正式表
     * 
     * @param anCustNo
     * @param anRelateCustNo
     * @return
     */
    public List<CustFileItem> findCustFileAduit(final Long anCustNo, final Long anRelateCustNo) {
        final List<CustFileItem> custFileItemList = new ArrayList<CustFileItem>();
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("aduitCustNo", anRelateCustNo);
        anMap.put("auditStatus", "1");
        for (final CustFileAduit custFileAduit : this.selectByProperty(anMap)) {
            final CustFileItem custFileItem = custFileItemService.findOneByBatchNo(custFileAduit.getId(), custFileAduit.getWorkType());
            if (custFileItem != null) {
                custFileItem.setBusinStatus("1");
                custFileItem.setFileDescription(agencyAuthFileGroupService.findAuthFileGroup(custFileItem.getFileInfoType()).getDescription());
                custFileItemList.add(custFileItem);
            }
        }
        return custFileItemList;
    }

    /**
     * 保存平台审批的资料的附件信息
     * 
     * @param anCustNo
     *            客户编号
     * @param anBatchNo
     *            文件的批次号
     */
    public void savePlatformAduitFile(final Long anCustNo, final Long anBatchNo) {

        final List<CustFileItem> fileItemList = this.custFileItemService.findCustFiles(anBatchNo);
        final Map<String, AtomicInteger> tmpMap = new HashMap();
        final CustFileItem workItem = Collections3.getFirst(fileItemList);
        if (workItem == null) {
            logger.warn("savePlatformAdduitFile anCustNo = " + anCustNo + ",  anBatchNo = " + anBatchNo + ", not find FileItemInfo");
            return;
        }

        AtomicInteger workCount;
        for (final CustFileItem fileItem : fileItemList) {
            workCount = tmpMap.get(fileItem.getFileInfoType());
            if (workCount == null) {
                tmpMap.put(fileItem.getFileInfoType(), new AtomicInteger(1));
            }
            else {
                workCount.incrementAndGet();
            }
        }

        CustFileAduit tmpFileAduitInfo;
        for (final Map.Entry<String, AtomicInteger> tmpEnt : tmpMap.entrySet()) {
            destroyOldFileAduit(anCustNo, tmpEnt.getKey());
            tmpFileAduitInfo = new CustFileAduit(anCustNo, anBatchNo, tmpEnt.getValue().intValue(), tmpEnt.getKey());
            tmpFileAduitInfo.setOperNo(workItem.getRegOperName());
            final CustInfo custInfo = UserUtils.getDefCustInfo();
            if (custInfo != null) {
                tmpFileAduitInfo.setAduitCustNo(custInfo.getCustNo());
            }
            else {
                tmpFileAduitInfo.setAduitCustNo(0L);
            }
            tmpFileAduitInfo.setRegDate(workItem.getRegDate());
            tmpFileAduitInfo.setRegTime(workItem.getRegTime());
            this.insert(tmpFileAduitInfo);
        }
    }

    /**
     * 作废掉就的审批信息
     * 
     * @param anCustNo
     * @param anFileType
     */
    private void destroyOldFileAduit(final Long anCustNo, final String anFileType) {
        final Map<String, Object> termMap = QueryTermBuilder.newInstance().put("custNo", anCustNo).put("workType", anFileType).build();
        for (CustFileAduit fileAduit : this.selectByProperty(termMap)) {
            fileAduit = CustFileAduit.destroyFileAuditInfo(fileAduit);
            this.updateByPrimaryKey(fileAduit);
        }
    }

    public static void main(final String[] args) {
        final String tmpStr = "1, 123, 1231 , 231313";
        for (final String tt : BetterStringUtils.splitTrim(tmpStr)) {
            System.out.println(tt + ", " + tt.length());
        }
    }
}
