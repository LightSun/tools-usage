package com.heaven7.study;

import com.sun.mail.util.MailSSLSocketFactory;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.*;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Properties;

public class SendEmailTest {

    public static void main(String[] args) throws Exception{
        //string from-acc frpm pwd.

        Properties props = new Properties();                    // 参数配置
        props.setProperty("mail.debug", "true");
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", "smtp.qq.com");     // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证

        MailSSLSocketFactory sf = new MailSSLSocketFactory();
        sf.setTrustAllHosts(true);
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.ssl.socketFactory", sf);

        // 1. 创建一封邮件
       // Properties props = new Properties();                // 用于连接邮件服务器的参数配置（发送邮件时才需要用到）
        Session session= Session.getInstance(props);        // 根据参数配置，创建会话对象（为了发送邮件准备的）
        MimeMessage message = new MimeMessage(session);     // 创建邮件对象

        //body
        Multipart multipart = new MimeMultipart();
        BodyPart messageBodyPart = new MimeBodyPart();
        messageBodyPart.setText("body text");

        multipart.addBodyPart(messageBodyPart);

        // 附件部分
        messageBodyPart = new MimeBodyPart();
        String filename = "E:\\feigang\\2021-04-16__18-25-57.jpg";
        DataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));

        //messageBodyPart.setFileName(filename);
        //处理附件名称中文（附带文件路径）乱码问题
        messageBodyPart.setFileName(MimeUtility.encodeText(filename));
        multipart.addBodyPart(messageBodyPart);

        /*
         * 也可以根据已有的eml邮件文件创建 MimeMessage 对象
         * MimeMessage message = new MimeMessage(session, new FileInputStream("myEmail.eml"));
         */

        // 2. From: 发件人
        //    其中 InternetAddress 的三个参数分别为: 邮箱, 显示的昵称(只用于显示, 没有特别的要求), 昵称的字符集编码
        //    真正要发送时, 邮箱必须是真实有效的邮箱。
        message.setFrom(new InternetAddress("532278976@qq.com", "USER_heaven7", "UTF-8"));

        // 3. To: 收件人
        message.setRecipient(MimeMessage.RecipientType.TO, new InternetAddress("978136772@qq.com", "USER_CC", "UTF-8"));
        //    To: 增加收件人（可选）
        message.addRecipient(MimeMessage.RecipientType.TO, new InternetAddress("421201093@qq.com", "USER_chuan_zong", "UTF-8"));
        //    Cc: 抄送（可选）
        message.setRecipient(MimeMessage.RecipientType.CC, new InternetAddress("morancheng@yeah.net", "USER_EE", "UTF-8"));
        //    Bcc: 密送（可选）
       // message.setRecipient(MimeMessage.RecipientType.BCC, new InternetAddress("ff@receive.com", "USER_FF", "UTF-8"));

        // 4. Subject: 邮件主题
        message.setSubject("邮件主题", "UTF-8");

        // 5. Content: 邮件正文（可以使用html标签）
       // message.setContent("这是邮件正文", "text/html;charset=UTF-8");
        message.setContent(multipart);

        // 6. 设置显示的发件时间
        message.setSentDate(new Date());

        // 7. 保存前面的设置
        message.saveChanges();

        // 8. 将该邮件保存到本地
        OutputStream out = new FileOutputStream("test.eml");
        message.writeTo(out);
        out.flush();
        out.close();

        //connect
        Transport transport = session.getTransport();
        transport.connect("smtp.qq.com", "532278976@qq.com", "lwyxlemzxynkbjjc");
        transport.sendMessage(message, message.getAllRecipients());
        // 7. 关闭连接
        transport.close();
    }
}
