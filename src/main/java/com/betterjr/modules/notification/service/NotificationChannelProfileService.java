package com.betterjr.modules.notification.service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Base64Coder;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.notification.dao.NotificationChannelProfileMapper;
import com.betterjr.modules.notification.entity.NotificationChannelProfile;

@Service
public class NotificationChannelProfileService extends BaseService<NotificationChannelProfileMapper, NotificationChannelProfile> {
    @Resource
    private NotificationProfileVariableService profileVariableService;
    
    /**
     * 查找 channel profile
     */
    public List<NotificationChannelProfile> queryChannelProfileByProfileId(Long anProfileId) {
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
        
        try {
            tempChannelProfile.setSubject(resolveContent(anChannelProfile.getSubject()));
            tempChannelProfile.setContent(resolveContent(anChannelProfile.getContent()));
            tempChannelProfile.setReference(resolveContent(anChannelProfile.getReference()));
        }
        catch (UnsupportedEncodingException e) {
            throw new BytterTradeException("模板编码解析错误！");
        }
        
        this.updateByPrimaryKeySelective(tempChannelProfile);
        return tempChannelProfile;
    }

    /**
     * @param anSubject
     * @return
     * @throws UnsupportedEncodingException 
     */
    private String resolveContent(String anContent) throws UnsupportedEncodingException {
        final String decodeStr = Base64Coder.decodeString(anContent);
        final String originStr = URLDecoder.decode(decodeStr, "UTF-8");
        return originStr;
    }

    /**
     * 将基础数据copy到新的数据上 
     */
    public void saveCopyBaseDataToTargetData(Long anBaseProfileId, Long anTargetProfileId, CustInfo anCustInfo, CustOperatorInfo anOperator) {
        List<NotificationChannelProfile> channelProfiles = queryChannelProfileByProfileId(anBaseProfileId);
        
        for (NotificationChannelProfile channelProfile: channelProfiles) {
            NotificationChannelProfile tempChannelProfile = new NotificationChannelProfile();
            tempChannelProfile.initAddValue(anTargetProfileId, channelProfile, anCustInfo, anOperator);
            this.insert(tempChannelProfile);
            
            profileVariableService.saveCopyBaseDataToTargetData(channelProfile.getId(), tempChannelProfile.getId(), anCustInfo, anOperator);
        };
    }
}
