package com.betterjr.modules.document.service;

import com.betterjr.common.data.NormalStatus;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.reflection.ReflectionUtils;
import com.betterjr.modules.document.dao.AuthorFileGroupMapper;
import com.betterjr.modules.document.entity.AuthorFileGroup;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class AuthorFileGroupService extends BaseService<AuthorFileGroupMapper, AuthorFileGroup> {

    public Map<String, AuthorFileGroup> findAllFileGroup() {
        Map<String, AuthorFileGroup> fileGroupMap = ReflectionUtils.listConvertToMap(
                selectByProperty("groupStatus", NormalStatus.VALID_STATUS.value), "fileInfoType");

        return fileGroupMap;
    }

    public List<AuthorFileGroup> findFileGroupList(String anBusinFlag) {
        Map<String, Object> propAndValues = new HashMap();
        propAndValues.put("businFlag", anBusinFlag);
        propAndValues.put("groupStatus", NormalStatus.VALID_STATUS.value);

        return this.selectByProperty(propAndValues);
    }

    public List<AuthorFileGroup> findCustFileGroupList() {

        return findFileGroupList("01");
    }
}