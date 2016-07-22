package com.betterjr.modules.customer.dubbo;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.modules.customer.ICustOpenAccountService;

/**
 * 开户流水
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustOpenAccountService.class)
public class CustOpenAccountDubboService implements ICustOpenAccountService {
}
