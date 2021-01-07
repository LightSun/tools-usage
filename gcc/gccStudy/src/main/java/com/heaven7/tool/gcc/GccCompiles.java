package com.heaven7.tool.gcc;

import com.heaven7.java.base.util.DefaultPrinter;
import com.heaven7.java.base.util.FileUtils;
import com.heaven7.java.base.util.TextUtils;
import com.heaven7.java.visitor.FireIndexedVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.util.List;

//like: mingw
public final class GccCompiles {

    private static final String TAG = "GccCompiles";
    private boolean useCpp;
    private List<String> sourceFiles;
    private List<String> sourceFilePres;
    private String compilerPath;
    private String cCompilerName;
    private String cppCompilerName;

    private String outDir;

    public GccCompiles(boolean useCpp, List<String> sourceFiles) {
        this.useCpp = useCpp;
        this.sourceFiles = sourceFiles;
    }

    public GccCompiles(boolean useCpp, final String dir, List<String> sourceFiles) {
        this.useCpp = useCpp;
        this.sourceFiles = VisitServices.from(sourceFiles).map(new ResultVisitor<String, String>() {
            @Override
            public String visit(String s, Object param) {
                return dir + "/" +s;
            }
        }).getAsList();
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

    private List<String> getSourcePathPrefix(){
        if(sourceFilePres == null){
            sourceFilePres = VisitServices.from(sourceFiles).map(new ResultVisitor<String, String>() {
                @Override
                public String visit(String s, Object param) {
                    String dir = FileUtils.getFileDir(s, 1, true);
                    String name = FileUtils.getFileName(s);
                    return dir + "/" + name;
                }
            }).getAsList();
        }
        return sourceFilePres;
    }
    public boolean compileBisonDirectly(String name){
        final CmdBuilder cmdBuilder = new CmdBuilder().str(getToolName());
        VisitServices.from(sourceFiles).fireWithIndex(new FireIndexedVisitor<String>() {
            @Override
            public Void visit(Object param, String s, int index, int size) {
                cmdBuilder.str(s);
                return null;
            }
        });
        //todo just for bison
        cmdBuilder
                //.str("-lfl")
                //.str("-ly")
                .str("-o");
        if(TextUtils.isEmpty(outDir)){
            cmdBuilder.str(name);
        }else {
            cmdBuilder.str(outDir + "/" + name);
        }
        boolean result = new CmdHelper(cmdBuilder.toCmd()).execute(new CmdHelper.InhertIoCallback());
        DefaultPrinter.getDefault().debug(TAG, "compileExecutableDirectly", "result = " + result);
        return result;
    }

    public boolean compileExecutable(String name){
        if(!preProcess()){
            return false;
        }
        if(!compile()){
            return false;
        }
        if(!asm()){
            return false;
        }
        return linkExecutable(name);
    }
    //compile steps: preProcess, compile, asm,link
    private boolean preProcess(){
        final List<String> pres = getSourcePathPrefix();
        for (int i = 0 , size  = sourceFiles.size() ; i < size ; i ++){
            String s = sourceFiles.get(i);
            String[] strs = new CmdBuilder().str(getToolName())
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
    private boolean compile(){
        final List<String> pres = getSourcePathPrefix();
        for (int i = 0 , size  = sourceFiles.size() ; i < size ; i ++) {
            String pre = pres.get(i);
            String[] strs = new CmdBuilder().str(getToolName())
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
    private boolean asm(){
        final List<String> pres = getSourcePathPrefix();
        for (int i = 0 , size  = sourceFiles.size() ; i < size ; i ++) {
            String pre = pres.get(i);
            String[] strs = new CmdBuilder().str(getToolName())
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
    private boolean linkExecutable(String name){
        final List<String> pres = getSourcePathPrefix();
        final CmdBuilder cmdBuilder = new CmdBuilder().str(getToolName());
        VisitServices.from(sourceFiles).fireWithIndex(new FireIndexedVisitor<String>() {
            @Override
            public Void visit(Object param, String s, int index, int size) {
                String pre = pres.get(index);
                cmdBuilder.str(pre + ".o");
                return null;
            }
        });
        cmdBuilder.str("-o");
        if(TextUtils.isEmpty(outDir)){
            cmdBuilder.str(name + ".exe");
        }else {
            cmdBuilder.str(outDir + "/" + name + ".exe");
        }

        boolean result = new CmdHelper(cmdBuilder.toCmd()).execute(new CmdHelper.InhertIoCallback());
        DefaultPrinter.getDefault().debug(TAG, "linkExecutable", "result = " + result);
        return result;
    }
    private String getToolName(){
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
}
