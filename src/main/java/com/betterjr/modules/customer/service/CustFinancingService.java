package com.betterjr.modules.customer.service;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.dao.CustFinancingMapper;
import com.betterjr.modules.customer.entity.CustFinancing;

/**
 * 融资情况
 * @author liuwl
 *
 */
@Service
public class CustFinancingService extends BaseService<CustFinancingMapper, CustFinancing> {

    /**
     * 查询融资情况列表
     * @param anCustNo
     * @return
     */
    public Page<CustFinancing> queryCustFinancingByCustNo(Long anCustNo, String anFlag, int anPageNum, int anPageSize) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");

        return this.selectPropertyByPage("custNo", anCustNo, anPageNum, anPageSize, "1".equals(anFlag));
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

    /**
     * 删除融资情况信息
     */
    public int saveDeleteCustFinancing(Long anId) {
        BTAssert.notNull(anId, "融资情况编号不允许为空！");
        BTAssert.notNull(this.selectByPrimaryKey(anId), "不存在对应融资情况");
        return this.deleteByPrimaryKey(anId);
    }

}