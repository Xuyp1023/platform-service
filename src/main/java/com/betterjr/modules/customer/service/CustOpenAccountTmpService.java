package com.betterjr.modules.customer.service;

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
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.IdcardUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.account.data.CustContextInfo;
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
    private CustMechBankAccountService custMechBankAccountService;

    @Autowired
    private CustAndOperatorRelaService custAndOperatorRelaService;

    @Override
    public void saveFormalData(String... anTmpIds) {

    }

    /**
     * 开户资料提交,客户操作员录入开户资料后点击提交按钮触发
     * 
     * @param anOpenAccountData
     * @param anFileList
     */
    public CustOpenAccountTmp saveOpenAccount(CustOpenAccountTmp anOpenAccountData, Long anId, String anFileList) {
        // 检查开户资料合法性
        checkOpenAccountValidValid(anOpenAccountData);

        // 数据存盘,机构操作员
        CustContextInfo custContext = UserUtils.getOperatorContextInfo();
        CustOperatorInfo operator = new CustOperatorInfo(custContext.getOperatorInfo());
        if (null == operator.getId()) {
            initOperator(anOpenAccountData, operator);
            custOperatorService.insert(operator);
        }

        // 数据存盘,客户资料
        CustInfo custInfo = new CustInfo();
        initCustInfo(anOpenAccountData, operator, custInfo);
        custAccountService.insert(custInfo);

        System.out.println("=====================t_custinfo.custNo===========" + custInfo.getCustNo());

        // 数据存盘,机构操作员和客户关系
        custAndOperatorRelaService.insert(new CustOperatorRelation(operator.getId(), custInfo.getCustNo(), operator.getOperOrg()));

        // 数据存盘,机构基本信息
        CustMechBase custBaseInfo = new CustMechBase();
        custBaseInfo.initAddValue(custInfo.getCustNo());
        initCustBaseInfo(anOpenAccountData, custBaseInfo);
        custMechBaseService.insert(custBaseInfo);

        // 数据存盘,机构银行账户信息
        CustMechBankAccount custBankAccount = new CustMechBankAccount();
        custBankAccount.initAddValue();
        initCustBankAccount(anOpenAccountData, custBankAccount, custInfo.getCustNo());
        custMechBankAccountService.insert(custBankAccount);

        // 数据存盘,机构法人信息
        CustMechLaw custLaw = new CustMechLaw();
        custLaw.initAddValue();
        initCustLaw(anOpenAccountData, custLaw, custInfo.getCustNo());
        custMechLawService.insert(custLaw);

        if (anId != null) {
            CustOpenAccountTmp anTempData = this.selectByPrimaryKey(anId);
            BeanMapper.copy(anTempData, anOpenAccountData);
            // 设置状态为使用中
            anTempData.setBusinStatus(CustomerConstants.TMP_STATUS_USEING);
            // 更新数据
            this.updateByPrimaryKeySelective(anTempData);
            
            return anTempData;
        }

        return anOpenAccountData;
    }

    private void initCustBaseInfo(CustOpenAccountTmp anOpenAccountData, CustMechBase custBaseInfo) {
        custBaseInfo.setLawName(anOpenAccountData.getLawName());
        custBaseInfo.setLawIdentType(anOpenAccountData.getLawIdentType());
        custBaseInfo.setLawIdentNo(anOpenAccountData.getLawIdentNo());
        custBaseInfo.setLawValidDate(anOpenAccountData.getLawValidDate());
        custBaseInfo.setOrgCode(anOpenAccountData.getOrgCode());
        custBaseInfo.setBusinLicence(anOpenAccountData.getBusinLicence());
        custBaseInfo.setAddress(anOpenAccountData.getAddress());
        custBaseInfo.setPhone(anOpenAccountData.getPhone());
        custBaseInfo.setFax(anOpenAccountData.getFax());
        custBaseInfo.setVersion(0l);
    }

    private void initOperator(CustOpenAccountTmp anOpenAccountData, CustOperatorInfo operator) {
        operator.setId(SerialGenerator.getLongValue(SerialGenerator.OPERATOR_ID));
        operator.setName(anOpenAccountData.getOperName());
        operator.setIdentType(anOpenAccountData.getOperIdenttype());
        operator.setIdentNo(anOpenAccountData.getOperIdentno());
        operator.setMobileNo(anOpenAccountData.getOperMobile());
        operator.setPhone(anOpenAccountData.getOperPhone());
        operator.setIdentClass(anOpenAccountData.getOperIdenttype());
        operator.setValidDate(anOpenAccountData.getOperValiddate());
        operator.setStatus("1");
        operator.setLastStatus("1");
        operator.setSex(IdcardUtils.getGenderByIdCard(operator.getIdentNo(), operator.getIdentType()));
        operator.setRegDate(BetterDateUtils.getNumDate());
        operator.setModiDate(BetterDateUtils.getNumDateTime());
        operator.setFaxNo(anOpenAccountData.getOperFaxNo());
        operator.setAddress(anOpenAccountData.getAddress());
        operator.setEmail(anOpenAccountData.getOperEmail());
        operator.setZipCode(anOpenAccountData.getZipCode());
    }

    private void initCustInfo(CustOpenAccountTmp anOpenAccountData, CustOperatorInfo operator, CustInfo custInfo) {
        custInfo.setCustNo(SerialGenerator.getCustNo());
        custInfo.setCustType("0");// 客户类型:0-机构;1-个人;
        custInfo.setCustName(anOpenAccountData.getCustName());
        custInfo.setIdentType("1");
        custInfo.setIdentNo(anOpenAccountData.getBusinLicence());
        custInfo.setVaildDate(anOpenAccountData.getBusinLicenceValidDate());
        custInfo.setIdentValid(false);
        custInfo.setRegOperId(operator.getId());
        custInfo.setRegOperName(operator.getName());
        custInfo.setRegDate(BetterDateUtils.getNumDate());
        custInfo.setRegTime(BetterDateUtils.getNumTime());
        custInfo.setOperOrg(operator.getOperOrg());
        custInfo.setBusinStatus("0");
        custInfo.setLastStatus("0");
        custInfo.setVersion(0l);
    }

    private void initCustLaw(CustOpenAccountTmp anOpenAccountData, CustMechLaw custLaw, Long anCustNo) {
        custLaw.setCustNo(anCustNo);
        custLaw.setName(anOpenAccountData.getLawName());
        custLaw.setIdentType(anOpenAccountData.getLawIdentType());
        custLaw.setIdentNo(anOpenAccountData.getLawIdentNo());
        custLaw.setValidDate(anOpenAccountData.getLawValidDate());
        custLaw.setSex(IdcardUtils.getGenderByIdCard(anOpenAccountData.getLawIdentNo(), anOpenAccountData.getLawIdentType()));
        custLaw.setBirthdate(IdcardUtils.getBirthByIdCard(anOpenAccountData.getLawIdentNo()));
        custLaw.setVersion(0l);
    }

    private void initCustBankAccount(CustOpenAccountTmp anOpenAccountData, CustMechBankAccount custBankAccount, Long anCustNo) {
        custBankAccount.setCustNo(anCustNo);
        custBankAccount.setIsDefault(true);
        custBankAccount.setTradeAcco("");
        custBankAccount.setBankNo(anOpenAccountData.getBankNo());
        custBankAccount.setBankName(anOpenAccountData.getBankName());
        custBankAccount.setBankAcco(anOpenAccountData.getBankAcco());
        custBankAccount.setBankAccoName(anOpenAccountData.getBankAccoName());
        custBankAccount.setBankBranch("");
        custBankAccount.setNetNo("");
        custBankAccount.setPayCenter("");
        custBankAccount.setAuthStatus("0");
        custBankAccount.setSignStatus("0");
        custBankAccount.setIdentType(anOpenAccountData.getIdentType());
        custBankAccount.setIdentNo(anOpenAccountData.getIdentNo());
        custBankAccount.setFlag("");
        custBankAccount.setBakupAcco("");
        custBankAccount.setCountyName("");
        custBankAccount.setCityNo(anOpenAccountData.getBankCityno());
        custBankAccount.setCityName("");
        custBankAccount.setAccoStatus("0");
        custBankAccount.setVersion(0l);
    }

    /**
     * 开户资料暂存,适用于客户操作员录入开户资料时暂存按钮
     * 
     * @param anOpenAccountData:开户资料
     * @param anFileList
     * @return
     */
    public CustOpenAccountTmp saveOpenAccountTemp(CustOpenAccountTmp anOpenAccountData, String anFileList) {
        logger.info("Begin to Save Open Account Temp Data");

        // 检查开户资料合法性
        checkOpenAccountValidValid(anOpenAccountData);

        // 检查是否已存在暂存记录
        CustOpenAccountTmp anTempData = findOpenAccountTemp();
        if (null == anTempData) {
            // 设置类型:自己暂存
            anOpenAccountData.setTmpType(CustomerConstants.TMP_TYPE_TEMPSTORE);
            // 数据存盘,开户资料暂存
            addOpenAccountTemp(anOpenAccountData);
        }
        else {
            // 初始化参数设置
            anOpenAccountData.initModifyValue(anTempData);
            // 数据存盘,开户资料暂存
            this.updateByPrimaryKeySelective(anOpenAccountData);
        }

        // 按附件类型处理批次号

        return anOpenAccountData;
    }

    private void addOpenAccountTemp(CustOpenAccountTmp anOpenAccountData) {
        // 初始化参数设置
        anOpenAccountData.initAddValue();
        // 数据存盘,开户数据暂存
        this.insert(anOpenAccountData);
    }

    /**
     * 开户资料暂存,适用于平台操作员开户代录时暂存
     * 
     * @param anOpenAccountData:开户资料
     * @param anInsteadRecordId:代录ID
     * @param anFileList
     * @return
     */
    public CustOpenAccountTmp saveOpenAccountInsteadTemp(CustOpenAccountTmp anOpenAccountData, Long anInsteadRecordId, String anFileList) {
        logger.info("Begin to Save Open Account Temp Data");

        // 检查开户资料合法性
        checkOpenAccountValidValid(anOpenAccountData);

        // 代录流水号不能为空
        BTAssert.notNull(anInsteadRecordId, "代录流水号不能为空");

        // 检查是否已存在代录
        CustInsteadRecord anInsteadRecord = custInsteadRecordService.selectByPrimaryKey(anInsteadRecordId);
        BTAssert.notNull(anInsteadRecord, "无法获取代录信息");

        // 获取代录暂存的开户资料ID号
        String anTempId = anInsteadRecord.getTmpIds();
        if (null == anTempId) {
            // 设置类型:代录暂存
            anOpenAccountData.setTmpType(CustomerConstants.TMP_TYPE_INSTEADSTORE);
            // 数据存盘,开户资料暂存
            addOpenAccountTemp(anOpenAccountData);
        }
        else {
            // 加载已暂存的开户资料
            CustOpenAccountTmp anTempData = this.selectByPrimaryKey(Long.valueOf(anTempId));
            // 初始化参数设置
            anOpenAccountData.initModifyValue(anTempData);
            // 数据存盘,开户资料暂存
            this.updateByPrimaryKeySelective(anOpenAccountData);
        }

        // 按附件类型处理批次号

        // 回写暂存流水号至代录申请表
        custInsteadRecordService.saveCustInsteadRecordTmp(anInsteadRecordId, String.valueOf(anOpenAccountData.getId()));

        return anOpenAccountData;
    }

    /**
     * 客户操作员点击开户菜单加载开户暂存信息
     * 
     * @return
     */
    public CustOpenAccountTmp findOpenAccountTemp() {
        // 构造查询条件
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("operOrg", UserUtils.getOperatorInfo().getOperOrg());
        anMap.put("businStatus", CustomerConstants.TMP_STATUS_NEW);
        anMap.put("tmpType", CustomerConstants.TMP_TYPE_TEMPSTORE);
        // 获取开户资料暂存记录
        List<CustOpenAccountTmp> result = this.selectByProperty(anMap);

        return Collections3.getFirst(result);
    }

    /**
     * 平台操作员查看代录时暂存的开户资料
     * 
     * @param anInsteadRecordId:代录ID
     * @return
     */
    public CustOpenAccountTmp findOpenAccountTempByInsteadId(Long anInsteadRecordId) {
        // 代录流水号不能为空
        BTAssert.notNull(anInsteadRecordId, "代录流水号不能为空");

        // 检查是否已存在代录
        CustInsteadRecord anInsteadRecord = custInsteadRecordService.selectByPrimaryKey(anInsteadRecordId);
        BTAssert.notNull(anInsteadRecord, "无法获取代录信息");

        // 获取代录暂存的开户资料ID号
        String anTempId = anInsteadRecord.getTmpIds();
        BTAssert.notNull(anTempId, "当前代录记录中不存在暂存的开户资料信息");

        // 加载已暂存的开户资料
        return this.selectByPrimaryKey(Long.valueOf(anTempId));
    }

    /**
     * 平台操作员查看代录时暂存的开户资料
     * 
     * @param anInsteadRecordId:代录ID
     * @return
     */

    private void checkOpenAccountValidValid(CustOpenAccountTmp anOpenAccountData) {
        // 检查开户资料入参
        checkOpenAccountParams(anOpenAccountData);

        // 检查申请机构名称是否存在
        if (checkCustExistsByCustName(anOpenAccountData.getCustName()) == true) {
            logger.warn("申请机构名称已存在");
            throw new BytterTradeException(40001, "申请机构名称已存在");
        }

        // 检查组织机构代码证是否存在
        if (checkCustExistsByIdentNo(anOpenAccountData.getOrgCode()) == true) {
            logger.warn("证件号码已存在");
            throw new BytterTradeException(40001, "证件号码已存在");
        }

        // 检查营业执照号码是否存在
        if (checkCustExistsByBusinLicence(anOpenAccountData.getBusinLicence()) == true) {
            logger.warn("营业执照号码已存在");
            throw new BytterTradeException(40001, "营业执照号码已存在");
        }

        // 检查银行账号是否存在
        if (checkCustExistsByBankAccount(anOpenAccountData.getBankAcco()) == true) {
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

    private void checkOpenAccountParams(CustOpenAccountTmp anOpenAccountData) {
        // 客户资料检查
        BTAssert.notNull(anOpenAccountData.getCustName(), "申请机构名称不能为空");
        BTAssert.notNull(anOpenAccountData.getOrgCode(), "组织机构代码证不能为空");
        BTAssert.notNull(anOpenAccountData.getBusinLicence(), "营业执照号码不能为空");
        BTAssert.notNull(anOpenAccountData.getBusinLicenceValidDate(), "营业执照有效期不能为空");
        BTAssert.notNull(anOpenAccountData.getZipCode(), "邮政编码不能为空");
        BTAssert.notNull(anOpenAccountData.getAddress(), "联系地址不能为空");
        BTAssert.notNull(anOpenAccountData.getPhone(), "业务联系电话不能为空");
        BTAssert.notNull(anOpenAccountData.getFax(), "传真号码不能为空");
        // 交收行信息检查
        BTAssert.notNull(anOpenAccountData.getBankAccoName(), "银行账户名不能为空");
        BTAssert.notNull(anOpenAccountData.getBankAcco(), "银行账户不能为空");
        BTAssert.notNull(anOpenAccountData.getBankNo(), "所属银行不能为空");
        BTAssert.notNull(anOpenAccountData.getBankCityno(), "开户银行所在地不能为空");
        BTAssert.notNull(anOpenAccountData.getBankName(), "开户银行全称不能为空");
        // 经办人信息检查
        BTAssert.notNull(anOpenAccountData.getOperName(), "经办人姓名不能为空");
        BTAssert.notNull(anOpenAccountData.getOperIdenttype(), "经办人证件类型不能为空");
        BTAssert.notNull(anOpenAccountData.getOperIdentno(), "经办人证件号码不能为空");
        BTAssert.notNull(anOpenAccountData.getOperValiddate(), "经办人证件有效期不能为空");
        BTAssert.notNull(anOpenAccountData.getOperMobile(), "经办人手机号码不能为空");
        BTAssert.notNull(anOpenAccountData.getOperEmail(), "经办人邮箱不能为空");
        BTAssert.notNull(anOpenAccountData.getOperPhone(), "经办人联系电话不能为空");
        // 法人信息检查
        BTAssert.notNull(anOpenAccountData.getLawName(), "法人姓名不能为空");
        BTAssert.notNull(anOpenAccountData.getLawIdentType(), "法人证件类型不能为空");
        BTAssert.notNull(anOpenAccountData.getLawIdentNo(), "法人证件号码不能为空");
        BTAssert.notNull(anOpenAccountData.getLawValidDate(), "法人证件有效期不能为空");
    }

}