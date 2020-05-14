package com.heaven7.study;

import com.heaven7.java.visitor.util.Map;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

public final class ApkParamTool {

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
        Apktools.execute(map);
    }
}
