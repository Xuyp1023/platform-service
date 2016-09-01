package com.betterjr.modules.cert.utils;

import java.lang.reflect.Field;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.Extension;
import org.bouncycastle.asn1.x509.GeneralName;
import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.KeyUsage;
import org.bouncycastle.cert.CertIOException;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betterjr.common.selectkey.SerialGenerator;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.IpAdressUtils;
import com.betterjr.modules.cert.data.BetterX509CertType;

/**
 * X509数字证书的的配置信息
 * 
 * @author zhoucy
 *
 */
public class BetterX509MetaData {
    private static final Logger logger = LoggerFactory.getLogger(BetterX509MetaData.class);

    // 重要的oids信息
    private final Map<String, String> oids;

    // 证书名称
    private final String commonName;

    // 证书密码
    private final String password;

    // 邮箱地址
    private String email;

    // 证书起始日期
    private Date notBefore;

    // 证书终止日期
    private Date notAfter;

    // 数字证书的序列号
    private String serialNumber;

    // 数字证书类型
    private BetterX509CertType certType = BetterX509CertType.END_CERT;

    // 数字证书秘钥长度
    private int keySize = BetterX509Utils.KEY_LENGTH;

    // 证书别名
    private String certAlias;

    public BetterX509CertStore createCertStore(BetterX509CertStore anParent, String anStoreFile) {

        BetterX509CertStore certStore = null;
        if (anParent instanceof BetterX509CertFileStore) {
            certStore = new BetterX509CertFileStore(anParent, anStoreFile, this.password, findCertAlias());
        }
        else {
            certStore = new BetterX509CertStreamStore(anParent, null, this.password, findCertAlias(), this.certType);
        }
        return certStore;
    }

    public KeyPair newKeyPair() {

        return BetterX509Utils.newRsaKeyPair(this.keySize);
    }


    private static void setOID(X500NameBuilder anDnBuilder, BetterX509MetaData anMetadata, String anOid, String anDefaultValue) {

        String value = anMetadata.findOID(anOid, anDefaultValue);

        if (BetterStringUtils.isNotBlank(value)) {
            try {
                Field field = BCStyle.class.getField(anOid);
                ASN1ObjectIdentifier objectId = (ASN1ObjectIdentifier) field.get(null);
                anDnBuilder.addRDN(objectId, value);
            }
            catch (Exception e) {
                logger.error(MessageFormat.format("设置 OID 失败 \"{0}\"!", anOid), e);
            }
        }
    }

    /**
     * 创建证书的主题信息
     *
     * @return a X500Name
     */
    public X500Name buildDistinguishedName() {
        X500NameBuilder dnBuilder = new X500NameBuilder(BCStyle.INSTANCE);
        setOID(dnBuilder, this, "CN", this.commonName);
        setOID(dnBuilder, this, "E", this.email);
        setOID(dnBuilder, this, "OU", null);
        setOID(dnBuilder, this, "O", null);
        setOID(dnBuilder, this, "L", null);
        setOID(dnBuilder, this, "ST", null);
        setOID(dnBuilder, this, "C", null);
        X500Name dn = dnBuilder.build();
        return dn;
    }

    public String findCertAlias() {
        if (BetterStringUtils.isBlank(certAlias)) {

            return commonName;
        }
        else {

            return certAlias;
        }
    }

    /**
     * 创建数字证书信息
     * 
     * @param anCommName
     *            数字证书名称，名称长度不能少于5位
     * @param anPassword
     *            数字证书密码，密码长度不能少于6位
     * @param anYear
     *            数字证书期限（年）
     */
    public BetterX509MetaData(String anCommName, String anPassword){
        BTAssert.isNotShorString(anCommName, 5, "数字证书的名称不能少于5个字符");
        BTAssert.isNotShorString(anPassword, 6, "数字证书的密码不能少于6个字符");
        commonName = anCommName;
        password = anPassword;
        oids = new HashMap<String, String>();
    }
    
    public BetterX509MetaData(String anCommName, String anPassword, int anYear){
        this(anCommName, anPassword);
        notBefore = BetterDateUtils.parseDate(BetterDateUtils.getDate());
        notAfter = BetterDateUtils.addDays(BetterDateUtils.addYears(notBefore, anYear), 10);
    }

