package com.betterjr.modules.workflow.dubbo;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.mapper.BeanMapper;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;
import com.betterjr.modules.sys.security.ShiroUser;
import com.betterjr.modules.workflow.IFlowService;
import com.betterjr.modules.workflow.data.CustFlowNodeData;
import com.betterjr.modules.workflow.data.FlowInput;
import com.betterjr.modules.workflow.data.FlowStatus;
import com.betterjr.modules.workflow.data.TaskAuditHistory;
import com.betterjr.modules.workflow.entity.CustFlowBase;
import com.betterjr.modules.workflow.entity.CustFlowMoney;
import com.betterjr.modules.workflow.entity.CustFlowNode;
import com.betterjr.modules.workflow.entity.CustFlowStep;
import com.betterjr.modules.workflow.entity.CustFlowSysNode;
import com.betterjr.modules.workflow.service.CustFlowBaseService;
import com.betterjr.modules.workflow.service.CustFlowMoneyService;
import com.betterjr.modules.workflow.service.CustFlowNodeService;
import com.betterjr.modules.workflow.service.CustFlowSysNodeService;
import com.betterjr.modules.workflow.service.FlowService;

@Service(interfaceClass = IFlowService.class)
public class FlowDubboService implements IFlowService {
    @Autowired
    private CustFlowBaseService flowBaseService;
    @Autowired
    private FlowService flowService;
    @Autowired
    private CustFlowNodeService flowNodeService;
    @Autowired
    private CustFlowSysNodeService flowSysNodeService;
    @Autowired
    private CustFlowMoneyService moneyService;

    /**
     * 保存流程配置
     */
    @Override
    public String webSaveProcess(Map base) {
        CustFlowBase flowObj = BeanMapper.map(base, CustFlowBase.class);
        flowObj.setRegDate(new Date());
        flowObj.setRegOperId(UserUtils.getUser().getId());
        flowObj.setRegOperName(UserUtils.getUserName());
        flowObj.setOperOrg(UserUtils.findOperOrg());
        this.flowBaseService.saveProcess(flowObj);
        return AjaxObject.newOk("保存流程配置成功").toJson();
    }
    
    /**
     * 读取流程配置，根据流程类型
     */
    @Override
    public String webFindProcessByType(String flowType) {
        CustFlowBase base=this.flowBaseService.findProcessByFlowType(flowType);
        return AjaxObject.newOk(base).toJson();
    }

    /**
     * 流程监控-修改流程审批人
     */
    @Override
    public String webChangeProcessAudit(String[] actorIds,String flowOrderId) {
        // TODO Auto-generated method stub
        this.flowService.changeProcessAudit(actorIds,flowOrderId);
        return AjaxObject.newOk("流程监控-修改流程审批人成功").toJson();
    }

    /**
     * 当前需要审批的任务(所有用户)
     */
    @Override
    public String webQueryWorkTask(Map<String, Object> anMap, int anFlag, int anPageNum, int anPageSize) {
        FlowStatus searchParam = (FlowStatus) RuleServiceDubboFilterInvoker.getInputObj();
        searchParam.setOperator(null);
        Page<FlowStatus> page = new Page<FlowStatus>(anPageNum, anPageSize, anFlag == 1);
        Page<FlowStatus> list = this.flowService.queryCurrentWorkTask(page, searchParam);
        return AjaxObject.newOkWithPage("查询当前所有用户需要审批的任务成功", list).toJson();
    }

    /**
     * 审批历史数据 (所有用户)
     */
    @Override
    public String webQueryHistoryWorkTask(Map<String, Object> anMap, int anFlag, int anPageNum, int anPageSize) {
        FlowStatus searchParam = (FlowStatus) RuleServiceDubboFilterInvoker.getInputObj();
        searchParam.setOperator(null);
        Page<FlowStatus> page = new Page<FlowStatus>(anPageNum, anPageSize, anFlag == 1);
        Page<FlowStatus> list = this.flowService.queryHistoryWorkTask(page, searchParam);
        return AjaxObject.newOkWithPage("查询所有用户审批历史数据成功", list).toJson();
    }
    
