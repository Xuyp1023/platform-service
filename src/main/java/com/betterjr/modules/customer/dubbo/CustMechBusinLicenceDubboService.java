package com.betterjr.modules.customer.dubbo;

import java.util.Map;

import javax.annotation.Resource;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.common.web.AjaxObject;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.ICustMechBusinLicenceService;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustMechBaseTmp;
import com.betterjr.modules.customer.entity.CustMechBusinLicence;
import com.betterjr.modules.customer.entity.CustMechBusinLicenceTmp;
import com.betterjr.modules.customer.helper.ChangeDetailBean;
import com.betterjr.modules.customer.service.CustChangeService;
import com.betterjr.modules.customer.service.CustInsteadService;
import com.betterjr.modules.customer.service.CustMechBusinLicenceService;
import com.betterjr.modules.customer.service.CustMechBusinLicenceTmpService;
import com.betterjr.modules.rule.service.RuleServiceDubboFilterInvoker;

/**
 * 营业执照
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustMechBusinLicenceService.class)
public class CustMechBusinLicenceDubboService implements ICustMechBusinLicenceService {

    @Resource
    private CustChangeService changeService;

    @Resource
    private CustInsteadService insteadService;

    @Resource
    private CustMechBusinLicenceService businLicenceService;
    
    @Resource
    private CustMechBusinLicenceTmpService businLicenceTmpService;

    @Override
    public String webFindBusinLicence(Long anCustNo) {
        final CustMechBusinLicence businLicence = businLicenceService.findBusinLicenceByCustNo(anCustNo);
        return AjaxObject.newOk("营业执照信息-详情查询 成功", businLicence).toJson();
    }

    @Override
    public String webFindChangeApply(Long anId) {
        final CustChangeApply changeApply = changeService.findChangeApply(anId, CustomerConstants.ITEM_BUSINLICENCE);
        
        final Long tmpId = Long.valueOf(changeApply.getTmpIds());
        
        final CustMechBusinLicenceTmp nowData = businLicenceTmpService.findBusinLicenceTmp(tmpId);
        final CustMechBusinLicenceTmp befData = businLicenceTmpService.findBusinLicenceTmpPrevVersion(nowData);
        
        ChangeDetailBean<CustMechBusinLicenceTmp> changeDetailBean = new ChangeDetailBean<>();
        changeDetailBean.setChangeApply(changeApply);
        if (nowData.getTmpOperType() != CustomerConstants.TMP_OPER_TYPE_DELETE) {
            changeDetailBean.setNowData(nowData);
        }
        changeDetailBean.setBefData(befData);
        return AjaxObject.newOk("营业执照信息-变更详情查询 成功", changeDetailBean).toJson();
    }

    @Override
    public String webQueryChangeApply(Long anCustNo, int anFlag, int anPageNum, int anPageSize) {
        final Page<CustChangeApply> changeApplys = changeService.queryChangeApply(anCustNo, CustomerConstants.ITEM_BUSINLICENCE, anFlag, anPageNum,
                anPageSize);
        return AjaxObject.newOkWithPage("营业执照信息-变更列表 成功", changeApplys).toJson();
    }

    @Override
    public String webAddChangeApply(Map<String, Object> anParam, String anFileList) {
        final CustMechBusinLicenceTmp businLicenceTmp = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("营业执照信息-变更申请 成功", businLicenceTmpService.addChangeApply(businLicenceTmp, anFileList)).toJson();
    }

    @Override
    public String webSaveChangeApply(Map<String, Object> anParam, Long anApplyId, String anFileList) {
        final CustMechBusinLicenceTmp businLicenceTmp = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("营业执照信息-变更修改 成功", businLicenceTmpService.saveChangeApply(businLicenceTmp, anApplyId, anFileList)).toJson();
    }
    
    @Override
    public String webFindInsteadRecord(Long anInsteadRecordId) {
        return AjaxObject.newOk("营业执照信息-代录详情 成功", businLicenceTmpService.findBusinLicenceTmpByInsteadRecord(anInsteadRecordId)).toJson();
    }

    @Override
    public String webAddInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId, String anFileList) {
        final CustMechBusinLicenceTmp businLicenceTmp = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("营业执照信息息-添加代录 成功", businLicenceTmpService.addInsteadRecord(businLicenceTmp, anInsteadRecordId, anFileList)).toJson();
    }

    @Override
    public String webSaveInsteadRecord(Map<String, Object> anParam, Long anInsteadRecordId, String anFileList) {
        final CustMechBusinLicenceTmp businLicenceTmp = RuleServiceDubboFilterInvoker.getInputObj();
        return AjaxObject.newOk("营业执照信息-代录修改 成功", businLicenceTmpService.saveInsteadRecord(businLicenceTmp, anInsteadRecordId, anFileList)).toJson();
     }


}
