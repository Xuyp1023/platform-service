package com.betterjr.modules.workflow.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.selectkey.SerialGenerator;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.mapper.entity.ReferClass;
import com.betterjr.modules.workflow.dao.CustFlowNodeMapper;
import com.betterjr.modules.workflow.data.CustFlowNodeData;
import com.betterjr.modules.workflow.data.FlowErrorCode;
import com.betterjr.modules.workflow.data.FlowType;
import com.betterjr.modules.workflow.entity.CustFlowNode;
import com.betterjr.modules.workflow.entity.CustFlowStep;

@Service
public class CustFlowNodeService extends BaseService<CustFlowNodeMapper, CustFlowNode> {
    
    public static final String IdKey="CustFlowNode.id";
    
    @Autowired
    private CustFlowStepService stepService;
    @Autowired
    private CustFlowStepApproversService stepAppService;

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
    public List<CustFlowNodeData> findFlowNodesByType(String flowType) {
        return this.mapper.findFlowNodesByType(flowType);
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
        BTAssert.notEmpty(nodeList, "不存在关联的系统节点");

        int nodeCount=nodeList.size();
        BTAssert.notLessThan(nodeCount,10, "关联一个系统节点的自定义节点数不能超过9个");
        
        Set<Long> idSet=new HashSet<Long>();
        for(CustFlowNode indexNode:nodeList){
            idSet.add(indexNode.getId());
        }
        
        Long newId=anNode.getSysNodeId();
        for(int i=1;i<=9;i++){
            newId=anNode.getSysNodeId()+i;
            if(!idSet.contains(newId)){
                break;
            }
        }
        anNode.setId(newId);
        this.insert(anNode);
        
    }

    /**
     * 更新, nodeCustomName为空，表示删除,只能修改自定义节点
     */
    public void saveFlowNode(CustFlowNode anNode) {
        
        CustFlowNode node = this.selectByPrimaryKey(anNode.getId());
        BTAssert.notNull(node,"数据库不存在记录，更新失败");
        
        if(node.getId().equals(node.getSysNodeId())){
            BTAssert.notNull(node,"不能修改或者删除系统节点");
        }

        if (BetterStringUtils.isBlank(anNode.getNodeCustomName())) {
            this.delete(node);
            
            //同步删除steps and stepApps
            List<CustFlowStep> stepList=this.stepService.selectByProperty("nodeId", node.getId());
            for(CustFlowStep step:stepList){
                this.stepService.delete(step);
                this.stepAppService.deleteByProperty("stepId", step.getId());
            }
            
        }
        else {
            //只允许修改自定义名称
            anNode.setSysNodeId(node.getSysNodeId());
            anNode.setSysNodeName(node.getSysNodeName());
            anNode.setMust("0");
            this.updateByPrimaryKey(anNode);
            
            //同步到steps
            List<CustFlowStep> stepList=this.stepService.selectByProperty("nodeId", node.getId());
            for(CustFlowStep step:stepList){
                step.setNodeName(anNode.getNodeCustomName());
                this.stepService.updateByPrimaryKey(step);
            }
            
        }

    }
}
