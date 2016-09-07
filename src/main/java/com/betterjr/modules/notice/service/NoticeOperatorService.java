package com.betterjr.modules.notice.service;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.notice.dao.NoticeOperatorMapper;
import com.betterjr.modules.notice.entity.NoticeOperator;

@Service
public class NoticeOperatorService extends BaseService<NoticeOperatorMapper, NoticeOperator> {
    @Resource
    private CustAccountService accountService;

    @Resource
    private CustOperatorService custOperatorService;

    /**
     * 按条件查询 NoticeOperator
     */
    public NoticeOperator findNoticeOperatorByCondition(Long anNoticeId, Long anCustNo, Long anOperId) {
        BTAssert.notNull(anNoticeId, "公告编号不允许为空!");
        BTAssert.notNull(anOperId, "操作员编号不允许为空!");

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("noticeId", anNoticeId);
        conditionMap.put("custNo", anCustNo);
        conditionMap.put("operId", anOperId);
        return Collections3.getFirst(this.selectByProperty(conditionMap));
    }
    
    /**
     * 置已删除  从已读->已删除
     */
    public NoticeOperator saveSetDeletedNotice(Long anNoticeId, CustInfo anCustomer, CustOperatorInfo anOperator) {
        BTAssert.notNull(anNoticeId, "公告编号不允许为空!");
        BTAssert.notNull(anCustomer, "客户不允许为空!");
        BTAssert.notNull(anOperator, "操作员不允许为空!");

        NoticeOperator noticeOperator = this.findNoticeOperatorByCondition(anNoticeId, anCustomer.getCustNo(), anOperator.getId());
        
        if (noticeOperator != null) {
            // 置为已删
            noticeOperator.initModifyValue();
            noticeOperator.setIsDeleted(Boolean.TRUE);
            this.updateByPrimaryKeySelective(noticeOperator);
            
            return noticeOperator;
        } else {
            // 为此条消息，创建一个已删 记录， 将已读状态置为true;
            noticeOperator = new NoticeOperator();
            noticeOperator.initAddValue(anNoticeId, anCustomer.getCustNo(), anCustomer.getCustName(), anOperator.getId(), anOperator.getName());
            noticeOperator.setIsRead(Boolean.TRUE);
            noticeOperator.setIsDeleted(Boolean.TRUE);
            this.insert(noticeOperator);
            return noticeOperator;
        }
        
    }

    /**
     * 置已读
     */
    public NoticeOperator saveSetReadNotice(Long anNoticeId, CustInfo anCustomer, CustOperatorInfo anOperator) {
        BTAssert.notNull(anNoticeId, "公告编号不允许为空!");
        BTAssert.notNull(anCustomer, "客户不允许为空!");
        BTAssert.notNull(anOperator, "操作员不允许为空!");
        
        NoticeOperator noticeOperator = this.findNoticeOperatorByCondition(anNoticeId, anCustomer.getCustNo(), anOperator.getId());
        if (noticeOperator != null) {
            // 置为已读
            noticeOperator.initModifyValue();
            noticeOperator.setIsRead(Boolean.TRUE);
            this.updateByPrimaryKeySelective(noticeOperator);
            
            return noticeOperator;
        } else {
            // 为此条消息，创建一个已读 记录， 将已读状态置为true;
            noticeOperator = new NoticeOperator();
            noticeOperator.initAddValue(anNoticeId, anCustomer.getCustNo(), anCustomer.getCustName(), anOperator.getId(), anOperator.getName());
            noticeOperator.setIsRead(Boolean.TRUE);
            this.insert(noticeOperator);
            return noticeOperator;
        }
    }

}
