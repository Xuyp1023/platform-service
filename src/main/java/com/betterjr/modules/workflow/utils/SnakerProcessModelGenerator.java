package com.betterjr.modules.workflow.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.snaker.engine.SnakerEngine;
import org.snaker.engine.model.DecisionModel;
import org.snaker.engine.model.EndModel;
import org.snaker.engine.model.ForkModel;
import org.snaker.engine.model.JoinModel;
import org.snaker.engine.model.NodeModel;
import org.snaker.engine.model.ProcessModel;
import org.snaker.engine.model.StartModel;
import org.snaker.engine.model.TaskModel;
import org.snaker.engine.model.TransitionModel;

import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.betterjr.common.mapper.BeanMapper;
import com.betterjr.common.mapper.JsonMapper;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.workflow.data.AuditType;
import com.betterjr.modules.workflow.data.FlowNodeRole;
import com.betterjr.modules.workflow.entity.CustFlowBase;
import com.betterjr.modules.workflow.entity.CustFlowMoney;
import com.betterjr.modules.workflow.entity.CustFlowStep;
import com.betterjr.modules.workflow.entity.CustFlowStepApprovers;

public class SnakerProcessModelGenerator {

    private CustFlowBase base;
    private List<CustFlowStep> stepList;
    private Map<Long, List<CustFlowStepApprovers>> stepApproversMap;
    private Map<Long, CustFlowMoney> moneyMap;

    public SnakerProcessModelGenerator() {
        long processId = 100l;
        Long step1Id = 200l;
        Long step2Id = 300l;


        stepApproversMap = new HashMap<Long, List<CustFlowStepApprovers>>();

        Long moneyClassId = 5001l;
        Long moneyClassId2 = 5002l;
        CustFlowMoney money1 = new CustFlowMoney();
        money1.setAuditMaxAmount(new BigDecimal("10000"));
        money1.setAuditMinAmount(new BigDecimal("0"));
        CustFlowMoney money2 = new CustFlowMoney();
        money2.setAuditMaxAmount(new BigDecimal("100000"));
        money2.setAuditMinAmount(new BigDecimal("10000"));
        moneyMap = new HashMap<Long, CustFlowMoney>();
        moneyMap.put(moneyClassId, money1);
        moneyMap.put(moneyClassId2, money2);

        CustFlowStepApprovers app1 = new CustFlowStepApprovers();
        app1.setStepId(step1Id);
        app1.setAuditOperId(4001l);
        app1.setAuditOperName("hewei");
        app1.setAuditMoneyId(moneyClassId);
        app1.setWeight(50);
        CustFlowStepApprovers app2 = new CustFlowStepApprovers();
        app2.setStepId(step1Id);
        app2.setAuditOperId(4002l);
        app2.setAuditOperName("hewei");
        app2.setAuditMoneyId(moneyClassId);
        app2.setWeight(50);
        CustFlowStepApprovers app3 = new CustFlowStepApprovers();
        app3.setStepId(step1Id);
        app3.setAuditOperId(4003l);
        app3.setAuditOperName("hewei");
        app3.setAuditMoneyId(moneyClassId2);
        app3.setWeight(50);
        CustFlowStepApprovers app4 = new CustFlowStepApprovers();
        app4.setStepId(step1Id);
        app4.setAuditOperId(4004l);
        app4.setAuditOperName("hewei");
        app4.setAuditMoneyId(moneyClassId2);
        app4.setWeight(50);
        List<CustFlowStepApprovers> stepAppsList = new ArrayList<CustFlowStepApprovers>();
        stepAppsList.add(app1);
        stepAppsList.add(app2);
        stepAppsList.add(app3);
        stepAppsList.add(app4);
        stepApproversMap.put(step1Id, stepAppsList);

        CustFlowStepApprovers app1Step2 = new CustFlowStepApprovers();
        app1Step2.setStepId(step2Id);
        app1Step2.setAuditOperId(4005l);
        app1Step2.setAuditMoneyId(moneyClassId);
        app1Step2.setAuditOperName("hewei");
        app1Step2.setWeight(50);
        CustFlowStepApprovers app2Step2 = new CustFlowStepApprovers();
        app2Step2.setStepId(step2Id);
        app2Step2.setAuditOperId(4006l);
        app2Step2.setAuditOperName("dafd");
        app2Step2.setAuditMoneyId(moneyClassId);
        app2Step2.setWeight(50);
        CustFlowStepApprovers app3Step2 = new CustFlowStepApprovers();
        app3Step2.setStepId(step2Id);
        app3Step2.setAuditOperId(4007l);
        app3Step2.setAuditOperName("fdafdd");
        app3Step2.setAuditMoneyId(moneyClassId2);
        app3Step2.setWeight(50);
        CustFlowStepApprovers app4Step2 = new CustFlowStepApprovers();
        app4Step2.setStepId(step2Id);
        app4Step2.setAuditOperId(4008l);
        app4Step2.setAuditOperName("f45d454");
        app4Step2.setAuditMoneyId(moneyClassId2);
        app4Step2.setWeight(50);
        List<CustFlowStepApprovers> stepAppsListStep2 = new ArrayList<CustFlowStepApprovers>();
        stepAppsListStep2.add(app1Step2);
        stepAppsListStep2.add(app2Step2);
        stepAppsListStep2.add(app3Step2);
        stepAppsListStep2.add(app4Step2);
        stepApproversMap.put(step2Id, stepAppsListStep2);
        
        base = new CustFlowBase();
        base.setId(processId);
        base.setFlowType("Trade");
        base.setMonitorOperId(111l);
        base.setMonitorOperName("hewei");
        

        stepList = new ArrayList<CustFlowStep>();
        CustFlowStep step1 = new CustFlowStep();
        step1.setId(step1Id);
        step1.setFlowBaseId(processId);
        step1.setNodeId(11l);
        step1.setNodeName("出具保理方案");
        step1.setAuditType("serial");
        step1.setOrderNum(1);
        step1.setStepApprovers(stepApproversMap.get(step1Id));
        CustFlowStep step2 = new CustFlowStep();
        step2.setId(step2Id);
        step1.setFlowBaseId(processId);
        step2.setNodeId(12l);
        step2.setNodeName("融资方确认方案");
        step2.setAuditType("serial");
        step2.setOrderNum(2);
        step2.setStepApprovers(stepApproversMap.get(step2Id));
        stepList.add(step1);
        stepList.add(step2);
        base.setStepList(stepList);
    }

