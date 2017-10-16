package com.betterjr.modules.workflow.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.modules.workflow.dao.CustFlowSysNodeMapper;
import com.betterjr.modules.workflow.entity.CustFlowSysNode;

@Service
public class CustFlowSysNodeService extends BaseService<CustFlowSysNodeMapper, CustFlowSysNode> {

    /**
     * 根据流程类型，得到系统节点
     */
    public List<CustFlowSysNode> findFlowSysNodesByType(String flowType) {
        return this.selectByProperty("flowType", flowType);
    }

}
