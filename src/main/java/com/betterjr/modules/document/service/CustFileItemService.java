package com.betterjr.modules.document.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.document.dao.CustFileItemMapper;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.document.utils.CustFileUtils;

@Service
public class CustFileItemService extends BaseService<CustFileItemMapper, CustFileItem> {

    private static final Pattern COMMA_PATTERN = Pattern.compile(",");

    private static String[] queryConds = new String[] { "bizLicenseFile", "orgCodeFile", "taxRegistFile", "representIdFile", "bankAcctAckFile",
    "brokerIdFile" };

    /**
     * 根据文件ID号，查询单个文件信息
     *
     * @param id
     * @return
     */
    public CustFileItem findOne(final Long id) {
        return this.selectByPrimaryKey(id);
    }

    /**
     * 根据文件编号，更新文件批次号
     * @param batchNo 文件批次号
     * @param fileItemId 文件编号
     * @return
     */
    public boolean updateFileItems(final Long batchNo, final Long fileItemId) {
        final CustFileItem fileItem = this.selectByPrimaryKey(fileItemId);
        fileItem.setBatchNo(batchNo);

        return this.updateByPrimaryKeySelective(fileItem) == 1;
    }

    /**
     * 根据文件编号,更新文件批次号,如果文件已经被其他批次号使用,copy一份
     * @param anBatchNo
     * @param anFileItemId
     * @return
     */
    private boolean updateAndDuplicateConflictFileItems(final Long anBatchNo, final Long anFileItemId, final CustOperatorInfo anOperator) {
        final CustFileItem fileItem = this.selectByPrimaryKey(anFileItemId);

        if (fileItem != null) {
            if (fileItem.getBatchNo().equals(0L) == true) {
                fileItem.setBatchNo(anBatchNo);
                return this.updateByPrimaryKeySelective(fileItem) == 1;
            } else if (fileItem.getBatchNo().equals(anBatchNo) == false) {
                final CustFileItem tempFileItem = new CustFileItem();
                tempFileItem.initDuplicateConflictValue(fileItem);
                tempFileItem.setBatchNo(anBatchNo);
                return this.saveAndUpdateFileItem(tempFileItem, anOperator);
            }
        }

        return false;
    }

    /**
     * 更新文件列表的批次号,如果批次号不存在,则创建批次号,如果文件已经被其它批次号使用,则将文件复制一份,与当前批次号绑定,不影响以前的绑定关系
     * @param anFileList
     * @param anBatchNo
     * @return
     */
    public Long updateAndDuplicateConflictFileItemInfo(final String anFileList, Long anBatchNo, final CustOperatorInfo anOperator) {
        if (BetterStringUtils.isBlank(anFileList)) {

            return anBatchNo;
        }

        if (anBatchNo == null) {
            anBatchNo = CustFileUtils.findBatchNo();
        }

        final List<Long> fileItems = COMMA_PATTERN.splitAsStream(anFileList).map(Long::valueOf).collect(Collectors.toList());

        return updateAndDuplicateConflictFileItemInfo(fileItems, anBatchNo, anOperator);
    }

    public Long updateAndDuplicateConflictFileItemInfo(final List<Long> fileItems, Long anBatchNo, final CustOperatorInfo anOperator) {
        if (Collections3.isEmpty(fileItems) == true) {
            return anBatchNo;
        }

        if (anBatchNo == null) {
            anBatchNo = CustFileUtils.findBatchNo();
        }

        for (final Long fileItem : fileItems) {
            updateAndDuplicateConflictFileItems(anBatchNo, fileItem, anOperator);
        }

        return anBatchNo;
    }

    /**
     * 更新文件列表的批次号，如果批次号不存在，则创建批次号
     * @param anFileList 以逗号分隔的文件编号
     * @param anBatchNo 文件批次号
     * @return 返回文件批次号
     */
    public Long updateCustFileItemInfo(final String anFileList, Long anBatchNo) {
        if (BetterStringUtils.isBlank(anFileList)) {

            return anBatchNo;
        }

        if (anBatchNo == null) {
            anBatchNo = CustFileUtils.findBatchNo();
        }

        logger.info("fileList:" + anFileList);
        final String[] fileItems = BetterStringUtils.split(anFileList, ",");
        for (final String item : fileItems) {
            if (BetterStringUtils.isNotBlank(item)) {
                final Long fileId = Long.valueOf(item.trim());
                updateFileItems(anBatchNo, fileId);
            }
        }

        return anBatchNo;
    }
    
