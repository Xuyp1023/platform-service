package com.betterjr.modules.notification.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.notification.dao.NotificationProfileMapper;
import com.betterjr.modules.notification.entity.NotificationProfile;

@Service
public class NotificationProfileService extends BaseService<NotificationProfileMapper, NotificationProfile> {

    /**
     * 查找 profile
     */
    public NotificationProfile findProfileByProfileNameAndCustNo(String anProfileName, Long anCustNo) {
        BTAssert.notNull(anProfileName, "模板名称不允许为空!");
        BTAssert.notNull(anCustNo, "客户编号不允许为空!");

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("profileName", anProfileName);
        conditionMap.put("custNo", anCustNo);

        return Collections3.getFirst(this.selectByProperty(conditionMap));
    }

    /**
     * 根据客户编号查询消息模板列表
     */
    public Page<NotificationProfile> queryNotificationProfile(Long anCustNo, int anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空!");

        return this.selectPropertyByPage("custNo", anCustNo, anPageNum, anPageSize, anFlag == 1);
    }

    /**
     * 设置消息模板状态
     */
    public NotificationProfile saveSetNotificationProfileStatus(Long anProfileId, String anBusinStatus) {
        BTAssert.notNull(anProfileId, "模板编号不允许为空!");
        BTAssert.notNull(anBusinStatus, "状态不允许为空!");

        NotificationProfile profile = this.selectByPrimaryKey(anProfileId);
        profile.initModifyValue(anBusinStatus);
        this.updateByPrimaryKeySelective(profile);
        return profile;
    }
}
