package com.betterjr.modules.cert.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.UnrecoverableKeyException;
import java.security.cert.CRLReason;
import java.security.cert.CertPathBuilder;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXBuilderParameters;
import java.security.cert.PKIXCertPathBuilderResult;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CRL;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.*;

import javax.crypto.Cipher;
import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;

import org.apache.commons.io.IOUtils;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CRLHolder;
import org.bouncycastle.cert.X509v2CRLBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509ExtensionUtils;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.jce.interfaces.PKCS12BagAttributeCarrier;
import org.bouncycastle.openssl.PEMEncryptor;
import org.bouncycastle.openssl.jcajce.JcaPEMWriter;
import org.bouncycastle.openssl.jcajce.JcePEMEncryptorBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
 
import com.betterjr.common.exception.BytterException;
import com.betterjr.common.exception.BytterSecurityException;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.common.utils.FileUtils;

/**
 * X509数字证书操作类
 *
 * @author zhoucy
 *
 */
public class BetterX509Utils {
    public static final String BC = org.bouncycastle.jce.provider.BouncyCastleProvider.PROVIDER_NAME;

    public static final int KEY_LENGTH = 2048;

    public static final String KEY_ALGORITHM = "RSA";

    private static final String SIGNING_ALGORITHM = "SHA256withRSA";

    private static final boolean unlimitedStrength;

    private static final Logger logger = LoggerFactory.getLogger(BetterX509Utils.class);

    static {
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        // 检查是否去掉JCE的安全强度限制策略
        int maxKeyLen = 0;
        try {
            maxKeyLen = Cipher.getMaxAllowedKeyLength("AES");
        }
        catch (NoSuchAlgorithmException e) {
        }

        unlimitedStrength = maxKeyLen > 128;
        if (unlimitedStrength) {
            logger.info("已经使用无限制的JCE的安全强度策略");
        }
        else {
            logger.info("使用默认安装的JCE安全强度策略, 加密秘钥的长度受到限制");
        }
    }

    /**
     * 创建新的证书秘钥对
     *
     * @return 秘钥对信息
     */
    public static KeyPair newRsaKeyPair(int anKeySize) {
        KeyPairGenerator kpGen;
        try {
            kpGen = KeyPairGenerator.getInstance(BetterX509Utils.KEY_ALGORITHM, BetterX509Utils.BC);
            kpGen.initialize(anKeySize, new SecureRandom());
            return kpGen.generateKeyPair();
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException e) {

            throw new BytterSecurityException("创建秘钥对异常", e);
        }
    }

    /**
     * 移动文件，如果目标文件存在，则删除
     * 
     * @param anSource
     *            源文件
     * @param anDest
     *            目标文件
     */
    public static void renameFileExistDel(File anSource, File anDest) {
        if (anDest.exists()) {
            anDest.delete();
        }
        anSource.renameTo(anDest);
    }

