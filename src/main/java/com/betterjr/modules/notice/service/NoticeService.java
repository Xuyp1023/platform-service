package com.betterjr.modules.notice.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.mapper.pagehelper.PageHelper;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustCertService;
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
    public Page<Notice> queryUnreadNotice(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();

        PageHelper.startPage(anPageNum, anPageSize, anFlag == 1);
        final Page<Notice> notices = this.mapper.selectUnreadNotice(operator.getId(), anParam);
        return notices;
    }

    /**
     * 已读消息
     */
    public Page<Notice> queryReadNotice(Map<String, Object> anParam, int anFlag, int anPageNum, int anPageSize) {
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();

        PageHelper.startPage(anPageNum, anPageSize, anFlag == 1);
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
    public Notice findNotice(Long anId) {
        BTAssert.notNull(anId, "公告编号不允许为空!");

        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        
        final Notice notice = this.selectByPrimaryKey(anId);
        BTAssert.notNull(notice, "没有找到相应公告！");
        
        // 校验是否是自己拥有的公司发布
        if (checkOwnCust(operator, notice) == true) {
            return notice;
        }

        // 校验是否有权限读
        Long receiveCount = this.mapper.selectCountReceiveNotice(operator.getId(), anId);

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
    private boolean checkOwnCust(CustOperatorInfo anOperator, Notice anNotice) {
        Collection<CustInfo> custInfos = accountService.findCustInfoByOperator(anOperator.getId(), anOperator.getOperOrg());
        
        Long noticeCustNo = anNotice.getCustNo();
        for (CustInfo custInfo: custInfos) {
            if (noticeCustNo.equals(custInfo.getCustNo())) {
                return true;
            }
        }
        return false;
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
     */
    public Notice addNotice(Notice anNotice, String anTargetCust, String anFileList, String anBusinStatus) {
        BTAssert.notNull(anNotice, "公告内容不允许为空!");

        final Long custNo = anNotice.getCustNo();
        BTAssert.notNull(custNo, "客户编号不允许为空!");

        String custName = accountService.queryCustName(custNo);
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

    private String[] findTargetCusts(Long anCustNo, String anTargetCust) {
        String[] targetCusts = null;
                
        if (BetterStringUtils.isBlank(anTargetCust) == true) {
            // 取所有接口
            if (UserUtils.platformUser() == true) { // 平台面向所有客户发送公告
                List<String> custNoList = accountService.queryValidCustInfo().stream().map(custInfo -> String.valueOf(custInfo.getCustNo()))
                        .collect(Collectors.toList());
                targetCusts = custNoList.toArray(new String[custNoList.size()]);
            }
            else { // 当前机构面向所有关系客户发送公告
                List<String> custNoList = relationService.queryCustRelation(anCustNo).stream().map(data -> data.getValue())
                        .collect(Collectors.toList());
                targetCusts = custNoList.toArray(new String[custNoList.size()]);
            }
        }
        else {
            if (UserUtils.platformUser() == true) { // 平台面向指定类型客户发送公告
                String[] roles = BetterStringUtils.split(anTargetCust, DELIMITER_COMMA);
                Set<String> operOrgSet = custCertService.queryOperOrgByRoles(roles);
                List<String> custNoList = accountService.queryCustInfoByOperOrgSet(operOrgSet).stream()
                        .map(custInfo -> String.valueOf(custInfo.getCustNo())).collect(Collectors.toList());
                targetCusts = custNoList.toArray(new String[custNoList.size()]);
            }
            else { // 当前机构面向指定关系客户发送公告
                targetCusts = BetterStringUtils.split(anTargetCust, DELIMITER_COMMA);
            }
        }
        return targetCusts;
    }

    /**
     * 修改-已发布,不允许修改
     * 
     * @param anBusinStatus
     */
    public Notice saveNotice(Notice anNotice, Long anId, String anTargetCust, String anFileList, String anBusinStatus) {
        BTAssert.notNull(anNotice, "公告内容不允许为空!");

        final Long custNo = anNotice.getCustNo();
        BTAssert.notNull(custNo, "客户编号不允许为空!");

        String custName = accountService.queryCustName(custNo);
        BTAssert.notNull(custName, "客户名称不允许为空!");

        final String[] targetCusts = findTargetCusts(custNo, anTargetCust);

        BTAssert.notEmpty(targetCusts, "公告客户列表不允许为空!");

        Notice tempNotice = checkNoticeStatus(anId, NoticeConstants.NOTICE_STATUS_STORED, NoticeConstants.NOTICE_STATUS_CANCELED);

        String tempBusinStatus = tempNotice.getBusinStatus();
        if (BetterStringUtils.equals(tempBusinStatus, NoticeConstants.NOTICE_STATUS_PUBLISHED) == true) {
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
     * 公告置删除状态
     */
    public NoticeOperator saveSetDeletedNotice(Long anId, Long anCustNo) {
        BTAssert.notNull(anId, "公告编号不允许为空!");

        CustOperatorInfo operator = UserUtils.getOperatorInfo();

        // 检查此消息有没被此人接收
        checkNoticeCustomer(anId, anCustNo, operator.getId());

        CustInfo customer = accountService.findCustInfo(anCustNo);

        return noticeOperatorService.saveSetDeletedNotice(anId, customer, operator);
    }

    /**
     * 公告置已读
     */
    public NoticeOperator saveSetReadNotice(Long anId, Long anCustNo) {
        BTAssert.notNull(anId, "公告编号不允许为空!");

        CustOperatorInfo operator = UserUtils.getOperatorInfo();

        // 检查此消息有没被此人接收
        checkNoticeCustomer(anId, anCustNo, operator.getId());

        CustInfo customer = accountService.findCustInfo(anCustNo);

        return noticeOperatorService.saveSetReadNotice(anId, customer, operator);
    }

    /**
     * 
     */
    private NoticeCustomer checkNoticeCustomer(Long anId, Long anCustNo, Long anOperId) {
        final NoticeCustomer noticeCustomer = noticeCustomerService.findNoticeCustomerByCondition(anId, anCustNo);
        BTAssert.notNull(noticeCustomer, "没有找到相应的公告接收记录!");
        return noticeCustomer;
    }

    /**
     * 检查公告状态
     */
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