    /**
     * 更新文件列表的批次号，如果批次号不存在，则创建批次号
     * @param anFileList 以逗号分隔的文件编号
     * @param anBatchNo 文件批次号
     * @return 返回文件批次号
     */
    public Long updateDuplicateCustFileItemInfo(final Long anFileItemId, final CustOperatorInfo anOperator) {
        BTAssert.notNull(anFileItemId, "文件编号不允许为空！");
        
        final CustFileItem fileItem = this.selectByPrimaryKey(anFileItemId);
        
        Long anBatchNo = CustFileUtils.findBatchNo();
        
        if (fileItem != null) {
            if (fileItem.getBatchNo().equals(0L) == true) {
                fileItem.setBatchNo(anBatchNo);
                this.updateByPrimaryKeySelective(fileItem) ;
            } else if (fileItem.getBatchNo().equals(anBatchNo) == false) {
                final CustFileItem tempFileItem = new CustFileItem();
                tempFileItem.initDuplicateConflictValue(fileItem);
                tempFileItem.setBatchNo(anBatchNo);
                this.saveAndUpdateFileItem(tempFileItem, anOperator);
            }
        }
        
        return anBatchNo;
    }

    /**
     * 更新文件列表的批次号，如果批次号不存在，则创建批次号
     * @param anFileList 以逗号分隔的文件编号
     * @param anBatchNo 文件批次号
     * @return 返回文件批次号
     */
    public Long updateAndDelCustFileItemInfo(final String anFileList, Long anBatchNo) {
        /*if (BetterStringUtils.isBlank(anFileList)) {

            return anBatchNo;
        }*/

        if (anBatchNo == null) {
            anBatchNo = CustFileUtils.findBatchNo();
        }
        logger.info("fileList:" + anFileList);

        final List<CustFileItem> fileItems = this.selectByProperty("batchNo", anBatchNo);

        final String[] strFileItems = BetterStringUtils.split(anFileList, ",");
        for (final String item : strFileItems) {
            if (BetterStringUtils.isNotBlank(item)) {
                final Long fileId = Long.valueOf(item.trim());

                final CustFileItem fileItem = checkFileItem(fileId, fileItems);
                if (fileItem == null) {
                    updateFileItems(anBatchNo, fileId);
                } else {
                    fileItems.remove(fileItem);
                }
            }
        }

        for (final CustFileItem fileItem: fileItems) {
            deleteFileItem(fileItem.getId(), fileItem.getBatchNo());
        }

        return anBatchNo;
    }

    /**
     * @param anFileId
     * @param anFileItems
     * @return
     */
    private CustFileItem checkFileItem(final Long anFileId, final List<CustFileItem> anFileItems) {
        for (final CustFileItem fileItem : anFileItems) {
            if (fileItem.getId().equals(anFileId)) {
                return fileItem;
            }
        }
        return null;
    }

    /**
     * 根据batchNo获得一个文件的信息
     *
     * @param anBatchNo
     * @return
     */
    public CustFileItem findOneByBatchNo(final Long anBatchNo) {
        final List<CustFileItem> fileList = this.selectByProperty("batchNo", anBatchNo);

        return Collections3.getFirst(fileList);
    }

    public Map<String, CustFileItem> findItems(final Map<String, Long> anMap) {
        final Map<String, CustFileItem> itemsMap = new HashMap<String, CustFileItem>();
        for (final String tmpKey : queryConds) {
            final Long tmpValue = anMap.get(tmpKey);
            final CustFileItem item = this.findOne(tmpValue);
            itemsMap.put(tmpKey, item);
        }
        return itemsMap;
    }

    /**
     * 根据批次号，查询本批次上传的文件。
     *
     * @param anBatchNo
     *            批次号
     * @return
     */
    public List<CustFileItem> findCustFiles(final Long anBatchNo) {
        if(anBatchNo != null && anBatchNo > 0){
            final Map<String, Object> conditionMap = new HashMap<>();
            conditionMap.put("batchNo", anBatchNo);
            conditionMap.put("GTid", 0L);
            return this.selectByProperty(conditionMap);
        }else{
            return Collections.EMPTY_LIST; 
        }
    }

