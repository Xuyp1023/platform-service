package com.betterjr.modules.customer.service;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.dao.CustMechCooperationMapper;
import com.betterjr.modules.customer.entity.CustMechCooperation;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechCooperationService extends BaseService<CustMechCooperationMapper, CustMechCooperation> {


    /**
     * 查询合作企业列表
     * @param anCustNo
     * @return
     */
    public Page<CustMechCooperation> queryCustMechCooperationByCustNo(Long anCustNo, String anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        return this.selectPropertyByPage("custNo", anCustNo, anPageNum, anPageSize, "1".equals(anFlag));
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
    
    /**
     * 删除合作企业信息
     */
    public int saveDeleteCustMechCooperation(Long anId) {
        BTAssert.notNull(anId, "合作企业编号不允许为空！");
        return this.deleteByPrimaryKey(anId);
    }

}