package com.heaven7.study;

import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.java.visitor.util.Map;

import java.util.ArrayList;
import java.util.List;

public final class CurlUploader {

    public static final String TYPE_PGY     = "pgy";

    public static final String KEY_FILE     = "file";
    public static final String KEY_USER_KEY = "userKey";
    public static final String KEY_API_KEY  = "apiKey";

    //opt
    public static final String KEY_INSTALL_TYPE  = "installType"; //1,2,3)。1：open，2：pwd，3：invitor。 default 1
    public static final String KEY_PASSWORD  = "password";
    public static final String KEY_UPDATE_DESC  = "updateDescription";
    public static final String KEY_CHANNEL  = "channelShortcut";

    private final String type;
    private final Map<String,String> params;

    public CurlUploader(String type, Map<String, String> params) {
        this.type = type;
        this.params = params;
    }

    public boolean upload(String projectDir){
        switch (type){
            case TYPE_PGY: {
                return uploadPuGongYing(projectDir, params);
            }
        }
        return false;
    }

    //curl --progress-bar -o /dev/null -k -F "file=@D:\study\work\out\build\oa\oa__app-release.apk" -F "uKey=" -F "_api_key=" https://www.pgyer.com/apiv1/app/upload
    private static boolean uploadPuGongYing(String projectDir, Map<String,String> inMap){
        java.util.Map<String, String> map = inMap.toNormalMap();
        String file = map.remove(KEY_FILE);
        String userKey = map.remove(KEY_USER_KEY);
        String apiKey = map.remove(KEY_API_KEY);
        if(file == null){
            System.err.println("upload file can't be empty.");
            return false;
        }
        if(userKey == null){
            System.err.println("userKey can't be empty.");
            return false;
        }
        if(apiKey == null){
            System.err.println("apiKey can't be empty.");
            return false;
        }

        List<String> params = new ArrayList<>();
        params.add("-k");
        params.add("-F");
        params.add("\"file=@" + file +"\"");
        params.add("-F");
        params.add("\"uKey="+ userKey +"\"");

        params.add("-F");
        params.add("\"_api_key="+ apiKey +"\"");
        for (java.util.Map.Entry<String,String> en : map.entrySet()){
            if(TextUtils.isEmpty(en.getKey()) || TextUtils.isEmpty(en.getValue())){
                continue;
            }
            params.add("-F");
            params.add("\""+ en.getKey() +"=" + en.getValue() +"\"");
        }
        //params.add("https://www.pgyer.com/apiv1/app/upload");

        String uploadUrl = "https://www.pgyer.com/apiv1/app/upload";
        return upload(projectDir, params, uploadUrl, true);
    }

    private static boolean upload(String projectDir, List<String> params, String uploadUrl, boolean showProgressBar){
        final List<String> list = new ArrayList<>();
        list.add("curl");
        if(showProgressBar){
            list.add("--progress-bar");
            list.add("-o");
            list.add("curl_log.txt");
        }
        VisitServices.from(params).fire(new FireVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                list.add(s);
                return null;
            }
        });
        list.add(uploadUrl);
        //execute cmd
        CmdHelper cleanHelper = new CmdHelper(list.toArray(new String[list.size()]));
        cleanHelper.setWorkDir(projectDir);
        if(!cleanHelper.execute(new CmdHelper.InhertIoCallback())){
            System.err.println("Curl upload failed. uploadUrl = " + uploadUrl);
            return false;
        }
        return true;
    }
}
