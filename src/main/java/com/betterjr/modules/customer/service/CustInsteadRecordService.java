package com.betterjr.modules.customer.service;

import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustInsteadRecordMapper;
import com.betterjr.modules.customer.entity.CustInsteadApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;

/**
 * 代录记录服务
 * 
 * @author liuwl
 *
 */
@Service
public class CustInsteadRecordService extends BaseService<CustInsteadRecordMapper, CustInsteadRecord> {
    
    private final static Pattern INSTEAD_ITEMS_PATTERN = Pattern.compile("^([01],){6}[01]$");
    private final static Pattern INSTEAD_ITEMS_INC_PATTERN = Pattern.compile("^.*1+.*$");
    
    
    @Resource
    private CustInsteadApplyService insteadApplyService;
    
    /**
     * 根据代录申请类型，代录项目生成代录记录
     */
    public void addInsteadRecord(CustInsteadApply anInsteadApply, String anInsteadType, String anInsteadItems) {
        // 0 开户代录 1 变更代录
        if (BetterStringUtils.equals(anInsteadType, CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT)) {
            this.addInsteadRecord(anInsteadApply, CustomerConstants.ITEM_OPENACCOUNT);
        }
        else {
            BTAssert.notNull(anInsteadItems, "代录项目不能为空");
            
            if (INSTEAD_ITEMS_PATTERN.matcher(anInsteadItems).matches() == true) {
                if (INSTEAD_ITEMS_INC_PATTERN.matcher(anInsteadItems).matches() == true) {
                    final String[] tempInsteadItems = BetterStringUtils.split(anInsteadItems, ",");
                    int insteadItemIndex = 0;
                    // 代录项目: 0公司基本信息，1法人信息，2股东信息，3高管信息，4营业执照，5联系人信息，6银行账户, 7开户代录
                    for (String insteadItem : tempInsteadItems) {
                        if (BetterStringUtils.equals(insteadItem, CustomerConstants.ITEM_ENABLED) == true) {
                            // 代录项目: 0公司基本信息，1法人信息，2股东信息，3高管信息，4营业执照，5联系人信息，6银行账户, 7开户代录
                            this.addInsteadRecord(anInsteadApply, String.valueOf(insteadItemIndex));
                        }
                        insteadItemIndex++;
                    }
                }
                else {
                    throw new BytterTradeException(20006, "至少需要一个代录项");
                }
            } else {
                throw new BytterTradeException(20005, "代录项目数据不正确");
            }
        }
    }
    
    /**
     * 根据代录申请类型，代录项目生成代录记录
     */
    public void saveInsteadRecord(CustInsteadApply anInsteadApply, String anInsteadType, String anInsteadItems) {
        // 0 开户代录 1 变更代录
        // 先把之前的代录记录找出来
        List<CustInsteadRecord> insteadRecords = this.queryInsteadRecord(anInsteadApply.getId());
        
        if (BetterStringUtils.equals(anInsteadType, CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT)) {
            CustInsteadRecord insteadRecord = checkExistsInInsteadRecords(CustomerConstants.ITEM_OPENACCOUNT, insteadRecords);
            if (insteadRecord == null) {
                this.addInsteadRecord(anInsteadApply, CustomerConstants.ITEM_OPENACCOUNT);
            } else {
                insteadRecords.remove(insteadRecord);
            }
        }
        else {
            BTAssert.notNull(anInsteadItems, "代录项目不能为空");
            
            if (INSTEAD_ITEMS_PATTERN.matcher(anInsteadItems).matches() == true) {
                if (INSTEAD_ITEMS_INC_PATTERN.matcher(anInsteadItems).matches() == true) {
                    final String[] tempInsteadItems = BetterStringUtils.split(anInsteadItems, ",");
                    int insteadItemIndex = 0;
                    // 代录项目: 0公司基本信息，1法人信息，2股东信息，3高管信息，4营业执照，5联系人信息，6银行账户, 7开户代录
                    for (String tempInsteadItem : tempInsteadItems) {
                        if (BetterStringUtils.equals(tempInsteadItem, CustomerConstants.ITEM_ENABLED) == true) {
                            // 代录项目: 0公司基本信息，1法人信息，2股东信息，3高管信息，4营业执照，5联系人信息，6银行账户, 7开户代录
                            String insteadItem = String.valueOf(insteadItemIndex);
                            CustInsteadRecord insteadRecord = checkExistsInInsteadRecords(insteadItem, insteadRecords);
                            if (insteadRecord == null) {
                                this.addInsteadRecord(anInsteadApply, insteadItem);
                            } else {
                                insteadRecords.remove(insteadRecord);
                            }
                        }
                        insteadItemIndex++;
                    }
                }
                else {
                    throw new BytterTradeException(20006, "至少需要一个代录项");
                }
            } else {
                throw new BytterTradeException(20005, "代录项目数据不正确");
            }
        }
        
        // 删除未使用的数据
        if (Collections3.isEmpty(insteadRecords) == false) {
            for (CustInsteadRecord insteadRecord: insteadRecords) {
                this.deleteByPrimaryKey(insteadRecord.getId());
            }
        }
    }

