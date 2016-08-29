package com.betterjr.modules.workflow.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snaker.engine.SnakerEngine;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.entity.HistoryOrder;
import org.snaker.engine.entity.HistoryTask;
import org.snaker.engine.entity.Order;
import org.snaker.engine.entity.Task;
import org.snaker.engine.entity.WorkItem;
import org.snaker.engine.model.ProcessModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.workflow.data.FlowCommand;
import com.betterjr.modules.workflow.data.FlowInput;
import com.betterjr.modules.workflow.data.FlowStatus;
import com.betterjr.modules.workflow.data.TaskAuditHistory;
import com.betterjr.modules.workflow.entity.CustFlowBase;
import com.betterjr.modules.workflow.entity.CustFlowInstanceBusiness;
import com.betterjr.modules.workflow.entity.CustFlowStep;
import com.betterjr.modules.workflow.snaker.core.BetterProcessService;
import com.betterjr.modules.workflow.snaker.core.BetterQueryService;
import com.betterjr.modules.workflow.snaker.core.BetterSpringSnakerEngine;
import com.betterjr.modules.workflow.utils.SnakerHelper;
import com.betterjr.modules.workflow.utils.SnakerPageUtils;

@Service
public class FlowService {
    private static final Logger logger = LoggerFactory.getLogger(FlowService.class);

    @Autowired
    private BetterSpringSnakerEngine engine;

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
        String flowType=input.getType().name();
        String coreOperOrg=input.getCoreOperOrg();
        String financerOperOrg=input.getFinancerOperOrg();
        org.snaker.engine.entity.Process process = engine.process().getProcessByName(flowType);
        Order order = engine.startInstanceById(process.getId(), input.getOperator(), formParas,coreOperOrg,financerOperOrg);
        //更新 businessId与orderId 的mapping
        CustFlowInstanceBusiness business = new CustFlowInstanceBusiness();
        business.setBusinessId(input.getBusinessId());
        business.setFlowOrderId(order.getId());
        business.setCoreOperOrg(input.getCoreOperOrg());
        business.setFinancerOperOrg(input.getFinancerOperOrg());
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

        //执行task
        Map<String, Object> formParas = input.toExecMap();
        String orderId=business.getFlowOrderId();
        String coreOperOrg=business.getCoreOperOrg();
        String financerOperOrg=business.getFinancerOperOrg();
        
