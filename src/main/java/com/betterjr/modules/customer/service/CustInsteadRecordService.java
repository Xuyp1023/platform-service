package com.betterjr.modules.customer.service;

import java.util.List;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
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
    
    private final static String INSTEAD_ITEMS_REGEX = "^([01],){6}[01]$";
    private final static String INSTEAD_ITEMS_INC_REGEX = "^.*1+.*$";
    private final static Pattern INSTEAD_ITEMS_PATTERN;
    private final static Pattern INSTEAD_ITEMS_INC_PATTERN;
    
    static {
        INSTEAD_ITEMS_PATTERN = Pattern.compile(INSTEAD_ITEMS_REGEX);
        INSTEAD_ITEMS_INC_PATTERN = Pattern.compile(INSTEAD_ITEMS_INC_REGEX);
    }
    
    @Resource
    private CustInsteadApplyService insteadApplyService;
    
    /**
     * 根据代录申请类型，代录项目生成代录记录
     */
    public void addCustInsteadRecord(CustInsteadApply anInsteadApply, String anInsteadType, String anInsteadItems) {
        // 0 开户代录 1 变更代录
        if (BetterStringUtils.equals(anInsteadType, CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT)) {
            this.addCustInsteadRecord(anInsteadApply, CustomerConstants.ITEM_OPENACCOUNT);
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
                            this.addCustInsteadRecord(anInsteadApply, String.valueOf(insteadItemIndex));
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
     * 添加代录项目 空代录项目
     */
    public CustInsteadRecord addCustInsteadRecord(CustInsteadApply anInsteadApply, String anInsteadItem) {
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
    public CustInsteadRecord findCustInsteadRecord(Long anId) {
        BTAssert.notNull(anId, "代录记录编号不能为空！");
        return this.selectByPrimaryKey(anId);
    }
    
    /**
     * 根据代录申请编号找到代录记录
     */
    public List<CustInsteadRecord> findCustInsteadRecordByApplyId(Long anApplyId) {
        // 检查代录申请是否正确
        checkInsteadApply(anApplyId);
        
        return this.selectByProperty("applyId", anApplyId);
    }
    
    /**
     * 保存代录项目暂存
     * 
     * @return
     */
    public CustInsteadRecord saveCustInsteadRecordTmp(Long anId, String anTmpIds) {
        BTAssert.notNull(anId, "代录项编号不能为空！");
        BTAssert.notNull(anTmpIds, "代录流水编号不能为空！");

        final CustInsteadRecord tempCustInsteadRecord = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempCustInsteadRecord, "没有找到对应的代录项目！");

        tempCustInsteadRecord.initModifyValue(CustomerConstants.INSTEAD_RECORD_STATUS_NEW, anTmpIds);

        this.updateByPrimaryKeySelective(tempCustInsteadRecord);
        return tempCustInsteadRecord;
    }

    /**
     * 保存代录项目
     */
    public CustInsteadRecord saveCustInsteadRecord(Long anId, String anTmpIds) {
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
    public CustInsteadRecord saveCustInsteadRecordStatus(Long anId, String anBusinStatus) {
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
}