    /**
     * 添加代录项目 空代录项目
     */
    public CustInsteadRecord addInsteadRecord(CustInsteadApply anInsteadApply, String anInsteadItem) {
        BTAssert.notNull(anInsteadApply, "代录申请不能为空！");
        BTAssert.notNull(anInsteadItem, "代录项目不能为空！");

        final Long applyId = anInsteadApply.getId();
        final Long custNo = anInsteadApply.getCustNo();
        
        final CustInsteadRecord custInsteadRecord = new CustInsteadRecord();
        custInsteadRecord.initAddValue(applyId, custNo, anInsteadItem);

        this.insert(custInsteadRecord);
        return custInsteadRecord;
    }
    
    
    /**
     * 查找代录项目
     */
    public CustInsteadRecord findInsteadRecord(Long anId) {
        BTAssert.notNull(anId, "代录记录编号不能为空！");
        return this.selectByPrimaryKey(anId);
    }
    
    /**
     * 根据代录申请编号找到代录记录 不检查代录申请
     */
    public List<CustInsteadRecord> queryInsteadRecord(Long anApplyId) {
        return this.selectByProperty("applyId", anApplyId);
    }
    
    /**
     * 根据代录申请编号找到代录记录
     */
    public List<CustInsteadRecord> queryInsteadRecordByApplyId(Long anApplyId) {
        // 检查代录申请是否正确
        checkInsteadApply(anApplyId);
        
        return this.selectByProperty("applyId", anApplyId);
    }
    
    /**
     * 保存代录项目
     */
    public CustInsteadRecord saveInsteadRecord(Long anId, String anTmpIds) {
        BTAssert.notNull(anId, "代录项编号不能为空！");
        BTAssert.notNull(anTmpIds, "代录流水编号不能为空！");

        final CustInsteadRecord tempCustInsteadRecord = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustInsteadRecord, "没有找到对应的代录项目！");

        tempCustInsteadRecord.initModifyValue(CustomerConstants.INSTEAD_RECORD_STATUS_TYPE_IN, anTmpIds);

        this.updateByPrimaryKeySelective(tempCustInsteadRecord);
        return tempCustInsteadRecord;
    }
    
    /**
     * 保存代录项目-修改状态
     */
    public CustInsteadRecord saveInsteadRecordStatus(Long anId, String anBusinStatus) {
        BTAssert.notNull(anId, "代录项编号不能为空！");
        BTAssert.notNull(anBusinStatus, "状态不允许为空！");

        final CustInsteadRecord tempCustInsteadRecord = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustInsteadRecord, "没有找到对应的代录项目！");

        tempCustInsteadRecord.initModifyValue(anBusinStatus);

        this.updateByPrimaryKeySelective(tempCustInsteadRecord);
        return tempCustInsteadRecord;
    }

    /**
     * 检查代录申请
     */
    private CustInsteadApply checkInsteadApply(Long anApplyId) {
        BTAssert.notNull(anApplyId, "代录申请编号不能为空！");
        
        final CustInsteadApply insteadApply = insteadApplyService.findCustInsteadApply(anApplyId);
        BTAssert.notNull(insteadApply, "没有找到对应代录申请!");
        
        return insteadApply;
    }
    
    private CustInsteadRecord checkExistsInInsteadRecords(String anInsteadItem, List<CustInsteadRecord> anInsteadRecords) {
        for (CustInsteadRecord insteadRecord: anInsteadRecords) {
            if (BetterStringUtils.equals(insteadRecord.getInsteadItem(), anInsteadItem) == true) {
                return insteadRecord;
            }
        }
        return null;
    }
}