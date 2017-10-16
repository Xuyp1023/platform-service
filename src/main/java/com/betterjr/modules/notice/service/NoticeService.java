package com.betterjr.modules.notice.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.betterjr.common.data.PlatformBaseRuleType;
import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.mapper.pagehelper.PageHelper;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.cert.service.CustCertService;
import com.betterjr.modules.customer.service.CustRelationService;
import com.betterjr.modules.document.service.CustFileItemService;
import com.betterjr.modules.notice.constants.NoticeConstants;
import com.betterjr.modules.notice.dao.NoticeMapper;
import com.betterjr.modules.notice.entity.Notice;
import com.betterjr.modules.notice.entity.NoticeCustomer;
import com.betterjr.modules.notice.entity.NoticeOperator;

@Service
public class NoticeService extends BaseService<NoticeMapper, Notice> {
    private static final String DELIMITER_COMMA = ",";

    @Resource
    private NoticeCustomerService noticeCustomerService;

    @Resource
    private NoticeOperatorService noticeOperatorService;

    @Resource
    private CustAccountService accountService;

    @Resource
    private CustFileItemService fileItemService;

    @Resource
    private CustRelationService relationService;

    @Resource
    private CustCertService custCertService;

    /**
     * 未读消息
     */
    public Page<Notice> queryUnreadNotice(final Map<String, Object> anParam, final int anFlag, final int anPageNum,
            final int anPageSize) {
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();

        PageHelper.startPage(anPageNum, anPageSize, anFlag == 1);

        // 处理 LIKEsubject
        final String LIKEsubject = (String) anParam.get("LIKEsubject");
        if (StringUtils.isNotBlank(LIKEsubject)) {
            anParam.put("LIKEsubject", "%" + LIKEsubject + "%");
        }
        final Page<Notice> notices = this.mapper.selectUnreadNotice(operator.getId(), anParam);
        return notices;
    }

