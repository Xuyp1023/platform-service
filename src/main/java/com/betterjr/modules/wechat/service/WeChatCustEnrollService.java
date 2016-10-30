package com.betterjr.modules.wechat.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.mapper.BeanMapper;
import com.betterjr.common.selectkey.SerialGenerator;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.QueryTermBuilder;
import com.betterjr.modules.account.entity.CustContactInfo;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.entity.CustOperatorRelation;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustAndOperatorRelaService;
import com.betterjr.modules.account.service.CustContactService;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.cert.entity.CustCertInfo;
import com.betterjr.modules.cert.service.CustCertService;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.entity.CustMechBankAccount;
import com.betterjr.modules.customer.entity.CustMechBase;
import com.betterjr.modules.customer.entity.CustMechBusinLicence;
import com.betterjr.modules.customer.entity.CustMechLaw;
import com.betterjr.modules.customer.entity.CustOpenAccountTmp;
import com.betterjr.modules.customer.entity.CustRelation;
import com.betterjr.modules.customer.service.CustMechBankAccountService;
import com.betterjr.modules.customer.service.CustMechBaseService;
import com.betterjr.modules.customer.service.CustMechBusinLicenceService;
import com.betterjr.modules.customer.service.CustMechLawService;
import com.betterjr.modules.customer.service.CustOpenAccountTmpService;
import com.betterjr.modules.customer.service.CustRelationService;
import com.betterjr.modules.document.entity.CustFileAduit;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.document.service.CustFileAuditService;
import com.betterjr.modules.document.service.CustFileItemService;
import com.betterjr.modules.document.utils.CustFileClientUtils;
import com.betterjr.modules.wechat.dao.CustTempEnrollInfoMapper;
import com.betterjr.modules.wechat.entity.CustTempEnrollInfo;
import com.betterjr.modules.wechat.entity.CustWeChatInfo;
import com.betterjr.modules.wechat.entity.SaleAccoBankInfo;
import com.betterjr.modules.wechat.entity.ScfRelation;
import com.betterjr.modules.wechat.entity.ScfSupplierBank;

@Service
public class WeChatCustEnrollService extends BaseService<CustTempEnrollInfoMapper, CustTempEnrollInfo> {

    @Autowired
    private CustWeChatService custWeChatService;

    @Autowired
    private CustAccountService custAccountService;

    @Autowired
    private ScfRelationService scfRelationService;

    @Autowired
    private CustContactService custContactService;

    @Autowired
    private SaleAccoBankService saleAccoBankService;

    @Autowired
    private CustRelationService custRelationService;

    @Autowired
    private CustOperatorService custOperatorService;

    @Autowired
    private CustFileItemService custFileItemService;

    @Autowired
    private CustFileAuditService custFileAuditService;

    @Autowired
    private ScfSupplierBankService scfSupplierBankService;

    @Autowired
    private CustOpenAccountTmpService custOpenAccountTmpService;

    @Autowired
    private CustCertService custCertService;

    @Autowired
    private CustAndOperatorRelaService custAndOperatorRelaService;

    @Autowired
    private CustMechBankAccountService custMechBankAccountService;

    @Autowired
    private CustMechBaseService custMechBaseService;

    @Autowired
    private CustMechLawService custMechLawService;

    @Autowired
    private CustMechBusinLicenceService custMechBusinLicenceService;

    /**
     * 获取当前微信用户开户信息
     */
    public CustTempEnrollInfo findCustEnroll(final String anOpenId) {
        final CustWeChatInfo weChatInfo = custWeChatService.selectByPrimaryKey(anOpenId);
        final Long custNo = weChatInfo.getCustNo();
        final CustTempEnrollInfo custEnrollInfo = new CustTempEnrollInfo();
        // 构架核心企业查询条件
        final Map<String, Object> coreMap = QueryTermBuilder.newInstance().put("custNo", custNo)
                .put("relateType", CustomerConstants.RELATE_TYPE_SUPPLIER_CORE).put("businStatus", CustomerConstants.RELATE_STATUS_AUDIT).build();
        final CustRelation coreRelation = Collections3.getFirst(custRelationService.selectByProperty(coreMap));
        // 核心企业编号
        custEnrollInfo.setCoreCustNo(coreRelation.getRelateCustno());
        // 核心企业名称
        custEnrollInfo.setCoreCustName(custAccountService.queryCustName(coreRelation.getRelateCustno()));
        // 开户企业编号
        custEnrollInfo.setCustNo(custNo);
        // 开户企业名称
        custEnrollInfo.setCustName(custAccountService.queryCustName(custNo));
        // 营业执照
        final Map<String, Object> custMap = QueryTermBuilder.newInstance().put("custNo", custNo).build();
        final CustMechBusinLicence licence = Collections3.getFirst(custMechBusinLicenceService.selectByProperty(custMap));
        custEnrollInfo.setIdentNo(licence.getRegNo());
        // 获取银行账户信息
        final CustMechBankAccount bankAccount = Collections3.getFirst(custMechBankAccountService.selectByProperty(custMap));
        // 银行账号
        custEnrollInfo.setBankAccount(bankAccount.getBankAcco());
        // 开户银行名称
        custEnrollInfo.setBankName(bankAccount.getBankName());
        // 获取操作员信息
        final CustOperatorInfo operator = custOperatorService.selectByPrimaryKey(weChatInfo.getOperId());
        // 经办人
        custEnrollInfo.setContName(operator.getName());
        // 经办人手机
        custEnrollInfo.setContMobileNo(operator.getMobileNo());
        // 经办人邮箱
        custEnrollInfo.setContEmail(operator.getEmail());
        // 实际经营地址
        custEnrollInfo.setPremisesAddress(custContactService.selectByPrimaryKey(custNo).getAddress());
        // 附件信息
        custEnrollInfo.setBatchNo(Collections3.getFirst(custOpenAccountTmpService.selectByProperty(custMap)).getBatchNo());

        return custEnrollInfo;
    }

