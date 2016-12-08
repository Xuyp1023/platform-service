package com.betterjr.modules.document.dubbo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.modules.document.ICustFileAduitTempService;
import com.betterjr.modules.document.entity.AgencyAuthorFileGroup;
import com.betterjr.modules.document.entity.CustFileAduitTemp;
import com.betterjr.modules.document.entity.CustFileItem;
import com.betterjr.modules.document.service.CustFileAduitTempService;

@Service
public class CustFileAduitTempDubboService implements ICustFileAduitTempService {

    @Autowired
    public CustFileAduitTempService custFileAduitTempService;
    
    @Override
    public List<CustFileItem> findCustAduitTemp(Long anCustNo, List<AgencyAuthorFileGroup> anAgencyAuthorFileGroupList) {
        
        return custFileAduitTempService.findCustFileAduitTempByCustNoAndType(anCustNo, anAgencyAuthorFileGroupList);
    }
    
    /***
     * 保存临时文件
     * @param anCustFileAduitTemp
     */
    public boolean addCustFileAduitTemp(CustFileAduitTemp anCustFileAduitTemp){
        return custFileAduitTempService.addCustFileAduitTemp(anCustFileAduitTemp);
    }
    
    /***
     * 检查文件是否都已上传
     * @param anCustNo 关联上传文件的客户号
     * @param anAgencyAuthorFileGroupList 文件类型列表
     * @return
     */
    public boolean checkCustFileAduitTempExist(Long anCustNo,List<AgencyAuthorFileGroup> anAgencyAuthorFileGroupList){
        return custFileAduitTempService.checkCustFileAduitTempExist(anCustNo, anAgencyAuthorFileGroupList);
    }
    
    /***
     * 删除临时审核附件关系表数据
     * @param anId 附件 id
     * @return
     */
    public boolean saveDeleteFileAduitTemp(Long anId){
        return custFileAduitTempService.saveDeleteFileAduitTemp(anId);
    }

}
