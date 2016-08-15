package com.betterjr.modules.notice.service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.data.SimpleDataEntity;
import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.mapper.pagehelper.PageHelper;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.notice.constant.NoticeConstants;
import com.betterjr.modules.notice.dao.NoticeMapper;
import com.betterjr.modules.notice.entity.Notice;
import com.betterjr.modules.notice.entity.NoticeCustomer;

@Service
public class NoticeService extends BaseService<NoticeMapper, Notice> {
    @Resource
    private NoticeCustomerService noticeCustomerService;

    @Resource
    private CustAccountService accountService;

    /**
     * 未读消息
     */
    public Page<Notice> queryUnreadNotice(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();

        PageHelper.startPage(anPageNum, anPageSize, anFlag == 1);
        return this.mapper.selectNoticeByCondition(operator.getId(), Boolean.FALSE);
    }

    /**
     * 已读消息
     */
    public Page<Notice> queryReadNotice(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();

        PageHelper.startPage(anPageNum, anPageSize, anFlag == 1);
        return this.mapper.selectNoticeByCondition(operator.getId(), Boolean.TRUE);
    }

    /**
     * 统计未读消息
     */
    public Long findCountUnreadNotice() {
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        return this.mapper.selectCountUnreadNotice(operator.getId());
    }

    /**
     * 公告详情
     */
    public Notice findNotice(Long anId) {
        BTAssert.notNull(anId, "公告编号不允许为空!");

        final CustOperatorInfo operator = UserUtils.getOperatorInfo();

        checkNoticeCustomer(anId, operator.getId());

        // 校验是否有权限读
        Notice notice = this.selectByPrimaryKey(anId);
        
        Set<SimpleDataEntity> targetCust = queryTargetCust(notice);
        
        notice.setTargetCust(targetCust);
        return notice;
    }

    private Set<SimpleDataEntity> queryTargetCust(Notice anNotice) {
        List<NoticeCustomer> noticeCustomers = noticeCustomerService.queryNoticeCustomer(anNotice.getId());
        
        
        return null;
    }

    /**
     * 查询本机构发布公告列表
     */
    public Page<Notice> queryNotice(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anParam, "参数不允许为空!");
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        Object subject = anParam.get("LIKEsubject");
        if (subject == null || BetterStringUtils.isBlank((String) subject)) {
            anParam.remove("LIKEsubject");
        }
        else {
            anParam.put("LIKEsubject", "%" + subject + "%");
        }

        anParam.put("businStatus", new String[] { NoticeConstants.NOTICE_STATUS_CANCELED, NoticeConstants.NOTICE_STATUS_PUBLISHED,
                NoticeConstants.NOTICE_STATUS_STORED });
        anParam.put("operOrg", operator.getOperOrg());

