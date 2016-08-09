package com.betterjr.modules.workflow.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.entity.HistoryTask;
import org.snaker.engine.entity.Order;
import org.snaker.engine.entity.WorkItem;
import org.snaker.engine.spring.SpringSnakerEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.workflow.data.FlowInput;
import com.betterjr.modules.workflow.data.FlowStatus;
import com.betterjr.modules.workflow.data.TaskAuditHistory;
import com.betterjr.modules.workflow.entity.CustFlowBase;
import com.betterjr.modules.workflow.entity.CustFlowInstanceBusiness;
import com.betterjr.modules.workflow.utils.SnakerPageUtils;

@Service
public class FlowService {
    private static final Logger logger = LoggerFactory.getLogger(FlowService.class);

    @Autowired
    private SpringSnakerEngine engine;

    @Autowired
    private CustFlowInstanceBusinessService businessService;
    
    @Autowired
    private CustFlowBaseService baseService;

    /**
     * 启动流程
     * 
     * @param id
     * @param money
     * @return
     */
    public void start(FlowInput input) {
        Map<String, Object> formParas = input.toStartMap();
      //加载流程配置
        BetterProcessService processService=(BetterProcessService)engine.process();
        org.snaker.engine.entity.Process process = processService.getProcessByName(input.getType().name());
        Order order = engine.startInstanceById(process.getId(), input.getOperator(), formParas);
        //更新 businessId与orderId 的mapping
        CustFlowInstanceBusiness business = new CustFlowInstanceBusiness();
        business.setBusinessId(input.getBusinessId());
        business.setFlowOrderId(order.getId());
        business.setUpdateTime(BetterDateUtils.getNow());
        this.businessService.insert(business);

        logger.info("start process:" + process.getName() + ",generate Order:" + order.getId() + " for bussiness:" + input.getBusinessId());
    }

    /**
     * 执行任务
     * 
     * @param input
     */
    public void exec(FlowInput input) {
        CustFlowInstanceBusiness business = this.businessService.selectByPrimaryKey(input.getBusinessId());
        if (business == null) {
            logger.error("can not find order for business :" + input.getBusinessId());
            return;
        }
        Map<String, Object> formParas = input.toExecMap();
        QueryFilter filter = new QueryFilter().setOrderId(business.getFlowOrderId()).setOperator(input.getOperator());
        List<WorkItem> workItemList = engine.query().getWorkItems(null, filter);
        if (workItemList != null && workItemList.size() > 0) {

            for (int index = 0; index < workItemList.size(); index++) {
                String taskId = workItemList.get(index).getTaskId();
                engine.executeTask(taskId, input.getOperator(), formParas);
                logger.info("finished task:" + workItemList.get(index));
            }
        }

    }

    /**
     * 当前流程已经执行的流程节点名称
     * 
     * @param businessId
     * @return
     */
    public List<String> getExecutedNodes(Long businessId) {
        List<TaskAuditHistory> historyList = this.getExecutedHistory(businessId);
        if (Collections3.isEmpty(historyList)) {
            return Collections.emptyList();
        }
        List<String> list = new ArrayList<String>();
        for (TaskAuditHistory his : historyList) {
            list.add(his.getFlowNodeName());
        }
        return list;
    }

    /**
     * 当前流程已经执行的流程节点详情
     * 
     * @param flowInstanceId
     * @return
     */
    public List<TaskAuditHistory> getExecutedHistory(Long businessId) {
        CustFlowInstanceBusiness business = this.businessService.selectByPrimaryKey(businessId);
        if (business == null) {
            logger.error("can not find order for business :" + businessId);
            return Collections.emptyList();
        }
        QueryFilter filter = new QueryFilter();
        filter.setOrderId(business.getFlowOrderId());
        List<HistoryTask> taskHistory = this.engine.query().getHistoryTasks(filter);

        List<TaskAuditHistory> historyList = new ArrayList<TaskAuditHistory>();
        for (HistoryTask task : taskHistory) {
            TaskAuditHistory flow = new TaskAuditHistory();
            flow.setAuditDate(BetterDateUtils.parseDate(task.getFinishTime()));
            FlowInput var = FlowInput.toObject(task.getVariable());
            flow.setCommand(var.getCommand());
            flow.setFlowNodeName(task.getTaskName());
            flow.setOperator(task.getOperator());
            flow.setReason(var.getReason());
            historyList.add(flow);
        }
        return historyList;
    }

