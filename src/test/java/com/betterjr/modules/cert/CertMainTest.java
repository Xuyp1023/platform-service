package com.betterjr.modules.cert;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Log4jConfigurer;

import com.betterjr.common.mapper.JsonMapper;
import com.betterjr.common.security.KeyReader;
import com.betterjr.common.security.SignHelper;
import com.betterjr.common.selectkey.SelectKeyAutoIDGen;
import com.betterjr.common.selectkey.SerialGenerator;
import com.betterjr.common.utils.BetterDateUtils;
import com.betterjr.modules.cert.data.BetterX509CertConfig;
import com.betterjr.modules.cert.entity.BetterX509CertInfo;
import com.betterjr.modules.cert.entity.CustCertInfo;
import com.betterjr.modules.cert.service.BetterX509CertService;
import com.betterjr.modules.cert.service.CustCertService;
import com.betterjr.modules.cert.utils.BetterX509CertFileStore;
import com.betterjr.modules.cert.utils.BetterX509CertStore;
import com.betterjr.modules.cert.utils.BetterX509MetaData;
import com.betterjr.modules.cert.utils.BetterX509Utils;

public class CertMainTest  {
    private static final Logger logger = LoggerFactory.getLogger(CertMainTest.class);
    private final String folder = "D:\\cert";
    private static String caKeystorePassword = "DemoKey";

    private static ClassPathXmlApplicationContext ctx = null;

    private static String[] configFiles = new String[] { "spring-context-platform-dubbo-provider-test.xml" };
    private static String logConfigFile ="log4j.properties";

    public static void setUpBeforeClass() throws Exception {
        final URL url=CertMainTest.class.getClassLoader().getSystemResource(logConfigFile);
        System.out.println(url.toString());
        Log4jConfigurer.initLogging(url.getFile());
        ctx = new ClassPathXmlApplicationContext(configFiles);
        ctx.getBean(SelectKeyAutoIDGen.class);
        ctx.getBean(SerialGenerator.class);
    }

    public static void main(final String[] args) throws Exception{
        final Certificate cert = KeyReader.fromCerStoredFile("E:\\xxxxxxx\\cert1.cer");
        if (cert instanceof X509Certificate){
            final X509Certificate cert123 =(X509Certificate) cert;
            System.out.println(  BetterX509Utils.findCertificateSubjectItem(cert123, "CN") );
        }
        final boolean result = SignHelper.verifySignFile(new File("E:\\new\\platform-service\\src\\main\\java\\com\\betterjr\\modules\\base\\dubbo\\BusinessTypeDubboService.java"), "N/IbcYMm9RK7NGN58oQKRWFtYxsyuzJzJGUkmcALsleXZytgfeoLBO+K/7W/ORtq/7RzQn4nGfBZau2EfLWEYnPNuq5f7DeQ73pnq/aAg8yV9GUuNvEtg37fTap4mwu6LNrAcqv8xr9mMzmmWXGAag9JXti6JwRUMRq8HM47rpQ=", cert.getPublicKey());
        System.out.println(cert.getClass());
        System.out.println(result);
    }

    public static void main3(final String[] args) throws Exception{
        final Certificate cert = KeyReader.fromCerStoredFile("D:\\cert\\certs\\BYTTER_DEMO_CA.cer");
        if (cert instanceof X509Certificate){
            final X509Certificate cert123 =(X509Certificate) cert;
            System.out.println(  BetterX509Utils.findCertificateSubjectItem(cert123, "CN") );
        }
        System.out.println(cert.getClass());
    }

    public static void main1(final String[] args) throws  Exception  {
        final KeyStore xx = null;//X509Utils.openKeyStore("D:\\cert\\certs\\TestRootCA.p12", "DemoKey");
        //System.out.println( xx.getKey("BYTTER DEMO CA", "DemoKey".toCharArray()));
        final Enumeration<String> ee = xx.aliases();
        while(ee.hasMoreElements()){
            System.out.println(ee.nextElement() );
        }
    }

