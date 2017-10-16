package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import com.betterjr.common.data.SimpleDataEntity;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BTAssert;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.QueryTermBuilder;
import com.betterjr.common.utils.UserUtils;
import com.betterjr.modules.customer.dao.CustMajorMapper;
import com.betterjr.modules.customer.entity.CustMajor;

/***
 * 平台重要客户
 * @author hubl
 *
 */
@Service
public class CustMajorService extends BaseService<CustMajorMapper, CustMajor> {

    /**
     * 查询保理公司列表
     * @return
     */
    public List<SimpleDataEntity> queryFactorList() {
        BTAssert.isTrue(UserUtils.platformUser(), "操作失败！");

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("custType", "3");
        conditionMap.put("businStatus", "1");

        return this.selectByProperty(conditionMap).stream()
                .map(custMajor -> new SimpleDataEntity(custMajor.getCustName(), custMajor.getCustNo().toString()))
                .collect(Collectors.toList());
    }

    /**
     * 查询保理公司列表
     * @return
     */
    public List<SimpleDataEntity> queryElecContractCustList() {

        final Map<String, Object> conditionMap = new HashMap<>();
        conditionMap.put("custType", "1");
        conditionMap.put("businStatus", "1");

        return this.selectByProperty(conditionMap).stream()
                .map(custMajor -> new SimpleDataEntity(custMajor.getCustName(), custMajor.getCustNo().toString()))
                .collect(Collectors.toList());
    }

    /***
     * 根据客户号查询重要客户对象
     * @param anCustNo
     * @return
     */
    public CustMajor findCustMajorByCustNo(final Long anCustNo) {
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custNo", anCustNo);
        anMap.put("businStatus", "1");
        return Collections3.getFirst(this.selectByProperty(anMap));
    }

    /***
     * 根据服务类型查询信息
     * @param anCustCorp
     * @return
     */
    public List<CustMajor> findCustMajorByCustCorp(final String anCustCorp) {
        final Map<String, Object> anMap = new HashMap<String, Object>();
        anMap.put("custCorp", anCustCorp);
        anMap.put("businStatus", "1");
        return this.selectByProperty(anMap);
    }

    /***
     * 根据客户类型查询重要客户对象
     * @param anCustNo
     * @return
     */
    public List<CustMajor> findCustMajorByType(final String anCustType) {
        final Map<String, Object> anMap = new HashMap<String, Object>();
        if (StringUtils.isNotBlank(anCustType)) {
            if (!anCustType.contains(",")) {

                anMap.put("custType", anCustType);
            } else {
                anMap.put("custType", anCustType.split(","));

            }
        }
        anMap.put("businStatus", "1");
        return this.selectByProperty(anMap);
    }

    /***
     * 根据客户类型查询重要客户对象
     * @param anCustNo
     * @return
     */
    public List<CustMajor> findCustMajorByMap(final Map<String, Object> anMap) {
        anMap.put("businStatus", "1");
        return this.selectByProperty(anMap);
    }

    /**
     * 查询平台企业id
     * @return
     */
    public Long findPlatCustMajor() {

        Map<String, Object> paramMap = QueryTermBuilder.newInstance().put("businStatus", "1").put("custType", "0")
                .build();

        List<CustMajor> list = this.selectByProperty(paramMap);

        if (!Collections3.isEmpty(list)) {

            return Collections3.getFirst(list).getCustNo();

        }

        return null;

    }

}