        return this.selectPropertyByPage(anParam, anPageNum, anPageSize, anFlag == 1);
    }

    /**
     * 添加
     * 
     * @param anNoticeStatusPublished
     */
    public Notice addNotice(Notice anNotice, String anTargetCust, String anFileList, String anBusinStatus) {
        BTAssert.notNull(anNotice, "公告内容不允许为空!");
        BTAssert.notNull(anTargetCust, "公告客户不允许为空!");

        final Long custNo = anNotice.getCustNo();
        BTAssert.notNull(custNo, "客户编号不允许为空!");

        String custName = accountService.queryCustName(custNo);
        BTAssert.notNull(custName, "客户名称不允许为空!");

        final String[] targetCusts = BetterStringUtils.split(anTargetCust, ",");
        BTAssert.notEmpty(targetCusts, "公告客户列表不允许为空!");

        // 初始化
        anNotice.initAddValue(custName, anBusinStatus);

        anNotice.setPublishDate(BetterDateUtils.getNumDate());
        anNotice.setPublishTime(BetterDateUtils.getNumTime());

        this.insert(anNotice);

        noticeCustomerService.saveNoticeCustomer(anNotice, targetCusts);
        return anNotice;
    }

    /**
     * 修改-已发布,不允许修改
     * 
     * @param anBusinStatus
     */
    public Notice saveNotice(Notice anNotice, Long anId, String anTargetCust, String anFileList, String anBusinStatus) {
        BTAssert.notNull(anNotice, "公告内容不允许为空!");

        final String[] targetCusts = BetterStringUtils.split(anTargetCust, ",");
        BTAssert.notEmpty(targetCusts, "公告客户列表不允许为空!");

        Notice tempNotice = checkNoticeStatus(anId, NoticeConstants.NOTICE_STATUS_STORED);

        String tempBusinStatus = tempNotice.getBusinStatus();
        if (BetterStringUtils.equals(tempBusinStatus, NoticeConstants.NOTICE_STATUS_PUBLISHED) == true) {
            throw new BytterTradeException("本条公告已经发布,不允许修改!");
        }

        tempNotice.initModifyValue(anNotice, anBusinStatus);

        this.updateByPrimaryKeySelective(tempNotice);

        // 更新客户关系
        noticeCustomerService.saveNoticeCustomer(tempNotice, targetCusts);
        return tempNotice;
    }

    /**
     * 发布 状态：0暂存，1已发布，2已撤回，3已删除
     */
    public Notice savePublishNotice(Long anId) {
        Notice tempNotice = checkNoticeStatus(anId, NoticeConstants.NOTICE_STATUS_STORED);

        // 设置发布时间
        tempNotice.setPublishDate(BetterDateUtils.getNumDate());
        tempNotice.setPublishTime(BetterDateUtils.getNumTime());

        tempNotice.initModifyValue(NoticeConstants.NOTICE_STATUS_PUBLISHED);

        this.updateByPrimaryKeySelective(tempNotice);
        return tempNotice;
    }

    /**
     * 撤回
     */
    public Notice saveCancelNotice(Long anId) {
        Notice tempNotice = checkNoticeStatus(anId, NoticeConstants.NOTICE_STATUS_PUBLISHED);

        tempNotice.initModifyValue(NoticeConstants.NOTICE_STATUS_CANCELED);

        this.updateByPrimaryKeySelective(tempNotice);
        return tempNotice;
    }

    /**
     * 删除
     */
    public Notice saveDeleteNotice(Long anId) {
        Notice tempNotice = checkNoticeStatus(anId, NoticeConstants.NOTICE_STATUS_CANCELED, NoticeConstants.NOTICE_STATUS_STORED);

        tempNotice.initModifyValue(NoticeConstants.NOTICE_STATUS_DELETED);

        this.updateByPrimaryKeySelective(tempNotice);
        return tempNotice;
    }

    /**
     * 公告置只删除
     */
    public NoticeCustomer saveSetDeletedNotice(Long anId) {
        BTAssert.notNull(anId, "公告编号不允许为空!");

        CustOperatorInfo operator = UserUtils.getOperatorInfo();

        // 检查此消息有没被此人接收
        checkNoticeCustomer(anId, operator.getId());

        return noticeCustomerService.saveSetDeletedNotice(anId, operator.getId());
    }

    /**
     * 公告置已读
     */
    public NoticeCustomer saveSetReadNotice(Long anId) {
        BTAssert.notNull(anId, "公告编号不允许为空!");

        CustOperatorInfo operator = UserUtils.getOperatorInfo();

        // 检查此消息有没被此人接收
        checkNoticeCustomer(anId, operator.getId());

        return noticeCustomerService.saveSetReadNotice(anId, operator.getId());
    }

    /**
     * 
     */
    private NoticeCustomer checkNoticeCustomer(Long anId, Long anOperId) {
        final NoticeCustomer noticeCustomer = noticeCustomerService.findNoticeCustomerByNoticeIdAndOperId(anId, anOperId);
        BTAssert.notNull(noticeCustomer, "没有找到相应的公告接收记录!");
        return noticeCustomer;
    }

    private Notice checkNoticeStatus(Long anId, String... anBusinStatus) {
        BTAssert.notNull(anId, "公告编号不允许为空!");
        Notice tempNotice = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempNotice, "没有找到公告!");

        List<String> businStatus = Arrays.asList(anBusinStatus);

        if (businStatus.contains(tempNotice.getBusinStatus()) == false) {
            throw new BytterTradeException(20099, "公告状态不正确!");
        }
        return tempNotice;
    }

}
