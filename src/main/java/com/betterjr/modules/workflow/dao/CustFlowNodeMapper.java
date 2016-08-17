package com.betterjr.modules.workflow.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.modules.notification.entity.Notification;
import com.betterjr.modules.workflow.data.CustFlowNodeData;
import com.betterjr.modules.workflow.entity.CustFlowNode;

@BetterjrMapper
public interface CustFlowNodeMapper extends Mapper<CustFlowNode> {
    @Select("SELECT flow.id as id,flow.`C_MUST` as must,flow.`C_NODE_CUSTOM_NAME` as nodeCustomName,flow.`C_SYS_NODE_NAME` as sysNodeName,flow.`L_SYS_NODE_ID` as sysNodeId ,sys.c_node_role as nodeRole,sys.C_FLOW_TYPE as flowType,sys.C_FLOW_TYPE_NAME as flowTypeName  FROM `t_cust_flow_node` flow LEFT JOIN `t_cust_flow_sys_node` sys ON sys.`ID`=flow.`L_SYS_NODE_ID` WHERE sys.`C_FLOW_TYPE`= #{flowType}")
    @ResultType(CustFlowNodeData.class)
    public List<CustFlowNodeData> findFlowNodesByType(@Param("flowType")String flowType);
    
    @Select("SELECT sys.`C_NODE_ROLE`  FROM `t_cust_flow_node` flow LEFT JOIN `t_cust_flow_sys_node` sys ON sys.`ID`=flow.`L_SYS_NODE_ID` WHERE flow.`ID`=#{id}")
    public String findNodeRoleById(@Param("id")Long id);
    
}