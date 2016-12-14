package com.betterjr.modules.document.service;

import com.betterjr.common.config.ParamNames;
import com.betterjr.common.config.SpringPropertyResourceReader;
import com.betterjr.common.data.CheckDataResult;
import com.betterjr.common.data.NormalStatus;
import com.betterjr.common.mapper.JsonMapper;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.Cryptos;
import com.betterjr.common.utils.FileUtils;
import com.betterjr.common.utils.reflection.ReflectionUtils;
import com.betterjr.modules.document.dao.AuthorFileGroupMapper;
import com.betterjr.modules.document.data.FileStoreType;
import com.betterjr.modules.document.data.OSSConfigInfo;
import com.betterjr.modules.document.entity.AgencyAuthorFileGroup;
import com.betterjr.modules.document.entity.AuthorFileGroup;
import com.betterjr.modules.sys.service.SysConfigService;

import java.util.*;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

@Service
public class AuthorFileGroupService extends BaseService<AuthorFileGroupMapper, AuthorFileGroup> {
    private static final String OSS_CONFIG = "OssConfigInfo";

    public Map<String, AuthorFileGroup> findAllFileGroup() {
        Map<String, AuthorFileGroup> fileGroupMap = ReflectionUtils.listConvertToMap(selectByProperty("groupStatus", NormalStatus.VALID_STATUS.value),
                "fileInfoType");

        return fileGroupMap;
    }

    public List<AuthorFileGroup> findFileGroupList(String anBusinFlag) {
        Map<String, Object> propAndValues = new HashMap();
        propAndValues.put("businFlag", anBusinFlag);
        propAndValues.put("groupStatus", NormalStatus.VALID_STATUS.value);

        return this.selectByProperty(propAndValues);
    }

    public List<AuthorFileGroup> findCustFileGroupList() {

        return findFileGroupList("01");
    }

    /**
     * 查找文件存储类型
     * 
     * @param anFileInfoType
     *            文件业务类型
     * @return
     */
    public FileStoreType findFileStoreType(String anFileInfoType) {
        AuthorFileGroup fileGroup = findAuthFileGroup(anFileInfoType);
        return FileStoreType.checking(fileGroup.getStoreType());
    }

    /**
     * 查找Aliyun OSS 配置信息
     * 
     * @return
     */
    public OSSConfigInfo findOSSConfigInfo() {
        OSSConfigInfo configInfo = null;
        String tmpStr = SysConfigService.getString(OSS_CONFIG);
        try {
            tmpStr = Cryptos.aesDecrypt(tmpStr);
        }
        catch (Exception ex) {
            String saveData = Cryptos.aesEncrypt(tmpStr);
            // SysConfigService.saveParamValue(OSS_CONFIG, saveData);
        }
        configInfo = (OSSConfigInfo) JsonMapper.fromJsonString(tmpStr, OSSConfigInfo.class);

        return configInfo;
    }

    private AuthorFileGroup findAuthFileGroup(String anFileInfoType) {
        AuthorFileGroup fileGroup = this.selectByPrimaryKey(anFileInfoType);
        if (fileGroup == null) {
            fileGroup = new AuthorFileGroup("00", anFileInfoType);
            fileGroup.setStoreType(SpringPropertyResourceReader.getProperty("fileStoreType", "0"));
        }

        return fileGroup;
    }

    /**
     * 查找文件创建路径
     * 
     * @param anAgencyNo
     *            合作机构代码
     * @param anFileInfoType
     *            文件类别
     * @return
     */
    public String findCreateFilePath(String anFileInfoType) {
        AuthorFileGroup fileGroup = findAuthFileGroup(anFileInfoType);
        FileStoreType storeType = FileStoreType.checking(fileGroup.getStoreType());
        return fileGroup.findCreateFilePath(storeType);
    }

    /**
     * 查找文件绝对路径信息
     * 
     * @param anFilePath
     *            文件路径信息
     * @return
     */
    public String findAbsFilePath(String anFilePath) {
        String basePath = (String) SysConfigService.getString(ParamNames.OPENACCO_FILE_DOWNLOAD_PATH);

        return basePath + anFilePath;
    }

    /**
     * 检查文件类型是否允许
     * @param anFileInfoType 文件业务类型
     * @param anFileType 文件类型
     * @return
     */
    public CheckDataResult findFileTypePermit(String anFileInfoType, String anFileType){
        AuthorFileGroup fileGroup = findAuthFileGroup(anFileInfoType);
        return new CheckDataResult(fileGroup.checkFileType(anFileType), fileGroup.getPermitFileTypes());
    }
    
    /**
     * 查找上传文件允许的文件类型列表信息
     * @param anFileInfoType
     * @return
     */
    public String findFileTypePermitInfo(String anFileInfoType){
        AuthorFileGroup fileGroup = findAuthFileGroup(anFileInfoType);
        String tmpStr = fileGroup.getPermitFileTypes();
        if (BetterStringUtils.isBlank(tmpStr)){
            tmpStr = BetterStringUtils.join(FileUtils.SupportedUploadFileType, ",");
        }
        StringBuilder sb = new StringBuilder();
        for(String tmpChar: BetterStringUtils.splitTrim(tmpStr)){
            sb.append("*.").append(tmpChar).append(", ");
        }
        sb.setLength( sb.length() -2);
        return sb.toString();
    }
}