package com.betterjr.modules.workflow.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.modules.notification.entity.Notification;
import com.betterjr.modules.workflow.entity.CustFlowNode;

@BetterjrMapper
public interface CustFlowNodeMapper extends Mapper<CustFlowNode> {
    @Select("SELECT flow.id as id,flow.`C_MUST` as c_must,flow.`C_NODE_CUSTOM_NAME` as C_NODE_CUSTOM_NAME,flow.`C_SYS_NODE_NAME` as C_SYS_NODE_NAME,flow.`L_SYS_NODE_ID` as L_SYS_NODE_ID FROM `t_cust_flow_node` flow LEFT JOIN `t_cust_flow_sys_node` sys ON sys.`ID`=flow.`L_SYS_NODE_ID` WHERE sys.`C_FLOW_TYPE`= #{flowType}")
    @ResultType(CustFlowNode.class)
    public List<CustFlowNode> findFlowNodesByType(@Param("flowType")String flowType);
    
    
    @Select("SELECT flow.id as id,flow.`C_MUST` as c_must,flow.`C_NODE_CUSTOM_NAME` as C_NODE_CUSTOM_NAME,flow.`C_SYS_NODE_NAME` as C_SYS_NODE_NAME,flow.`L_SYS_NODE_ID` as L_SYS_NODE_ID FROM `t_cust_flow_node` flow LEFT JOIN `t_cust_flow_sys_node` sys ON sys.`ID`=flow.`L_SYS_NODE_ID` WHERE sys.`C_FLOW_TYPE`= #{flowType}")
    @ResultType(CustFlowNode.class)
    public CustFlowNode findFlowNodesById(Long id);
}