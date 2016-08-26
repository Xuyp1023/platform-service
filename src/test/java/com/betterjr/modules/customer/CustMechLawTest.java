package com.betterjr.modules.customer;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.betterjr.modules.BasicServiceTest;
import com.betterjr.modules.customer.entity.CustMechLawTmp;
import com.betterjr.modules.customer.service.CustMechLawTmpService;

public class CustMechLawTest extends BasicServiceTest<CustMechLawTmpService> {

    @Test
    public void test() {
        CustMechLawTmpService lawTmpService = this.getServiceObject();
        
        
        assertNotNull("不能为空!", lawTmpService);
        
        CustMechLawTmp lawTmp = new CustMechLawTmp();
        lawTmp.setRefId(100000177L);
        lawTmpService.addCustMechLawTmp(lawTmp, null, "1");
    }

    @Override
    public Class<CustMechLawTmpService> getTargetServiceClass() {
        return CustMechLawTmpService.class;
    }

}