    /**
     * 当前需要审批的任务(当前用户)
     */
    @Override
    public String webQueryCurrentUserWorkTask(Map<String, Object> anMap, int anFlag, int anPageNum, int anPageSize) {
        FlowStatus searchParam = (FlowStatus) RuleServiceDubboFilterInvoker.getInputObj();
        searchParam.setOperator(transCurrentUser());
        Page<FlowStatus> page = new Page<FlowStatus>(anPageNum, anPageSize, anFlag == 1);
        Page<FlowStatus> list = this.flowService.queryCurrentWorkTask(page, searchParam);
        return AjaxObject.newOkWithPage("查询当前用户需要审批的任务成功", list).toJson();
    }

    /**
     * 审批历史数据 (当前用户)
     */
    @Override
    public String webQueryCurrentUserHistoryWorkTask(Map<String, Object> anMap, int anFlag, int anPageNum, int anPageSize) {
        FlowStatus searchParam = (FlowStatus) RuleServiceDubboFilterInvoker.getInputObj();
        searchParam.setOperator(transCurrentUser());
        Page<FlowStatus> page = new Page<FlowStatus>(anPageNum, anPageSize, anFlag == 1);
        Page<FlowStatus> list = this.flowService.queryHistoryWorkTask(page, searchParam);
        return AjaxObject.newOkWithPage("查询当前用户审批历史数据成功", list).toJson();
    }

    /**
     * 流程监控,查询当前用户所监控的进行时流程
     */
    @Override
    public String webQueryWorkTaskByMonitor(Map<String, Object> anMap, int anFlag, int anPageNum, int anPageSize) {
        String user = transCurrentUser();
        FlowStatus searchParam = (FlowStatus) RuleServiceDubboFilterInvoker.getInputObj();
        searchParam.setOperator(user);
        Page<FlowStatus> page = new Page<FlowStatus>(anPageNum, anPageSize, anFlag == 1);
        Page<FlowStatus> list = this.flowService.queryWorkTaskByMonitor(page, searchParam);
        return AjaxObject.newOkWithPage("查询当前用户所监控的进行时流程成功", list).toJson();
    }

    /**
     * 新增流程节点
     * 
     * @param anMap
     * @return
     */
    @Override
    public String webAddFlowNode(Map<String, Object> anMap) {
        CustFlowNode anNode = (CustFlowNode) RuleServiceDubboFilterInvoker.getInputObj();
        this.flowNodeService.addFlowNode(anNode);
        return AjaxObject.newOk("新增流程节点成功").toJson();
    }

    /**
     * 修改流程节点
     * 
     * @param anMap
     * @return
     */
    @Override
    public String webSaveFlowNode(Map<String, Object> anMap) {
        CustFlowNode anNode = (CustFlowNode) RuleServiceDubboFilterInvoker.getInputObj();
        this.flowNodeService.saveFlowNode(anNode);
        return AjaxObject.newOk("修改流程节点成功").toJson();
    }
    
    /**
     * 删除流程节点
     * 
     * @param anMap
     * @return
     */
    @Override
    public String webDeleteFlowNode(Map<String, Object> anMap) {
        CustFlowNode anNode = (CustFlowNode) RuleServiceDubboFilterInvoker.getInputObj();
        this.flowNodeService.saveFlowNode(anNode);
        return AjaxObject.newOk("删除流程节点成功").toJson();
    }

    /**
     * 当前流程已经执行的历史详情
     * 
     * @param businessId
     * @return
     */
    @Override
    public String webFindExecutedHistory(Long businessId) {
        List<TaskAuditHistory> list = this.flowService.findExecutedHistory(businessId);
        return AjaxObject.newOk(list).toJson();
    }

    /**
     * 当前流程当前节点之前的流程节点详情
     * 
     * @param businessId
     * @return
     */
    @Override
    public String webFindExecutedNodes(Long businessId) {
        List<CustFlowStep> list = this.flowService.findExecutedNodes(businessId);
        return AjaxObject.newOk(list).toJson();
    }

