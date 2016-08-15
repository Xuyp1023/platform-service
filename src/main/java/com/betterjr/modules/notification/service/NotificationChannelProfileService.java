package com.betterjr.modules.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.notification.dao.NotificationChannelProfileMapper;
import com.betterjr.modules.notification.entity.NotificationChannelProfile;

@Service
public class NotificationChannelProfileService extends BaseService<NotificationChannelProfileMapper, NotificationChannelProfile> {
    /**
     * 查找 channel profile
     */
    public List<NotificationChannelProfile> queryChannelProfileByParentProfileId(Long anProfileId) {
        BTAssert.notNull(anProfileId, "主模板编号不允许为空!");

        return this.selectByProperty("profileId", anProfileId);
    }

    /**
     * 修改通道模板
     */
    public NotificationChannelProfile saveChannelProfile(NotificationChannelProfile anChannelProfile, Long anChannelProfileId) {
        BTAssert.notNull(anChannelProfileId, "消息通道模板编号不允许为空!");
        BTAssert.notNull(anChannelProfile, "消息通知模板内容不允许为空!");

        NotificationChannelProfile tempChannelProfile = this.selectByPrimaryKey(anChannelProfileId);
        BTAssert.notNull(tempChannelProfile, "没有找到相应的模板!");

        tempChannelProfile.initModifyValue(anChannelProfile);
        this.updateByPrimaryKeySelective(tempChannelProfile);
        return tempChannelProfile;
    }

}
