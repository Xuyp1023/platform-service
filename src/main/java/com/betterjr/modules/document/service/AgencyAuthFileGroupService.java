package com.betterjr.modules.document.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.modules.document.dao.AgencyAuthorFileGroupMapper;
import com.betterjr.modules.document.entity.AgencyAuthorFileGroup;

@Service
public class AgencyAuthFileGroupService extends BaseService<AgencyAuthorFileGroupMapper, AgencyAuthorFileGroup> {
    
    protected static Map<String, AgencyAuthorFileGroup> fileGroupMap = new HashMap(); 
    @PostConstruct
    public void initConfigValue() {
        Map<String, AgencyAuthorFileGroup>  tmpGroupMap = new HashMap();
        for( AgencyAuthorFileGroup  fileGroup : this.selectByProperty("groupStatus", "1")){
            tmpGroupMap.put(fileGroup.findComposeKey(), fileGroup);
        };
        
        synchronized(fileGroupMap){
           fileGroupMap.clear();
           fileGroupMap.putAll(tmpGroupMap);
        }
        DownloadFileService fileService = new DownloadFileService(fileGroupMap);
        fileService.start();
    }
    
    /**
     * 根据合作伙伴编码和业务类型，获得授权使用的业务文件类型
     * @param anAgencyNo 合作伙伴编码
     * @param anBusinFlag 业务文件类型
     * @return
     */
    public List<AgencyAuthorFileGroup> findAuthorFileGroup(String anAgencyNo, String anBusinFlag){
       
        return findAuthorFileGroup(new String[]{anAgencyNo}, anBusinFlag);
    }
    
    public List<AgencyAuthorFileGroup> findAuthorFileGroup(String[] anAgencyNoList, String anBusinFlag){
        Map map = new HashMap();
        map.put("agencyNo",  anAgencyNoList);
        map.put("businFlag", anBusinFlag);
        
        return this.selectByProperty(map);
    }
    
    /**
     * 根据合作伙伴编码和业务类型，获得授权使用的业务文件类型，组合为直接使用的数据
     * @param anAgencyNo 合作伙伴编码
     * @param anBusinFlag 业务文件类型
     * @return
     */
    public List<String> composeList(String anAgencyNo, String anBusinFlag){
        List<String> result = new ArrayList<String>(); 
        for (AgencyAuthorFileGroup fileGroup : this.findAuthorFileGroup(anAgencyNo, anBusinFlag)){
            result.add(fileGroup.getFileInfoType());
        }
        
        return result;
    }
    
}