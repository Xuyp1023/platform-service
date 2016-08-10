package com.betterjr.modules.customer.entity;

import java.math.BigDecimal;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.betterjr.common.annotation.MetaData;
import com.betterjr.common.entity.BetterjrEntity;
import com.betterjr.common.mapper.CustDateJsonSerializer;
import com.betterjr.common.selectkey.SerialGenerator;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@Access(AccessType.FIELD)
@Entity
@Table(name = "t_cust_mech_base_tmp")
public class CustMechBaseTmp implements BetterjrEntity {
    /**
     * 编号
     */
    @Id
    @Column(name = "ID", columnDefinition = "INTEGER")
    @MetaData(value = "编号", comments = "编号")
    private Long id;
    
    /**
     * 代录记录/变更申请 编号
     */
    @Column(name = "L_PARENTID",  columnDefinition="INTEGER" )
    @MetaData( value="代录记录/变更申请 编号", comments = "代录记录/变更申请 编号")
    private Long parentId;
    
    /**
     * 客户编号
     */
    @Column(name = "L_CUSTNO",  columnDefinition="INTEGER" )
    @MetaData( value="客户编号", comments = "客户编号")
    private Long custNo;
    
    /**
     * 客户全称
     */
    @Column(name = "C_CUSTNAME",  columnDefinition="VARCHAR" )
    @MetaData( value="客户全称", comments = "客户全称")
    private String custName;
    
    /**
     * 数据版本号
     */
    @JsonIgnore
    @Column(name = "N_VERSION", columnDefinition = "INTEGER")
    @MetaData(value = "数据版本号", comments = "数据版本号")
    private Long version;

    /**
     * 英文名称
     */
    @JsonIgnore
    @Column(name = "C_ENG_NAME", columnDefinition = "VARCHAR")
    @MetaData(value = "英文名称", comments = "英文名称")
    private String engName;

    /**
     * 机构类别；企业法人、机关法人、事业法人、社团法人、工会法人、其他非金融机构法人、证券公司、银行、信托投资公司、基金管理公司、保险公司、其他金融机构法人、普通合伙企业、特殊普通合伙企业、有限合伙企业、非法人非合伙制创投企业、境外一般机构、境外代理人、境外证券公司、境外基金公司、破产管理人
     * 、中国金融期货交易所、其他
     */
    @JsonIgnore
    @Column(name = "C_CATEGORY", columnDefinition = "VARCHAR")
    @MetaData(value = "机构类别", comments = "机构类别；企业法人、机关法人、事业法人、社团法人、工会法人、其他非金融机构法人、证券公司、银行、信托投资公司、基金管理公司、保险公司、其他金融机构法人、普通合伙企业、特殊普通合伙企业、有限合伙企业、非法人非合伙制创投企业、境外一般机构、境外代理人、境外证券公司、境外基金公司、破产管理人、中国金融期货交易所、其他")
    private String category;

    /**
     * 企业类型: 0国有企业 1集体所有制企业 2私营企业 3股份制企业 4联营企业 5外商投资企业 6港澳台投资企业 7股份合作企业
     */
    @Column(name = "C_CORP_TYPE",  columnDefinition="VARCHAR" )
    @MetaData( value="企业类型", comments = "企业类型: 0国有企业 1集体所有制企业 2私营企业 3股份制企业 4联营企业 5外商投资企业 6港澳台投资企业 7股份合作企业")
    private String corpType;
    
    /**
     * 国有属性；国务院国资委管辖、地方国资委管辖、其他国有企业、非国有
     */
    @JsonIgnore
    @Column(name = "C_NATION_TYPE", columnDefinition = "VARCHAR")
    @MetaData(value = "国有属性", comments = "国有属性；国务院国资委管辖、地方国资委管辖、其他国有企业、非国有")
    private String nationType;

    /**
     * 资本属性；境内资本、三资（合资、合作、外资）、境外资本
     */
    @JsonIgnore
    @Column(name = "C_CAPITAL_TYPE", columnDefinition = "CHAR")
    @MetaData(value = "资本属性", comments = "资本属性；境内资本、三资（合资、合作、外资）、境外资本")
    private String capitalType;

    /**
     * 法人姓名
     */
    @JsonIgnore
    @Column(name = "C_LAW_NAME", columnDefinition = "VARCHAR")
    @MetaData(value = "法人姓名", comments = "法人姓名")
    private String lawName;

    /**
     * 法人代表联系电话
     */
    @JsonIgnore
    @Column(name = "C_LAW_PHONE", columnDefinition = "VARCHAR")
    @MetaData(value = "法人代表联系电话", comments = "法人代表联系电话")
    private String lawPhone;

    /**
     * 法人证件号码
     */
    @JsonIgnore
    @Column(name = "C_LAW_IDENTNO", columnDefinition = "VARCHAR")
    @MetaData(value = "法人证件号码", comments = "法人证件号码")
    private String lawIdentNo;

    /**
     * 法人证件类型:0-身份证，1-护照，2-军官证，3-士兵证，4-回乡证，5-户口本，6-外国护照
     */
    @JsonIgnore
    @Column(name = "C_LAW_IDENTTYPE", columnDefinition = "CHAR")
    @MetaData(value = "法人证件类型:0-身份证", comments = "法人证件类型:0-身份证，1-护照，2-军官证，3-士兵证，4-回乡证，5-户口本，6-外国护照")
    private String lawIdentType;

    /**
     * 法人证件有效期
     */
    @JsonIgnore
    @Column(name = "D_LAW_VALIDDATE", columnDefinition = "VARCHAR")
    @MetaData(value = "法人证件有效期", comments = "法人证件有效期")
    private String lawValidDate;

    /**
     * 企业注册地址
     */
    @Column(name = "C_REG_ADDRESS", columnDefinition = "VARCHAR")
    @MetaData(value = "企业注册地址", comments = "企业注册地址")
    private String regAddress;

    /**
     * 行业
     */
    @JsonIgnore
    @Column(name = "C_CORP_VOCATION", columnDefinition = "VARCHAR")
    @MetaData(value = "行业", comments = "行业")
    private String corpVocation;

