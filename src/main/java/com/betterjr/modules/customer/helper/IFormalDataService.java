package com.betterjr.modules.customer.helper;

import com.betterjr.modules.customer.data.ICustAuditEntityFace;

/**
 * @author liuwl
 *
 */
public interface IFormalDataService {
    /**
     * 保存数据至正式表接口
     * 
     * @param anTmpIds
     * @return
     */
    public void saveFormalData(Long anParentId);

    /**
     * 作废数据状态回写接口
     * 
     * @param anTmpIds
     */
    public void saveCancelData(Long anParentId);

    public ICustAuditEntityFace findSaveDataByParentId(Long anParentId);
}
