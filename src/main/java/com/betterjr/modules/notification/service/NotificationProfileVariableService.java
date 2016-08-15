package com.betterjr.modules.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.notification.dao.NotificationProfileVariableMapper;
import com.betterjr.modules.notification.entity.NotificationProfileVariable;

@Service
public class NotificationProfileVariableService extends BaseService<NotificationProfileVariableMapper, NotificationProfileVariable> {

    /**
     * 查找 variable
     * @param anChannelProfileId
     * @return
     */
    public List<NotificationProfileVariable> queryVariableByProfileId(Long anChannelProfileId) {
        BTAssert.notNull(anChannelProfileId, "模板编号不允许为空!");
        
        return this.selectByProperty("channelProfileId", anChannelProfileId);
    }
}
