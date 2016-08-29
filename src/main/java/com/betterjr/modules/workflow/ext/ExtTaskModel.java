package com.betterjr.modules.workflow.ext;

import org.snaker.engine.model.TaskModel;

/**
 * 自定义任务模型
 * @author yuqs
 * @since 0.1
 */
public class ExtTaskModel extends TaskModel {
    private String assigneeDisplay;
    
    private Integer weight;
    private boolean hasWeight;
    
    public String getAssigneeDisplay() {
        return assigneeDisplay;
    }

    public void setAssigneeDisplay(String assigneeDisplay) {
        this.assigneeDisplay = assigneeDisplay;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public boolean isHasWeight() {
        return hasWeight;
    }

    public void setHasWeight(boolean hasWeight) {
        this.hasWeight = hasWeight;
    }
}
