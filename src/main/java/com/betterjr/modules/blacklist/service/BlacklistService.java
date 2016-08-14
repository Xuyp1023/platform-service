package com.betterjr.modules.blacklist.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.BetterStringUtils;
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
        // 检查是否为保理操作员,保理操作员必须使用operOrg进行数据过滤,且需要处理入参:custType类型(0:个人;1:机构;)
        anMap.put("operOrg", UserUtils.getOperatorInfo().getOperOrg());
        Object anCreditType = anMap.get("custType");
        if (null == anCreditType || anCreditType.toString().isEmpty()) {
            anMap.put("custType", new String[] { "0", "1"});
        }
        
        // 检查是否为平台用户(待确定...),可查看所有黑名单信息

        return this.selectPropertyByPage(Blacklist.class, anMap, anPageNum, anPageSize, "1".equals(anFlag));
    }

    /**
     * 黑名单录入
     * 
     * @param anMap
     * @return
     */
    public Blacklist addBlacklist(Blacklist anBlacklist) {
        logger.info("Begin to add blacklist");

        // 检查
        checkBlacklist(anBlacklist);

        // 初始化黑名单
        anBlacklist.initAddValue();
        anBlacklist.initLawName(anBlacklist.getCustType());

        // 数据存盘
        this.insert(anBlacklist);

        return anBlacklist;
    }

    private void checkBlacklist(Blacklist anBlacklist) {
        // 检查证件号码
        BTAssert.notNull(anBlacklist.getIdentNo(), "证件号码不允许为空");

        // 检查姓名
        BTAssert.notNull(anBlacklist.getName(), "被执行人姓名/名称不允许为空");

        // 检查法人
        if (BetterStringUtils.equals(anBlacklist.getCustType(), "1") == true) {
            BTAssert.notNull(anBlacklist.getLawName(), "当客户类型为机构时,法人信息不允许为空");
        }

        // 检查是否已存在黑名单-证件号码
        if (queryBlacklistByIdentNo(anBlacklist.getIdentNo()).size() > 0) {
            logger.warn("该证件号码在黑名单信息中已存在");
            throw new BytterTradeException(40001, "该证件号码在黑名单信息中已存在");
        }

        // 检查是否已存在黑名单-机构名称或姓名
        if (queryBlacklistByName(anBlacklist.getName()).size() > 0) {
            logger.warn("该机构名称或姓名在黑名单信息中已存在");
            throw new BytterTradeException(40001, "该机构名称或姓名在黑名单信息中已存在");
        }

        // 检查是否已存在黑名单-法人
        if (queryBlacklistByName(anBlacklist.getLawName()).size() > 0) {
            logger.warn("该法人在黑名单信息中已存在");
            throw new BytterTradeException(40001, "该法人在黑名单信息中已存在");
        }
    }

    /**
     * 黑名单修改
     * 
     * @param anMap
     * @param anId
     * @return
     */
    public Blacklist saveModifyBlacklist(Blacklist anModiBlacklist, Long anId) {
        logger.info("Begin to modify blacklist");

        // 加载原黑名单记录
        Blacklist anBlacklist = this.selectByPrimaryKey(anId);
        BTAssert.notNull(anBlacklist, "无法加载原黑名单信息");

        // 检查当前操作员是否能修改该黑名单
        checkOperator(anBlacklist.getOperOrg(), "当前操作员不能修改该黑名单信息");

        // 不允许修改已生效(businStatus:1)的黑名单
        checkStatus(anBlacklist.getBusinStatus(), "1", true, "当前黑名单已生效,不允许修改");

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
        BTAssert.notNull(anBlacklist, "无法加载黑名单信息");

        // 检查当前操作员是否能激活该黑名单
        checkOperator(anBlacklist.getOperOrg(), "当前操作员不能激活该黑名单");

        // 不允许激活已生效(businStatus:1)的黑名单
        checkStatus(anBlacklist.getBusinStatus(), "1", true, "当前黑名单已激活");

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
        BTAssert.notNull(anBlacklist, "无法加载黑名单信息");

        // 检查当前操作员是否能注销该黑名单
        checkOperator(anBlacklist.getOperOrg(), "当前操作员不能注销该黑名单");

        // 不允许注销未生效(businStatus:0)的黑名单
        checkStatus(anBlacklist.getBusinStatus(), "0", true, "当前黑名单未生效,不需要注销");

        // 设置黑名单注销状态(businStatus:0)
        anBlacklist.setBusinStatus("0");

        // 数据存盘
        this.updateByPrimaryKey(anBlacklist);

        return anBlacklist;
    }

    /**
     * 黑名单删除
     * 
     * @param anId
     */
    public int saveDeleteBlacklist(Long anId) {
        logger.info("Begin to delete blacklist");

        // 加载黑名单记录
        Blacklist anBlacklist = this.selectByPrimaryKey(anId);
        BTAssert.notNull(anBlacklist, "无法加载黑名单信息");

        // 检查当前操作员是否能删除该黑名单
        checkOperator(anBlacklist.getOperOrg(), "当前操作员不能删除该黑名单");

        // 不允许删除已生效(businStatus:1)的黑名单
        checkStatus(anBlacklist.getBusinStatus(), "1", true, "当前黑名单已生效,不允许删除");

        // 删除黑名单
        return this.deleteByPrimaryKey(anId);
    }

    /**
     * 检查是否存在黑名单
     * 
     * @param anName
     * @param anIdentNo
     * @param anLawName
     * @return
     */
    public String checkBlacklistExists(String anName, String anIdentNo, String anLawName) {
        // 入参不能同时为空
        if (null == anName && null == anIdentNo && null == anLawName) {
            logger.warn("参数不能为空");
            throw new IllegalArgumentException("参数不能为空");
        }
        List<Blacklist> resultByName = queryBlacklistByName(anName);
        List<Blacklist> resultByIdentNo = queryBlacklistByIdentNo(anIdentNo);
        List<Blacklist> resultByLawName = queryBlacklistByLawName(anLawName);
        if (resultByName.size() > 0 || resultByIdentNo.size() > 0 || resultByLawName.size() > 0) {
            return "1";
        }
        return "0";
    }

    private List<Blacklist> queryBlacklistByIdentNo(String anIdentNo) {
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("identNo", anIdentNo);

        return this.selectByProperty(anMap);
    }

    private List<Blacklist> queryBlacklistByName(String anName) {
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("name", anName);

        return this.selectByProperty(anMap);
    }

    private List<Blacklist> queryBlacklistByLawName(String anLawName) {
        Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("lawName", anLawName);

        return this.selectByProperty(anMap);
    }

    private void checkOperator(String anOperOrg, String anMessage) {
        if (BetterStringUtils.equals(UserUtils.getOperatorInfo().getOperOrg(), anOperOrg) == false) {
            logger.warn(anMessage);
            throw new BytterTradeException(40001, anMessage);
        }
    }

    private void checkStatus(String anBusinStatus, String anTargetStatus, boolean anFlag, String anMessage) {
        if (BetterStringUtils.equals(anBusinStatus, anTargetStatus) == anFlag) {
            logger.warn(anMessage);
            throw new BytterTradeException(40001, anMessage);
        }
    }

}
