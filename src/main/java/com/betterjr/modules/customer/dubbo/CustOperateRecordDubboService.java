package com.betterjr.modules.customer.dubbo;

import com.alibaba.dubbo.config.annotation.Service;
import com.betterjr.modules.customer.ICustOperateRecordService;

/**
 * 操作记录
 * 
 * @author liuwl
 *
 */
@Service(interfaceClass = ICustOperateRecordService.class)
public class CustOperateRecordDubboService implements ICustOperateRecordService {

}
