package com.betterjr.modules.customer.service;

import com.betterjr.common.config.ConfigServiceFace;
import com.betterjr.common.config.ParamNames;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.common.utils.reflection.ReflectionUtils;
import com.betterjr.modules.customer.dao.PlatformAgencyInfoMapper;
import com.betterjr.modules.customer.entity.PlatformAgencyInfo;

import java.util.*;

import org.springframework.stereotype.Service;

@Service
public class PlatformAgencyService extends BaseService<PlatformAgencyInfoMapper, PlatformAgencyInfo> {

    private static Map<String, PlatformAgencyInfo> saleMap = null;

    public List<PlatformAgencyInfo> findAll() {
        List<PlatformAgencyInfo> saleList = this.selectAll();
        saleMap = ReflectionUtils.listConvertToMap(saleList, "saleAgencyNo");
        for (PlatformAgencyInfo taInfo : saleList) {
            if (BetterStringUtils.isNotBlank(taInfo.getRelaCustNo())) {
                saleMap.put(taInfo.getRelaCustNo(), taInfo);
            }
        }
        return saleList;
    }

    public String getMyServiceName() {

        return ParamNames.AGENCY;
    }

    /**
     * 根据合作机构代码或客户编号，获取合作机构信息
     * @param anSaleAgencyNo
     * @return
     */
    public PlatformAgencyInfo findSaleAgency(String anSaleAgencyNo) {
        PlatformAgencyInfo agencyInfo = null;
        if (BetterStringUtils.isNotBlank(anSaleAgencyNo)){
            agencyInfo = Collections3.getFirst(selectByProperty("relaCustNo", anSaleAgencyNo));
            if (agencyInfo == null){
               agencyInfo = Collections3.getFirst(selectByProperty("saleAgencyNo", anSaleAgencyNo));
            }
        }
        
        return agencyInfo;
    }

    public List<PlatformAgencyInfo> findWorkSaleAgencyList() {
        List<PlatformAgencyInfo> saleList = new ArrayList<PlatformAgencyInfo>();
        saleList.addAll(saleMap.values());

        return saleList;
    }

    /**
     * 获取需要接口调用
     * 
     * @param businFlag
     * @return
     */
    public List<PlatformAgencyInfo> findSaleInfoListByBusinFlag(String businFlag) {
        List<PlatformAgencyInfo> saleList = new ArrayList<PlatformAgencyInfo>();
        Collection<PlatformAgencyInfo> list = saleMap.values();
        for (PlatformAgencyInfo taInfo : list) {
            if (businFlag.equals(taInfo.getBusinFlag())) {
                saleList.add(taInfo);
            }
        }
        return saleList;
    }
}
