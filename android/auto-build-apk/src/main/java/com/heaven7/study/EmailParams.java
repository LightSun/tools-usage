package com.heaven7.study;

import com.heaven7.java.base.util.FileUtils;
import com.heaven7.java.base.util.IOUtils;
import com.heaven7.java.base.util.Throwables;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private String sender_acc;
    private String sender_pwd;
    private List<String> receivers;
    private List<String> receivers_copy;
    private List<String> receivers_safe_copy;

    private String subject;
    private String body_text;
    private List<String> files;

    private String saveFile;
    private boolean enableSsl = true;

    public static EmailParams fromProperties(String file) {
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
                            obj.setReceivers_copy(Arrays.asList(value.split(",")));
                            break;
                        case KEY_RECEIVERS_SAFE_COPY:
                            obj.setReceivers_safe_copy(Arrays.asList(value.split(",")));
                            break;
                        case KEY_SUBJECT:
                            obj.setSubject(value);
                            break;
                        case KEY_BODY:
                            obj.setBody_text(value);
                            break;
                        case KEY_FILE_DIR:
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
                                    if (exts.isEmpty()) {
                                        return true;
                                    }
                                    String ext = FileUtils.getFileExtension(pathname);
                                    return ext != null && exts.contains(ext);
                                }
                            }, files);
                            obj.setFiles(files);
                            break;
                        case KEY_FILES:
                            obj.setFiles(Arrays.asList(value.split(",")));
                            break;

                        case KEY_SAVE_FILE:
                            obj.setSaveFile(value);
                            break;
                        case KEY_ENABLE_SSL:
                            obj.setEnableSsl(Boolean.parseBoolean(value));
                            break;
                    }
                }
                return null;
            }
        });
        return obj;
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
