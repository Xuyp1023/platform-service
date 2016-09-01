package com.betterjr.modules.cert.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import org.apache.commons.io.IOUtils;

import com.betterjr.common.exception.BytterSecurityException;
import com.betterjr.modules.cert.data.BetterX509CertType;

public class BetterX509CertStreamStore extends BetterX509CertStore {
    private static final long serialVersionUID = -2038570968922057797L;

    private byte[] data;

    public BetterX509CertStreamStore() {

    }

    public BetterX509CertStreamStore(X509Certificate anX509Cert) {
        this.userCert = anX509Cert;
        try {
            this.data = anX509Cert.getEncoded();
            this.setCertAlias(BetterX509Utils.findCertificateSubjectItem(userCert, "CN"));
        }
        catch (CertificateEncodingException e) {
            e.printStackTrace();
        }

    }

    public BetterX509CertStreamStore(BetterX509CertStore anParent, byte[] anData, String anPassword, String anCertAlias,
            BetterX509CertType anCertType) {
        super(anParent, anPassword, anCertAlias);
        this.data = anData;
        if (anCertType == BetterX509CertType.ROOT_CA && (anData != null)) {
            try {
                userCert = BetterX509Utils.loadCertFromStream(new ByteArrayInputStream(anData));
                this.setCertAlias(BetterX509Utils.findCertificateSubjectItem(userCert, "CN"));
            }
            catch (Exception e) {
                logger.warn("数据流不是合法的数字证书格式", e);
            }
        }
    }

    @Override
    public KeyStore openKeyStore(boolean anCreate) {
        InputStream fis = null;
        try {
            store = KeyStore.getInstance("PKCS12", BetterX509Utils.BC);
            if (anCreate) {
                store.load(null);
            }
            else {
                fis = new ByteArrayInputStream(data);
                store.load(fis, this.findPassword());
            }
        }
        catch (KeyStoreException | NoSuchProviderException | NoSuchAlgorithmException | CertificateException | IOException e) {
            e.printStackTrace();
        }
        finally {
            IOUtils.closeQuietly(fis);
        }

        return store;
    }

    public byte[] readOrignData() {
        if (this.data == null) {
            return new byte[0];
        }
        return this.data;
    }

    @Override
    public void saveKeyStore(KeyStore anStore) {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        try {
            anStore.store(outStream, this.findPassword());
            outStream.flush();
            this.data = outStream.toByteArray();
        }
        catch (KeyStoreException | NoSuchAlgorithmException | CertificateException | IOException e) {

            throw new BytterSecurityException("不能保存数字证书仓库到输出流中", e);
        }
        finally {
            IOUtils.closeQuietly(outStream);
        }

        return;
    }
}