    /**
     * 客户微信端开户
     *
     * @param anMap
     * @param anCoreCustNo
     * @param anOpenId
     * @param anFileList
     * @return
     */
    public CustTempEnrollInfo addCustEnroll(final CustTempEnrollInfo anCustEnrollInfo, final String anOpenId, final String anFileList) {
        // 检查入参
        checkParameter(anOpenId, "请关注微信公众号[qiejftest]后再进行开户!");
        checkParameter(anFileList, "请上传附件!");

        // 客户注册信息
        checkEnrollInfo(anCustEnrollInfo);
        anCustEnrollInfo.initWeChatAddValue();

        // 生成开户流水
        final CustOpenAccountTmp anTempAccountData = addCustOpenAccountTmp(anCustEnrollInfo, anFileList);

        // 开户生效操作
        final CustOpenAccountTmp anValidAccountData = addWeChatAccount(anTempAccountData.getId(), anFileList);

        // 获取客户信息
        final CustInfo custInfo = custAccountService.selectByPrimaryKey(anValidAccountData.getCustNo());

        // 获取操作员信息
        final CustOperatorInfo operator = Collections3.getFirst(custOperatorService.queryOperatorInfoByCustNo(custInfo.getCustNo()));

        // 创建客户与核心企业关系
        addCustAndCoreRelation(anCustEnrollInfo, custInfo, operator);

        // 生成银行账户信息
        addBankAccount(anCustEnrollInfo, custInfo);

        // 生成客户联系信息
        addCustContact(anCustEnrollInfo, custInfo);

        // 保存客户注册信息
        anCustEnrollInfo.setBatchNo(anValidAccountData.getBatchNo());
        addCusrEnrollInfo(anCustEnrollInfo, custInfo);

        // 初始化数字证书信息
        initCustCertinfo(anCustEnrollInfo, operator);

        // 经办人与当前微信绑定,openId从Session中获取
        addBindWeChat(custInfo, operator, anOpenId);

        return anCustEnrollInfo;
    }

    // =========================================================================================================
    public CustOpenAccountTmp addWeChatAccount(final Long anId, final String anFileList) {
        // 获取客户开户资料
        final CustOpenAccountTmp anOpenAccountInfo = custOpenAccountTmpService.selectByPrimaryKey(anId);
        BTAssert.notNull(anOpenAccountInfo, "无法获取客户开户资料信息");
        // 检查开户资料合法性
        custOpenAccountTmpService.checkAccountInfoValid(anOpenAccountInfo);
        if (BetterStringUtils.equals(anOpenAccountInfo.getBusinStatus(), CustomerConstants.TMP_STATUS_USEING)) {
            // 生成开户数据
            createWeChatValidAccount(anOpenAccountInfo, anOpenAccountInfo.getRegOperId(), anOpenAccountInfo.getRegOperName(),
                    anOpenAccountInfo.getOperOrg(), anFileList);
            // 设置状态为已使用
            anOpenAccountInfo.setBusinStatus(CustomerConstants.TMP_STATUS_USED);
            anOpenAccountInfo.setLastStatus(CustomerConstants.TMP_STATUS_USED);
            // 审核日期
            anOpenAccountInfo.setAuditDate(BetterDateUtils.getNumDate());
            // 审核时间
            anOpenAccountInfo.setAuditTime(BetterDateUtils.getNumTime());
            // 更新数据
            custOpenAccountTmpService.updateByPrimaryKeySelective(anOpenAccountInfo);
        }

        return anOpenAccountInfo;
    }