    protected static BetterX509MetaData createMetaData(){
        final BetterX509MetaData metadata = new BetterX509MetaData("China Online Trading", "DemoKey", 3);
        final Map<String, String> oids = new HashMap();
        oids.put("C", "CN");
        oids.put("ST", "广东");
        oids.put("L", "深圳");
        oids.put("OU", "运营部");
        oids.put("O", "深圳市前海拜特互联网金融服务有限公司");
        metadata.addOids(oids, "zhoucy@bytter.com");

        return metadata;
    }

    protected static BetterX509CertInfo createCertInfo(){
        final BetterX509CertInfo certInfo = new BetterX509CertInfo();
        certInfo.setCityName("shenzhen");
        certInfo.setCommName("China Online Trading");
        certInfo.setCountryCode("CN");
        certInfo.setCertAlias("China.Online.Trading");
        certInfo.setCreateDate(BetterDateUtils.getNumDate());
        certInfo.setValidDate(BetterDateUtils.formatNumberDate(BetterDateUtils.addYears(BetterDateUtils.getNow(), 5)));
        certInfo.setProvinceName("guangdong");
        certInfo.setOrgName("深圳市前海拜特互联网金融服务有限公司");
        certInfo.setOrgUnitName("运营部");
        certInfo.setSigner("BYTTER DEMO CA");
        certInfo.setCertType("3");

        return certInfo;
    }

    public static void queryCert() throws Exception{
        BetterX509Utils.isSelfSigned(null);
        final KeyStore  store = KeyStore.getInstance("PKCS12", BetterX509Utils.BC);
        store.load(new FileInputStream("D:\\cert\\certs\\test.p12"), "DemoKey".toCharArray());
        //store.load(new FileInputStream("D:\\cert\\certs\\BYTTER_DEMO_CA.p12"), "DemoKey".toCharArray());
        final Enumeration<String>  ee = store.aliases();
        /*        BetterX509CertInfo certInfo = BetterX509CertService.saveX509CertFromFile("D:\\cert\\certs\\test.p12", "DemoKey");
        logger.info("out certInfo =" + certInfo);
         */        while(ee.hasMoreElements()){
             final String tmpStr = ee.nextElement();
             System.out.println("this is alias:"+tmpStr);
             final Certificate[] arrList = store.getCertificateChain(tmpStr);
             if (arrList == null){
                 continue;
             }
             for(final Certificate cc : arrList){
                 final X509Certificate tmpCC = (X509Certificate)cc;
                 System.out.println(tmpCC.getSerialNumber() +"=" + tmpCC.getSubjectDN() +",  getType = " + tmpCC.getType() + ", BasicConstraints="+tmpCC.getBasicConstraints());
             }
         }
    }

    public static void saveToDB() throws Exception{
        setUpBeforeClass();
        final BetterX509CertInfo certInfo = createCertInfo();
        final BetterX509CertService certService = ctx.getBean(BetterX509CertService.class);

        //certService.saveX509CertFromWeb(certInfo);
        //        final Map<String, BetterX509CertStore> map = certService.findMiddleCertStore();
        //        for(final Map.Entry ent : map.entrySet()){
        //            System.out.println(ent.getKey() + "=" + ent.getValue());
        //        }
        //certService.saveRevokeCert(10010L, "72057594037927940", "KEY_COMPROMISE");
        // certService.saveRevokeCert(10011L, "72057594037927941", "KEY_COMPROMISE");

        //certInfo = certService.savePublishStatus(10009L, "DemoKey");
        //FileUtils.copyInputStreamToFile( new ByteArrayInputStream(certInfo.getData()), new File("D:\\cert\\certs\\test123456.p12"));
        certService.saveX509CertInfoFromFile("E:\\cert\\certs\\BYTTER_DEMO_CA.p12", "DemoKey");
        //certService.saveX509CertInfoFromFile("D:\\cert\\test\\Bytter_supplychain_Client_Key.p12", "DemoKey");
        //certService.saveX509CertInfoFromFile("D:\\cert\\test\\QIEJF_DEMO_CA.p12", "DemoKey");
        //certService.saveX509CertInfoFromFile("E:\\cert\\certs\\ROOT_CER.cer", "");
        System.exit(0);
    }

