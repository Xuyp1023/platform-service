package com.betterjr.modules.notification.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.notification.NotificationConstants;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.entity.CustCertInfo;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustCertService;
import com.betterjr.modules.notification.dao.NotificationProfileMapper;
import com.betterjr.modules.notification.entity.NotificationProfile;

@Service
public class NotificationProfileService extends BaseService<NotificationProfileMapper, NotificationProfile> {
    @Resource
    private CustAccountService accountService;

    @Resource
    private CustCertService certService;

    @Resource
    private NotificationChannelProfileService channelProfileService;

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

    /**
     * 将基础数据copy到新的数据上
     */
    public void saveInitCustomerNotificationProfile(CustInfo anCustInfo, CustOperatorInfo anOperator) {
        CustCertInfo certInfo = certService.findCertByOperOrg(anCustInfo.getOperOrg());

        if (UserUtils.coreCustomer(certInfo)) { // 核心企业
            saveCopyBaseDataToTargetData(NotificationConstants.PROFILE_TYPE_CORE, anCustInfo, anOperator);
        }

        if (UserUtils.factorCustomer(certInfo)) {// 资金方
            saveCopyBaseDataToTargetData(NotificationConstants.PROFILE_TYPE_FACTOR, anCustInfo, anOperator);
        }

        if (UserUtils.sellerCustomer(certInfo)) {// 经销商
            saveCopyBaseDataToTargetData(NotificationConstants.PROFILE_TYPE_SELLER, anCustInfo, anOperator);
        }

        if (UserUtils.supplierCustomer(certInfo)) {// 供应商
            saveCopyBaseDataToTargetData(NotificationConstants.PROFILE_TYPE_SUPPLIER, anCustInfo, anOperator);
        }
        // TODO 得到公司类型
        // List<NotificationProfile> profiles = custInfo.getCustType();
    }

    public void saveCopyBaseDataToTargetData(String anProfileType, CustInfo anCustInfo, CustOperatorInfo anOperator) {
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("profileType", anProfileType);
        conditionMap.put("LTid", 0);
        List<NotificationProfile> notificationProfiles = this.selectByProperty(conditionMap);

        for (NotificationProfile notificationProfile : notificationProfiles) {
            NotificationProfile tempNotificationProfile = new NotificationProfile();
            tempNotificationProfile.initAddValue(notificationProfile, anCustInfo, anOperator);

            this.insert(tempNotificationProfile);
            channelProfileService.saveCopyBaseDataToTargetData(notificationProfile.getId(), tempNotificationProfile.getId(), anCustInfo, anOperator);
        }
    }
}