    private void createWeChatValidAccount(final CustOpenAccountTmp anOpenAccountInfo, final Long anOperId, final String anOperName,
            final String anOperOrg, final String anFileList) {
        // 数据存盘,客户资料
        final CustInfo custInfo = addCustInfo(anOpenAccountInfo, anOperId, anOperName, anOperOrg);

        // 处理附件,写入文件认证信息表中
        addFileAudit(custInfo, anFileList);

        // 数据存盘,基本信息
        addCustMechBase(anOpenAccountInfo, custInfo.getCustNo(), anOperId, anOperName, anOperOrg);

        // 数据存盘,法人信息
        addCustMechLaw(anOpenAccountInfo, custInfo.getCustNo(), anOperId, anOperName, anOperOrg);

        // 数据存盘,营业执照
        addCustMechBusinLicence(anOpenAccountInfo, custInfo.getCustNo(), anOperId, anOperName, anOperOrg);

        // 数据存盘,银行账户
        addCustMechBankAccount(anOpenAccountInfo, custInfo.getCustNo(), anOperId, anOperName, anOperOrg);

        // 数据存盘,经办人信息
        addWeChatCustOperatorInfo(anOpenAccountInfo, custInfo.getCustNo(), anOperId, anOperName, anOperOrg);

        // 数据存盘,当前操作员关联客户
        custAndOperatorRelaService.insert(new CustOperatorRelation(anOperId, custInfo.getCustNo(), anOperOrg));

        // 回写客户编号
        anOpenAccountInfo.setCustNo(custInfo.getCustNo());
    }

    private CustInfo addCustInfo(final CustOpenAccountTmp anOpenAccountInfo, final Long anOperId, final String anOperName, final String anOperOrg) {
        final CustInfo anCustInfo = new CustInfo();
        anCustInfo.setRegOperId(anOperId);
        anCustInfo.setRegOperName(anOperName);
        anCustInfo.setOperOrg(anOperOrg);
        anCustInfo.setCustNo(SerialGenerator.getCustNo());
        anCustInfo.setIdentValid(true);
        anCustInfo.setCustType(CustomerConstants.CUSTOMER_TYPE_ENTERPRISE);// 客户类型:0-机构;1-个人;
        anCustInfo.setCustName(anOpenAccountInfo.getCustName());
        anCustInfo.setIdentType("1");
        anCustInfo.setIdentNo(anOpenAccountInfo.getBusinLicence());
        anCustInfo.setValidDate(anOpenAccountInfo.getBusinLicenceValidDate());
        anCustInfo.setRegDate(BetterDateUtils.getNumDate());
        anCustInfo.setRegTime(BetterDateUtils.getNumTime());
        anCustInfo.setBusinStatus("0");
        anCustInfo.setLastStatus("0");
        anCustInfo.setVersion(0l);

        custAccountService.insert(anCustInfo);

        return anCustInfo;
    }

    private void addCustMechBase(final CustOpenAccountTmp anOpenAccountInfo, final Long anCustNo, final Long anOperId, final String anOperName,
            final String anOperOrg) {
        final CustMechBase anCustMechBaseInfo = new CustMechBase();
        anCustMechBaseInfo.setRegOperId(anOperId);
        anCustMechBaseInfo.setRegOperName(anOperName);
        anCustMechBaseInfo.setOperOrg(anOperOrg);
        anCustMechBaseInfo.setLawName(anOpenAccountInfo.getLawName());
        anCustMechBaseInfo.setLawIdentType(anOpenAccountInfo.getLawIdentType());
        anCustMechBaseInfo.setLawIdentNo(anOpenAccountInfo.getLawIdentNo());
        anCustMechBaseInfo.setLawValidDate(anOpenAccountInfo.getLawValidDate());
        anCustMechBaseInfo.setOrgCode(anOpenAccountInfo.getOrgCode());
        anCustMechBaseInfo.setBusinLicence(anOpenAccountInfo.getBusinLicence());
        anCustMechBaseInfo.setAddress(anOpenAccountInfo.getAddress());
        anCustMechBaseInfo.setPhone(anOpenAccountInfo.getPhone());
        anCustMechBaseInfo.setFax(anOpenAccountInfo.getFax());
        anCustMechBaseInfo.setVersion(0l);
        anCustMechBaseInfo.setZipCode("000000");

        custMechBaseService.addCustMechBase(anCustMechBaseInfo, anCustNo);
    }

    private void addCustMechLaw(final CustOpenAccountTmp anOpenAccountInfo, final Long anCustNo, final Long anOperId, final String anOperName,
            final String anOperOrg) {
        final CustMechLaw anCustMechLawInfo = new CustMechLaw();
        anCustMechLawInfo.setCustNo(anCustNo);
        anCustMechLawInfo.setRegOperId(anOperId);
        anCustMechLawInfo.setRegOperName(anOperName);
        anCustMechLawInfo.setOperOrg(anOperOrg);
        anCustMechLawInfo.setName(anOpenAccountInfo.getLawName());
        anCustMechLawInfo.setIdentType(anOpenAccountInfo.getLawIdentType());
        anCustMechLawInfo.setIdentNo(anOpenAccountInfo.getLawIdentNo());
        anCustMechLawInfo.setValidDate(anOpenAccountInfo.getLawValidDate());
        // anCustMechLawInfo.setSex(IdcardUtils.getGenderByIdCard(anOpenAccountInfo.getLawIdentNo(), anOpenAccountInfo.getLawIdentType()));
        // anCustMechLawInfo.setBirthdate(IdcardUtils.getBirthByIdCard(anOpenAccountInfo.getLawIdentNo()));
        anCustMechLawInfo.setVersion(0l);

        anCustMechLawInfo.setBatchNo(Collections3
                .getFirst(custFileAuditService
                        .selectByProperty(QueryTermBuilder.newInstance().put("custNo", anCustNo).put("workType", "representIdFile").build()))
                .getId());

        custMechLawService.addCustMechLaw(anCustMechLawInfo, anCustNo);
    }

