package com.betterjr.modules.workflow.ext;

import org.snaker.engine.core.Execution;
import org.snaker.engine.handlers.impl.MergeBranchHandler;
import org.snaker.engine.model.JoinModel;

import com.betterjr.modules.workflow.handlers.MergeBranchWithWeightHandler;

public class ExtJoinModel extends JoinModel{
    public void exec(Execution execution) {
        fire(new MergeBranchWithWeightHandler(this), execution);
        if(execution.isMerged()) runOutTransition(execution);
    }
}
