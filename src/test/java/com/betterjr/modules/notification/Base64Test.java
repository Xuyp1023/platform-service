package com.betterjr.modules.notification;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;

import org.junit.Test;

import com.betterjr.common.utils.Base64Coder;

public class Base64Test {

    @Test
    public void testBase64() {
        String str = "<p>\r\n" + "    <br/>\r\n" + "</p>\r\n" + "<p>\r\n" + "    恭喜您,开户成功<br/>\r\n" + "</p>\r\n" + "<p>\r\n" + "    <br/>\r\n"
                + "</p>\r\n" + "<table>\r\n" + "    <tbody>\r\n" + "        <tr class=\"firstRow\">\r\n"
                + "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\">\r\n" + "                客户编号:\r\n"
                + "            </td>\r\n" + "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\">\r\n"
                + "                ${entity.custNo}\r\n" + "            </td>\r\n" + "        </tr>\r\n" + "        <tr>\r\n"
                + "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\">\r\n" + "                客户名称:\r\n"
                + "            </td>\r\n" + "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\">\r\n"
                + "                ${entity.custName}\r\n" + "            </td>\r\n" + "        </tr>\r\n" + "        <tr>\r\n"
                + "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\">\r\n" + "                电子邮件:\r\n"
                + "            </td>\r\n" + "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\">\r\n"
                + "                ${entity.email}\r\n" + "            </td>\r\n" + "        </tr>\r\n" + "        <tr>\r\n"
                + "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\"></td>\r\n"
                + "            <td width=\"401\" valign=\"top\" style=\"word-break: break-all;\"></td>\r\n" + "        </tr>\r\n" + "    </tbody>\r\n"
                + "</table>\r\n" + "<p>\r\n" + "    <br/>\r\n" + "</p>\r\n" + "<p>\r\n" + "    <br/>\r\n" + "</p>\r\n" + "<p>\r\n"
                + "    发送人:#发送人#\r\n" + "</p>\r\n" + "<p>\r\n" + "    发送机构:#发送机构#\r\n" + "</p>\r\n" + "<p>\r\n" + "    邮件通知\r\n" + "</p>";
        // System.out.println(str);
        try {
            String encodeStr = URLEncoder.encode(str, "UTF-8");
            System.out.println(encodeStr);
            String base64Str = Base64Coder.encodeString(encodeStr);
            System.out.println(base64Str);
            String decodeBase64Str = Base64Coder.decodeString(base64Str);
            System.out.println(decodeBase64Str);
            String decodeStr = URLDecoder.decode(decodeBase64Str, "UTF-8");
            System.out.println(decodeStr);
        }
        catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Content Encoder");
        frame.setLayout(new BorderLayout());
        frame.setSize(1000, 800);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));
        frame.add(panel, BorderLayout.CENTER);

        JTextArea fristTextArea = new JTextArea();
        JScrollPane fristScrollPane = new JScrollPane(fristTextArea);
        fristScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        fristTextArea.setBorder(new TitledBorder("原始数据"));
        fristTextArea.setLineWrap(true);
        panel.add(fristScrollPane);
        JTextArea secondTextArea = new JTextArea();
        JScrollPane secondScrollPane = new JScrollPane(secondTextArea);
        secondScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        secondTextArea.setBorder(new TitledBorder("处理后数据"));
        secondTextArea.setLineWrap(true);
        panel.add(secondScrollPane);

        JPanel toolbarPanel = new JPanel(new FlowLayout());
        frame.add(toolbarPanel, BorderLayout.SOUTH);

        JButton encodeButton = new JButton("编码");
        JButton decodeButton = new JButton("解码");
        JButton exitButton = new JButton("退出");

        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                frame.dispose();
                System.exit(0);
            }
        });

        encodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent anE) {
                try {
                    String orginStr = fristTextArea.getText();
                    String encodeStr = URLEncoder.encode(orginStr, "UTF-8");
                    String base64Str = Base64Coder.encodeString(encodeStr);
                    secondTextArea.setText(base64Str);
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, "发生错误" + e.getMessage());
                }
            }
        });

        decodeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent anE) {
                try {
                    String base64Str = fristTextArea.getText();
                    String decodeBase64Str = Base64Coder.decodeString(base64Str);
                    String decodeStr = URLDecoder.decode(decodeBase64Str, "UTF-8");
                    secondTextArea.setText(decodeStr);
                }
                catch (Exception e) {
                    JOptionPane.showMessageDialog(frame, "发生错误" + e.getMessage());
                }
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent anE) {
                frame.dispose();
                System.exit(0);
            }
        });
        toolbarPanel.add(encodeButton);
        toolbarPanel.add(decodeButton);
        toolbarPanel.add(exitButton);
        frame.setVisible(true);
    }
}
