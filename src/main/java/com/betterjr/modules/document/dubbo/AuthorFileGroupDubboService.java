package com.betterjr.modules.document.dubbo;

import java.util.List;
import java.util.Map;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.modules.document.IAuthorFileGroupService;
import com.betterjr.modules.document.entity.AuthorFileGroup;
import com.betterjr.modules.document.service.AuthorFileGroupService;

@Service(interfaceClass=IAuthorFileGroupService.class)
public class AuthorFileGroupDubboService implements IAuthorFileGroupService {
    
    private AuthorFileGroupService authorFileGroupService;

    @Override
    public Map<String, AuthorFileGroup> findAllFileGroup() {
        // TODO Auto-generated method stub
        return authorFileGroupService.findAllFileGroup();
    }

    @Override
    public List<AuthorFileGroup> findFileGroupList(String anBusinFlag) {
        // TODO Auto-generated method stub
        return authorFileGroupService.findFileGroupList(anBusinFlag);
    }

    @Override
    public List<AuthorFileGroup> findCustFileGroupList() {
        // TODO Auto-generated method stub
        return authorFileGroupService.findCustFileGroupList();
    }

}