    /**
     * 企业性质；0-国企，1-民营，2-合资，3-境外资本，9-其它
     */
    @JsonIgnore
    @Column(name = "C_CORP_PROPERTY", columnDefinition = "CHAR")
    @MetaData(value = "企业性质", comments = "企业性质；0-国企，1-民营，2-合资，3-境外资本，9-其它")
    private String corpProperty;

    /**
     * 投资经历 0：无经验，1：1-3年，2：3-5年，3：5-10年，4：10年以上
     */
    @JsonIgnore
    @Column(name = "C_INVEST", columnDefinition = "CHAR")
    @MetaData(value = "投资经历 0：无经验", comments = "投资经历 0：无经验，1：1-3年，2：3-5年，3：5-10年，4：10年以上")
    private String invest;

    /**
     * 机构类型；0-保险机构，1-基金公司，2-上市公司，3-信托公司，4-证券公司，5-理财产品，6-企业年金，7-社保基金，8-其他机构
     */
    @JsonIgnore
    @Column(name = "C_INST_TYPE", columnDefinition = "CHAR")
    @MetaData(value = "机构类型", comments = "机构类型；0-保险机构，1-基金公司，2-上市公司，3-信托公司，4-证券公司，5-理财产品，6-企业年金，7-社保基金，8-其他机构")
    private String instType;

    /**
     * 客户分类；01普通客户；02企业年金计划；03银行、券商、信托公司等的理财产品或理财计划；04保险产品；05社保基金组合；09其他
     */
    @JsonIgnore
    @Column(name = "C_CUST_CLASS", columnDefinition = "CHAR")
    @MetaData(value = "客户分类", comments = "客户分类；01普通客户；02企业年金计划；03银行、券商、信托公司等的理财产品或理财计划；04保险产品；05社保基金组合；09其他")
    private String custClass;

    /**
     * 组织机构代码证
     */
    @Column(name = "C_ORG_CODE", columnDefinition = "VARCHAR")
    @MetaData(value = "组织机构代码证", comments = "组织机构代码证")
    private String orgCode;

    /**
     * 营业执照号码
     */
    @Column(name = "C_BUSIN_LICENCE", columnDefinition = "VARCHAR")
    @MetaData(value = "营业执照号码", comments = "营业执照号码")
    private String businLicence;

    /**
     * 营业执照登记日期
     */
    @JsonIgnore
    @Column(name = "D_BUSIN_LICENCE_REGDATE", columnDefinition = "VARCHAR")
    @MetaData(value = "营业执照登记日期", comments = "营业执照登记日期")
    private String businLicenceRegDate;

    /**
     * 营业执照截止日期
     */
    @JsonIgnore
    @Column(name = "D_BUSIN_LICENCE_VALIDDATE", columnDefinition = "VARCHAR")
    @MetaData(value = "营业执照截止日期", comments = "营业执照截止日期")
    private String businLicenceValidDate;

    /**
     * 注册资本
     */
    @Column(name = "C_REG_CAPITAL", columnDefinition = "VARCHAR")
    @MetaData(value = "注册资本", comments = "注册资本")
    private String regCapital;

    /**
     * 实收资本
     */
    @Column(name = "C_PAID_CAPITAL", columnDefinition = "VARCHAR")
    @MetaData(value = "实收资本", comments = "实收资本")
    private String paidCapital;

    /**
     * 人数
     */
    @Column(name = "N_PERSON", columnDefinition = "INTEGER")
    @MetaData(value = "人数", comments = "人数")
    private Long person;

    /**
     * 经营面积（平方米）
     */
    @Column(name = "F_PREMISES_AREA", columnDefinition = "DOUBLE")
    @MetaData(value = "经营面积（平方米）", comments = "经营面积（平方米）")
    private BigDecimal premisesArea;

    /**
     * 经营场地所有权年限(年)
     */
    @Column(name = "N_PREMISES_YEAR", columnDefinition = "INTEGER")
    @MetaData(value = "经营场地所有权年限(年)", comments = "经营场地所有权年限(年)")
    private Long premisesYear;

    /**
     * 经营场地类型
     */
    @Column(name = "C_PREMISES_TYPE", columnDefinition = "CHAR")
    @MetaData(value = "经营场地类型", comments = "经营场地类型")
    private String premisesType;

    /**
     * 经营场地地址
     */
    @Column(name = "C_PREMISES_ADDRESS", columnDefinition = "VARCHAR")
    @MetaData(value = "经营场地地址", comments = "经营场地地址")
    private String premisesAddress;

    /**
     * 经营范围
     */
    @Column(name = "C_BUSIN_SCOPE", columnDefinition = "VARCHAR")
    @MetaData(value = "经营范围", comments = "经营范围")
    private String businScope;

    /**
     * 成立日期
     */
    @Column(name = "D_SETUP_DATE", columnDefinition = "VARCHAR")
    @MetaData(value = "成立日期", comments = "成立日期")
    private String setupDate;

    /**
     * 地址
     */
    @Column(name = "C_ADDRESS", columnDefinition = "VARCHAR")
    @MetaData(value = "地址", comments = "地址")
    private String address;

    /**
     * 邮编
     */
    @JsonIgnore
    @Column(name = "C_ZIPCODE", columnDefinition = "VARCHAR")
    @MetaData(value = "邮编", comments = "邮编")
    private String zipCode;

    /**
     * 电话
     */
    @Column(name = "C_PHONE", columnDefinition = "VARCHAR")
    @MetaData(value = "电话", comments = "电话")
    private String phone;

    /**
     * 传真
     */
    @JsonIgnore
    @Column(name = "C_FAX", columnDefinition = "VARCHAR")
    @MetaData(value = "传真", comments = "传真")
    private String fax;

    /**
     * 移动电话
     */
    @JsonIgnore
    @Column(name = "C_MOBILE", columnDefinition = "VARCHAR")
    @MetaData(value = "移动电话", comments = "移动电话")
    private String mobile;

    /**
     * 电子邮件
     */
    @Column(name = "C_EMAIL", columnDefinition = "VARCHAR")
    @MetaData(value = "电子邮件", comments = "电子邮件")
    private String email;

