package com.betterjr.modules.workflow.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.snaker.engine.IQueryService;
import org.snaker.engine.SnakerEngine;
import org.snaker.engine.access.QueryFilter;
import org.snaker.engine.core.Execution;
import org.snaker.engine.entity.Order;
import org.snaker.engine.entity.Task;
import org.snaker.engine.handlers.impl.MergeBranchHandler;
import org.snaker.engine.model.ForkModel;
import org.snaker.engine.model.JoinModel;
import org.snaker.engine.model.NodeModel;
import org.snaker.engine.model.ProcessModel;
import org.snaker.engine.model.SubProcessModel;
import org.snaker.engine.model.TaskModel;
import org.snaker.engine.model.TransitionModel;
import org.snaker.engine.model.WorkModel;

import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.workflow.ext.ExtTaskModel;

public class MergeBranchWithWeightHandler extends MergeBranchHandler {
    protected JoinModel model;
    
    public MergeBranchWithWeightHandler(JoinModel model) {
        super(model);
        this.model=model;
    }

    public void handle(Execution execution) {
        /**
         * 查询当前流程实例的无法参与合并的node列表
         * 若所有中间node都完成，则设置为已合并状态，告诉model可继续执行join的输出变迁
         */
        IQueryService queryService = execution.getEngine().query();
        Order order = execution.getOrder();
        ProcessModel model = execution.getModel();
        String[] activeNodes = findActiveNodes();
        boolean isSubProcessMerged = false;
        boolean isTaskMerged = false;
        
        if(model.containsNodeNames(SubProcessModel.class, activeNodes)) {
            QueryFilter filter = new QueryFilter().setParentId(order.getId())
                    .setExcludedIds(new String[]{execution.getChildOrderId()});
            List<Order> orders = queryService.getActiveOrders(filter);
            //如果所有子流程都已完成，则表示可合并
            if(orders == null || orders.isEmpty()) {
                isSubProcessMerged = true;
            }
        } else {
            isSubProcessMerged = true;
        }
        if(isSubProcessMerged && model.containsNodeNames(TaskModel.class, activeNodes)) {
            QueryFilter filter = new QueryFilter().
                    setOrderId(order.getId()).
                    setExcludedIds(new String[]{execution.getTask().getId() }).
                    setNames(activeNodes);
            List<Task> tasks = queryService.getActiveTasks(filter);
            if(tasks == null || tasks.isEmpty()) {
                //如果所有task都已完成，则表示可合并
                isTaskMerged = true;
            }else{
                isTaskMerged = mergeWithWeight(execution,tasks);
            }
        }
        execution.setMerged(isSubProcessMerged && isTaskMerged);
    }

    private boolean mergeWithWeight(Execution execution,List<Task> unFinishedTasks) {
        String nodeName=execution.getTask().getTaskName();
        Map<String,Task> unFinishedTaskMap=Collections3.extractToMap(unFinishedTasks, "taskName");
        List<ExtTaskModel> activeNodes=new ArrayList<ExtTaskModel>();
        this.findForkTaskNames(this.model, activeNodes);
        int totalWeight=0;
        for(ExtTaskModel model:activeNodes){
            String activeName=model.getName();
            if(!unFinishedTaskMap.containsKey(activeName)){
                totalWeight=totalWeight+model.getWeight();
            }
        }
        if(totalWeight>=100){
            //如果总审批权重超过100，则通过审批，进入下一步， 其他任务由系统自动完成跟进。
            for(Task task:unFinishedTasks){
                execution.getEngine().task().complete(task.getId(),SnakerEngine.AUTO,execution.getArgs());
            }
            return true;
        }
        return false;
    }
    
    /**
     * 对join节点的所有输入变迁进行递归，查找join至fork节点的所有中间task元素
     * @param node
     * @param buffer
     */
    private void findForkTaskNames(NodeModel node, List<ExtTaskModel> activeNodes) {
        if(node instanceof ForkModel) return;
        List<TransitionModel> inputs = node.getInputs();
        for(TransitionModel tm : inputs) {
            if(tm.getSource() instanceof ExtTaskModel) {
                activeNodes.add((ExtTaskModel)tm.getSource());
            }
            findForkTaskNames(tm.getSource(), activeNodes);
        }
    }
}
