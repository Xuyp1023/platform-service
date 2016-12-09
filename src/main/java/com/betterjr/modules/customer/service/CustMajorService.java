package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.Collections3;
import com.betterjr.modules.customer.dao.CustMajorMapper;
import com.betterjr.modules.customer.entity.CustMajor;
/***
 * 平台重要客户
 * @author hubl
 *
 */
@Service
public class CustMajorService extends BaseService<CustMajorMapper, CustMajor>{

    /***
     * 根据客户号查询重要客户对象
     * @param anCustNo
     * @return
     */
    public CustMajor findCustMajorByCustNo(Long anCustNo){
        Map<String, Object> anMap=new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("businStatus", "1");
        return Collections3.getFirst(this.selectByProperty(anMap));
    }
   
    /***
     * 根据服务类型查询信息
     * @param anCustCorp
     * @return
     */
    public List<CustMajor> findCustMajorByCustCorp(String anCustCorp){
        Map<String, Object> anMap=new HashMap<String, Object>();
        anMap.put("custCorp", anCustCorp);
        anMap.put("businStatus", "1");
        return this.selectByProperty(anMap);
    }
    
}
