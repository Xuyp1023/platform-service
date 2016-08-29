package com.betterjr.modules.workflow;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snaker.engine.SnakerEngine;

import com.betterjr.common.data.PlatformBaseRuleType;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.BasicServiceTest;
import com.betterjr.modules.account.data.CustContextInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.sys.security.ShiroUser;
import com.betterjr.modules.workflow.data.FlowCommand;
import com.betterjr.modules.workflow.data.FlowInput;
import com.betterjr.modules.workflow.data.FlowStatus;
import com.betterjr.modules.workflow.data.FlowType;
import com.betterjr.modules.workflow.data.TaskAuditHistory;
import com.betterjr.modules.workflow.entity.CustFlowStep;
import com.betterjr.modules.workflow.service.FlowService;

public class FlowServiceTest extends BasicServiceTest<FlowService> {

    private static final Logger logger=LoggerFactory.getLogger(FlowServiceTest.class);
    
    @Override
    public Class<FlowService> getTargetServiceClass() {
        // TODO Auto-generated method stub
        return FlowService.class;
    }

    @Test
    public void startAndExec() {
        FlowType type = FlowType.Trade;
        Long businessId =(long)(56383459l);
        BigDecimal money = new BigDecimal(100001l);
        String financerOperOrg="biet;Developer.Supplier.Company";
        String coreOperOrg="biet;Developer.Core.Enterprise";
        FlowInput in = new FlowInput();
        in.setType(type);
        in.setMoney(money);
        in.setBusinessId(businessId);
        in.setOperator(financerOperOrg);
        in.setCoreOperOrg(coreOperOrg);
        in.setFinancerOperOrg(financerOperOrg);

        FlowService service = this.getServiceObject();
        service.start(in);

        String cuOperators[] = new String[] { "1259", financerOperOrg,"1259",coreOperOrg,"1118","1092"};
        for (String operator : cuOperators) {
            try {
                Thread.sleep(2000);
            }
            catch (InterruptedException e) {}
            in.setCommand(FlowCommand.GoNext);
            in.setOperator(operator);
            in.setReason("ok,pass");
            service.exec(in);
        }
    }

    @Test
    public void rollBack() {
        Long businessId = 56383459l;
        BigDecimal money = new BigDecimal(10001l);

        FlowService service = this.getServiceObject();
        FlowInput in = new FlowInput();
        in.setMoney(money);
        in.setBusinessId(businessId);
        in.setCommand(FlowCommand.Rollback);
        in.setRollbackNodeId("150");
        in.setOperator( "1259");
        in.setReason("rollback to 放款确认");
        service.exec(in);
    }
    
    @Test
    public void rollBack2() {
        Long businessId = 56383459l;
        BigDecimal money = new BigDecimal(10001l);

        FlowService service = this.getServiceObject();
        FlowInput in = new FlowInput();
        in.setMoney(money);
        in.setBusinessId(businessId);
        in.setCommand(FlowCommand.Rollback);
        in.setRollbackNodeId("140");
        in.setOperator( "1118");
        in.setReason("rollback to 核心企业确认");
        service.exec(in);
    }
    
    @Test
    public void exec() {
        Long businessId = 56383459l;
        BigDecimal money = new BigDecimal(10001l);

        FlowService service = this.getServiceObject();
        FlowInput in = new FlowInput();
        in.setMoney(money);
        in.setBusinessId(businessId);
        in.setCommand(FlowCommand.GoNext);
        in.setOperator("biet;Developer.Core.Enterprise");
        in.setReason("ok,pass");
        service.exec(in);
    }
    
    @Test
    public void exit() {
        Long businessId = 56383459l;
        BigDecimal money = new BigDecimal(10001l);

        FlowService service = this.getServiceObject();
        FlowInput in = new FlowInput();
        in.setMoney(money);
        in.setBusinessId(businessId);
        in.setCommand(FlowCommand.Exit);
        in.setOperator( "1095");
        in.setReason("拒绝，终止流程");
        service.exec(in);
    }
    
    @Test
    public void queryHistoryWorkTask(){
        FlowService service = this.getServiceObject();
        Page<FlowStatus> page=service.queryHistoryWorkTask(null, null);
        List<FlowStatus> list=page.getResult();
        if(!Collections3.isEmpty(list)){
            for(FlowStatus sta:list){
                logger.error("queryHistoryWorkTask:"+sta);
            }
        }
    }
    
    @Test
    public void changeProcessAudit() {
        String flowOrderId="92f1ce6054104446be19981124feb875";
        String[] actorIds=new String[]{"1259","changeAudit1","changeAudit2","changeAudit3"};
        FlowService service = this.getServiceObject();
        service.changeProcessAudit(actorIds, flowOrderId);
    }
    
    @Test
    public void queryCurrentWorkTask(){
        FlowService service = this.getServiceObject();
        Page<FlowStatus> page=service.queryCurrentWorkTask(null, null);
        List<FlowStatus> list=page.getResult();
        if(!Collections3.isEmpty(list)){
            for(FlowStatus sta:list){
                logger.error("queryCurrentWorkTask:"+sta);
            }
        }
    }
    
    @Test
    public void queryCurrentWorkTaskWithNotExistsUser(){
        FlowService service = this.getServiceObject();
        FlowStatus search=new FlowStatus();
        search.setOperator("testUser3");
        Page<FlowStatus> page=service.queryCurrentWorkTask(null,search );
        List<FlowStatus> list=page.getResult();
        if(!Collections3.isEmpty(list)){
            for(FlowStatus sta:list){
                logger.error("queryCurrentWorkTask:"+sta);
            }
        }
    }
    
    @Test
    public void queryCurrentWorkTaskWithUser(){
        FlowService service = this.getServiceObject();
        FlowStatus search=new FlowStatus();
        search.setOperator("1259");
        Page<FlowStatus> page=service.queryCurrentWorkTask(null, search);
        List<FlowStatus> list=page.getResult();
        if(!Collections3.isEmpty(list)){
            for(FlowStatus sta:list){
                logger.error("queryCurrentWorkTask:"+sta);
            }
        }
    }
    
    @Test
    public void getExecutedHistory(){
        FlowService service = this.getServiceObject();
        Long businessId=6538303027681064631l;
        List<TaskAuditHistory> list=service.findExecutedHistory(businessId);
        if(!Collections3.isEmpty(list)){
            for(TaskAuditHistory sta:list){
                logger.error("getExecutedHistory:"+sta+","+sta.getCommand().getDisplayName());
            }
        }
    }
    
    @Test
    public void getExecutedNodes(){
        FlowService service = this.getServiceObject();
        Long businessId=6538303027681064631l;
        List<CustFlowStep> list=service.findExecutedNodes(businessId);
        if(!Collections3.isEmpty(list)){
            for(CustFlowStep sta:list){
                logger.error("getExecutedNodes:"+sta);
            }
        }
    }
    
    @Test
    public void queryWorkTaskByMonitor(){
        FlowService service = this.getServiceObject();
        FlowStatus search=new FlowStatus();
        search.setOperator("testMonitor");
        Page<FlowStatus> page=service.queryWorkTaskByMonitor(null, search);
        List<FlowStatus> list=page.getResult();
        if(!Collections3.isEmpty(list)){
            for(FlowStatus sta:list){
                logger.error("queryWorkTaskByMonitor:"+sta);
            }
        }
    }

}
