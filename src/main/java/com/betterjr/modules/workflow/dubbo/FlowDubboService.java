package com.betterjr.modules.workflow.dubbo;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;
import com.betterjr.modules.workflow.IFlowService;
import com.betterjr.modules.workflow.data.FlowInput;
import com.betterjr.modules.workflow.data.FlowStatus;
import com.betterjr.modules.workflow.data.TaskAuditHistory;
import com.betterjr.modules.workflow.entity.CustFlowBase;
import com.betterjr.modules.workflow.service.CustFlowBaseService;
import com.betterjr.modules.workflow.service.FlowService;

@Service(interfaceClass=IFlowService.class)
public class FlowDubboService implements IFlowService{
    @Autowired
    private CustFlowBaseService flowBaseService;
    @Autowired
    private FlowService flowService;
    
    /**
     * 保存流程配置
     */
    @Override
    public String webSaveProcess(Map base) {
        CustFlowBase flowObj=(CustFlowBase)RuleServiceDubboFilterInvoker.getInputObj();
        this.flowBaseService.saveProcess(flowObj);
        return AjaxObject.newOk("保存流程配置成功").toJson();
    }
    
    /**
     * 修改当前流程节点的操作人
     */
    @Override
    public String webSaveProcessAudit(String[] operators) {
        // TODO Auto-generated method stub
        this.flowBaseService.saveProcessAudit(operators);
        return AjaxObject.newOk("修改流程节点的操作人成功").toJson();
    }
    
    /**
     * 当前需要审批的任务(所有用户)
     */
    @Override
    public String webQueryWorkTask(Map<String, Object> anMap, int anFlag, int anPageNum, int anPageSize) {
        String user=null;
        FlowStatus searchParam = (FlowStatus)RuleServiceDubboFilterInvoker.getInputObj();
        Page<FlowStatus> page=new Page<FlowStatus>(anPageNum, anPageSize, anFlag==1);
        page.add(searchParam);
        Page<FlowStatus> list=this.flowService.queryCurrentWorkTask(page, user);
        return AjaxObject.newOkWithPage("查询当前所有用户需要审批的任务成功",list).toJson();
    }

    /**
     * 审批历史数据 (所有用户)
     */
    @Override
    public String webQueryHistoryWorkTask(Map<String, Object> anMap, int anFlag, int anPageNum, int anPageSize) {
        String user=null;
        FlowStatus searchParam = (FlowStatus)RuleServiceDubboFilterInvoker.getInputObj();
        Page<FlowStatus> page=new Page<FlowStatus>(anPageNum, anPageSize, anFlag==1);
        page.add(searchParam);
        Page<FlowStatus> list=this.flowService.queryHistoryWorkTask(page, user);
        return AjaxObject.newOkWithPage("查询所有用户审批历史数据成功",list).toJson();
    }
    
    /**
     * 流程监控,查询当前用户所监控的进行时流程
     */
    @Override
    public String webQueryWorkTaskByMonitor(Map<String, Object> anMap, int anFlag, int anPageNum, int anPageSize) {
        String user=UserUtils.getUserName();
        FlowStatus searchParam = (FlowStatus)RuleServiceDubboFilterInvoker.getInputObj();
        Page<FlowStatus> page=new Page<FlowStatus>(anPageNum, anPageSize, anFlag==1);
        page.add(searchParam);
        Page<FlowStatus> list=this.flowService.queryWorkTaskByMonitor(page, user);
        return AjaxObject.newOkWithPage("查询当前用户所监控的进行时流程成功",list).toJson();
    }


    /**
     * 当前流程已经执行的流程节点详情
     */
    @Override
    public List<TaskAuditHistory> getExecutedHistory(Long businessId) {
        // TODO Auto-generated method stub
        return this.flowService.getExecutedHistory(businessId);
    }

    /**
     * 当前流程已经执行的流程节点名称
     */
    @Override
    public List<String> getExecutedNodes(Long businessId) {
        // TODO Auto-generated method stub
        return this.flowService.getExecutedNodes(businessId);
    }

    /**
     * 执行流程
     */
    @Override
    public void exec(FlowInput input) {
        // TODO Auto-generated method stub
        this.flowService.exec(input);
    }

    /**
     * 启动流程
     */
    @Override
    public void start(FlowInput input) {
        // TODO Auto-generated method stub
        this.flowService.start(input);
    }

    /**
     * 当前需要审批的任务(当前用户)
     */
    @Override
    public Page<FlowStatus> queryCurrentUserWorkTask(Page<FlowStatus> page) {
        // TODO Auto-generated method stub
        return this.flowService.queryCurrentWorkTask(page, UserUtils.getUserName());
    }

    /**
     * 审批历史数据 (当前用户)
     */
    @Override
    public Page<FlowStatus> queryCurrentUserHistoryWorkTask(Page<FlowStatus> page) {
        // TODO Auto-generated method stub
        return this.flowService.queryHistoryWorkTask(page, UserUtils.getUserName());
    }



}
