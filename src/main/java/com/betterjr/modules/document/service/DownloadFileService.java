package com.betterjr.modules.document.service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import com.betterjr.common.mapper.BeanMapper;
import com.betterjr.common.selectkey.SerialGenerator;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.UUIDUtils;
import com.betterjr.modules.document.data.DownloadFileInfo;
import com.betterjr.modules.document.entity.AgencyAuthorFileGroup;
import com.betterjr.modules.document.entity.CustFileItem;

public class DownloadFileService extends Thread {

    private static Map<String, AgencyAuthorFileGroup> fileGroupMap= new HashMap();
    
    private static Map<String, FileAccessInfo> cacheData = new ConcurrentHashMap();
    
    //检查间隔时间默认10秒
    private int innerTime = 10;
    
    public int getInnerTime() {
        return this.innerTime;
    }

    public void setInnerTime(int anInnerTime) {
        this.innerTime = anInnerTime;
    }

    public DownloadFileService(){
        
    }
    
    public static DownloadFileInfo exactDownloadFile(String anToken){
       if( BetterStringUtils.isNotBlank(anToken)){
           FileAccessInfo fileAccessInfo = cacheData.get(anToken);
           if (fileAccessInfo != null){
               
               return fileAccessInfo.exactFile();
           }
       }
       
       return null;
    }
    
    public static void addDownloadFile(DownloadFileInfo anFileInfo){
        AgencyAuthorFileGroup fileGroup = fileGroupMap.get(anFileInfo.findComposeKey());
        Integer invalidTime = null;
        if (fileGroup == null){
            invalidTime = new Integer(30);
        }
        else{
            invalidTime = fileGroup.getInvalidTime();
        }
        FileAccessInfo acccessInfo = new FileAccessInfo(anFileInfo, invalidTime);
        
        cacheData.put(anFileInfo.getAccessToken(), acccessInfo);
    }
    
    public static DownloadFileInfo createInstance(CustFileItem anFileItem, Long anCustNo, String anPartnerCode, String anBusingType){
        DownloadFileInfo fileInfo = BeanMapper.map(anFileItem, DownloadFileInfo.class);
        fileInfo.setBusinType(anBusingType);
        fileInfo.setCustNo(anCustNo);
        fileInfo.setPartnerCode(anPartnerCode);
        String tmpToken = UUIDUtils.uuid().concat(Long.toHexString(System.currentTimeMillis())).concat(SerialGenerator.randomBase62(20));
        fileInfo.setAccessToken(tmpToken);
        DownloadFileService.addDownloadFile(fileInfo);
        return fileInfo;
    }
    
    public DownloadFileService(Map anMap){
        
        fileGroupMap = anMap;
    }
    
    @Override
    public void run() {
        long tmpInnerTime = this.innerTime * 1000;
        List<String> tmpInvalidKeyList = new ArrayList();
        while(true){
            tmpInvalidKeyList.clear();
            for(Map.Entry<String, FileAccessInfo> ent : cacheData.entrySet()){
                if (ent.getValue().valid() == false){
                    tmpInvalidKeyList.add(ent.getKey());
                }
            }
            for(String invalidKey : tmpInvalidKeyList){
               cacheData.remove(invalidKey); 
            }
            try {
                Thread.sleep(tmpInnerTime);
            }
            catch (InterruptedException e) { 
                break;
            }
        }
    }

    protected static class FileAccessInfo{
       private final long createTime;
       private final DownloadFileInfo  fileInfo;
       private final long validTime;
       public FileAccessInfo(DownloadFileInfo anFileInfo, Integer anValidTime){
           this.createTime = System.currentTimeMillis();
           this.fileInfo = anFileInfo;
           this.validTime = anValidTime.intValue() * 60 * 1000;
       }
       
       protected boolean valid(){
          long tmpTime = this.createTime + validTime;
          
          return tmpTime > System.currentTimeMillis();
       }
       
       protected DownloadFileInfo exactFile(){
           if (valid()){
               return this.fileInfo;
           }
           else{
              return null; 
           }
       }
    }
}