    private void addCustMechBankAccount(final CustOpenAccountTmp anOpenAccountInfo, final Long anCustNo, final Long anOperId, final String anOperName,
            final String anOperOrg) {
        final CustMechBankAccount anCustMechBankAccountInfo = new CustMechBankAccount();
        anCustMechBankAccountInfo.setCustNo(anCustNo);
        anCustMechBankAccountInfo.setRegOperId(anOperId);
        anCustMechBankAccountInfo.setRegOperName(anOperName);
        anCustMechBankAccountInfo.setOperOrg(anOperOrg);
        anCustMechBankAccountInfo.setIsDefault(true);
        anCustMechBankAccountInfo.setTradeAcco("");
        anCustMechBankAccountInfo.setBankNo(anOpenAccountInfo.getBankNo());
        anCustMechBankAccountInfo.setBankName(anOpenAccountInfo.getBankName());
        anCustMechBankAccountInfo.setBankAcco(anOpenAccountInfo.getBankAcco());
        anCustMechBankAccountInfo.setBankAccoName(anOpenAccountInfo.getBankAccoName());
        anCustMechBankAccountInfo.setBankBranch("");
        anCustMechBankAccountInfo.setNetNo("");
        anCustMechBankAccountInfo.setPayCenter("");
        anCustMechBankAccountInfo.setAuthStatus("0");
        anCustMechBankAccountInfo.setSignStatus("0");
        anCustMechBankAccountInfo.setIdentType(anOpenAccountInfo.getIdentType());
        anCustMechBankAccountInfo.setIdentNo(anOpenAccountInfo.getIdentNo());
        anCustMechBankAccountInfo.setFlag("");
        anCustMechBankAccountInfo.setBakupAcco("");
        anCustMechBankAccountInfo.setCountyName("");
        anCustMechBankAccountInfo.setCityNo(anOpenAccountInfo.getBankCityno());
        anCustMechBankAccountInfo.setCityName("");
        anCustMechBankAccountInfo.setAccoStatus("0");
        anCustMechBankAccountInfo.setVersion(0l);

        custMechBankAccountService.addCustMechBankAccount(anCustMechBankAccountInfo, anCustNo);
    }

    private void addCustMechBusinLicence(final CustOpenAccountTmp anOpenAccountInfo, final Long anCustNo, final Long anOperId,
            final String anOperName, final String anOperOrg) {
        final CustMechBusinLicence anCustMechBusinLicenceInfo = new CustMechBusinLicence();
        anCustMechBusinLicenceInfo.setCustNo(anCustNo);
        anCustMechBusinLicenceInfo.setRegOperId(anOperId);
        anCustMechBusinLicenceInfo.setRegOperName(anOperName);
        anCustMechBusinLicenceInfo.setOperOrg(anOperOrg);
        anCustMechBusinLicenceInfo.setRegNo(anOpenAccountInfo.getBusinLicence());
        anCustMechBusinLicenceInfo.setCertifiedDate(anOpenAccountInfo.getBusinLicenceRegDate());
        anCustMechBusinLicenceInfo.setOrgCode(anOpenAccountInfo.getOrgCode());
        anCustMechBusinLicenceInfo.setLawName(anOpenAccountInfo.getLawName());
        anCustMechBusinLicenceInfo.setEndDate(anOpenAccountInfo.getBusinLicenceValidDate());

        anCustMechBusinLicenceInfo
                .setBatchNo(Collections3
                        .getFirst(custFileAuditService
                                .selectByProperty(QueryTermBuilder.newInstance().put("custNo", anCustNo).put("workType", "bizLicenseFile").build()))
                        .getId());

        custMechBusinLicenceService.addBusinLicence(anCustMechBusinLicenceInfo, anCustNo);
    }

