package com.betterjr.modules.customer.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.dao.CustInsteadApplyMapper;
import com.betterjr.modules.customer.entity.CustInsteadApply;

/**
 * 代录申请
 * 
 * @author liuwl
 *
 */
@Service
public class CustInsteadApplyService extends BaseService<CustInsteadApplyMapper, CustInsteadApply> {
    private static Logger logger = LoggerFactory.getLogger(CustInsteadApplyService.class);

    /**
     * 添加代录申请 检查是否已经有申请在进行中
     * 
     * @param anCustInsteadApply
     * @return
     */
    public CustInsteadApply addCustInsteadApply(CustInsteadApply anCustInsteadApply) {
        
        return null;
    }

    /**
     * 查询代录申请
     * @param anCustNo
     * @param anId
     * @return
     */
    public CustInsteadApply findCustInsteadApply(Long anCustNo, Long anId) {
        return null;
    }

    /**
     * 保存代录申请
     * @param anCustInsteadApply
     * @return
     */
    public CustInsteadApply saveCustInsteadApply(CustInsteadApply anCustInsteadApply) {
        return null;
    }
    
    /**
     * 查询代表列表 平台使用
     * @return
     */
    public Page<CustInsteadApply> queryCustInsteadApply() {
        return null;
    }
}