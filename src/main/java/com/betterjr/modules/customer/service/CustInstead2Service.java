package com.betterjr.modules.customer.service;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.selectkey.SerialGenerator;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.QueryTermBuilder;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.entity.CustInsteadApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustOpenAccountTmp;

/**
 * 代录服务
 */
@Service
public class CustInstead2Service {

    @Resource
    private CustInsteadRecordService insteadRecordService;
    @Resource
    private CustInsteadApplyService insteadApplyService;
    @Autowired
    private CustOperatorService custOperatorService;
    @Autowired
    private CustOpenAccountTmp2Service custOpenaccountTmpService;
    @Autowired
    private CustInsteadService custInsteadService;

    /**
     * PC端发起代录申请
     */
    public CustInsteadApply addInsteadApply(final String anCustName, final Long anOperId, final String anFileList) {
        // pc端默认值
        final String insteadType = CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT;
        final String insteadItems = "0,0,0,0,0,0,0";
        Map<String, Object> anMap = QueryTermBuilder.newInstance().put("insteadType", insteadType).put("insteadItems", insteadItems).build();
        CustInsteadApply custInsteadApply = custInsteadService.addInsteadApply(anMap, anFileList);
        //更新custName
        custInsteadApply.setCustName(anCustName);
        insteadApplyService.updateByPrimaryKeySelective(custInsteadApply);
        // 保存用户选择信息：客户名称、经办人信息
        CustOpenAccountTmp anCustOpenAccountTmp = this.addOpenAccountTmp(anCustName, anOperId);
        // 将开户信息保存至开户申请记录中
        fillInsteadRecordByAccountTmp(custInsteadApply.getId(), anCustOpenAccountTmp.getId());

        return custInsteadApply;
    }
    
    /**
     * 微信端代录申请
     * !!-- 在此处生成operId、OperName、OperOrg --!!
     */
    public CustInsteadApply wechatAddInsteadApply(String anCustName, Long anId, String anFileList) {
        final String insteadType = CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT;
        final String insteadItems = "0,0,0,0,0,0,0";
        Map<String, Object> anMap = QueryTermBuilder.newInstance().put("insteadType", insteadType).put("insteadItems", insteadItems).build();
        CustInsteadApply custInsteadApply = custInsteadService.addWeChatInsteadApply(anMap,anCustName, anFileList);
        
        //更新custName,生成相应微信不存在信息
        custInsteadApply.setCustName(anCustName);
        custInsteadApply.setRegOperId(SerialGenerator.getLongValue(SerialGenerator.OPERATOR_ID));
        custInsteadApply.setRegOperName(anCustName);
        insteadApplyService.updateByPrimaryKeySelective(custInsteadApply);
        
        //生成operOrg
        CustOpenAccountTmp anOpenAccountInfo = custOpenaccountTmpService.selectByPrimaryKey(anId);
        anOpenAccountInfo.setOperOrg(anOpenAccountInfo.getCustName() + SerialGenerator.randomBase62(10));
        anOpenAccountInfo.setRegOperId(custInsteadApply.getRegOperId());
        anOpenAccountInfo.setRegOperName(custInsteadApply.getRegOperName());
        custOpenaccountTmpService.updateByPrimaryKey(anOpenAccountInfo);
        
        // 保存用户选择信息：客户名称、经办人信息
        fillInsteadRecordByAccountTmp(custInsteadApply.getId(), anOpenAccountInfo.getId());

        return custInsteadApply;
    }
    /**
     * 根据开户信息tmp id 查询开户申请
     */
    public CustInsteadApply findInsteadApplyByAccountTmpId(Long anId) {
        return insteadApplyService.findInsteadApplyByAccountTmpId(anId);
    }

    /**
     * 将开户信息保存至开户申请记录
     */
    private void fillInsteadRecordByAccountTmp(Long anApplyId, Long anAccountTmpid) {
        // 查询对应insteadRecord
        Map<String, Object> anMap = QueryTermBuilder.newInstance().put("applyId", anApplyId).put("insteadItem", CustomerConstants.ITEM_OPENACCOUNT)
                .build();
        CustInsteadRecord insteadRecord = Collections3.getFirst(insteadRecordService.selectByProperty(anMap));
        insteadRecord.setTmpIds(anAccountTmpid.toString());
        insteadRecordService.updateByPrimaryKeySelective(insteadRecord);
    }

    /**
     * 保存PC端填写开户数据至信息表
     */
    private CustOpenAccountTmp addOpenAccountTmp(String anCustName, Long anOperId) {
        CustOpenAccountTmp anCustOpenAccountTmp = new CustOpenAccountTmp();
        anCustOpenAccountTmp.initAddValue();
        anCustOpenAccountTmp.setCustName(anCustName);
        CustOperatorInfo anOperator = custOperatorService.selectByPrimaryKey(anOperId);
        // 经办人信息
        anCustOpenAccountTmp.setOperName(anOperator.getName());
        anCustOpenAccountTmp.setOperIdenttype(anOperator.getIdentType());
        anCustOpenAccountTmp.setOperIdentno(anOperator.getIdentNo());
        anCustOpenAccountTmp.setOperValiddate(anOperator.getValidDate());
        anCustOpenAccountTmp.setOperMobile(anOperator.getMobileNo());
        anCustOpenAccountTmp.setOperEmail(anOperator.getEmail());
        anCustOpenAccountTmp.setOperPhone(anOperator.getPhone());
        anCustOpenAccountTmp.setOperFaxNo(anOperator.getFaxNo());
        custOpenaccountTmpService.insert(anCustOpenAccountTmp);
        return anCustOpenAccountTmp;
    }
}
