package com.betterjr.modules.notification;

import org.junit.Test;

import com.betterjr.common.utils.MailUtils;

public class MailTest {

    @Test
    public void testSendMail() {
        String content = "<p>\r\n" + 
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
                "                100\r\n" + 
                "            </td>\r\n" + 
                "        </tr>\r\n" + 
                "        <tr>\r\n" + 
                "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\">\r\n" + 
                "                客户名称:\r\n" + 
                "            </td>\r\n" + 
                "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\">\r\n" + 
                "                亿起融金融服务有限责任公司\r\n" + 
                "            </td>\r\n" + 
                "        </tr>\r\n" + 
                "        <tr>\r\n" + 
                "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\">\r\n" + 
                "                电子邮件:\r\n" + 
                "            </td>\r\n" + 
                "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\">\r\n" + 
                "                vanlin@163.com\r\n" + 
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
                "    发送人:管理员-平台\r\n" + 
                "</p>\r\n" + 
                "<p>\r\n" + 
                "    发送机构:深圳市前海拜特互联网金融服务有限公司\r\n" + 
                "</p>\r\n" + 
                "<p>\r\n" + 
                "    站内消息\r\n" + 
                "</p>";
        MailUtils.sendMail("test@mail.com", "测试测试", content, null);
    }
}
