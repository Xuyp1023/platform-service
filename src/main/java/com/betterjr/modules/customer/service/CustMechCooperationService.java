package com.betterjr.modules.customer.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.dao.CustMechCooperationMapper;
import com.betterjr.modules.customer.entity.CustMechCooperation;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechCooperationService extends BaseService<CustMechCooperationMapper, CustMechCooperation> {

    private static Logger logger = LoggerFactory.getLogger(CustMechCooperationService.class);

    /**
     * 查询合作企业列表
     * @param anCustNo
     * @return
     */
    public List<CustMechCooperation> queryCustMechCooperationByCustNo(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        return this.selectByProperty("custNo", anCustNo);
    }
    
    /**
     * 查询合作企业信息
     */
    public CustMechCooperation findCustMechCooperation(Long anId) {
        BTAssert.notNull(anId, "合作企业编号不允许为空！");
        
        return this.selectByPrimaryKey(anId);
    }
    
    /**
     * 添加合作企业信息
     * @param anCustMechCooperation
     * @return
     */
    public CustMechCooperation addCustMechCooperation(CustMechCooperation anCustMechCooperation) {
        BTAssert.notNull(anCustMechCooperation, "合作企业信息不允许为空！");
        
        anCustMechCooperation.initAddValue();
        this.insert(anCustMechCooperation);
        return anCustMechCooperation;
    }
    
    /**
     * 保存合作企业信息
     * @param anCustMechCooperation
     * @param anId
     * @return
     */
    public CustMechCooperation saveCustMechCooperation(CustMechCooperation anCustMechCooperation, Long anId) {
        BTAssert.notNull(anId, "合作企业编号不允许为空！");
        BTAssert.notNull(anCustMechCooperation, "合作企业信息不允许为空！");
        
        final CustMechCooperation tempCustMechCooperation = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechCooperation, "对应的合作企业信息没有找到！");
        
        tempCustMechCooperation.initModifyValue(anCustMechCooperation);
        this.updateByPrimaryKeySelective(tempCustMechCooperation);
        return tempCustMechCooperation;
    }

}