package com.betterjr.modules.notice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.notice.constant.NoticeConstants;
import com.betterjr.modules.notice.dao.NoticeCustomerMapper;
import com.betterjr.modules.notice.entity.Notice;
import com.betterjr.modules.notice.entity.NoticeCustomer;

@Service
public class NoticeCustomerService extends BaseService<NoticeCustomerMapper, NoticeCustomer> {
    @Resource
    private CustAccountService accountService;

    @Resource
    private CustOperatorService custOperatorService;

    /**
     * 按条件查询 NoticeCustomer
     */
    public NoticeCustomer findNoticeCustomerByCondition(Long anNoticeId, Long anCustNo, Long anOperId) {
        BTAssert.notNull(anNoticeId, "公告编号不允许为空!");
        BTAssert.notNull(anOperId, "操作员编号不允许为空!");

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("noticeId", anNoticeId);
        conditionMap.put("custNo", anCustNo);
        conditionMap.put("operId", anOperId);
        conditionMap.put("businStatus", NoticeConstants.NOTICE_STATUS_PUBLISHED);
        return Collections3.getFirst(this.selectByProperty(conditionMap));
    }

    /**
     * 置已删除
     */
    public NoticeCustomer saveSetDeletedNotice(Long anNoticeId, Long anCustNo, Long anOperId) {
        BTAssert.notNull(anNoticeId, "公告编号不允许为空!");
        BTAssert.notNull(anOperId, "操作员编号不允许为空!");

        NoticeCustomer noticeCustomer = this.findNoticeCustomerByCondition(anNoticeId, anCustNo, anOperId);
        noticeCustomer.initModifyValue(null, Boolean.TRUE);

        this.updateByPrimaryKeySelective(noticeCustomer);
        return noticeCustomer;
    }

    /**
     * 置已读
     */
    public NoticeCustomer saveSetReadNotice(Long anNoticeId, Long anCustNo, Long anOperId) {
        BTAssert.notNull(anNoticeId, "公告编号不允许为空!");
        BTAssert.notNull(anOperId, "操作员编号不允许为空!");

        NoticeCustomer noticeCustomer = this.findNoticeCustomerByCondition(anNoticeId, anCustNo, anOperId);
        noticeCustomer.initModifyValue(Boolean.TRUE, null);

        this.updateByPrimaryKeySelective(noticeCustomer);
        return noticeCustomer;
    }

    /**
     * 按noticeId查询 NoticeCustomer
     */
    public List<NoticeCustomer> queryNoticeCustomer(Long anNoticeId) {
        BTAssert.notNull(anNoticeId, "公告编号不允许为空!");

        return this.selectByProperty("noticeId", anNoticeId);
    }

    /**
     * 根据 NoticeId 查询 已接收的 公司 仅回传 custNo + custName
     */
    public List<NoticeCustomer> querySimpleNoticeCustomer(Long anNoticeId) {
        BTAssert.notNull(anNoticeId, "公告编号不允许为空!");

        return this.mapper.selectNoticeCustomerByNoticeId(anNoticeId);
    }

    /**
     * 公告与客户操作员关系
     */
    public void saveNoticeCustomer(Notice anNotice, String[] anTargetCusts) {
        BTAssert.notNull(anNotice, "公告不允许为空!");
        BTAssert.notNull(anTargetCusts, "客户列表不允许为空!");

        Long noticeId = anNotice.getId();

        List<NoticeCustomer> noticeCustomers = queryNoticeCustomer(noticeId);
        List<Triple<Long, Long, Long>> saveNoticeCustomers = new ArrayList<>();

        for (String targetCust : anTargetCusts) {
            saveNoticeCustomer(noticeId, Long.valueOf(targetCust), saveNoticeCustomers);
        }

        noticeCustomers.stream().filter(noticeCustomer -> checkExistInSaveNoticeCustomer(noticeCustomer, saveNoticeCustomers) == false)
                .forEach(noticeCustomer -> this.deleteByPrimaryKey(noticeCustomer.getId()));
    }

    /**
     * 检查是否存在于发布列表
     */
    private boolean checkExistInSaveNoticeCustomer(NoticeCustomer anNoticeCustomer, List<Triple<Long, Long, Long>> anSaveNoticeCustomers) {
        Long noticeId = anNoticeCustomer.getNoticeId();
        Long custNo = anNoticeCustomer.getCustNo();
        Long operId = anNoticeCustomer.getOperId();

        for (Triple<Long, Long, Long> saveNoticeCustomer : anSaveNoticeCustomers) {
            Long saveNoticeId = saveNoticeCustomer.getLeft();
            Long saveCustNo = saveNoticeCustomer.getMiddle();
            Long saveOperId = saveNoticeCustomer.getRight();

            if (noticeId.equals(saveNoticeId) && custNo.equals(saveCustNo) && operId.equals(saveOperId)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 添加 给该公司下面的所有操作员均发布
     */
    private void saveNoticeCustomer(Long anNoticeId, Long anCustNo, List<Triple<Long, Long, Long>> anSaveNoticeCustomers) {
        final CustInfo custInfo = accountService.findCustInfo(anCustNo);
        final List<CustOperatorInfo> operatorInfos = custOperatorService.queryOperatorInfoByCustNo(anCustNo);
        operatorInfos.forEach(operator -> {
            final NoticeCustomer tempNoticeCustomer = this.findNoticeCustomerByCondition(anNoticeId, anCustNo, operator.getId());
            if (tempNoticeCustomer == null) {
                final NoticeCustomer noticeCustomer = new NoticeCustomer();
                noticeCustomer.initAddValue(anNoticeId, anCustNo, custInfo.getCustName(), operator.getId(), operator.getName());
                this.insert(noticeCustomer);
            }
            else {
                tempNoticeCustomer.initModifyValue(Boolean.FALSE, Boolean.FALSE); // 置为未读
                this.updateByPrimaryKeySelective(tempNoticeCustomer);
            }
            anSaveNoticeCustomers.add(new ImmutableTriple<Long, Long, Long>(anNoticeId, anCustNo, operator.getId()));
        });
    }

}
