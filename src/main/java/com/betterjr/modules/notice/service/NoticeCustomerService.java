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
    private CustOperatorService custOperatorService;

    /**
     * 按条件查询 NoticeCustomer
     */
    public NoticeCustomer findNoticeCustomerByCondition(Long anNoticeId, Long anCustNo) {
        BTAssert.notNull(anNoticeId, "公告编号不允许为空!");
        BTAssert.notNull(anCustNo, "机构编号不允许为空!");

        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("noticeId", anNoticeId);
        conditionMap.put("custNo", anCustNo);
        return Collections3.getFirst(this.selectByProperty(conditionMap));
    }

    /**
     * 按noticeId查询 NoticeCustomer
     */
    public List<NoticeCustomer> queryNoticeCustomer(Long anNoticeId) {
        BTAssert.notNull(anNoticeId, "公告编号不允许为空!");

        return this.selectByProperty("noticeId", anNoticeId);
    }

    /**
     * 公告与客户关系
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

        noticeCustomers.stream()
                .filter(noticeCustomer -> checkExistInPublished(noticeCustomer, saveNoticeCustomers) == false)
                .forEach(noticeCustomer -> this.deleteByPrimaryKey(noticeCustomer.getId()));
    }

    /**
     * 检查是否存在于发布列表
     */
    private boolean checkExistInPublished(NoticeCustomer anNoticeCustomer,
            List<Pair<Long, Long>> anSaveNoticeCustomers) {
        Long noticeId = anNoticeCustomer.getNoticeId();
        Long custNo = anNoticeCustomer.getCustNo();

        for (Pair<Long, Long> saveNoticeCustomer : anSaveNoticeCustomers) {
            Long saveNoticeId = saveNoticeCustomer.getLeft();
            Long saveCustNo = saveNoticeCustomer.getRight();

            if (noticeId.equals(saveNoticeId) && custNo.equals(saveCustNo)) {
                return true;
            }
        }

        return false;
    }

    /**
     * 添加 给该公司发布
     */
    private void saveNoticeCustomer(Long anNoticeId, Long anCustNo, List<Pair<Long, Long>> anSaveNoticeCustomers) {
        final CustInfo custInfo = accountService.findCustInfo(anCustNo);
        final NoticeCustomer tempNoticeCustomer = this.findNoticeCustomerByCondition(anNoticeId, anCustNo);
        if (tempNoticeCustomer == null) {
            final NoticeCustomer noticeCustomer = new NoticeCustomer();
            noticeCustomer.initAddValue(anNoticeId, anCustNo, custInfo.getCustName());
            this.insert(noticeCustomer);
        } else {
            this.updateByPrimaryKeySelective(tempNoticeCustomer);
        }
        anSaveNoticeCustomers.add(new ImmutablePair<Long, Long>(anNoticeId, anCustNo));
    }

}
