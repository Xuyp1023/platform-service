package com.betterjr.modules.cert.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.commons.io.IOUtils;

import com.betterjr.common.exception.BytterException;
import com.betterjr.common.exception.BytterSecurityException;
import com.betterjr.common.utils.BetterStringUtils;

public class BetterX509CertFileStore extends BetterX509CertStore {

    private static final long serialVersionUID = -5575188232963074741L;
    private String storeFile;

    public BetterX509CertFileStore() {

    }

    public BetterX509CertFileStore(BetterX509CertStore anParent, String anStoreFile, String anPassword, String anCertAlias) {
        super(anParent, anPassword, anCertAlias);
        this.storeFile = anStoreFile;
        this.checkCertFile(anStoreFile);
    }

    public BetterX509CertFileStore(String anCertFile) {

        this(null, anCertFile, null, null);
    }

    protected void checkCertFile(String anFile) {
        if (BetterStringUtils.isNotBlank(anFile)) {
            String tmpFile = anFile.toLowerCase();
            InputStream inputStream = BetterX509Utils.findInputStream(anFile);
            if (inputStream == null) {
                return;
            }
            if (tmpFile.endsWith(".cer")) {
                try {
                    this.userCert = BetterX509Utils.loadCertFromStream(inputStream);
                    this.setCertAlias(BetterX509Utils.findCertificateSubjectItem(userCert, "CN"));
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else if (tmpFile.endsWith(".pfx") || tmpFile.endsWith(".p12")) {
                KeyStore tmpKeyStore = openKeyStore(false);

                Certificate[] arrList;
                try {
                    arrList = tmpKeyStore.getCertificateChain(this.findCertAlias());
                    if (arrList == null) {
                        return;
                    }
                    BetterX509CertStore tmpParent;
                    BetterX509CertStore currCertStore = this;
                    X509Certificate myCert = this.findCertificate();
                    for (Certificate cc : arrList) {
                        X509Certificate tmpCC = (X509Certificate) cc;
                        if (myCert.getSubjectDN().equals(tmpCC.getSubjectDN())) {
                            continue;
                        }
                        tmpParent = new BetterX509CertStreamStore(tmpCC);
                        currCertStore.setParent(tmpParent);
                        currCertStore = tmpParent;
                    }
                }
                catch (KeyStoreException e) {
                    e.printStackTrace();
                }
            }

        }
    }

    public String getStoreFile() {
        return this.storeFile;
    }

    public void setStoreFile(String anStoreFile) {
        this.storeFile = anStoreFile;
    }

    /**
     * 打开数字证书存储仓库文件
     *
     * @param storeFile
     * @param storePassword
     * @return KeyStore，存储数字证书的仓库
     */
    public KeyStore openKeyStore(boolean anCreate) {
        if (anCreate == false) {
            if (this.store != null) {
                return this.store;
            }
        }
        InputStream fis = null;
        if (BetterStringUtils.isBlank(this.storeFile) || BetterStringUtils.isBlank(this.getPassword())) {

            return null;
        }
        try {
            if (this.storeFile.endsWith(".p12") || this.storeFile.endsWith(".pfx")) {
                store = KeyStore.getInstance("PKCS12", BetterX509Utils.BC);
            }
            else {
                store = KeyStore.getInstance("JKS");
            }
            if (anCreate) {
                store.load(null);
            }
            else {
                fis = BetterX509Utils.findInputStream(this.storeFile);
                store.load(fis, this.findPassword());
            }

            return store;
        }
        catch (IOException e) {

            throw new BytterSecurityException("不能打开数字证书仓库，证书文件不存在或文件格式异常： " + this.storeFile, e);
        }
        catch (CertificateException | NoSuchAlgorithmException | KeyStoreException e) {

            throw new BytterSecurityException("不能打开数字证书仓库： " + this.storeFile, e);
        }
        catch (NoSuchProviderException e) {

            throw new BytterSecurityException("不能打开数字证书仓库，没有找到JCE提供者信息： " + this.storeFile, e);
        }
        finally {
            IOUtils.closeQuietly(fis);
        }
    }

    /**
     * 保存数字证书仓库到指定的文件
     *
     * @param 目标文件
     * @param 数字证书仓库
     * @param 数字证书仓库存取密码
     */
    public void saveKeyStore(KeyStore anStore) {

        File tmpStoreFile = new File(this.storeFile);
        File folder = tmpStoreFile.getAbsoluteFile().getParentFile();
        // 如果目录不存在，则创建目录
        if (folder.exists() == false) {
            folder.mkdirs();
        }
        File tmpFile = new File(folder, Long.toHexString(System.currentTimeMillis()) + BetterStringUtils.createRandomCharAndNum(10) + ".tmp");
        OutputStream fos = null;
        try {
            fos = new FileOutputStream(tmpFile);
            anStore.store(fos, this.findPassword());
            fos.flush();
            IOUtils.closeQuietly(fos);
            BetterX509Utils.renameFileExistDel(tmpFile, tmpStoreFile);
            this.store = anStore;
        }
        catch (IOException e) {
            String message = e.getMessage().toLowerCase();
            if (message.contains("illegal key size")) {
                throw new RuntimeException("非法的秘钥长度，请考虑使用无限制的JCE的安全强度策略");
            }
            else {
                throw new BytterSecurityException("不能保存数字证书仓库到文件： " + this.storeFile, e);
            }
        }
        catch (Exception e) {
            throw new BytterSecurityException("不能保存数字证书仓库到文件： " + this.storeFile, e);
        }
        finally {
            IOUtils.closeQuietly(fos);
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
        }
    }

    @Override
    public byte[] readOrignData() {
        InputStream in = BetterX509Utils.findInputStream(this.storeFile);
        if (in != null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            try {
                IOUtils.copy(in, bos);

                bos.flush();
                return bos.toByteArray();

            }
            catch (IOException e) {
                throw BytterException.unchecked(e);
            }
            finally {
                IOUtils.closeQuietly(in);
            }

        }

        return new byte[0];
    }

}
