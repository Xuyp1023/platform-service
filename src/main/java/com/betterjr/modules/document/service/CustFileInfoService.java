package com.betterjr.modules.document.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.document.dao.CustFileInfoMapper;
import com.betterjr.modules.document.entity.CustFileInfo;
import com.betterjr.modules.document.entity.CustFileItem;

@Service
public class CustFileInfoService extends BaseService<CustFileInfoMapper, CustFileInfo> {

    @Autowired
    private CustFileItemService fileItemService;
    protected static final Logger logger = LoggerFactory.getLogger(CustFileInfoService.class);

    public List<CustFileItem> findUploadFileByAgency(String anRequestNo, String anBusinFlag, String anAgecyNo) {
        Map<String, Object> map = new HashMap();
        map.put("requestNo", anRequestNo);
        map.put("businFlag", anBusinFlag);
        List<CustFileInfo> list = this.selectByProperty(map);
        if (Collections3.isEmpty(list) == false) {
            CustFileInfo fileInfo = list.get(0);
            if (StringUtils.isBlank(anAgecyNo)
                    || StringUtils.isNotBlank(anAgecyNo) && anAgecyNo.contains(fileInfo.getAgencyNo())) {
                List<CustFileItem> result = fileItemService.findCustFiles(fileInfo.getId());
                for (CustFileItem item : result) {
                    logger.warn("query fileItem " + item);
                    item.setFileInfo(fileInfo);
                }
                return result;
            }
        }

        return new ArrayList<CustFileItem>();
    }

    public List<CustFileItem> findUploadFiles(String anRequestNo, String anBusinFlag) {

        return findUploadFileByAgency(anRequestNo, anBusinFlag, null);
    }

    public List<CustFileItem> findUploadFileByCustNo(Long custNo, String anBusinFlag) {
        Map<String, Object> map = new HashMap();
        map.put("custNo", custNo);
        map.put("businFlag", anBusinFlag);
        logger.info("findUploadFileByCustNo request parameter :" + map);
        List<CustFileInfo> list = this.selectByProperty(map);
        if (Collections3.isEmpty(list) == false) {
            CustFileInfo fileInfo = list.get(0);
            List<CustFileItem> result = fileItemService.findCustFiles(fileInfo.getId());
            for (CustFileItem item : result) {
                item.setFileInfo(fileInfo);
            }

            return result;
        } else {
            logger.warn("findUploadFileByCustNo CustFileInfo is null");
        }

        return new ArrayList<CustFileItem>();
    }
}
