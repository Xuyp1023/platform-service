package com.betterjr.modules.notice.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.notice.dao.NoticeCustomerMapper;
import com.betterjr.modules.notice.entity.Notice;
import com.betterjr.modules.notice.entity.NoticeCustomer;

@Service
public class NoticeCustomerService extends BaseService<NoticeCustomerMapper, NoticeCustomer> {
    @Resource
    private CustAccountService accountService;

    @Resource
    private CustOperatorService operatorService;

    /**
     *  
     */
    public NoticeCustomer findNoticeCustomerByNoticeIdAndOperId(Long anNoticeId, Long anOperId) {
        BTAssert.notNull(anNoticeId, "公告编号不允许为空!");
        BTAssert.notNull(anOperId, "操作员编号不允许为空!");

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("noticeId", anNoticeId);
        conditionMap.put("operId", anOperId);
        return Collections3.getFirst(this.selectByProperty(conditionMap));
    }
    
    /**
     * 置已删除
     */
    public NoticeCustomer saveSetDeletedNotice(Long anNoticeId, Long anOperId) {
        BTAssert.notNull(anNoticeId, "公告编号不允许为空!");
        BTAssert.notNull(anOperId, "操作员编号不允许为空!");

        NoticeCustomer noticeCustomer = this.findNoticeCustomerByNoticeIdAndOperId(anNoticeId, anOperId);
        noticeCustomer.initModifyValue(null, Boolean.TRUE);

        this.updateByPrimaryKeySelective(noticeCustomer);
        return noticeCustomer;
    }

    /**
     * 置已读
     */
    public NoticeCustomer saveSetReadNotice(Long anNoticeId, Long anOperId) {
        BTAssert.notNull(anNoticeId, "公告编号不允许为空!");
        BTAssert.notNull(anOperId, "操作员编号不允许为空!");

        NoticeCustomer noticeCustomer = this.findNoticeCustomerByNoticeIdAndOperId(anNoticeId, anOperId);
        noticeCustomer.initModifyValue(Boolean.TRUE, null);

        this.updateByPrimaryKeySelective(noticeCustomer);
        return noticeCustomer;
    }

    public List<NoticeCustomer> queryNoticeCustomer(Long anNoticeId) {
        BTAssert.notNull(anNoticeId, "公告编号不允许为空!");

        return this.selectByProperty("noticeId", anNoticeId);
    }

    /**
     * 发布新公告添加公告与客户操作员关系
     */
    public void saveNoticeCustomer(Notice anNotice, String[] anTargetCusts) {
        BTAssert.notNull(anNotice, "公告不允许为空!");
        BTAssert.notNull(anTargetCusts, "客户列表不允许为空!");

        Long noticeId = anNotice.getId();

        List<NoticeCustomer> noticeCustomers = queryNoticeCustomer(noticeId);
        List<Pair<Long, Long>> saveNoticeCustomers = new ArrayList<>();

        for (String targetCust : anTargetCusts) {
            saveNoticeCustomer(noticeId, Long.valueOf(targetCust), saveNoticeCustomers);
        }

        noticeCustomers.forEach(noticeCustomer -> {
            if (checkExistInSaveNoticeCustomer(noticeCustomer, saveNoticeCustomers) == false) {
                // TODO 没有保存的 需要删除
                this.deleteByPrimaryKey(noticeCustomer.getId());
            }
        });
    }

    private boolean checkExistInSaveNoticeCustomer(NoticeCustomer anNoticeCustomer, List<Pair<Long, Long>> anSaveNoticeCustomers) {
        Long noticeId = anNoticeCustomer.getNoticeId();
        Long operId = anNoticeCustomer.getOperId();
        
        for (Pair<Long, Long> saveNoticeCustomer: anSaveNoticeCustomers) {
            Long saveNoticeId = saveNoticeCustomer.getLeft();
            Long saveOperId = saveNoticeCustomer.getRight();
            
            if (noticeId.equals(saveNoticeId) && operId.equals(saveOperId)) {
                return true;
            }
        }
        
        return false;
    }

    /**
     * 添加 给该公司下面的所有操作员均发布
     */
    private void saveNoticeCustomer(Long anNoticeId, Long anCustNo, List<Pair<Long, Long>> anSaveNoticeCustomers) {
        final CustInfo custInfo = accountService.findCustInfo(anCustNo);
        final List<CustOperatorInfo> operatorInfos = operatorService.queryOperatorInfoByCustNo(anCustNo);
        operatorInfos.forEach(operator -> {
            final NoticeCustomer tempNoticeCustomer = this.findNoticeCustomerByNoticeIdAndOperId(anNoticeId, operator.getId());
            if (tempNoticeCustomer == null) {
                final NoticeCustomer noticeCustomer = new NoticeCustomer();
                noticeCustomer.initAddValue(anNoticeId, anCustNo, custInfo.getCustName(), operator.getId(), operator.getName());
                this.insert(noticeCustomer);
            }
            else {
                tempNoticeCustomer.initModifyValue(Boolean.FALSE, Boolean.FALSE); // 置为未读
                this.updateByPrimaryKeySelective(tempNoticeCustomer);
            }
            anSaveNoticeCustomers.add(new ImmutablePair<Long, Long>(anNoticeId, operator.getId()));
        });
    }

}
