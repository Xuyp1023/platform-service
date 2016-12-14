package com.betterjr.modules.document.dubbo;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.data.CheckDataResult;
import com.betterjr.modules.document.IAgencyAuthFileGroupService;
import com.betterjr.modules.document.data.FileStoreType;
import com.betterjr.modules.document.data.OSSConfigInfo;
import com.betterjr.modules.document.entity.AgencyAuthorFileGroup;
import com.betterjr.modules.document.entity.AuthorFileGroup;
import com.betterjr.modules.document.service.AgencyAuthFileGroupService;
import com.betterjr.modules.document.service.AuthorFileGroupService;

@Service(interfaceClass=IAgencyAuthFileGroupService.class)
public class AgencyAuthFileGroupDubboService implements IAgencyAuthFileGroupService {
    
    @Autowired
    private AgencyAuthFileGroupService agencyAuthFileGroupService;
 
    @Autowired
    private AuthorFileGroupService authorFileGroupService;

    @Override
    public List<AgencyAuthorFileGroup> findAuthorFileGroup(String anAgencyNo, String anBusinFlag) {

        return agencyAuthFileGroupService.findAuthorFileGroup(anAgencyNo, anBusinFlag);
    }

    @Override
    public List<AgencyAuthorFileGroup> findAuthorFileGroup(String[] anAgencyNoList, String anBusinFlag) {

        return agencyAuthFileGroupService.findAuthorFileGroup(anAgencyNoList, anBusinFlag);
    }

    @Override
    public List<String> composeList(String anAgencyNo, String anBusinFlag) {

        return agencyAuthFileGroupService.composeList(anAgencyNo, anBusinFlag);
    }

    @Override
    public Map<String, AuthorFileGroup> findAllFileGroup() {

        return authorFileGroupService.findAllFileGroup();
    }

    @Override
    public List<AuthorFileGroup> findFileGroupList(String anBusinFlag) {

        return authorFileGroupService.findFileGroupList(anBusinFlag);
    }

    @Override
    public List<AuthorFileGroup> findCustFileGroupList() {

        return authorFileGroupService.findCustFileGroupList();
    }

    @Override
    public OSSConfigInfo findOSSConfigInfo() {

        return authorFileGroupService.findOSSConfigInfo();
    }

    @Override
    public String findCreateFilePath(String anFileInfoType) {

        return authorFileGroupService.findCreateFilePath(anFileInfoType);
    }

    @Override
    public String findAbsFilePath(String anFilePath) {

        return authorFileGroupService.findAbsFilePath(anFilePath);
    }

    @Override
    public String findTempFilePath(String anAgencyNo, String anFileInfoType) {

        return agencyAuthFileGroupService.findTempFilePath(anAgencyNo, anFileInfoType);
    }

    @Override
    public String findDemoFilePath(String anAgencyNo, String anFileInfoType) {

        return agencyAuthFileGroupService.findDemoFilePath(anAgencyNo, anFileInfoType);
    }

    @Override
    public FileStoreType findFileStoreType(String anFileInfoType) {
        
        return authorFileGroupService.findFileStoreType(anFileInfoType);
    }
    
    /***
     * 根据不同条件查询返回文件类型对象
     * @param anMap 条件
     * @return
     */
    @Override
    public AgencyAuthorFileGroup findAuthorFileGroupByMap(Map<String, Object> anMap){
        return agencyAuthFileGroupService.findAuthorFileGroupByMap(anMap);
    }
    
    public CheckDataResult findFileTypePermit(String anFileInfoType, String anFileType){
        
        return authorFileGroupService.findFileTypePermit(anFileInfoType, anFileType);
    }
}