    public static void checkRevokeCert() throws Exception{
        BetterX509Utils.isSelfSigned(null);
        final KeyStore  store = KeyStore.getInstance("PKCS12", BetterX509Utils.BC);
        store.load(new FileInputStream("D:\\cert\\test\\Bytter_supplychain_Client_Key.p12"), "DemoKey".toCharArray());
        final X509Certificate cert = (X509Certificate) store.getCertificate("Bytter supplychain Client Key");
        logger.info("check revoke status :" + BetterX509Utils.isRevoked(cert, "D:\\cert\\cets\\CertificateCRLFile.cer"));
    }

    public static void testFindCustCertFromX509Cert() throws Exception{
        setUpBeforeClass();
        final CustCertService certService = ctx.getBean(CustCertService.class);
        final CustCertInfo certInfo = certService.findCustCertFromX509Cert(10010L, "72057594037927940");
        logger.info("" + certInfo);
    }

    public static void testAddCustCertInfo() throws Exception{
        setUpBeforeClass();
        final CustCertService certService = ctx.getBean(CustCertService.class);
        final CustCertInfo certInfo = certService.findCustCertFromX509Cert(10010L, "72057594037927940");
        certInfo.setRuleList("SUPPLIER_USER");
        certInfo.setContName("张三");
        certInfo.setContPhone("13828796910");
        certInfo.setContIdentNo("1231312831283");
        certInfo.setContIdentType("1");
        certInfo.setDescription("测试");
        //final Map<String, Object> data = BeanMapper.map(certInfo, HashMap.class);
        certService.addCustCertInfo(certInfo);

    }

    public static void testPushlishX509Cert() throws Exception{
        setUpBeforeClass();
        final CustCertService certService = ctx.getBean(CustCertService.class);
        certService.savePublishCert("72057594037927940", "1");
    }

    public static void testDownloadX509Cert() throws Exception{
        setUpBeforeClass();
        final CustCertService certService = ctx.getBean(CustCertService.class);
        final byte[] bbs = certService.saveDownloadCert("6445dd558ce84c2aa516f2307fc8e86dEDIUXMpikBylbnfZ1DarNUch54mc1yqOAdJJDHeeJHfhuucy");
        FileUtils.copyInputStreamToFile(new ByteArrayInputStream(bbs) , new File("d://cert/cets/test1231312.p12"));
    }

    public static void main11(final String[] args) throws Exception{
        //testDownloadX509Cert();

        //testPushlishX509Cert();
        //testFindCustCertFromX509Cert();
        //testAddCustCertInfo();
        //checkRevokeCert();
        //BetterX509Utils.saveToPem("D:\\cert\\cets\\CertificateCRLFile.crl", "");
        saveToDB();
        //createCert();
        //queryCert();
        //logger.info(""+tmpConfig);
        System.exit(0);
    }

    protected static void createCert(){

        final String rootCA = "/certs/ROOT_CER.cer";
        BetterX509Utils.isSelfSigned(null);
        final BetterX509CertStore rootStore = new BetterX509CertFileStore(rootCA);
        final String middileCA = "/certs/BYTTER_DEMO_CA.p12";
        final BetterX509CertStore middileStore = new BetterX509CertFileStore(rootStore, middileCA, caKeystorePassword, null);
        final BetterX509CertConfig config = new  BetterX509CertConfig("D:\\cert\\", "certs", "D:\\cert\\certs\\RevocationPath.cer");
        config.addData("BYTTER DEMO CA", middileStore);
        final String tmpStr = JsonMapper.toNormalJson(config);
        //X509CertConfig tmpConfig = (X509CertConfig) JsonMapper.fromJsonString(tmpStr, X509CertConfig.class);
        final BetterX509MetaData metaData = createMetaData();
        BetterX509Utils.newCertificate(metaData, middileStore, config.findCertPath()+"/test.p12");
        logger.info(tmpStr);
    }


    public static void main2(final String[] args) {
        final CertMainTest test = new CertMainTest();

    }

    public void log(final String anMessage) {
        logger.info("this is :" + anMessage);
    }

}
