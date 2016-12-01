package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.selectkey.SerialGenerator;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.cert.entity.CustCertInfo;
import com.betterjr.modules.cert.service.CustCertService;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.dao.CustInsteadApplyMapper;
import com.betterjr.modules.customer.entity.CustInsteadApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.document.service.CustFileItemService;

/**
 * 代录申请
 *
 * @author liuwl
 *
 */
@Service
public class CustInsteadApplyService extends BaseService<CustInsteadApplyMapper, CustInsteadApply> {
    @Resource
    private CustAccountService accountService;

    @Resource
    private CustCertService custCertService;

    @Resource
    private CustInsteadRecordService insteadRecordService;

    @Resource
    private CustFileItemService fileItemService;

    /**
     * 添加代录申请 检查是否已经有申请在进行中
     *
     * @param anCustInsteadApply
     * @return
     */
    public CustInsteadApply addCustInsteadApply(final String anInsteadType, final Long anCustNo, final String anFileList) {
        if (BetterStringUtils.isBlank(anInsteadType) == true) {
            throw new BytterTradeException(20061, "代录申请类型不允许为空！");
        }

        final CustInsteadApply custInsteadApply = new CustInsteadApply();
        if (anInsteadType.equals(CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT) == true) {
            custInsteadApply.initAddValue(anInsteadType, null, null);
        }
        else {// 变更代录 需要 custNo 和 custName

            // TODO @@@@@@@@ 检查是否有正在进行的变更 代录

            BTAssert.notNull(anCustNo, "客户编号不能为空！");

            if (checkExistActiveInsteadApply(anCustNo) == true) {
                throw new BytterTradeException(20062, "所选客户有正在进行的代录申请！");
            }

            final String custName = accountService.queryCustName(anCustNo);
            custInsteadApply.initAddValue(anInsteadType, anCustNo, custName);
        }

        final CustCertInfo certInfo = custCertService.findCertByOperOrg(UserUtils.getOperatorInfo().getOperOrg());

//        BTAssert.notNull(certInfo, "证书信息不允许为空!");

//        custInsteadApply.setOrgName(certInfo.getCustName());
        //微信
        if(null != certInfo) {
            custInsteadApply.setOrgName(certInfo.getCustName());
        }

        custInsteadApply.setBatchNo(fileItemService.updateCustFileItemInfo(anFileList, custInsteadApply.getBatchNo()));

        this.insert(custInsteadApply);
        return custInsteadApply;
    }
    
    /**
     * 微信端代录申请,无证书
     * !!-- 在此处生成operId、OperName、OperOrg --!!
     */
    public CustInsteadApply addWeChatCustInsteadApply(final String anInsteadType, final String anCustName, final String anFileList) {
        if (BetterStringUtils.isBlank(anInsteadType) == true) {
            throw new BytterTradeException(20061, "代录申请类型不允许为空！");
        }
        final CustInsteadApply custInsteadApply = new CustInsteadApply();
        
        if (anInsteadType.equals(CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT) == true) {
            custInsteadApply.initAddValue(anInsteadType, null, null);
        }
        
        //更新custName,生成相应微信不存在信息
        custInsteadApply.setOperOrg(custInsteadApply.getCustName() + SerialGenerator.randomBase62(10));
        custInsteadApply.setCustName(anCustName);
        custInsteadApply.setRegOperId(SerialGenerator.getLongValue(SerialGenerator.OPERATOR_ID));
        custInsteadApply.setRegOperName(anCustName);
        //将文件拷贝再拷贝一份保存
        copyOpenAccountFile(custInsteadApply, anFileList);
        
        //微信生成默认operOrg和operName
        custInsteadApply.setOperOrg(custInsteadApply.getCustName() + SerialGenerator.randomBase62(10));
        custInsteadApply.setOrgName(custInsteadApply.getCustName());

        this.insert(custInsteadApply);
        return custInsteadApply;
    }
    

    /**
     * 将文件拷贝再拷贝一分保存
     */
    private void copyOpenAccountFile(CustInsteadApply anCustInsteadApply, String anFileList) {
        //构造操作员信息，作为调用接口使用
        CustOperatorInfo anOperator = new CustOperatorInfo();
        anOperator.setName(anCustInsteadApply.getRegOperName());
        anOperator.setId(anCustInsteadApply.getRegOperId());
        anOperator.setOperOrg(anCustInsteadApply.getOperOrg());
        
        Long anBatchNo = fileItemService.updateAndDuplicateConflictFileItemInfo(anFileList, anCustInsteadApply.getBatchNo(), anOperator);
        anCustInsteadApply.setBatchNo(anBatchNo);
    }

    /**
     *
     * @param anCustNo
     * @return
     */
    public Boolean checkExistActiveInsteadApply(final Long anCustNo) {
        final Map<String, Object> conditionMap = new HashMap<>();

        conditionMap.put(CustomerConstants.CUST_NO, anCustNo);
        // INSTEAD_APPLY_STATUS_CONFIRM_PASS 这两种状态表明 此申请已经完成 或者 取消
        // INSTEAD_APPLY_STATUS_CANCEL
        final String[] businStatues = {
                CustomerConstants.INSTEAD_APPLY_STATUS_NEW,
                CustomerConstants.INSTEAD_APPLY_STATUS_AUDIT_PASS,
                CustomerConstants.INSTEAD_APPLY_STATUS_AUDIT_REJECT,
                CustomerConstants.INSTEAD_APPLY_STATUS_TYPE_IN,
                CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_PASS,
                CustomerConstants.INSTEAD_APPLY_STATUS_REVIEW_REJECT,
                CustomerConstants.INSTEAD_APPLY_STATUS_CONFIRM_REJECT };
        conditionMap.put("businStatus", businStatues);
        return Collections3.isEmpty(this.selectByProperty(conditionMap)) == false;
    }

