package com.betterjr.modules.workflow.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.selectkey.SerialGenerator;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.mapper.entity.ReferClass;
import com.betterjr.modules.workflow.dao.CustFlowNodeMapper;
import com.betterjr.modules.workflow.data.FlowErrorCode;
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
     * 根据流程id,得到nodeRole
     */
    public String findNodeRoleById(Long id) {
        return this.mapper.findNodeRoleById(id);
    }

    /**
     * 根据流程类型，得到自定义流程所有节点
     */
    public List<CustFlowNode> findFlowNodesByType(String flowType) {
        return this.mapper.findFlowNodesByType(flowType);
    }
    
    /**
     * 根据流程类型，得到系统节点
     */
    public List<CustFlowNode> findFlowSysNodesByType(String flowType) {
        return this.mapper.findFlowSysNodesByType(flowType);
    }
    
    /**
     * 新增，存在则更新,系统节点id是两位（110-990）,自定义节点是在系统节点id的后面加一,范围0-9：111-119,121-129.... （第一个自定义节点默认是系统节点Id）
     */
    public void addFlowNode(CustFlowNode anNode){
        List<CustFlowNode> nodeListByName =this.selectByProperty("nodeCustomName", anNode.getNodeCustomName());
        if(!Collections3.isEmpty(nodeListByName)){
            throw new BytterTradeException(FlowErrorCode.ExistsNodeName.getCode(),"节点名称已存在!!");
        }
        List<CustFlowNode> nodeList =this.selectByProperty("sysNodeId", anNode.getSysNodeId());
        if(Collections3.isEmpty(nodeList)){
            anNode.setId(SerialGenerator.getLongValue(CustFlowNode.selectKey));
            this.insert(anNode);
        }else{
            int newIndex=nodeList.size();
            long newId=anNode.getSysNodeId()+newIndex;
            anNode.setId(newId);
            this.insert(anNode);
        }
    }

    /**
     * 更新, nodeCustomName为空，表示删除
     */
    public void saveFlowNode(CustFlowNode anNode) {
        
        CustFlowNode node = this.selectByPrimaryKey(anNode.getId());
        BTAssert.notNull(node,"数据库不存在记录，更新失败");

        if (BetterStringUtils.isBlank(anNode.getNodeCustomName())) {
            this.delete(anNode);
        }
        else {
            List<CustFlowNode> nodeListByName =this.selectByProperty("nodeCustomName", anNode.getNodeCustomName());
            if(!Collections3.isEmpty(nodeListByName)){
                throw new BytterTradeException(FlowErrorCode.ExistsNodeName.getCode(),"节点名称已存在!!");
            }
            this.updateByPrimaryKey(anNode);
        }

    }
}
