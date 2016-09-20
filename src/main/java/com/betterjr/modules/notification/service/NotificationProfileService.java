package com.betterjr.modules.notification.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.entity.CustCertInfo;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustCertService;
import com.betterjr.modules.notification.constants.NotificationConstants;
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
    public NotificationProfile findProfileByProfileNameAndCustNo(final String anProfileName, final Long anCustNo) {
        BTAssert.notNull(anProfileName, "模板名称不允许为空!");
        BTAssert.notNull(anCustNo, "客户编号不允许为空!");

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("profileName", anProfileName);
        conditionMap.put("custNo", anCustNo);
        conditionMap.put("custom", NotificationConstants.PROFILE_CUSTOM);

        NotificationProfile profile = Collections3.getFirst(this.selectByProperty(conditionMap));

        // 如果找不到 查找 Default模板
        if (profile == null) {
            profile = findDefaultProfileByProfileNameAndCustNo(anProfileName, anCustNo);
        }

        return profile;
    }

    public NotificationProfile findDefaultProfileByProfileNameAndCustNo(final String anProfileName, final Long anCustNo) {
        final List<String> types = getCustTypesByCustNo(anCustNo); // 公司类型

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("profileName", anProfileName);
        conditionMap.put("LTid", 0);                    // 编号小于0
        conditionMap.put("NEcustom", NotificationConstants.PROFILE_CUSTOM);              // NotEqual非custom
        conditionMap.put("profileType", types);         // 公司类型需要匹配

        return Collections3.getFirst(this.selectByProperty(conditionMap));
    }

    // 模板类型  0:平台,1:保理公司,2:核心企业,3:供应商,4:经销商
    public List<String> getCustTypesByCustNo(final Long anCustNo) {
        final List<String> types = new ArrayList<>();

        final CustInfo custInfo = accountService.findCustInfo(anCustNo);
        if (custInfo == null) {
            return types;
        }

        final CustCertInfo certInfo = certService.findCertByOperOrg(custInfo.getOperOrg());
        if (certInfo == null) {
            return types;
        }

        if (UserUtils.coreCustomer(certInfo)) {
            types.add(NotificationConstants.PROFILE_TYPE_CORE);
        }
        if (UserUtils.platformCustomer(certInfo)) {
            types.add(NotificationConstants.PROFILE_TYPE_PLATFORM);
        }
        if (UserUtils.factorCustomer(certInfo)) {
            types.add(NotificationConstants.PROFILE_TYPE_FACTOR);
        }
        if (UserUtils.supplierCustomer(certInfo)) {
            types.add(NotificationConstants.PROFILE_TYPE_SUPPLIER);
        }
        if (UserUtils.sellerCustomer(certInfo)) {
            types.add(NotificationConstants.PROFILE_TYPE_SELLER);
        }
        return types;
    }

    /**
     * 根据客户编号查询消息模板列表
     */
    public Page<NotificationProfile> queryNotificationProfile(final Long anCustNo, final int anFlag, final int anPageNum, final int anPageSize) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空!");

        final List<String> types = getCustTypesByCustNo(anCustNo);  // 公司类型

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("LTid", 0);
        conditionMap.put("NEcustom", NotificationConstants.PROFILE_CUSTOM);
        conditionMap.put("profileType", types);

        final Page<NotificationProfile> profilePage = this.selectPropertyByPage(conditionMap, anPageNum, anPageSize, anFlag == 1);

        for (final NotificationProfile profile: profilePage) {
            final NotificationProfile tempProfile = findProfileByProfileNameAndCustNo(profile.getProfileName(), anCustNo);
            profile.setBusinStatus(tempProfile.getBusinStatus());
        }
        return profilePage;
    }

    /**
     * 设置消息模板状态
     * @param anCustNo
     */
    public NotificationProfile saveSetNotificationProfileStatus(final Long anProfileId, final Long anCustNo, final String anBusinStatus) {
        BTAssert.notNull(anProfileId, "模板编号不允许为空!");
        BTAssert.notNull(anBusinStatus, "状态不允许为空!");

        NotificationProfile tempProfile = this.selectByPrimaryKey(anProfileId);
        BTAssert.notNull(tempProfile, "没有找到对应的消息模板");

        if (checkDefaultProfile(tempProfile) == true) {  // default
            tempProfile = findProfileByProfileNameAndCustNo(tempProfile.getProfileName(), anCustNo);
            if (checkDefaultProfile(tempProfile) == true) { // 只有 default
                tempProfile = saveCopyBaseDataToTargetData(tempProfile, anCustNo); // 同步一个
            }
        }

        tempProfile.initModifyValue(anBusinStatus);
        this.updateByPrimaryKeySelective(tempProfile);
        return tempProfile;
    }

    /**
     * @param anTempProfile
     * @param anCustNo
     */
    public NotificationProfile saveCopyBaseDataToTargetData(final NotificationProfile anTempProfile, final Long anCustNo) {
        final NotificationProfile tempNotificationProfile = new NotificationProfile();

        final CustInfo custInfo = accountService.findCustInfo(anCustNo);
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        tempNotificationProfile.initAddValue(anTempProfile, custInfo, operator);

        this.insert(tempNotificationProfile);
        channelProfileService.saveCopyBaseDataToTargetData(anTempProfile.getId(), tempNotificationProfile.getId(), custInfo, operator);

        return tempNotificationProfile;
    }

    /**
     * @param anTempProfile
     * @return
     */
    public boolean checkDefaultProfile(final NotificationProfile anTempProfile) {
        if (BetterStringUtils.equals(anTempProfile.getCustom(), NotificationConstants.PROFILE_CUSTOM) && anTempProfile.getId() < 0) {
            return false;
        }
        return true;
    }
}
