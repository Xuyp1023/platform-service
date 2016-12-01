package com.betterjr.modules.customer.service;

import java.util.Map;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.JedisUtils;
import com.betterjr.common.utils.QueryTermBuilder;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.entity.CustInsteadApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustOpenAccountTmp;
import com.betterjr.modules.sms.constants.SmsConstants;
import com.betterjr.modules.sms.entity.VerifyCode;

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
    
    private final static Pattern DEAL_PASSWORD_PATTERN = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,10}$");

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
    public CustInsteadApply wechatAddInsteadApply(Map<String, Object> anMap, Long anId, String anFileList) {
        //取出相应数据
        final String anCustName = (String) anMap.get("custName");
        final String anVerifyCode = (String) anMap.get("verifyCode");
        final String anNewPassword = (String) anMap.get("newPassword");
        final String anOkPassword = (String) anMap.get("okPassword");
        //获取开户信息
        CustOpenAccountTmp anOpenAccountInfo = custOpenaccountTmpService.selectByPrimaryKey(anId);
        BTAssert.notNull(anOpenAccountInfo, "无法获取开户信息！");
        //验证手机验证码
        verifyMobileMessage(anOpenAccountInfo.getOperMobile(), anVerifyCode);
        //保存交易密码
        if (BetterStringUtils.equals(anNewPassword, anOkPassword)) {
            anOpenAccountInfo.setDealPassword(anNewPassword);
        } else {
            BTAssert.notNull(null, "两次输入密码不一致，请检查！");
        }
        if(!DEAL_PASSWORD_PATTERN.matcher(anNewPassword).matches()) {
            BTAssert.notNull(null, "密码为6-18位并包含数字和字母！");
        }
        
        //生成代录申请及代录记录
        CustInsteadApply custInsteadApply = createInsteadApply(anCustName, anFileList);
        
        //生成operOrg
        anOpenAccountInfo.setOperOrg(custInsteadApply.getOperOrg());
        anOpenAccountInfo.setRegOperId(custInsteadApply.getRegOperId());
        anOpenAccountInfo.setRegOperName(custInsteadApply.getRegOperName());
        //更新开户信息数据
        custOpenaccountTmpService.updateByPrimaryKey(anOpenAccountInfo);
        
        // 保存用户选择信息：客户名称、经办人信息。讲信息与申请进行关联
        fillInsteadRecordByAccountTmp(custInsteadApply.getId(), anOpenAccountInfo.getId());

        return custInsteadApply;
    }
    
    /**
     * 生成代录申请
     */
    private CustInsteadApply createInsteadApply(String anCustName, String anFileList) {
        final String insteadType = CustomerConstants.INSTEAD_APPLY_TYPE_OPENACCOUNT;
        final String insteadItems = "0,0,0,0,0,0,0";
        Map<String, Object> queryMap = QueryTermBuilder.newInstance().put("insteadType", insteadType).put("insteadItems", insteadItems).build();
        CustInsteadApply custInsteadApply = custInsteadService.addWeChatInsteadApply(queryMap,anCustName, anFileList);
        return custInsteadApply;
    }

    /**
     * 校验手机验证码
     */
    private void verifyMobileMessage(String anMobile, String anVerifyCode) {

        final VerifyCode verifyCode = JedisUtils.getObject(SmsConstants.smsOpenAccountVerifyCodePrefix + anMobile);
        BTAssert.notNull(verifyCode, "验证码已过期");

        if (BetterStringUtils.equals(verifyCode.getVerifiCode(), anVerifyCode)) {
            JedisUtils.setObject(SmsConstants.smsOpenAccountVerifyCodePrefix + anMobile, "true", SmsConstants.SEC_300);
        } else {
            throw new BytterTradeException(40001, "验证码不正确!");
        }
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

    /**
     * 代录开户激活操作
     */
    public CustInsteadApply saveActiveOpenAccount(Long anId) {
        CustInsteadRecord anInsteadRecord = insteadRecordService.selectByPrimaryKey(anId);
        //调用原有确认开户操作
        custInsteadService.saveConfirmPassInsteadRecord(anId, "代录开户激活");
        //调用原有提交操作
        CustInsteadApply anInsteadApply = custInsteadService.saveSubmitConfirmInsteadApply(anInsteadRecord.getApplyId());
        return anInsteadApply;
    }
}
