package com.heaven7.study;

import com.heaven7.java.base.util.FileUtils;
import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.visitor.MapResultVisitor;
import com.heaven7.java.visitor.collection.KeyValuePair;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.java.visitor.util.Map;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class Apktools {

    private static final String TEMPLATE_FILE_PATH       = "templateFilePath";
    private static final String REPLACE_FILE_PATH        = "replaceFilePath";
    private static final String FINAL_OUT_TEMPLATE_PATH  = "finalOutTemplatePath";

    private static final String PROJECT_DIR = "projectDir";
    private static final String APK_OUT_DIR = "apkOutDir";
    private static final String APK_PREFIX  = "apkPrefix";
    private static final String RELEASE     = "release";
    private static final String GEN_APK     = "genApk";

    private static final List<String> KEYS = Arrays.asList(
            TEMPLATE_FILE_PATH,
            REPLACE_FILE_PATH,
            FINAL_OUT_TEMPLATE_PATH,
            PROJECT_DIR,
            APK_OUT_DIR,
            APK_PREFIX,
            RELEASE,
            GEN_APK
    );

    /*public*/ static void execute(Map<String, String> map){
        for (String key : KEYS){
            if(!verifyParam(map, key)){
                 return;
            }
        }
        List<String> values = VisitServices.from(map).sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                int i1 = KEYS.indexOf(o1);
                int i2 = KEYS.indexOf(o2);
                return Integer.compare(i1, i2);
            }
        }).map(new MapResultVisitor<String, String, String>() {
            @Override
            public String visit(KeyValuePair<String, String> t, Object param) {
                return t.getValue();
            }
        }).getAsList();

        main(values.toArray(new String[values.size()]));
    }
    private static boolean verifyParam(Map<String, String> map, String key){
        String s1 = map.get(key);
        if(TextUtils.isEmpty(s1)){
            System.err.println(key + " can't be empty");
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        try {
            String templateFilePath = args[0];
            String replaceFilePath = args[1];
            String finalOutTemplatePath = args[2];
            //gen config file.
            ConfigFileGenerator generator = new ConfigFileGenerator(templateFilePath, replaceFilePath, finalOutTemplatePath);
            if(!generator.generate()){
                return;
            }
        }catch (Exception e){
            logParamError();
            return;
        }

        String projectDir;
        String apkOutDir ;
        String apkPrefix ;
        boolean release ;
        boolean genApk ;

        try {
            projectDir = args[3];
            apkOutDir = args[4];
            apkPrefix = args[5];
            release = Boolean.parseBoolean(args[6]);
            genApk = Boolean.parseBoolean(args[7]);
        }catch (Exception e){
            logParamError();
            return;
        }
        if(!genApk){
            return;
        }
        //cd to projectDir
    /*    String drive = getDrive(projectDir);
        String[] cdCmds = {drive +":"};
        CmdHelper cdHelper = new CmdHelper(cdCmds);
        System.out.println(" >>> start execute cmd: " + cdHelper.getCmdActually());
        cdHelper.execute(new CmdHelper.InhertIoCallback());

        cdCmds = new String[] {"cd", projectDir};
        cdHelper = new CmdHelper(cdCmds);
        System.out.println(" >>> start execute cmd: " + cdHelper.getCmdActually());*/

        //clean
        String[] cleanCmds = {projectDir + "/gradlew.bat", "clean"};
        CmdHelper cleanHelper = new CmdHelper(cleanCmds);
        cleanHelper.setWorkDir(projectDir);
        System.out.println(" >>> start execute cmd " + cleanHelper.getCmdActually());
        if(!cleanHelper.execute(new CmdHelper.InhertIoCallback())){
            return;
        }
        System.out.println("clean success. project = " + projectDir);
        //build apk
        String[] cmds = release ? new String[]{projectDir + "/gradlew.bat", "assembleRelease"}
            : new String[]{projectDir + "/gradlew.bat", "assembleDebug"};
        CmdHelper cmd = new CmdHelper(cmds);
        cmd.setWorkDir(projectDir);
        System.out.println(" >>> start execute cmd: " + cmd.getCmdActually());
        if(!cmd.execute(new CmdHelper.InhertIoCallback())){
            return;
        }
        System.out.println("build apk finished , start copy apk.");

        //copy apk
        String inputFile = projectDir +"/app/build/outputs/apk/" +
                (release ? "release/app-release.apk" :"debug/app-debug.apk");
        File outFile = new File(apkOutDir, release ? apkPrefix + "__app-release.apk" : apkPrefix + "__app-debug.apk");
        if(outFile.exists()){
            outFile.delete();
        }
        FileUtils.copyFile(new File(inputFile), outFile);
        System.out.println("copy apk finished. path = " + outFile.getAbsolutePath());
    }

    private static String getDrive(String projectDir) {
        int index = projectDir.indexOf(":");
        return projectDir.substring(0, index);
    }

    private static void logParamError(){
        System.err.println("params error. require 8. like '$[templateFilePath replaceFilePath finalOutTemplatePath projectDir apkOutDir apkPrefix release(bool) genApk(bool)]'");
    }
}