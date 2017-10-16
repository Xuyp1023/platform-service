package com.betterjr.modules.notification.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.notification.dao.NotificationProfileVariableMapper;
import com.betterjr.modules.notification.entity.NotificationProfileVariable;

@Service
public class NotificationProfileVariableService
        extends BaseService<NotificationProfileVariableMapper, NotificationProfileVariable> {

    /**
     * 查询 variable 列表
     */
    public List<NotificationProfileVariable> queryVariableByProfileId(Long anChannelProfileId) {
        BTAssert.notNull(anChannelProfileId, "模板编号不允许为空!");

        return this.selectByProperty("channelProfileId", anChannelProfileId);
    }

    /**
     * 将基础数据copy到新的数据上
     */
    public void saveCopyBaseDataToTargetData(Long anBaseChannelProfileId, Long anTargetChannelProfileId,
            CustInfo anCustInfo, CustOperatorInfo anOperator) {
        List<NotificationProfileVariable> profileVariables = queryVariableByProfileId(anBaseChannelProfileId);

        for (NotificationProfileVariable profileVariable : profileVariables) {
            NotificationProfileVariable tempProfileVariable = new NotificationProfileVariable();
            tempProfileVariable.initAddValue(anTargetChannelProfileId, profileVariable, anCustInfo, anOperator);
            this.insert(tempProfileVariable);
        }
    }
}
