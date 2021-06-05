package com.heaven7.study;

import com.heaven7.java.base.util.*;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class EmailParams {

    public static final String KEY_SENDER_ACC = "sender_acc";
    public static final String KEY_SENDER_PWD = "sender_pwd";
    public static final String KEY_RECEIVERS = "receivers";
    public static final String KEY_RECEIVERS_COPY = "receivers_copy";
    public static final String KEY_RECEIVERS_SAFE_COPY = "receivers_safe_copy";

    public static final String KEY_SUBJECT = "subject";
    public static final String KEY_BODY = "body_text";
    public static final String KEY_FILE_DIR = "file_dir";
    public static final String KEY_FILES = "files";
    public static final String KEY_SAVE_FILE = "save_file";
    public static final String KEY_ENABLE_SSL = "enable_ssl";
    public static final String KEY_SEND_DATE = "send_date";

    public static final String KEY_PROTOCOL = "protocol";
    public static final String KEY_PROTOCOL_HOST = "protocol_host";


    private String sender_acc;//need
    private String sender_pwd;//need
    private List<String> receivers;//need
    private List<String> receivers_copy;
    private List<String> receivers_safe_copy;

    private String subject;   //need
    private String body_text; //need
    private List<String> files;

    private String saveFile;
    private boolean enableSsl = true;

    private long sendDate;
    private String protocol = "smtp";
    private String protocol_host = "smtp.qq.com";

    private final Map<String, String> mExtras = new HashMap<>();

    public void verify(){
        if(TextUtils.isEmpty(sender_acc)){
            throw new RuntimeException("must assign sender by 'sender_acc'");
        }
        if(TextUtils.isEmpty(sender_pwd)){
            throw new RuntimeException("must assign sender pwd by 'sender_pwd'");
        }
        if(Predicates.isEmpty(receivers)){
            throw new RuntimeException("must assign receivers by 'receivers'");
        }
        if(Predicates.isEmpty(subject)){
            throw new RuntimeException("must assign subject by 'subject'");
        }
        if(Predicates.isEmpty(body_text)){
            throw new RuntimeException("must assign body_text by 'body_text'");
        }
    }

    public static EmailParams fromFile(String file) {
        FileReader reader = null;
        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            return null;
        }
        List<String> lines;
        try {
            lines = IOUtils.readStringLines(reader);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(reader);
        }
        final EmailParams obj = new EmailParams();

        VisitServices.from(lines).fire(new FireVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                String[] ks = s.split("=");
                if (ks.length < 2) {
                    System.err.println("wrong line: " + s);
                } else {
                    String key = ks[0];
                    String value = ks[1];
                    switch (key) {
                        case KEY_SENDER_ACC:
                            obj.setSender_acc(value);
                            break;
                        case KEY_SENDER_PWD:
                            obj.setSender_pwd(value);
                            break;
                        case KEY_RECEIVERS:
                            obj.setReceivers(Arrays.asList(value.split(",")));
                            break;
                        case KEY_RECEIVERS_COPY:
                            if(!TextUtils.isEmpty(value)){
                                obj.setReceivers_copy(Arrays.asList(value.split(",")));
                            }
                            break;
                        case KEY_RECEIVERS_SAFE_COPY:
                            if(!TextUtils.isEmpty(value)){
                                obj.setReceivers_safe_copy(Arrays.asList(value.split(",")));
                            }
                            break;
                        case KEY_SUBJECT:
                            obj.setSubject(value);
                            break;
                        case KEY_BODY:
                            obj.setBody_text(value);
                            break;
                        case KEY_FILE_DIR:
                            if(!TextUtils.isEmpty(value)){
                                List<String> exts = new ArrayList<>();
                                String dir;
                                if (value.contains("::")) {
                                    String[] strs = value.split("::");
                                    exts.addAll(Arrays.asList(strs[0].split(",")));
                                    dir = strs[1];
                                } else {
                                    dir = value;
                                }
                                List<String> files = new ArrayList<>();
                                FileUtils.getFiles(new File(dir), new FileFilter() {
                                    @Override
                                    public boolean accept(File pathname) {
                                        if(pathname.isDirectory()){
                                            return false;
                                        }
                                        if (exts.isEmpty()) {
                                            return true;
                                        }
                                        String ext = FileUtils.getFileExtension(pathname);
                                        return ext != null && exts.contains(ext);
                                    }
                                }, files);
                                obj.setFiles(files);
                            }
                            break;
                        case KEY_FILES:
                            if(!TextUtils.isEmpty(value)){
                                obj.setFiles(Arrays.asList(value.split(",")));
                            }
                            break;

                        case KEY_SAVE_FILE:
                            obj.setSaveFile(value);
                            break;
                        case KEY_ENABLE_SSL:
                            if(!TextUtils.isEmpty(value)){
                                obj.setEnableSsl(Boolean.parseBoolean(value));
                            }
                            break;

                        case KEY_SEND_DATE:
                            if (value.contains("'")) {
                                int s1 = value.indexOf("'");
                                int s2 = value.lastIndexOf("'");
                                String fmt = value.substring(s1, s2 + 1);
                                try {
                                    obj.setSendDate(new SimpleDateFormat(fmt).parse(value.substring(s2 + 1)).getTime());
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;

                        case KEY_PROTOCOL:
                            if(!TextUtils.isEmpty(value)){
                                obj.setProtocol(value);
                            }
                            break;

                        case KEY_PROTOCOL_HOST:
                            if(!TextUtils.isEmpty(value)){
                                obj.setProtocol_host(value);
                            }
                            break;

                        default:
                            obj.getExtras().put(key, value);
                    }
                }
                return null;
            }
        });
        return obj;
    }

    public Map<String, String> getExtras() {
        return mExtras;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getProtocol_host() {
        return protocol_host;
    }

    public void setProtocol_host(String protocol_host) {
        this.protocol_host = protocol_host;
    }

    public long getSendDate() {
        return sendDate;
    }

    public void setSendDate(long sendDate) {
        this.sendDate = sendDate;
    }

    public boolean isEnableSsl() {
        return enableSsl;
    }

    public void setEnableSsl(boolean enableSsl) {
        this.enableSsl = enableSsl;
    }

    public String getSaveFile() {
        return saveFile;
    }

    public void setSaveFile(String saveFile) {
        this.saveFile = saveFile;
    }

    public String getSender_acc() {
        return sender_acc;
    }

    public void setSender_acc(String sender_acc) {
        this.sender_acc = sender_acc;
    }

    public String getSender_pwd() {
        return sender_pwd;
    }

    public void setSender_pwd(String sender_pwd) {
        this.sender_pwd = sender_pwd;
    }

    public List<String> getReceivers() {
        return receivers;
    }

    public void setReceivers(List<String> receivers) {
        this.receivers = receivers;
    }

    public List<String> getReceivers_copy() {
        return receivers_copy;
    }

    public void setReceivers_copy(List<String> receivers_copy) {
        this.receivers_copy = receivers_copy;
    }

    public List<String> getReceivers_safe_copy() {
        return receivers_safe_copy;
    }

    public void setReceivers_safe_copy(List<String> receivers_safe_copy) {
        this.receivers_safe_copy = receivers_safe_copy;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody_text() {
        return body_text;
    }

    public void setBody_text(String body_text) {
        this.body_text = body_text;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}
