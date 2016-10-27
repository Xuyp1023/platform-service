package com.betterjr.modules.wechat.service;

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
import com.betterjr.modules.account.entity.CustOperatorRelation;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustAndOperatorRelaService;
import com.betterjr.modules.account.service.CustContactService;
import com.betterjr.modules.account.service.CustOperatorService;
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
    private CustAndOperatorRelaService custAndOperatorRelaService;

    /**
     * 获取当前微信用户开户信息
     */
    public CustTempEnrollInfo findCustEnroll() {
        Long custNo = Collections3.getFirst(UserUtils.findCustNoList());
        CustTempEnrollInfo custEnrollInfo = Collections3.getFirst(this.selectByProperty("custNo", custNo));
        Long coreCustNo = custEnrollInfo.getCoreCustNo();
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
    public CustTempEnrollInfo addCustEnroll(CustTempEnrollInfo anCustEnrollInfo, final String anCoreCustNo, final String anOpenId,
            final String anFileList) {
        // 检查入参
        checkParameter(anOpenId, "请关注微信公众号[qiejftest]后再进行开户!");
        checkParameter(anCoreCustNo, "请选择核心企业!");
        checkParameter(anFileList, "请上传附件!");

        // 客户注册信息
        checkEnrollInfo(anCustEnrollInfo);
        anCustEnrollInfo.initWeChatAddValue();

        // 转换核心企业编号
        final String coreCustNoValue = DictUtils.getDictCode("FactorCoreCustInfo", anCoreCustNo);
        checkParameter(coreCustNoValue, "请选择核心企业!");
        anCustEnrollInfo.setCoreCustNo(Long.valueOf(coreCustNoValue));

        // 生成OperOrg:客户名称+10位随机数
        final String operOrg = anCustEnrollInfo.getCustName() + SerialGenerator.randomBase62(10);

        // 创建客户操作员
        final CustOperatorInfo operator = addCustOperator(anCustEnrollInfo, operOrg);

        // 生成客户信息
        final CustInfo custInfo = addCustInfo(anCustEnrollInfo, operator);

        // 创建客户与操作员关系
        addCustAndOperatorRelation(custInfo, operator);

        // 创建客户与核心企业关系
        addCustAndCoreRelation(anCustEnrollInfo, custInfo, operator);

        // 生成银行账户信息
        addSaleAccoBank(anCustEnrollInfo, custInfo);
        final ScfSupplierBank bankAccount = addScfSupplierBank(anCustEnrollInfo, custInfo);

        // 生成客户联系信息
        addCustContact(anCustEnrollInfo, custInfo);

        // 保存客户注册信息
        addCusrEnrollInfo(anCustEnrollInfo, custInfo, anFileList);

        // 处理附件,写入文件认证信息表中
        addFileAudit(anCustEnrollInfo, new String[] { "bizLicenseFile", "representIdFile" });

        // 经办人与当前微信绑定,openId从Session中获取
        addBindWeChat(custInfo, operator, anOpenId);

        // 开户信息发送给百乐润
//        sendOpenAccountInfo(custInfo, operator, bankAccount);

        return anCustEnrollInfo;
    }

    private ScfSupplierBank addScfSupplierBank(CustTempEnrollInfo anCustEnrollInfo, CustInfo anCustInfo) {
        ScfSupplierBank bankAccount = new ScfSupplierBank();
        bankAccount.fillDefaultValue();
        bankAccount.setCustNo(anCustInfo.getCustNo());
        bankAccount.setCustName(anCustInfo.getCustName());
        bankAccount.setCoreCustNo(anCustEnrollInfo.getCoreCustNo());
        bankAccount.setBankAccount(anCustEnrollInfo.getBankAccount());
        bankAccount.setBankAccountName(anCustEnrollInfo.getCustName());
        bankAccount.setBankName(anCustEnrollInfo.getBankName());
        bankAccount.setOperOrg(anCustInfo.getOperOrg());
        scfSupplierBankService.insert(bankAccount);
        return bankAccount;
    }

    /**
     * 增加操作员
     */
    private CustOperatorInfo addCustOperator(final CustTempEnrollInfo anCustEnrollInfo, final String anOperOrg) {
        final CustOperatorInfo anCustOperatorInfo = new CustOperatorInfo();
        anCustOperatorInfo.setOperOrg(anOperOrg);
        anCustOperatorInfo.setId(SerialGenerator.getLongValue(SerialGenerator.OPERATOR_ID));
        anCustOperatorInfo.setName(anCustEnrollInfo.getContName());
        anCustOperatorInfo.setMobileNo(anCustEnrollInfo.getContMobileNo());
        anCustOperatorInfo.setPhone(anCustEnrollInfo.getContPhone());
        anCustOperatorInfo.setEmail(anCustEnrollInfo.getContEmail());
        anCustOperatorInfo.setIdentType("0");
        anCustOperatorInfo.setIdentClass("4");// 经办人识别方式(1-书面委托 2-印鉴 3-密码 4-证件)
        anCustOperatorInfo.setStatus("1");
        anCustOperatorInfo.setLastStatus("1");
        anCustOperatorInfo.setRegDate(BetterDateUtils.getNumDate());
        anCustOperatorInfo.setModiDate(BetterDateUtils.getNumDateTime());
        custOperatorService.insert(anCustOperatorInfo);
        return anCustOperatorInfo;
    }

    /**
     * 增加客户信息
     */
    private CustInfo addCustInfo(final CustTempEnrollInfo anCustEnrollInfo, final CustOperatorInfo anOperator) {
        final CustInfo custInfo = new CustInfo();
        custInfo.setRegOperId(anOperator.getId());
        custInfo.setRegOperName(anOperator.getName());
        custInfo.setOperOrg(anOperator.getOperOrg());
        custInfo.setCustNo(SerialGenerator.getCustNo());
        custInfo.setIdentValid(true);
        custInfo.setCustType(anCustEnrollInfo.getCustType());// 客户类型:0-机构;1-个人;
        custInfo.setCustName(anCustEnrollInfo.getCustName());
        custInfo.setIdentType(anCustEnrollInfo.getIdentType());
        custInfo.setIdentNo(anCustEnrollInfo.getIdentNo());
        custInfo.setRegDate(BetterDateUtils.getNumDate());
        custInfo.setRegTime(BetterDateUtils.getNumTime());
        custInfo.setBusinStatus("0");
        custInfo.setLastStatus("0");
        custInfo.setVersion(0L);
        custAccountService.insert(custInfo);
        return custInfo;
    }

    /**
     * 增加客户与操作员关系
     */
    private void addCustAndOperatorRelation(final CustInfo anCustInfo, final CustOperatorInfo anOperator) {
        custAndOperatorRelaService.insert(new CustOperatorRelation(anOperator.getId(), anCustInfo.getCustNo(), anOperator.getOperOrg()));
    }

    /**
     * 增加客户与核心企业关系
     */
    private void addCustAndCoreRelation(final CustTempEnrollInfo anCustEnrollInfo, final CustInfo anCustInfo, final CustOperatorInfo anOperator) {
        // 写入T_CUST_RELATION
        custRelationService.addWeChatCustAndCoreRelation(anCustInfo, anCustEnrollInfo.getCoreCustNo(), anOperator);
        // 写入T_SCF_RELATION
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
     * 生成银行账户信息
     */
    private void addSaleAccoBank(final CustTempEnrollInfo anCustEnrollInfo, final CustInfo anCustInfo) {
        final SaleAccoBankInfo saleBankAccount = new SaleAccoBankInfo();
        saleBankAccount.setMoneyAccount(SerialGenerator.getMoneyAccountID());
        saleBankAccount.setCustNo(anCustInfo.getCustNo());
        saleBankAccount.setTradeAccount("");
        saleBankAccount.setBankName(anCustEnrollInfo.getBankName());
        saleBankAccount.setBankAccount(anCustEnrollInfo.getBankAccount());
        saleBankAccount.setBankAcountName(anCustEnrollInfo.getCustName());
        saleBankAccount.setBranchBank("");
        saleBankAccount.setNetNo("");
        saleBankAccount.setIdentType("1");
        saleBankAccount.setIdentNo("");
        saleBankAccount.setRegDate(BetterDateUtils.getNumDate());
        saleBankAccount.setModiDate(BetterDateUtils.getNumDateTime());
        saleBankAccount.setStatus("4");
        saleBankAccount.setLastStatus("0");
        saleBankAccount.setAuthStatus("1");
        saleBankAccount.setSignStatus(Boolean.TRUE);
        saleBankAccount.setFlag("0");
        saleAccoBankService.insert(saleBankAccount);
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
        for (String fileInfoType : anFileinfoType) {
            final Map<String, Object> anMap = new HashMap<String, Object>();
            anMap.put("fileInfoType", fileInfoType);
            anMap.put("batchNo", anCustEnrollInfo.getBatchNo());
            final List<CustFileItem> bizLicenseFileItem = custFileItemService.selectByProperty(anMap);
            if (bizLicenseFileItem.size() > 0) {
                Long batchNo = CustFileClientUtils.findBatchNo();
                addCustFileAduit(anCustEnrollInfo.getCustNo(), batchNo, bizLicenseFileItem.size(), fileInfoType, anCustEnrollInfo.getOperOrg());
                for (CustFileItem fileItem : bizLicenseFileItem) {
                    addCustFileItem(fileItem, batchNo);
                }
            }
        }
    }
    
    private void addCustFileItem(CustFileItem anCustFileItem, Long anBatchNo) {
        CustFileItem fileItem = new CustFileItem();
        BeanMapper.copy(anCustFileItem, fileItem);
        fileItem.setBatchNo(anBatchNo);
        fileItem.setId(SerialGenerator.getLongValue("CustFileItem.id"));
        custFileItemService.insert(fileItem);
    }

    /**
     * 保存客户注册信息
     */
    private void addCusrEnrollInfo(final CustTempEnrollInfo anCustEnrollInfo, final CustInfo anCustInfo, final String anFileList) {
        anCustEnrollInfo.setCustNo(anCustInfo.getCustNo());
        anCustEnrollInfo.setOperOrg(anCustInfo.getOperOrg());
        anCustEnrollInfo.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anCustEnrollInfo.getBatchNo()));
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

    /**
     * 开户信息发送给百乐润
     */
/*    private void sendOpenAccountInfo(final CustInfo anCustInfo, final CustOperatorInfo anOperator, final ScfSupplierBank anBankAccount) {
        final SaleAccoRequestInfo request = new SaleAccoRequestInfo();
        // 发送内容
        request.setBusinFlag("08");
        request.setCustNo(anCustInfo.getCustNo());// 拜特客户号
        request.setCustType("1");// 客户类型：1:供应商开户;2:核心企业开户;
        request.setCustName(anCustInfo.getCustName());// 单位名称
        request.setContName(anOperator.getName());// 经办人
        request.setContMobileNo(anOperator.getMobileNo());// 经办人联系电话
        request.setBankName(anBankAccount.getBankName());// 开户行
        request.setBankAccount(anBankAccount.getBankAccount());// 银行账号
        request.setAgencyNo("yqr");
        // 调用接口
        saleRemoteHelper.dealAccoRequest(request, anOperator);
    }*/

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
