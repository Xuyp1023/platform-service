package com.betterjr.modules.workflow.snaker.core;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.snaker.engine.cache.Cache;
import org.snaker.engine.cache.CacheManager;
import org.snaker.engine.core.ProcessService;
import org.snaker.engine.entity.Process;
import org.snaker.engine.model.NodeModel;
import org.snaker.engine.model.ProcessModel;
import org.snaker.engine.model.TaskModel;
import org.springframework.beans.factory.annotation.Autowired;

import com.betterjr.modules.workflow.data.FlowNodeRole;
import com.betterjr.modules.workflow.service.CustFlowBaseService;

public class BetterProcessService extends ProcessService {
    private static final String DEFAULT_SEPARATOR = ".";
    /**
     * 流程定义对象cache名称
     */
    private static final String CACHE_ENTITY = "snaker.process.entity";
    /**
     * 流程id、name的cache名称
     */
    private static final String CACHE_NAME = "snaker.process.name";

    @Autowired
    private CustFlowBaseService flowBaseService;

    private CacheManager newCacheManager;

    /**
     * 根据id获取process对象
     * 先通过cache获取，如果返回空，就从数据库读取并put
     */
    @Override
    public Process getProcessById(String id) {
        Process process = super.getProcessById(id);
        if (process != null) {
            setBetterModel(id, process, null, null);
        }
        return process;
    }

    /**
     * 根据name获取process对象
     * 先通过cache获取，如果返回空，就从数据库读取并put
     */
    @Override
    public Process getProcessByName(String name) {
        return getProcessByVersion(name, null);
    }

    /**
     * 根据name获取process对象
     * 先通过cache获取，如果返回空，就从数据库读取并put
     */
    @Override
    public Process getProcessByVersion(String name, Integer version) {
        Process process = super.getProcessByVersion(name, version);
        if (process != null) {
            String id = process.getId();
            this.setBetterModel(id, process, null, null);
        }
        return process;
    }

    /**
     * 根据id获取process对象,并设置核心&融资方机构
     * 先通过cache获取，如果返回空，就从数据库读取并put
     */
    public Process getProcessWithOrgById(String id, String coreOperOrg, String financerOperOrg) {
        Process process = super.getProcessById(id);
        if (process != null) {
            setBetterModel(id, process, coreOperOrg, financerOperOrg);
        }
        return process;
    }

    private void setBetterModel(String id, Process process, String coreOperOrg, String financerOperOrg) {
        ProcessModel model = this.flowBaseService.findProcessModelByProcessId(id);
        model.setName(process.getName());
        model.setDisplayName(process.getDisplayName());
        process.setModel(model);

        // 设置融资方&核心企业 节点的操作人为机构，如果当前操作人属于融资方&核心企业
        this.populateTaskModelAssigner(process, coreOperOrg, financerOperOrg);

        if (newCacheManager != null) {
            Cache<String, Process> processCache = newCacheManager.getCache(CACHE_ENTITY);
            Cache<String, String> nameCache = newCacheManager.getCache(CACHE_NAME);
            String processName = process.getName() + DEFAULT_SEPARATOR + process.getVersion();
            processCache.put(processName, process);
            nameCache.put(process.getId(), processName);
        }
    }

    public CacheManager getNewCacheManager() {
        return newCacheManager;
    }

    public void setNewCacheManager(CacheManager newCacheManager) {
        this.newCacheManager = newCacheManager;
    }

    /**
     * 每次读取process，动态设置融资方和核心企业的操作人为 机构
     * @param process
     */
    public void populateTaskModelAssigner(Process process, String coreOperOrg, String financerOperOrg) {
        if (StringUtils.isBlank(financerOperOrg) || StringUtils.isBlank(coreOperOrg)) {
            return;
        }

        ProcessModel model = process.getModel();
        List<NodeModel> nodeList = model.getNodes();
        for (NodeModel node : nodeList) {
            if (node instanceof TaskModel) {
                TaskModel task = (TaskModel) node;
                String user = task.getAssignee();
                if (FlowNodeRole.Core.name().equalsIgnoreCase(user)) {
                    task.setAssignee(coreOperOrg);
                }
                if (FlowNodeRole.Financer.name().equalsIgnoreCase(user)) {
                    task.setAssignee(financerOperOrg);
                }
                if (FlowNodeRole.Financer.name().equalsIgnoreCase(user)) {
                    task.setAssignee(financerOperOrg);
                }
            }
        }
    }

    /**
     * 保存process实体对象
     */
    @Override
    public void saveProcess(Process process) {
        Process ori = super.getProcessById(process.getId());
        if (ori == null) {
            access().saveProcess(process);
        } else {
            access().updateProcess(process);
        }
    }
}
