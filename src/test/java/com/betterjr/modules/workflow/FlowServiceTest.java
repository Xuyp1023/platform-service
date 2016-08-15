package com.betterjr.modules.workflow;

import java.math.BigDecimal;
import java.util.List;
import java.util.Random;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betterjr.common.utils.Collections3;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.BasicServiceTest;
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
        Long businessId = new Random().nextLong();
        BigDecimal money = new BigDecimal(10001l);

        FlowInput in = new FlowInput();
        in.setType(type);
        in.setMoney(money);
        in.setBusinessId(businessId);
        in.setOperator("1262");


        FlowService service = this.getServiceObject();
        service.start(in);

//        String cuOperators[] = new String[] { "testUser3", "testUser4", "testUser7", "testUser8" };
        String cuOperators[] = new String[] { "1259", "1262","1259","1258","1259"};
        for (String operator : cuOperators) {
            in.setCommand(FlowCommand.GoNext);
            in.setOperator(operator);
            in.setReason("ok,pass");
            service.exec(in);
        }
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
