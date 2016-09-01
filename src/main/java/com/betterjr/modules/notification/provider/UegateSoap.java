package com.betterjr.modules.notification.provider;
/**
  * 本示例使用Axis1.4，项目需要引用的包为axis.jar,commons-discovery-0.2.jar,
  * jaxrpc.jar,wsdl4j-1.5.1.jar,mail.jar,commons-logging-1.0.4.jar
  * 6个包缺一不可。
  * 优易网关soap接口地址：http://inter.smswang.net:7801/sms?wsdl
  */

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UegateSoap {
    protected final Logger logger = LoggerFactory.getLogger(UegateSoap.class);

    // 短信提交
    public String submit(String spID, String password, String accessCode, String content, String mobileString) {
        String result = "";
        try {
            // soap接口地址，不能加后面的?wsdl
            String endpoint = "http://inter.smswang.net:7801/sms";
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(endpoint);
            call.setOperationName("Submit");
            call.addParameter("spID", org.apache.axis.encoding.XMLType.XSD_DATE, javax.xml.rpc.ParameterMode.IN);
            call.addParameter("password", org.apache.axis.encoding.XMLType.XSD_DATE, javax.xml.rpc.ParameterMode.IN);
            call.addParameter("accessCode", org.apache.axis.encoding.XMLType.XSD_DATE, javax.xml.rpc.ParameterMode.IN);
            call.addParameter("content", org.apache.axis.encoding.XMLType.XSD_DATE, javax.xml.rpc.ParameterMode.IN);
            call.addParameter("mobileString", org.apache.axis.encoding.XMLType.XSD_DATE, javax.xml.rpc.ParameterMode.IN);
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
            call.setUseSOAPAction(true);
            call.setSOAPActionURI("http://schemas.microsoft.com/clr/nsassem/com.softwee.smgw.soaps.Soap57Provider/soaps#Submit");
            result = (String) call.invoke("Submit", new Object[] { spID, password, accessCode, content, mobileString });
        }
        catch (Exception e) {
            logger.error("短信发送错误！", e);
        }
        return result;
    }

    // 查询余额
    public String queryMo(String spID, String password) {
        String result = "";
        try {
            // soap接口地址，不能加后面的?wsdl
            String endpoint = "http://inter.smswang.net:7801/sms";
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(endpoint);
            call.setOperationName("QueryMo");
            call.addParameter("spID", org.apache.axis.encoding.XMLType.XSD_DATE, javax.xml.rpc.ParameterMode.IN);
            call.addParameter("password", org.apache.axis.encoding.XMLType.XSD_DATE, javax.xml.rpc.ParameterMode.IN);
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
            call.setUseSOAPAction(true);
            call.setSOAPActionURI("http://schemas.microsoft.com/clr/nsassem/com.softwee.smgw.soaps.Soap57Provider/soaps#QueryMo");
            result = (String) call.invoke("QueryMo", new Object[] { spID, password });
        }
        catch (Exception e) {
            logger.error("查询余额错误！", e);
        }
        return result;
    }

    // 状态报告
    public String queryReport(String spID, String password) {
        String result = "";
        try {
            // soap接口地址，不能加后面的?wsdl
            String endpoint = "http://inter.smswang.net:7801/sms";
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(endpoint);
            call.setOperationName("Submit");
            call.addParameter("spID", org.apache.axis.encoding.XMLType.XSD_DATE, javax.xml.rpc.ParameterMode.IN);
            call.addParameter("password", org.apache.axis.encoding.XMLType.XSD_DATE, javax.xml.rpc.ParameterMode.IN);
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
            call.setUseSOAPAction(true);
            call.setSOAPActionURI("http://schemas.microsoft.com/clr/nsassem/com.softwee.smgw.soaps.Soap57Provider/soaps#QueryReport");
            result = (String) call.invoke("QueryReport", new Object[] { spID, password });
        }
        catch (Exception e) {
            logger.error("查询发送报告错误！", e);
        }
        return result;
    }

    // 短信回复
    public String retrieveAll(String spID, String password) {
        String result = "";
        try {
            // soap接口地址，不能加后面的?wsdl
            String endpoint = "http://inter.smswang.net:7801/sms";
            Service service = new Service();
            Call call = (Call) service.createCall();
            call.setTargetEndpointAddress(endpoint);
            call.setOperationName("RetrieveAll");
            call.addParameter("spID", org.apache.axis.encoding.XMLType.XSD_DATE, javax.xml.rpc.ParameterMode.IN);
            call.addParameter("password", org.apache.axis.encoding.XMLType.XSD_DATE, javax.xml.rpc.ParameterMode.IN);
            call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);
            call.setUseSOAPAction(true);
            call.setSOAPActionURI("http://schemas.microsoft.com/clr/nsassem/com.softwee.smgw.soaps.Soap57Provider/soaps#RetrieveAll");
            result = (String) call.invoke("RetrieveAll", new Object[] { spID, password });
        }
        catch (Exception e) {
            logger.error("查询回复短信错误！", e);
        }

        return result;
    }
}