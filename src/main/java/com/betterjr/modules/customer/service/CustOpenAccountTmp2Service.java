package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.mq.core.RocketMQProducer;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.JedisUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.blacklist.service.BlacklistService;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.dao.CustOpenAccountTmpMapper;
import com.betterjr.modules.customer.entity.CustInsteadApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustOpenAccountTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;
import com.betterjr.modules.document.ICustFileService;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.document.service.CustFileItemService;
import com.betterjr.modules.sms.constants.SmsConstants;
import com.betterjr.modules.sms.dubbo.interfaces.IVerificationCodeService;
import com.betterjr.modules.sms.entity.VerifyCode;
import com.betterjr.modules.sms.util.VerifyCodeType;
import com.betterjr.modules.wechat.service.CustWeChatService;

@Service
public class CustOpenAccountTmp2Service extends BaseService<CustOpenAccountTmpMapper, CustOpenAccountTmp> implements IFormalDataService {

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
    @Autowired
    @Resource
    private RocketMQProducer betterProducer;
    @Autowired
    private CustInsteadRecordService custInsteadRecordService;
    @Autowired
    private CustInsteadApplyService custInsteadApplyService;
    @Autowired
    private CustWeChatService custWeChatService;

    @Reference(interfaceClass = ICustFileService.class)
    private ICustFileService custFileItemService2;

    @Autowired
    private CustInstead2Service custInstead2Service;
    
    @Reference(interfaceClass = IVerificationCodeService.class)
    private IVerificationCodeService verificationCodeService;

