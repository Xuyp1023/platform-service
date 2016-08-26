package com.betterjr.modules.notification.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
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
    
    public List<ProfileSubscribeModel> queryProfileSubscribe(Long anCustNo) {
        BTAssert.notNull(anCustNo, "公司编号不允许为空!");
        List<ProfileSubscribeModel> profileSubscribes = this.mapper.selectProfileSubscribe(anCustNo);
        
        Long operId = UserUtils.getOperatorInfo().getId();
        
        profileSubscribes.forEach(profileSubscribe -> {
            queryChannelSubscribe(operId, anCustNo, profileSubscribe.getCustNo(), profileSubscribe.getId());
        });
        return profileSubscribes;
    }
    
    private List<ChannelSubscribeModel> queryChannelSubscribe(Long anOperId, Long anCustNo, Long anSourceCustNo, Long anProfileId) {
        // Long operId, Long custNo, Long sourceCustNo, Long profileId
        List<ChannelSubscribeModel> channelSubscribes = this.mapper.selectChannelSubscribe(anOperId, anCustNo, anSourceCustNo, anProfileId);
        return channelSubscribes;
    }
    
    // 确认订阅 删数据
    public void saveConfirmSubscribe(Long anCustNo, Long anChannelProfileId) {
        BTAssert.notNull(anCustNo, "公司编号不允许为空!");
        BTAssert.notNull(anChannelProfileId, "通道模板编号不允许为空!");
        
        Long operId = UserUtils.getOperatorInfo().getId();
        
        NotificationChannelProfile channelProfile = channelProfileService.selectByPrimaryKey(anChannelProfileId);
        BTAssert.notNull(channelProfile, "没有找到对应的通道模板!");
        
        Long profileId = channelProfile.getProfileId();
        NotificationProfile profile = profileService.selectByPrimaryKey(profileId);
        BTAssert.notNull(channelProfile, "没有找到对应的消息通知模板!");
        
        Long sourceCustNo = profile.getCustNo();
        
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("operId", operId);
        conditionMap.put("custNo", anCustNo);
        conditionMap.put("sourceCustNo", sourceCustNo);
        conditionMap.put("profileId", profileId);
        conditionMap.put("channelProfileId", anChannelProfileId);
        
        NotificationSubscribe notificationSubscribe = Collections3.getFirst(this.selectByProperty(conditionMap));
        if (notificationSubscribe != null) {
            this.delete(notificationSubscribe);
        }
    }
    
    // 撤销订阅 加数据
    public void saveCancelSubscribe(Long anCustNo, Long anChannelProfileId) {
        BTAssert.notNull(anCustNo, "公司编号不允许为空!");
        BTAssert.notNull(anChannelProfileId, "通道模板编号不允许为空!");
        
        CustOperatorInfo operator = UserUtils.getOperatorInfo();
        Long operId = operator.getId();
        
        CustInfo customer = accountService.findCustInfo(anCustNo);
        
        NotificationChannelProfile channelProfile = channelProfileService.selectByPrimaryKey(anChannelProfileId);
        BTAssert.notNull(channelProfile, "没有找到对应的通道模板!");
        
        Long profileId = channelProfile.getProfileId();
        String channel = channelProfile.getChannel();
        
        NotificationProfile profile = profileService.selectByPrimaryKey(profileId);
        BTAssert.notNull(channelProfile, "没有找到对应的消息通知模板!");
        
        Long sourceCustNo = profile.getCustNo();
        String sourceCustName = profile.getCustName();
        
        Map<String, Object> conditionMap = new HashMap<>();
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
}
