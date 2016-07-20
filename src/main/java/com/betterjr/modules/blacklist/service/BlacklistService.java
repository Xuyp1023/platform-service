package com.betterjr.modules.blacklist.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.Collections3;
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
    
    /**
     * 黑名单录入
     * 
     * @param anMap
     * @return
     */
    public Blacklist addBlacklist(Blacklist anBlacklist) {
        logger.info("Begin to add blacklist");

        // 检查是否已存在黑名单
        boolean isExists = checkBlacklistExists(anBlacklist.getIdentNo());
        if (isExists == true) {
            logger.error("已存在该黑名单信息");
            throw new BytterTradeException(40001, "已存在该黑名单信息");
        }

        // 初始化黑名单
        anBlacklist.initAddValue();
        anBlacklist.initLawName(anBlacklist.getCustType());

        // 数据存盘
        this.insert(anBlacklist);

        return anBlacklist;
    }
    
    /**
     * 检查是否存在黑名单
     * 
     * @param anIdentNo
     * @return
     */
    private boolean checkBlacklistExists(String anIdentNo) {
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("identNo", anIdentNo);
        Blacklist anBlacklist = Collections3.getFirst(this.selectByProperty(anMap));
        if (null == anBlacklist) {
            return false;
        }
        else {
            return true;
        }
    }
    
}