    /**
     * 根据批次号和文件业务类型，获得相关的文件信息
     * @param anBatchNoList  批次号列表
     * @param anbusinTypeList 文件类型列表
     * @return
     */
    public List<CustFileItem> findCustFilesByBatch(final List<Long> anBatchNoList, final List<String> anbusinTypeList) {

        return findCustFilesByBatch(anBatchNoList, anbusinTypeList, true);
    }

    /**
     * 根据批次号获得文件列表
     * @param anBatchNoList 批次号列表
     * @return
     */
    public List<CustFileItem> findCustFilesByBatch(final List<Long> anBatchNoList) {

        return findCustFilesByBatch(anBatchNoList, null, false);
    }

    private List<CustFileItem> findCustFilesByBatch(final List<Long> anBatchNoList, final List<String> anbusinTypeList, final boolean anMust) {

        // 如果出现条件为null，不能查询
        if (Collections3.isEmpty(anbusinTypeList) && anMust || Collections3.isEmpty(anBatchNoList)) {

            return new ArrayList();
        }

        final Map map = new HashMap();
        map.put("batchNo", anBatchNoList);
        map.put("fileInfoType", anbusinTypeList);
        return this.selectByProperty(map);
    }

    /**
     * 保存文件信息，如果存在就更新，不存在就增加
     * @param anFileItem
     * @param anOperator
     * @return
     */
    public boolean saveAndUpdateFileItem(final CustFileItem anFileItem, final CustOperatorInfo anOperator) {
        final CustFileItem tmpFileItem = this.selectByPrimaryKey(anFileItem.getId());
        if (tmpFileItem == null){
            anFileItem.initAddValue(anOperator);
            this.insert(anFileItem);
        }
        else {
            anFileItem.initModifyValue(anOperator);
            this.updateByPrimaryKey(anFileItem);
        }

        return true;
    }

    /**
     * 删除附件，具体逻辑是如果存在并匹配上了，就可以设置batchNo为负值，便于今后查询
     *
     * @param anId
     *            文件ID号
     * @param anBatchNo
     *            文件批次号
     */
    public boolean deleteFileItem(final Long anId, final Long anBatchNo) {
        logger.info("Detach file item from accept bill with item id " + anId);
        if (null == anId) {
            logger.error("附件编号不能为空");
            return false;
        }
        final CustFileItem item = this.selectByPrimaryKey(anId);
        if (null == item) {
            logger.error("不能获取附件！" + anId + ", anBatchNo=" + anBatchNo);

            return false;
        }
        if (anBatchNo <= 0 || !item.getBatchNo().equals(anBatchNo)) {
            logger.error("获取附件！" + anId + ", anBatchNo=" + anBatchNo + ", batchNo不一致!");

            return false;
        }

        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        if (BetterStringUtils.equals(operator.getOperOrg(), item.getOperOrg()) == false) {
            logger.error("不是同一OperOrg的数据,不允许删除! operUser=" + operator.getName());
            return false;
        }

        item.setBatchNo(-anBatchNo);

        item.initModifyValue(operator);
        final int result = this.updateByPrimaryKeySelective(item);
        logger.debug("update result:" + result);
        return true;
    }

    /**
     * 保存文件信息，如果存在就更新，不存在就增加
     * @param anFileItem
     * @return
     */
    public boolean saveAndUpdateFileItem(final CustFileItem anFileItem) {
        final CustFileItem tmpFileItem = this.selectByPrimaryKey(anFileItem.getId());
        anFileItem.setRegDate(BetterDateUtils.getNumDate());
        anFileItem.setRegTime(BetterDateUtils.getNumTime());
        if (tmpFileItem == null) {
            this.insert(anFileItem);
        }
        else {
            this.updateByPrimaryKey(anFileItem);
        }

        return true;
    }

    public List<CustFileItem> findFileListByIds(String[] anIds) {
        return this.selectByProperty("id", anIds);
    }

}