    /**
     * 参考ModelParser实现
     * 
     * @return
     */
    public ProcessModel buildProcessModel() {
        ProcessModel process = new ProcessModel();
        String processName = "test-" + base.getId().toString();
        process.setName(processName);
        process.setDisplayName(processName);
        List<NodeModel> nodeList = process.getNodes();
        // 构建流程
        // 根据orderNum，排序，升序
        Collections.sort(stepList);
        StartModel startModel = new StartModel();
        startModel.setName("start");
        nodeList.add(startModel);
        TransitionModel tranFromPrevStep = null;
        for (int index = 0; index < stepList.size(); index++) {
            if (index == 0) {
                tranFromPrevStep = new TransitionModel();
                startModel.setOutputs(Collections.singletonList(tranFromPrevStep));
                tranFromPrevStep.setSource(startModel);
            }
            CustFlowStep step = stepList.get(index);
            tranFromPrevStep = this.createOneStepProcess(step, tranFromPrevStep, nodeList);
        }
        EndModel endModel = new EndModel();
        endModel.setName("end");
        tranFromPrevStep.setTarget(endModel);
        endModel.setInputs(Collections.singletonList(tranFromPrevStep));
        nodeList.add(endModel);

        return process;
    }

    private TransitionModel createOneStepProcess(CustFlowStep step, TransitionModel tranFromPrevStep, List<NodeModel> nodeList) {
        // 生成所有的task model
        // 根据金额区间分类task model ，如果分类超过1个，则生成1个decision model，一个jion model
        // 在同类task model中，如果个数>1，生成一个fork model，一个join model
        // 最后生成一个transition model，连接各个节点

        List<CustFlowStepApprovers> stepApprovers = stepApproversMap.get(step.getId());
        if (Collections3.isEmpty(stepApprovers)) {
            if(FlowNodeRole.Factoring.equals(step.getNodeRole())){
                return tranFromPrevStep;
            }else{
                CustFlowStepApprovers newapp=new CustFlowStepApprovers();
                newapp.setAuditMoneyId(CustFlowMoney.DefaultMoney);
                newapp.setStepId(step.getId());
                newapp.setWeight(CustFlowStepApprovers.MaxWeight);
                this.stepApproversMap.put(step.getId(), Collections.singletonList(newapp));
                stepApprovers= stepApproversMap.get(step.getId());
            }
        }

        Map<Long, List<CustFlowStepApprovers>> moneyClassMap = new HashMap<Long, List<CustFlowStepApprovers>>();
        for (CustFlowStepApprovers approver : stepApprovers) {
            List<CustFlowStepApprovers> classList = moneyClassMap.get(approver.getAuditMoneyId());
            if (classList == null) {
                classList = new ArrayList<CustFlowStepApprovers>();
                moneyClassMap.put(approver.getAuditMoneyId(), classList);
            }
            classList.add(approver);
        }

        Map<Long, NodeModel> headList = new HashMap<Long, NodeModel>();
        List<TransitionModel> tailList = new ArrayList<TransitionModel>();
        for (Long moneyId : moneyClassMap.keySet()) {
            List<CustFlowStepApprovers> classList = moneyClassMap.get(moneyId);
            if (CollectionUtils.isEmpty(classList)) {
                continue;
            }
            NodeModel head = null;
            TransitionModel tail = null;
            if (classList.size() > 1) {
                ForkModel forkModel = new ForkModel();
                forkModel.setName(step.getId() + "-" + moneyId + "-fork");
                nodeList.add(forkModel);
                JoinModel join2Model = new JoinModel();
                join2Model.setName(step.getId() + "-" + moneyId + "-join");
                nodeList.add(join2Model);
                List<TransitionModel> forkOutputs = new ArrayList<TransitionModel>();
                List<TransitionModel> joinInputputs = new ArrayList<TransitionModel>();
                for (CustFlowStepApprovers app : classList) {
                    TaskModel taskModel = new TaskModel();
                    nodeList.add(taskModel);
                    populateTaskModel(step, app, taskModel);
                    // task point to join
                    TransitionModel trans = new TransitionModel();
                    trans.setTarget(join2Model);
                    trans.setSource(taskModel);
                    joinInputputs.add(trans);
                    taskModel.setOutputs(Collections.singletonList(trans));
                    // add trans of task to list
                    TransitionModel trans2 = new TransitionModel();
                    trans2.setTarget(taskModel);
                    trans2.setSource(forkModel);
                    taskModel.setInputs(Collections.singletonList(trans2));
                    forkOutputs.add(trans2);
                }
                // fork point to trans of task
                forkModel.setOutputs(forkOutputs);
                // join point to trans
                TransitionModel trans3 = new TransitionModel();
                join2Model.setOutputs(Collections.singletonList(trans3));
                join2Model.setInputs(joinInputputs);
                trans3.setSource(join2Model);
                //
                head = forkModel;
                tail = trans3;
            }
            else {
                for (CustFlowStepApprovers app : classList) {
                    TaskModel taskModel = new TaskModel();
                    nodeList.add(taskModel);
                    populateTaskModel(step, app, taskModel);
                    // task point to trans
                    TransitionModel trans = new TransitionModel();
                    taskModel.setOutputs(Collections.singletonList(trans));
                    trans.setSource(taskModel);
                    //
                    head = taskModel;
                    tail = trans;
                }
            }
            headList.put(moneyId, head);
            tailList.add(tail);
        }

        if (moneyClassMap.size() > 1) {
            DecisionModel decisionModel = new DecisionModel();
            decisionModel.setName(step.getId() + "decision");
            nodeList.add(decisionModel);
            JoinModel joinModel = new JoinModel();
            joinModel.setName(step.getId() + "join");
            nodeList.add(joinModel);
            // set decision point to head list
            List<TransitionModel> transList = new ArrayList<TransitionModel>();
            for (Long moneyId : headList.keySet()) {
                NodeModel node = headList.get(moneyId);
                CustFlowMoney money = moneyMap.get(moneyId);
                TransitionModel trans = new TransitionModel();
                trans.setTarget(node);
                trans.setSource(decisionModel);
                node.setInputs(Collections.singletonList(trans));
                trans.setExpr(money.toSpelExpression());
                transList.add(trans);
            }
            decisionModel.setOutputs(transList);
            // set tail list to join model
            for (TransitionModel tail : tailList) {
                tail.setTarget(joinModel);
            }
            joinModel.setInputs(tailList);

            TransitionModel trans = new TransitionModel();
            joinModel.setOutputs(Collections.singletonList(trans));
            trans.setSource(joinModel);
            tranFromPrevStep.setTarget(decisionModel);
            decisionModel.setInputs(Collections.singletonList(tranFromPrevStep));
            return trans;
        }
        else {
            NodeModel node=Collections3.getFirst(headList.values());
            tranFromPrevStep.setTarget(node);
            node.setInputs(Collections.singletonList(tranFromPrevStep));
            return tailList.get(0);
        }

    }

