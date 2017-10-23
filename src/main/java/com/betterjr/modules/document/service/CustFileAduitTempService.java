package com.betterjr.modules.document.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.dubbo.config.annotation.Reference;
import com.betterjr.common.data.PlatformBaseRuleType;
import com.betterjr.common.mapper.BeanMapper;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.account.dubbo.interfaces.ICustInfoService;
import com.betterjr.modules.account.entity.CustOperatorInfo;
import com.betterjr.modules.customer.constants.CustomerConstants;
import com.betterjr.modules.customer.data.ICustAuditEntityFace;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.helper.ChangeDetailBean;
import com.betterjr.modules.customer.helper.IFormalDataService;
import com.betterjr.modules.customer.service.CustChangeApplyService;
import com.betterjr.modules.customer.service.CustChangeService;
import com.betterjr.modules.document.IAgencyAuthFileGroupService;
import com.betterjr.modules.document.dao.CustFileAduitTempMapper;
import com.betterjr.modules.document.entity.AgencyAuthorFileGroup;
import com.betterjr.modules.document.entity.CustFileAduit;
import com.betterjr.modules.document.entity.CustFileAduitTemp;
import com.betterjr.modules.document.entity.CustFileItem;

/****
 * 临时附件审核表
 * @author hubl
 *
 */
