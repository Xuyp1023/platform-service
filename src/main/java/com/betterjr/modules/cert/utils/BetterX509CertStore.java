package com.betterjr.modules.cert.utils;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.betterjr.common.exception.BytterSecurityException;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.modules.cert.data.BetterX509CertType;

/**
 * 数字证书仓库的信息
 * 
 * @author zhoucy
 *
 */
public abstract class BetterX509CertStore implements java.io.Serializable {
    private static final long serialVersionUID = 3284953070781757796L;
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    private BetterX509CertStore parent;
    private String password;
    private String certAlias;
    private String description;
    protected X509Certificate userCert = null;
    protected KeyStore store;
    
    public BetterX509CertStore() {

    }

    public BetterX509CertType findCertType(){
        
        return BetterX509CertType.checkType(this.findCertificate());        
    }
    
    public String findSigner(){
        if (this.parent != null ){
            X509Certificate tmpCert = this.parent.findCertificate();
            return BetterX509Utils.findCertificateSubjectItem(tmpCert, "CN");
        }
        return " ";
    }
    
    public BetterX509CertStore(BetterX509CertStore anParent, String anPassword, String anCertAlias) {
        this.parent = anParent;
        this.password = anPassword;
        this.certAlias = anCertAlias;
    }

    public String findCertAlias() {
        if (BetterStringUtils.isBlank(this.certAlias)) {
            openKeyStore(false);
            Enumeration<String> ee;
            try {
                ee = store.aliases();
                if (ee != null) {
                    while (ee.hasMoreElements()) {
                        this.certAlias = ee.nextElement();
                        break;
                    }
                }
            }
            catch (KeyStoreException e) {
                e.printStackTrace();
            }
        }
        return this.certAlias;
    }

    /**
     * 打开数字证书存储仓库文件
     *
     * @param storeFile
     * @param storePassword
     * @return KeyStore，存储数字证书的仓库
     */
    public abstract KeyStore openKeyStore(boolean anCreate);

    /**
     * 根据数字证书别名，从数字证书仓库中获得数字证书私钥
     *
     * @param anAlias
     *            数字证书别名
     * @param anStoreFile
     *            数字证书存储文件
     * @param anStorePassword
     *            数字证书存取密码
     * @return 数字证书私钥
     */
    public PrivateKey findPrivateKey() {

        return findPrivateKey(null);
    }
    
    public byte[] findPrivateKeyEncode(){
        PrivateKey tmpKey = findPrivateKey();
        if (tmpKey == null){
            return new byte[0];
        }
        else{
            return tmpKey.getEncoded();
        }
    }
    public PrivateKey findPrivateKey(KeyStore anKeyStore) {
        try {
            store = anKeyStore;
            if (store == null) {
                store = openKeyStore(false);
            }
            PrivateKey key = (PrivateKey) store.getKey(findCertAlias(), this.findPassword());

            return key;
        }
        catch (Exception e) {

          //  throw new BytterSecurityException("从数据仓库获取数字证书私钥出现异常", e);
            return null;
        }
    }

    /**
     * 导入数字证书到仓库.
     *
     * @param anAlias
     *            数字证书别名
     * @param anCert
     *            数字证书
     * @param anStoreFile
     *            存储的文件路径
     * @param anStorePassword
     *            存取的密码
     */
    public void addTrustedCertificate(String anAlias, X509Certificate anCert) {
        try {
            openKeyStore(false);
            store.setCertificateEntry(anAlias, anCert);
            saveKeyStore(store);
        }
        catch (Exception e) {
            throw new BytterSecurityException("不能导入数字证书到数字证书仓库 ", e);
        }
    }
    
    /**
     * 读取原始的信息
     * @return
     */
    public abstract byte[] readOrignData();
    
    /**
     * 根据数字证书别名，从数字证书仓库中获得数字证书
     *
     * @param anAlias
     *            数字证书别名
     * @param anStoreFile
     *            数字证书存储文件
     * @param anStorePassword
     *            数字证书存取密码
     * @return the certificate 数字证书信息
     */
    public X509Certificate findCertificate() {
        if (this.userCert != null) {

            return userCert;
        }

        openKeyStore(false);
        X509Certificate cert;
        try {
            cert = (X509Certificate) store.getCertificate(findCertAlias());

            return cert;
        }
        catch (KeyStoreException e) {
            throw new BytterSecurityException("从数据仓库获取证书出现异常", e);
        }
    }

    /**
     * 保存数字证书仓库到指定的文件
     *
     * @param 目标文件
     * @param 数字证书仓库
     * @param 数字证书仓库存取密码
     */
    public abstract void saveKeyStore(KeyStore anStore);

    public BetterX509CertStore getParent() {
        
        return this.parent;
    }

    public void setParent(BetterX509CertStore anParent) {
        
        this.parent = anParent;
    }

    public char[] findPassword() {
        if (BetterStringUtils.isNotBlank(this.password)) {
            return this.password.toCharArray();
        }
        else {
            return new char[] {};
        }
    }
   
    /**
     * 查找证书链信息
     * 
     * @return
     */
    public X509Certificate[] findChainList(X509Certificate anSelf) {
        List<X509Certificate> result = new ArrayList<X509Certificate>();
        if (anSelf != null) {
            result.add(anSelf);
        }
        subFindChainList(result);

        return result.toArray(new X509Certificate[result.size()]);
    }

    private void subFindChainList(List<X509Certificate> anList) {
        if (this.parent != null) {
            anList.add(this.parent.findCertificate());
            this.parent.subFindChainList(anList);
        }
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String anPassword) {
        this.password = anPassword;
    }

    public String getCertAlias() {
        return this.certAlias;
    }

    public void setCertAlias(String anCertAlias) {
        this.certAlias = anCertAlias;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String anDescription) {
        this.description = anDescription;
    }

    public String toString() {

        return ToStringBuilder.reflectionToString(this);
    }
}
