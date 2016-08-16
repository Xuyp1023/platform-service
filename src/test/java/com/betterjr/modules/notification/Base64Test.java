package com.betterjr.modules.notification;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Base64.Encoder;

import org.junit.Test;

import com.betterjr.common.utils.Base64Coder;

public class Base64Test {

    @Test
    public void testBase64() {
        String str = "<p>\r\n" + 
                "    <br/>\r\n" + 
                "</p>\r\n" + 
                "<p>\r\n" + 
                "    恭喜您,开户成功<br/>\r\n" + 
                "</p>\r\n" + 
                "<p>\r\n" + 
                "    <br/>\r\n" + 
                "</p>\r\n" + 
                "<table>\r\n" + 
                "    <tbody>\r\n" + 
                "        <tr class=\"firstRow\">\r\n" + 
                "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\">\r\n" + 
                "                客户编号:\r\n" + 
                "            </td>\r\n" + 
                "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\">\r\n" + 
                "                ${entity.custNo}\r\n" + 
                "            </td>\r\n" + 
                "        </tr>\r\n" + 
                "        <tr>\r\n" + 
                "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\">\r\n" + 
                "                客户名称:\r\n" + 
                "            </td>\r\n" + 
                "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\">\r\n" + 
                "                ${entity.custName}\r\n" + 
                "            </td>\r\n" + 
                "        </tr>\r\n" + 
                "        <tr>\r\n" + 
                "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\">\r\n" + 
                "                电子邮件:\r\n" + 
                "            </td>\r\n" + 
                "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\">\r\n" + 
                "                ${entity.email}\r\n" + 
                "            </td>\r\n" + 
                "        </tr>\r\n" + 
                "        <tr>\r\n" + 
                "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\"></td>\r\n" + 
                "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\"></td>\r\n" + 
                "        </tr>\r\n" + 
                "    </tbody>\r\n" + 
                "</table>\r\n" + 
                "<p>\r\n" + 
                "    <br/>\r\n" + 
                "</p>\r\n" + 
                "<p>\r\n" + 
                "    <br/>\r\n" + 
                "</p>\r\n" + 
                "<p>\r\n" + 
                "    发送人:#发送人#\r\n" + 
                "</p>\r\n" + 
                "<p>\r\n" + 
                "    发送机构:#发送机构#\r\n" + 
                "</p>\r\n" + 
                "<p>\r\n" + 
                "    邮件通知\r\n" + 
                "</p>";
        //System.out.println(str);
        try {
            String encodeStr = URLEncoder.encode(str, "UTF-8");
            System.out.println(encodeStr);
            String base64Str = Base64Coder.encodeString(encodeStr);
            System.out.println(base64Str);
            String decodeBase64Str = Base64Coder.decodeString(base64Str);
            System.out.println(decodeBase64Str);
            String decodeStr = URLDecoder.decode(decodeBase64Str,"UTF-8");
            System.out.println(decodeStr);
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
