package com.betterjr.modules.notification;

import org.junit.Test;

import com.betterjr.modules.notification.provider.UegateSoap;

public class SmsTest {
    /**
     * 调用类的方法说明：
     * Submit(spID, password, accessCode, content, mobileString)： 
     *      发送短信，5个参数，必须按照顺序调用
     * QueryMo(spID, password) 余额以人民币厘单位计算，
     *      除以1000为剩余人民币元单位，除以按厘计算的短信价
     *      格（比如80）为剩余短信条数。
     * QueryReport(spID, password)：
     *      状态报告查询一次后不再显示。
     * RetrieveAll(spID, password)：
     *      短信回复调用一次后不再显示。
     *      账号：000401
密码：BytterA@2001X
接入码：1069032239089391
     */
    @Test
    public void testSms() {
    //以下信息，根据自己的账户信息修改
    String spID = "000401";
    String password = "BytterA@2001X";
    String accessCode = "1069032239089391";
    //发送的短信内容和目标手机号码,多个手机号码以英文逗号隔开。
    String content = "尊敬的客户，您提交的开户申请（深圳市锐拓显示技术有限公司）审核通过。【企e金服】";
    String mobileString = "13808060501,18696990518";
    //短信提交
    UegateSoap uegatesoap = new  UegateSoap();
    //String submitresult=uegatesoap.Submit(spID, password, accessCode, content, mobileString);
    //System.out.println(submitresult);
    
    //查询余额
    String querymoresult=uegatesoap.queryMo(spID, password);
    System.out.println(querymoresult);
    
    //状态报告
    String reportresult=uegatesoap.queryReport(spID, password);
    System.out.println(reportresult);
    
    //短信回复
    String receiveresult=uegatesoap.retrieveAll(spID, password);
    System.out.println(receiveresult);
    }
}
