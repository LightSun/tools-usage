package com.heaven7.tool.gcc;

import com.heaven7.java.base.util.DefaultPrinter;

import java.util.List;

public class GccCompileManager implements IGccCompile{

    public static final byte GEN_TYPE_STATIC = 1;
    public static final byte GEN_TYPE_SHARED = 2;
    public static final byte GEN_TYPE_EXECUTE = 3;

    private static final String TAG = "GccCompileManager";

    private List<String> cSrcFiles;
    private List<String> cppSrcFiles;

    private List<String> cSrcFilePres;
    private List<String> cppSrcFilePres;

    private String compilerPath;
    private String cCompilerName;
    private String cppCompilerName;

    private String arName;
    private String ldName;

    private String cFlags = "";
    private String cppFlags = "";

    private String outDir;
    private byte genType;
    private String genName;

    private LinkManager linkManager;

    public LinkManager getLinkManager() {
        return linkManager;
    }

    public void setLinkManager(LinkManager linkManager) {
        this.linkManager = linkManager;
    }
    public String getGenName() {
        return genName;
    }
    public void setGenName(String genName) {
        this.genName = genName;
    }

    public byte getGenType() {
        return genType;
    }
    public void setGenType(byte genType) {
        this.genType = genType;
    }

    public String getcFlags() {
        return cFlags;
    }

    public void setcFlags(String cFlags) {
        this.cFlags = cFlags;
    }

    public String getCppFlags() {
        return cppFlags;
    }

    public void setCppFlags(String cppFlags) {
        this.cppFlags = cppFlags;
    }

    public String getCompilerPath() {
        return compilerPath;
    }
    public void setCompilerPath(String path){
        this.compilerPath = path;
    }
    public String getcCompilerName() {
        return cCompilerName;
    }

    public void setcCompilerName(String cCompilerName) {
        this.cCompilerName = cCompilerName;
    }

    public String getCppCompilerName() {
        return cppCompilerName;
    }

    public void setCppCompilerName(String cppCompilerName) {
        this.cppCompilerName = cppCompilerName;
    }

    public String getOutDir() {
        return outDir;
    }
    public void setOutDir(String outDir) {
        this.outDir = outDir;
    }

    @Override
    public String preProcess() {
        if(preProcess(cSrcFiles, cSrcFilePres, false)){
            return "preProcess cFile failed.";
        }
        if(preProcess(cppSrcFiles, cppSrcFilePres, true)){
            return "preProcess cFile failed.";
        }
        return null;
    }
    @Override
    public String compile() {
        if(compile(cSrcFilePres, false)){
            return "compile cFile failed";
        }
        if(compile(cppSrcFilePres, true)){
            return "compile cppFile failed";
        }
        return null;
    }

    @Override
    public String asm() {
        if(asm(cSrcFilePres, false)){
            return "asm cFile failed";
        }
        if(asm(cppSrcFilePres, true)){
            return "asm cppFile failed";
        }
        return null;
    }

    @Override
    public String link() {
        //TODO pack and link
        switch (genType){
            case GEN_TYPE_SHARED:
                //gcc -shared -fPIC -o libmyshare.so test.c
                if(!ld()){
                    return "ld failed";
                }
                break;
            case GEN_TYPE_STATIC:
                //ar & link
                if(!ar()){
                    return "ar failed";
                }

                break;
            case GEN_TYPE_EXECUTE:

                break;
        }
        return null;
    }
    private boolean ld(){
        CmdBuilder ldBuilder = new CmdBuilder();
        ldBuilder.str(getLdName())
                .str("-o")
                .str(outDir + "/" + genName);

        for (int i = 0 , size  = cSrcFilePres.size() ; i < size ; i ++) {
            String pre = cSrcFilePres.get(i);
            ldBuilder.str(pre +".o");
        }
        for (int i = 0 , size  = cppSrcFilePres.size() ; i < size ; i ++) {
            String pre = cppSrcFilePres.get(i);
            ldBuilder.str(pre +".o");
        }
        ldBuilder.str(linkManager.toLibsStr());
        boolean result = new CmdHelper(ldBuilder.toCmd()).execute(new CmdHelper.InhertIoCallback());
        DefaultPrinter.getDefault().debug(TAG, "ld", "result = " + result);
        return result;
    }
    private boolean ar() {
        CmdBuilder arBuilder = new CmdBuilder();
        arBuilder.str(getArName())
                .str("-rcs")
                .str(outDir + "/lib" + genName +".a");
        for (int i = 0 , size  = cSrcFilePres.size() ; i < size ; i ++) {
            String pre = cSrcFilePres.get(i);
            arBuilder.str(pre +".o");
        }
        for (int i = 0 , size  = cppSrcFilePres.size() ; i < size ; i ++) {
            String pre = cppSrcFilePres.get(i);
            arBuilder.str(pre +".o");
        }
        arBuilder.str(linkManager.toLibsStr());
        boolean result = new CmdHelper(arBuilder.toCmd()).execute(new CmdHelper.InhertIoCallback());
        DefaultPrinter.getDefault().debug(TAG, "ar", "result = " + result);
        return result;
    }

