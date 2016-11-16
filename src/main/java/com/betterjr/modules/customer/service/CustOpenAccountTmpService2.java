package com.betterjr.modules.customer.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.blacklist.service.BlacklistService;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.dao.CustOpenAccountTmpMapper;
import com.betterjr.modules.customer.entity.CustOpenAccountTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;
import com.betterjr.modules.customer.service.CustMechBankAccountService;
import com.betterjr.modules.customer.service.CustMechBaseService;
import com.betterjr.modules.document.service.CustFileItemService;

@Service
public class CustOpenAccountTmpService2 extends BaseService<CustOpenAccountTmpMapper, CustOpenAccountTmp> implements IFormalDataService {
    
    @Autowired
    private CustAccountService custAccountService;
    @Autowired
    private CustMechBaseService custMechBaseService;
    @Autowired
    private CustMechBankAccountService custMechBankAccountService;
    @Autowired
    private CustOperatorService custOperatorService;
    @Autowired
    private BlacklistService blacklistService;
    @Autowired
    private CustFileItemService custFileItemService;

    /**
     * 开户申请提交
     */
    public CustOpenAccountTmp saveOpenAccountApply(final CustOpenAccountTmp anOpenAccountInfo, final String anFileList) {
        logger.info("Begin to Commit Open Account Apply");
        // 检查开户资料合法性
        checkAccountInfoValid(anOpenAccountInfo);
        // 初始化参数设置
        initAddValue(anOpenAccountInfo, CustomerConstants.TMP_TYPE_TEMPSTORE, CustomerConstants.TMP_STATUS_NEW);
        // 处理附件
        anOpenAccountInfo.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anOpenAccountInfo.getBatchNo()));
        // 数据存盘,开户资料暂存
        this.insert(anOpenAccountInfo);
        return anOpenAccountInfo;
    }
    
    private void initAddValue(final CustOpenAccountTmp anOpenAccountInfo, final String anTmpType, final String anBusinStatus) {
        anOpenAccountInfo.initAddValue();
        // 设置类型:自己暂存/平台操作员代录时暂存
        anOpenAccountInfo.setTmpType(anTmpType);
        // 设置状态为使用中
        anOpenAccountInfo.setBusinStatus(anBusinStatus);
        anOpenAccountInfo.setLastStatus(anBusinStatus);
        // 营业执照
        initIdentInfo(anOpenAccountInfo);
    }

    /**
     * 生成营业执照
     */
    private void initIdentInfo(final CustOpenAccountTmp anOpenAccountInfo) {
        anOpenAccountInfo.setIdentNo(anOpenAccountInfo.getBusinLicence());
        //证件类型
        anOpenAccountInfo.setIdentType("1");
        anOpenAccountInfo.setValidDate(anOpenAccountInfo.getBusinLicenceValidDate());
    }
    
    public void checkAccountInfoValid(final CustOpenAccountTmp anOpenAccountInfo) {
        // 检查开户资料入参
        checkAccountInfoParams(anOpenAccountInfo);

        // 检查申请机构名称是否存在
        if (checkCustExistsByCustName(anOpenAccountInfo.getCustName()) == true) {
            logger.warn("申请机构名称已存在");
            throw new BytterTradeException(40001, "申请机构名称已存在");
        }

        // 检查组织机构代码证是否存在
        if (checkCustExistsByIdentNo(anOpenAccountInfo.getOrgCode()) == true) {
            logger.warn("组织机构代码证已存在");
            throw new BytterTradeException(40001, "组织机构代码证已存在");
        }

        // 检查营业执照号码是否存在
        if (checkCustExistsByBusinLicence(anOpenAccountInfo.getBusinLicence()) == true) {
            logger.warn("营业执照号码已存在");
            throw new BytterTradeException(40001, "营业执照号码已存在");
        }

        // 检查银行账号是否存在
        if (checkCustExistsByBankAccount(anOpenAccountInfo.getBankAcco()) == true) {
            logger.warn("银行账号已存在");
            throw new BytterTradeException(40001, "银行账号已存在");
        }

        // 检查是否黑名单
        final String anFlag = blacklistService.checkBlacklistExists(anOpenAccountInfo.getCustName(), anOpenAccountInfo.getOrgCode(),
                anOpenAccountInfo.getLawName());
        if (BetterStringUtils.equals(anFlag, "1")) {
            logger.warn("从黑名单库中检测到当前客户开户资料信息,请确认!");
            throw new BytterTradeException(40001, "从黑名单库中检测到当前客户开户资料信息,请确认!");
        }
    }
    
    private void checkAccountInfoParams(final CustOpenAccountTmp anOpenAccountInfo) {
        // 客户资料检查
        BTAssert.notNull(anOpenAccountInfo.getCustName(), "申请机构名称不能为空");
        BTAssert.notNull(anOpenAccountInfo.getOrgCode(), "组织机构代码证不能为空");
        BTAssert.notNull(anOpenAccountInfo.getBusinLicence(), "营业执照号码不能为空");
        BTAssert.notNull(anOpenAccountInfo.getBusinLicenceValidDate(), "营业执照有效期不能为空");
        BTAssert.notNull(anOpenAccountInfo.getZipCode(), "邮政编码不能为空");
        BTAssert.notNull(anOpenAccountInfo.getAddress(), "联系地址不能为空");
        BTAssert.notNull(anOpenAccountInfo.getPhone(), "业务联系电话不能为空");
        BTAssert.notNull(anOpenAccountInfo.getFax(), "传真号码不能为空");
        // 交收行信息检查
        BTAssert.notNull(anOpenAccountInfo.getBankAccoName(), "银行账户名不能为空");
        BTAssert.notNull(anOpenAccountInfo.getBankAcco(), "银行账户不能为空");
        BTAssert.notNull(anOpenAccountInfo.getBankNo(), "所属银行不能为空");
        BTAssert.notNull(anOpenAccountInfo.getBankCityno(), "开户银行所在地不能为空");
        BTAssert.notNull(anOpenAccountInfo.getBankName(), "开户银行全称不能为空");
        // 经办人信息检查
        BTAssert.notNull(anOpenAccountInfo.getOperName(), "经办人姓名不能为空");
        BTAssert.notNull(anOpenAccountInfo.getOperIdenttype(), "经办人证件类型不能为空");
        BTAssert.notNull(anOpenAccountInfo.getOperIdentno(), "经办人证件号码不能为空");
        BTAssert.notNull(anOpenAccountInfo.getOperValiddate(), "经办人证件有效期不能为空");
        BTAssert.notNull(anOpenAccountInfo.getOperMobile(), "经办人手机号码不能为空");
        BTAssert.notNull(anOpenAccountInfo.getOperEmail(), "经办人邮箱不能为空");
        BTAssert.notNull(anOpenAccountInfo.getOperPhone(), "经办人联系电话不能为空");
        // 法人信息检查
        BTAssert.notNull(anOpenAccountInfo.getLawName(), "法人姓名不能为空");
        BTAssert.notNull(anOpenAccountInfo.getLawIdentType(), "法人证件类型不能为空");
        BTAssert.notNull(anOpenAccountInfo.getLawIdentNo(), "法人证件号码不能为空");
        BTAssert.notNull(anOpenAccountInfo.getLawValidDate(), "法人证件有效期不能为空");
    }

    
    /**
     * 检查申请机构名称是否存在
     */
    public boolean checkCustExistsByCustName(final String anCustName) {
        return custAccountService.selectByProperty("custName", anCustName).size() > 0;
    }
    
    /**
     * 检查组织机构代码证是否存在
     */
    public boolean checkCustExistsByIdentNo(final String anIdentNo) {

        return custAccountService.selectByProperty("identNo", anIdentNo).size() > 0;
    }
    
    /**
     * 检查营业执照号码是否存在
     */
    public boolean checkCustExistsByBusinLicence(final String anBusinLicence) {

        return custMechBaseService.selectByProperty("businLicence", anBusinLicence).size() > 0;
    }

    /**
     * 检查银行账号是否存在
     */
    public boolean checkCustExistsByBankAccount(final String anBankAccount) {

        return custMechBankAccountService.selectByProperty("bankAcco", anBankAccount).size() > 0;
    }
    
    /**
     * 检查邮箱是否已注册
     */
    public boolean checkCustExistsByEmail(final String anEmail) {
        return custOperatorService.selectByProperty("email", anEmail).size() > 0;
    }
    
    /**
     * 检查手机号码是否已注册
     */
    public boolean checkCustExistsByMobileNo(final String anMobileNo) {
        return custOperatorService.selectByProperty("mobileNo", anMobileNo).size() > 0;
    }

    private void checkPlatformUser() {
        if (UserUtils.platformUser() == false) {
            logger.warn("当前操作员不能执行该操作");
            throw new BytterTradeException(40001, "当前操作员不能执行该操作");
        }
    }
    
    @Override
    public void saveFormalData(Long anParentId) {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveCancelData(Long anParentId) {
        // TODO Auto-generated method stub

    }

    /**
     *  开户信息修改保存
     */
    public CustOpenAccountTmp saveModifyOpenAccount(CustOpenAccountTmp anOpenAccountInfo, Long anId, String anFileList) {
        this.checkPlatformUser();
        final CustOpenAccountTmp anExitsOpenAccountInfo = this.selectByPrimaryKey(anId);
        BTAssert.notNull(anExitsOpenAccountInfo, "无法获取客户开户资料信息");
        // 初始化参数设置
        anOpenAccountInfo.initModifyValue(anExitsOpenAccountInfo);
        // 营业执照
        initIdentInfo(anOpenAccountInfo);
        // 处理附件
        anOpenAccountInfo.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anOpenAccountInfo.getBatchNo()));
        // 数据存盘,开户资料暂存
        this.updateByPrimaryKeySelective(anOpenAccountInfo);
        return anOpenAccountInfo;
    }
    
    /**
     * 开户信息保存并审核
     */
    public CustOpenAccountTmp saveModifyAndAuditOpenAccount(final Long anId, CustOpenAccountTmp anOpenAccountInfo, String anFileList) {
        //保存修改内容
        this.saveModifyOpenAccount(anOpenAccountInfo, anId, anFileList);
        return null;
    }
}