    public void saveX509Extent(X509v3CertificateBuilder anCertBuilder, X509Certificate anCaCert, PublicKey anPubKey, JcaX509ExtensionUtils anExtUtils)
            throws CertIOException, NoSuchAlgorithmException {
        anCertBuilder.addExtension(Extension.basicConstraints, true, new BasicConstraints(BetterX509CertType.hasCA(this.certType)));
        anCertBuilder.addExtension(Extension.subjectKeyIdentifier, false, anExtUtils.createSubjectKeyIdentifier(anPubKey));
        if (BetterX509CertType.ROOT_CA == this.certType) {
            anCertBuilder.addExtension(Extension.authorityKeyIdentifier, false, anExtUtils.createAuthorityKeyIdentifier(anPubKey));
        }
        else {
            anCertBuilder.addExtension(Extension.authorityKeyIdentifier, false, anExtUtils.createAuthorityKeyIdentifier(anCaCert.getPublicKey()));
        }

        // CA证书的用途，数字签名、签发数字证书、回收数字证书
        if (BetterX509CertType.hasCA(this.certType)) {
            anCertBuilder.addExtension(Extension.keyUsage, true, new KeyUsage(KeyUsage.digitalSignature | KeyUsage.keyCertSign | KeyUsage.cRLSign));
        }
        else {
            // 最终用户的普通证书包括数字签名、秘钥处理等
            anCertBuilder.addExtension(Extension.keyUsage, true,
                    new KeyUsage(KeyUsage.keyEncipherment | KeyUsage.keyAgreement | KeyUsage.dataEncipherment | KeyUsage.digitalSignature));
        }

        List<GeneralName> altNames = new ArrayList<GeneralName>();
        if (IpAdressUtils.isIpAddress(this.commonName)) {

            altNames.add(new GeneralName(GeneralName.iPAddress, this.commonName));
        }
        if (StringUtils.isNotBlank(this.email)) {

            altNames.add(new GeneralName(GeneralName.rfc822Name, this.email));
        }

        if (altNames.size() > 0) {
            GeneralNames subjectAltName = new GeneralNames(altNames.toArray(new GeneralName[altNames.size()]));
            anCertBuilder.addExtension(Extension.subjectAlternativeName, false, subjectAltName);
        }
    }

    /**
     * 根据现有的数字证书信息，复制一个新的数字证书
     * 
     * @param anCommName
     *            数字证书名称，名称长度不能少于5位
     * @param anPassword
     *            数字证书密码，密码长度不能少于6位
     * @param anYear
     *            数字证书期限（年）
     * @return 数字证书信息
     */
    public BetterX509MetaData clone(String anCommName, String anPassword, int anYear) {
        BetterX509MetaData clone = new BetterX509MetaData(anCommName, anPassword, anYear);
        clone.email = this.email;
        clone.notBefore = this.notBefore;
        clone.notAfter = this.notAfter;
        clone.oids.putAll(oids);
        clone.certType = this.certType;

        return clone;
    }
    
    public void addData(Map<String, String> anData, String anEmail, String anNotBefore, String anNotAfter, String anSerialNumber) {
       this.addOids(anData, anEmail);
       this.notBefore = BetterDateUtils.parseDate(anNotBefore);
       this.notAfter = BetterDateUtils.addDays(BetterDateUtils.parseDate(anNotAfter), 10);
       this.serialNumber = anSerialNumber;
    }
    
    public void addOids(Map<String, String> anData, String anEmail) {
        this.oids.putAll(anData);
        this.email = anEmail;
    }

    /**
     * 查找证书信息中的对象唯一标示，如果没有找到，返回空字符串
     * 
     * @param anOid
     *            对象唯一标示名称
     * @return
     */
    public String findOID(String anOid) {

        return findOID(anOid, "");
    }

    /**
     * 查找证书信息中的对象唯一标示
     * 
     * @param anOid
     *            对象唯一标示名称
     * @param defaultValue
     *            默认值
     * @return
     */
    public String findOID(String anOid, String anDefaultValue) {
        if (oids.containsKey(anOid)) {

            return oids.get(anOid);
        }

        return anDefaultValue;
    }

    /**
     * 写入对象唯一标示OID的值，如果值为空，表示删除OID
     * 
     * @param anOid
     *            对象唯一标示
     * @param anValue
     */
    public String putOID(String anOid, String anValue) {
        if (StringUtils.isBlank(anValue)) {
            return oids.remove(anOid);
        }
        else {
            oids.put(anOid, anValue);
            return anValue;
        }
    }

    public String findSerialNumber() {
        if (BetterStringUtils.isBlank(this.serialNumber)) {
            this.serialNumber = Long.toUnsignedString(System.currentTimeMillis() * 10000 + SerialGenerator.randomInt(10000));
        }
        logger.info("work serialNumber :" + serialNumber);
        // this.serialNumber = Long.toString(System.currentTimeMillis());
        return this.serialNumber;
    }

    public String getCommonName() {
        return this.commonName;
    }

    public Date getNotBefore() {
        return this.notBefore;
    }

    public Date getNotAfter() {
        return this.notAfter;
    }

}
