package com.betterjr.modules.document.dubbo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.modules.document.IAgencyAuthFileGroupService;
import com.betterjr.modules.document.entity.AgencyAuthorFileGroup;
import com.betterjr.modules.document.service.AgencyAuthFileGroupService;

@Service(interfaceClass=IAgencyAuthFileGroupService.class)
public class AgencyAuthFileGroupDubboService implements IAgencyAuthFileGroupService {
    
    @Autowired
    private AgencyAuthFileGroupService agencyAuthFileGroupService;

    @Override
    public List<AgencyAuthorFileGroup> findAuthorFileGroup(String anAgencyNo, String anBusinFlag) {
        // TODO Auto-generated method stub
        return agencyAuthFileGroupService.findAuthorFileGroup(anAgencyNo, anBusinFlag);
    }

    @Override
    public List<AgencyAuthorFileGroup> findAuthorFileGroup(String[] anAgencyNoList, String anBusinFlag) {
        // TODO Auto-generated method stub
        return agencyAuthFileGroupService.findAuthorFileGroup(anAgencyNoList, anBusinFlag);
    }

    @Override
    public List<String> composeList(String anAgencyNo, String anBusinFlag) {
        // TODO Auto-generated method stub
        return agencyAuthFileGroupService.composeList(anAgencyNo, anBusinFlag);
    }

}
