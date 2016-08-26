package com.betterjr.modules.customer.service;

import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.dao.CustMechManagerMapper;
import com.betterjr.modules.customer.entity.CustMechManager;
import com.betterjr.modules.customer.entity.CustMechManagerTmp;

/**
 * 高管信息管理
 * @author liuwl
 *
 */
@Service
public class CustMechManagerService extends BaseService<CustMechManagerMapper, CustMechManager> {
    
    @Resource
    private CustMechManagerTmpService managerTmpService;

    /**
     * 查询高管列表
     * @param anCustNo
     * @return
     */
    public List<CustMechManager> queryCustMechManager(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        return this.selectByProperty("custNo", anCustNo);
    }
    
    /**
     * 查询高管信息
     */
    public CustMechManager findCustMechManager(Long anId) {
        BTAssert.notNull(anId, "高管编号不允许为空！");
        
        CustMechManager manager = this.selectByPrimaryKey(anId);
        return manager;
    }
    
    /**
     * 添加高管信息
     */
    public CustMechManager addCustMechManager(CustMechManagerTmp anManagerTmp) {
        BTAssert.notNull(anManagerTmp, "高管流水信息不允许为空！");
        
        CustMechManager manager = new CustMechManager();
        manager.initAddValue(anManagerTmp);
        this.insert(manager);
        
        return manager;
    }
    
    /**
     * 保存高管信息
     */
    public CustMechManager saveCustMechManager(CustMechManager anManager, Long anId) {
        BTAssert.notNull(anId, "高管编号不允许为空！");
        BTAssert.notNull(anManager, "高管信息不允许为空！");
        
        final CustMechManager tempManager = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempManager, "对应的高管信息没有找到！");
        
        tempManager.initModifyValue(anManager);
        this.updateByPrimaryKeySelective(tempManager);
        return tempManager;
    }

    /**
     * 通过高管流水修改高管 
     */
    public CustMechManager saveCustMechManager(CustMechManagerTmp anManagerTmp) {
        BTAssert.notNull(anManagerTmp, "高管流水编号不允许为空！");
        
        final CustMechManager tempManager = this.selectByPrimaryKey(anManagerTmp.getRefId());
        BTAssert.notNull(tempManager, "对应的高管信息没有找到！");
        
        tempManager.initModifyValue(anManagerTmp);
        this.updateByPrimaryKeySelective(tempManager);
        return tempManager;
    }
}
