package com.betterjr.modules.customer.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.account.entity.CustInfo;
import com.betterjr.modules.customer.dao.CustInfoRoleMapper;
import com.betterjr.modules.customer.entity.CustInfoRole;

/**
 * 审核记录服务
 * 
 * @author tangzw
 *
 */
@Service
public class CustInfoRoleService extends BaseService<CustInfoRoleMapper, CustInfoRole> {

    /**
     * 根据用户编号查询该用户拥有的角色列表
     */
    public List<CustInfoRole> findCustRoles(Long anCustNo) {
        BTAssert.notNull(anCustNo, "企业编号不能为空！");
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("custNo", anCustNo);
        return this.selectByProperty(conditionMap);
    }

    public boolean hasRole(Long anCustNo, String anOperRole) {
        BTAssert.notNull(anCustNo, "企业编号不能为空！");
        BTAssert.notNull(anOperRole, "角色不能为空！");
        Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("custNo", anCustNo);
        conditionMap.put("operRole", anOperRole);
        List<CustInfoRole> list = this.selectByProperty(conditionMap);
        if (Collections3.isEmpty(list)) {
            return false;
        }
        return true;
    }

    /**
     * 去除没有角色的信息
     * @param anCustInfos
     * @return
     */
    public List<CustInfo> custInfoFilter(Collection<CustInfo> anCustInfos) {
        List<CustInfo> useCustInfos = new ArrayList<CustInfo>();
        String currRole = UserUtils.getUserRole().name();
        for (CustInfo custInfo : anCustInfos) {
            List<CustInfoRole> list = this.findCustRoles(custInfo.getCustNo());

            // 默认用户
            if (Collections3.isEmpty(list)) {
                useCustInfos.add(custInfo);
                continue;
            }

            // 广西建工的特殊用户
            for (CustInfoRole role : list) {
                if (currRole.equals(role.getOperRole())) {
                    useCustInfos.add(custInfo);
                    break;
                }
            }
        }

        return useCustInfos;
    }

}