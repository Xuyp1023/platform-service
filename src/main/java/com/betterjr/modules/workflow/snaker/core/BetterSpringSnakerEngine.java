package com.betterjr.modules.workflow.snaker.core;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.snaker.engine.core.Execution;
import org.snaker.engine.entity.Order;
import org.snaker.engine.entity.Process;
import org.snaker.engine.entity.Task;
import org.snaker.engine.helper.AssertHelper;
import org.snaker.engine.helper.DateHelper;
import org.snaker.engine.model.NodeModel;
import org.snaker.engine.model.ProcessModel;
import org.snaker.engine.model.StartModel;
import org.snaker.engine.model.TransitionModel;
import org.snaker.engine.spring.SpringSnakerEngine;

import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.workflow.data.FlowInput;

public class BetterSpringSnakerEngine extends SpringSnakerEngine {
    private static final Logger log = LoggerFactory.getLogger(BetterSpringSnakerEngine.class);
    /**
     * 根据任务主键ID，操作人ID，参数列表完成任务，并且构造执行对象
     * @param taskId 任务id
     * @param operator 操作人
     * @param args 参数列表
     * @return Execution
     */
    private Execution betterExecute(String taskId, String operator, Map<String, Object> args, String coreOperOrg,String financerOperOrg) {
        if(args == null) args = new HashMap<String, Object>();
        Task task = task().complete(taskId, operator, args);
        if(log.isDebugEnabled()) {
            log.debug("任务[taskId=" + taskId + "]已完成");
        }
        Order order = query().getOrder(task.getOrderId());
        AssertHelper.notNull(order, "指定的流程实例[id=" + task.getOrderId() + "]已完成或不存在");
        order.setLastUpdator(operator);
        order.setLastUpdateTime(DateHelper.getTime());
        order().updateOrder(order);
        //协办任务完成不产生执行对象
        if(!task.isMajor()) {
            return null;
        }
        Map<String, Object> orderMaps = order.getVariableMap();
        if(orderMaps != null) {
            for(Map.Entry<String, Object> entry : orderMaps.entrySet()) {
                if(args.containsKey(entry.getKey())) {
                    continue;
                }
                args.put(entry.getKey(), entry.getValue());
            }
        }
        BetterProcessService processService = (BetterProcessService)process();
        Process process=processService.getProcessWithOrgById(order.getProcessId(), coreOperOrg, financerOperOrg);
        Execution execution = new Execution(this, process, order, args);
        execution.setOperator(operator);
        execution.setTask(task);
        return execution;
    }
    
    /**
     * 创建流程实例，并返回执行对象
     * @param process 流程定义
     * @param operator 操作人
     * @param args 参数列表
     * @param parentId 父流程实例id
     * @param parentNodeName 启动子流程的父流程节点名称
     * @return Execution
     */
    private Execution betterExecute(Process process, String operator, Map<String, Object> args, 
            String parentId, String parentNodeName) {
        Order order = order().createOrder(process, operator, args, parentId, parentNodeName);
        if(log.isDebugEnabled()) {
            log.debug("创建流程实例对象:" + order);
        }
        Execution current = new Execution(this, process, order, args);
        current.setOperator(operator);
        return current;
    }
    
    private Order betterStartProcess(Process process, String operator, Map<String, Object> args) {
        Execution execution = betterExecute(process, operator, args, null, null);
        if(process.getModel() != null) {
            StartModel start = process.getModel().getStart();
            AssertHelper.notNull(start, "流程定义[name=" + process.getName() + ", version=" + process.getVersion() + "]没有开始节点");
            start.execute(execution);
        }

        return execution.getOrder();
    }
    
    /**
     * 根据任务主键ID，操作人ID，参数列表执行任务，并且根据nodeNameList跳转到任意节点
     * 1、nodeNameList为null时，则驳回至上一步处理
     * 2、nodeNameList不为null时，则任意跳转，即动态创建转移
     */
    public List<Task> executeAndJumpTask(String taskId, String operator, Map<String, Object> args, Set<String> nodeNameList,String coreOperOrg,String financerOperOrg) {
        Execution execution = betterExecute(taskId, operator, args,  coreOperOrg, financerOperOrg);
        if(execution == null) return Collections.emptyList();
        ProcessModel model = execution.getProcess().getModel();
        AssertHelper.notNull(model, "当前任务未找到流程定义模型");
        if(Collections3.isEmpty(nodeNameList)) {
            Task newTask = task().rejectTask(model, execution.getTask());
            execution.addTask(newTask);
        } else {
            for(String nodeName:nodeNameList){
                NodeModel nodeModel = model.getNode(nodeName);
                AssertHelper.notNull(nodeModel, "根据节点名称[" + nodeName + "]无法找到节点模型");
                //动态创建转移对象，由转移对象执行execution实例
                TransitionModel tm = new TransitionModel();
                tm.setTarget(nodeModel);
                tm.setEnabled(true);
                tm.execute(execution);
            }
        }

        return execution.getTasks();
    }

    
    /**
     * 根据任务主键ID，操作人ID，参数列表执行任务
     */
    public List<Task> executeTask(String taskId, String operator, Map<String, Object> args,String coreOperOrg,String financerOperOrg) {
        //完成任务，并且构造执行对象
        Execution execution = betterExecute(taskId, operator, args,  coreOperOrg, financerOperOrg);
        if(execution == null) return Collections.emptyList();
        ProcessModel model = execution.getProcess().getModel();
        if(model != null) {
            NodeModel nodeModel = model.getNode(execution.getTask().getTaskName());
            //将执行对象交给该任务对应的节点模型执行
            nodeModel.execute(execution);
        }
        return execution.getTasks();
    }
    
    
    /**
     * 根据任务主键ID，操作人ID，参数列表执行任务,但是不驱动流程
     */
    public List<Task> executeTaskOnly(String taskId, String operator, Map<String, Object> args,String coreOperOrg,String financerOperOrg) {
        //完成任务，并且构造执行对象
        Execution execution = betterExecute(taskId, operator, args,  coreOperOrg, financerOperOrg);
        return Collections.emptyList();
    }
    
    
    
    
    /**
     * 根据流程定义ID，操作人ID，参数列表启动流程实例
     */
    public Order startInstanceById(String id, String operator, Map<String, Object> args,String coreOperOrg,String financerOperOrg) {
        if(args == null) args = new HashMap<String, Object>();
        BetterProcessService processService = (BetterProcessService)process();
        Process process = processService.getProcessWithOrgById(id, coreOperOrg,financerOperOrg);
        process().check(process, id);
        return betterStartProcess(process, operator, args);
    }
}
