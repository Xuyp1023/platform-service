package com.betterjr.modules.cert.data;

import java.io.File;
import java.util.*;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.betterjr.modules.cert.utils.BetterX509CertStore;

/**
 * 数字证书相关业务配置信息
 * @author zhoucy
 *
 */
public class X509CertConfig implements java.io.Serializable {
 
    private static final long serialVersionUID = -7088317501627542831L;

    //基本目录
    private String basePath;
    
    //数字证书存放路径
    private String certsPath;
    
    //数字证书回收文件存放路径,这个文件路径是绝对路径
    private String revocationPath;
    private Map<String, BetterX509CertStore> data = new HashMap();
    
    public X509CertConfig(){
        
    }

    /**
     * 查找数字证书存储路径
     * @return
     */
    public String findCertPath(){
        File tmpFile = new File(basePath + File.separator + certsPath);
        if (tmpFile.exists() == false){
           tmpFile.mkdirs(); 
        }
        
        return tmpFile.getAbsolutePath();
    }
    
    
    public X509CertConfig(String anBasePath, String anCertPath, String anRevocationPath){
        this.basePath = anBasePath;
        this.certsPath = anCertPath;
        this.revocationPath = anRevocationPath;
    }
    
    public String getBasePath(){
        
        return this.basePath;
    }

    public Map<String, BetterX509CertStore> getData() {
        return this.data;
    }

    public void addData(String anAlias, BetterX509CertStore anStore){
    
        this.data.put(anAlias, anStore); 
    }
    
    public void setData(Map<String, BetterX509CertStore> anData) {
        
        this.data = anData;
    }

    public void setBasePath(String anBasePath) {
        this.basePath = anBasePath;
    }

    public String getCertsPath() {
        return this.certsPath;
    }

    public void setCertsPath(String anCertsPath) {
        this.certsPath = anCertsPath;
    }

    public String getRevocationPath() {
        return this.revocationPath;
    }

    public void setRevocationPath(String anRevocationPath) {
        this.revocationPath = anRevocationPath;
    }
    

    public String toString(){
        
       return ToStringBuilder.reflectionToString(this);
    }
}
