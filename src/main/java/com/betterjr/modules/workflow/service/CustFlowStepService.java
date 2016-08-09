package com.betterjr.modules.workflow.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.modules.workflow.dao.CustFlowStepMapper;
import com.betterjr.modules.workflow.data.FlowNodeRole;
import com.betterjr.modules.workflow.entity.CustFlowStep;

@Service
public class CustFlowStepService extends BaseService<CustFlowStepMapper,CustFlowStep>{
    
    public List<Long> findStepsByProcessAndNodeRole(Long anProcessId,FlowNodeRole role){
        return this.mapper.findStepsByProcessAndNodeRole(anProcessId, role);
    }

}
