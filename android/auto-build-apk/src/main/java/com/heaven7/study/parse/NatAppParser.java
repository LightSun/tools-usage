package com.heaven7.study.parse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.heaven7.java.base.util.Predicates;
import com.heaven7.java.base.util.TextReadHelper;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.PredicateVisitor;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.study.CmdBuilder;
import com.heaven7.study.CmdHelper;
import com.heaven7.study.parse.bean.LoginRes;
import com.heaven7.study.utils.OkHttpUtils;
import okhttp3.*;

import java.util.List;

public final class NatAppParser {

    private final String mReqDomain;
    private final String natAppPath;
    private final String path;
    private final String typeSuffix;

    public NatAppParser(String domain, String natAppPath, String path, String typeSuffix) {
        this.mReqDomain = domain;
        this.natAppPath = natAppPath;
        this.path = path;
        this.typeSuffix = typeSuffix;
    }

    public static void main(String[] args) {
        String natAppPath;
        String logPath;
        String typeSuffix;
        String domain = "http://scrap-identify.stable-dev.bdfint.cn/";
        if(args.length < 3){
            natAppPath = "";
            logPath = "D:\\study\\github\\mine2\\tools-usage\\android\\auto-build-apk\\config\\log.txt";
            typeSuffix = "-api-server";
        }else{
            natAppPath = args[0];
            logPath = args[1];
            typeSuffix = args[2];
            if(args.length >= 4){
                domain = args[3];
            }
        }
        new NatAppParser(domain, natAppPath, logPath, typeSuffix).startNatApp();
    }

    public void startNatApp(){
        if(!Predicates.isEmpty(natAppPath)){
            CmdBuilder cmdBuilder = new CmdBuilder()
                    .str("start")
                    .str(natAppPath);

            CmdHelper cmd = new CmdHelper(cmdBuilder.toCmd());
            System.out.println(" >>> start execute cmd: " + cmd.getCmdActually());
            if(!cmd.execute(new CmdHelper.InhertIoCallback())){
                System.err.println(">>> execute failed.");
            }else{
                System.err.println(">>> execute success.");
            }
        }
        parse();
    }

    public void parse() {
        String str = "Tunnel established at";
        TextReadHelper<String> helper = new TextReadHelper<>(new ReaderCallback0());
        List<String> list = helper.read(null, path);
        List<String> lines = VisitServices.from(list).filter(new PredicateVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                return s.contains(str);
            }
        }).getAsList();
        System.out.println(lines);
        //only use last line
        if(!Predicates.isEmpty(lines)){
            String s = lines.get(lines.size() - 1);
            String subStr = s.substring(s.indexOf(str) + str.length() + 1).trim();
            String[] strs = subStr.split(":");
            //tcp://server.natappfree.cc:36851
            callServer(strs[0], strs[1].substring(2), strs[2]);
        }
    }
    //like tcp, server.natappfree.cc, 36851
    private void callServer(String protocol, String domain, String port) {
        final String url = mReqDomain + "api/sys/QR/addScrapQr";
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
        String url = mReqDomain + "api/auth/login";
        JsonObject je = new JsonObject();
        je.addProperty("userType", "0");
        je.addProperty("clientKey", "identify-sys-admin");
        je.addProperty("loginType", "0");
        //je.addProperty("loginName", "QJOne");
        je.addProperty("loginName", "QJTwo");
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
