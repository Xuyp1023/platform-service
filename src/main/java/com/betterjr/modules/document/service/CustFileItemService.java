package com.betterjr.modules.document.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
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

    private static String[] queryConds = new String[] { "bizLicenseFile", "orgCodeFile", "taxRegistFile", "representIdFile", "bankAcctAckFile",
            "brokerIdFile" };

    /**
     * 根据文件ID号，查询单个文件信息
     * 
     * @param id
     * @return
     */
    public CustFileItem findOne(Long id) {
        return this.selectByPrimaryKey(id);
    }

    /**
     * 根据文件编号，更新文件批次号
     * @param batchNo 文件批次号
     * @param fileItemId 文件编号
     * @return
     */
    public boolean updateFileItems(Long batchNo, Long fileItemId) {
        CustFileItem fileItem = this.selectByPrimaryKey(fileItemId);
        fileItem.setBatchNo(batchNo);

        return this.updateByPrimaryKeySelective(fileItem) == 1;
    }
    
    /**
     * 根据文件编号,更新文件批次号,如果文件已经被其他批次号使用,copy一份
     * @param anBatchNo
     * @param anFileItemId
     * @return
     */
    private boolean updateAndDuplicateConflictFileItems(Long anBatchNo, Long anFileItemId, CustOperatorInfo anOperator) {
        CustFileItem fileItem = this.selectByPrimaryKey(anFileItemId);
        
        if (fileItem != null) {
            if (fileItem.getBatchNo().equals(0L) == true) {
                fileItem.setBatchNo(anBatchNo);
                return this.updateByPrimaryKeySelective(fileItem) == 1;
            } else if (fileItem.getBatchNo().equals(anBatchNo) == false) {
                CustFileItem tempFileItem = new CustFileItem();
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
    public Long updateAndDuplicateConflictFileItemInfo(String anFileList, Long anBatchNo, CustOperatorInfo anOperator) {
        if (BetterStringUtils.isBlank(anFileList)) {

            return anBatchNo;
        }

        if (anBatchNo == null) {
            anBatchNo = CustFileUtils.findBatchNo();
        }
        
        logger.info("fileList:" + anFileList);
        String[] fileItems = BetterStringUtils.split(anFileList, ",");
        for (String item : fileItems) {
            if (BetterStringUtils.isNotBlank(item)) {
                Long fileItemId = Long.valueOf(item.trim());
                updateAndDuplicateConflictFileItems(anBatchNo, fileItemId, anOperator);
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
    public Long updateCustFileItemInfo(String anFileList, Long anBatchNo) {
        if (BetterStringUtils.isBlank(anFileList)) {

            return anBatchNo;
        }

        if (anBatchNo == null) {
            anBatchNo = CustFileUtils.findBatchNo();
        }

        logger.info("fileList:" + anFileList);
        String[] fileItems = BetterStringUtils.split(anFileList, ",");
        for (String item : fileItems) {
            if (BetterStringUtils.isNotBlank(item)) {
                Long fileId = Long.valueOf(item.trim());
                updateFileItems(anBatchNo, fileId);
            }
        }

        return anBatchNo;
    }

    /**
     * 根据batchNo获得一个文件的信息
     * 
     * @param anBatchNo
     * @return
     */
    public CustFileItem findOneByBatchNo(Long anBatchNo) {
        List<CustFileItem> fileList = this.selectByProperty("batchNo", anBatchNo);

        return Collections3.getFirst(fileList);
    }

    public Map<String, CustFileItem> findItems(Map<String, Long> anMap) {
        Map<String, CustFileItem> itemsMap = new HashMap<String, CustFileItem>();
        for (String tmpKey : queryConds) {
            Long tmpValue = anMap.get(tmpKey);
            CustFileItem item = this.findOne(tmpValue);
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
    public List<CustFileItem> findCustFiles(Long anBatchNo) {
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("batchNo", anBatchNo);
        conditionMap.put("GTid", 0L);

        return this.selectByProperty(conditionMap);
    }

    /**
     * 根据批次号和文件业务类型，获得相关的文件信息
     * @param anBatchNoList  批次号列表
     * @param anbusinTypeList 文件类型列表
     * @return
     */
    public List<CustFileItem> findCustFilesByBatch(List<Long> anBatchNoList, List<String> anbusinTypeList) {

        return findCustFilesByBatch(anBatchNoList, anbusinTypeList, true);
    }

    /**
     * 根据批次号获得文件列表
     * @param anBatchNoList 批次号列表
     * @return
     */
    public List<CustFileItem> findCustFilesByBatch(List<Long> anBatchNoList) {
        
        return findCustFilesByBatch(anBatchNoList, null, false);
    }

    private List<CustFileItem> findCustFilesByBatch(List<Long> anBatchNoList, List<String> anbusinTypeList, boolean anMust) {

        // 如果出现条件为null，不能查询
        if (Collections3.isEmpty(anbusinTypeList) && anMust || Collections3.isEmpty(anBatchNoList)) {

            return new ArrayList();
        }

        Map map = new HashMap();
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
    public boolean saveAndUpdateFileItem(CustFileItem anFileItem, CustOperatorInfo anOperator) {
        CustFileItem tmpFileItem = this.selectByPrimaryKey(anFileItem.getId());
        if (tmpFileItem == null) {
            anFileItem.initAddValue(anOperator);
            this.insert(anFileItem);
        }
        else {
            tmpFileItem.initModifyValue(anOperator);
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
    public boolean deleteFileItem(Long anId, Long anBatchNo) {
        logger.info("Detach file item from accept bill with item id " + anId);
        if (null == anId) {
            logger.error("附件编号不能为空");
            return false;
        }
        CustFileItem item = this.selectByPrimaryKey(anId);
        if (null == item) {
            logger.error("不能获取附件！" + anId + ", anBatchNo=" + anBatchNo);

            return false;
        }
        if (anBatchNo <= 0 || item.getBatchNo() != anBatchNo) {
            logger.error("获取附件！" + anId + ", anBatchNo=" + anBatchNo + ", batchNo不一致!");

            return false;
        }

        CustOperatorInfo operator = UserUtils.getOperatorInfo();
        if (BetterStringUtils.equals(operator.getOperOrg(), item.getOperOrg()) == false) {
            logger.error("不是同一OperOrg的数据,不允许删除! operUser=" + operator.getName());
            return false;
        }
        
        item.setBatchNo(-anBatchNo);

        item.initModifyValue(operator);
        int result = this.updateByPrimaryKeySelective(item);
        logger.debug("update result:" + result);
        return true;
    }

}
