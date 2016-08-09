package com.betterjr.modules.workflow.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.snaker.engine.core.ProcessService;
import org.snaker.engine.model.ProcessModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAndOperatorRelaService;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.workflow.dao.CustFlowBaseMapper;
import com.betterjr.modules.workflow.data.FlowNodeRole;
import com.betterjr.modules.workflow.entity.CustFlowBase;
import com.betterjr.modules.workflow.entity.CustFlowMoney;
import com.betterjr.modules.workflow.entity.CustFlowNode;
import com.betterjr.modules.workflow.entity.CustFlowStep;
import com.betterjr.modules.workflow.entity.CustFlowStepApprovers;
import com.betterjr.modules.workflow.utils.SnakerProcessModelGenerator;

@Service
public class CustFlowBaseService extends BaseService<CustFlowBaseMapper, CustFlowBase> {

    @Autowired
    private CustFlowStepService stepService;
    @Autowired
    private CustFlowStepApproversService stepAppService;
    @Autowired
    private CustFlowMoneyService moneyService;
    @Autowired
    private ProcessService snakerProcessService;

    @Autowired
    private CustAndOperatorRelaService operatorRelaService;
    @Autowired
    private CustOperatorService operatorService;
    @Autowired
    private CustFlowNodeService nodeService;

    /**
     * 读取流程配置，构建snaker ProcessModel
     * 
     * @param id
     * @return
     */
    public ProcessModel findProcessModelByProcessId(String id) {
        return this.findProcessModelByProcessId(Long.parseLong(id));
    }

    /**
     * 读取流程配置，构建snaker ProcessModel
     * 
     * @param id
     * @return
     */
    public ProcessModel findProcessModelByProcessId(Long id) {
        CustFlowBase base = this.selectByPrimaryKey(id);
        if (base == null) {
            return null;
        }
        List<CustFlowStep> stepList = this.stepService.selectByProperty("flowBaseId", id);

        Map<Long, List<CustFlowStepApprovers>> stepApproversMap = new HashMap();
        Map<Long, CustFlowMoney> moneyMap = new HashMap();
        for (CustFlowStep step : stepList) {
            List<CustFlowStepApprovers> appList = this.stepAppService.selectByProperty("stepId", step.getId());
            stepApproversMap.put(step.getId(), appList);

            for (CustFlowStepApprovers app : appList) {
                CustFlowMoney money = this.moneyService.selectByPrimaryKey(app.getAuditMoneyId());
                if (money != null) {
                    moneyMap.put(money.getId(), money);
                    app.setMoney(money);
                }
            }

            step.setStepApprovers(appList);
        }

        SnakerProcessModelGenerator generator = new SnakerProcessModelGenerator();
        generator.setBase(base);
        generator.setStepList(stepList);
        generator.setStepApproversMap(stepApproversMap);
        generator.setMoneyMap(moneyMap);

        return generator.buildProcessModel();

    }

    /**
     * 保存流程配置
     * 
     * @param base
     */
    public void saveProcess(CustFlowBase base) {
        // base
        this.insert(base);
        // steps --> approvers
        if (!Collections3.isEmpty(base.getStepList())) {
            for (CustFlowStep step : base.getStepList()) {
                this.stepService.insert(step);
                // 判断节点角色
                CustFlowNode node = step.getStepNode();
                if (FlowNodeRole.Core.name().equalsIgnoreCase(node.getNodeRole())) {

                    if (!Collections3.isEmpty(step.getStepApprovers())) {
                        for (CustFlowStepApprovers app : step.getStepApprovers()) {
                            this.addCoreAudit(app.getAuditOperId(), base.getId());
                        }
                    }
                }

                if (FlowNodeRole.Financer.name().equalsIgnoreCase(node.getNodeRole())) {

                    if (!Collections3.isEmpty(step.getStepApprovers())) {
                        for (CustFlowStepApprovers app : step.getStepApprovers()) {
                            this.addFinancerAudit(app.getAuditOperId(), base.getId());
                        }
                    }
                }

                if (FlowNodeRole.Factoring.name().equalsIgnoreCase(node.getNodeRole())) {
                    if (!Collections3.isEmpty(step.getStepApprovers())) {
                        for (CustFlowStepApprovers app : step.getStepApprovers()) {
                            this.stepAppService.insert(app);
                        }
                    }
                }
            }
        }

        // snaker process
        org.snaker.engine.entity.Process snakerProcess = new org.snaker.engine.entity.Process();
        snakerProcess.setCreator(base.getRegOperName());
        snakerProcess.setCreateTime(BetterDateUtils.formatDateTime(base.getRegDate()));
        snakerProcess.setType(base.getFlowType());
        snakerProcess.setName(base.getFlowType());
        snakerProcess.setDisplayName(base.getFlowType());
        snakerProcess.setId(base.getId().toString());
        snakerProcess.setState(1);
        snakerProcess.setVersion(1);
        this.snakerProcessService.saveProcess(snakerProcess);
    }

    /**
     * 增加融资方审批人
     */
    private void addFinancerAudit(Long custNo, Long anProcessId) {
        this.addAudit(custNo, anProcessId, FlowNodeRole.Financer);
    }

    /**
     * 增加核心企业审批人
     */
    private void addCoreAudit(Long custNo, Long anProcessId) {
        addAudit(custNo, anProcessId, FlowNodeRole.Core);
    }

    private void addAudit(Long custNo, Long anProcessId, FlowNodeRole role) {
        List<Long> operaList = this.operatorRelaService.findOperNoList(custNo);
        List<CustOperatorInfo> list = this.operatorService.selectByListProperty("id", operaList);

        List<Long> stepIdList = this.stepService.findStepsByProcessAndNodeRole(anProcessId, role);
        for (CustOperatorInfo oper : list) {
            for (Long stepId : stepIdList) {
                CustFlowStepApprovers app = new CustFlowStepApprovers();
                app.setStepId(stepId);
                app.setAuditOperId(oper.getId());
                app.setAuditOperName(oper.getName());
                app.setAuditMoneyId(CustFlowMoney.DefaultMoney);
                app.setWeight(CustFlowStepApprovers.MaxWeight);
                this.stepAppService.insert(app);
            }
        }
    }

    /**
     * 当前节点流程审批人
     */
    public void saveProcessAudit(String[] operators) {

    }
}
