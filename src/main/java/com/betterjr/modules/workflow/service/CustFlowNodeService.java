package com.betterjr.modules.workflow.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.betterjr.common.selectkey.SerialGenerator;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.modules.workflow.dao.CustFlowNodeMapper;
import com.betterjr.modules.workflow.data.FlowType;
import com.betterjr.modules.workflow.entity.CustFlowNode;

@Service
public class CustFlowNodeService extends BaseService<CustFlowNodeMapper, CustFlowNode> {
    
    public static final String IdKey="CustFlowNode.id";

    /**
     * 得到流程类型
     * 
     * @return
     */
    public FlowType[] findAllFlowType() {
        return FlowType.values();
    }

    /**
     * 根据流程类型，得到自定义流程所有节点
     */
    public List<CustFlowNode> findFlowNodesByType(String flowType) {
        return this.mapper.findFlowNodesByType(flowType);
    }

    /**
     * 更新, nodeCustomName为空，表示删除
     */
    public void saveFlowNode(CustFlowNode anNode) {
        
        if (anNode.getId() == null) {
            anNode.setId(SerialGenerator.getLongValue(IdKey));
            this.insert(anNode);
            return;
        }

        CustFlowNode node = this.selectByPrimaryKey(anNode.getId());
        BTAssert.notNull(node,"数据库不存在记录，更新失败");

        if (BetterStringUtils.isBlank(anNode.getNodeCustomName())) {
            this.delete(anNode);
        }
        else {
            this.updateByPrimaryKey(anNode);
        }

    }
}
