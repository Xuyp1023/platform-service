package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.selectkey.SerialGenerator;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.IdcardUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.account.entity.CustOperatorRelation;
import com.betterjr.modules.account.service.CustAccountService;
import com.betterjr.modules.account.service.CustAndOperatorRelaService;
import com.betterjr.modules.account.service.CustOperatorService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.dao.CustOpenAccountTmpMapper;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.entity.CustMechBankAccount;
import com.betterjr.modules.customer.entity.CustMechBase;
import com.betterjr.modules.customer.entity.CustMechBusinLicence;
import com.betterjr.modules.customer.entity.CustMechLaw;
import com.betterjr.modules.customer.entity.CustOpenAccountTmp;
import com.betterjr.modules.customer.helper.IFormalDataService;

/**
 * 
 * @author liuwl
 *
 */
@Service
public class CustOpenAccountTmpService extends BaseService<CustOpenAccountTmpMapper, CustOpenAccountTmp> implements IFormalDataService {

    @Autowired
    private CustMechLawService custMechLawService;

    @Autowired
    private CustAccountService custAccountService;

    @Autowired
    private CustMechBaseService custMechBaseService;

    @Autowired
    private CustOperatorService custOperatorService;

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
    public CustOpenAccountTmp findOpenAccountInfoByOperator() {
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("operOrg", UserUtils.getOperatorInfo().getOperOrg());
        anMap.put("businStatus", CustomerConstants.TMP_STATUS_NEW);
        anMap.put("tmpType", CustomerConstants.TMP_TYPE_TEMPSTORE);

        return Collections3.getFirst(this.selectByProperty(anMap));
    }

