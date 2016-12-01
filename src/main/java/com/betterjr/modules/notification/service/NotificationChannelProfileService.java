package com.betterjr.modules.notification.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Base64Coder;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.notification.dao.NotificationChannelProfileMapper;
import com.betterjr.modules.notification.entity.NotificationChannelProfile;
import com.betterjr.modules.notification.entity.NotificationProfile;

@Service
public class NotificationChannelProfileService extends BaseService<NotificationChannelProfileMapper, NotificationChannelProfile> {
    private static final String UTF_8 = "UTF-8";

    @Resource
    private NotificationProfileVariableService profileVariableService;

    @Resource
    private NotificationProfileService profileService;

    /**
     * 查找 channel profile
     *
     * @param anCustNo
     */
    public List<NotificationChannelProfile> queryChannelProfileByProfileId(final Long anProfileId) {
        BTAssert.notNull(anProfileId, "主模板编号不允许为空!");

        return this.selectByProperty("profileId", anProfileId);
    }

    /**
     * 查找 channel profile
     *
     * @param anCustNo
     */
    public List<NotificationChannelProfile> queryChannelProfileByProfileId(final Long anProfileId, final Long anCustNo) {
        BTAssert.notNull(anProfileId, "主模板编号不允许为空!");

        // 找到主模板
        NotificationProfile tempProfile = profileService.selectByPrimaryKey(anProfileId);
        BTAssert.notNull(tempProfile, "没有找到相应的模板!");

        // 通过模板名称再找一次
        tempProfile = profileService.findProfileByProfileNameAndCustNo(tempProfile.getProfileName(), anCustNo);
        BTAssert.notNull(tempProfile, "没有找到相应的模板!");

        return this.selectByProperty("profileId", tempProfile.getId());
    }

    /**
     * @param anId
     * @param anChannel
     * @return
     */
    public NotificationChannelProfile findChannelProfileByProfileIdAndChannel(final Long anProfileId, final String anChannel) {
        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("profileId", anProfileId);
        conditionMap.put("channel", anChannel);

        return Collections3.getFirst(this.selectByProperty(conditionMap));
    }

    /**
     * 修改通道模板
     * @param anContentText
     */
    public NotificationChannelProfile saveChannelProfile(final NotificationChannelProfile anChannelProfile, final Long anChannelProfileId,
            final Long anCustNo, final String anContentText) {
        BTAssert.notNull(anChannelProfileId, "消息通道模板编号不允许为空!");
        BTAssert.notNull(anChannelProfile, "消息通知模板内容不允许为空!");

        final NotificationChannelProfile tempChannelProfile = this.selectByPrimaryKey(anChannelProfileId);
        BTAssert.notNull(tempChannelProfile, "没有找到相应的通道模板!");

        // 检查主模板是否已经被自定义
        final Long profileId = tempChannelProfile.getProfileId(); // 拿到主模板编号
        NotificationProfile tempProfile = profileService.selectByPrimaryKey(profileId);
        BTAssert.notNull(tempProfile, "没有找到相应的模板!");

        NotificationChannelProfile channelProfile = null;
        // 如果没有，需要自定义
        if (profileService.checkDefaultProfile(tempProfile) == true) { // 找到消息模板为 default模板
            // 通过模板名称再找一次
            tempProfile = profileService.findProfileByProfileNameAndCustNo(tempProfile.getProfileName(), anCustNo);
            BTAssert.notNull(tempProfile, "没有找到相应的模板!");

            if (profileService.checkDefaultProfile(tempProfile) == true) { // 找到消息模板为 default模板
                // 需要先同步模板至当前用户
                tempProfile = saveSyncProfile(tempProfile, anCustNo); // 将消息同步为 custom 模板
                channelProfile = saveModifyChannelProfile(anChannelProfile, tempProfile, tempChannelProfile.getChannel(), anCustNo, anContentText);
            }
            else { // 找到消息模板为 custom模板
                channelProfile = saveModifyChannelProfile(anChannelProfile, tempProfile, tempChannelProfile.getChannel(), anCustNo, anContentText);
            }
        }
        else { // 找到消息模板为 custom模板
            channelProfile = saveModifyChannelProfile(anChannelProfile, tempProfile, tempChannelProfile.getChannel(), anCustNo, anContentText);
        }

        return channelProfile;
    }



    /**
     * @param anChannelProfile
     * @param anProfile
     * @param anString
     * @param anCustNo
     * @param anContentText
     * @return
     */
    private NotificationChannelProfile saveModifyChannelProfile(final NotificationChannelProfile anChannelProfile,
            final NotificationProfile anProfile, final String anChannel, final Long anCustNo, final String anContentText) {

        final NotificationChannelProfile tempChannelProfile = findChannelProfileByProfileIdAndChannel(anProfile.getId(), anChannel);
        // 修改状态
        tempChannelProfile.initModifyValue(anChannelProfile);

        try {
            // 发送通道类型:0站内消息，1电子邮   修改主题，内容
            // 2短信    修改内容
            // 3微信
            switch (tempChannelProfile.getChannel()) {
            case "0":
            case "1":
                tempChannelProfile.setSubject(anChannelProfile.getSubject());
                tempChannelProfile.setContent(resolveContent(anChannelProfile.getContent()));
                break;
            case "2":
                tempChannelProfile.setContent(anContentText != null ? anContentText : resolveContent(anChannelProfile.getContent()));
                break;
            }
        }
        catch (final UnsupportedEncodingException e) {
            throw new BytterTradeException("模板编码解析错误！");
        }

        this.updateByPrimaryKeySelective(tempChannelProfile);
        return tempChannelProfile;
    }


    /**
     * @param anTempProfile
     * @param anCustNo
     * @return
     */
    private NotificationProfile saveSyncProfile(final NotificationProfile anTempProfile, final Long anCustNo) {

        return profileService.saveCopyBaseDataToTargetData(anTempProfile, anCustNo);
    }


    /**
     * @param anSubject
     * @return
     * @throws UnsupportedEncodingException
     */
    private String resolveContent(final String anContent) throws UnsupportedEncodingException {
        final String decodeStr = Base64Coder.decodeString(anContent);
        final String originStr = URLDecoder.decode(decodeStr, UTF_8);
        return originStr;
    }

    /**
     * 将基础数据copy到新的数据上
     */
    public void saveCopyBaseDataToTargetData(final Long anBaseProfileId, final Long anTargetProfileId, final CustInfo anCustInfo,
            final CustOperatorInfo anOperator) {
        final List<NotificationChannelProfile> channelProfiles = queryChannelProfileByProfileId(anBaseProfileId);

        for (final NotificationChannelProfile channelProfile : channelProfiles) {
            final NotificationChannelProfile tempChannelProfile = new NotificationChannelProfile();
            tempChannelProfile.initAddValue(anTargetProfileId, channelProfile, anCustInfo, anOperator);
            this.insert(tempChannelProfile);

            profileVariableService.saveCopyBaseDataToTargetData(channelProfile.getId(), tempChannelProfile.getId(), anCustInfo, anOperator);
        }
    }
}
