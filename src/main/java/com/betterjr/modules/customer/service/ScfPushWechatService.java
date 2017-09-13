package com.betterjr.modules.customer.service;

import java.util.Date;
import java.util.Map;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.service.SpringContextHolder;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.notification.INotificationSendService;
import com.betterjr.modules.notification.NotificationModel;
import com.betterjr.modules.notification.NotificationModel.Builder;
import com.betterjr.modules.wechat.ICustWeChatService;
import com.betterjr.modules.wechat.dubboclient.CustWeChatDubboClientService;
/***
 * 推送微信消息服务类
 * @author hubl
 *
 */
@Service
public class ScfPushWechatService {

    private static final Logger logger = LoggerFactory.getLogger(ScfPushWechatService.class);
    @Autowired
    private CustAccountService accountService;
    
    @Reference(interfaceClass = INotificationSendService.class)
    public INotificationSendService notificationSendService;
    
    @Reference(interfaceClass = ICustWeChatService.class)
    private ICustWeChatService wechatService;
    
    @Autowired
    private CustOperatorService custOperatorService;
    
    /****
     * 发送关系验证通知信息
     * @param anMap
     * @return
     */
    public boolean pushVerifySend(Map<String,Object> anMap){
        logger.info(" ScfRelationService  pushVerifySend anMap:"+anMap);
        boolean bool=false;
        final CustInfo sendCustomer = accountService.findCustInfo(Long.parseLong(anMap.get("coreCustNo").toString()));
        final CustOperatorInfo sendOperator = Collections3.getFirst(custOperatorService.queryOperatorInfoByCustNo(Long.parseLong(anMap.get("coreCustNo").toString())));
        final CustInfo targetCustomer = accountService.findCustInfo(Long.parseLong(anMap.get("custNo").toString()));
        final CustOperatorInfo targetOperator = Collections3.getFirst(custOperatorService.queryOperatorInfoByCustNo(Long.parseLong(anMap.get("custNo").toString())));
        if(sendOperator!=null && targetOperator!=null){
            final Builder builder = NotificationModel.newBuilder("验证通过提醒", sendCustomer, sendOperator);
            builder.addParam("appId", wechatService.getMpAccount().getAppId());
            builder.addParam("wechatUrl", wechatService.getMpAccount().getWechatUrl());
            builder.addParam("coreCustName", anMap.get("coreCustName"));
            builder.addParam("operName",anMap.get("operName"));
            builder.addParam("dateTime",BetterDateUtils.formatDate(BetterDateUtils.parseDate(new Date()), "yyyy年MM月dd日 HH:mm"));
            builder.addReceiver(targetCustomer.getCustNo(), null);  // 接收人
            bool=notificationSendService.sendNotification(builder.build());
            logger.info("pushVerifySend 消息发送标识  bool："+bool);
        }
        return bool;
    }
    
}
