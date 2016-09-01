package com.betterjr.modules.cert.data;

import java.security.cert.X509Certificate;

import com.betterjr.common.utils.BetterStringUtils;

public enum BetterX509CertType {
    ROOT_CA("0", "根证书"), MIDDLE_CA("1", "中级证书"), SSL_CERT("2", "SSL证书"), END_CERT("3", "最终用户证书"), PRIV_KEY("5", "简单的密钥私钥"), PUB_KEY("6", "简单的密钥公钥");
    private final String name;
    private final String value;

    BetterX509CertType(String anValue, String anName) {
        name = anName;
        this.value = anValue;
    }

    public String getValue() {

        return this.value;
    }

    public static BetterX509CertType checkType(X509Certificate anCert) {
        if (anCert == null || anCert.getBasicConstraints() < 0) {
            return END_CERT;
        }
        int kk = anCert.getBasicConstraints();
        if (kk > 0) {
            if (anCert.getIssuerDN().equals(anCert.getSubjectDN())) {
                return ROOT_CA;
            }
            else {
                return MIDDLE_CA;
            }
        }
        return END_CERT;
    }

    public static BetterX509CertType checking(String anWorkType) {
        try {
            if (BetterStringUtils.isNotBlank(anWorkType)) {
                for (BetterX509CertType mm : BetterX509CertType.values()) {
                    if (mm.value.equalsIgnoreCase(anWorkType)) {

                        return mm;
                    }
                }

                return BetterX509CertType.valueOf(anWorkType.trim().toUpperCase());
            }
        }
        catch (Exception ex) {

        }
        return END_CERT;
    }

    // 判断是否是CA数字证书
    public static boolean hasCA(BetterX509CertType anType) {
        if (anType != null) {

            return ROOT_CA.equals(anType) || MIDDLE_CA.equals(anType);
        }

        return false;
    }

    public String getName() {

        return this.name;
    }

}
