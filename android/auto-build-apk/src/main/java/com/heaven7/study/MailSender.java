package com.heaven7.study;

import com.heaven7.java.base.util.FileUtils;
import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.visitor.FireIndexedVisitor;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.MapFireVisitor;
import com.heaven7.java.visitor.collection.KeyValuePair;
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

public final class MailSender {

    //可以动态指定 发送的文件和或者文件夹
    public static void main(String[] args) {
        //java -jar MailSender-1.0-SNAPSHOT-capsule.jar mail_config.properties
        if(args.length == 0){
            args = new String[]{
                "F:\\test\\emailSender\\mail_config.properties",
                "prefix=22",
            };

        }
        final EmailParams params = EmailParams.fromFile(args[0]);
        if(params != null){
            //override parameter
            if(args.length > 1){
                VisitServices.from(args).fireWithIndex(new FireIndexedVisitor<String>() {
                    @Override
                    public Void visit(Object param, String s, int index, int size) {
                        //0 is config file
                        if(index > 0){
                            params.setIfNeed(s);
                        }
                        return null;
                    }
                });
            }
            //do post
            params.precessPostTasks();
            try {
                if (!Predicates.isEmpty(params.getFiles())) {
                    //if send file separate
                    if(params.isSend_file_separate()){
                        VisitServices.from(params.getFiles()).fire(new FireVisitor<String>() {
                            @Override
                            public Boolean visit(String s, Object param) {
                                try {
                                    new MailSender().send(params, s);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return null;
                            }
                        });
                    }else{
                        new MailSender().send(params, null);
                    }
                }else{
                    new MailSender().send(params, null);
                }
            } catch (Exception e) {
                throw new RuntimeException("send email failed", e);
            }finally {
                if (!Predicates.isEmpty(params.getFiles())) {
                    VisitServices.from(params.getFiles()).fire(new FireVisitor<String>() {
                        @Override
                        public Boolean visit(String s, Object param) {
                            if (params.isCompress() && s.endsWith(".zip")) {
                                new File(s).delete();
                            }
                            return null;
                        }

                        ;
                    });
                }
            }
        }else {
            throw new RuntimeException("load mail config file failed from " + args[0]);
        }
    }

    //HBIGIOKVOWRHVHML 163
    //file1 null: means all files
    public void send(EmailParams params, String file1) throws Exception {
        params.verify();
        final Properties props = new Properties();
        //props.setProperty("mail.debug", "true");
        props.setProperty("mail.transport.protocol", params.getProtocol());   // 使用的协议（JavaMail规范要求）
        props.setProperty("mail.smtp.host", params.getProtocol_host());     // 发件人的邮箱的 SMTP 服务器地址
        props.setProperty("mail.smtp.auth", "true");            // 需要请求认证

        if(params.getProtocol_host().contains(".163.")){
            props.setProperty("mail.smtp.starttls.enable", "true");
            props.setProperty("mail.imap.socketFactory.fallback", "false");
        }
        //often need ssl
        if (params.isEnableSsl()) {
            MailSSLSocketFactory sf = new MailSSLSocketFactory();
            sf.setTrustAllHosts(true);
            props.put("mail.smtp.ssl.enable", "true");
            props.put("mail.smtp.ssl.socketFactory", sf);
        }
        if(!params.getExtras().isEmpty()){
            VisitServices.from(params.getExtras()).fire(new MapFireVisitor<String, String>() {
                @Override
                public Boolean visit(KeyValuePair<String, String> pair, Object param) {
                    props.put(pair.getKey(), pair.getValue());
                    return null;
                }
            });
        }

        // 1. 创建一封邮件
        Session session;
            session = Session.getInstance(props);        // 根据参数配置，创建会话对象（为了发送邮件准备的）

        MimeMessage message = new MimeMessage(session);      // 创建邮件对象

        //body
        Multipart multipart = new MimeMultipart();
        BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(params.getBody_text());

        multipart.addBodyPart(bodyPart);

        // 附件部分
        if(TextUtils.isEmpty(file1)){
            if (!Predicates.isEmpty(params.getFiles())) {
                VisitServices.from(params.getFiles()).fire(new FireVisitor<String>() {
                    @Override
                    public Boolean visit(String filename, Object param) {
                        System.out.println("start add file: " + filename);
                        try {
                            MimeBodyPart part = new MimeBodyPart();
                            part.setDataHandler(new DataHandler(new FileDataSource(filename)));

                            //处理附件名称中文（附带文件路径）乱码问题
                            String name = MimeUtility.encodeText(FileUtils.getSimpleFileName(filename));
                            part.setFileName(name);
                            multipart.addBodyPart(part);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
            }
        }else{
            try {
                MimeBodyPart part = new MimeBodyPart();
                part.setDataHandler(new DataHandler(new FileDataSource(file1)));

                //处理附件名称中文（附带文件路径）乱码问题
                String name = MimeUtility.encodeText(FileUtils.getSimpleFileName(file1));
                part.setFileName(name);
                multipart.addBodyPart(part);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 2. From: 发件人
        //    其中 InternetAddress 的三个参数分别为: 邮箱, 显示的昵称(只用于显示, 没有特别的要求), 昵称的字符集编码
        //    真正要发送时, 邮箱必须是真实有效的邮箱。
        message.setFrom(new InternetAddress(params.getSender_acc(), params.getSender_name(), "UTF-8"));

        // 3. 收件人，抄送，密送
        addReceivers(params.getReceivers(), message, MimeMessage.RecipientType.TO, true);
        addReceivers(params.getReceivers_copy(), message, MimeMessage.RecipientType.CC, false);
        addReceivers(params.getReceivers_safe_copy(), message, MimeMessage.RecipientType.BCC, false);

        message.setSubject(params.getSubject(), "UTF-8");

        // 5. Content: 邮件正文（可以使用html标签）
        // message.setContent("这是邮件正文", "text/html;charset=UTF-8");
        message.setContent(multipart);

        // 6. 设置发件时间
        if(params.getSendDate() > 0){
            message.setSentDate(new Date(params.getSendDate()));
        }else {
            message.setSentDate(new Date());
        }

        // 7. 保存前面的设置
        message.saveChanges();

        // 8. 将该邮件保存到本地
        if(!TextUtils.isEmpty(params.getSaveFile())){
            File file = new File(params.getSaveFile());
            if(file.getParentFile() != null && !file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }
            OutputStream out = new FileOutputStream(params.getSaveFile());
            message.writeTo(out);
            out.flush();
            out.close();
        }

        //connect
        Transport transport = session.getTransport();
        transport.connect(params.getProtocol_host(), params.getSender_acc(), params.getSender_pwd());
        transport.sendMessage(message, message.getAllRecipients());
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
