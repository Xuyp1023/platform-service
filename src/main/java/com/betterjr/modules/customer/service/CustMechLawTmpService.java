package com.betterjr.modules.customer.service;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.customer.dao.CustMechLawTmpMapper;
import com.betterjr.modules.customer.entity.CustMechBaseTmp;
import com.betterjr.modules.customer.entity.CustMechLaw;
import com.betterjr.modules.customer.entity.CustMechLawTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustMechLawTmpService extends BaseService<CustMechLawTmpMapper, CustMechLawTmp>  implements IFormalDataService{
    @Resource
    private CustMechLawService custMechLawService;

    /**
     * 查询公司法人流水信息
     * 
     * @param anCustNo
     * @return
     */
    public CustMechLawTmp findCustMechLawTmpByCustNo(Long anId) {
        BTAssert.notNull(anId, "公司法人流水信息编号不允许为空！");
        
        return this.selectByPrimaryKey(anId);
    }

    /**
     * 保存公司法人流水信息
     * 
     * @param anCustMechLawTmp
     * @return
     */
    public int saveCustMechLawTmp(CustMechLawTmp anCustMechLawTmp, Long anId) {
        BTAssert.notNull(anId, "公司法人流水编号不允许为空！");
        
        final CustMechLawTmp tempCustMechLawTmp = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustMechLawTmp, "没有找到对应的公司法人流水信息！");
        
        tempCustMechLawTmp.initModifyValue(anCustMechLawTmp);
        return this.updateByPrimaryKey(tempCustMechLawTmp);
    }

    /**
     * 添加客户基本信息流水信息
     * 
     * 在初次添加基本信息时也需要添加
     * 
     * @param anCustMechBaseTmp
     * @return
     */
    public CustMechLawTmp addCustMechLawTmpByChange(CustMechLawTmp anCustMechLawTmp, String anTmpType) {
        BTAssert.notNull(anCustMechLawTmp, "客户基本信息流水信息编号不允许为空！");
        // 处理version 查询变更详情的时候使用

        anCustMechLawTmp.initAddValue(anTmpType);
        this.insert(anCustMechLawTmp);

        // 回写
        return anCustMechLawTmp;
    }

    /**
     * 保存数据至正式表
     */
    @Override
    public void saveFormalData(String ... anTmpIds) {
        BTAssert.notEmpty(anTmpIds, "临时流水编号不允许为空！");

        if (anTmpIds.length != 1) {
            throw new BytterTradeException(20021, "临时流水编号只能有一位！");
        }
        
        Long tmpId = Long.valueOf(anTmpIds[0]);
        
        CustMechLawTmp mechLawTmp = this.selectByPrimaryKey(tmpId);
        BTAssert.notNull(mechLawTmp, "没有找到临时流水信息！");
        
        Long id = mechLawTmp.getRefId();
        CustMechLaw custMechLaw = custMechLawService.findCustMechLaw(id);
        
        BTAssert.notNull(custMechLaw, "没有找到正式表数据！");
        
        custMechLaw.initModifyValue(mechLawTmp);
        custMechLawService.saveCustMechLaw(custMechLaw);
    }
}
