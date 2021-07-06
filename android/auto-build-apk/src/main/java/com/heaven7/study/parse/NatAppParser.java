package com.heaven7.study.parse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.heaven7.java.base.util.TextReadHelper;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.study.parse.bean.LoginRes;
import com.heaven7.study.utils.OkHttpUtils;
import okhttp3.*;

import java.util.List;

public final class NatAppParser {

    private final String path;
    private final String typeSuffix;

    public NatAppParser(String path, String typeSuffix) {
        this.path = path;
        this.typeSuffix = typeSuffix;
    }

    public static void main(String[] args) {
        String logPath;
        String typeSuffix;
        if(args.length < 2){
            logPath = "D:\\study\\github\\mine2\\tools-usage\\android\\auto-build-apk\\config\\log.txt";
            typeSuffix = "-camera-server";
        }else{
            logPath = args[0];
            typeSuffix = args[1];
        }
        new NatAppParser(logPath, typeSuffix).parse();
    }

    public void parse() {
        TextReadHelper<String> helper = new TextReadHelper<>(new ReaderCallback0());
        List<String> list = helper.read(null, path);
        VisitServices.from(list).fire(new FireVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                String str = "Tunnel established at";
                if (s.contains(str)) {
                    String subStr = s.substring(s.indexOf(str) + str.length() + 1).trim();
                    String[] strs = subStr.split(":");
                    //tcp://server.natappfree.cc:36851
                    callServer(strs[0], strs[1].substring(2), strs[2]);
                }
                return null;
            }
        });
    }
    //like tcp, server.natappfree.cc, 36851
    private void callServer(String protocol, String domain, String port) {
        final String url = "http://scrap-identify.stable-dev.bdfint.cn/api/sys/QR/addScrapQr";
        login(new OkHttpUtils.StringCallback() {
            public void call(String token) {
                JsonObject je = new JsonObject();
                je.addProperty("scrapQrHost", port);
                je.addProperty("scrapQrUrl", domain);
                je.addProperty("scrapQrType", protocol + typeSuffix);
                OkHttpUtils.post(url, je.toString(), new OkHttpUtils.HeaderCallback() {
                    @Override
                    public void addHeader(Request.Builder builder) {
                        builder.addHeader("Authorization", token)
                                .addHeader("content-type", "application/json");
                    }
                }, new OkHttpUtils.StringCallback() {
                    @Override
                    public void call(String res) {
                        System.out.println("add port to server result: \r\n" + res);
                    }
                });
            }
        });
    }

    private void login(OkHttpUtils.StringCallback next) {
        String url = "http://scrap-identify.stable-dev.bdfint.cn/api/auth/login";
        JsonObject je = new JsonObject();
        je.addProperty("userType", "0");
        je.addProperty("clientKey", "identify-sys-admin");
        je.addProperty("loginType", "0");
        je.addProperty("loginName", "QJOne");
        je.addProperty("password", "Aa123456");
        OkHttpUtils.post(url, je.toString(), null, new OkHttpUtils.StringCallback() {
            @Override
            public void call(String res) {
                LoginRes loginRes = new Gson().fromJson(res, LoginRes.class);
                if (loginRes.getCode() == 0) {
                    //success
                    next.call(loginRes.getData().getTokenPrefix() + loginRes.getData().getToken());
                } else {
                    System.err.println("login failed: " + loginRes);
                }
            }
        });
    }

    private static class ReaderCallback0 extends TextReadHelper.Callback<String> {
        @Override
        public String parse(String line) {
            return line;
        }
    }
}