    /**
     * 开户资料暂存
     * 
     * @param anOpenAccountInfo
     * @return
     */
    public CustOpenAccountTmp saveOpenAccountInfo(CustOpenAccountTmp anOpenAccountInfo) {
        logger.info("Begin to Save Open Account Infomation");
        // 检查开户资料合法性
        checkAccountInfoValid(anOpenAccountInfo);
        // 按当前操作员查找是否存在暂存记录,以此保证每个操作员当前只能有一条未生效的暂存开户记录
        CustOpenAccountTmp anExitsOpenAccountInfo = findOpenAccountInfoByOperator();
        if (null == anExitsOpenAccountInfo) {
            // 初始化参数设置
            initAddValue(anOpenAccountInfo, CustomerConstants.TMP_TYPE_TEMPSTORE, CustomerConstants.TMP_STATUS_NEW);
            // 数据存盘,开户资料暂存
            this.insert(anOpenAccountInfo);
        }
        else {
            // 初始化参数设置
            initModifyValue(anOpenAccountInfo, anExitsOpenAccountInfo, CustomerConstants.TMP_STATUS_NEW);
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
    public CustOpenAccountTmp saveOpenAccountApply(CustOpenAccountTmp anOpenAccountInfo, Long anId) {
        logger.info("Begin to Commit Open Account Apply");
        // 检查开户资料合法性
        checkAccountInfoValid(anOpenAccountInfo);
        // 开户流水号
        if (null == anId) {
            // 初始化参数设置
            initAddValue(anOpenAccountInfo, CustomerConstants.TMP_TYPE_TEMPSTORE, CustomerConstants.TMP_STATUS_USEING);
            // 数据存盘,开户资料暂存
            this.insert(anOpenAccountInfo);
        }
        else {
            // 加载已暂存的开户资料
            CustOpenAccountTmp anExitsOpenAccountInfo = this.selectByPrimaryKey(anId);
            // 初始化参数设置
            initModifyValue(anOpenAccountInfo, anExitsOpenAccountInfo, CustomerConstants.TMP_STATUS_USEING);
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
        initValidAccount(anOpenAccountInfo, anOpenAccountInfo.getRegOperId(), anOpenAccountInfo.getRegOperName(), anOpenAccountInfo.getOperOrg());
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
        // 设置状态为未使用
        anOpenAccountInfo.setBusinStatus(CustomerConstants.TMP_STATUS_NEW);
        anOpenAccountInfo.setLastStatus(CustomerConstants.TMP_STATUS_NEW);
        // 更新数据
        this.updateByPrimaryKeySelective(anOpenAccountInfo);
        // 写入开户日志
        custOpenAccountAuditService.addRefuseOpenAccountApplyLog(anOpenAccountInfo.getId(), anAuditOpinion, "开户审核");

        return anOpenAccountInfo;
    }

    /**
     * 代录开户资料暂存
     * 
     * @param anOpenAccountInfo
     * @param anInsteadId
     * @return
     */
    public CustOpenAccountTmp saveOpenAccountInfoByInstead(CustOpenAccountTmp anOpenAccountInfo, Long anInsteadId) {
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
            // 数据存盘,开户资料暂存
            this.insert(anOpenAccountInfo);
        }
        else {
            // 加载已暂存的开户资料
            CustOpenAccountTmp anExitsOpenAccountInfo = this.selectByPrimaryKey(Long.valueOf(anTempId));
            // 初始化参数设置
            initModifyValue(anOpenAccountInfo, anExitsOpenAccountInfo, CustomerConstants.TMP_STATUS_NEW);
            // 数据存盘,开户资料暂存
            this.updateByPrimaryKeySelective(anOpenAccountInfo);
        }
        // 回写暂存流水号至代录申请表
        custInsteadRecordService.saveCustInsteadRecordTmp(anInsteadId, String.valueOf(anOpenAccountInfo.getId()));

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
    public void saveFormalData(String... anTmpIds) {
        BTAssert.notEmpty(anTmpIds, "临时流水编号不允许为空！");
        if (anTmpIds.length != 1) {
            logger.warn("临时流水编号只能有一位！");
            throw new BytterTradeException(40001, "临时流水编号只能有一位！");
        }
        // 获取客户开户资料信息
        CustOpenAccountTmp anOpenAccountInfo = this.selectByPrimaryKey(Long.valueOf(anTmpIds[0]));
        BTAssert.notNull(anOpenAccountInfo, "无法获取客户开户资料信息");
        // 检查开户资料合法性
        checkAccountInfoValid(anOpenAccountInfo);
        // 生成开户数据
        initValidAccount(anOpenAccountInfo, UserUtils.getOperatorInfo().getId(), UserUtils.getOperatorInfo().getName(),
                UserUtils.getOperatorInfo().getOperOrg());
        // 更新数据
        this.updateByPrimaryKeySelective(anOpenAccountInfo);
    }
    
    @Override
    public void saveCancelData(String... anTmpIds) {
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

    private void initModifyValue(CustOpenAccountTmp anOpenAccountInfo, CustOpenAccountTmp anExitsOpenAccountInfo, String anBusinStatus) {
        // 初始化参数设置
        anOpenAccountInfo.initModifyValue(anExitsOpenAccountInfo);
        // 设置状态为使用中
        anOpenAccountInfo.setBusinStatus(anBusinStatus);
        anOpenAccountInfo.setLastStatus(anBusinStatus);
    }

    private void initValidAccount(CustOpenAccountTmp anOpenAccountInfo, Long anOperId, String anOperName, String anOperOrg) {
        // 数据存盘,客户资料
        CustInfo custInfo = new CustInfo();
        initCustInfo(custInfo, anOpenAccountInfo);
        custInfo.setRegOperId(anOperId);
        custInfo.setRegOperName(anOperName);
        custInfo.setOperOrg(anOperOrg);
        custAccountService.insert(custInfo);

        // 数据存盘,基本信息
        CustMechBase custMechBaseInfo = new CustMechBase();
        initCustBaseInfo(custMechBaseInfo, anOpenAccountInfo);
        custMechBaseInfo.setRegOperId(anOperId);
        custMechBaseInfo.setRegOperName(anOperName);
        custMechBaseInfo.setOperOrg(anOperOrg);
        custMechBaseService.addCustMechBase(custMechBaseInfo, custInfo.getCustNo());

        // 数据存盘,法人信息
        CustMechLaw custMechLawInfo = new CustMechLaw();
        initCustLaw(custMechLawInfo, anOpenAccountInfo, custInfo.getCustNo());
        custMechLawInfo.setRegOperId(anOperId);
        custMechLawInfo.setRegOperName(anOperName);
        custMechLawInfo.setOperOrg(anOperOrg);
        custMechLawService.addCustMechLaw(custMechLawInfo, custInfo.getCustNo());

        // 数据存盘,营业执照
        CustMechBusinLicence custMechBusinLicenceInfo = new CustMechBusinLicence();
        initBusinLicence(custMechBusinLicenceInfo, anOpenAccountInfo, custInfo.getCustNo());
        custMechBusinLicenceInfo.setRegOperId(anOperId);
        custMechBusinLicenceInfo.setRegOperName(anOperName);
        custMechBusinLicenceInfo.setOperOrg(anOperOrg);
        custMechBusinLicenceService.addCustMechBusinLicence(custMechBusinLicenceInfo, custInfo.getCustNo());

        // 数据存盘,银行账户
        CustMechBankAccount custMechBankAccountInfo = new CustMechBankAccount();
        initCustBankAccount(custMechBankAccountInfo, anOpenAccountInfo, custInfo.getCustNo());
        custMechBankAccountInfo.setRegOperId(anOperId);
        custMechBankAccountInfo.setRegOperName(anOperName);
        custMechBankAccountInfo.setOperOrg(anOperOrg);
        custMechBankAccountService.addCustMechBankAccount(custMechBankAccountInfo, custInfo.getCustNo());

        // 数据存盘,经办人信息
        CustOperatorInfo operator = new CustOperatorInfo();
        initOperator(operator, anOpenAccountInfo);
        operator.setOperOrg(anOperOrg);
        custOperatorService.insert(operator);

        // 数据存盘,当前操作员关联客户
        custAndOperatorRelaService.insert(new CustOperatorRelation(anOperId, custInfo.getCustNo(), anOperOrg));

        // 回写客户编号
        anOpenAccountInfo.setCustNo(custInfo.getCustNo());

        // 设置状态为已使用
        anOpenAccountInfo.setBusinStatus(CustomerConstants.TMP_STATUS_USED);
        anOpenAccountInfo.setLastStatus(CustomerConstants.TMP_STATUS_USED);
    }

    private void initCustInfo(CustInfo anCustInfo, CustOpenAccountTmp anOpenAccountInfo) {
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
    }

    private void initCustBaseInfo(CustMechBase anCustMechBaseInfo, CustOpenAccountTmp anOpenAccountInfo) {
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
    }

    private void initCustLaw(CustMechLaw anCustMechLawInfo, CustOpenAccountTmp anOpenAccountInfo, Long anCustNo) {
        anCustMechLawInfo.setCustNo(anCustNo);
        anCustMechLawInfo.setName(anOpenAccountInfo.getLawName());
        anCustMechLawInfo.setIdentType(anOpenAccountInfo.getLawIdentType());
        anCustMechLawInfo.setIdentNo(anOpenAccountInfo.getLawIdentNo());
        anCustMechLawInfo.setValidDate(anOpenAccountInfo.getLawValidDate());
        anCustMechLawInfo.setSex(IdcardUtils.getGenderByIdCard(anOpenAccountInfo.getLawIdentNo(), anOpenAccountInfo.getLawIdentType()));
        anCustMechLawInfo.setBirthdate(IdcardUtils.getBirthByIdCard(anOpenAccountInfo.getLawIdentNo()));
        anCustMechLawInfo.setVersion(0l);
    }

    private void initBusinLicence(CustMechBusinLicence anCustMechBusinLicenceInfo, CustOpenAccountTmp anOpenAccountInfo, Long anCustNo) {
        anCustMechBusinLicenceInfo.setRegNo(anOpenAccountInfo.getBusinLicence());
        anCustMechBusinLicenceInfo.setCertifiedDate(anOpenAccountInfo.getBusinLicenceRegDate());
        anCustMechBusinLicenceInfo.setOrgCode(anOpenAccountInfo.getOrgCode());
        anCustMechBusinLicenceInfo.setLawName(anOpenAccountInfo.getLawName());
        anCustMechBusinLicenceInfo.setEndDate(anOpenAccountInfo.getBusinLicenceValidDate());
    }

    private void initCustBankAccount(CustMechBankAccount anCustMechBankAccountInfo, CustOpenAccountTmp anOpenAccountInfo, Long anCustNo) {
        anCustMechBankAccountInfo.setCustNo(anCustNo);
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
    }

    private void initOperator(CustOperatorInfo anCustOperatorInfo, CustOpenAccountTmp anOpenAccountInfo) {
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