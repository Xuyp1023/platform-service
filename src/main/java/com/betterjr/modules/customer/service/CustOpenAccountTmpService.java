package com.betterjr.modules.customer.service;

import java.util.ArrayList;
import java.util.Collection;
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
import com.betterjr.common.utils.IdcardUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.common.utils.reflection.ReflectionUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.entity.CustOperatorRelation;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustAndOperatorRelaService;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.blacklist.service.BlacklistService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustOpenAccountTmpMapper;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustMechBankAccount;
import com.betterjr.modules.customer.entity.CustMechBase;
import com.betterjr.modules.customer.entity.CustMechBusinLicence;
import com.betterjr.modules.customer.entity.CustMechLaw;
import com.betterjr.modules.customer.entity.CustOpenAccountTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;
import com.betterjr.modules.document.entity.CustFileAduit;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.document.service.CustFileAuditService;
import com.betterjr.modules.document.service.CustFileItemService;
import com.betterjr.modules.document.utils.CustFileUtils;
import com.google.common.collect.Multimap;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustOpenAccountTmpService extends BaseService<CustOpenAccountTmpMapper, CustOpenAccountTmp> implements IFormalDataService {

    @Autowired
    private BlacklistService blacklistService;

    @Autowired
    private CustMechLawService custMechLawService;

    @Autowired
    private CustAccountService custAccountService;

    @Autowired
    private CustMechBaseService custMechBaseService;

    @Autowired
    private CustOperatorService custOperatorService;

    @Autowired
    private CustFileItemService custFileItemService;

    @Autowired
    private CustFileAuditService custFileAuditService;

    @Autowired
    private CustInsteadRecordService custInsteadRecordService;

    @Autowired
    private CustAndOperatorRelaService custAndOperatorRelaService;

    @Autowired
    private CustMechBankAccountService custMechBankAccountService;

    @Autowired
    private CustMechBusinLicenceService custMechBusinLicenceService;

    @Autowired
    private CustOpenAccountAuditService custOpenAccountAuditService;

    /**
     * 开户资料读取
     * 
     * @return
     */
    public CustOpenAccountTmp findOpenAccountInfo() {
        // 读取被驳回的记录
        CustOpenAccountTmp anOpenAccountInfo = findRefuseAccountInfo();
        if (anOpenAccountInfo != null) {
            return anOpenAccountInfo;
        }
        // 读取暂存的记录
        return findTempStoreAccountInfo();
    }

    private CustOpenAccountTmp findTempStoreAccountInfo() {
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("operOrg", UserUtils.getOperatorInfo().getOperOrg());
        anMap.put("businStatus", CustomerConstants.TMP_STATUS_NEW);
        anMap.put("tmpType", CustomerConstants.TMP_TYPE_TEMPSTORE);
        return Collections3.getFirst(this.selectByProperty(anMap));
    }

    private CustOpenAccountTmp findRefuseAccountInfo() {
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("operOrg", UserUtils.getOperatorInfo().getOperOrg());
        anMap.put("businStatus", CustomerConstants.TMP_STATUS_REFUSE);
        anMap.put("tmpType", CustomerConstants.TMP_TYPE_TEMPSTORE);
        return Collections3.getFirst(this.selectByProperty(anMap));
    }

    /**
     * 开户资料暂存
     * 
     * @param anOpenAccountInfo
     * @param anFileList
     * @return
     */
    public CustOpenAccountTmp saveOpenAccountInfo(CustOpenAccountTmp anOpenAccountInfo, Long anId, String anFileList) {
        logger.info("Begin to Save Open Account Infomation");
        // 检查开户资料合法性
        checkAccountInfoValid(anOpenAccountInfo);
        if (null == anId) {
            // 初始化参数设置
            initAddValue(anOpenAccountInfo, CustomerConstants.TMP_TYPE_TEMPSTORE, CustomerConstants.TMP_STATUS_NEW);
            // 处理附件
            anOpenAccountInfo.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anOpenAccountInfo.getBatchNo()));
            // 数据存盘,开户资料暂存
            this.insert(anOpenAccountInfo);
        }
        else {
            CustOpenAccountTmp anExitsOpenAccountInfo = this.selectByPrimaryKey(anId);
            BTAssert.notNull(anExitsOpenAccountInfo, "无法获取客户开户资料信息");
            // 初始化参数设置
            anOpenAccountInfo.initModifyValue(anExitsOpenAccountInfo);
            // 处理附件
            anOpenAccountInfo.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anOpenAccountInfo.getBatchNo()));
            // 数据存盘,开户资料暂存
            this.updateByPrimaryKeySelective(anOpenAccountInfo);
        }

        return anOpenAccountInfo;
    }

    /**
     * 开户申请
     * 
     * @param anOpenAccountInfo
     * @param anId
     * @return
     */
    public CustOpenAccountTmp saveOpenAccountApply(CustOpenAccountTmp anOpenAccountInfo, Long anId, String anFileList) {
        logger.info("Begin to Commit Open Account Apply");
        // 检查开户资料合法性
        checkAccountInfoValid(anOpenAccountInfo);
        // 开户流水号
        if (null == anId) {
            // 初始化参数设置
            initAddValue(anOpenAccountInfo, CustomerConstants.TMP_TYPE_TEMPSTORE, CustomerConstants.TMP_STATUS_USEING);
            // 处理附件
            anOpenAccountInfo.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anOpenAccountInfo.getBatchNo()));
            // 数据存盘,开户资料暂存
            this.insert(anOpenAccountInfo);
        }
        else {
            // 加载已暂存的开户资料
            CustOpenAccountTmp anExitsOpenAccountInfo = this.selectByPrimaryKey(anId);
            BTAssert.notNull(anExitsOpenAccountInfo, "无法获取客户开户资料信息");
            // 初始化参数设置
            anOpenAccountInfo.initModifyValue(anExitsOpenAccountInfo);
            // 设置状态为使用中
            anOpenAccountInfo.setBusinStatus(CustomerConstants.TMP_STATUS_USEING);
            anOpenAccountInfo.setLastStatus(CustomerConstants.TMP_STATUS_USEING);
            // 申请日期
            anOpenAccountInfo.setApplyDate(BetterDateUtils.getNumDate());
            // 申请时间
            anOpenAccountInfo.setApplyTime(BetterDateUtils.getNumTime());
            // 处理附件
            anOpenAccountInfo.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anOpenAccountInfo.getBatchNo()));
            // 数据存盘,开户资料暂存
            this.updateByPrimaryKeySelective(anOpenAccountInfo);
        }
        // 写入开户日志
        custOpenAccountAuditService.addInitOpenAccountApplyLog(anOpenAccountInfo.getId(), "自动通过", "开户申请");

        return anOpenAccountInfo;
    }

    /**
     * 开户申请待审批列表
     * 
     * @param anFlag
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<CustOpenAccountTmp> queryOpenAccountApply(String anFlag, int anPageNum, int anPageSize) {
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("businStatus", CustomerConstants.TMP_STATUS_USEING);// 状态:使用中

        return this.selectPropertyByPage(CustOpenAccountTmp.class, anMap, anPageNum, anPageSize, "1".equals(anFlag));
    }

    /**
     * 开户审核生效
     * 
     * @param anId
     * @param anAuditOpinion
     * @return
     */
    public CustOpenAccountTmp saveAuditOpenAccountApply(Long anId, String anAuditOpinion) {
        // 检查操作员是否能执行审核操作
        checkPlatformUser();
        // 获取客户开户资料
        CustOpenAccountTmp anOpenAccountInfo = this.selectByPrimaryKey(anId);
        BTAssert.notNull(anOpenAccountInfo, "无法获取客户开户资料信息");
        // 检查开户资料合法性
        checkAccountInfoValid(anOpenAccountInfo);
        // 生成开户数据
        createValidAccount(anOpenAccountInfo, anOpenAccountInfo.getRegOperId(), anOpenAccountInfo.getRegOperName(), anOpenAccountInfo.getOperOrg());
        // 设置状态为已使用
        anOpenAccountInfo.setBusinStatus(CustomerConstants.TMP_STATUS_USED);
        anOpenAccountInfo.setLastStatus(CustomerConstants.TMP_STATUS_USED);
        // 审核日期
        anOpenAccountInfo.setAuditDate(BetterDateUtils.getNumDate());
        // 审核时间
        anOpenAccountInfo.setAuditTime(BetterDateUtils.getNumTime());
        // 更新数据
        this.updateByPrimaryKeySelective(anOpenAccountInfo);
        // 写入开户日志
        custOpenAccountAuditService.addAuditOpenAccountApplyLog(anOpenAccountInfo.getId(), anAuditOpinion, "开户审核");

        return anOpenAccountInfo;
    }

    /**
     * 开户申请驳回
     * 
     * @param anId
     * @param anAuditOpinion
     * @return
     */
    public CustOpenAccountTmp saveRefuseOpenAccountApply(Long anId, String anAuditOpinion) {
        // 检查操作员是否能执行审核操作
        checkPlatformUser();
        // 获取客户开户资料
        CustOpenAccountTmp anOpenAccountInfo = this.selectByPrimaryKey(anId);
        BTAssert.notNull(anOpenAccountInfo, "无法获取客户开户资料信息");
        // 设置状态为驳回
        anOpenAccountInfo.setBusinStatus(CustomerConstants.TMP_STATUS_REFUSE);
        anOpenAccountInfo.setLastStatus(CustomerConstants.TMP_STATUS_REFUSE);
        // 审核日期
        anOpenAccountInfo.setAuditDate(BetterDateUtils.getNumDate());
        // 审核时间
        anOpenAccountInfo.setAuditTime(BetterDateUtils.getNumTime());
        // 更新数据
        this.updateByPrimaryKeySelective(anOpenAccountInfo);
        // 写入开户日志
        custOpenAccountAuditService.addRefuseOpenAccountApplyLog(anOpenAccountInfo.getId(), anAuditOpinion, "开户审核");

        return anOpenAccountInfo;
    }

    /**
     * 代录开户资料提交
     * 
     * @param anOpenAccountInfo
     * @param anInsteadId
     * @param anFileList
     * @return
     */
    public CustOpenAccountTmp saveOpenAccountInfoByInstead(CustOpenAccountTmp anOpenAccountInfo, Long anInsteadId, String anFileList) {
        logger.info("Begin to Save Open Account Infomation Instead");
        // 代录流水号不能为空
        BTAssert.notNull(anInsteadId, "代录流水号不能为空");
        // 检查开户资料合法性
        checkAccountInfoValid(anOpenAccountInfo);
        // 检查是否已存在代录
        CustInsteadRecord anInsteadRecord = custInsteadRecordService.selectByPrimaryKey(anInsteadId);
        BTAssert.notNull(anInsteadRecord, "无法获取代录信息");
        // 获取代录暂存的开户资料ID号
        String anTempId = anInsteadRecord.getTmpIds();
        if (null == anTempId) {
            // 初始化参数设置
            initAddValue(anOpenAccountInfo, CustomerConstants.TMP_TYPE_INSTEADSTORE, CustomerConstants.TMP_STATUS_NEW);
            // 处理附件
            anOpenAccountInfo.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anOpenAccountInfo.getBatchNo()));
            // 数据存盘,开户资料暂存
            this.insert(anOpenAccountInfo);
        }
        else {
            // 加载已暂存的开户资料
            CustOpenAccountTmp anExitsOpenAccountInfo = this.selectByPrimaryKey(Long.valueOf(anTempId));
            // 初始化参数设置
            anOpenAccountInfo.initModifyValue(anExitsOpenAccountInfo);
            // 设置状态为未使用
            anOpenAccountInfo.setBusinStatus(CustomerConstants.TMP_STATUS_NEW);
            anOpenAccountInfo.setLastStatus(CustomerConstants.TMP_STATUS_NEW);
            // 处理附件
            anOpenAccountInfo.setBatchNo(custFileItemService.updateCustFileItemInfo(anFileList, anOpenAccountInfo.getBatchNo()));
            // 数据存盘,开户资料暂存
            this.updateByPrimaryKeySelective(anOpenAccountInfo);
        }
        // 回写暂存流水号至代录申请表
        custInsteadRecordService.saveInsteadRecord(anInsteadId, String.valueOf(anOpenAccountInfo.getId()));

        return anOpenAccountInfo;
    }

    /**
     * 代录开户资料读取
     * 
     * @param anInsteadId
     * @return
     */
    public CustOpenAccountTmp findOpenAccountInfoByInsteadId(Long anInsteadId) {
        // 检查是否已存在代录
        CustInsteadRecord anInsteadRecord = custInsteadRecordService.selectByPrimaryKey(anInsteadId);
        BTAssert.notNull(anInsteadRecord, "无法获取代录信息");
        // 获取代录暂存的开户资料ID号
        String anTempId = anInsteadRecord.getTmpIds();
        BTAssert.notNull(anTempId, "无法获取客户开户资料信息");

        return this.selectByPrimaryKey(Long.valueOf(anTempId));
    }

    /**
     * 客户确认开户
     */
    @Override
    public void saveFormalData(Long anParentId) {
        BTAssert.notNull(anParentId, "代录记录流水号不允许为空！");
        // 获取客户开户资料信息
        CustOpenAccountTmp anOpenAccountInfo = Collections3.getFirst(this.selectByProperty("parentId", anParentId));
        BTAssert.notNull(anOpenAccountInfo, "无法获取客户开户资料信息");
        // 检查开户资料合法性
        checkAccountInfoValid(anOpenAccountInfo);
        // 生成开户数据
        createValidAccount(anOpenAccountInfo, UserUtils.getOperatorInfo().getId(), UserUtils.getOperatorInfo().getName(),
                UserUtils.getOperatorInfo().getOperOrg());
        // 更新数据
        this.updateByPrimaryKeySelective(anOpenAccountInfo);
    }

    @Override
    public void saveCancelData(Long anParentId) {
        // TODO Auto-generated method stub

    }

    private void checkPlatformUser() {
        if (UserUtils.platformUser() == false) {
            logger.warn("当前操作员不能执行该操作");
            throw new BytterTradeException(40001, "当前操作员不能执行该操作");
        }
    }

    private void initAddValue(CustOpenAccountTmp anOpenAccountInfo, String anTmpType, String anBusinStatus) {
        anOpenAccountInfo.initAddValue();
        // 设置类型:自己暂存/平台操作员代录时暂存
        anOpenAccountInfo.setTmpType(anTmpType);
        // 设置状态为使用中
        anOpenAccountInfo.setBusinStatus(anBusinStatus);
        anOpenAccountInfo.setLastStatus(anBusinStatus);
    }

    private void createValidAccount(CustOpenAccountTmp anOpenAccountInfo, Long anOperId, String anOperName, String anOperOrg) {
        // 开户资料附件
        Long anBatchNo = anOpenAccountInfo.getBatchNo();

        // 开户资料附件信息
        Multimap<String, Object> anCustFileItem = ReflectionUtils.listConvertToMuiltMap(custFileItemService.findCustFiles(anBatchNo), "fileInfoType");

        // 数据存盘,客户资料
        CustInfo custInfo = addCustInfo(anOpenAccountInfo, anOperId, anOperName, anOperOrg);

        // 数据存盘,基本信息
        addCustMechBase(anOpenAccountInfo, custInfo.getCustNo(), anOperId, anOperName, anOperOrg);

        // 数据存盘,法人信息
        addCustMechLaw(anOpenAccountInfo, anCustFileItem, custInfo.getCustNo(), anOperId, anOperName, anOperOrg);

        // 数据存盘,营业执照
        addCustMechBusinLicence(anOpenAccountInfo, anCustFileItem, custInfo.getCustNo(), anOperId, anOperName, anOperOrg);

        // 数据存盘,银行账户
        addCustMechBankAccount(anOpenAccountInfo, anCustFileItem, custInfo.getCustNo(), anOperId, anOperName, anOperOrg);

        // 数据存盘,经办人信息
        addCustOperatorInfo(anOpenAccountInfo, anCustFileItem, custInfo.getCustNo(), anOperId, anOperName, anOperOrg);

        // 数据存盘,当前操作员关联客户
        custAndOperatorRelaService.insert(new CustOperatorRelation(anOperId, custInfo.getCustNo(), anOperOrg));

        // 回写客户编号
        anOpenAccountInfo.setCustNo(custInfo.getCustNo());
    }

    private CustInfo addCustInfo(CustOpenAccountTmp anOpenAccountInfo, Long anOperId, String anOperName, String anOperOrg) {
        CustInfo anCustInfo = new CustInfo();
        anCustInfo.setRegOperId(anOperId);
        anCustInfo.setRegOperName(anOperName);
        anCustInfo.setOperOrg(anOperOrg);
        anCustInfo.setCustNo(SerialGenerator.getCustNo());
        anCustInfo.setIdentValid(true);
        anCustInfo.setCustType(CustomerConstants.CUSTOMER_TYPE_ENTERPRISE);// 客户类型:0-机构;1-个人;
        anCustInfo.setCustName(anOpenAccountInfo.getCustName());
        anCustInfo.setIdentType("1");
        anCustInfo.setIdentNo(anOpenAccountInfo.getBusinLicence());
        anCustInfo.setVaildDate(anOpenAccountInfo.getBusinLicenceValidDate());
        anCustInfo.setRegDate(BetterDateUtils.getNumDate());
        anCustInfo.setRegTime(BetterDateUtils.getNumTime());
        anCustInfo.setBusinStatus("0");
        anCustInfo.setLastStatus("0");
        anCustInfo.setVersion(0l);
        custAccountService.insert(anCustInfo);

        return anCustInfo;
    }

    private void addCustMechBase(CustOpenAccountTmp anOpenAccountInfo, Long anCustNo, Long anOperId, String anOperName, String anOperOrg) {
        CustMechBase anCustMechBaseInfo = new CustMechBase();
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
        custMechBaseService.addCustMechBase(anCustMechBaseInfo, anCustNo);
    }

    private void addCustMechLaw(CustOpenAccountTmp anOpenAccountInfo, Multimap<String, Object> anCustFileItem, Long anCustNo, Long anOperId,
            String anOperName, String anOperOrg) {
        CustMechLaw anCustMechLawInfo = new CustMechLaw();
        anCustMechLawInfo.setCustNo(anCustNo);
        anCustMechLawInfo.setRegOperId(anOperId);
        anCustMechLawInfo.setRegOperName(anOperName);
        anCustMechLawInfo.setOperOrg(anOperOrg);
        anCustMechLawInfo.setName(anOpenAccountInfo.getLawName());
        anCustMechLawInfo.setIdentType(anOpenAccountInfo.getLawIdentType());
        anCustMechLawInfo.setIdentNo(anOpenAccountInfo.getLawIdentNo());
        anCustMechLawInfo.setValidDate(anOpenAccountInfo.getLawValidDate());
        anCustMechLawInfo.setSex(IdcardUtils.getGenderByIdCard(anOpenAccountInfo.getLawIdentNo(), anOpenAccountInfo.getLawIdentType()));
        anCustMechLawInfo.setBirthdate(IdcardUtils.getBirthByIdCard(anOpenAccountInfo.getLawIdentNo()));
        anCustMechLawInfo.setVersion(0l);
        // 附件：法人representIdFile
        Collection anCollection = anCustFileItem.get("representIdFile");
        List<CustFileItem> anFileItemList = new ArrayList(anCollection);
        if (anFileItemList.size() > 0) {
            Long anNewBatchNo = CustFileUtils.findBatchNo();
            // 更新文件信息,同时写入文件认证信息
            saveCustFileItem(anFileItemList, "representIdFile", anCustNo, anNewBatchNo, anOperId);
            // 更新附件批次号
            anCustMechLawInfo.setBatchNo(anNewBatchNo);
        }
        custMechLawService.addCustMechLaw(anCustMechLawInfo, anCustNo);
    }

    private void addCustMechBusinLicence(CustOpenAccountTmp anOpenAccountInfo, Multimap<String, Object> anCustFileItem, Long anCustNo, Long anOperId,
            String anOperName, String anOperOrg) {
        CustMechBusinLicence anCustMechBusinLicenceInfo = new CustMechBusinLicence();
        anCustMechBusinLicenceInfo.setCustNo(anCustNo);
        anCustMechBusinLicenceInfo.setRegOperId(anOperId);
        anCustMechBusinLicenceInfo.setRegOperName(anOperName);
        anCustMechBusinLicenceInfo.setOperOrg(anOperOrg);
        anCustMechBusinLicenceInfo.setRegNo(anOpenAccountInfo.getBusinLicence());
        anCustMechBusinLicenceInfo.setCertifiedDate(anOpenAccountInfo.getBusinLicenceRegDate());
        anCustMechBusinLicenceInfo.setOrgCode(anOpenAccountInfo.getOrgCode());
        anCustMechBusinLicenceInfo.setLawName(anOpenAccountInfo.getLawName());
        anCustMechBusinLicenceInfo.setEndDate(anOpenAccountInfo.getBusinLicenceValidDate());
        // 附件：组织机构代码证orgCodeFile
        Collection anOrgCodeCollection = anCustFileItem.get("orgCodeFile");
        List<CustFileItem> anOrgCodeFileItemList = new ArrayList(anOrgCodeCollection);
        // 附件：税务登记证taxRegistFile
        Collection anTaxRegistollection = anCustFileItem.get("orgCodeFile");
        List<CustFileItem> anTaxRegistFileItemList = new ArrayList(anTaxRegistollection);
        // 附件：营业执照bizLicenseFile
        Collection anBizLicenseCollection = anCustFileItem.get("orgCodeFile");
        List<CustFileItem> anBizLicenseFileItemList = new ArrayList(anBizLicenseCollection);
        // 企业三证附件信息同时处理
        if (anOrgCodeFileItemList.size() > 0 || anTaxRegistFileItemList.size() > 0 || anBizLicenseFileItemList.size() > 0) {
            Long anNewBatchNo = CustFileUtils.findBatchNo();
            // 附件：组织机构代码证orgCodeFile,更新文件信息,同时写入文件认证信息
            saveCustFileItem(anOrgCodeFileItemList, "orgCodeFile", anCustNo, anNewBatchNo, anOperId);
            // 附件：税务登记证taxRegistFile,更新文件信息,同时写入文件认证信息
            saveCustFileItem(anTaxRegistFileItemList, "taxRegistFile", anCustNo, anNewBatchNo, anOperId);
            // 附件：营业执照bizLicenseFile,更新文件信息,同时写入文件认证信息
            saveCustFileItem(anTaxRegistFileItemList, "bizLicenseFile", anCustNo, anNewBatchNo, anOperId);
            // 更新附件批次号
            anCustMechBusinLicenceInfo.setBatchNo(anNewBatchNo);
        }
        custMechBusinLicenceService.addBusinLicence(anCustMechBusinLicenceInfo, anCustNo);
    }

    private void addCustMechBankAccount(CustOpenAccountTmp anOpenAccountInfo, Multimap<String, Object> anCustFileItem, Long anCustNo, Long anOperId,
            String anOperName, String anOperOrg) {
        CustMechBankAccount anCustMechBankAccountInfo = new CustMechBankAccount();
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
        // 附件：银行账户bankAcctAckFile
        Collection anCollection = anCustFileItem.get("bankAcctAckFile");
        List<CustFileItem> anFileItemList = new ArrayList(anCollection);
        if (anFileItemList.size() > 0) {
            Long anNewBatchNo = CustFileUtils.findBatchNo();
            // 更新文件信息,同时写入文件认证信息
            saveCustFileItem(anFileItemList, "bankAcctAckFile", anCustNo, anNewBatchNo, anOperId);
            // 更新附件批次号
            anCustMechBankAccountInfo.setBatchNo(anNewBatchNo);
        }
        custMechBankAccountService.addCustMechBankAccount(anCustMechBankAccountInfo, anCustNo);
    }

    private void addCustOperatorInfo(CustOpenAccountTmp anOpenAccountInfo, Multimap<String, Object> anCustFileItem, Long anCustNo, Long anOperId,
            String anOperName, String anOperOrg) {
        CustOperatorInfo anCustOperatorInfo = new CustOperatorInfo();
        anCustOperatorInfo.setOperOrg(anOperOrg);
        anCustOperatorInfo.setId(SerialGenerator.getLongValue(SerialGenerator.OPERATOR_ID));
        anCustOperatorInfo.setName(anOpenAccountInfo.getOperName());
        anCustOperatorInfo.setIdentType(anOpenAccountInfo.getOperIdenttype());
        anCustOperatorInfo.setIdentNo(anOpenAccountInfo.getOperIdentno());
        anCustOperatorInfo.setMobileNo(anOpenAccountInfo.getOperMobile());
        anCustOperatorInfo.setPhone(anOpenAccountInfo.getOperPhone());
        anCustOperatorInfo.setIdentClass(anOpenAccountInfo.getOperIdenttype());
        anCustOperatorInfo.setValidDate(anOpenAccountInfo.getOperValiddate());
        anCustOperatorInfo.setStatus("1");
        anCustOperatorInfo.setLastStatus("1");
        anCustOperatorInfo.setSex(IdcardUtils.getGenderByIdCard(anCustOperatorInfo.getIdentNo(), anCustOperatorInfo.getIdentType()));
        anCustOperatorInfo.setRegDate(BetterDateUtils.getNumDate());
        anCustOperatorInfo.setModiDate(BetterDateUtils.getNumDateTime());
        anCustOperatorInfo.setFaxNo(anOpenAccountInfo.getOperFaxNo());
        anCustOperatorInfo.setAddress(anOpenAccountInfo.getAddress());
        anCustOperatorInfo.setEmail(anOpenAccountInfo.getOperEmail());
        anCustOperatorInfo.setZipCode(anOpenAccountInfo.getZipCode());
        custOperatorService.insert(anCustOperatorInfo);
        // 附件：经办人brokerIdFile
        Collection anCollection = anCustFileItem.get("brokerIdFile");
        List<CustFileItem> anFileItemList = new ArrayList(anCollection);
        if (anFileItemList.size() > 0) {
            Long anNewBatchNo = CustFileUtils.findBatchNo();
            // 更新文件信息,同时写入文件认证信息
            saveCustFileItem(anFileItemList, "brokerIdFile", anCustNo, anNewBatchNo, anOperId);
        }
    }

    /**
     * 更新文件信息
     * 
     * @param anCustFileItem
     * @param anFileInfoType
     * @param anCustNo
     * @param anBatchNo
     * @param anOperId
     */
    private void saveCustFileItem(List<CustFileItem> anCustFileItem, String anFileInfoType, Long anCustNo, Long anBatchNo, Long anOperId) {
        if (anCustFileItem.size() > 0) {
            // 写入文件信息
            for (CustFileItem fileItem : anCustFileItem) {
                addCustFileItem(fileItem, anBatchNo);
            }
            // 操作员编码
            String anOperCode = custOperatorService.selectByPrimaryKey(anOperId).getOperCode();
            // 写入文件认证信息
            addCustFileAduit(anCustNo, anBatchNo, anCustFileItem.size(), anFileInfoType, anOperCode);
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
     * 写入文件认证信息
     * 
     * @param anCustNo
     * @param anBatchNo
     * @param anFileCount
     * @param anFileInfoType
     * @param anOperCode
     */
    private void addCustFileAduit(Long anCustNo, Long anBatchNo, int anFileCount, String anFileInfoType, String anOperCode) {
        CustFileAduit anFileAudit = new CustFileAduit();
        anFileAudit.setId(anBatchNo);
        anFileAudit.setCustNo(anCustNo);
        anFileAudit.setFileCount(anFileCount);
        anFileAudit.setAuditStatus("1");
        anFileAudit.setWorkType(anFileInfoType);
        anFileAudit.setDescription("");
        anFileAudit.setRegDate(BetterDateUtils.getNumDate());
        anFileAudit.setRegTime(BetterDateUtils.getNumTime());
        anFileAudit.setOperNo(anOperCode);
        custFileAuditService.insert(anFileAudit);
    }

    private void checkAccountInfoValid(CustOpenAccountTmp anOpenAccountInfo) {
        // 检查开户资料入参
        checkAccountInfoParams(anOpenAccountInfo);

        // 检查申请机构名称是否存在
        if (checkCustExistsByCustName(anOpenAccountInfo.getCustName()) == true) {
            logger.warn("申请机构名称已存在");
            throw new BytterTradeException(40001, "申请机构名称已存在");
        }

        // 检查组织机构代码证是否存在
        if (checkCustExistsByIdentNo(anOpenAccountInfo.getOrgCode()) == true) {
            logger.warn("证件号码已存在");
            throw new BytterTradeException(40001, "证件号码已存在");
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
        String anFlag = blacklistService.checkBlacklistExists(anOpenAccountInfo.getCustName(), anOpenAccountInfo.getOrgCode(), anOpenAccountInfo.getLawName());
        if (BetterStringUtils.equals(anFlag, "1")){
            logger.warn("从黑名单库中检测到当前客户开户资料信息,请确认!");
            throw new BytterTradeException(40001, "从黑名单库中检测到当前客户开户资料信息,请确认!");
        }
    }

    private boolean checkCustExistsByCustName(String anCustName) {

        return custAccountService.selectByProperty("custName", anCustName).size() > 0;
    }

    private boolean checkCustExistsByIdentNo(String anIdentNo) {

        return custAccountService.selectByProperty("identNo", anIdentNo).size() > 0;
    }

    private boolean checkCustExistsByBusinLicence(String anBusinLicence) {

        return custMechBaseService.selectByProperty("businLicence", anBusinLicence).size() > 0;
    }

    private boolean checkCustExistsByBankAccount(String anBankAccount) {

        return custMechBankAccountService.selectByProperty("bankAcco", anBankAccount).size() > 0;
    }

    private void checkAccountInfoParams(CustOpenAccountTmp anOpenAccountInfo) {
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

}