    private void addWeChatCustOperatorInfo(final CustOpenAccountTmp anOpenAccountInfo, final Long anCustNo, final Long anOperId,
            final String anOperName, final String anOperOrg) {
        final CustOperatorInfo anCustOperatorInfo = new CustOperatorInfo();
        anCustOperatorInfo.setOperOrg(anOperOrg);
        anCustOperatorInfo.setId(anOperId);
        anCustOperatorInfo.setName(anOperName);
        anCustOperatorInfo.setIdentType(anOpenAccountInfo.getOperIdenttype());
        anCustOperatorInfo.setIdentNo(anOpenAccountInfo.getOperIdentno());
        anCustOperatorInfo.setMobileNo(anOpenAccountInfo.getOperMobile());
        anCustOperatorInfo.setPhone(anOpenAccountInfo.getOperPhone());
        anCustOperatorInfo.setIdentClass(anOpenAccountInfo.getOperIdenttype());
        anCustOperatorInfo.setValidDate(anOpenAccountInfo.getOperValiddate());
        anCustOperatorInfo.setStatus("1");
        anCustOperatorInfo.setLastStatus("1");
        // anCustOperatorInfo.setSex(IdcardUtils.getGenderByIdCard(anCustOperatorInfo.getIdentNo(), anCustOperatorInfo.getIdentType()));
        anCustOperatorInfo.setRegDate(BetterDateUtils.getNumDate());
        anCustOperatorInfo.setModiDate(BetterDateUtils.getNumDateTime());
        anCustOperatorInfo.setFaxNo(anOpenAccountInfo.getOperFaxNo());
        anCustOperatorInfo.setAddress(anOpenAccountInfo.getAddress());
        anCustOperatorInfo.setEmail(anOpenAccountInfo.getOperEmail());
        anCustOperatorInfo.setZipCode(anOpenAccountInfo.getZipCode());
        anCustOperatorInfo.setOperCode("wechat");

        custOperatorService.insert(anCustOperatorInfo);
    }
    // =========================================================================================================

    private void initCustCertinfo(final CustTempEnrollInfo anCustEnrollInfo, final CustOperatorInfo anOperator) {
        final CustCertInfo certInfo = new CustCertInfo();
        certInfo.setSerialNo(Long.toUnsignedString(System.currentTimeMillis() * 10000 + SerialGenerator.randomInt(10000)));
        certInfo.setCustNo(anCustEnrollInfo.getCustNo());
        certInfo.setCustName(anCustEnrollInfo.getCustName());
        certInfo.setIdentNo(anCustEnrollInfo.getIdentNo());
        certInfo.setContName(anCustEnrollInfo.getContName());
        certInfo.setContIdentType("0");
        certInfo.setContIdentNo("");
        certInfo.setContPhone(anCustEnrollInfo.getContMobileNo());
        certInfo.setStatus("8"); // 微信端开户
        certInfo.setVersionUid("wechat");
        certInfo.setSubject("wechat" + anCustEnrollInfo.getCustNo());
        certInfo.setOperNo("-1");
        certInfo.setCertInfo("wechat" + anCustEnrollInfo.getCustNo());
        certInfo.setValidDate(BetterDateUtils.getNumDate());
        certInfo.setCreateDate(BetterDateUtils.getNumDate());
        certInfo.setToken("wechat" + anCustEnrollInfo.getCustNo());
        certInfo.setOperOrg(anOperator.getOperOrg());
        certInfo.setRuleList("SUPPLIER_USER");
        certInfo.setCertId(-1l);
        certInfo.setRegOperId(anOperator.getId());
        certInfo.setRegOperName(anOperator.getName());
        certInfo.setRegDate(BetterDateUtils.getNumDate());
        certInfo.setRegTime(BetterDateUtils.getNumTime());
        certInfo.setModiOperId(anOperator.getId());
        certInfo.setModiOperName(anOperator.getName());
        certInfo.setModiDate(BetterDateUtils.getNumDate());
        certInfo.setModiTime(BetterDateUtils.getNumTime());
        certInfo.setPublishDate(BetterDateUtils.getNumDate());
        certInfo.setPublishMode(BetterDateUtils.getNumTime());
        certInfo.setDescription("wechat" + anCustEnrollInfo.getCustNo());
        certInfo.setEmail(anCustEnrollInfo.getContEmail());
        custCertService.addCustCertInfo(certInfo);
    }

