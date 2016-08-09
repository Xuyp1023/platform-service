package com.betterjr.modules.workflow.service;

import org.snaker.engine.cache.Cache;
import org.snaker.engine.cache.CacheManager;
import org.snaker.engine.core.ProcessService;
import org.snaker.engine.entity.Process;
import org.snaker.engine.model.ProcessModel;
import org.springframework.beans.factory.annotation.Autowired;

public class BetterProcessService extends ProcessService{
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
	public Process getProcessById(String id) {
		Process process=super.getProcessById(id);
		if(process!=null){
		    setBetterModel(id, process);
		}
		return process;
	}
	
	/**
     * 根据name获取process对象
     * 先通过cache获取，如果返回空，就从数据库读取并put
     */
    public Process getProcessByName(String name) {
        return getProcessByVersion(name, null);
    }
    
    /**
     * 根据name获取process对象
     * 先通过cache获取，如果返回空，就从数据库读取并put
     */
    public Process getProcessByVersion(String name, Integer version) {
        Process process=super.getProcessByVersion(name, version);
        if(process!=null){
            String id=process.getId();
            this.setBetterModel(id, process);
        }
        return process;
    }

    private void setBetterModel(String id, Process process) {
        ProcessModel model=this.flowBaseService.findProcessModelByProcessId(id);
		model.setName(process.getName());
		model.setDisplayName(process.getDisplayName());
		process.setModel(model);
		
		if(newCacheManager!=null){
			Cache<String, Process> processCache=newCacheManager.getCache(CACHE_ENTITY);
			Cache<String, String> nameCache=newCacheManager.getCache(CACHE_NAME);
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

}
