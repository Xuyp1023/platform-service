// Copyright (c) 2014-2016 Betty. All rights reserved.
// ============================================================================
// CURRENT VERSION
// ============================================================================
// CHANGE LOG
// V2.0 : 2016年9月19日, liuwl, creation
// ============================================================================
package com.betterjr.modules.operator.dubbo;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.data.CustPasswordType;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.JedisUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.operator.ITradePassService;
import com.betterjr.modules.sms.constants.SmsConstants;
import com.betterjr.modules.sms.dubbo.interfaces.IVerificationCodeService;
import com.betterjr.modules.sms.entity.VerifyCode;
import com.betterjr.modules.sms.util.VerifyCodeType;
import com.betterjr.modules.wechat.service.CustWeChatService;

/**
 * @author liuwl
 *
 */
@Service(interfaceClass = ITradePassService.class)
public class TradePassDubboService implements ITradePassService {

    @Reference(interfaceClass = IVerificationCodeService.class)
    private IVerificationCodeService verificationCodeService;

    @Resource
    private CustWeChatService wechatService;

    @Resource
    private CustOperatorService custOperatorService;


    /* (non-Javadoc)
     * @see com.betterjr.modules.operator.ITradePassService#sendVerifyCode()
     */
    @Override
    public String webSendVerifyCode() {
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();

        final CustOperatorInfo  tempOperator = custOperatorService.findCustOperatorInfo(operator.getId());

        final String mobile = tempOperator.getMobileNo();

        BTAssert.isTrue(BetterStringUtils.isNotBlank(mobile), "经办人手机号码不允许为空！");

        final VerifyCode verifyCode = verificationCodeService.sendVerifyCode(mobile, VerifyCodeType.CHANGE_TRADE_PASSWORD);
        BTAssert.notNull(verifyCode, "没有生成验证码！");

        JedisUtils.setObject(SmsConstants.smsModifyTradePassVerifyCodePrefix + operator.getId(), verifyCode, SmsConstants.SEC_300);
        return AjaxObject.newOk("发送验证码成功").toJson();
    }

    /* (non-Javadoc)
     * @see com.betterjr.modules.operator.ITradePassService#checkVerifiCode(java.lang.String)
     */
    @Override
    public String webCheckVerifyCode(final String anVerifyCode) {
        final CustOperatorInfo operator = UserUtils.getOperatorInfo();

        final VerifyCode verifyCode = JedisUtils.getObject(SmsConstants.smsModifyTradePassVerifyCodePrefix + operator.getId());

        BTAssert.notNull(verifyCode, "验证码已过期");

        if (BetterStringUtils.equals(verifyCode.getVerifiCode(), anVerifyCode)) {
            JedisUtils.setObject(SmsConstants.smsModifyTradePassVerifyCodePrefix + operator.getId(), verifyCode, SmsConstants.SEC_600);

            return AjaxObject.newOk("验证码验证成功").toJson();
        } else {
            return AjaxObject.newError("验证码不匹配").toJson();
        }
    }

    /* (non-Javadoc)
     * @see com.betterjr.modules.wechat.dubbo.interfaces.ICustWeChatService#saveMobileTradePass(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String webSaveMobileTradePass(final String anNewPasswd, final String anOkPasswd, final String anLoginPasswd) {
        return AjaxObject.newOk("交易密码保存成功", wechatService.saveMobileTradePass(anNewPasswd, anOkPasswd, anLoginPasswd, CustPasswordType.ORG)).toJson();
    }

    /* (non-Javadoc)
     * @see com.betterjr.modules.operator.ITradePassService#saveModifyTradePass(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String webSaveModifyTradePass(final String anNewPassword, final String anOkPassword, final String anOldPassword) {
        return AjaxObject.newOk("交易密码保存成功", wechatService.saveModifyTradePass(anNewPassword, anOkPassword, anOldPassword)).toJson();
    }

}
