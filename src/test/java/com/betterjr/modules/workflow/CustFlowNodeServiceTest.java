package com.betterjr.modules.workflow;

import java.util.List;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betterjr.modules.BasicServiceTest;
import com.betterjr.modules.workflow.data.CustFlowNodeData;
import com.betterjr.modules.workflow.data.FlowType;
import com.betterjr.modules.workflow.entity.CustFlowNode;
import com.betterjr.modules.workflow.service.CustFlowNodeService;

public class CustFlowNodeServiceTest extends BasicServiceTest<CustFlowNodeService>{

    private static final Logger logger=LoggerFactory.getLogger(CustFlowNodeServiceTest.class);
    
    @Override
    public Class<CustFlowNodeService> getTargetServiceClass() {
        // TODO Auto-generated method stub
        return CustFlowNodeService.class;
    }

    @Test
    public void findFlowNodesByType(){
        CustFlowNodeService service=this.getServiceObject();
        List<CustFlowNodeData> list=service.findFlowNodesByType(FlowType.Trade.name());
        for(CustFlowNodeData node:list){
            logger.error(node.toString());
        }
    }
}