    private CustOpenAccountTmp addCustOpenAccountTmp(final CustTempEnrollInfo anCustEnrollInfo, final String anFileList) {
        final CustOpenAccountTmp anOpenAccountInfo = new CustOpenAccountTmp();
        anOpenAccountInfo.setParentId(0l);
        anOpenAccountInfo.setApplyDate(BetterDateUtils.getNumDate());
        anOpenAccountInfo.setApplyTime(BetterDateUtils.getNumTime());
        anOpenAccountInfo.setAuditDate(BetterDateUtils.getNumDate());
        anOpenAccountInfo.setAuditTime(BetterDateUtils.getNumTime());
        anOpenAccountInfo.setCustNo(0l);
        anOpenAccountInfo.setCustName(anCustEnrollInfo.getCustName());
        anOpenAccountInfo.setIdentNo(anCustEnrollInfo.getIdentNo());
        anOpenAccountInfo.setIdentType(anCustEnrollInfo.getIdentType());
        anOpenAccountInfo.setValidDate("");
        anOpenAccountInfo.setBusinLicence(anCustEnrollInfo.getIdentNo());
        anOpenAccountInfo.setBusinLicenceRegDate("");
        anOpenAccountInfo.setBusinLicenceValidDate("");
        anOpenAccountInfo.setAddress(anCustEnrollInfo.getPremisesAddress());
        anOpenAccountInfo.setZipCode("000000");
        anOpenAccountInfo.setPhone("");
        anOpenAccountInfo.setFax("");
        anOpenAccountInfo.setEmail(anCustEnrollInfo.getContEmail());
        anOpenAccountInfo.setBankAcco(anCustEnrollInfo.getBankAccount());
        anOpenAccountInfo.setBankAccoName(anCustEnrollInfo.getCustName());
        anOpenAccountInfo.setBankNo("");
        anOpenAccountInfo.setBankName(anCustEnrollInfo.getBankName());
        anOpenAccountInfo.setBankCityno("");
        anOpenAccountInfo.setOperName(anCustEnrollInfo.getContName());
        anOpenAccountInfo.setOperIdenttype("0");
        anOpenAccountInfo.setOperIdentno("");
        anOpenAccountInfo.setOperValiddate("");
        anOpenAccountInfo.setOperMobile(anCustEnrollInfo.getContMobileNo());
        anOpenAccountInfo.setOperEmail(anCustEnrollInfo.getContEmail());
        anOpenAccountInfo.setOperPhone("");
        anOpenAccountInfo.setOperFaxNo("");
        anOpenAccountInfo.setLawName("");
        anOpenAccountInfo.setLawIdentType("0");
        anOpenAccountInfo.setLawIdentNo("");
        anOpenAccountInfo.setLawValidDate("");
        anOpenAccountInfo.setBatchNo(anCustEnrollInfo.getBatchNo());
        anOpenAccountInfo.setBusinStatus(CustomerConstants.TMP_STATUS_USEING);
        anOpenAccountInfo.setLastStatus(CustomerConstants.TMP_STATUS_USEING);
        anOpenAccountInfo.setTmpType(CustomerConstants.TMP_TYPE_TEMPSTORE);
        anOpenAccountInfo.setTmpOperType(CustomerConstants.TMP_OPER_TYPE_ADD);
        anOpenAccountInfo.setOrgCode("");
        anOpenAccountInfo.setCoreList(String.valueOf(anCustEnrollInfo.getCoreCustNo()));

        anOpenAccountInfo.setId(SerialGenerator.getLongValue("CustOpenAccountTmp.id"));
        anOpenAccountInfo.setRegOperId(SerialGenerator.getLongValue(SerialGenerator.OPERATOR_ID));
        anOpenAccountInfo.setRegOperName(anCustEnrollInfo.getContName());
        anOpenAccountInfo.setRegDate(BetterDateUtils.getNumDate());
        anOpenAccountInfo.setRegTime(BetterDateUtils.getNumTime());
        // 生成OperOrg:客户名称+10位随机数
        anOpenAccountInfo.setOperOrg(anCustEnrollInfo.getCustName() + SerialGenerator.randomBase62(10));

        // 处理附件
        anOpenAccountInfo.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anOpenAccountInfo.getBatchNo()));

        custOpenAccountTmpService.insert(anOpenAccountInfo);

        return anOpenAccountInfo;
    }

    /**
     * 增加客户与核心企业关系
     */
    private void addCustAndCoreRelation(final CustTempEnrollInfo anCustEnrollInfo, final CustInfo anCustInfo, final CustOperatorInfo anOperator) {
        // 写入T_CUST_RELATION
        custRelationService.addWeChatCustAndCoreRelation(anCustInfo, anCustEnrollInfo.getCoreCustNo(), anOperator);

        // 写入T_SCF_RELATION(兼容1.0版本数据结构,汇票池匹配时需要使用该信息)
        final ScfRelation relation = new ScfRelation();
        relation.initWeChatValue();
        relation.setCustNo(anCustInfo.getCustNo());
        relation.setCustName(anCustInfo.getCustName());
        relation.setCoreCustNo(anCustEnrollInfo.getCoreCustNo());
        relation.setBankAccount(anCustEnrollInfo.getBankAccount());
        relation.setBankAccountName(anCustEnrollInfo.getCustName());
        relation.setOperOrg(anOperator.getOperOrg());
        relation.setBtNo("");
        relation.setCorpNo("");
        scfRelationService.insert(relation);
    }

    private void addBankAccount(final CustTempEnrollInfo anCustEnrollInfo, final CustInfo anCustInfo) {
        final SaleAccoBankInfo saleBankAccount = new SaleAccoBankInfo();
        saleBankAccount.setMoneyAccount(SerialGenerator.getMoneyAccountID());
        saleBankAccount.setCustNo(anCustInfo.getCustNo());
        saleBankAccount.setTradeAccount("");
        saleBankAccount.setBankName(anCustEnrollInfo.getBankName());
        saleBankAccount.setBankAccount(anCustEnrollInfo.getBankAccount());
        saleBankAccount.setBankAcountName(anCustEnrollInfo.getCustName());
        saleBankAccount.setBranchBank("");
        saleBankAccount.setBankNo("0");
        saleBankAccount.setNetNo("");
        saleBankAccount.setIdentType("1");
        saleBankAccount.setIdentNo("");
        saleBankAccount.setRegDate(BetterDateUtils.getNumDate());
        saleBankAccount.setModiDate(BetterDateUtils.getNumDate());
        saleBankAccount.setStatus("4");
        saleBankAccount.setLastStatus("0");
        saleBankAccount.setAuthStatus("1");
        saleBankAccount.setSignStatus(Boolean.TRUE);
        saleBankAccount.setFlag("0");
        saleAccoBankService.insert(saleBankAccount);

        final ScfSupplierBank bankAccount = new ScfSupplierBank();
        bankAccount.fillDefaultValue();
        bankAccount.setCustNo(anCustInfo.getCustNo());
        bankAccount.setCustName(anCustInfo.getCustName());
        bankAccount.setCoreCustNo(anCustEnrollInfo.getCoreCustNo());
        bankAccount.setBankAccount(anCustEnrollInfo.getBankAccount());
        bankAccount.setBankAccountName(anCustEnrollInfo.getCustName());
        bankAccount.setBankName(anCustEnrollInfo.getBankName());
        bankAccount.setOperOrg(anCustInfo.getOperOrg());
        scfSupplierBankService.insert(bankAccount);

        // TODO 银行账号信息 CustMechBankAccount
    }

    /**
     * 生成客户联系信息
     */
    private void addCustContact(final CustTempEnrollInfo anCustEnrollInfo, final CustInfo anCustInfo) {
        final CustContactInfo contact = new CustContactInfo();
        contact.setCustNo(anCustInfo.getCustNo());
        contact.setAddress(anCustEnrollInfo.getPremisesAddress());
        contact.setModiDate(BetterDateUtils.getNumDateTime());
        custContactService.insert(contact);
    }

    /**
     * 处理附件,写入文件认证信息表中
     */
    private void addFileAudit(final CustInfo anCustInfo, final String anFileList) {
        List<CustFileItem> licenseList = new ArrayList<CustFileItem>();
        List<CustFileItem> representList = new ArrayList<CustFileItem>();
        for (String fileId : anFileList.split(",")) {
            CustFileItem fileItem = custFileItemService.selectByPrimaryKey(Long.valueOf(fileId.trim()));
            String fileInfoType = fileItem.getFileInfoType();
            // 企业营业执照
            if (BetterStringUtils.equals(fileInfoType, "bizLicenseFile")) {
                licenseList.add(fileItem);
            }
            // 法人身份证件
            if (BetterStringUtils.equals(fileInfoType, "representIdFile")) {
                representList.add(fileItem);
            }
        }
        // 处理企业营业执照附件,写入认证表
        final Long licenseBatchNo = CustFileClientUtils.findBatchNo();
        addCustFileAduit(anCustInfo.getCustNo(), licenseBatchNo, licenseList.size(), "bizLicenseFile", anCustInfo.getOperOrg());
        for (CustFileItem fileItem : licenseList) {
            addCustFileItem(fileItem, licenseBatchNo);
        }
        // 处理法人身份证附件,写入认证表
        final Long representBatchNo = CustFileClientUtils.findBatchNo();
        addCustFileAduit(anCustInfo.getCustNo(), representBatchNo, licenseList.size(), "representIdFile", anCustInfo.getOperOrg());
        for (CustFileItem fileItem : licenseList) {
            addCustFileItem(fileItem, representBatchNo);
        }
    }

    private void addCustFileItem(final CustFileItem anCustFileItem, final Long anBatchNo) {
        final CustFileItem fileItem = new CustFileItem();
        fileItem.setId(SerialGenerator.getLongValue("CustFileItem.id"));
        fileItem.setBatchNo(anBatchNo);
        fileItem.setFileName(anCustFileItem.getFileName());
        fileItem.setFileType(anCustFileItem.getFileType());
        fileItem.setFileNo(anCustFileItem.getFileNo());
        fileItem.setFilePath(anCustFileItem.getFilePath());
        fileItem.setFileLength(anCustFileItem.getFileLength());
        fileItem.setRegDate(anCustFileItem.getRegDate());
        fileItem.setRegTime(anCustFileItem.getRegTime());
        fileItem.setFileInfoType(anCustFileItem.getFileInfoType());
        fileItem.setRegOperId(anCustFileItem.getRegOperId());
        fileItem.setRegOperName(anCustFileItem.getRegOperName());
        fileItem.setModiOperId(anCustFileItem.getModiOperId());
        fileItem.setModiOperName(anCustFileItem.getModiOperName());
        fileItem.setModiDate(anCustFileItem.getModiDate());
        fileItem.setModiTime(anCustFileItem.getModiTime());
        fileItem.setOperOrg(anCustFileItem.getOperOrg());
        custFileItemService.insert(fileItem);
    }

    /**
     * 保存客户注册信息
     */
    private void addCusrEnrollInfo(final CustTempEnrollInfo anCustEnrollInfo, final CustInfo anCustInfo) {
        anCustEnrollInfo.setCustNo(anCustInfo.getCustNo());
        anCustEnrollInfo.setOperOrg(anCustInfo.getOperOrg());
        this.insert(anCustEnrollInfo);
    }

    /**
     * 经办人与当前微信绑定
     */
    private void addBindWeChat(final CustInfo anCustInfo, final CustOperatorInfo anOperator, final String anOpenId) {
        BTAssert.isTrue(BetterStringUtils.isNotBlank(anOpenId), "openid 不允许为空");
        final CustWeChatInfo weChatInfo = custWeChatService.selectByPrimaryKey(anOpenId);
        BTAssert.notNull(weChatInfo, "没有找到微信用户信息！");
        weChatInfo.setOperId(anOperator.getId());
        weChatInfo.setOperName(anOperator.getName());
        weChatInfo.setOperOrg(anOperator.getOperOrg());
        weChatInfo.setModiDate(BetterDateUtils.getNumDate());
        weChatInfo.setModiTime(BetterDateUtils.getNumTime());
        weChatInfo.setModiOperId(anOperator.getId());
        weChatInfo.setModiOperName(anOperator.getName());
        weChatInfo.setCustNo(anCustInfo.getCustNo());
        weChatInfo.setBusinStatus("1");// 开户结束后设置为已完成状态;
        custWeChatService.updateByPrimaryKeySelective(weChatInfo);
    }

    private void addCustFileAduit(final Long anCustNo, final Long anBatchNo, final int anFileCount, final String anFileInfoType,
            final String anOperCode) {
        final CustFileAduit anFileAudit = new CustFileAduit();
        anFileAudit.setId(anBatchNo);
        anFileAudit.setCustNo(anCustNo);
        anFileAudit.setFileCount(anFileCount);
        anFileAudit.setAuditStatus("1");
        anFileAudit.setWorkType(anFileInfoType);
        anFileAudit.setDescription("");
        anFileAudit.setRegDate(BetterDateUtils.getNumDate());
        anFileAudit.setRegTime(BetterDateUtils.getNumTime());
        anFileAudit.setOperNo("");
        custFileAuditService.insert(anFileAudit);
    }

    private void checkEnrollInfo(final CustTempEnrollInfo anCustEnrollInfo) {
        BTAssert.notNull(anCustEnrollInfo, "请填写开户信息!");
        if (BetterStringUtils.isBlank(anCustEnrollInfo.getCustName())) {
            logger.warn("请填写客户名称！");
            throw new BytterTradeException(40001, "请填写客户名称！");
        }
        if (BetterStringUtils.isBlank(anCustEnrollInfo.getIdentNo())) {
            logger.warn("请填写营业执照号码！");
            throw new BytterTradeException(40001, "请填写营业执照号码！");
        }
        if (BetterStringUtils.isBlank(anCustEnrollInfo.getBankAccount())) {
            logger.warn("请填写与核心企业往来银行账号！");
            throw new BytterTradeException(40001, "请填写与核心企业往来银行账号！");
        }
        if (BetterStringUtils.isBlank(anCustEnrollInfo.getBankName())) {
            logger.warn("请填写开户银行名称！");
            throw new BytterTradeException(40001, "请填写开户银行名称！");
        }
        if (BetterStringUtils.isBlank(anCustEnrollInfo.getContName())) {
            logger.warn("请填写经办人！");
            throw new BytterTradeException(40001, "请填写经办人！");
        }
        if (BetterStringUtils.isBlank(anCustEnrollInfo.getContMobileNo())) {
            logger.warn("请填写经办人手机号码！");
            throw new BytterTradeException(40001, "请填写经办人手机号码！");
        }
        if (BetterStringUtils.isBlank(anCustEnrollInfo.getContEmail())) {
            logger.warn("请填写经办人邮箱！");
            throw new BytterTradeException(40001, "请填写经办人邮箱！");
        }
        if (BetterStringUtils.isBlank(anCustEnrollInfo.getPremisesAddress())) {
            logger.warn("请填写经营地址！");
            throw new BytterTradeException(40001, "请填写经营地址！");
        }
        // 检查企业名称或营业执照是否存在
        if (checkCustExists(anCustEnrollInfo) == true) {
            logger.warn("注册使用的公司名称或营业执照已注册!");
            throw new BytterTradeException(40001, "注册使用的公司名称或营业执照已注册!");
        }
    }

    private void checkParameter(final String anKey, final String anMessage) {
        if (BetterStringUtils.isBlank(anKey)) {
            logger.warn(anMessage);
            throw new BytterTradeException(40001, anMessage);
        }
    }

    private boolean checkCustExists(final CustTempEnrollInfo anCustEnrollInfo) {

        return custAccountService.selectByProperty("custName", anCustEnrollInfo.getCustName()).size() > 0
                || custAccountService.selectByProperty("identNo", anCustEnrollInfo.getIdentNo()).size() > 0;
    }

}
