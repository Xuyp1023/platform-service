package com.betterjr.modules.notification.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.mapper.pagehelper.PageHelper;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.cert.entity.CustCertInfo;
import com.betterjr.modules.cert.entity.CustCertRule;
import com.betterjr.modules.cert.service.CustCertRuleService;
import com.betterjr.modules.cert.service.CustCertService;
import com.betterjr.modules.customer.service.CustRelationService;
import com.betterjr.modules.notification.dao.NotificationSubscribeMapper;
import com.betterjr.modules.notification.entity.NotificationChannelProfile;
import com.betterjr.modules.notification.entity.NotificationProfile;
import com.betterjr.modules.notification.entity.NotificationSubscribe;
import com.betterjr.modules.notification.model.ChannelSubscribeModel;
import com.betterjr.modules.notification.model.ProfileSubscribeModel;

@Service
public class NotificationSubscribeService extends BaseService<NotificationSubscribeMapper, NotificationSubscribe> {

    @Resource
    private NotificationProfileService profileService;

    @Resource
    private NotificationChannelProfileService channelProfileService;

    @Resource
    private CustAccountService accountService;

    @Resource
    private CustCertRuleService certRuleService;

    @Resource
    private CustCertService certService;

    @Resource
    private CustRelationService relationService;

    /**
     * 查询订阅列表
     * @param anCustNo
     * @param anFlag
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<ProfileSubscribeModel> queryProfileSubscribe(final Long anCustNo, final int anFlag, final int anPageNum, final int anPageSize) {
        BTAssert.notNull(anCustNo, "公司编号不允许为空!");

        final Map<String, Object> param = new HashMap<>();

        // 取关系客户
        final Set<String> custNoSet = relationService.queryCustRelation(anCustNo).stream().map(data -> data.getValue())
                .collect(Collectors.toSet());
        // 取平台
        final List<CustCertRule> certRules = certRuleService.queryCertRuleListByRule("PLATFORM_USER");
        final Set<String> operOrgSet = new HashSet<>();
        for (final CustCertRule certRule: certRules) {
            final CustCertInfo certInfo = certService.findBySerialNo(certRule.getSerialNo());
            if (certInfo != null) {
                operOrgSet.add(certInfo.getOperOrg());
            }
        }
        if (Collections3.isEmpty(operOrgSet) == false) {
            final List<CustInfo> custInfos = accountService.queryCustInfoByOperOrgSet(operOrgSet);
            custNoSet.addAll(custInfos.stream().map(custInfo->String.valueOf(custInfo.getCustNo())).collect(Collectors.toSet()));
        }

        param.put("customers", custNoSet);

        PageHelper.startPage(anPageNum, anPageSize, anFlag == 1);
        final Page<ProfileSubscribeModel> profileSubscribes = this.mapper.selectProfileSubscribe(param);

        profileSubscribes.forEach(profileSubscribe -> {
            profileSubscribe.setChannels(queryChannelSubscribe(anCustNo, profileSubscribe.getCustNo(), profileSubscribe.getProfileName()));
        });
        return profileSubscribes;
    }

    /**
     * 获取订阅通道
     * @param anOperId
     * @param anCustNo
     * @param anSourceCustNo
     * @param anProfileId
     * @return
     */
    private List<ChannelSubscribeModel> queryChannelSubscribe(final Long anCustNo, final Long anSourceCustNo, final String anProfileName) {
        // Long operId, Long custNo, Long sourceCustNo, Long profileId
        final List<ChannelSubscribeModel> channelSubscribes = this.mapper.selectChannelSubscribe(anCustNo, anSourceCustNo, anProfileName);
        return channelSubscribes;
    }

    // 确认订阅 删数据
    public void saveConfirmSubscribe(final Long anCustNo, final Long anSourceCustNo, final String anProfileName, final String anChannel) {
        BTAssert.notNull(anCustNo, "公司编号不允许为空!");
        BTAssert.notNull(anSourceCustNo, "模板所属公司编号不允许为空！");
        BTAssert.notNull(anProfileName, "模板名称不允许为空！");

        final NotificationProfile profile = profileService.findDefaultProfileByProfileNameAndCustNo(anProfileName, anSourceCustNo);
        BTAssert.notNull(profile, "没有找到消息通知模板!");

        final NotificationChannelProfile channelProfile = channelProfileService.findChannelProfileByProfileIdAndChannel(profile.getId(), anChannel);
        BTAssert.notNull(channelProfile, "没有找到对应的通道模板!");

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("custNo", anCustNo);
        conditionMap.put("sourceCustNo",anSourceCustNo);
        conditionMap.put("profileName", anProfileName);
        conditionMap.put("channel", anChannel);

        final NotificationSubscribe notificationSubscribe = Collections3.getFirst(this.selectByProperty(conditionMap));
        if (notificationSubscribe != null) {
            this.delete(notificationSubscribe);
        }
    }

    // 撤销订阅 加数据
    public void saveCancelSubscribe(final Long anCustNo, final Long anSourceCustNo, final String anProfileName, final String anChannel) {
        BTAssert.notNull(anCustNo, "公司编号不允许为空!");
        BTAssert.notNull(anSourceCustNo, "模板所属公司编号不允许为空！");
        BTAssert.notNull(anProfileName, "模板名称不允许为空！");
        final CustInfo customer = accountService.findCustInfo(anCustNo);
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();

        final NotificationProfile profile = profileService.findDefaultProfileByProfileNameAndCustNo(anProfileName, anSourceCustNo);
        BTAssert.notNull(profile, "没有找到消息通知模板!");

        final NotificationChannelProfile channelProfile = channelProfileService.findChannelProfileByProfileIdAndChannel(profile.getId(), anChannel);
        BTAssert.notNull(channelProfile, "没有找到对应的通道模板!");

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("custNo", anCustNo);
        conditionMap.put("sourceCustNo", anSourceCustNo);
        conditionMap.put("profileName", anProfileName);
        conditionMap.put("channel", anChannel);

        NotificationSubscribe notificationSubscribe = Collections3.getFirst(this.selectByProperty(conditionMap));
        if (notificationSubscribe != null) {
            notificationSubscribe.setSubscribe(Boolean.FALSE);
            this.updateByPrimaryKeySelective(notificationSubscribe);
        } else {
            notificationSubscribe = new NotificationSubscribe();
            notificationSubscribe.initAddValue(anCustNo, anSourceCustNo, anProfileName, anChannel, operator, customer);
            this.insert(notificationSubscribe);
        }
    }

}
