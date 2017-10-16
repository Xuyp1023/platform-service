package com.betterjr.modules.workflow.snaker.access;

import java.util.ArrayList;
import java.util.List;

import org.snaker.engine.access.Page;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.access.mybatis.MybatisAccess;
import org.snaker.engine.entity.WorkItem;
import org.snaker.engine.helper.StringHelper;

public class BetterMybatisAccess extends MybatisAccess {

    public List<WorkItem> getWorkItemsByLikeTaskName(Page<WorkItem> page, QueryFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(
                " select distinct o.process_Id, t.order_Id, t.id as id, t.id as task_Id, p.display_Name as process_Name, p.instance_Url, o.parent_Id, o.creator, ");
        sql.append(
                " o.create_Time as order_Create_Time, o.expire_Time as order_Expire_Time, o.order_No, o.variable as order_Variable, ");
        sql.append(
                " t.display_Name as task_Name, t.task_Name as task_Key, t.task_Type, t.perform_Type, t.operator, t.action_Url, ");
        sql.append(
                " t.create_Time as task_Create_Time, t.finish_Time as task_End_Time, t.expire_Time as task_Expire_Time, t.variable as task_Variable ");
        sql.append(" from wf_task t ");
        sql.append(" left join wf_order o on t.order_id = o.id ");
        sql.append(" left join wf_task_actor ta on ta.task_id=t.id ");
        sql.append(" left join wf_process p on p.id = o.process_id ");
        sql.append(" where 1=1 ");

        /**
         * 查询条件构造sql的where条件
         */
        List<Object> paramList = new ArrayList<Object>();
        if (filter.getOperators() != null && filter.getOperators().length > 0) {
            sql.append(" and ta.actor_Id in (");
            for (String actor : filter.getOperators()) {
                sql.append("?,");
                paramList.add(actor);
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(") ");
        }

        if (StringHelper.isNotEmpty(filter.getProcessId())) {
            sql.append(" and o.process_Id = ?");
            paramList.add(filter.getProcessId());
        }
        if (StringHelper.isNotEmpty(filter.getDisplayName())) {
            sql.append(" and p.display_Name like ?");
            paramList.add("%" + filter.getDisplayName() + "%");
        }
        if (StringHelper.isNotEmpty(filter.getParentId())) {
            sql.append(" and o.parent_Id = ? ");
            paramList.add(filter.getParentId());
        }
        if (StringHelper.isNotEmpty(filter.getOrderId())) {
            sql.append(" and t.order_id = ? ");
            paramList.add(filter.getOrderId());
        }
        if (filter.getNames() != null && filter.getNames().length > 0) {
            sql.append(" and t.task_Name like ?");
            paramList.add("%" + filter.getNames()[0] + "%");
        }
        if (filter.getTaskType() != null) {
            sql.append(" and t.task_Type = ? ");
            paramList.add(filter.getTaskType());
        }
        if (filter.getPerformType() != null) {
            sql.append(" and t.perform_Type = ? ");
            paramList.add(filter.getPerformType());
        }
        if (StringHelper.isNotEmpty(filter.getCreateTimeStart())) {
            sql.append(" and t.create_Time >= ? ");
            paramList.add(filter.getCreateTimeStart());
        }
        if (StringHelper.isNotEmpty(filter.getCreateTimeEnd())) {
            sql.append(" and t.create_Time <= ? ");
            paramList.add(filter.getCreateTimeEnd());
        }
        if (!filter.isOrderBySetted()) {
            filter.setOrder(QueryFilter.DESC);
            filter.setOrderBy("t.create_Time");
        }

        return queryList(page, filter, WorkItem.class, sql.toString(), paramList.toArray());
    }

    public List<WorkItem> getHistoryWorkItemsByLikeTaskName(Page<WorkItem> page, QueryFilter filter) {
        StringBuilder sql = new StringBuilder();
        sql.append(
                " select distinct o.process_Id, t.order_Id, t.id as id, t.id as task_Id, p.display_Name as process_Name, p.instance_Url, o.parent_Id, o.creator, ");
        sql.append(
                " o.create_Time as order_Create_Time, o.expire_Time as order_Expire_Time, o.order_No, o.variable as order_Variable, ");
        sql.append(
                " t.display_Name as task_Name, t.task_Name as task_Key, t.task_Type, t.perform_Type,t.operator, t.action_Url, ");
        sql.append(
                " t.create_Time as task_Create_Time, t.finish_Time as task_End_Time, t.expire_Time as task_Expire_Time, t.variable as task_Variable ");
        sql.append(" from wf_hist_task t ");
        sql.append(" left join wf_hist_order o on t.order_id = o.id ");
        sql.append(" left join wf_hist_task_actor ta on ta.task_id=t.id ");
        sql.append(" left join wf_process p on p.id = o.process_id ");
        sql.append(" where 1=1 ");
        /**
         * 查询条件构造sql的where条件
         */
        List<Object> paramList = new ArrayList<Object>();
        if (filter.getOperators() != null && filter.getOperators().length > 0) {
            sql.append(" and ta.actor_Id in (");
            for (String actor : filter.getOperators()) {
                sql.append("?,");
                paramList.add(actor);
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(") ");
        }

        if (StringHelper.isNotEmpty(filter.getProcessId())) {
            sql.append(" and o.process_Id = ?");
            paramList.add(filter.getProcessId());
        }
        if (StringHelper.isNotEmpty(filter.getDisplayName())) {
            sql.append(" and p.display_Name like ?");
            paramList.add("%" + filter.getDisplayName() + "%");
        }
        if (StringHelper.isNotEmpty(filter.getParentId())) {
            sql.append(" and o.parent_Id = ? ");
            paramList.add(filter.getParentId());
        }
        if (StringHelper.isNotEmpty(filter.getOrderId())) {
            sql.append(" and t.order_id = ? ");
            paramList.add(filter.getOrderId());
        }
        if (filter.getNames() != null && filter.getNames().length > 0) {
            sql.append(" and t.task_Name like ?");
            paramList.add("%" + filter.getNames()[0] + "%");
        }
        if (filter.getTaskType() != null) {
            sql.append(" and t.task_Type = ? ");
            paramList.add(filter.getTaskType());
        }
        if (filter.getPerformType() != null) {
            sql.append(" and t.perform_Type = ? ");
            paramList.add(filter.getPerformType());
        }
        if (StringHelper.isNotEmpty(filter.getCreateTimeStart())) {
            sql.append(" and t.create_Time >= ? ");
            paramList.add(filter.getCreateTimeStart());
        }
        if (StringHelper.isNotEmpty(filter.getCreateTimeEnd())) {
            sql.append(" and t.create_Time <= ? ");
            paramList.add(filter.getCreateTimeEnd());
        }

        if (!filter.isOrderBySetted()) {
            filter.setOrder(QueryFilter.DESC);
            filter.setOrderBy("t.create_Time");
        }
        return queryList(page, filter, WorkItem.class, sql.toString(), paramList.toArray());
    }

}
