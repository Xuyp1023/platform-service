package com.betterjr.modules.workflow.snaker.model;

import org.snaker.engine.core.Execution;
import org.snaker.engine.model.JoinModel;

import com.betterjr.modules.workflow.snaker.handlers.MergeBranchWithWeightHandler;

public class ExtJoinModel extends JoinModel {
    @Override
    public void exec(Execution execution) {
        fire(new MergeBranchWithWeightHandler(this), execution);
        if (execution.isMerged()) runOutTransition(execution);
    }
}
