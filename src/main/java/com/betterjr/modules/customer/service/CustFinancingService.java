package com.betterjr.modules.customer.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.dao.CustFinancingMapper;
import com.betterjr.modules.customer.entity.CustFinancing;

/**
 * 融资情况
 * @author liuwl
 *
 */
@Service
public class CustFinancingService extends BaseService<CustFinancingMapper, CustFinancing> {

    private static Logger logger = LoggerFactory.getLogger(CustFinancingService.class);

    /**
     * 查询融资情况列表
     * @param anCustNo
     * @return
     */
    public List<CustFinancing> queryCustFinancingByCustNo(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        
        return this.selectByProperty("custNo", anCustNo);
    }
    
    /**
     * 查询融资情况信息
     */
    public CustFinancing findCustFinancing(Long anId) {
        BTAssert.notNull(anId, "融资情况编号不允许为空！");
        
        return this.selectByPrimaryKey(anId);
    }
    
    /**
     * 添加融资情况信息
     * @param anCustFinancing
     * @return
     */
    public CustFinancing addCustFinancing(CustFinancing anCustFinancing) {
        BTAssert.notNull(anCustFinancing, "融资情况信息不允许为空！");
        
        anCustFinancing.initAddValue();
        this.insert(anCustFinancing);
        return anCustFinancing;
    }
    
    /**
     * 保存融资情况信息
     * @param anCustFinancing
     * @param anId
     * @return
     */
    public CustFinancing saveCustFinancing(CustFinancing anCustFinancing, Long anId) {
        BTAssert.notNull(anId, "融资情况编号不允许为空！");
        BTAssert.notNull(anCustFinancing, "融资情况信息不允许为空！");
        final CustFinancing tempCustFinancing = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustFinancing, "对应的融资情况信息没有找到！");
        
        tempCustFinancing.initModifyValue(anCustFinancing);
        this.updateByPrimaryKeySelective(tempCustFinancing);
        return tempCustFinancing;
    }

}