    /**
     * 查询金额分段
     */
    @Override
    public String webFindMoneyClass() {
        // TODO Auto-generated method stub
        List<CustFlowMoney> list=this.moneyService.findAllValiableClasses();
        return AjaxObject.newOk(list).toJson();
    }

    /**
     * 查询系统节点
     */
    @Override
    public String webFindSysNode(String flowType) {
        // TODO Auto-generated method stub
        List<CustFlowSysNode> list= this.flowSysNodeService.findFlowSysNodesByType(flowType);
        return AjaxObject.newOk(list).toJson();
    }
    
    /**
     * 根据流程类型，得到自定义流程所有节点
     * 
     * @param flowType
     * @return
     */
    @Override
    public String webFindFlowNodesByType(String flowType) {
        // TODO Auto-generated method stub
        List<CustFlowNodeData> list= this.flowNodeService.findFlowNodesByType(flowType);
        return AjaxObject.newOk(list).toJson();
    }
    
    /**
     * 显示流程图当前节点tips（操作人，抵达时间）
     */
    public String webFindTipsJson(String businessId, String taskName) {
        // TODO Auto-generated method stub
        return AjaxObject.newOk(this.flowService.findTipsJson(businessId, taskName)).toJson();
    }

    /**
     * 显示流程图
     */
    public String webFindFlowJson(String processId, String businessId) {
        // TODO Auto-generated method stub
        return AjaxObject.newOk(this.flowService.findFlowJson(processId, businessId)).toJson();
    }

    /**
     * 执行流程
     */
    @Override
    public void exec(FlowInput input) {
        // TODO Auto-generated method stub
        String op=this.transCurrentUser();
        input.setOperator(op);
        this.flowService.exec(input);
    }

    /**
     * 启动流程
     */
    @Override
    public void start(FlowInput input) {
        // TODO Auto-generated method stub
        String op=this.transCurrentUser();
        input.setOperator(op);
        this.flowService.start(input);
    }

    /**
     * 当前需要审批的任务(当前用户)
     */
    @Override
    public Page<FlowStatus> queryCurrentUserWorkTask(Page page, FlowStatus search) {
        // TODO Auto-generated method stub
        if (search == null) {
            search = new FlowStatus();
        }
        search.setOperator(transCurrentUser());
        return this.flowService.queryCurrentWorkTask(page, search);
    }
    
    /**
     * 当前需要审批的任务(当前流程,前端自定义用户)
     */
    @Override
    public Page<FlowStatus> queryCurrentWorkTask(Page page, FlowStatus search) {
        // TODO Auto-generated method stub
        if (search == null) {
            search = new FlowStatus();
        }
        return this.flowService.queryCurrentWorkTask(page, search);
    }

    /**
     * 审批历史数据 (当前用户)
     */
    @Override
    public Page<FlowStatus> queryCurrentUserHistoryWorkTask(Page page, FlowStatus search) {
        if (search == null) {
            search = new FlowStatus();
        }
        search.setOperator(transCurrentUser());
        return this.flowService.queryHistoryWorkTask(page, search);
    }

    /**
     * 根据流程类型，得到自定义流程所有节点
     * 
     * @param flowType
     * @return
     */
    @Override
    public List<CustFlowNodeData> findFlowNodesByType(String flowType) {
        // TODO Auto-generated method stub
        return this.flowNodeService.findFlowNodesByType(flowType);
    }


    /**
     * 如果当前操作人属于供应商，经销商，核心企业， 则切换查询参数为当前机构
     * @return
     */
    private String transCurrentUser(){
        ShiroUser user=UserUtils.getPrincipal();
        if(ShiroUser.coreUser(user) || ShiroUser.sellerUser(user) || ShiroUser.supplierUser(user)){
            return UserUtils.getOperatorInfo().getOperOrg();
        }
        return user.getUser().getId().toString();
    }
}