    /**
     * 开户申请提交
     */
    public CustOpenAccountTmp saveOpenAccountApply(final CustOpenAccountTmp anOpenAccountInfo, final Long anOperId, final String anFileList) {
        logger.info("Begin to Commit Open Account Apply");
        // 填充操作员信息
        fillOperatorByOperId(anOperId, anOpenAccountInfo);
        // 检查开户资料合法性
        checkAccountInfoValid(anOpenAccountInfo);
        // 初始化参数设置
        initAddValue(anOpenAccountInfo, CustomerConstants.TMP_TYPE_TEMPSTORE, CustomerConstants.TMP_STATUS_OWN);
        // 处理附件
        anOpenAccountInfo.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anOpenAccountInfo.getBatchNo()));
        // 数据存盘,开户资料暂存
        this.insert(anOpenAccountInfo);
        return anOpenAccountInfo;
    }

    /**
     * 填充操作员信息
     */
    private void fillOperatorByOperId(Long anOperId, CustOpenAccountTmp anCustOpenAccountTmp) {
        CustOperatorInfo anOperator = custOperatorService.selectByPrimaryKey(anOperId);
        BTAssert.notNull(anOperator, "未找到操作员信息！");
        anCustOpenAccountTmp.setOperName(anOperator.getName());
        anCustOpenAccountTmp.setOperIdenttype(anOperator.getIdentType());
        anCustOpenAccountTmp.setOperIdentno(anOperator.getIdentNo());
        anCustOpenAccountTmp.setOperValiddate(anOperator.getValidDate());
        anCustOpenAccountTmp.setOperMobile(anOperator.getMobileNo());
        anCustOpenAccountTmp.setOperEmail(anOperator.getEmail());
        anCustOpenAccountTmp.setOperPhone(anOperator.getPhone());
        anCustOpenAccountTmp.setOperFaxNo(anOperator.getFaxNo());
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
     * 生成营业执照,客户证件类型， 个人证件类型0-身份证，1-护照，2-军官证，3-士兵证，4-回乡证，5-户口本，6-外国护照； 机构证件类型 0-技术监督局代码，1-营业执照，2-行政机关，3-社会团体，4-军队，5-武警，6-下属机构（具有主管单位批文号），7-基金会
     */
    private void initIdentInfo(final CustOpenAccountTmp anOpenAccountInfo) {
        anOpenAccountInfo.setIdentNo(anOpenAccountInfo.getBusinLicence());
        // 证件类型
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
        
    }

    @Override
    public void saveCancelData(Long anParentId) {
        // TODO Auto-generated method stub

    }

    /**
     * 开户信息修改保存
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
     * 代录开户资料提交
     */
    public CustOpenAccountTmp saveOpenAccountInfoByInstead(CustOpenAccountTmp anOpenAccountInfo, Long anInsteadRecordId, String anFileList) {
        logger.info("Begin to Save Open Account Infomation Instead");
        // 代录流水号不能为空
        BTAssert.notNull(anInsteadRecordId, "代录流水号不能为空");
        // 检查开户资料合法性
        checkAccountInfoValid(anOpenAccountInfo);
        // 检查是否已存在代录
        final CustInsteadRecord anInsteadRecord = custInsteadRecordService.selectByPrimaryKey(anInsteadRecordId);
        BTAssert.notNull(anInsteadRecord, "无法获取代录信息");
        // 获取代录暂存的开户资料ID号
        final String anTempId = anInsteadRecord.getTmpIds();
        if (null == anTempId) {
            // 初始化参数设置
            initAddValue(anOpenAccountInfo, CustomerConstants.TMP_TYPE_INSTEADSTORE, CustomerConstants.TMP_STATUS_USEING);
            // 处理附件
            anOpenAccountInfo.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anOpenAccountInfo.getBatchNo()));
            // 数据存盘,开户资料暂存
            this.insert(anOpenAccountInfo);
        }
        else {
            // 加载已暂存的开户资料
            final CustOpenAccountTmp anExitsOpenAccountInfo = this.selectByPrimaryKey(Long.valueOf(anTempId));
            // 初始化参数设置
            anOpenAccountInfo.initModifyValue(anExitsOpenAccountInfo);
            // 营业执照
            initIdentInfo(anOpenAccountInfo);
            // 设置状态为使用
            anOpenAccountInfo.setBusinStatus(CustomerConstants.TMP_STATUS_USEING);
            anOpenAccountInfo.setLastStatus(CustomerConstants.TMP_STATUS_USEING);
            // 处理附件
            anOpenAccountInfo.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anOpenAccountInfo.getBatchNo()));
            // 数据存盘,开户资料暂存
            this.updateByPrimaryKeySelective(anOpenAccountInfo);
        }
        // 回写暂存流水号至代录申请表
        final CustInsteadRecord insteadRecord = custInsteadRecordService.saveInsteadRecord(anInsteadRecordId,
                String.valueOf(anOpenAccountInfo.getId()));

        // 回写 parentId by instead record id. add by Liuwl 2016-10-12
        anOpenAccountInfo.setParentId(insteadRecord.getId());
        this.updateByPrimaryKeySelective(anOpenAccountInfo);

        custInsteadApplyService.saveCustInsteadApplyCustInfo(insteadRecord.getApplyId(), null, anOpenAccountInfo.getCustName());

        return anOpenAccountInfo;
    }

    /**
     * 开户资料暂存
     */
    public CustOpenAccountTmp saveOpenAccountInfo(final CustOpenAccountTmp anOpenAccountInfo, final Long anId, final String anFileList) {
        logger.info("Begin to Save Open Account Infomation");
        // 检查开户资料合法性,部分检查
        wechatCheckAccountInfoValid(anOpenAccountInfo);
        if (null == anId) {
            // 初始化参数设置
            initAddValue(anOpenAccountInfo, CustomerConstants.TMP_TYPE_TEMPSTORE, CustomerConstants.TMP_STATUS_NEW);
            // 初始化微信相应选--解决前端默认值被暂存刷新问题
            anOpenAccountInfo.initDefaultValue();
            // 处理附件
            anOpenAccountInfo.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anOpenAccountInfo.getBatchNo()));
            // 数据存盘,开户资料暂存
            this.insert(anOpenAccountInfo);
        }
        else {
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
        }

        return anOpenAccountInfo;
    }

    /**
     * 微信检查入参
     */
    private void wechatCheckAccountInfoValid(CustOpenAccountTmp anOpenAccountInfo) {
        BTAssert.notNull(anOpenAccountInfo.getCustName(), "企业名称不能为空");
        BTAssert.notNull(anOpenAccountInfo.getOperName(), "经办人姓名不能为空");
        BTAssert.notNull(anOpenAccountInfo.getOperMobile(), "经办人手机号码不能为空");
        BTAssert.notNull(anOpenAccountInfo.getOperEmail(), "经办人邮箱不能为空");

        // 检查申请机构名称是否存在
        if (checkCustExistsByCustName(anOpenAccountInfo.getCustName()) == true) {
            logger.warn("申请机构名称已存在");
            throw new BytterTradeException(40001, "申请机构名称已存在");
        }
        if (checkCustExistsByEmail(anOpenAccountInfo.getOperEmail()) == true) {
            logger.warn("经办人邮箱已存在");
            throw new BytterTradeException(40001, "经办人邮箱已存在");
        }
        if (checkCustExistsByEmail(anOpenAccountInfo.getOperEmail()) == true) {
            logger.warn("经办人邮箱已存在");
            throw new BytterTradeException(40001, "经办人邮箱已存在");
        }

        if (checkCustExistsByMobileNo(anOpenAccountInfo.getOperMobile()) == true) {
            logger.warn("经办人手机号已存在");
            throw new BytterTradeException(40001, "经办人手机号已存在");
        }

    }

    /**
     * 通过微信唯一标识查询开户信息
     */
    public CustOpenAccountTmp findAccountTmpInfo(String anOpenId) {
        // 检查入参
        checkParameter(anOpenId, "请关注微信公众号[qiejftest]后再进行开户!");
        CustOpenAccountTmp accountTmpInfo = Collections3.getFirst(this.selectByProperty("wechatOpenId", anOpenId));
        return accountTmpInfo;
    }

    private void checkParameter(final String anKey, final String anMessage) {
        if (BetterStringUtils.isBlank(anKey)) {
            logger.warn(anMessage);
            throw new BytterTradeException(40001, anMessage);
        }
    }

    /**
     * 根据开户id和文件id保存附件
     */
    public CustFileItem saveSingleFileLink(Long anId, String anFileTypeName, String anFileMediaId) {
        CustOpenAccountTmp custOpenInfo = this.selectByPrimaryKey(anId);
        BTAssert.notNull(custOpenInfo, "无法获取开户信息");
        CustFileItem fileItem = custWeChatService.saveWechatFile(anFileTypeName, anFileMediaId);
        System.out.println(fileItem);
        System.out.println(anId);
        custOpenInfo.setBatchNo(custFileItemService2.updateCustFileItemInfo(fileItem.getId().toString(), custOpenInfo.getBatchNo()));
        this.updateByPrimaryKey(custOpenInfo);
        return fileItem;
    }

    /**
     * 根据batchNo生成对应文件类型Map Json对象(微信使用)
     */
    public Map<String, Object> findAccountFileByBatChNo(Long anBatchNo) {
        Map<String, Object> fileMap = new HashMap<String, Object>();
        List<CustFileItem> fileList = custFileItemService.findCustFiles(anBatchNo);
        String[] fileInfoTypes = { "CustBizLicenseFile", "CustOrgCodeFile", "CustTaxRegistFile", "CustCreditCodeFile", "CustBankOpenLicenseFile",
                "BrokerIdHeadFile", "BrokerIdNationFile", "BrokerIdHoldFile", "RepresentIdHeadFile", "RepresentIdNationFile", "RepresentIdHoldFile",
                "CustOpenAccountFilePack" };
        for (String anFileInfoType : fileInfoTypes) {
            // 默认数据
            CustFileItem defaultFile = new CustFileItem();
            defaultFile.setFileInfoType(anFileInfoType);
//            fileMap.put(anFileInfoType, defaultFile);
            // 遍历文件，若存在则放入
            for (CustFileItem anFile : fileList) {
                if (BetterStringUtils.equals(anFileInfoType, anFile.getFileInfoType())) {
                    fileMap.put(anFileInfoType, anFile);
                }
            }
        }
        return fileMap;
    }

    /**
     * 根据openId查询开户申请状态，返回对应值，供页面相应跳转使用
     * @param anOpenId
     * @return
     */
    public String findOpenAccountStatus(String anOpenId) {
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("wechatOpenId", anOpenId);

        CustOpenAccountTmp openAccountTmp = Collections3.getFirst(this.selectByProperty(conditionMap));

        if (openAccountTmp == null) {
            return "0";
        }
        else {
            CustInsteadApply custInsteadApply = custInstead2Service.findInsteadApplyByAccountTmpId(openAccountTmp.getId());

            if (custInsteadApply != null) {
                if (BetterStringUtils.equals(custInsteadApply.getBusinStatus(), "4")) {
                    return "2";
                }
                return "1";
            }
            else {

                return "0";
            }
        }
    }

    /**
     * 发送手机短信验证码
     */
    public String sendValidMessage(String anMobileNo) {
        BTAssert.isTrue(BetterStringUtils.isNotBlank(anMobileNo), "手机号码不允许为空！");
        final VerifyCode verifyCode = verificationCodeService.sendVerifyCode(anMobileNo, VerifyCodeType.OPEN_ACCOUNT_PASSWORD);
        BTAssert.notNull(verifyCode, "没有生成验证码！");
        JedisUtils.delObject(SmsConstants.smsOpenAccountVerifyCodePrefix + anMobileNo);
        JedisUtils.setObject(SmsConstants.smsOpenAccountVerifyCodePrefix + anMobileNo, verifyCode, SmsConstants.SEC_600);
        return AjaxObject.newOk("发送验证码成功").toJson();
    }

    /**
     * 根据operOrg查询Apply状态
     */
    public String findInsteadApplyStatus() {
        CustInsteadApply instApply = Collections3.getFirst(custInsteadApplyService.selectByProperty("operOrg", UserUtils.getOperatorInfo().getOperOrg()));
        if (null != instApply) {
            return instApply.getBusinStatus();
        }else {
           return "";
        }
    }
    
    /**
     * 根据operOrg查询开户信息
     */
    public CustOpenAccountTmp findOpenAccoutnTmp() {
        CustOpenAccountTmp accountInfo = Collections3.getFirst(this.selectByProperty("operOrg", UserUtils.getOperatorInfo().getOperOrg()));
        return accountInfo;
    }

    /**
     * 微信，删除附件
     */
    public int saveDeleteSingleFile(Long anId) {
        CustFileItem anFile = custFileItemService.selectByPrimaryKey(anId);
        BTAssert.notNull(anFile, "无法获取相应附件!");
        anFile.setBatchNo(-anFile.getBatchNo());
        return custFileItemService.updateByPrimaryKey(anFile);
    }
}