    /**
     * 查询代录申请
     *
     * @param anId
     * @return
     */
    public CustInsteadApply findCustInsteadApply(final Long anId) {
        BTAssert.notNull(anId, "编号不允许为空！");

        final CustInsteadApply insteadApply = this.selectByPrimaryKey(anId);

        final List<CustInsteadRecord> insteadRecords = insteadRecordService.queryInsteadRecord(insteadApply.getId());
        insteadApply.setInsteadItems(generateInsteadItems(insteadRecords));

        return insteadApply;
    }

    private String generateInsteadItems(final List<CustInsteadRecord> anInsteadRecords) {
        final StringBuilder sb = new StringBuilder();
        sb.append(getInsteadItem(anInsteadRecords, CustomerConstants.ITEM_BASE)).append(",");
        sb.append(getInsteadItem(anInsteadRecords, CustomerConstants.ITEM_LAW)).append(",");
        sb.append(getInsteadItem(anInsteadRecords, CustomerConstants.ITEM_SHAREHOLDER)).append(",");
        sb.append(getInsteadItem(anInsteadRecords, CustomerConstants.ITEM_MANAGER)).append(",");
        sb.append(getInsteadItem(anInsteadRecords, CustomerConstants.ITEM_BUSINLICENCE)).append(",");
        sb.append(getInsteadItem(anInsteadRecords, CustomerConstants.ITEM_CONTACTER)).append(",");
        sb.append(getInsteadItem(anInsteadRecords, CustomerConstants.ITEM_BANKACCOUNT));
        return sb.toString();
    }

    private String getInsteadItem(final List<CustInsteadRecord> anInsteadRecords, final String anItem) {
        boolean flag = false;
        for (final CustInsteadRecord insteadRecord : anInsteadRecords) {
            if (insteadRecord.getInsteadItem().equals(anItem) == true) {
                flag = true;
                break;
            }
        }

        return flag ? "1" : "0";
    }

    /**
     * 修改代录申请及文件列表
     */
    public CustInsteadApply saveCustInsteadApply(final CustInsteadApply anInsteadApply, final String anFileList) {
        BTAssert.notNull(anInsteadApply, "代录申请不允许为空！");

        final CustInsteadApply tempInsteadApply = this.selectByPrimaryKey(anInsteadApply.getId());
        BTAssert.notNull(tempInsteadApply, "没有找到对应的代录申请！");

        tempInsteadApply.initModifyValue(anInsteadApply);

        final Long batchNo = anInsteadApply.getBatchNo();

        tempInsteadApply.setBatchNo(fileItemService.updateAndDelCustFileItemInfo(anFileList, anInsteadApply.getBatchNo()));

        this.updateByPrimaryKeySelective(tempInsteadApply);
        return tempInsteadApply;
    }

    /**
     * 修改代录申请状态
     */
    public CustInsteadApply saveCustInsteadApplyStatus(final Long anId, final String anBusinStatus) {
        BTAssert.notNull(anId, "编号不允许为空！");
        BTAssert.notNull(anBusinStatus, "状态不允许为空！");

        final CustInsteadApply tempInsteadApply = this.selectByPrimaryKey(anId);
        BTAssert.notNull(tempInsteadApply, "没有找到对应的代录申请！");

        tempInsteadApply.initModifyValue(anBusinStatus);
        this.updateByPrimaryKeySelective(tempInsteadApply);
        return tempInsteadApply;
    }

    /**
     * 查询代表列表 平台使用
     *
     * @return
     */
    public Page<CustInsteadApply> queryCustInsteadApply(final Map<String, Object> anParam, final int anFlag, final int anPageNum, final int anPageSize) {
        final Object custName = anParam.get("LIKEcustName");
        final Object businStatus = anParam.get("businStatus");
        if (custName == null || BetterStringUtils.isBlank((String) custName)) {
            anParam.remove("LIKEcustName");
        }
        else {
            anParam.put("LIKEcustName", "%" + custName + "%");
        }
        if (businStatus == null || (businStatus instanceof String && BetterStringUtils.isBlank((String) businStatus))) {
            anParam.remove("businStatus");
        }
        return this.selectPropertyByPage(CustInsteadApply.class, anParam, anPageNum, anPageSize, anFlag == 1);
    }

    /**
     * 回写 custNo, custName
     * @param applyId
     * @param custName
     */
    public CustInsteadApply saveCustInsteadApplyCustInfo(final Long anId, final Long anCustNo, final String anCustName) {
        final CustInsteadApply tempInsteadApply = this.selectByPrimaryKey(anId);

        BTAssert.notNull(tempInsteadApply, "没有找到对应的代录申请！");

        if (anCustNo != null) {
            tempInsteadApply.setCustNo(anCustNo);
        }
        if (BetterStringUtils.isNotBlank(anCustName)) {
            tempInsteadApply.setCustName(anCustName);
        }

        this.updateByPrimaryKeySelective(tempInsteadApply);

        return tempInsteadApply;
    }
    
    /**
     * 根据开户信息tmp id 查询开户申请
     */
    public CustInsteadApply findInsteadApplyByAccountTmpId(Long anId) {
        CustInsteadRecord anRecord = Collections3.getFirst(insteadRecordService.selectByProperty("tmpIds", anId));
        if (anRecord == null) {
            return null;
        }
        CustInsteadApply custApply = this.selectByPrimaryKey(anRecord.getApplyId());
        return custApply;
    }

}