    /**
     * 已读消息
     */
    public Page<Notice> queryReadNotice(final Map<String, Object> anParam, final int anFlag, final int anPageNum,
            final int anPageSize) {
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();

        PageHelper.startPage(anPageNum, anPageSize, anFlag == 1);
        // 处理 LIKEsubject
        final String LIKEsubject = (String) anParam.get("LIKEsubject");
        if (StringUtils.isNotBlank(LIKEsubject)) {
            anParam.put("LIKEsubject", "%" + LIKEsubject + "%");
        }
        final Page<Notice> notices = this.mapper.selectReadNotice(operator.getId(), anParam);
        return notices;
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
    public Notice findNotice(final Long anId) {
        BTAssert.notNull(anId, "公告编号不允许为空!");

        final CustOperatorInfo operator = UserUtils.getOperatorInfo();

        final Notice notice = this.selectByPrimaryKey(anId);
        BTAssert.notNull(notice, "没有找到相应公告！");

        // 校验是否是自己拥有的公司发布
        if (checkOwnCust(operator, notice) == true) {
            return notice;
        }

        // 校验是否有权限读
        final Long receiveCount = this.mapper.selectCountReceiveNotice(operator.getId(), anId);

        if (receiveCount > 0) {
            return notice;
        }

        throw new BytterTradeException("公告详情查询错误!");
    }

    /**
     * @param anOperator
     * @param anNotice
     * @return
     */
    private boolean checkOwnCust(final CustOperatorInfo anOperator, final Notice anNotice) {
        final Collection<CustInfo> custInfos = accountService.findCustInfoByOperator(anOperator.getId(),
                anOperator.getOperOrg());

        final Long noticeCustNo = anNotice.getCustNo();
        for (final CustInfo custInfo : custInfos) {
            if (noticeCustNo.equals(custInfo.getCustNo())) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查询本机构发布公告列表
     */
    public Page<Notice> queryNotice(final Map<String, Object> anParam, final int anFlag, final int anPageNum,
            final int anPageSize) {
        BTAssert.notNull(anParam, "参数不允许为空!");
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        final Object subject = anParam.get("LIKEsubject");
        if (subject == null || StringUtils.isBlank((String) subject)) {
            anParam.remove("LIKEsubject");
        } else {
            anParam.put("LIKEsubject", "%" + subject + "%");
        }

        anParam.put("businStatus", new String[] { NoticeConstants.NOTICE_STATUS_CANCELED,
                NoticeConstants.NOTICE_STATUS_PUBLISHED, NoticeConstants.NOTICE_STATUS_STORED });
        anParam.put("operOrg", operator.getOperOrg());

        return this.selectPropertyByPage(anParam, anPageNum, anPageSize, anFlag == 1);
    }

    /**
     * 添加
     */
    public Notice addNotice(final Notice anNotice, final String anTargetCust, final String anFileList,
            final String anBusinStatus) {
        BTAssert.notNull(anNotice, "公告内容不允许为空!");

        final Long custNo = anNotice.getCustNo();
        BTAssert.notNull(custNo, "客户编号不允许为空!");

        final String custName = accountService.queryCustName(custNo);
        BTAssert.notNull(custName, "客户名称不允许为空!");

        final String[] targetCusts = findTargetCusts(custNo, anTargetCust);

        BTAssert.notEmpty(targetCusts, "公告客户列表不允许为空!");

        // 初始化
        anNotice.initAddValue(custName, anBusinStatus);
        anNotice.setTargetCust(anTargetCust);
        anNotice.setPublishDate(BetterDateUtils.getNumDate());
        anNotice.setPublishTime(BetterDateUtils.getNumTime());
        anNotice.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, anNotice.getBatchNo()));

        this.insert(anNotice);
        noticeCustomerService.saveNoticeCustomer(anNotice, targetCusts);
        return anNotice;
    }

    private String[] findTargetCusts(final Long anCustNo, final String anTargetCust) {
        String[] targetCusts = null;

        if (StringUtils.isBlank(anTargetCust) == true) {
            // 取所有接口
            if (UserUtils.platformUser() == true) { // 平台面向所有客户发送公告
                final List<String> custNoList = accountService.queryValidCustInfo().stream()
                        .map(custInfo -> String.valueOf(custInfo.getCustNo())).collect(Collectors.toList());
                targetCusts = custNoList.toArray(new String[custNoList.size()]);
            } else { // 当前机构面向所有关系客户发送公告
                final PlatformBaseRuleType role = UserUtils.getPrincipal().getInnerRules().iterator().next();
                final List<String> custNoList = relationService.queryCustRelation(anCustNo, role).stream()
                        .map(data -> data.getValue()).collect(Collectors.toList());
                targetCusts = custNoList.toArray(new String[custNoList.size()]);
            }
        } else {
            if (UserUtils.platformUser() == true) { // 平台面向指定类型客户发送公告
                final String[] roles = StringUtils.split(anTargetCust, DELIMITER_COMMA);
                final Set<String> operOrgSet = custCertService.queryOperOrgByRoles(roles);
                final List<String> custNoList = accountService.queryCustInfoByOperOrgSet(operOrgSet).stream()
                        .map(custInfo -> String.valueOf(custInfo.getCustNo())).collect(Collectors.toList());
                targetCusts = custNoList.toArray(new String[custNoList.size()]);
            } else { // 当前机构面向指定关系客户发送公告
                targetCusts = StringUtils.split(anTargetCust, DELIMITER_COMMA);
            }
        }
        return targetCusts;
    }

    /**
     * 修改-已发布,不允许修改
     *
     * @param anBusinStatus
     */
    public Notice saveNotice(final Notice anNotice, final Long anId, final String anTargetCust, final String anFileList,
            final String anBusinStatus) {
        BTAssert.notNull(anNotice, "公告内容不允许为空!");

        final Long custNo = anNotice.getCustNo();
        BTAssert.notNull(custNo, "客户编号不允许为空!");

        final String custName = accountService.queryCustName(custNo);
        BTAssert.notNull(custName, "客户名称不允许为空!");

        final String[] targetCusts = findTargetCusts(custNo, anTargetCust);

        BTAssert.notEmpty(targetCusts, "公告客户列表不允许为空!");

        final Notice tempNotice = checkNoticeStatus(anId, NoticeConstants.NOTICE_STATUS_STORED,
                NoticeConstants.NOTICE_STATUS_CANCELED);

        final String tempBusinStatus = tempNotice.getBusinStatus();
        if (StringUtils.equals(tempBusinStatus, NoticeConstants.NOTICE_STATUS_PUBLISHED) == true) {
            throw new BytterTradeException("本条公告已经发布,不允许修改!");
        }

        tempNotice.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, anNotice.getBatchNo()));
        tempNotice.initModifyValue(anNotice, anBusinStatus);
        tempNotice.setTargetCust(anTargetCust);
        this.updateByPrimaryKeySelective(tempNotice);

