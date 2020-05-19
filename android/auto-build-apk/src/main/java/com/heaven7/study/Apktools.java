package com.heaven7.study;

import com.heaven7.java.base.util.FileUtils;
import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.visitor.MapPredicateVisitor;
import com.heaven7.java.visitor.MapResultVisitor;
import com.heaven7.java.visitor.collection.KeyValuePair;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.java.visitor.util.Map;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public final class Apktools {

    private static final String IGNORE_TEMPLATE          = "ignoreTemplate";
    private static final String TEMPLATE_FILE_PATH       = "templateFilePath";
    private static final String REPLACE_FILE_PATH        = "replaceFilePath";
    private static final String FINAL_OUT_TEMPLATE_PATH  = "finalOutTemplatePath";

    /*private*/ static final String PROJECT_DIR = "projectDir";
    private static final String APK_OUT_DIR = "apkOutDir";
    private static final String APK_PREFIX  = "apkPrefix";
    private static final String RELEASE     = "release";
    private static final String GEN_APK     = "genApk";

    private static final List<String> KEYS = Arrays.asList(
            IGNORE_TEMPLATE,
            TEMPLATE_FILE_PATH,
            REPLACE_FILE_PATH,
            FINAL_OUT_TEMPLATE_PATH,

            PROJECT_DIR,
            APK_OUT_DIR,
            APK_PREFIX,
            RELEASE,
            GEN_APK
    );

    /*public*/ static String execute(Map<String, String> map){
        //process '{$b}'
        map = new VariableProcessor(map).process();
        if(map == null){
            return null;
        }

        for (String key : KEYS){
            if(!verifyParam(map, key)){
                 return null;
            }
        }
        List<String> values = VisitServices.from(map).filter(new MapPredicateVisitor<String, String>() {
            @Override
            public Boolean visit(KeyValuePair<String, String> pair, Object param) {
                return KEYS.indexOf(pair.getKey()) >= 0 ;
            }
        }, null).sort(new Comparator<String>() {
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

        return executeImpl(values.toArray(new String[values.size()]));
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
        executeImpl(args);
    }
    private static String executeImpl(String[] args) {
        try {
            String ignoreTemplate = args[0];
            //if ignore template. just build direct
            if(!Boolean.parseBoolean(ignoreTemplate)){
                String templateFilePath = args[1];
                String replaceFilePath = args[2];
                String finalOutTemplatePath = args[3];
                //gen config file.
                ConfigFileGenerator generator = new ConfigFileGenerator(templateFilePath, replaceFilePath, finalOutTemplatePath);
                if(!generator.generate()){
                    return null;
                }
            }
        }catch (Exception e){
            logParamError();
            return null;
        }

        String projectDir;
        String apkOutDir ;
        String apkPrefix ;
        boolean release ;
        boolean genApk ;

        try {
            projectDir = args[4];
            apkOutDir = args[5];
            apkPrefix = args[6];
            release = Boolean.parseBoolean(args[7]);
            genApk = Boolean.parseBoolean(args[8]);
        }catch (Exception e){
            logParamError();
            return null;
        }
        if(!genApk){
            return null;
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
            return null;
        }
        System.out.println("clean success. project = " + projectDir);
        //build apk
        String[] cmds = release ? new String[]{projectDir + "/gradlew.bat", "assembleRelease"}
                : new String[]{projectDir + "/gradlew.bat", "assembleDebug"};
        CmdHelper cmd = new CmdHelper(cmds);
        cmd.setWorkDir(projectDir);
        System.out.println(" >>> start execute cmd: " + cmd.getCmdActually());
        if(!cmd.execute(new CmdHelper.InhertIoCallback())){
            return null;
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
        return outFile.getAbsolutePath();
    }

    private static String getDrive(String projectDir) {
        int index = projectDir.indexOf(":");
        return projectDir.substring(0, index);
    }

    private static void logParamError(){
        System.err.println("params error. require 8. like '$[templateFilePath replaceFilePath finalOutTemplatePath projectDir apkOutDir apkPrefix release(bool) genApk(bool)]'");
    }
}