    @Override
    public String start() {
        String msg = preProcess();
        if(msg != null){
            return msg;
        }
        msg = compile();
        if(msg != null){
            return msg;
        }
        msg = asm();
        if(msg != null){
            return msg;
        }
        msg = link();
        if(msg != null){
            return msg;
        }
        return null;
    }

    private boolean preProcess(List<String> cSrcFiles, List<String> cSrcFilePres, boolean cpp){
        final List<String> pres = cSrcFilePres;
        for (int i = 0 , size  = cSrcFiles.size() ; i < size ; i ++){
            String s = cSrcFiles.get(i);
            String[] strs = new CmdBuilder().str(getToolName(cpp))
                    .str(cpp ? cppFlags : cFlags)
                    .str(linkManager.toIncludeStr())
                    .str("-E").str(s).str("-o").str(pres.get(i) + ".i")
                    .toCmd();
            boolean result = new CmdHelper(strs).execute(new CmdHelper.InhertIoCallback());
            DefaultPrinter.getDefault().debug(TAG, "preProcess", "result = " + result);
            if(!result){
                return false;
            }
        }
        return true;
    }
    private boolean compile(List<String> cSrcFilePres, boolean cpp){
        final List<String> pres = cSrcFilePres;
        for (int i = 0 , size  = pres.size() ; i < size ; i ++) {
            String pre = pres.get(i);
            String[] strs = new CmdBuilder().str(getToolName(cpp))
                    .str(cpp ? cppFlags : cFlags)
                    .str(linkManager.toIncludeStr())
                    .str("-S").str(pre + ".i").str("-o").str(pre +".s")
                    .toCmd();
            boolean result = new CmdHelper(strs).execute(new CmdHelper.InhertIoCallback());
            DefaultPrinter.getDefault().debug(TAG, "compile", "result = " + result);
            if(!result){
                return false;
            }
        }
        return true;
    }
    private boolean asm(List<String> cSrcFilePres, boolean cpp){
        final List<String> pres = cSrcFilePres;
        for (int i = 0 , size  = cSrcFilePres.size() ; i < size ; i ++) {
            String pre = pres.get(i);
            String[] strs = new CmdBuilder().str(getToolName(cpp))
                    .str("-c").str(pre + ".s").str("-o").str(pre +".o")
                    .toCmd();
            boolean result = new CmdHelper(strs).execute(new CmdHelper.InhertIoCallback());
            DefaultPrinter.getDefault().debug(TAG, "asm", "result = " + result);
            if(!result){
                return false;
            }
        }
        return true;
    }
    private String getToolName(boolean useCpp){
        if(compilerPath != null){
            if(useCpp){
                if(cppCompilerName != null){
                    return compilerPath + "/" + cppCompilerName;
                }
            }else {
                if(cCompilerName != null){
                    return compilerPath + "/" + cCompilerName;
                }
            }
        }
        return useCpp ? "g++" : "gcc";
    }
    private String getArName(){
        if(compilerPath != null){
            if(arName != null){
                return compilerPath + "/" + arName;
            }
        }
        return "ar";
    }
    private String getLdName(){
        if(compilerPath != null){
            if(ldName != null){
                return compilerPath + "/" + ldName;
            }
        }
        return "ld";
    }
}
