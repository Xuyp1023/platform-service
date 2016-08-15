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
import org.snaker.engine.entity.Task;
import org.snaker.engine.entity.WorkItem;
import org.snaker.engine.spring.SpringSnakerEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.workflow.data.FlowInput;
import com.betterjr.modules.workflow.data.FlowStatus;
import com.betterjr.modules.workflow.data.TaskAuditHistory;
import com.betterjr.modules.workflow.entity.CustFlowBase;
import com.betterjr.modules.workflow.entity.CustFlowInstanceBusiness;
import com.betterjr.modules.workflow.entity.CustFlowNode;
import com.betterjr.modules.workflow.entity.CustFlowStep;
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
    
    @Autowired
    private CustFlowNodeService nodeService;
    
    @Autowired
    private CustFlowStepService stepService;

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
     * 当前流程当前节点之前的流程节点
     * 
     * @param businessId
     * @return
     */
    public List<CustFlowStep> findExecutedNodes(Long businessId) {
        CustFlowInstanceBusiness business = this.businessService.selectByPrimaryKey(businessId);
        if (business == null) {
            logger.warn("not found order for business :" + businessId);
            return Collections.emptyList();
        }
        QueryFilter filter = new QueryFilter();
        filter.setOrderId(business.getFlowOrderId());
        List<Task> taskList=this.engine.query().getActiveTasks(filter);
        Task task=Collections3.getFirst(taskList);
        if(task==null){
            logger.warn("not found current task for order id:"+business.getFlowOrderId());
            return Collections.emptyList();
        }
       String nodeId=task.getTaskName();
       


       List<Order> orderList=this.engine.query().getActiveOrders(filter);
       Order order=Collections3.getFirst(orderList);
       if(order==null){
           logger.warn("not found order for order id:"+business.getFlowOrderId());
           return Collections.emptyList();
       }
       String processId=order.getProcessId();
        
       List<CustFlowStep> stepList=this.stepService.findPrevStepsByNodeId(Long.parseLong(processId), Long.parseLong(nodeId));

       return stepList;
    }

    /**
     * 当前流程已经执行的历史详情
     * 
     * @param flowInstanceId
     * @return
     */
    public List<TaskAuditHistory> findExecutedHistory(Long businessId) {
        CustFlowInstanceBusiness business = this.businessService.selectByPrimaryKey(businessId);
        if (business == null) {
            logger.warn("not found order for business :" + businessId);
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
            flow.setFlowNodeName(task.getDisplayName());
            flow.setOperator(task.getOperator());
            flow.setReason(var.getReason());
            flow.setFlowNodeId(Long.parseLong(task.getTaskName()));
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
    public Page<FlowStatus> queryCurrentWorkTask(Page page, FlowStatus search) {
        QueryFilter filter = convertFlowStatusToQuery(search);

        org.snaker.engine.access.Page snakerPage=null;
        if(page!=null){
            snakerPage = SnakerPageUtils.toSnakerPageForQuery(page);
        }else{
            page=new Page();
        }
        List<WorkItem> list = this.engine.query().getWorkItems(snakerPage, filter);
        populatePage(page, list);
        return page;
    }

    private QueryFilter convertFlowStatusToQuery(FlowStatus search) {
        QueryFilter filter = new QueryFilter();
        if(search==null){
            return filter;
        }
        if (!BetterStringUtils.isBlank(search.getOperator())) {
            filter.setOperator(search.getOperator());
        }
        if (!BetterStringUtils.isBlank(search.getFlowName())) {
            filter.setOrderId(search.getFlowName());
        }
        if (!BetterStringUtils.isBlank(search.getFlowType())) {
            filter.setProcessType(search.getFlowType());
        }
        if (search.getBusinessId()!=null && search.getBusinessId()>0) {
            CustFlowInstanceBusiness business = this.businessService.selectByPrimaryKey(search.getBusinessId());
            if (business != null) {
               filter.setOrderId(business.getFlowOrderId());
            }
        }
        return filter;
    }


    /**
     * 审批历史数据 user=null,表示所有用户
     * 
     * @param page
     * @return
     */
    public Page<FlowStatus> queryHistoryWorkTask(Page page, FlowStatus search) {
        QueryFilter filter = convertFlowStatusToQuery(search);
        
        org.snaker.engine.access.Page snakerPage=null;
        if(page!=null){
            snakerPage = SnakerPageUtils.toSnakerPageForQuery(page);
        }else{
            page=new Page();
        }
        List<WorkItem> list = this.engine.query().getHistoryWorkItems(snakerPage, filter);
        
        this.populatePage(page, list);
        return page;
    }
    
    
    /**
     * 流程监控,user 不能为空
     */
    public Page<FlowStatus> queryWorkTaskByMonitor(Page page,FlowStatus search) {
        
        List<CustFlowBase> monitoredList=this.baseService.selectByProperty("monitorOperName", search.getOperator());
        if(Collections3.isEmpty(monitoredList)){
            return new Page();
        }
        
        QueryFilter filter =convertFlowStatusToQuery(search);
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
        this.populatePage(page, list);
        return page;
    }
    
    private void populatePage(Page<FlowStatus> page, List<WorkItem> list) {
        if (!Collections3.isEmpty(list)) {
            List orderList=Collections3.extractToList(list, "orderId");
            List<CustFlowInstanceBusiness> businessList=this.businessService.selectByListProperty("flowOrderId", orderList);
            Map businessMap=Collections3.extractToMap(businessList, "flowOrderId", "businessId");
            
            for (WorkItem it : list) {
                FlowStatus status = new FlowStatus();
                status.setCreateOperator(it.getCreator());
                status.setCreateTime(BetterDateUtils.parseDate(it.getOrderCreateTime()));
                status.setCurrentNodeName(it.getTaskName());
                status.setCurrentNodeId(Long.parseLong(it.getTaskKey()));
                status.setFlowName(it.getOrderId());
                status.setFlowType(it.getProcessName());
                status.setLastUpdateTime(BetterDateUtils.parseDate(it.getTaskEndTime()));
                status.setOperator(it.getOperator());
                status.setBusinessId((Long)businessMap.get(it.getOrderId()));
                page.add(status);
            }
        }
    }

}
