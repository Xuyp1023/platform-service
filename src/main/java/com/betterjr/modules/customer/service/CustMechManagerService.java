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

/**
 * 高管信息管理
 * @author liuwl
 *
 */
@Service
public class CustMechManagerService extends BaseService<CustMechManagerMapper, CustMechManager> {

    private static Logger logger = LoggerFactory.getLogger(CustMechManagerService.class);
    
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
        
        return this.selectByPrimaryKey(anId);
    }
    
    /**
     * 添加高管信息
     * @param anCustMechManager
     * @return
     */
    public CustMechManager addCustMechManager(CustMechManager anCustMechManager) {
        BTAssert.notNull(anCustMechManager, "高管信息不允许为空！");
        
        anCustMechManager.initAddValue();
        this.insert(anCustMechManager);
        return anCustMechManager;
    }
    
    /**
     * 保存高管信息
     * @param anCustMechManager
     * @param anId
     * @return
     */
    public CustMechManager saveCustMechManager(CustMechManager anCustMechManager, Long anId) {
        BTAssert.notNull(anId, "高管编号不允许为空！");
        BTAssert.notNull(anCustMechManager, "高管信息不允许为空！");
        
        final CustMechManager tempCustMechManager = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechManager, "对应的高管信息没有找到！");
        
        tempCustMechManager.initModifyValue(anCustMechManager);
        this.updateByPrimaryKeySelective(tempCustMechManager);
        return tempCustMechManager;
    }

}