        // 更新客户关系
        noticeCustomerService.saveNoticeCustomer(tempNotice, targetCusts);
        return tempNotice;
    }

    /**
     * 发布 状态：0暂存，1已发布，2已撤回，3已删除
     */
    public Notice savePublishNotice(final Long anId) {
        final Notice tempNotice = checkNoticeStatus(anId, NoticeConstants.NOTICE_STATUS_STORED);

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
    public Notice saveCancelNotice(final Long anId) {
        final Notice tempNotice = checkNoticeStatus(anId, NoticeConstants.NOTICE_STATUS_PUBLISHED);

        tempNotice.initModifyValue(NoticeConstants.NOTICE_STATUS_CANCELED);

        this.updateByPrimaryKeySelective(tempNotice);
        return tempNotice;
    }

    /**
     * 删除
     */
    public Notice saveDeleteNotice(final Long anId) {
        final Notice tempNotice = checkNoticeStatus(anId, NoticeConstants.NOTICE_STATUS_CANCELED,
                NoticeConstants.NOTICE_STATUS_STORED);

        tempNotice.initModifyValue(NoticeConstants.NOTICE_STATUS_DELETED);

        this.updateByPrimaryKeySelective(tempNotice);
        return tempNotice;
    }

    /**
     * 公告置删除状态
     */
    public NoticeOperator saveSetDeletedNotice(final Long anId, final Long anCustNo) {
        BTAssert.notNull(anId, "公告编号不允许为空!");

        final CustOperatorInfo operator = UserUtils.getOperatorInfo();

        // 检查此消息有没被此人接收
        checkNoticeCustomer(anId, anCustNo, operator.getId());

        final CustInfo customer = accountService.findCustInfo(anCustNo);

        return noticeOperatorService.saveSetDeletedNotice(anId, customer, operator);
    }

    /**
     * 公告置已读
     */
    public NoticeOperator saveSetReadNotice(final Long anId, final Long anCustNo) {
        BTAssert.notNull(anId, "公告编号不允许为空!");

        final CustOperatorInfo operator = UserUtils.getOperatorInfo();

        // 检查此消息有没被此人接收
        checkNoticeCustomer(anId, anCustNo, operator.getId());

        final CustInfo customer = accountService.findCustInfo(anCustNo);

        return noticeOperatorService.saveSetReadNotice(anId, customer, operator);
    }

    /**
     *
     */
    private NoticeCustomer checkNoticeCustomer(final Long anId, final Long anCustNo, final Long anOperId) {
        final NoticeCustomer noticeCustomer = noticeCustomerService.findNoticeCustomerByCondition(anId, anCustNo);
        BTAssert.notNull(noticeCustomer, "没有找到相应的公告接收记录!");
        return noticeCustomer;
    }

    /**
     * 检查公告状态
     */
    private Notice checkNoticeStatus(final Long anId, final String... anBusinStatus) {
        BTAssert.notNull(anId, "公告编号不允许为空!");
        final Notice tempNotice = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempNotice, "没有找到公告!");

        final List<String> businStatus = Arrays.asList(anBusinStatus);

        if (businStatus.contains(tempNotice.getBusinStatus()) == false) {
            throw new BytterTradeException(20099, "公告状态不正确!");
        }
        return tempNotice;
    }

}
