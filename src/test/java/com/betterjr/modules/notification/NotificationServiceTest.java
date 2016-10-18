package com.betterjr.modules.notification;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.junit.Test;

import com.betterjr.common.utils.DictUtils;
import com.betterjr.modules.BasicServiceTest;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.customer.entity.CustMechBase;
import com.betterjr.modules.notification.NotificationModel.Builder;
import com.betterjr.modules.notification.service.NotificationService;
import com.betterjr.modules.sys.entity.DictItemInfo;

public class NotificationServiceTest extends BasicServiceTest<NotificationService> {

    private CustOperatorInfo createCoreUser() {
        final CustOperatorInfo operatorInfo = new CustOperatorInfo();
        operatorInfo.setId(1L);
        operatorInfo.setName("测试");
        operatorInfo.setOperCode("biet;Developer.Core.Enterprise");
        return operatorInfo;
    }

    @Test
    public void testSendNotificationModel() {
        final CustOperatorInfo operator = createCoreUser();

        final List<DictItemInfo> anPlatformGroupDict = DictUtils.getDictList("PlatformGroup");
        final Long platformCustNo = Long.valueOf(anPlatformGroupDict.iterator().next().getItemValue());
        final CustAccountService accountService = getCtx().getBean(CustAccountService.class);
        final CustOperatorService operatorService = getCtx().getBean(CustOperatorService.class);
        final NotificationService sendService = getCtx().getBean(NotificationService.class);

        final CustMechBase baseInfo = new CustMechBase();
        baseInfo.setCustNo(100L);
        baseInfo.setEmail("vanlin@163.com");
        baseInfo.setCustName("亿起融金融服务有限责任公司");

        final Builder builder = NotificationModel.newBuilder("开户成功通知", accountService.findCustInfo(platformCustNo), operatorService.findCustOperatorInfo(1260L));
        builder.addReceiver(null, 1258L);
        builder.addReceiveEmail("liuwl@bytter.com");
        builder.addReceiveMobile("13808060501");
        builder.setEntity(baseInfo);
        final NotificationModel notificationModel = builder.build();

        //        for (long i = 0; i <  1000000L; i++)
        //sendService.sendNotification(notificationModel);

        pauseThread();
    }

    @Test
    public void testConsumerMessage() {
        createCoreUser();

        pauseThread();
    }

    private void pauseThread() {
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        try {
            countDownLatch.await();
        }
        catch (final InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Class<NotificationService> getTargetServiceClass() {
        return NotificationService.class;
    }
}
