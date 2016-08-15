package com.betterjr.modules.notice.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.mapper.pagehelper.PageHelper;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.notice.dao.NoticeMapper;
import com.betterjr.modules.notice.entity.Notice;
import com.betterjr.modules.notice.entity.NoticeCustomer;

@Service
public class NoticeService extends BaseService<NoticeMapper, Notice> {
    @Resource
    private NoticeCustomerService noticeCustomerService;

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
     * 读取
     */
    public Notice findNotice(Long anId) {
        BTAssert.notNull(anId, "公告编号不允许为空!");

        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        
        checkNoticeCustomer(anId, operator.getId());

        // 校验是否有权限读
        return this.selectByPrimaryKey(anId);
    }

    /**
     * 添加
     * 
     * @param anFileList2
     */
    public Notice addNotice(Notice anNotice, String anTargetCust, String anFileList) {
        BTAssert.notNull(anNotice, "公告内容不允许为空!");
        BTAssert.notNull(anTargetCust, "公告客户不允许为空!");

        final String[] targetCusts = BetterStringUtils.split(anTargetCust, ",");
        BTAssert.notEmpty(targetCusts, "公告客户列表不允许为空!");

        // 初始化
        anNotice.initAddValue();

        this.insert(anNotice);

        noticeCustomerService.saveNoticeCustomer(anNotice, targetCusts);
        return anNotice;
    }

    /**
     * 修改-已发布,不允许修改
     */
    public Notice saveNotice(Notice anNotice, Long anId, String anTargetCust, String anFileList) {
        BTAssert.notNull(anNotice, "公告内容不允许为空!");
        BTAssert.notNull(anId, "公告编号不允许为空!");

        Notice tempNotice = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempNotice, "没有找到公告!");

        tempNotice.initModifyValue(anNotice);

        this.updateByPrimaryKeySelective(tempNotice);
        return tempNotice;
    }

    /**
     * 发布 状态：0暂存，1已发布，2已撤回，3已删除
     */
    public Notice savePublishNotice(Long anId) {
        BTAssert.notNull(anId, "公告编号不允许为空!");

        Notice tempNotice = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempNotice, "没有找到公告!");

        tempNotice.initModifyValue("1");

        this.updateByPrimaryKeySelective(tempNotice);
        return tempNotice;
    }

    /**
     * 撤回
     */
    public Notice saveCancelNotice(Long anId) {
        BTAssert.notNull(anId, "公告编号不允许为空!");

        Notice tempNotice = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempNotice, "没有找到公告!");

        tempNotice.initModifyValue("2");

        this.updateByPrimaryKeySelective(tempNotice);
        return tempNotice;
    }

    /**
     * 公告置只读 
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

}
