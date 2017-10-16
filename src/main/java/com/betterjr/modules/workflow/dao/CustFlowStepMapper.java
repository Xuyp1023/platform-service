package com.betterjr.modules.workflow.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;

import com.betterjr.common.annotation.BetterjrMapper;
import com.betterjr.mapper.common.Mapper;
import com.betterjr.modules.workflow.data.FlowNodeRole;
import com.betterjr.modules.workflow.entity.CustFlowStep;

@BetterjrMapper
public interface CustFlowStepMapper extends Mapper<CustFlowStep> {
    @Select("SELECT step.id  FROM `t_cust_flow_steps` step LEFT JOIN `t_cust_flow_node` node  ON step.`L_NODE_ID`=node.`ID` LEFT JOIN `t_cust_flow_sys_node` sys ON sys.`ID`=node.`L_SYS_NODE_ID` WHERE sys.`C_NODE_ROLE`=#{role} AND step.`L_FLOWBASE_ID`=#{anProcessId};")
    @ResultType(Long.class)
    public List<Long> findStepsByProcessAndNodeRole(@Param("anProcessId") Long anProcessId,
            @Param("role") FlowNodeRole role);

    @Select("SELECT step2.* FROM `t_cust_flow_steps` step JOIN t_cust_flow_steps step2 WHERE step.`L_FLOWBASE_ID`=#{anProcessId} AND step.`L_NODE_ID`=#{anNodeId} AND step2.`N_ORDER_NUM`<step.`N_ORDER_NUM`")
    @ResultType(CustFlowStep.class)
    public List<CustFlowStep> findPrevStepsByNodeId(@Param("anProcessId") Long anProcessId,
            @Param("anNodeId") Long anNodeId);
}