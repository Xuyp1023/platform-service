package com.betterjr.modules.customer.helper;

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
    public void saveFormalData(String... anTmpIds);
    
    /**
     * 作废数据状态回写接口
     * @param anTmpIds
     */
    public void saveCancelData(String... anTmpIds);
}
