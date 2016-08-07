package com.betterjr.modules.customer.helper;

import com.betterjr.common.service.SpringContextHolder;
import com.betterjr.modules.customer.constant.CustomerConstants;
import com.betterjr.modules.customer.entity.CustChangeApply;
import com.betterjr.modules.customer.entity.CustInsteadRecord;
import com.betterjr.modules.customer.service.CustMechBankAccountTmpService;
import com.betterjr.modules.customer.service.CustMechBaseTmpService;
import com.betterjr.modules.customer.service.CustMechBusinLicenceTmpService;
import com.betterjr.modules.customer.service.CustMechContacterTmpService;
import com.betterjr.modules.customer.service.CustMechLawTmpService;
import com.betterjr.modules.customer.service.CustMechManagerTmpService;
import com.betterjr.modules.customer.service.CustMechShareholderTmpService;
import com.betterjr.modules.customer.service.CustOpenAccountTmpService;

/**
 * 
 * @author liuwl
 *
 */
public final class FormalDataHelper {

    public static IFormalDataService getFormalDataService(CustChangeApply anCustChangeApply) {
        return getFormalDataService(anCustChangeApply.getChangeItem());
    }

    public static IFormalDataService getFormalDataService(CustInsteadRecord anCustInsteadRecord) {
        return getFormalDataService(anCustInsteadRecord.getInsteadItem());
    }

    /**
     * 得到保存正式数据服务
     * 
     * @param anItem
     * @return
     */
    public static IFormalDataService getFormalDataService(String anItem) {
        IFormalDataService saveFormalData = null;
        switch (anItem) {
        case CustomerConstants.ITEM_BASE:
            saveFormalData = (IFormalDataService) SpringContextHolder.getBean(CustMechBaseTmpService.class);
            break;
        case CustomerConstants.ITEM_LAW:
            saveFormalData = (IFormalDataService) SpringContextHolder.getBean(CustMechLawTmpService.class);
            break;
        case CustomerConstants.ITEM_SHAREHOLDER:
            saveFormalData = (IFormalDataService) SpringContextHolder.getBean(CustMechShareholderTmpService.class);
            break;
        case CustomerConstants.ITEM_MANAGER:
            saveFormalData = (IFormalDataService) SpringContextHolder.getBean(CustMechManagerTmpService.class);
            break;
        case CustomerConstants.ITEM_BUSINLICENCE:
            saveFormalData = (IFormalDataService) SpringContextHolder.getBean(CustMechBusinLicenceTmpService.class);
            break;
        case CustomerConstants.ITEM_CONTACTER:
            saveFormalData = (IFormalDataService) SpringContextHolder.getBean(CustMechContacterTmpService.class);
            break;
        case CustomerConstants.ITEM_BANKACCOUNT:
            saveFormalData = (IFormalDataService) SpringContextHolder.getBean(CustMechBankAccountTmpService.class);
            break;
        case CustomerConstants.ITEM_OPENACCOUNT:
            saveFormalData = (IFormalDataService) SpringContextHolder.getBean(CustOpenAccountTmpService.class);
            break;
        default:

        }
        return saveFormalData;
    }
}
