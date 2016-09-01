package com.betterjr.modules.notification.provider;

public class SmsUtils {
    private static String spID = null;
    private static String password = null;
    private static String accessCode = null;
    private static UegateSoap uegateSoap = null;
    
    public SmsUtils(String spID, String password, String accessCode) {
        SmsUtils.spID = spID;
        SmsUtils.password = password;
        SmsUtils.accessCode = accessCode;
        uegateSoap = new UegateSoap();
    }

    /**
     * 
     * @param anContent
     * @param anMobileList
     * @return
     */
    public static String send(String anContent, String anMobileList) {
        synchronized (uegateSoap) {
            return uegateSoap.submit(spID, password, accessCode, anContent, anMobileList);
        }
    }
    
    /**
     * 
     */
    public static String queryBalance() {
        synchronized (uegateSoap) {
            return uegateSoap.queryMo(spID, password);
        }
    }
    
    /**
     * 
     */
    public static String queryReport() {
        synchronized (uegateSoap) {
            return uegateSoap.queryReport(spID, password);
        }
    }
    
    /**
     * 
     */
    public static String retrieveAll() {
        synchronized (uegateSoap) {
            return uegateSoap.retrieveAll(spID, password);
        }
    }
}
