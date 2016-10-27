package com.betterjr.modules.wechat.service;

import java.util.ArrayList;
import java.util.HashMap;
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
import com.betterjr.common.utils.DictUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.account.entity.CustContactInfo;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustContactService;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.entity.CustOpenAccountTmp;
import com.betterjr.modules.customer.entity.CustRelation;
import com.betterjr.modules.customer.service.CustOpenAccountTmpService;
import com.betterjr.modules.customer.service.CustRelationService;
import com.betterjr.modules.document.entity.CustFileAduit;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.document.service.CustFileAuditService;
import com.betterjr.modules.document.service.CustFileItemService;
import com.betterjr.modules.document.utils.CustFileClientUtils;
import com.betterjr.modules.sys.entity.DictItemInfo;
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

    /**
     * 获取当前微信用户开户信息
     */
    public CustTempEnrollInfo findCustEnroll() {
        final Long custNo = Collections3.getFirst(UserUtils.findCustNoList());
        final CustTempEnrollInfo custEnrollInfo = Collections3.getFirst(this.selectByProperty("custNo", custNo));
        final Long coreCustNo = custEnrollInfo.getCoreCustNo();
        if (null != coreCustNo) {
            custEnrollInfo.setCoreCustName(custAccountService.queryCustName(coreCustNo));
        }
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
        CustOpenAccountTmp anTempAccountData = addCustOpenAccountTmp(anCustEnrollInfo, anFileList);

        // 开户生效操作
        CustOpenAccountTmp anValidAccountData = custOpenAccountTmpService.addWeChatAccount(anTempAccountData.getId());

        // 获取客户信息
        CustInfo custInfo = custAccountService.selectByPrimaryKey(anValidAccountData.getCustNo());

        // 获取操作员信息
        CustOperatorInfo operator = Collections3.getFirst(custOperatorService.queryOperatorInfoByCustNo(custInfo.getCustNo()));

        // 创建客户与核心企业关系
        addCustAndCoreRelation(anCustEnrollInfo, custInfo, operator);

        // 建立客户与保理公司关系(临时过渡方案)
        addCustAndFactorRelation(anCustEnrollInfo, custInfo, operator);

        // 生成银行账户信息
        addBankAccount(anCustEnrollInfo, custInfo);

        // 生成客户联系信息
        addCustContact(anCustEnrollInfo, custInfo);

        // 保存客户注册信息
        anCustEnrollInfo.setBatchNo(anValidAccountData.getBatchNo());
        addCusrEnrollInfo(anCustEnrollInfo, custInfo);

        // 处理附件,写入文件认证信息表中
        addFileAudit(anCustEnrollInfo, new String[] { "bizLicenseFile", "representIdFile" });

        // 经办人与当前微信绑定,openId从Session中获取
        addBindWeChat(custInfo, operator, anOpenId);

        return anCustEnrollInfo;
    }

    private CustOpenAccountTmp addCustOpenAccountTmp(final CustTempEnrollInfo anCustEnrollInfo, String anFileList) {
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
        anOpenAccountInfo.setZipCode("518000");
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
        anOpenAccountInfo.setBusinStatus(CustomerConstants.TMP_STATUS_USED);
        anOpenAccountInfo.setLastStatus(CustomerConstants.TMP_STATUS_USED);
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

    /**
     * 建立客户与保理公司关系(临时过渡方案)
     *
     * @param anCustEnrollInfo
     * @param anCustInfo
     * @param anOperator
     */
    private void addCustAndFactorRelation(final CustTempEnrollInfo anCustEnrollInfo, final CustInfo anCustInfo, final CustOperatorInfo anOperator) {
        // 从字典表获取保理公司
        final List<Long> factorList = new ArrayList<Long>();
        final List<DictItemInfo> factorDictItems = DictUtils.getDictList("ScfFactorGroup");
        for (final DictItemInfo factorItem : factorDictItems) {
            final Long factorNo = Long.valueOf(factorItem.getItemValue());
            if (!factorList.contains(factorNo)) {
                factorList.add(factorNo);
            }
        }

        // 建立客户与保理公司关系
        for (final Long relateCustNo : factorList) {
            final CustRelation relation = new CustRelation();
            relation.initWeChatValue(anOperator);
            relation.setCustNo(anCustInfo.getCustNo());
            relation.setCustName(anCustInfo.getCustName());
            relation.setCustType(anCustInfo.getCustType());
            relation.setRelateCustno(relateCustNo);
            relation.setRelateCustname(custAccountService.queryCustName(relateCustNo));
            relation.setRelateType("1");
            relation.setBusinStatus("3");
            relation.setLastStatus(relation.getBusinStatus());
            custRelationService.insert(relation);
        }
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
    private void addFileAudit(final CustTempEnrollInfo anCustEnrollInfo, final String[] anFileinfoType) {
        for (final String fileInfoType : anFileinfoType) {
            final Map<String, Object> anMap = new HashMap<String, Object>();
            anMap.put("fileInfoType", fileInfoType);
            anMap.put("batchNo", anCustEnrollInfo.getBatchNo());
            final List<CustFileItem> bizLicenseFileItem = custFileItemService.selectByProperty(anMap);
            if (bizLicenseFileItem.size() > 0) {
                final Long batchNo = CustFileClientUtils.findBatchNo();
                addCustFileAduit(anCustEnrollInfo.getCustNo(), batchNo, bizLicenseFileItem.size(), fileInfoType, anCustEnrollInfo.getOperOrg());
                for (final CustFileItem fileItem : bizLicenseFileItem) {
                    addCustFileItem(fileItem, batchNo);
                }
            }
        }
    }

    private void addCustFileItem(final CustFileItem anCustFileItem, final Long anBatchNo) {
        final CustFileItem fileItem = new CustFileItem();
        BeanMapper.copy(anCustFileItem, fileItem);
        fileItem.setBatchNo(anBatchNo);
        fileItem.setId(SerialGenerator.getLongValue("CustFileItem.id"));
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
        if (BetterStringUtils.isNotBlank(anOpenId)) {
            final CustWeChatInfo weChatInfo = custWeChatService.selectByPrimaryKey(anOpenId);
            if (weChatInfo != null) {
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
        }
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
