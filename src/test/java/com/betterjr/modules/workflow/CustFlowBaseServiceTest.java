package com.betterjr.modules.workflow;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.snaker.engine.model.ProcessModel;

import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.BasicServiceTest;
import com.betterjr.modules.workflow.data.FlowNodeRole;
import com.betterjr.modules.workflow.data.FlowType;
import com.betterjr.modules.workflow.entity.CustFlowBase;
import com.betterjr.modules.workflow.entity.CustFlowMoney;
import com.betterjr.modules.workflow.entity.CustFlowNode;
import com.betterjr.modules.workflow.entity.CustFlowStep;
import com.betterjr.modules.workflow.entity.CustFlowStepApprovers;
import com.betterjr.modules.workflow.service.CustFlowBaseService;

public class CustFlowBaseServiceTest extends BasicServiceTest<CustFlowBaseService>{

    @Override
    public Class<CustFlowBaseService> getTargetServiceClass() {
        // TODO Auto-generated method stub
        return CustFlowBaseService.class;
    }
    
    @Test
    public void saveProcess(){
        CustFlowBaseService service=this.getServiceObject();
        
        CustFlowBase base = buildCustFlowBase();
        service.saveProcess(base);
    }

    private CustFlowBase buildCustFlowBase() {
        CustFlowBase base;
        List<CustFlowStep> stepList;
        Map<Long,List<CustFlowStepApprovers>> stepApproversMap;
        Map<Long,CustFlowMoney> moneyMap;
        
        long processId=100l;
        Long step1Id=200l;
        Long step2Id=300l;
        Long step3Id=400l;
        Long step4Id=500l;
        base=new CustFlowBase();
        base.setId(processId);
        base.setFlowType(FlowType.Trade.name());
        base.setMonitorOperName("testMonitor");
        base.setRegOperName("testbase");
        base.setOperOrg("亿起融");
        base.setRegDate(BetterDateUtils.getNow());
        
        CustFlowNode node=null;
        
        stepList=new ArrayList<CustFlowStep>();
        CustFlowStep step1=new CustFlowStep();
        step1.setId(step1Id);
        step1.setFlowBaseId(processId);
        step1.setOrderNum(1);
        step1.setNodeName("复核");
        step1.setNodeId(2l);
        node=new CustFlowNode();
        node.setNodeRole(FlowNodeRole.Factoring.name());
        step1.setStepNode(node);
        CustFlowStep step2=new CustFlowStep();
        step2.setId(step2Id);
        step2.setFlowBaseId(processId);
        step2.setOrderNum(2);
        step2.setNodeName("融资确认");
        step2.setNodeId(9l);
        node=new CustFlowNode();
        node.setNodeRole(FlowNodeRole.Financer.name());
        step2.setStepNode(node);
        CustFlowStep step3=new CustFlowStep();
        step3.setId(step3Id);
        step3.setFlowBaseId(processId);
        step3.setOrderNum(3);
        step3.setNodeName("核心企业确认");
        step3.setNodeId(10l);
        node=new CustFlowNode();
        node.setNodeRole(FlowNodeRole.Core.name());
        step3.setStepNode(node);
        CustFlowStep step4=new CustFlowStep();
        step4.setId(step4Id);
        step4.setFlowBaseId(processId);
        step4.setOrderNum(4);
        step4.setNodeName("审批");
        step4.setNodeId(6l);
        node=new CustFlowNode();
        node.setNodeRole(FlowNodeRole.Factoring.name());
        step4.setStepNode(node);
        stepList.add(step1);
        stepList.add(step2);
        stepList.add(step3);
        stepList.add(step4);
        
        stepApproversMap=new HashMap<Long,List<CustFlowStepApprovers>>();
        
        Long moneyClassId=5001l;
        Long moneyClassId2=5002l;
        CustFlowMoney money1=new CustFlowMoney();
        money1.setAuditMaxAmount(new BigDecimal("10000"));
        money1.setAuditMinAmount(new BigDecimal("0"));
        CustFlowMoney money2=new CustFlowMoney();
        money2.setAuditMaxAmount(CustFlowMoney.MaxAmount);
        money2.setAuditMinAmount(new BigDecimal("10000"));
        moneyMap=new HashMap<Long,CustFlowMoney>();
        moneyMap.put(moneyClassId, money1);
        moneyMap.put(moneyClassId2, money2);
        
        CustFlowStepApprovers app1=new CustFlowStepApprovers();
        app1.setStepId(step1Id);
        app1.setAuditOperId(4001l);
        app1.setAuditOperName("testUser1");
        app1.setAuditMoneyId(moneyClassId);
        app1.setWeight(50);
        CustFlowStepApprovers app2=new CustFlowStepApprovers();
        app2.setStepId(step1Id);
        app2.setAuditOperId(4002l);
        app2.setAuditOperName("testUser2");
        app2.setAuditMoneyId(moneyClassId);
        app2.setWeight(50);
        CustFlowStepApprovers app3=new CustFlowStepApprovers();
        app3.setStepId(step1Id);
        app3.setAuditOperId(4003l);
        app3.setAuditOperName("testUser3");
        app3.setAuditMoneyId(moneyClassId2);
        app3.setWeight(50);
        CustFlowStepApprovers app4=new CustFlowStepApprovers();
        app4.setStepId(step1Id);
        app4.setAuditOperId(4004l);
        app4.setAuditOperName("testUser4");
        app4.setAuditMoneyId(moneyClassId2);
        app4.setWeight(50);
        List<CustFlowStepApprovers> stepAppsList=new ArrayList<CustFlowStepApprovers>();
        stepAppsList.add(app1);
        stepAppsList.add(app2);
        stepAppsList.add(app3);
        stepAppsList.add(app4);
        stepApproversMap.put(step1Id, stepAppsList);
        
        CustFlowStepApprovers app1Step2=new CustFlowStepApprovers();
        app1Step2.setStepId(step2Id);
        app1Step2.setAuditOperId(111111111l);
        List<CustFlowStepApprovers> stepAppsListStep2=new ArrayList<CustFlowStepApprovers>();
        stepAppsListStep2.add(app1Step2);
        stepApproversMap.put(step2Id, stepAppsListStep2);
        
        CustFlowStepApprovers app1Step3=new CustFlowStepApprovers();
        app1Step3.setStepId(step3Id);
        app1Step3.setAuditOperId(100000153l);
        List<CustFlowStepApprovers> stepAppsListStep3=new ArrayList<CustFlowStepApprovers>();
        stepAppsListStep3.add(app1Step3);
        stepApproversMap.put(step3Id, stepAppsListStep3);
        
        CustFlowStepApprovers app1Step4=new CustFlowStepApprovers();
        app1Step4.setStepId(step4Id);
        app1Step4.setAuditOperId(4005l);
        app1Step4.setAuditOperName("testUser5");
        app1Step4.setAuditMoneyId(moneyClassId);
        app1Step4.setWeight(50);
        CustFlowStepApprovers app2Step4=new CustFlowStepApprovers();
        app2Step4.setStepId(step4Id);
        app2Step4.setAuditOperId(4006l);
        app2Step4.setAuditOperName("testUser6");
        app2Step4.setAuditMoneyId(moneyClassId);
        app2Step4.setWeight(50);
        CustFlowStepApprovers app3Step4=new CustFlowStepApprovers();
        app3Step4.setStepId(step4Id);
        app3Step4.setAuditOperId(4007l);
        app3Step4.setAuditOperName("testUser7");
        app3Step4.setAuditMoneyId(moneyClassId2);
        app3Step4.setWeight(50);
        CustFlowStepApprovers app4Step4=new CustFlowStepApprovers();
        app4Step4.setStepId(step4Id);
        app4Step4.setAuditOperId(4008l);
        app4Step4.setAuditOperName("testUser8");
        app4Step4.setAuditMoneyId(moneyClassId2);
        app4Step4.setWeight(50);
        List<CustFlowStepApprovers> stepAppsListStep4=new ArrayList<CustFlowStepApprovers>();
        stepAppsListStep4.add(app1Step4);
        stepAppsListStep4.add(app2Step4);
        stepAppsListStep4.add(app3Step4);
        stepAppsListStep4.add(app4Step4);
        stepApproversMap.put(step4Id, stepAppsListStep4);
        
        base.setStepList(stepList);
        for(CustFlowStep step:stepList){
            Long id=step.getId();
            List<CustFlowStepApprovers> appList=stepApproversMap.get(id);
            if(!Collections3.isEmpty(appList)){
                for(CustFlowStepApprovers app:appList){
                    Long moneyId=app.getAuditMoneyId();
                    app.setMoney(moneyMap.get(moneyId));
                }
            }
            step.setStepApprovers(appList);
        }
        return base;
    }

}