    private void populateTaskModel(CustFlowStep step, CustFlowStepApprovers app, TaskModel taskModel) {
        String nodeName = step.getNodeId().toString();
        taskModel.setName(nodeName);
        if(FlowNodeRole.Factoring.name().equalsIgnoreCase(step.getNodeRole())){
            taskModel.setAssignee(app.getAuditOperId().toString());
        }else{
            taskModel.setAssignee(SnakerEngine.AUTO);
        }
        taskModel.setDisplayName(step.getNodeName());
    }

    public CustFlowBase getBase() {
        return base;
    }

    public void setBase(CustFlowBase base) {
        this.base = base;
    }

    public List<CustFlowStep> getStepList() {
        return stepList;
    }

    public void setStepList(List<CustFlowStep> stepList) {
        this.stepList = stepList;
    }

    public Map<Long, List<CustFlowStepApprovers>> getStepApproversMap() {
        return stepApproversMap;
    }

    public void setStepApproversMap(Map<Long, List<CustFlowStepApprovers>> stepApproversMap) {
        this.stepApproversMap = stepApproversMap;
    }

    public Map<Long, CustFlowMoney> getMoneyMap() {
        return moneyMap;
    }

    public void setMoneyMap(Map<Long, CustFlowMoney> moneyMap) {
        this.moneyMap = moneyMap;
    }

    public static void main(String[] args){
        SnakerProcessModelGenerator process=new SnakerProcessModelGenerator();
        JsonMapper jMapper = JsonMapper.buildNonEmptyMapper();
        String json=jMapper.toJson(process.base);
        System.out.println(json);
        Object base= JsonMapper.parserJson(json);
        
        CustFlowBase testObj=BeanMapper.map(base, CustFlowBase.class);
        System.out.println(base);
        System.out.println(base.getClass());
        System.out.println(testObj);
        System.out.println(testObj.getClass());
    }
}
