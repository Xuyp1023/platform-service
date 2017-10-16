package com.betterjr.modules.workflow.snaker.core;

import java.util.List;

import org.snaker.engine.DBAccess;
import org.snaker.engine.access.Page;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.core.QueryService;
import org.snaker.engine.entity.WorkItem;
import org.snaker.engine.helper.AssertHelper;

import com.betterjr.modules.workflow.snaker.access.BetterMybatisAccess;

public class BetterQueryService extends QueryService {

    public List<WorkItem> getWorkItemsByLikeTaskName(Page<WorkItem> page, QueryFilter filter) {
        AssertHelper.notNull(filter);
        DBAccess dbAccess = access();
        if (dbAccess instanceof BetterMybatisAccess) {
            return ((BetterMybatisAccess) dbAccess).getWorkItemsByLikeTaskName(page, filter);
        }
        return access().getWorkItems(page, filter);
    }

    public List<WorkItem> getHistoryWorkItemsByLikeTaskName(Page<WorkItem> page, QueryFilter filter) {
        AssertHelper.notNull(filter);
        DBAccess dbAccess = access();
        if (dbAccess instanceof BetterMybatisAccess) {
            return ((BetterMybatisAccess) dbAccess).getHistoryWorkItemsByLikeTaskName(page, filter);
        }
        return access().getHistoryWorkItems(page, filter);
    }

}
