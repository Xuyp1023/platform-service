package com.betterjr.modules.wechat.job;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.reflection.ReflectionUtils;
import com.betterjr.modules.wechat.data.api.Follower;
import com.betterjr.modules.wechat.data.api.Follower2;
import com.betterjr.modules.wechat.entity.CustWeChatInfo;
import com.betterjr.modules.wechat.service.CustWeChatService;
import com.betterjr.modules.wechat.util.WechatAPIImpl;
import com.dangdang.ddframe.job.api.JobExecutionMultipleShardingContext;
import com.dangdang.ddframe.job.plugin.job.type.simple.AbstractSimpleElasticJob;

/**
 * 发送短信
 *
 * @author liuwl
 *
 */
@Service
public class WechatJob extends AbstractSimpleElasticJob {
    private final static Logger logger = LoggerFactory.getLogger(WechatJob.class);

    @Inject
    private CustWeChatService wechatService;

    private WechatAPIImpl wechatApi;

    @PostConstruct
    public synchronized void init() {
        wechatApi = WechatAPIImpl.create(wechatService.getMpAccount());
    }

    @Override
    public void process(final JobExecutionMultipleShardingContext anParamJobExecutionMultipleShardingContext) {
        logger.info("微信账号信息完善JOB start.");
        worker();
        logger.info("微信账号信息完善JOB finished");
    }

    private void worker() {
        final List<Follower2> users = new ArrayList<>();
        final Map<String, CustWeChatInfo> tmpCustWeChatMap = ReflectionUtils
                .listConvertToMap(wechatService.findPendingWeChat(), "openId");
        try {
            if (Collections3.isEmpty(tmpCustWeChatMap) == false) {
                final Collection<String> tmpSet = tmpCustWeChatMap.keySet();
                for (final String tmpOpenId : tmpSet) {
                    users.add(new Follower2(tmpOpenId, "zh_CN"));
                }
                CustWeChatInfo tmpWeChatInfo;
                for (final Follower ff : wechatApi.getFollowers(users)) {
                    tmpWeChatInfo = tmpCustWeChatMap.get(ff.getOpenid());
                    if (tmpWeChatInfo == null) {
                        continue;
                    }
                    if (ff.getSubscribe() == 0) {
                        tmpWeChatInfo.modifySubscribe("0", 0L);
                    } else {
                        tmpWeChatInfo.modifySubscribe("1", ff.getSubscribeTime());
                        tmpWeChatInfo.setCityName(ff.getCity());
                        tmpWeChatInfo.setProvinceName(ff.getProvince());
                        tmpWeChatInfo.setCountryName(ff.getCountry());
                        tmpWeChatInfo.putCustSex(ff.getSex());
                        tmpWeChatInfo.setDescription(ff.getRemark());
                        tmpWeChatInfo.setGroupId(Integer.toString(ff.getGroupid()));
                        tmpWeChatInfo.setNickName(ff.getNickname());
                    }

                    wechatService.saveWeChatInfo(tmpWeChatInfo);
                }
            }
        }
        catch (final Exception ex) {
            logger.error("维护微信账号信息出错", ex);
        }
        finally {
            users.clear();
        }
    }

}
