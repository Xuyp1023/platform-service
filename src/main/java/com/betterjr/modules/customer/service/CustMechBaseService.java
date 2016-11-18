package com.betterjr.modules.customer.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.data.SimpleDataEntity;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustAndOperatorRelaService;
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
    @Resource
    private CustAccountService accountService;

    @Resource
    private CustMechBaseTmpService baseTmpService;

    @Resource
    private CustAndOperatorRelaService custAndOpService;

    @Resource
    private CustMechBusinLicenceService businLicenceService;

    @Resource
    private CustRelationService custRelationService;

    /**
     * 公司基本信息-查询详情
     *
     * @param anCustNo
     * @return
     */
    public CustMechBase findCustMechBaseByCustNo(final Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");

        final CustMechBase custMechBase = this.selectByPrimaryKey(anCustNo);

        return custMechBase;
    }

    /**
     * 公司基本信息-修改
     *
     * @param anCustMechBase
     * @return
     */
    public CustMechBase saveCustMechBase(final CustMechBaseTmp anCustMechBaseTmp) {
        BTAssert.notNull(anCustMechBaseTmp, "客户基本信息流不允许为空！");

        final Long custNo = anCustMechBaseTmp.getRefId();

        final CustMechBase tempCustMechBase = this.findCustMechBaseByCustNo(custNo);
        BTAssert.notNull(tempCustMechBase, "对应的客户基本信息没有找到！");

        tempCustMechBase.initModifyValue(anCustMechBaseTmp);
        this.updateByPrimaryKey(tempCustMechBase);

        final CustInfo custInfo = accountService.findCustInfo(custNo);
        custInfo.setCustName(tempCustMechBase.getCustName());

        accountService.updateByPrimaryKeySelective(custInfo);

        // 将所有关系均需要修改
        custRelationService.saveUpdateCustName(custNo, custInfo.getCustName());

        businLicenceService.saveUpdateCustName(custNo, custInfo.getCustName());

        return tempCustMechBase;
    }

    /**
     * 公司基本信息-添加
     *
     * @param anCustMechBase
     * @return
     */
    public CustMechBase addCustMechBase(final CustMechBase anCustMechBase, final Long anCustNo) {
        BTAssert.notNull(anCustNo, "客户编号不允许为空！");
        BTAssert.notNull(anCustMechBase, "客户基本信息不允许为空！");

        // 检查 custNo 是否已经存在
        final CustMechBase tempCustMechBase = findCustMechBaseByCustNo(anCustNo);
        BTAssert.isNull(tempCustMechBase, "客户基本信息已存在，不允许重复录入！");

        final CustInfo custInfo = accountService.selectByPrimaryKey(anCustNo);
        anCustMechBase.initAddValue(anCustNo, custInfo.getCustName(), custInfo.getRegOperId(), custInfo.getRegOperName(), custInfo.getOperOrg());
        this.insert(anCustMechBase);

        // 建立初始流水
        baseTmpService.addCustMechBaseTmp(anCustMechBase);

        return anCustMechBase;
    }

    /**
     * 公司列表
     *
     * @return
     */
    public Collection<CustInfo> queryCustInfo() {
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        return accountService.findCustInfoByOperator(operator.getId(), operator.getOperOrg());
    }

    /**
     * 公司列表 供选择框使用
     *
     * @return
     */
    public Collection<SimpleDataEntity> queryCustInfoSelect() {
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();
        final List<CustInfo> custInfos = accountService.findCustInfoByOperator(operator.getId(), operator.getOperOrg());
        final Collection<SimpleDataEntity> custInfoSelects = new ArrayList<>();

        custInfos.forEach(custInfo -> custInfoSelects.add(new SimpleDataEntity(custInfo.getCustName(), String.valueOf(custInfo.getCustNo()))));

        return custInfoSelects;
    }
}