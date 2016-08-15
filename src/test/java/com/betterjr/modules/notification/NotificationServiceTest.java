package com.betterjr.modules.notification;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.betterjr.common.notification.NotificationModel;
import com.betterjr.common.notification.NotificationModel.NotificationBuilder;
import com.betterjr.common.service.NotificationService;
import com.betterjr.common.utils.DictUtils;
import com.betterjr.modules.BasicServiceTest;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.customer.entity.CustMechBase;
import com.betterjr.modules.sys.entity.DictItemInfo;

public class NotificationServiceTest extends BasicServiceTest<NotificationService> {
    
    private CustOperatorInfo createCoreUser() {
        CustOperatorInfo operatorInfo = new CustOperatorInfo();
        operatorInfo.setId(1L);
        operatorInfo.setName("测试");
        operatorInfo.setOperCode("biet;Developer.Core.Enterprise");
        return operatorInfo;
    }

    @Test
    public void testSendNotificationModel() {
        CustOperatorInfo operator = createCoreUser();
        
        List<DictItemInfo> anPlatformGroupDict = DictUtils.getDictList("PlatformGroup");
        Long platformCustNo = Long.valueOf(anPlatformGroupDict.iterator().next().getItemValue());
        
        NotificationService sendService = getCtx().getBean(NotificationService.class);

        CustMechBase baseInfo = new CustMechBase();
        baseInfo.setCustNo(100L);
        baseInfo.setEmail("vanlin@163.com");
        baseInfo.setCustName("亿起融金融服务有限责任公司");
        
        NotificationBuilder builder = new NotificationModel.NotificationBuilder("开户成功通知", platformCustNo, 1260L);
        builder.addReceiveOperator(1118L);
        builder.addReceiveOperator(11848L);
        builder.addReceiveOperator(11850L);
        builder.addReceiveOperator(12573L);
        builder.setEntity(baseInfo);
        NotificationModel notificationModel = builder.build();
        
//        for (long i = 0; i <  1000000L; i++)
        sendService.sendNotifition(notificationModel);
        
        pauseThread();
    }
    
    @Test
    public void testConsumerMessage() {
        createCoreUser();
        
        
        pauseThread();
    }
    
    private void pauseThread() {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            countDownLatch.await();
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<NotificationService> getTargetServiceClass() {
        return NotificationService.class;
    }
}