@Service
public class CustFileAduitTempService extends BaseService<CustFileAduitTempMapper, CustFileAduitTemp> implements
        IFormalDataService {

    @Autowired
    private CustFileItemService custFileItemService;
    @Autowired
    private CustFileAuditService custFileAuditService;
    @Reference(interfaceClass = ICustInfoService.class)
    private ICustInfoService custInfoService;
    @Reference(interfaceClass = IAgencyAuthFileGroupService.class)
    private IAgencyAuthFileGroupService agencyAuthFileGroupService;

    @Autowired
    private AuthorFileGroupService authorFileGroupService;
    @Resource
    private CustChangeApplyService changeApplyService;
    @Resource
    private CustChangeService changeService;

    /***
     * 获取类型附件列表
     * @param anCustNo 客户号
     * @param anAgencyAuthorFileGroupList 文件类型
     * @return
     */
    public List<CustFileItem> findCustFileAduitTempByCustNoAndType(final Long anCustNo, final Long anSelectCustNo,
            final List<AgencyAuthorFileGroup> anAgencyAuthorFileGroupList) {
        final List<CustFileItem> custFileItemList = new ArrayList<CustFileItem>();
        for (final AgencyAuthorFileGroup agencyAuthorFileGroup : anAgencyAuthorFileGroupList) {
            final CustFileAduitTemp custFileAduitTemp = findCustFileAduitTempByType(anCustNo, anSelectCustNo,
                    agencyAuthorFileGroup.getFileInfoType());
            if (custFileAduitTemp != null) {
                CustFileItem custFileItem = custFileItemService.findOneByBatchNo(custFileAduitTemp.getId(),
                        agencyAuthorFileGroup.getFileInfoType());
                if (custFileItem != null
                        && StringUtils
                                .equalsIgnoreCase(custFileAduitTemp.getWorkType(), custFileItem.getFileInfoType())) {
                    custFileItem.setFileDescription(agencyAuthorFileGroup.getDescription());
                    custFileItem.setBusinStatus(custFileAduitTemp.getAuditStatus());
                    custFileItemList.add(custFileItem);
                } else {
                    custFileItem = new CustFileItem();
                    custFileItem.setFileInfoType(agencyAuthorFileGroup.getFileInfoType());
                    custFileItem.setFileDescription(agencyAuthorFileGroup.getDescription());
                    custFileItemList.add(custFileItem);
                }
            } else {
                // 临时文件里面是空的，则再从正式文件关系表中获取开户时上传的附件
                CustFileItem custFileItem = findCustFileAduitByFileType(agencyAuthorFileGroup.getFileInfoType());
                if (custFileItem == null) {
                    custFileItem = new CustFileItem();
                    custFileItem.setFileInfoType(agencyAuthorFileGroup.getFileInfoType());
                    custFileItem.setFileDescription(agencyAuthorFileGroup.getDescription());
                }
                custFileItemList.add(custFileItem);
            }
        }
        return custFileItemList;
    }

    /***
     * 获取当前登录时开户的附件
     * @param anFileType
     * @return
     */
    public CustFileItem findCustFileAduitByFileType(final String anFileType) {
        final List<String> fileTypeList = new ArrayList<String>();
        fileTypeList.add(anFileType);
        final List<Long> batchNos = custFileAuditService.findBatchNo(custInfoService.findCustNo(), fileTypeList);
        final CustFileItem custFileItem = custFileItemService.findOneByBatchNo(Collections3.getFirst(batchNos),
                anFileType);
        if (custFileItem != null) {
            custFileItem.setFileDescription(agencyAuthFileGroupService
                    .findAuthFileGroup(custFileItem.getFileInfoType()).getDescription());
        }
        return custFileItem;
    }

    public CustFileAduitTemp findCustFileAduitTempByType(final Long anCustNo, final Long anSelectCustNo,
            final String anType) {
        Map<String, Object> anMap = new HashMap<String, Object>();
        if (anSelectCustNo == null) {
            anMap = setLoginParam(anCustNo);
        } else {
            anMap = setParam(anCustNo, anSelectCustNo);
        }
        anMap.put("workType", anType);
        return Collections3.getFirst(this.selectByProperty(anMap));
    }

    private Map<String, Object> setLoginParam(final Long anCustNo) {
        final Map<String, Object> anMap = new HashMap<String, Object>();
        // 当前登录的用户是保理公司则传进查询条件要变动
        if (UserUtils.factorUser()) {
            anMap.put("custNo", anCustNo);
            anMap.put("aduitCustNo", custInfoService.findCustNo());
        } else {
            anMap.put("custNo", custInfoService.findCustNo());
            anMap.put("aduitCustNo", anCustNo);
        }
        return anMap;
    }

    private Map<String, Object> setParam(final Long anCustNo, final Long anSelectCustNo) {
        final Map<String, Object> anMap = new HashMap<String, Object>();
        // 当前登录的用户是保理公司则传进查询条件要变动
        if (UserUtils.factorUser()) {
            anMap.put("custNo", anCustNo);
            anMap.put("aduitCustNo", anSelectCustNo);
        } else {
            anMap.put("custNo", anSelectCustNo);
            anMap.put("aduitCustNo", anCustNo);
        }
        return anMap;
    }

    /***
     * 保存临时文件
     * @param anCustFileAduitTemp
     */
    public boolean addCustFileAduitTemp(final CustFileAduitTemp anCustFileAduitTemp) {
        return this.insert(anCustFileAduitTemp) > 0;
    }

    /***
     * 检查文件是否都已上传
     * @param anCustNo 关联上传文件的客户号
     * @param anAgencyAuthorFileGroupList 文件类型列表
     * @return
     */
    public boolean checkCustFileAduitTempExist(final Long anCustNo, final Long anSelectCustNo,
            final List<AgencyAuthorFileGroup> anAgencyAuthorFileGroupList) {
        boolean bool = false;
        for (final AgencyAuthorFileGroup agencyAuthorFileGroup : anAgencyAuthorFileGroupList) {
            CustFileAduitTemp custFileAduitTemp = findCustFileAduitTempByType(anCustNo, anSelectCustNo,
                    agencyAuthorFileGroup.getFileInfoType());
            if (custFileAduitTemp == null) { // 临时表若为空，则在正式表中查询出备份到临时表一份
                final CustFileItem custFileItem = findCustFileAduitByFileType(agencyAuthorFileGroup.getFileInfoType());
                if (custFileItem != null) {
                    custFileAduitTemp = new CustFileAduitTemp();
                    custFileAduitTemp.setCustNo(custInfoService.findCustNo());
                    custFileAduitTemp.setAduitCustNo(anCustNo);
                    custFileAduitTemp.setId(custFileItemService.updateDuplicateCustFileItemInfo(custFileItem.getId(),
                            UserUtils.getOperatorInfo()));
                    custFileAduitTemp.setWorkType(custFileItem.getFileInfoType());
                    custFileAduitTemp.setAuditStatus("1");
                    final CustOperatorInfo custOperator = (CustOperatorInfo) UserUtils.getPrincipal().getUser();
                    custFileAduitTemp.setOperNo(String.valueOf(custOperator.getId()));
                    custFileAduitTemp.initValue();
                    this.insert(custFileAduitTemp);
                }
            }
            if (custFileAduitTemp != null) {
                final CustFileItem custFileItem = custFileItemService.findOneByBatchNo(custFileAduitTemp.getId(),
                        agencyAuthorFileGroup.getFileInfoType());
                if (custFileItem == null) {
                    bool = true;
                    break;
                }
            } else {
                bool = true;
                break;
            }
        }
        return bool;
    }

    /***
     * 删除临时审核附件关系表数据
     * @param anId 附件 id
     * @return
     */
    public boolean saveDeleteFileAduitTemp(final Long anId) {
        final CustFileItem anFile = custFileItemService.selectByPrimaryKey(anId);
        BTAssert.notNull(anFile, "无法获取相应附件!");
        final List<CustFileAduitTemp> custFileAduitTempList = this.selectByProperty("id", anFile.getBatchNo());
        final CustFileAduitTemp fileAduitTemp = Collections3.getFirst(custFileAduitTempList);
        if (fileAduitTemp != null) {
            fileAduitTemp.setWorkType("-" + fileAduitTemp.getWorkType());
            fileAduitTemp.setAduitCustNo(-fileAduitTemp.getAduitCustNo());
            final Map<String, Object> anMap = new HashMap<String, Object>();
            anMap.put("id", fileAduitTemp.getId());
            return this.updateByExample(fileAduitTemp, anMap) > 0;
        }

        return true;
    }

    /***
     * 添加客户文件关系
     * @param anRelationCustNo 关联的客户号
     * @param fileIds 上传的文件列表(以,分隔)
     */
    public void saveCustFileAduitTemp(final Long anCustNo, final Long anRelateCustNo, final String anFileIds,
            final String anCustType) {
        for (final String fileId : anFileIds.split(",")) {
            if (StringUtils.isNoneBlank(fileId)) {
                final CustFileItem anFile = custFileItemService.selectByPrimaryKey(Long.parseLong(fileId));

                final CustFileAduitTemp custFileAduitTemp = findCustFileAduitTempByType(anRelateCustNo, anCustNo,
                        anFile.getFileInfoType());
                if (custFileAduitTemp == null) { // 添加绑定关系
                    addCustFileRelate(anCustNo, anRelateCustNo, anFile, anCustType);
                } else if (StringUtils.equalsIgnoreCase("0", custFileAduitTemp.getAuditStatus())
                        && StringUtils.equalsIgnoreCase(String.valueOf(custFileAduitTemp.getId()),
                                String.valueOf(anFile.getBatchNo()))) { // 审核失败且没有重新上传情况，将原来的文件状态改回成已上传状态
                    custFileAduitTemp.setAuditStatus("2");
                    final Map<String, Object> anMap = new HashMap<String, Object>();
                    anMap.put("id", custFileAduitTemp.getId());
                    this.updateByExample(custFileAduitTemp, anMap);
                } else if (!StringUtils.equalsIgnoreCase(String.valueOf(custFileAduitTemp.getId()),
                        String.valueOf(anFile.getBatchNo()))) { // 将原来的废弃，添加新的绑定
                    custFileAduitTemp.setWorkType("-" + custFileAduitTemp.getWorkType());
                    custFileAduitTemp.setAduitCustNo(-custFileAduitTemp.getAduitCustNo());
                    final Map<String, Object> anMap = new HashMap<String, Object>();
                    anMap.put("id", custFileAduitTemp.getId());
                    this.updateByExample(custFileAduitTemp, anMap);
                    // // 附件修改
                    // anFile.setBatchNo(-anFile.getBatchNo());
                    // custFileItemService.updateByPrimaryKey(anFile);
                    // 删除沃通关系审核附件表
                    if (StringUtils.equalsIgnoreCase(String.valueOf(PlatformBaseRuleType.WOS), anCustType)) {
                        custFileAuditService.deleteByPrimaryKey(custFileAduitTemp.getId());
                    }
                    // 添加新关系
                    addCustFileRelate(anCustNo, anRelateCustNo, anFile, anCustType);
                }
            }
        }
    }

    /***
     * 添加客户文件关系
     * @param anCustNo
     * @param anRelateCustNo
     * @param anFile
     */
    public void addCustFileRelate(final Long anCustNo, final Long anRelateCustNo, final CustFileItem anFile,
            final String anCustType) {
        final CustOperatorInfo custOperator = (CustOperatorInfo) UserUtils.getPrincipal().getUser();
        final CustFileAduitTemp custFileAduitTemp = new CustFileAduitTemp();
        custFileAduitTemp.setCustNo(anCustNo);
        custFileAduitTemp.setAduitCustNo(anRelateCustNo);
        custFileAduitTemp.setId(custFileItemService.updateDuplicateCustFileItemInfo(anFile.getId(), custOperator));
        custFileAduitTemp.setWorkType(anFile.getFileInfoType());
        custFileAduitTemp.setAuditStatus("2");
        custFileAduitTemp.setOperNo(String.valueOf(custOperator.getId()));
        custFileAduitTemp.initValue();
        this.insert(custFileAduitTemp);
        if (StringUtils.equalsIgnoreCase(String.valueOf(PlatformBaseRuleType.WOS), anCustType)) { // 如果是沃通服务，在正式关系表中添加文件关系
            final CustFileAduit custFileAduit = new CustFileAduit();
            BeanMapper.copy(custFileAduitTemp, custFileAduit);
            custFileAuditService.addCustFileAduit(custFileAduit);
        }
    }

    /***
     * 查询关系审核附件
     * @param anCustNo
     * @return
     */
    public List<CustFileItem> findRelateAduitTempFile(final Long anCustNo) {
        final List<CustFileItem> custFileItemList = new ArrayList<CustFileItem>();
        final Map<String, Object> anMap = setLoginParam(anCustNo);
        anMap.put("auditStatus", new String[] { "0", "1", "2" });

        for (final CustFileAduitTemp custFileAduitTemp : this.selectByProperty(anMap)) {
            final CustFileItem custFileItem = custFileItemService.findOneByBatchNo(custFileAduitTemp.getId(),
                    custFileAduitTemp.getWorkType());
            if (custFileItem != null) {
                custFileItem.setFileDescription(agencyAuthFileGroupService.findAuthFileGroup(
                        custFileItem.getFileInfoType()).getDescription());
                custFileItemList.add(custFileItem);
            }
        }
        return custFileItemList;
    }

    /***
     * 保存附件关系
     * @param passFiles 审核通过的文件列表
     * @param failFiles 审核不通过的文件列表
     */
    public void saveAcceptFileTemp(final String anPassFiles, final String anFailFiles) {
        for (final String batchNo : anPassFiles.split(",")) { // 处理通过的文件列表
            final List<CustFileAduitTemp> tempList = this.selectByProperty("id", batchNo);
            if (tempList != null && tempList.size() > 0) {
                final CustFileAduitTemp custFileAduitTemp = Collections3.getFirst(tempList);
                custFileAduitTemp.setAuditStatus("1");
                custFileAduitTemp.saveInitValue();
                final Map<String, Object> anMap = new HashMap<String, Object>();
                anMap.put("id", custFileAduitTemp.getId());
                this.updateByExample(custFileAduitTemp, anMap);
            }
        }
        for (final String batchNo : anFailFiles.split(",")) { // 处理不通过的文件列表
            final List<CustFileAduitTemp> tempList = this.selectByProperty("id", batchNo);
            if (tempList != null && tempList.size() > 0) {
                final CustFileAduitTemp custFileAduitTemp = Collections3.getFirst(tempList);
                custFileAduitTemp.setAuditStatus("0");
                custFileAduitTemp.saveInitValue();
                final Map<String, Object> anMap = new HashMap<String, Object>();
                anMap.put("id", custFileAduitTemp.getId());
                this.updateByExample(custFileAduitTemp, anMap);
            }
        }
    }

    /***
     * 查询关系附件保存到正式表中
     * @param anCustNo
     * @param anRelateCustNo
     */
    public void saveAduitFile(final Long anCustNo, final Long anRelateCustNo) {
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("aduitCustNo", custInfoService.findCustNo());
        anMap.put("auditStatus", "1");
        for (final CustFileAduitTemp custFileAduitTemp : this.selectByProperty(anMap)) {
            final CustFileAduit aduit = custFileAuditService.selectByPrimaryKey(custFileAduitTemp.getId());
            if (aduit == null) {
                final CustFileAduit custFileAduit = new CustFileAduit();
                BeanMapper.copy(custFileAduitTemp, custFileAduit);
                custFileAuditService.addCustFileAduit(custFileAduit);
            }
        }
    }

    public void saveCustFileAduitTempFile(final Long custNo, final String fileIds) {
        final CustFileAduitTemp custFileAduitTemp = new CustFileAduitTemp();
        custFileAduitTemp.init("", custNo);
        custFileAduitTemp.setBatchNo(custFileItemService.updateAndDelCustFileItemInfo(fileIds, null));
        this.insert(custFileAduitTemp);
        // 保存审批
        final CustChangeApply changeApply = changeApplyService.addChangeApply(custNo, CustomerConstants.ITEM_FUND_FILE,
                String.valueOf(custFileAduitTemp.getId()));
        saveCustFileAduitTempParentId(custFileAduitTemp.getId(), changeApply.getId());

    }

    private void saveCustFileAduitTempParentId(final Long anId, final Long anParentId) {

        final CustFileAduitTemp custFileAduitTemp = Collections3.getFirst(this.selectByProperty("id", anId));
        custFileAduitTemp.setParentId(anParentId);
        final Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", anId);
        final int i = this.updateByExample(custFileAduitTemp, map);
        logger.debug("===:" + i);
        // this.updateByPrimaryKeySelective(custFileAduitTemp);
        // this.updateByPrimaryKey(custFileAduitTemp);
    }

    @Override
    public void saveFormalData(final Long anParentId) {
        final CustFileAduitTemp custfileAduitTemp = Collections3
                .getFirst(this.selectByProperty("parentId", anParentId));
        // 审核这一步可将临时表中的数据删除
        if (custfileAduitTemp != null) {
            this.delete(custfileAduitTemp);
        }
    }

    @Override
    public void saveCancelData(final Long anParentId) {

    }

    @Override
    public ICustAuditEntityFace findSaveDataByParentId(final Long anParentId) {
        return Collections3.getFirst(this.selectByProperty("parentId", anParentId));
    }

    public ChangeDetailBean<CustFileAduitTemp> findChangeApply(final Long anId) {
        final CustChangeApply changeApply = changeService.findChangeApply(anId, CustomerConstants.ITEM_FUND_FILE);
        final Long tmpId = Long.valueOf(changeApply.getTmpIds());
        final CustFileAduitTemp nowData = Collections3.getFirst(this.selectByProperty("id", tmpId));
        final ChangeDetailBean<CustFileAduitTemp> changeDetailBean = new ChangeDetailBean<>();
        changeDetailBean.setChangeApply(changeApply);
        changeDetailBean.setNowData(nowData);
        changeDetailBean.setBefData(new CustFileAduitTemp());
        return changeDetailBean;
    }
}
