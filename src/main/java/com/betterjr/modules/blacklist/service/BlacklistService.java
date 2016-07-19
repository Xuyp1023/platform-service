package com.betterjr.modules.blacklist.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.blacklist.dao.BlacklistMapper;
import com.betterjr.modules.blacklist.entity.Blacklist;

@Service
public class BlacklistService extends BaseService<BlacklistMapper, Blacklist> {

    /**
     * 黑名单信息分页查询
     * 
     * @param anMap
     * @param anFlag
     * @param anPageNum
     * @param anPageSize
     * @return
     */
    public Page<Blacklist> queryBlacklist(Map<String, Object> anMap, String anFlag, int anPageNum, int anPageSize) {
        // 判断是否为平台用户(待确定...),保理商仅可以查询自己的黑名单记录
        anMap.put("operOrg", UserUtils.getOperatorInfo().getOperOrg());
        
        // 查询黑名单记录
        Page<Blacklist> anBlacklist = this.selectPropertyByPage(Blacklist.class, anMap, anPageNum, anPageSize, "1".equals(anFlag));

        return anBlacklist;
    }
    
}