        QueryFilter filter = new QueryFilter().setOrderId(orderId).setOperator(input.getOperator());
        List<Task> workTaskList = engine.query().getActiveTasks(filter);
        //回滚&终止 只要一个人操作，其他人由系统自动跟进。
        if(!FlowCommand.GoNext.equals(input.getCommand()) && !Collections3.isEmpty(workTaskList)){
            filter=new QueryFilter().setOrderId(orderId);
            List<Task> newWorkTaskList = engine.query().getActiveTasks(filter);
            
            Map workMap=Collections3.extractToMap(workTaskList, "id");
            for(Task task:newWorkTaskList){
                if(workMap.containsKey(task.getId())){
                    task.setOperator(input.getOperator());
                }else{
                    task.setOperator(SnakerEngine.AUTO);
                }
            }
            
            workTaskList=newWorkTaskList;
        }

        
        switch(input.getCommand()){
            case GoNext:
                execTask(input, formParas, workTaskList,coreOperOrg,financerOperOrg);
                break;
            case Rollback:
                rollBackTask(input, formParas, workTaskList,coreOperOrg,financerOperOrg);
                break;
            case Exit:
                exitTask(input, formParas, workTaskList,coreOperOrg,financerOperOrg);
                break;
            default:
                break;
        }
            


    }
    
    private void exitTask(FlowInput input, Map<String, Object> formParas, List<Task> workItemList,String coreOperOrg,String financerOperOrg) {
        if (workItemList != null && workItemList.size() > 0) {
            for (int index = 0; index < workItemList.size(); index++) {
                String taskId = workItemList.get(index).getId();
                String operator=workItemList.get(index).getOperator();
                
                //如果是系统自动跟进的任务，则不驱动流程
                if(SnakerEngine.AUTO.equals(operator)){
                    engine.executeTaskOnly(taskId, operator, formParas, coreOperOrg, financerOperOrg);
                }
                logger.info("exit task:" + workItemList.get(index));
            }
            
            for (int index = 0; index < workItemList.size(); index++) {
                String taskId = workItemList.get(index).getId();
                String operator=workItemList.get(index).getOperator();
                
                //如果是系统自动跟进的任务，则不驱动流程
                if(!SnakerEngine.AUTO.equals(operator)){
                    engine.executeAndJumpTask(taskId, operator, formParas, Collections.singleton("end"),coreOperOrg,financerOperOrg);
                }
                logger.info("exit task:" + workItemList.get(index));
            }
        }
    }
    
    private void rollBackTask(FlowInput input, Map<String, Object> formParas,List<Task> workItemList,String coreOperOrg,String financerOperOrg) {
        if (workItemList != null && workItemList.size() > 0) {
            for (int index = 0; index < workItemList.size(); index++) {
                String taskId = workItemList.get(index).getId();
                String orderId= workItemList.get(index).getOrderId();
                String operator=workItemList.get(index).getOperator();

                QueryFilter filter=new QueryFilter();
                filter.setOrderId(orderId);
                List<HistoryTask> histList=engine.query().getHistoryTasks(filter);
                Set<String> rollbackSet=new HashSet<String>();
                for(HistoryTask hist:histList){
                    String histName=hist.getTaskName();
                    if(histName.startsWith(input.getRollbackNodeId()+"-")){
                        rollbackSet.add(histName);
                    }
                }
                
                //如果是系统自动跟进的任务，则不驱动流程
                if(SnakerEngine.AUTO.equals(operator)){
                    engine.executeTaskOnly(taskId, operator, formParas, coreOperOrg, financerOperOrg);
                }else{
                    engine.executeAndJumpTask(taskId, operator, formParas,rollbackSet,coreOperOrg,financerOperOrg);
                }
                logger.info("rollback task:" + workItemList.get(index).getTaskName() + " to "+rollbackSet);
            }
        }
    }

    private void execTask(FlowInput input, Map<String, Object> formParas, List<Task> workItemList,String coreOperOrg,String financerOperOrg) {
        if (workItemList != null && workItemList.size() > 0) {
            for (int index = 0; index < workItemList.size(); index++) {
                String taskId = workItemList.get(index).getId();
                engine.executeTask(taskId, input.getOperator(), formParas,coreOperOrg,financerOperOrg);
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
       String nodeId=this.convertTaskName2NodeId(task.getTaskName()).toString();
       


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
            flow.setFlowNodeId(this.convertTaskName2NodeId(task.getTaskName()));
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
        BetterQueryService query=(BetterQueryService)this.engine.query();
        List<WorkItem> list = query.getWorkItemsByLikeTaskName(snakerPage, filter);
        if(snakerPage!=null){
            page.setTotal(snakerPage.getTotalCount());
        }
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
        //流程名称
        if (!BetterStringUtils.isBlank(search.getFlowName())) {
            filter.setOrderId(search.getFlowName());
        }
        //流程类型
        if (!BetterStringUtils.isBlank(search.getFlowType())) {
            filter.setDisplayName(search.getFlowType());
        }
        //流程启动时间
        if (!BetterStringUtils.isBlank(search.getGTFlowDate())) {
            filter.setCreateTimeStart(search.getGTFlowDate());
        }
        if (!BetterStringUtils.isBlank(search.getLTFlowDate())) {
            filter.setCreateTimeStart(search.getLTFlowDate());
        }
        //审批节点
        if (search.getCurrentNodeId()!=null) {
            filter.setName(search.getCurrentNodeId().toString());
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
        BetterQueryService query=(BetterQueryService)this.engine.query();
        List<WorkItem> list = query.getHistoryWorkItemsByLikeTaskName(snakerPage, filter);
        if(snakerPage!=null){
            page.setTotal(snakerPage.getTotalCount());
        }
        this.populatePage(page, list);
        return page;
    }
    
    
    /**
     * 流程监控,user 不能为空
     */
    public Page<FlowStatus> queryWorkTaskByMonitor(Page page,FlowStatus search) {
        
        List<CustFlowBase> monitoredList=this.baseService.selectByProperty("monitorOperId", search.getOperator());
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
        BetterQueryService query=(BetterQueryService)this.engine.query();
        List<WorkItem> list = query.getWorkItemsByLikeTaskName(snakerPage, filter);
        if(snakerPage!=null){
            page.setTotal(snakerPage.getTotalCount());
        }
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
                status.setCurrentNodeId(this.convertTaskName2NodeId(it.getTaskKey()));
                status.setFlowName(it.getOrderId());
                status.setFlowType(it.getProcessName());
                status.setLastUpdateTime(BetterDateUtils.parseDate(it.getTaskEndTime()));
                status.setOperator(it.getOperator());
                status.setBusinessId((Long)businessMap.get(it.getOrderId()));
                page.add(status);
            }
        }
    }
    
    private Long convertTaskName2NodeId(String taskName){
        List<String> list=BetterStringUtils.splitTrim(taskName, "-");
        String node=Collections3.getFirst(list);
        return Long.parseLong(node);
    }
    
    
    /**
     * 改变当前节点流程审批人
     */
    public void changeProcessAudit(String[] actorIds,String flowOrderId) {
        QueryFilter filter = new QueryFilter().setOrderId(flowOrderId);
        List<WorkItem> workItemList = engine.query().getWorkItems(null, filter);
        for(WorkItem item:workItemList){
            String taskId=item.getTaskId();
            List<Task> taskList=this.engine.query().getActiveTasks(new QueryFilter().setTaskId(taskId));
            if(!Collections3.isEmpty(taskList)){
                Task task=Collections3.getFirst(taskList);
                String[] oriActorIds=task.getActorIds();
                this.engine.task().removeTaskActor(taskId, oriActorIds);
                this.engine.task().addTaskActor(taskId, actorIds);
                logger.info("deleted actorids:"+Collections3.arrayToList(oriActorIds));
                logger.info("added actorids:"+Collections3.arrayToList(actorIds));
            }
        }
        
    }
    
    /**
     * 显示流程图当前节点tips（操作人，抵达时间）
     */
    public Map<String, String> findTipsJson(String businessId, String taskName) {
        CustFlowInstanceBusiness buObj=this.businessService.selectByPrimaryKey(Long.parseLong(businessId));
        if(buObj==null){
            logger.error("can not find order for business :"+businessId);
            return Collections.EMPTY_MAP;
        }
        
        String orderId=buObj.getFlowOrderId();
        List<Task> tasks = this.engine.query().getActiveTasks(new QueryFilter().setOrderId(orderId));
        StringBuilder builder = new StringBuilder();
        String createTime = "";
        for(Task task : tasks) {
            if(task.getTaskName().equalsIgnoreCase(taskName)) {
                String[] actors = this.engine.query().getTaskActorsByTaskId(task.getId());
                for(String actor : actors) {
                    builder.append(actor).append(",");
                }
                createTime = task.getCreateTime();
            }
        }
        if(builder.length() > 0) {
            builder.deleteCharAt(builder.length() - 1);
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put("actors", builder.toString());
        data.put("createTime", createTime);
        return data;
    }
    
    /**
     * 显示流程图
     */
    public Map<String, String> findFlowJson(String businessId) {
        BetterProcessService processService=(BetterProcessService)this.engine.process();
        CustFlowInstanceBusiness buObj=this.businessService.selectByPrimaryKey(Long.parseLong(businessId));
        if(buObj==null){
            logger.error("can not find order for business :"+businessId);
            return Collections.EMPTY_MAP;
        }
        String coreOperOrg=buObj.getCoreOperOrg();
        String financerOperOrg = buObj.getFinancerOperOrg();
        String orderId=buObj.getFlowOrderId();
        
        HistoryOrder histOrder=this.engine.query().getHistOrder(orderId);
        org.snaker.engine.entity.Process process = processService.getProcessWithOrgById(histOrder.getProcessId(), coreOperOrg, financerOperOrg);
        
        ProcessModel model = process.getModel();
        Map<String, String> jsonMap = new HashMap<String, String>();
        if(model != null) {
            jsonMap.put("process", SnakerHelper.getModelJson(model));
        }

        if(BetterStringUtils.isNotEmpty(orderId)) {
            List<Task> tasks = this.engine.query().getActiveTasks(new QueryFilter().setOrderId(orderId));
            List<HistoryTask> historyTasks = this.engine.query().getHistoryTasks(new QueryFilter().setOrderId(orderId));
            jsonMap.put("state", SnakerHelper.getStateJson(model, tasks, historyTasks));
        }
        logger.info(jsonMap.get("state"));
        //{"historyRects":{"rects":[{"paths":["TO 任务1"],"name":"开始"},{"paths":["TO 分支"],"name":"任务1"},{"paths":["TO 任务3","TO 任务4","TO 任务2"],"name":"分支"}]}}
        return jsonMap;
//        return new HashMap<String, String>();
    }
}
