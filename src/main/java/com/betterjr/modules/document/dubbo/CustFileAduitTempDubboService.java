package com.betterjr.modules.document.dubbo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
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
    public List<CustFileItem> findCustAduitTemp(final Long anCustNo, final Long anSelectCustNo,
            final List<AgencyAuthorFileGroup> anAgencyAuthorFileGroupList) {

        return custFileAduitTempService.findCustFileAduitTempByCustNoAndType(anCustNo, anSelectCustNo,
                anAgencyAuthorFileGroupList);
    }

    /***
     * 保存临时文件
     * @param anCustFileAduitTemp
     */
    @Override
    public boolean addCustFileAduitTemp(final CustFileAduitTemp anCustFileAduitTemp) {
        return custFileAduitTempService.addCustFileAduitTemp(anCustFileAduitTemp);
    }

    /***
     * 检查文件是否都已上传
     * @param anCustNo 关联上传文件的客户号
     * @param anAgencyAuthorFileGroupList 文件类型列表
     * @return
     */
    @Override
    public boolean checkCustFileAduitTempExist(final Long anCustNo, final Long anSelectCustNo,
            final List<AgencyAuthorFileGroup> anAgencyAuthorFileGroupList) {
        return custFileAduitTempService.checkCustFileAduitTempExist(anCustNo, anSelectCustNo,
                anAgencyAuthorFileGroupList);
    }

    /***
     * 删除临时审核附件关系表数据
     * @param anId 附件 id
     * @return
     */
    @Override
    public boolean saveDeleteFileAduitTemp(final Long anId) {
        return custFileAduitTempService.saveDeleteFileAduitTemp(anId);
    }

    /***
     * 添加客户文件关系
     * @param anRelationCustNo 关联的客户号
     * @param fileIds 上传的文件列表(以,分隔)
     */
    @Override
    public void saveCustFileAduitTemp(final Long anCustNo, final Long anRelateCustNo, final String anFileIds,
            final String anCustType) {
        custFileAduitTempService.saveCustFileAduitTemp(anCustNo, anRelateCustNo, anFileIds, anCustType);
    }

    /***
     * 查询关系审核附件
     * @param anCustNo
     * @return
     */
    @Override
    public List<CustFileItem> findRelateAduitTempFile(final Long anCustNo) {
        return custFileAduitTempService.findRelateAduitTempFile(anCustNo);
    }

    /***
     * 保存附件关系
     * @param passFiles 审核通过的文件列表
     * @param failFiles 审核不通过的文件列表
     * @param anBusinStatus 关系状态
     */
    @Override
    public void saveAcceptFileTemp(final String anPassFiles, final String anFailFiles) {
        custFileAduitTempService.saveAcceptFileTemp(anPassFiles, anFailFiles);
    }

    @Override
    public void saveAduitFile(final Long anCustNo, final Long anRelateCustNo) {
        custFileAduitTempService.saveAduitFile(anCustNo, anRelateCustNo);
    }

    @Override
    public String saveCustFileAuditTempInfo(final Long anCustNo, final String anFileIds) {
        custFileAduitTempService.saveCustFileAduitTempFile(anCustNo, anFileIds);
        return AjaxObject.newOk("新增用户认证文件审核信息成功").toJson();
    }

    @Override
    public String webFindChangeApply(final Long anId) {
        return AjaxObject.newOk("查找审核申请数据", custFileAduitTempService.findChangeApply(anId)).toJson();
    }
}