    /**
     * 当前需要审批的任务, user=null,表示所有用户
     * 
     * @param page
     * @return
     */
    public Page<FlowStatus> queryCurrentWorkTask(Page<FlowStatus> page, String user) {
        QueryFilter filter = new QueryFilter();
        if (!BetterStringUtils.isBlank(user)) {
            filter.setOperator(user);
        }

        org.snaker.engine.access.Page snakerPage=null;
        if(page!=null){
            snakerPage = SnakerPageUtils.toSnakerPageForQuery(page);
        }else{
            page=new Page();
        }
        List<WorkItem> list = this.engine.query().getWorkItems(snakerPage, filter);
        if (!Collections3.isEmpty(list)) {
            for (WorkItem it : list) {
                FlowStatus status = new FlowStatus();
                status.setCreateOperator(it.getCreator());
                status.setCreateTime(BetterDateUtils.parseDate(it.getOrderCreateTime()));
                status.setCurrentTaskName(it.getTaskKey());
                status.setFlowName(it.getOrderId());
                status.setFlowType(it.getProcessName());
                status.setLastUpdateTime(BetterDateUtils.parseDate(it.getTaskEndTime()));
                status.setOperator(it.getOperator());
                page.add(status);
            }
        }
        return page;
    }

    /**
     * 审批历史数据 user=null,表示所有用户
     * 
     * @param page
     * @return
     */
    public Page<FlowStatus> queryHistoryWorkTask(Page<FlowStatus> page, String user) {
        QueryFilter filter = new QueryFilter();
        if (!BetterStringUtils.isBlank(user)) {
            filter.setOperator(user);
        }
        
        org.snaker.engine.access.Page snakerPage=null;
        if(page!=null){
            snakerPage = SnakerPageUtils.toSnakerPageForQuery(page);
        }else{
            page=new Page();
        }
        List<WorkItem> list = this.engine.query().getHistoryWorkItems(snakerPage, filter);
        
        if (!Collections3.isEmpty(list)) {
            for (WorkItem it : list) {
                FlowStatus status = new FlowStatus();
                status.setCreateOperator(it.getCreator());
                status.setCreateTime(BetterDateUtils.parseDate(it.getOrderCreateTime()));
                status.setCurrentTaskName(it.getTaskKey());
                status.setFlowName(it.getOrderId());
                status.setFlowType(it.getProcessName());
                status.setLastUpdateTime(BetterDateUtils.parseDate(it.getTaskEndTime()));
                status.setOperator(it.getOperator());
                page.add(status);
            }
        }
        return page;
    }
    
    
    /**
     * 流程监控,user 不能为空
     */
    public Page<FlowStatus> queryWorkTaskByMonitor(Page<FlowStatus> page, String user) {
        
        List<CustFlowBase> monitoredList=this.baseService.selectByProperty("monitorOperName", user);
        if(Collections3.isEmpty(monitoredList)){
            return new Page();
        }
        
        QueryFilter filter = new QueryFilter();
        for(CustFlowBase monitoredItem:monitoredList){
            filter.setProcessId(monitoredItem.getId().toString());
        }
        
        org.snaker.engine.access.Page snakerPage=null;
        if(page!=null){
            snakerPage = SnakerPageUtils.toSnakerPageForQuery(page);
        }else{
            page=new Page();
        }
        List<WorkItem> list = this.engine.query().getWorkItems(snakerPage, filter);
        if (!Collections3.isEmpty(list)) {
            for (WorkItem it : list) {
                FlowStatus status = new FlowStatus();
                status.setCreateOperator(it.getCreator());
                status.setCreateTime(BetterDateUtils.parseDate(it.getOrderCreateTime()));
                status.setCurrentTaskName(it.getTaskKey());
                status.setFlowName(it.getOrderId());
                status.setFlowType(it.getProcessName());
                status.setLastUpdateTime(BetterDateUtils.parseDate(it.getTaskEndTime()));
                status.setOperator(it.getOperator());
                page.add(status);
            }
        }
        return page;
    }

}
