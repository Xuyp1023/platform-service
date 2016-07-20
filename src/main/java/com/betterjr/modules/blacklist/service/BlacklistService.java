package com.betterjr.modules.blacklist.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.account.entity.CustOperatorInfo;
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
     * 黑名单修改
     * 
     * @param anMap
     * @param anId
     * @return
     */
    public Blacklist saveModifyBlacklist(Blacklist anModiBlacklist) {
        logger.info("Begin to modify blacklist");

        // 加载原黑名单记录
        Blacklist anBlacklist = this.selectByPrimaryKey(anModiBlacklist.getId());
        if (null == anBlacklist) {
            logger.error("无法加载原黑名单信息");
            throw new BytterTradeException(40001, "无法加载原黑名单信息");
        }

        // 检查当前操作员是否能修改该黑名单
        CustOperatorInfo operator = UserUtils.getOperatorInfo();
        if (BetterStringUtils.equals(operator.getOperOrg(), anBlacklist.getOperOrg()) == false) {
            logger.warn("当前操作员不能修改该黑名单信息");
            throw new BytterTradeException(40001, "当前操作员不能修改该黑名单信息");
        }

        // 不允许修改已生效(businStatus:1)的黑名单
        String status = anBlacklist.getBusinStatus();
        if (BetterStringUtils.equals("1", status) == true) {
            logger.warn("当前黑名单已生效,不允许修改");
            throw new BytterTradeException(40001, "当前黑名单已生效,不允许修改");
        }

        // 初始化黑名单修改信息
        anModiBlacklist.initModifyValue(anBlacklist);
        anBlacklist.initLawName(anBlacklist.getCustType());

        // 数据存盘
        this.updateByPrimaryKey(anModiBlacklist);

        return anModiBlacklist;
    }

    /**
     * 黑名单激活
     * 
     * @param anId
     * @return
     */
    public Blacklist saveActivateBlacklist(Long anId) {
        logger.info("Begin to activate blacklist");

        // 获取黑名单记录
        Blacklist anBlacklist = this.selectByPrimaryKey(anId);
        if (null == anBlacklist) {
            logger.error("无法获取黑名单");
            throw new BytterTradeException(40001, "无法获取黑名单");
        }

        // 检查当前操作员是否能激活该黑名单
        CustOperatorInfo operator = UserUtils.getOperatorInfo();
        if (BetterStringUtils.equals(operator.getOperOrg(), anBlacklist.getOperOrg()) == false) {
            logger.warn("当前操作员不能激活该黑名单");
            throw new BytterTradeException(40001, "当前操作员不能激活该黑名单");
        }

        // 不允许激活已生效(businStatus:1)的黑名单
        String anBusinStatus = anBlacklist.getBusinStatus();
        if (BetterStringUtils.equals("1", anBusinStatus) == true) {
            logger.warn("当前黑名单已激活");
            throw new BytterTradeException(40001, "当前黑名单已激活");
        }

        // 设置黑名单激活状态(businStatus:1)
        anBlacklist.setBusinStatus("1");

        // 数据存盘
        this.updateByPrimaryKey(anBlacklist);

        return anBlacklist;
    }

    /**
     * 黑名单注销
     * 
     * @param anId
     * @return
     */
    public Blacklist saveCancelBlacklist(Long anId) {
        logger.info("Begin to cancel blacklist");

        // 获取黑名单记录
        Blacklist anBlacklist = this.selectByPrimaryKey(anId);
        if (null == anBlacklist) {
            logger.error("无法获取黑名单信息");
            throw new BytterTradeException(40001, "无法获取黑名单信息");
        }

        // 检查当前操作员是否能注销该黑名单
        CustOperatorInfo operator = UserUtils.getOperatorInfo();
        if (BetterStringUtils.equals(operator.getOperOrg(), anBlacklist.getOperOrg()) == false) {
            logger.warn("当前操作员不能注销该黑名单");
            throw new BytterTradeException(40001, "当前操作员不能注销该黑名单");
        }

        // 仅允许注销已生效(businStatus:1)的黑名单
        String anBusinStatus = anBlacklist.getBusinStatus();
        if (BetterStringUtils.equals("1", anBusinStatus) == false) {
            logger.warn("当前黑名单未生效,不需要注销");
            throw new BytterTradeException(40001, "当前黑名单未生效,不需要注销");
        }

        // 设置黑名单注销状态(businStatus:0)
        anBlacklist.setBusinStatus("0");

        // 数据存盘
        this.updateByPrimaryKey(anBlacklist);

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
