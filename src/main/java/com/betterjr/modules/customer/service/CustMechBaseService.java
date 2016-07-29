package com.betterjr.modules.customer.service;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.modules.customer.dao.CustMechBaseMapper;
import com.betterjr.modules.customer.entity.CustMechBase;
import com.betterjr.modules.customer.entity.CustMechBaseTmp;

/**
 * 客户基本信息管理
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechBaseService extends BaseService<CustMechBaseMapper, CustMechBase> {
    /**
     * 公司基本信息-查询详情
     * 
     * @param anCustNo
     * @return
     */
    public CustMechBase findCustMechBaseByCustNo(Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");

        CustMechBase custMechBase = this.selectByPrimaryKey(anCustNo);
        BTAssert.notNull(custMechBase, "没有找到公司基本信息!");

        return custMechBase;
    }

    /**
     * 公司基本信息-修改
     * 
     * @param anCustMechBase
     * @return
     */
    public CustMechBase saveCustMechBase(CustMechBaseTmp anCustMechBaseTmp) {
        BTAssert.notNull(anCustMechBaseTmp, "客户基本信息流不允许为空！");

        Long custNo = anCustMechBaseTmp.getRefId();

        final CustMechBase tempCustMechBase = this.findCustMechBaseByCustNo(custNo);
        BTAssert.notNull(tempCustMechBase, "对应的客户基本信息没有找到！");

        tempCustMechBase.initModifyValue(anCustMechBaseTmp);
        this.updateByPrimaryKey(tempCustMechBase);

        return tempCustMechBase;
    }

    /**
     * 公司基本信息-添加
     * 
     * @param anCustMechBase
     * @return
     */
    public CustMechBase addCustMechBase(CustMechBase anCustMechBase, Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        BTAssert.notNull(anCustMechBase, "客户基本信息不允许为空！");

        // 检查 custNo 是否已经存在
        CustMechBase tempCustMechBase = findCustMechBaseByCustNo(anCustNo);
        BTAssert.isNull(tempCustMechBase, "客户基本信息已存在，不允许重复录入！");

        anCustMechBase.initAddValue(anCustNo);
        this.insert(anCustMechBase);
        return anCustMechBase;
    }

}