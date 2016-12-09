package com.betterjr.modules.document.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.QueryTermBuilder;
import com.betterjr.modules.document.dao.AgencyAuthorFileGroupMapper;
import com.betterjr.modules.document.data.FileStoreType;
import com.betterjr.modules.document.entity.AgencyAuthorFileGroup;
import com.betterjr.modules.document.utils.DownloadFileService;

@Service
public class AgencyAuthFileGroupService extends BaseService<AgencyAuthorFileGroupMapper, AgencyAuthorFileGroup> {

    protected static Map<String, AgencyAuthorFileGroup> fileGroupMap = new HashMap();
    @Autowired
    private AuthorFileGroupService fileGroupService;

    @PostConstruct
    public void initConfigValue() {
        Map<String, AgencyAuthorFileGroup> tmpGroupMap = new HashMap();
        for (AgencyAuthorFileGroup fileGroup : this.selectByProperty("groupStatus", "1")) {
            tmpGroupMap.put(fileGroup.findComposeKey(), fileGroup);
        }
        ;

        synchronized (fileGroupMap) {
            fileGroupMap.clear();
            fileGroupMap.putAll(tmpGroupMap);
        }
        DownloadFileService fileService = new DownloadFileService(fileGroupMap);
        fileService.start();
    }

    /**
     * 根据合作伙伴编码和业务类型，获得授权使用的业务文件类型
     * 
     * @param anAgencyNo
     *            合作伙伴编码
     * @param anBusinFlag
     *            业务文件类型
     * @return
     */
    public List<AgencyAuthorFileGroup> findAuthorFileGroup(String anAgencyNo, String anBusinFlag) {

        return findAuthorFileGroup(new String[] { anAgencyNo }, anBusinFlag);
    }

    public List<AgencyAuthorFileGroup> findAuthorFileGroup(String[] anAgencyNoList, String anBusinFlag) {
        Map map = new HashMap();
        map.put("agencyNo", anAgencyNoList);
        map.put("businFlag", anBusinFlag);

        return this.selectByProperty(map);
    }

    /**
     * 根据合作伙伴编码和业务类型，获得授权使用的业务文件类型，组合为直接使用的数据
     * 
     * @param anAgencyNo
     *            合作伙伴编码
     * @param anBusinFlag
     *            业务文件类型
     * @return
     */
    public List<String> composeList(String anAgencyNo, String anBusinFlag) {
        List<String> result = new ArrayList<String>();
        for (AgencyAuthorFileGroup fileGroup : this.findAuthorFileGroup(anAgencyNo, anBusinFlag)) {
            result.add(fileGroup.getFileInfoType());
        }

        return result;
    }

    /**
     * 查找模板文件路径
     * 
     * @param anAgencyNo
     *            合作机构
     * @param anFileInfoType
     *            文件类型
     * @return
     */
    public String findTempFilePath(String anAgencyNo, String anFileInfoType) {
        
        return findFilePath(anAgencyNo, anFileInfoType, false);
    }

    /**
     * 查找demo样张的文件路径
     * 
     * @param anFileInfoType
     * @return
     */
    public String findDemoFilePath(String anAgencyNo, String anFileInfoType) {

        return findFilePath(anAgencyNo, anFileInfoType, true);
    }

    private String findFilePath(String anAgencyNo, String anFileInfoType, boolean anDemo) {
        if (BetterStringUtils.isBlank(anAgencyNo) || BetterStringUtils.isBlank(anFileInfoType)) {
            return "";
        }
        List<AgencyAuthorFileGroup> tmpList = this.selectByProperty(
                QueryTermBuilder.newInstance().put("agencyNo", anAgencyNo).put("fileInfoType", anFileInfoType).put("groupStatus", "1").build());
        AgencyAuthorFileGroup tmpFileGroup = Collections3.getOnlyOne(tmpList);
        String tmpPath = "";
        if (tmpFileGroup != null) {
            if (anDemo) {
                tmpPath = tmpFileGroup.findDemoPath();
            }
            else {
                tmpPath = tmpFileGroup.findTempPath();
            }
            FileStoreType storeType = fileGroupService.findFileStoreType(anFileInfoType);
            if (storeType == FileStoreType.FILE_STORE) {
                tmpPath = fileGroupService.findAbsFilePath(tmpPath);
            }
        }
        return tmpPath;
    }
    

    
    /***
     * 根据不同条件查询返回文件类型对象
     * @param anMap 条件
     * @return
     */
    public AgencyAuthorFileGroup findAuthorFileGroupByMap(Map<String, Object> anMap){
        return Collections3.getFirst(this.selectByProperty(anMap));
    }
}