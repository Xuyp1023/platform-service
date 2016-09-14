package com.betterjr.modules.notification.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        PageHelper.startPage(anPageNum, anPageSize, anFlag == 1);
        final Page<ProfileSubscribeModel> profileSubscribes = this.mapper.selectProfileSubscribe(anCustNo);

        final Long operId = UserUtils.getOperatorInfo().getId();

        profileSubscribes.forEach(profileSubscribe -> {
            queryChannelSubscribe(operId, anCustNo, profileSubscribe.getCustNo(), profileSubscribe.getId());
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
    private List<ChannelSubscribeModel> queryChannelSubscribe(final Long anOperId, final Long anCustNo, final Long anSourceCustNo, final Long anProfileId) {
        // Long operId, Long custNo, Long sourceCustNo, Long profileId
        final List<ChannelSubscribeModel> channelSubscribes = this.mapper.selectChannelSubscribe(anOperId, anCustNo, anSourceCustNo, anProfileId);
        return channelSubscribes;
    }

    // 确认订阅 删数据
    public void saveConfirmSubscribe(final Long anCustNo, final Long anChannelProfileId) {
        BTAssert.notNull(anCustNo, "公司编号不允许为空!");
        BTAssert.notNull(anChannelProfileId, "通道模板编号不允许为空!");

        final Long operId = UserUtils.getOperatorInfo().getId();

        final NotificationChannelProfile channelProfile = channelProfileService.selectByPrimaryKey(anChannelProfileId);
        BTAssert.notNull(channelProfile, "没有找到对应的通道模板!");

        final Long profileId = channelProfile.getProfileId();
        final NotificationProfile profile = profileService.selectByPrimaryKey(profileId);
        BTAssert.notNull(channelProfile, "没有找到对应的消息通知模板!");

        final Long sourceCustNo = profile.getCustNo();

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("operId", operId);
        conditionMap.put("custNo", anCustNo);
        conditionMap.put("sourceCustNo", sourceCustNo);
        conditionMap.put("profileId", profileId);
        conditionMap.put("channelProfileId", anChannelProfileId);

        final NotificationSubscribe notificationSubscribe = Collections3.getFirst(this.selectByProperty(conditionMap));
        if (notificationSubscribe != null) {
            this.delete(notificationSubscribe);
        }
    }

    // 撤销订阅 加数据
    public void saveCancelSubscribe(final Long anCustNo, final Long anChannelProfileId) {
        BTAssert.notNull(anCustNo, "公司编号不允许为空!");
        BTAssert.notNull(anChannelProfileId, "通道模板编号不允许为空!");

        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        final Long operId = operator.getId();

        final CustInfo customer = accountService.findCustInfo(anCustNo);

        final NotificationChannelProfile channelProfile = channelProfileService.selectByPrimaryKey(anChannelProfileId);
        BTAssert.notNull(channelProfile, "没有找到对应的通道模板!");

        final Long profileId = channelProfile.getProfileId();
        final String channel = channelProfile.getChannel();

        final NotificationProfile profile = profileService.selectByPrimaryKey(profileId);
        BTAssert.notNull(channelProfile, "没有找到对应的消息通知模板!");

        final Long sourceCustNo = profile.getCustNo();

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("operId", operId);
        conditionMap.put("custNo", anCustNo);
        conditionMap.put("sourceCustNo", sourceCustNo);
        conditionMap.put("profileId", profileId);
        conditionMap.put("channelProfileId", anChannelProfileId);

        NotificationSubscribe notificationSubscribe = Collections3.getFirst(this.selectByProperty(conditionMap));
        if (notificationSubscribe != null) {
            notificationSubscribe.setSubscribe(Boolean.FALSE);
            this.updateByPrimaryKeySelective(notificationSubscribe);
        } else {
            notificationSubscribe = new NotificationSubscribe();
            notificationSubscribe.initAddValue(profileId, anChannelProfileId, channel, sourceCustNo, operator, customer);
            this.insert(notificationSubscribe);
        }
    }

    /**
     * @param anProfileId
     * @param anChannelProfileId
     * @param anCustNo
     * @param anId
     * @return
     */
    public boolean checkSubscribe(final Long anProfileId, final Long anChannelProfileId, final Long anCustNo, final Long anOperId, final Long sourceCustNo) {
        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("operId", anOperId);
        conditionMap.put("custNo", anCustNo);
        conditionMap.put("sourceCustNo", sourceCustNo);
        conditionMap.put("profileId", anProfileId);
        conditionMap.put("channelProfileId", anChannelProfileId);


        if (Collections3.isEmpty(this.selectByProperty(conditionMap))) {
            return true;
        }
        //未订阅有记录
        return false;
    }
}
