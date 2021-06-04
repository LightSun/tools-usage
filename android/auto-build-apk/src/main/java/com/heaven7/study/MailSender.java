package com.heaven7.study;

import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.visitor.FireIndexedVisitor;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.collection.VisitServices;
import com.sun.mail.util.MailSSLSocketFactory;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;

public class MailSender {

    public void send(EmailParams params) throws Exception {
        Properties props = new Properties();
        props.setProperty("mail.debug", "true");
        props.setProperty("mail.transport.protocol", "smtp");   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", "smtp.qq.com");     // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证
        //often need ssl
        if (params.isEnableSsl()) {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.socketFactory", sf);
        }

        // 1. 创建一封邮件
        Session session = Session.getInstance(props);        // 根据参数配置，创建会话对象（为了发送邮件准备的）
        MimeMessage message = new MimeMessage(session);     // 创建邮件对象

        //body
        Multipart multipart = new MimeMultipart();
        BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(params.getBody_text());

        multipart.addBodyPart(bodyPart);

        // 附件部分
        if (!Predicates.isEmpty(params.getFiles())) {
            VisitServices.from(params.getFiles()).fire(new FireVisitor<String>() {
                @Override
                public Boolean visit(String filename, Object param) {
                    try {
                        MimeBodyPart part = new MimeBodyPart();
                        part.setDataHandler(new DataHandler(new FileDataSource(filename)));

                        //处理附件名称中文（附带文件路径）乱码问题
                        part.setFileName(MimeUtility.encodeText(filename));
                        multipart.addBodyPart(part);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
        }

        // 2. From: 发件人
        //    其中 InternetAddress 的三个参数分别为: 邮箱, 显示的昵称(只用于显示, 没有特别的要求), 昵称的字符集编码
        //    真正要发送时, 邮箱必须是真实有效的邮箱。
        message.setFrom(new InternetAddress(params.getSender_acc(), "USER_1", "UTF-8"));

        // 3. To: 收件人
        addReceivers(params.getReceivers(), message, MimeMessage.RecipientType.TO, true);
        addReceivers(params.getReceivers_copy(), message, MimeMessage.RecipientType.CC, false);
        addReceivers(params.getReceivers_safe_copy(), message, MimeMessage.RecipientType.BCC, false);

        message.setSubject(params.getSubject(), "UTF-8");

        // 5. Content: 邮件正文（可以使用html标签）
        // message.setContent("这是邮件正文", "text/html;charset=UTF-8");
        message.setContent(multipart);

        // 6. 设置显示的发件时间
        message.setSentDate(new Date());

        // 7. 保存前面的设置
        message.saveChanges();

        // 8. 将该邮件保存到本地
        if(params.getSaveFile() != null){
            File file = new File(params.getSaveFile());
            if(!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            OutputStream out = new FileOutputStream(params.getSaveFile());
            message.writeTo(out);
            out.flush();
            out.close();
        }

        //connect
        Transport transport = session.getTransport();
        transport.connect("smtp.qq.com", "532278976@qq.com", "lwyxlemzxynkbjjc");
        transport.sendMessage(message, message.getAllRecipients());
        // 7. 关闭连接
        transport.close();
    }

    private void addReceivers(List<String> receivers, MimeMessage message, Message.RecipientType to, boolean needSetRecipient) {
        if (Predicates.isEmpty(receivers)) {
            return;
        }
        VisitServices.from(receivers).fireWithIndex(new FireIndexedVisitor<String>() {
            @Override
            public Void visit(Object param, String s, int index, int size) {
                try {
                    if (index != 0 || !needSetRecipient) {
                        message.addRecipient(to, new InternetAddress(s, "USER_1", "UTF-8"));
                    } else {
                        message.setRecipient(to, new InternetAddress(s, "USER_1", "UTF-8"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
}