    /**
     * 保存数字证书到文件，如果不指定扩展名则使用原始文件方式保存，如果扩展名是pem；则保存为pem文件
     * 
     * @param anCert
     *            数字证书
     * @param anTargetFile
     */
    public static void saveCertificate(X509Certificate anCert, File anTargetFile) {
        File folder = anTargetFile.getAbsoluteFile().getParentFile();
        if (folder.exists() == false) {

            folder.mkdirs();
        }
        File tmpFile = new File(folder, Long.toHexString(System.currentTimeMillis()) + ".tmp");
        try {
            boolean asPem = anTargetFile.getName().toLowerCase().endsWith(".pem");
            if (asPem) {
                // PEM encoded X509
                JcaPEMWriter pemWriter = null;
                try {
                    pemWriter = new JcaPEMWriter(new FileWriter(tmpFile));
                    pemWriter.writeObject(anCert);
                    pemWriter.flush();
                }
                finally {
                    IOUtils.closeQuietly(pemWriter);
                }
            }
            else {
                // DER encoded X509
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(tmpFile);
                    fos.write(anCert.getEncoded());
                    fos.flush();
                }
                finally {
                    IOUtils.closeQuietly(fos);
                }
            }

            renameFileExistDel(tmpFile, anTargetFile);
        }
        catch (Exception e) {
            throw new BytterSecurityException("保存证书失败： " + anCert.getSubjectX500Principal().getName(), e);
        }
        finally {
            if (tmpFile.exists()) {
                tmpFile.delete();
            }
        }
    }
 
    /**
     * 创建数字证书，如果数字证书存在，则覆盖
     *
     * @param anMetadata
     *            数字证书信息
     * @param anCaPrivateKey
     *            CA私钥
     * @param anCaCert
     *            CA证书
     * @param targetFolder
     *            文件保存目标目录
     * @return
     */
    public static BetterX509CertStore newCertificate(BetterX509MetaData anMetadata, BetterX509CertStore anCaCertStore, String anTargetFile) {
        KeyPair pair = anMetadata.newKeyPair();
        return newCertificate(anMetadata, anCaCertStore, pair.getPrivate(), pair.getPublic(), anTargetFile);
    }

    public static BetterX509CertStore newCertificate(BetterX509MetaData anMetadata, BetterX509CertStore anCaCertStore, PrivateKey anPrivKey,
            PublicKey anPubKey, String anTargetFile) {
        try {
            X500Name userDN = anMetadata.buildDistinguishedName();
            X509Certificate caCert = (X509Certificate) anCaCertStore.findCertificate();

            // 创建新的数字证书
            X509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(caCert, new BigInteger(anMetadata.findSerialNumber()),
                    anMetadata.getNotBefore(), anMetadata.getNotAfter(), userDN, anPubKey);
            JcaX509ExtensionUtils extUtils = new JcaX509ExtensionUtils();

            // 赋予数字证书扩展属性
            anMetadata.saveX509Extent(certBuilder, caCert, anPubKey, extUtils);
            ContentSigner signer = new JcaContentSignerBuilder(SIGNING_ALGORITHM).setProvider(BC).build(anCaCertStore.findPrivateKey());

            X509Certificate userCert = new JcaX509CertificateConverter().setProvider(BC).getCertificate(certBuilder.build(signer));
            PKCS12BagAttributeCarrier bagAttr = (PKCS12BagAttributeCarrier) anPrivKey;
            bagAttr.setBagAttribute(PKCSObjectIdentifiers.pkcs_9_at_localKeyId, extUtils.createSubjectKeyIdentifier(anPubKey));

            // 验证最终的数字证书
            userCert.checkValidity();
            userCert.verify(caCert.getPublicKey());
            if (userCert.getIssuerDN().equals(caCert.getSubjectDN()) == false) {
                logger.warn("证书签发者和证书中的签发者信息不一致");
                throw new BytterSecurityException("证书签发者和证书中的签发者信息不一致");
            }

            // 保存数字证书的路径
            BetterX509CertStore certStorer = anMetadata.createCertStore(anCaCertStore, anTargetFile);
            X509Certificate[] certChainList = certStorer.findChainList(userCert);
            verifyChain(userCert, certChainList);

            KeyStore userStore = certStorer.openKeyStore(true);
            userStore.setKeyEntry(findCertificateSubjectItem(userCert, "CN"), anPrivKey, null, certChainList);
            certStorer.saveKeyStore(userStore);

            // 如果是文件模式，则保存数字证书文件相关信息
            if ( certStorer instanceof BetterX509CertStreamStore){
                
                return certStorer;
            }
            File targetFolder = new File(anTargetFile.substring(0, anTargetFile.length() - 6));
            targetFolder.mkdirs();
            String date = BetterDateUtils.getNumDate();
            String id = date;
            File certFile = new File(targetFolder, id + ".cer");
            int count = 0;
            while (certFile.exists()) {
                id = date + "_" + Character.toString((char) (0x61 + count));
                certFile = new File(targetFolder, id + ".cer");
                count++;
            }

            // 保存私钥，数字证书、数字证书链等到pem文件
            File pemFile = new File(targetFolder, anMetadata.getCommonName() + ".pem");
            if (pemFile.exists()) {
                pemFile.delete();
            }
            JcePEMEncryptorBuilder builder = new JcePEMEncryptorBuilder("DES-EDE3-CBC");
            builder.setSecureRandom(new SecureRandom());
            PEMEncryptor pemEncryptor = builder.build("DemoKey".toCharArray());
            JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter(pemFile));
            pemWriter.writeObject(certStorer.findPrivateKey(), pemEncryptor);
            pemWriter.writeObject(userCert);
            pemWriter.writeObject(caCert);
            pemWriter.flush();
            pemWriter.close();

            // 保存数字证书 saveCertificate(userCert, certFile);

            // 更新证书序列号信息 anMetadata.serialNumber = userCert.getSerialNumber().toString();

            return certStorer;
        }
        catch (Throwable t) {
            throw new RuntimeException("创建数字证书失败!", t);
        }
    }

    /**
     * 查找数字证书中主题中的单项的值
     * 
     * @param anX509
     *            数字证书
     * @param anItem
     *            需要查找的单项，例如CN、OU、O等
     * @return
     */
    public static String findCertificateSubjectItem(Certificate anX509, String anItem) {
        if (anX509 == null) {

            return "";
        }

        LdapName ldapDN;
        try {
            if (anX509 instanceof X509Certificate) {
                ldapDN = new LdapName(((X509Certificate) anX509).getSubjectX500Principal().getName());

                for (Rdn dd : ldapDN.getRdns()) {
                    if (dd.getType().equals(anItem)) {
                        return (String) dd.getValue();
                    }
                }
            }
        }
        catch (InvalidNameException e) {
            logger.warn("查找数字证书主题的单项失败，", e);
        }

        return "";
    }

    public static Map<String, String> findCertificateSubjectMap(Certificate anX509) {
        Map<String, String> result = new HashMap();
        if (anX509 != null) {
            LdapName ldapDN;
            try {
                if (anX509 instanceof X509Certificate) {
                    ldapDN = new LdapName(((X509Certificate) anX509).getSubjectX500Principal().getName());
                    for (Rdn dd : ldapDN.getRdns()) {
                        Object obj = dd.getValue();
                        // System.out.println(dd.getType() + " = " + obj);
                        if (obj instanceof String) {
                            result.put(dd.getType(), (String) dd.getValue());
                        }
                    }
                }
            }
            catch (InvalidNameException e) {
                logger.warn("查找数字证书主题的单项失败，", e);
            }
        }
        return result;
    }

    /**
     * 验证数字证书，确保数字证书链的完整性.
     *
     * @param anTestCert
     *            需要验证的证书
     * @param anAdditionalCerts
     *            证书链列表
     * @return PKIXCertPathBuilderResult 数字证书验证结果
     */
    public static PKIXCertPathBuilderResult verifyChain(X509Certificate anTestCert, X509Certificate... anAdditionalCerts) {
        try {
            // 检查是否是自签证书，自签的数字证书；不需要验证
            if (isSelfSigned(anTestCert)) {
                logger.warn("自签的数字证书；不需要验证");
                return null;
            }

            // 证书链信息必须完整
             Set<X509Certificate> certs = new HashSet<X509Certificate>();
            certs.add(anTestCert);
            certs.addAll(Arrays.asList(anAdditionalCerts));

            X509CertSelector selector = new X509CertSelector();
            selector.setCertificate(anTestCert);

            Set<TrustAnchor> trustAnchors = new HashSet<TrustAnchor>();
            boolean workContinue = false;
            for (X509Certificate cert : anAdditionalCerts) {
                if (isSelfSigned(cert)) {
                    workContinue = true;
                }
                trustAnchors.add(new TrustAnchor(cert, null));
            }

            if (workContinue == false) {
                logger.warn("数字证书链中不包括根证书，验证不充分");
                throw new BytterSecurityException("数字证书链中不包括根证书，验证不充分");
            }

            PKIXBuilderParameters pkixParams = new PKIXBuilderParameters(trustAnchors, selector);
            pkixParams.setRevocationEnabled(false);
            pkixParams.addCertStore(CertStore.getInstance("Collection", new CollectionCertStoreParameters(certs), BC));

            CertPathBuilder builder = CertPathBuilder.getInstance("PKIX", BC);
            PKIXCertPathBuilderResult verifiedCertChain = (PKIXCertPathBuilderResult) builder.build(pkixParams);

            return verifiedCertChain;
        }
        catch (CertPathBuilderException e) {
            throw new RuntimeException("Error building certification path: " + anTestCert.getSubjectX500Principal(), e);
        }
        catch (Exception e) {
            throw new RuntimeException("Error verifying the certificate: " + anTestCert.getSubjectX500Principal(), e);
        }
    }

    /**
     * 检查数字证书是否是自签数字证书，依据就是证书签署方的公钥和证书的公钥一致
     *
     * @param anCert
     * @return true 表示是自签证书
     */
    public static boolean isSelfSigned(X509Certificate anCert) {
        try {
            if (anCert != null) {
                anCert.verify(anCert.getPublicKey());
                return true;
            }
        }
        catch (Exception e) {
        }

        return false;
    }

    /**
     * 回收数字证书.
     *
     * @param anCert
     *            需要回收的数字证书
     * @param anReason
     *            数字证书回收原因
     * @param anCaRevocationList
     *            数字证书回收文件列表
     * @param caKeystoreFile
     *            证书签发者仓库信息
     * @param x509log
     * @return true 表示回收成功
     */
    public static boolean revoke(X509Certificate anCert, CRLReason anReason, BetterX509CertStore anCaStoreInfo, String anCaRevocationList) {
        KeyStore store = anCaStoreInfo.openKeyStore(false);
        PrivateKey caPrivateKey;
        try {
            caPrivateKey = (PrivateKey) store.getKey(anCaStoreInfo.getCertAlias(), anCaStoreInfo.findPassword());
            return revoke(anCert, anReason, anCaRevocationList, caPrivateKey);
        }
        catch (UnrecoverableKeyException | KeyStoreException | NoSuchAlgorithmException e) {
            return false;
        }
    }

    /**
     * 回收数字证书.
     *
     * @param anCert
     *            需要回收的数字证书
     * @param anReason
     *            数字证书回收原因
     * @param anCaRevocationList
     *            数字证书回收文件列表
     * @param anCaPrivateKey
     *            证书签发者私钥
     * @param x509log
     * @return true 表示回收成功
     */
    public static boolean revoke(X509Certificate anCert, CRLReason anReason, String anCaRevocationList, PrivateKey anCaPrivateKey) {
        try {
            X500Name issuerDN = new X500Name(anCert.getIssuerX500Principal().getName());
            X509v2CRLBuilder crlBuilder = new X509v2CRLBuilder(issuerDN, new Date());
            File caRevocationList = new File(anCaRevocationList);
            if (caRevocationList.exists()) {
                byte[] data = FileUtils.readContent(caRevocationList);
                X509CRLHolder crl = new X509CRLHolder(data);
                crlBuilder.addCRL(crl);
            }
            crlBuilder.addCRLEntry(anCert.getSerialNumber(), new Date(), anReason.ordinal());

            // build and sign CRL with CA private key
            ContentSigner signer = new JcaContentSignerBuilder("SHA1WithRSA").setProvider(BC).build(anCaPrivateKey);
            X509CRLHolder crl = crlBuilder.build(signer);

            File tmpPath = caRevocationList.getParentFile();
            if (tmpPath.exists() == false) {
                tmpPath.mkdirs();
            }

            File tmpFile = new File(tmpPath, Long.toHexString(System.currentTimeMillis()) + ".tmp");
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(tmpFile);
                fos.write(crl.getEncoded());
                fos.flush();
                fos.close();
                renameFileExistDel(tmpFile, caRevocationList);
            }
            finally {
                IOUtils.closeQuietly(fos);
                if (tmpFile.exists()) {
                    tmpFile.delete();
                }
            }
            JcaPEMWriter pemWriter = new JcaPEMWriter(new FileWriter(anCaRevocationList.substring(0, anCaRevocationList.length() - 4) + ".pem"));
            pemWriter.writeObject(crl);
            pemWriter.flush();
            pemWriter.close();

            logger.warn(MessageFormat.format("回收数字证书 {0,number,0} 原因: {1} [{2}]", anCert.getSerialNumber(), anReason.toString(),
                    anCert.getSubjectDN().getName()));

            return true;
        }
        catch (IOException | OperatorCreationException e) {
            logger.error(MessageFormat.format("回收数字证书失败 {0,number,0} [{1}] in {2}", anCert.getSerialNumber(), anCert.getSubjectDN().getName(),
                    anCaRevocationList), e);
        }

        return false;
    }

    /**
     * 从文件路径或ClassPath中获得指定文件的输入数据流信息
     * 
     * @param anStoreFile
     *            指定的文件
     * @return
     */
    public static InputStream findInputStream(String anStoreFile) {
        File storeFile = new File(anStoreFile);
        // 如果文件存在，则使用文件方式加载，如果不存在，则使用ClassPath方式加载
        try {
            if (storeFile.exists() && storeFile.isFile()) {
                return new FileInputStream(storeFile);
            }
            else {
                ClassPathResource cc = new ClassPathResource(anStoreFile);
                return cc.getInputStream();
            }
        }
        catch (IOException e) {
            // throw new BytterFieldNotFoundException(1000, "资源文件不存在：" + anStoreFile, e);
            return null;
        }
    }

    /**
     * 获取数字证书的指纹信息
     * 
     * @param anCert数字证书
     * @return
     */
    public static String fingerprint(org.bouncycastle.asn1.x509.Certificate anCert) {
        byte[] der;
        try {
            der = anCert.getEncoded();

            byte[] sha1 = sha256DigestOf(der);
            byte[] hexBytes = Hex.encode(sha1);
            String hex = new String(hexBytes, "ASCII").toUpperCase();

            StringBuffer fp = new StringBuffer();
            int i = 0;
            fp.append(hex.substring(i, i + 2));
            while ((i += 2) < hex.length()) {
                fp.append(':');
                fp.append(hex.substring(i, i + 2));
            }
            return fp.toString();
        }
        catch (IOException e) {
            throw new BytterSecurityException("获取数字证书指纹信息失败", e);
        }
    }

    public static byte[] sha256DigestOf(byte[] anInput) {
        SHA256Digest d = new SHA256Digest();
        d.update(anInput, 0, anInput.length);
        byte[] result = new byte[d.getDigestSize()];
        d.doFinal(result, 0);
        return result;
    }

    /**
     * 检查数字证书是否被回收
     *
     * @param anCert
     *            数字证书
     * @param anCaRevocationList
     *            数字证书回收文件列表
     * @return true 表示数字证书被回收
     */
    public static boolean isRevoked(X509Certificate anCert, String anCaRevocationList) {
        InputStream inStream = null;
        try {
            inStream = findInputStream(anCaRevocationList);
            CertificateFactory cf = CertificateFactory.getInstance("X.509", BC);
            X509CRL crl = (X509CRL) cf.generateCRL(inStream);
            return crl.isRevoked(anCert);
        }
        catch (Exception e) {
            logger.error(MessageFormat.format("无法检查证书的撤销状态 {0,number,0} [{1}] in {2}", anCert.getSerialNumber(), anCert.getSubjectDN().getName(),
                    anCaRevocationList));
        }
        finally {
            IOUtils.closeQuietly(inStream);
        }

        return false;
    }

    /**
     * 将文件转换为同名下的Pem文件
     * 
     * @param anFilePath
     */
    public static void saveToPem(String anFilePath, String anPasswd) {
        JcaPEMWriter pemWriter;
        InputStream inStream = null;
        try {
            pemWriter = new JcaPEMWriter(new FileWriter(anFilePath.substring(0, anFilePath.length() - 4) + ".pem"));
            if (anFilePath.toLowerCase().endsWith(".crl")) {
                inStream = findInputStream(anFilePath);
                CertificateFactory cf = CertificateFactory.getInstance("X.509", BC);
                X509CRL crl = (X509CRL) cf.generateCRL(inStream);
                pemWriter.writeObject(crl);
            }
            pemWriter.flush();
            pemWriter.close();

        }
        catch (Exception e) {

            throw BytterException.unchecked(e);
        }
    }

    public static X509Certificate loadCertFromStream(InputStream anStream) {
        CertificateFactory cf;
        try {
            cf = CertificateFactory.getInstance("X.509", "BC");
            return (X509Certificate) cf.generateCertificate(anStream);
        }
        catch (CertificateException | NoSuchProviderException e) {
            e.printStackTrace();
            throw new BytterSecurityException("从数据流加载数字证书失败:", e);
        }
    }

    /**
     * 读取公钥或私钥信息
     * 
     * @param anData
     * @param anUsePublic
     * @return
     */
    public static PublicKey readPublicKeyFromStream(byte[] anData) {

        return (PublicKey) readKeyFromStream(anData, true);
    }

    public static PrivateKey readPrivateKeyFromStream(byte[] anData) {

        return (PrivateKey) readKeyFromStream(anData, false);
    }

    private static Key readKeyFromStream(byte[] anData, boolean anUsePublic) {
        if (anData == null || anData.length < 10) {

            return null;
        }

        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance(KEY_ALGORITHM, BC);
            if (anUsePublic) {
                java.security.spec.EncodedKeySpec keySpec = new X509EncodedKeySpec(anData);
                return keyFactory.generatePublic(keySpec);
            }

            java.security.spec.EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(anData);
            return keyFactory.generatePrivate(keySpec);
        }
        catch (NoSuchAlgorithmException | NoSuchProviderException | InvalidKeySpecException e) {

            throw new BytterSecurityException("读取简单的公钥或私钥数据出错", e);
        }
    }

}
