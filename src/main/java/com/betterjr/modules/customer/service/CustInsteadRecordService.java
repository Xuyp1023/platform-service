package com.betterjr.modules.customer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.modules.customer.dao.CustInsteadRecordMapper;
import com.betterjr.modules.customer.entity.CustInsteadRecord;

/**
 * 代录记录服务
 * 
 * @author liuwl
 *
 */
@Service
public class CustInsteadRecordService extends BaseService<CustInsteadRecordMapper, CustInsteadRecord> {
    private static Logger logger = LoggerFactory.getLogger(CustInsteadRecordService.class);

    /**
     * 添加代录项目 空项目
     * 
     * @param anRefId
     * @param anChangeItemBase
     * @param anValueOf
     * @return
     */
    public CustInsteadRecord addCustInsteadRecord(Long anApplyId, String anInsteadItem) {
        BTAssert.notNull(anApplyId, "代录申请编号不能为空！");
        BTAssert.notNull(anInsteadItem, "代录项目不能为空！");

        final CustInsteadRecord custInsteadRecord = new CustInsteadRecord();
        custInsteadRecord.initAddValue(anApplyId, anInsteadItem);

        this.insert(custInsteadRecord);
        return custInsteadRecord;
    }

    /**
     * 查找代录项目
     * 
     * @param anCustNo
     * @param anId
     * @return
     */
    public CustInsteadRecord findCustInsteadRecord(Long anId) {
        BTAssert.notNull(anId, "代录记录编号不能为空！");
        return this.selectByPrimaryKey(anId);
    }

    /**
     * 保存代录项目
     * 
     * @return
     */
    public CustInsteadRecord saveCustInsteadRecord(Long anId, String anInsteadItem, String anTmpIds) {
        BTAssert.notNull(anId, "代录项编号不能为空！");
        BTAssert.notNull(anInsteadItem, "代录项目不能为空！");
        BTAssert.notNull(anTmpIds, "代录流水编号不能为空！");

        final CustInsteadRecord tempCustInsteadRecord = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustInsteadRecord, "没有找到对应的代录项目！");

        final String insteadItem = tempCustInsteadRecord.getInsteadItem();
        if (BetterStringUtils.equals(anInsteadItem, insteadItem) == false) {
            throw new BytterTradeException(20006, "代录项目不匹配！");
        }

        tempCustInsteadRecord.setTmpIds(anTmpIds);
        tempCustInsteadRecord.initModifyValue();

        this.updateByPrimaryKeySelective(tempCustInsteadRecord);
        return tempCustInsteadRecord;
    }

}