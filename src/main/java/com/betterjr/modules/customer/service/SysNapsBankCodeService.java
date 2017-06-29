package com.betterjr.modules.customer.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.betterjr.common.exception.BytterTradeException;
import com.betterjr.common.service.BaseService;
import com.betterjr.common.utils.BetterStringUtils;
import com.betterjr.common.utils.Collections3;
import com.betterjr.mapper.pagehelper.Page;
import com.betterjr.modules.customer.dao.SysNapsBankCodeMapper;
import com.betterjr.modules.customer.entity.SysNapsBankCode;

/****
 * 查询银行关联的联行号信息
 * @author hubl
 *
 */
@Service
public class SysNapsBankCodeService extends BaseService<SysNapsBankCodeMapper, SysNapsBankCode> {

    public Page<SysNapsBankCode> findSysBankCodeList(Map<String, Object> anParam){
        Map<String,Object> anMap=new HashMap<String, Object>();
        if(BetterStringUtils.isNotBlank((String)anParam.get("bankName"))){
            anMap.put("LIKEorgFullName", "%" +anParam.get("bankName")+ "%");
        }
        return this.selectPropertyByPage(anMap, 1, 10, false);
    }
    
    /***
     * 银行全称是否一致
     * @param anPaySysNum
     * @param anBankName
     */
    public void checkBankCode(String anPaySysNum,String anBankName){
        SysNapsBankCode bankCode=this.selectByPrimaryKey(anPaySysNum);
        if(bankCode!=null){
            if(!BetterStringUtils.equalsIgnoreCase(anBankName, bankCode.getOrgFullName())){
                throw new BytterTradeException("所填银行全称不存在"); 
            }
        }else{
            throw new BytterTradeException("请选择银行全称");
        }
    }
    
    public SysNapsBankCode findSysBankCodeInfoByBankName(String anBankName){
        List<SysNapsBankCode> codeList=this.selectByProperty("orgFullName", anBankName);
        return Collections3.getFirst(codeList);
    }
    
}