    /**
     * 微信
     */
    @JsonIgnore
    @Column(name = "C_WECHAT", columnDefinition = "VARCHAR")
    @MetaData(value = "微信", comments = "微信")
    private String wechat;

    /**
     * QQ
     */
    @JsonIgnore
    @Column(name = "C_QQ", columnDefinition = "VARCHAR")
    @MetaData(value = "QQ", comments = "QQ")
    private String qq;

    /**
     * 网址
     */
    @JsonIgnore
    @Column(name = "C_WEBADDR", columnDefinition = "VARCHAR")
    @MetaData(value = "网址", comments = "网址")
    private String webaddr;

    /**
     * 企业名称
     */
    @JsonIgnore
    @Column(name = "C_CORPNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "企业名称", comments = "企业名称")
    private String corpName;

    /**
     * 城市地区代码
     */
    @JsonIgnore
    @Column(name = "C_CITYNO", columnDefinition = "VARCHAR")
    @MetaData(value = "城市地区代码", comments = "城市地区代码")
    private String cityNo;

    /**
     * 附件
     */
    @Column(name = "N_BATCHNO", columnDefinition = "INTEGER")
    @MetaData(value = "附件", comments = "附件")
    private Long batchNo;

    /**
     * 创建人(操作员)ID号
     */
    @JsonIgnore
    @Column(name = "L_REG_OPERID", columnDefinition = "INTEGER")
    @MetaData(value = "创建人(操作员)ID号", comments = "创建人(操作员)ID号")
    private Long regOperId;

    /**
     * 创建人(操作员)姓名
     */
    @JsonIgnore
    @Column(name = "C_REG_OPERNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "创建人(操作员)姓名", comments = "创建人(操作员)姓名")
    private String regOperName;

    /**
     * 创建日期
     */
    @JsonSerialize(using = CustDateJsonSerializer.class)
    @Column(name = "D_REG_DATE", columnDefinition = "VARCHAR")
    @MetaData(value = "创建日期", comments = "创建日期")
    private String regDate;

    /**
     * 创建时间
     */
    @JsonIgnore
    @Column(name = "T_REG_TIME", columnDefinition = "VARCHAR")
    @MetaData(value = "创建时间", comments = "创建时间")
    private String regTime;

    /**
     * 修改人(操作员)ID号
     */
    @JsonIgnore
    @Column(name = "L_MODI_OPERID", columnDefinition = "INTEGER")
    @MetaData(value = "修改人(操作员)ID号", comments = "修改人(操作员)ID号")
    private Long modiOperId;

    /**
     * 修改人(操作员)姓名
     */
    @JsonIgnore
    @Column(name = "C_MODI_OPERNAME", columnDefinition = "VARCHAR")
    @MetaData(value = "修改人(操作员)姓名", comments = "修改人(操作员)姓名")
    private String modiOperName;

    /**
     * 修改日期
     */
    @JsonSerialize(using = CustDateJsonSerializer.class)
    @Column(name = "D_MODI_DATE", columnDefinition = "VARCHAR")
    @MetaData(value = "修改日期", comments = "修改日期")
    private String modiDate;

    /**
     * 修改时间
     */
    @JsonIgnore
    @Column(name = "T_MODI_TIME", columnDefinition = "VARCHAR")
    @MetaData(value = "修改时间", comments = "修改时间")
    private String modiTime;

    /**
     * 操作机构
     */
    @JsonIgnore
    @Column(name = "C_OPERORG", columnDefinition = "VARCHAR")
    @MetaData(value = "操作机构", comments = "操作机构")
    private String operOrg;

    /**
     * 使用状态:0未使用 1使用中 2已使用
     */
    @Column(name = "C_BUSIN_STATUS", columnDefinition = "CHAR")
    @MetaData(value = "使用状态:0未使用  1使用中  2已使用", comments = "使用状态:0未使用  1使用中  2已使用")
    private String businStatus;

    @Column(name = "C_LAST_STATUS", columnDefinition = "CHAR")
    @MetaData(value = "", comments = "")
    private String lastStatus;

    /**
     * 引用编号
     */
    @Column(name = "L_REF_ID", columnDefinition = "INTEGER")
    @MetaData(value = "引用编号", comments = "引用编号")
    private Long refId;

    /**
     * 流水类型:0 代录 1 变更 2 暂存
     */
    @Column(name = "C_TMP_TYPE", columnDefinition = "CHAR")
    @MetaData(value = "流水类型", comments = "流水类型:0 代录  1 变更  2 暂存 3 初始信息")
    private String tmpType;

    /**
     * 流水操作类型:0 新增 1 修改 2 删除 3 未改变
     */
    @Column(name = "C_TMP_OPER_TYPE", columnDefinition = "CHAR")
    @MetaData(value = "流水操作类型", comments = "流水操作类型:0 新增  1 修改  2 删除  3 未改变")
    private String tmpOperType;

    private static final long serialVersionUID = 1468812783857L;

    public Long getId() {
        return id;
    }

    public void setId(Long anId) {
        id = anId;
    }

    public Long getCustNo() {
        return custNo;
    }

    public void setCustNo(Long anCustNo) {
        custNo = anCustNo;
    }
    
    public String getCustName() {
        return custName;
    }

    public void setCustName(String anCustName) {
        custName = anCustName;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getEngName() {
        return engName;
    }

    public void setEngName(String engName) {
        this.engName = engName == null ? null : engName.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category == null ? null : category.trim();
    }

    public String getCorpType() {
        return corpType;
    }

    public void setCorpType(String anCorpType) {
        corpType = anCorpType;
    }

    public String getNationType() {
        return nationType;
    }

    public void setNationType(String nationType) {
        this.nationType = nationType == null ? null : nationType.trim();
    }

    public String getCapitalType() {
        return capitalType;
    }

    public void setCapitalType(String capitalType) {
        this.capitalType = capitalType == null ? null : capitalType.trim();
    }

    public String getLawName() {
        return lawName;
    }

    public void setLawName(String lawName) {
        this.lawName = lawName == null ? null : lawName.trim();
    }

    public String getLawPhone() {
        return lawPhone;
    }

    public void setLawPhone(String lawPhone) {
        this.lawPhone = lawPhone == null ? null : lawPhone.trim();
    }

    public String getLawIdentNo() {
        return lawIdentNo;
    }

    public void setLawIdentNo(String lawIdentNo) {
        this.lawIdentNo = lawIdentNo == null ? null : lawIdentNo.trim();
    }

    public String getLawIdentType() {
        return lawIdentType;
    }

    public void setLawIdentType(String lawIdentType) {
        this.lawIdentType = lawIdentType == null ? null : lawIdentType.trim();
    }

    public String getLawValidDate() {
        return lawValidDate;
    }

    public void setLawValidDate(String lawValidDate) {
        this.lawValidDate = lawValidDate == null ? null : lawValidDate.trim();
    }

    public String getRegAddress() {
        return regAddress;
    }

    public void setRegAddress(String regAddress) {
        this.regAddress = regAddress == null ? null : regAddress.trim();
    }

    public String getCorpVocation() {
        return corpVocation;
    }

    public void setCorpVocation(String corpVocation) {
        this.corpVocation = corpVocation == null ? null : corpVocation.trim();
    }

    public String getCorpProperty() {
        return corpProperty;
    }

    public void setCorpProperty(String corpProperty) {
        this.corpProperty = corpProperty == null ? null : corpProperty.trim();
    }

    public String getInvest() {
        return invest;
    }

    public void setInvest(String invest) {
        this.invest = invest == null ? null : invest.trim();
    }

    public String getInstType() {
        return instType;
    }

    public void setInstType(String instType) {
        this.instType = instType == null ? null : instType.trim();
    }

    public String getCustClass() {
        return custClass;
    }

    public void setCustClass(String custClass) {
        this.custClass = custClass == null ? null : custClass.trim();
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode == null ? null : orgCode.trim();
    }

    public String getBusinLicence() {
        return businLicence;
    }

    public void setBusinLicence(String businLicence) {
        this.businLicence = businLicence == null ? null : businLicence.trim();
    }

    public String getBusinLicenceRegDate() {
        return businLicenceRegDate;
    }

    public void setBusinLicenceRegDate(String businLicenceRegDate) {
        this.businLicenceRegDate = businLicenceRegDate == null ? null : businLicenceRegDate.trim();
    }

    public String getBusinLicenceValidDate() {
        return businLicenceValidDate;
    }

    public void setBusinLicenceValidDate(String businLicenceValidDate) {
        this.businLicenceValidDate = businLicenceValidDate == null ? null : businLicenceValidDate.trim();
    }

    public String getRegCapital() {
        return regCapital;
    }

    public void setRegCapital(String regCapital) {
        this.regCapital = regCapital == null ? null : regCapital.trim();
    }

    public String getPaidCapital() {
        return paidCapital;
    }

    public void setPaidCapital(String paidCapital) {
        this.paidCapital = paidCapital == null ? null : paidCapital.trim();
    }

    public Long getPerson() {
        return person;
    }

    public void setPerson(Long person) {
        this.person = person;
    }

    public BigDecimal getPremisesArea() {
        return premisesArea;
    }

    public void setPremisesArea(BigDecimal premisesArea) {
        this.premisesArea = premisesArea;
    }

    public Long getPremisesYear() {
        return premisesYear;
    }

    public void setPremisesYear(Long premisesYear) {
        this.premisesYear = premisesYear;
    }

    public String getPremisesType() {
        return premisesType;
    }

    public void setPremisesType(String premisesType) {
        this.premisesType = premisesType == null ? null : premisesType.trim();
    }

    public String getPremisesAddress() {
        return premisesAddress;
    }

    public void setPremisesAddress(String premisesAddress) {
        this.premisesAddress = premisesAddress == null ? null : premisesAddress.trim();
    }

    public String getBusinScope() {
        return businScope;
    }

    public void setBusinScope(String businScope) {
        this.businScope = businScope == null ? null : businScope.trim();
    }

    public String getSetupDate() {
        return setupDate;
    }

    public void setSetupDate(String setupDate) {
        this.setupDate = setupDate == null ? null : setupDate.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String anAddress) {
        address = anAddress;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode == null ? null : zipCode.trim();
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone == null ? null : phone.trim();
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax == null ? null : fax.trim();
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email == null ? null : email.trim();
    }

    public String getWechat() {
        return wechat;
    }

    public void setWechat(String wechat) {
        this.wechat = wechat == null ? null : wechat.trim();
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq == null ? null : qq.trim();
    }

    public String getWebaddr() {
        return webaddr;
    }

    public void setWebaddr(String webaddr) {
        this.webaddr = webaddr == null ? null : webaddr.trim();
    }

    public String getCorpName() {
        return corpName;
    }

    public void setCorpName(String corpName) {
        this.corpName = corpName == null ? null : corpName.trim();
    }

    public String getCityNo() {
        return cityNo;
    }

    public void setCityNo(String cityNo) {
        this.cityNo = cityNo == null ? null : cityNo.trim();
    }

    public Long getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(Long batchNo) {
        this.batchNo = batchNo;
    }

    public Long getRegOperId() {
        return regOperId;
    }

    public void setRegOperId(Long regOperId) {
        this.regOperId = regOperId;
    }

    public String getRegOperName() {
        return regOperName;
    }

    public void setRegOperName(String regOperName) {
        this.regOperName = regOperName == null ? null : regOperName.trim();
    }

    public String getRegDate() {
        return regDate;
    }

    public void setRegDate(String regDate) {
        this.regDate = regDate == null ? null : regDate.trim();
    }

    public String getRegTime() {
        return regTime;
    }

    public void setRegTime(String regTime) {
        this.regTime = regTime == null ? null : regTime.trim();
    }

    public Long getModiOperId() {
        return modiOperId;
    }

    public void setModiOperId(Long modiOperId) {
        this.modiOperId = modiOperId;
    }

    public String getModiOperName() {
        return modiOperName;
    }

    public void setModiOperName(String modiOperName) {
        this.modiOperName = modiOperName == null ? null : modiOperName.trim();
    }

    public String getModiDate() {
        return modiDate;
    }

    public void setModiDate(String modiDate) {
        this.modiDate = modiDate == null ? null : modiDate.trim();
    }

    public String getModiTime() {
        return modiTime;
    }

    public void setModiTime(String modiTime) {
        this.modiTime = modiTime == null ? null : modiTime.trim();
    }

    public String getOperOrg() {
        return operOrg;
    }

    public void setOperOrg(String operOrg) {
        this.operOrg = operOrg == null ? null : operOrg.trim();
    }

    public String getBusinStatus() {
        return businStatus;
    }

    public void setBusinStatus(String businStatus) {
        this.businStatus = businStatus == null ? null : businStatus.trim();
    }

    public String getLastStatus() {
        return lastStatus;
    }

    public void setLastStatus(String lastStatus) {
        this.lastStatus = lastStatus == null ? null : lastStatus.trim();
    }

    public Long getRefId() {
        return refId;
    }

    public void setRefId(Long refId) {
        this.refId = refId;
    }

    public String getTmpType() {
        return tmpType;
    }

    public void setTmpType(String tmpType) {
        this.tmpType = tmpType == null ? null : tmpType.trim();
    }

    public String getTmpOperType() {
        return tmpOperType;
    }

    public void setTmpOperType(String tmpOperType) {
        this.tmpOperType = tmpOperType == null ? null : tmpOperType.trim();
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long anParentId) {
        parentId = anParentId;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", parentId=").append(parentId);
        sb.append(", custNo=").append(custNo);
        sb.append(", custName=").append(custName);
        sb.append(", version=").append(version);
        sb.append(", engName=").append(engName);
        sb.append(", category=").append(category);
        sb.append(", corpType=").append(corpType);
        sb.append(", nationType=").append(nationType);
        sb.append(", capitalType=").append(capitalType);
        sb.append(", lawName=").append(lawName);
        sb.append(", lawPhone=").append(lawPhone);
        sb.append(", lawIdentNo=").append(lawIdentNo);
        sb.append(", lawIdentType=").append(lawIdentType);
        sb.append(", lawValidDate=").append(lawValidDate);
        sb.append(", regAddress=").append(regAddress);
        sb.append(", corpVocation=").append(corpVocation);
        sb.append(", corpProperty=").append(corpProperty);
        sb.append(", invest=").append(invest);
        sb.append(", instType=").append(instType);
        sb.append(", custClass=").append(custClass);
        sb.append(", orgCode=").append(orgCode);
        sb.append(", businLicence=").append(businLicence);
        sb.append(", businLicenceRegDate=").append(businLicenceRegDate);
        sb.append(", businLicenceValidDate=").append(businLicenceValidDate);
        sb.append(", regCapital=").append(regCapital);
        sb.append(", paidCapital=").append(paidCapital);
        sb.append(", person=").append(person);
        sb.append(", premisesArea=").append(premisesArea);
        sb.append(", premisesYear=").append(premisesYear);
        sb.append(", premisesType=").append(premisesType);
        sb.append(", premisesAddress=").append(premisesAddress);
        sb.append(", businScope=").append(businScope);
        sb.append(", setupDate=").append(setupDate);
        sb.append(", address=").append(address);
        sb.append(", zipCode=").append(zipCode);
        sb.append(", phone=").append(phone);
        sb.append(", fax=").append(fax);
        sb.append(", mobile=").append(mobile);
        sb.append(", email=").append(email);
        sb.append(", wechat=").append(wechat);
        sb.append(", qq=").append(qq);
        sb.append(", webaddr=").append(webaddr);
        sb.append(", corpName=").append(corpName);
        sb.append(", cityNo=").append(cityNo);
        sb.append(", batchNo=").append(batchNo);
        sb.append(", regOperId=").append(regOperId);
        sb.append(", regOperName=").append(regOperName);
        sb.append(", regDate=").append(regDate);
        sb.append(", regTime=").append(regTime);
        sb.append(", modiOperId=").append(modiOperId);
        sb.append(", modiOperName=").append(modiOperName);
        sb.append(", modiDate=").append(modiDate);
        sb.append(", modiTime=").append(modiTime);
        sb.append(", operOrg=").append(operOrg);
        sb.append(", businStatus=").append(businStatus);
        sb.append(", lastStatus=").append(lastStatus);
        sb.append(", refId=").append(refId);
        sb.append(", tmpType=").append(tmpType);
        sb.append(", tmpOperType=").append(tmpOperType);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        CustMechBaseTmp other = (CustMechBaseTmp) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getParentId() == null ? other.getParentId() == null : this.getParentId().equals(other.getParentId()))
                && (this.getCustNo() == null ? other.getCustNo() == null : this.getCustNo().equals(other.getCustNo()))
                && (this.getCustName() == null ? other.getCustName() == null : this.getCustName().equals(other.getCustName()))
                && (this.getVersion() == null ? other.getVersion() == null : this.getVersion().equals(other.getVersion()))
                && (this.getEngName() == null ? other.getEngName() == null : this.getEngName().equals(other.getEngName()))
                && (this.getCategory() == null ? other.getCategory() == null : this.getCategory().equals(other.getCategory()))
                && (this.getCorpType() == null ? other.getCorpType() == null : this.getCorpType().equals(other.getCorpType()))
                && (this.getNationType() == null ? other.getNationType() == null : this.getNationType().equals(other.getNationType()))
                && (this.getCapitalType() == null ? other.getCapitalType() == null : this.getCapitalType().equals(other.getCapitalType()))
                && (this.getLawName() == null ? other.getLawName() == null : this.getLawName().equals(other.getLawName()))
                && (this.getLawPhone() == null ? other.getLawPhone() == null : this.getLawPhone().equals(other.getLawPhone()))
                && (this.getLawIdentNo() == null ? other.getLawIdentNo() == null : this.getLawIdentNo().equals(other.getLawIdentNo()))
                && (this.getLawIdentType() == null ? other.getLawIdentType() == null : this.getLawIdentType().equals(other.getLawIdentType()))
                && (this.getLawValidDate() == null ? other.getLawValidDate() == null : this.getLawValidDate().equals(other.getLawValidDate()))
                && (this.getRegAddress() == null ? other.getRegAddress() == null : this.getRegAddress().equals(other.getRegAddress()))
                && (this.getCorpVocation() == null ? other.getCorpVocation() == null : this.getCorpVocation().equals(other.getCorpVocation()))
                && (this.getCorpProperty() == null ? other.getCorpProperty() == null : this.getCorpProperty().equals(other.getCorpProperty()))
                && (this.getInvest() == null ? other.getInvest() == null : this.getInvest().equals(other.getInvest()))
                && (this.getInstType() == null ? other.getInstType() == null : this.getInstType().equals(other.getInstType()))
                && (this.getCustClass() == null ? other.getCustClass() == null : this.getCustClass().equals(other.getCustClass()))
                && (this.getOrgCode() == null ? other.getOrgCode() == null : this.getOrgCode().equals(other.getOrgCode()))
                && (this.getBusinLicence() == null ? other.getBusinLicence() == null : this.getBusinLicence().equals(other.getBusinLicence()))
                && (this.getBusinLicenceRegDate() == null ? other.getBusinLicenceRegDate() == null
                        : this.getBusinLicenceRegDate().equals(other.getBusinLicenceRegDate()))
                && (this.getBusinLicenceValidDate() == null ? other.getBusinLicenceValidDate() == null
                        : this.getBusinLicenceValidDate().equals(other.getBusinLicenceValidDate()))
                && (this.getRegCapital() == null ? other.getRegCapital() == null : this.getRegCapital().equals(other.getRegCapital()))
                && (this.getPaidCapital() == null ? other.getPaidCapital() == null : this.getPaidCapital().equals(other.getPaidCapital()))
                && (this.getPerson() == null ? other.getPerson() == null : this.getPerson().equals(other.getPerson()))
                && (this.getPremisesArea() == null ? other.getPremisesArea() == null : this.getPremisesArea().equals(other.getPremisesArea()))
                && (this.getPremisesYear() == null ? other.getPremisesYear() == null : this.getPremisesYear().equals(other.getPremisesYear()))
                && (this.getPremisesType() == null ? other.getPremisesType() == null : this.getPremisesType().equals(other.getPremisesType()))
                && (this.getPremisesAddress() == null ? other.getPremisesAddress() == null
                        : this.getPremisesAddress().equals(other.getPremisesAddress()))
                && (this.getBusinScope() == null ? other.getBusinScope() == null : this.getBusinScope().equals(other.getBusinScope()))
                && (this.getSetupDate() == null ? other.getSetupDate() == null : this.getSetupDate().equals(other.getSetupDate()))
                && (this.getAddress() == null ? other.getAddress() == null : this.getAddress().equals(other.getAddress()))
                && (this.getZipCode() == null ? other.getZipCode() == null : this.getZipCode().equals(other.getZipCode()))
                && (this.getPhone() == null ? other.getPhone() == null : this.getPhone().equals(other.getPhone()))
                && (this.getFax() == null ? other.getFax() == null : this.getFax().equals(other.getFax()))
                && (this.getMobile() == null ? other.getMobile() == null : this.getMobile().equals(other.getMobile()))
                && (this.getEmail() == null ? other.getEmail() == null : this.getEmail().equals(other.getEmail()))
                && (this.getWechat() == null ? other.getWechat() == null : this.getWechat().equals(other.getWechat()))
                && (this.getQq() == null ? other.getQq() == null : this.getQq().equals(other.getQq()))
                && (this.getWebaddr() == null ? other.getWebaddr() == null : this.getWebaddr().equals(other.getWebaddr()))
                && (this.getCorpName() == null ? other.getCorpName() == null : this.getCorpName().equals(other.getCorpName()))
                && (this.getCityNo() == null ? other.getCityNo() == null : this.getCityNo().equals(other.getCityNo()))
                && (this.getBatchNo() == null ? other.getBatchNo() == null : this.getBatchNo().equals(other.getBatchNo()))
                && (this.getRegOperId() == null ? other.getRegOperId() == null : this.getRegOperId().equals(other.getRegOperId()))
                && (this.getRegOperName() == null ? other.getRegOperName() == null : this.getRegOperName().equals(other.getRegOperName()))
                && (this.getRegDate() == null ? other.getRegDate() == null : this.getRegDate().equals(other.getRegDate()))
                && (this.getRegTime() == null ? other.getRegTime() == null : this.getRegTime().equals(other.getRegTime()))
                && (this.getModiOperId() == null ? other.getModiOperId() == null : this.getModiOperId().equals(other.getModiOperId()))
                && (this.getModiOperName() == null ? other.getModiOperName() == null : this.getModiOperName().equals(other.getModiOperName()))
                && (this.getModiDate() == null ? other.getModiDate() == null : this.getModiDate().equals(other.getModiDate()))
                && (this.getModiTime() == null ? other.getModiTime() == null : this.getModiTime().equals(other.getModiTime()))
                && (this.getOperOrg() == null ? other.getOperOrg() == null : this.getOperOrg().equals(other.getOperOrg()))
                && (this.getBusinStatus() == null ? other.getBusinStatus() == null : this.getBusinStatus().equals(other.getBusinStatus()))
                && (this.getLastStatus() == null ? other.getLastStatus() == null : this.getLastStatus().equals(other.getLastStatus()))
                && (this.getRefId() == null ? other.getRefId() == null : this.getRefId().equals(other.getRefId()))
                && (this.getTmpType() == null ? other.getTmpType() == null : this.getTmpType().equals(other.getTmpType()))
                && (this.getTmpOperType() == null ? other.getTmpOperType() == null : this.getTmpOperType().equals(other.getTmpOperType()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getParentId() == null) ? 0 : getParentId().hashCode());
        result = prime * result + ((getCustNo() == null) ? 0 : getCustNo().hashCode());
        result = prime * result + ((getCustName() == null) ? 0 : getCustName().hashCode());
        result = prime * result + ((getVersion() == null) ? 0 : getVersion().hashCode());
        result = prime * result + ((getEngName() == null) ? 0 : getEngName().hashCode());
        result = prime * result + ((getCategory() == null) ? 0 : getCategory().hashCode());
        result = prime * result + ((getCorpType() == null) ? 0 : getCorpType().hashCode());
        result = prime * result + ((getNationType() == null) ? 0 : getNationType().hashCode());
        result = prime * result + ((getCapitalType() == null) ? 0 : getCapitalType().hashCode());
        result = prime * result + ((getLawName() == null) ? 0 : getLawName().hashCode());
        result = prime * result + ((getLawPhone() == null) ? 0 : getLawPhone().hashCode());
        result = prime * result + ((getLawIdentNo() == null) ? 0 : getLawIdentNo().hashCode());
        result = prime * result + ((getLawIdentType() == null) ? 0 : getLawIdentType().hashCode());
        result = prime * result + ((getLawValidDate() == null) ? 0 : getLawValidDate().hashCode());
        result = prime * result + ((getRegAddress() == null) ? 0 : getRegAddress().hashCode());
        result = prime * result + ((getCorpVocation() == null) ? 0 : getCorpVocation().hashCode());
        result = prime * result + ((getCorpProperty() == null) ? 0 : getCorpProperty().hashCode());
        result = prime * result + ((getInvest() == null) ? 0 : getInvest().hashCode());
        result = prime * result + ((getInstType() == null) ? 0 : getInstType().hashCode());
        result = prime * result + ((getCustClass() == null) ? 0 : getCustClass().hashCode());
        result = prime * result + ((getOrgCode() == null) ? 0 : getOrgCode().hashCode());
        result = prime * result + ((getBusinLicence() == null) ? 0 : getBusinLicence().hashCode());
        result = prime * result + ((getBusinLicenceRegDate() == null) ? 0 : getBusinLicenceRegDate().hashCode());
        result = prime * result + ((getBusinLicenceValidDate() == null) ? 0 : getBusinLicenceValidDate().hashCode());
        result = prime * result + ((getRegCapital() == null) ? 0 : getRegCapital().hashCode());
        result = prime * result + ((getPaidCapital() == null) ? 0 : getPaidCapital().hashCode());
        result = prime * result + ((getPerson() == null) ? 0 : getPerson().hashCode());
        result = prime * result + ((getPremisesArea() == null) ? 0 : getPremisesArea().hashCode());
        result = prime * result + ((getPremisesYear() == null) ? 0 : getPremisesYear().hashCode());
        result = prime * result + ((getPremisesType() == null) ? 0 : getPremisesType().hashCode());
        result = prime * result + ((getPremisesAddress() == null) ? 0 : getPremisesAddress().hashCode());
        result = prime * result + ((getBusinScope() == null) ? 0 : getBusinScope().hashCode());
        result = prime * result + ((getSetupDate() == null) ? 0 : getSetupDate().hashCode());
        result = prime * result + ((getAddress() == null) ? 0 : getAddress().hashCode());
        result = prime * result + ((getZipCode() == null) ? 0 : getZipCode().hashCode());
        result = prime * result + ((getPhone() == null) ? 0 : getPhone().hashCode());
        result = prime * result + ((getFax() == null) ? 0 : getFax().hashCode());
        result = prime * result + ((getMobile() == null) ? 0 : getMobile().hashCode());
        result = prime * result + ((getEmail() == null) ? 0 : getEmail().hashCode());
        result = prime * result + ((getWechat() == null) ? 0 : getWechat().hashCode());
        result = prime * result + ((getQq() == null) ? 0 : getQq().hashCode());
        result = prime * result + ((getWebaddr() == null) ? 0 : getWebaddr().hashCode());
        result = prime * result + ((getCorpName() == null) ? 0 : getCorpName().hashCode());
        result = prime * result + ((getCityNo() == null) ? 0 : getCityNo().hashCode());
        result = prime * result + ((getBatchNo() == null) ? 0 : getBatchNo().hashCode());
        result = prime * result + ((getRegOperId() == null) ? 0 : getRegOperId().hashCode());
        result = prime * result + ((getRegOperName() == null) ? 0 : getRegOperName().hashCode());
        result = prime * result + ((getRegDate() == null) ? 0 : getRegDate().hashCode());
        result = prime * result + ((getRegTime() == null) ? 0 : getRegTime().hashCode());
        result = prime * result + ((getModiOperId() == null) ? 0 : getModiOperId().hashCode());
        result = prime * result + ((getModiOperName() == null) ? 0 : getModiOperName().hashCode());
        result = prime * result + ((getModiDate() == null) ? 0 : getModiDate().hashCode());
        result = prime * result + ((getModiTime() == null) ? 0 : getModiTime().hashCode());
        result = prime * result + ((getOperOrg() == null) ? 0 : getOperOrg().hashCode());
        result = prime * result + ((getBusinStatus() == null) ? 0 : getBusinStatus().hashCode());
        result = prime * result + ((getLastStatus() == null) ? 0 : getLastStatus().hashCode());
        result = prime * result + ((getRefId() == null) ? 0 : getRefId().hashCode());
        result = prime * result + ((getTmpType() == null) ? 0 : getTmpType().hashCode());
        result = prime * result + ((getTmpOperType() == null) ? 0 : getTmpOperType().hashCode());
        return result;
    }
    
    public void initAddValue(String anTmpType, Long anCustNo) {
        this.initAddValue(anTmpType, CustomerConstants.TMP_STATUS_NEW, anCustNo);
    }
    
    public void initAddValue(String anTmpType, String anBusinStatus, Long anCustNo) {
        this.id = SerialGenerator.getLongValue("CustMechBaseTmp.id");

        this.regOperId = UserUtils.getOperatorInfo().getId();
        this.regOperName = UserUtils.getOperatorInfo().getName();
        this.regDate = BetterDateUtils.getNumDate();
        this.regTime = BetterDateUtils.getNumTime();

        this.modiOperId = UserUtils.getOperatorInfo().getId();
        this.modiOperName = UserUtils.getOperatorInfo().getName();
        this.modiDate = BetterDateUtils.getNumDate();
        this.modiTime = BetterDateUtils.getNumTime();

        this.operOrg = UserUtils.getOperatorInfo().getOperOrg();
        
        this.businStatus = anBusinStatus;
        this.custNo = anCustNo;
        this.tmpType = anTmpType;
        
        this.tmpOperType = CustomerConstants.TMP_OPER_TYPE_MODIFY;// 单表只有修改操作
    }

    /**
     * 使用正式表数据建立流水数据
     * @param anCustMechBase
     */
    public void initAddValue(CustMechBase anCustMechBase, String anTmpType, String anBusinStatus) {
        // 初始化公共字段
        initAddValue(anTmpType, anBusinStatus, anCustMechBase.getCustNo());
        
        // 从CustMechBase 取参数值
        this.custName = anCustMechBase.getCustName();
        this.address = anCustMechBase.getAddress();
        this.businLicence = anCustMechBase.getBusinLicence();
        this.businScope = anCustMechBase.getBusinScope();
        this.capitalType = anCustMechBase.getCapitalType();
        this.category = anCustMechBase.getCategory();
        this.corpType = anCustMechBase.getCorpType();
        this.cityNo = anCustMechBase.getCityNo();
        this.corpName = anCustMechBase.getCorpName();
        this.engName = anCustMechBase.getEngName();
        this.corpProperty = anCustMechBase.getCorpProperty();
        this.corpVocation = anCustMechBase.getCorpVocation();
        this.custClass = anCustMechBase.getCustClass();
        this.email = anCustMechBase.getEmail();
        this.fax = anCustMechBase.getFax();
        this.instType = anCustMechBase.getInstType();
        this.invest = anCustMechBase.getInvest();
        this.lawIdentNo = anCustMechBase.getLawIdentNo();
        this.lawIdentType = anCustMechBase.getLawIdentType();
        this.lawName = anCustMechBase.getLawName();
        this.lawPhone = anCustMechBase.getLawPhone();
        this.lawValidDate = anCustMechBase.getLawValidDate();
        this.mobile = anCustMechBase.getMobile();
        this.orgCode = anCustMechBase.getOrgCode();
        this.paidCapital = anCustMechBase.getPaidCapital();
        this.person = anCustMechBase.getPerson();
        this.phone = anCustMechBase.getPhone();
        this.premisesAddress = anCustMechBase.getPremisesAddress();
        this.premisesArea = anCustMechBase.getPremisesArea();
        this.premisesType = anCustMechBase.getPremisesType();
        this.premisesYear = anCustMechBase.getPremisesYear();
        this.qq = anCustMechBase.getQq();
        this.regAddress = anCustMechBase.getRegAddress();
        this.webaddr = anCustMechBase.getWebaddr();
        this.wechat = anCustMechBase.getWechat();
        this.zipCode = anCustMechBase.getZipCode();
    }
    
    public void initModifyValue(final String anBusinStatus) {
        this.modiOperId = UserUtils.getOperatorInfo().getId();
        this.modiOperName = UserUtils.getOperatorInfo().getName();
        this.modiDate = BetterDateUtils.getNumDate();
        this.modiTime = BetterDateUtils.getNumTime();
        
        this.businStatus = anBusinStatus;
    }
    
    public void initModifyValue(final CustMechBaseTmp anCustMechBaseTmp) {
        this.modiOperId = UserUtils.getOperatorInfo().getId();
        this.modiOperName = UserUtils.getOperatorInfo().getName();
        this.modiDate = BetterDateUtils.getNumDate();
        this.modiTime = BetterDateUtils.getNumTime();
        /*
        this.custNo = anCustMechBaseTmp.getCustNo();
        */
        this.custName = anCustMechBaseTmp.getCustName();
        this.address = anCustMechBaseTmp.getAddress();
        this.businLicence = anCustMechBaseTmp.getBusinLicence();
        this.businScope = anCustMechBaseTmp.getBusinScope();
        this.capitalType = anCustMechBaseTmp.getCapitalType();
        this.category = anCustMechBaseTmp.getCategory();
        this.corpType = anCustMechBaseTmp.getCorpType();
        this.cityNo = anCustMechBaseTmp.getCityNo();
        this.corpName = anCustMechBaseTmp.getCorpName();
        this.engName = anCustMechBaseTmp.getEngName();
        this.corpProperty = anCustMechBaseTmp.getCorpProperty();
        this.corpVocation = anCustMechBaseTmp.getCorpVocation();
        this.custClass = anCustMechBaseTmp.getCustClass();
        this.email = anCustMechBaseTmp.getEmail();
        this.fax = anCustMechBaseTmp.getFax();
        this.instType = anCustMechBaseTmp.getInstType();
        this.invest = anCustMechBaseTmp.getInvest();
        this.lawIdentNo = anCustMechBaseTmp.getLawIdentNo();
        this.lawIdentType = anCustMechBaseTmp.getLawIdentType();
        this.lawName = anCustMechBaseTmp.getLawName();
        this.lawPhone = anCustMechBaseTmp.getLawPhone();
        this.lawValidDate = anCustMechBaseTmp.getLawValidDate();
        this.mobile = anCustMechBaseTmp.getMobile();
        this.orgCode = anCustMechBaseTmp.getOrgCode();
        this.paidCapital = anCustMechBaseTmp.getPaidCapital();
        this.person = anCustMechBaseTmp.getPerson();
        this.phone = anCustMechBaseTmp.getPhone();
        this.premisesAddress = anCustMechBaseTmp.getPremisesAddress();
        this.premisesArea = anCustMechBaseTmp.getPremisesArea();
        this.premisesType = anCustMechBaseTmp.getPremisesType();
        this.premisesYear = anCustMechBaseTmp.getPremisesYear();
        this.qq = anCustMechBaseTmp.getQq();
        this.regAddress = anCustMechBaseTmp.getRegAddress();
        this.webaddr = anCustMechBaseTmp.getWebaddr();
        this.wechat = anCustMechBaseTmp.getWechat();
        this.zipCode = anCustMechBaseTmp.getZipCode();
        
        this.businStatus = anCustMechBaseTmp.getBusinStatus();
    }
}