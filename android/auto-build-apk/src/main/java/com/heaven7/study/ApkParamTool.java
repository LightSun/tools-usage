package com.heaven7.study;

import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.visitor.util.Map;

import java.io.File;

public final class ApkParamTool {

    //cmd: $config_file pgy $upload_config_file
    public static void main(String[] args) {
        try {
            new File(args[0]);
        }catch (Exception e){
            System.err.println("you must assign the config file of ApkTools.");
            return;
        }

        Map<String, String> map = Utils.load(args[0]);
        if(map == null){
            System.err.println("load properties failed for path = " + args[0]);
            return;
        }
        String apkPath = Apktools.execute(map);
        if(apkPath != null && !TextUtils.isEmpty(args[1])){
            String projectDir = map.get(Apktools.PROJECT_DIR);
            //upload
            try {
                map = Utils.load(args[2]);
                if(map == null){
                    return;
                }
            }catch (Exception e){
                System.err.println("can't find upload-config-file for path = " + args[1]);
                return;
            }
            map.put(CurlUploader.KEY_FILE, apkPath);

            System.out.println("start upload: type = " + args[1]);
            boolean result = new CurlUploader(args[1], map).upload(projectDir);
            System.out.println(" upload result = " + result);
        }
    }
}
