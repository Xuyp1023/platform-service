package com.betterjr.modules.wechat.service;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.wechat.dao.WechatMediaMapper;
import com.betterjr.modules.wechat.data.message.ImageMsg;
import com.betterjr.modules.wechat.data.message.VideoMsg;
import com.betterjr.modules.wechat.data.message.VoiceMsg;
import com.betterjr.modules.wechat.entity.CustWeChatInfo;
import com.betterjr.modules.wechat.entity.WechatMedia;

/***
 * 推送
 *
 * @author hubl
 *
 */
@Service
public class WechatMediaService extends BaseService<WechatMediaMapper, WechatMedia> {
    @Resource
    private CustWeChatService wechatService;

    private static final Logger logger = LoggerFactory.getLogger(WechatMediaService.class);

    /**
     * @param anMsg
     */
    public boolean saveWechatMedia(final ImageMsg anMsg) {
        return saveWechatMedia(anMsg.getFromUserName(), anMsg.getMediaId(), "0");
    }

    /**
     * @param anMsg
     * @return
     */
    public boolean saveWechatMedia(final VoiceMsg anMsg) {
        return saveWechatMedia(anMsg.getFromUserName(), anMsg.getMediaId(), "2");
    }

    /**
     * @param anMsg
     * @return
     */
    public boolean saveWechatMedia(final VideoMsg anMsg) {
        return saveWechatMedia(anMsg.getFromUserName(), anMsg.getMediaId(), "1");
    }

    /**
     * @param anMsg
     */
    public boolean saveWechatMedia(final String anOpenId, final String anMediaId, final String anMediaType) {
        // 首先保存文件
        final CustFileItem fileItem = wechatService.fileUpload("WechatMedia", anMediaId);
        if (fileItem == null) {
            return Boolean.FALSE;
        }
        final WechatMedia wechatMedia = new WechatMedia();
        wechatMedia.initDefValue(wechatService.getAppId(), anOpenId, anMediaType, fileItem.getId());

        final CustWeChatInfo wechatInfo = wechatService.findWechatUserByOpenId(anOpenId);
        if (wechatInfo != null) { // 处理绑定道经
            wechatMedia.setCustNo(wechatInfo.getCustNo());
            wechatMedia.setOperId(wechatInfo.getOperId());
            wechatMedia.setOperName(wechatInfo.getOperName());
            wechatMedia.setOperOrg(wechatInfo.getOperOrg());
        }

        final int result = this.insert(wechatMedia);

        return result != 0;
    }

}
