package com.heaven7.study.dll;

import com.heaven7.java.base.util.FileUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.study.CmdBuilder;
import com.heaven7.study.CmdHelper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class GccDllToWin32Lib {

    private DllToWin32Param param;
    private String arch;
    private String inputDll;
    private String outDir;
    private final List<String> toDefCmds = new ArrayList<>();

    public GccDllToWin32Lib(String configFile, String arch, String inputDll, String outDir) {
        this.param = DllToWin32Param.from(configFile);
        this.arch = arch;
        this.inputDll = inputDll;
        this.outDir = outDir;
        if(param == null){
            throw new RuntimeException();
        }
    }
    public void execute(){
        execute(inputDll);
    }
    public void execute(List<String> dlls){
        VisitServices.from(dlls).fire(new FireVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                execute(s);
                return null;
            }
        });
    }
    public void execute(String inputDll){
        String fileName = FileUtils.getFileName(inputDll);
        String defFilePath = outDir + "/" + fileName + ".def";
        //pexports.exe E:\study\cpp\msys2_64\mingw64\bin\libgpr.dll > libgpr2.def
        //E:\visualstudio\ide\VC\Tools\MSVC\14.16.27023\bin\Hostx64\x64\lib.exe /def:libgpr2.def /machine:x64 /out:libgpr.lib
        //1, to def
        CmdBuilder cmdBuilder = new CmdBuilder()
                .str(param.getPexportsDir() + "/pexports.exe")
                .str(inputDll)
                .str(">")
                .str(defFilePath);

        CmdHelper cmd = new CmdHelper(cmdBuilder.toCmd());
        System.out.println(" >>> start execute cmd: " + cmd.getCmdActually());
        if(!cmd.execute(new CmdHelper.InhertIoCallback())){
            System.err.println(">>> execute failed.");
        }else{
            System.err.println(">>> execute success.");
        }
        toDefCmds.add(cmd.getCmdActually());
        //2, to lib
        String libDir = arch.equals("x64") ? param.getLib_x64_dir() : param.getLib_x86_dir();
        cmdBuilder = new CmdBuilder().str(libDir + "/lib.exe")
                .str("/def:" + defFilePath)
                .str("/machine:" + arch)
                .str("/out:" + outDir + "/" + fileName + ".lib");

        cmd = new CmdHelper(cmdBuilder.toCmd());
        System.out.println(" >>> start execute cmd: " + cmd.getCmdActually());
        if(!cmd.execute(new CmdHelper.InhertIoCallback())){
            System.err.println(">>> execute failed.");
        }else{
            System.err.println(">>> execute success.");
        }
    }

    public static void main(String[] args) {
        //xxx xxx.properties -arch=x86 a.dll outDir
        //xxx xxx.properties -arch=x64 a.dll outDir
        if(args.length < 4){
            throw new IllegalArgumentException("param error, should be: java -jar xxx.jar xxx.properties -arch=<x64/x86> <input dll/dll dir> <outDir>.");
        }

        if(!args[1].startsWith("-arch")){
            throw new IllegalArgumentException("param error, should be: java -jar xxx.jar xxx.properties -arch=<x64/x86> <input dll/dll dir> <outDir>");
        }
        if(!new File(args[3]).isDirectory()){
            throw new IllegalArgumentException("param error, should be: java -jar xxx.jar xxx.properties -arch=<x64/x86> <input dll/dll dir> <outDir>");
        }
        //args[2] can be a dll or a dir. 'c:xxx/a.dll' or 'c:xxx/xx::a.dll,b.dll' or 'c:xxx'
       /* if(!args[2].endsWith("dll")){
            throw new IllegalArgumentException("param error, should be: java -jar xxx.jar xxx.properties -arch=<x64/x86> <input dll> <outDir>");
        }*/
        String arch = args[1].substring(6);
        GccDllToWin32Lib instance = new GccDllToWin32Lib(args[0], arch, args[2], args[3]);
        if(args[2].endsWith("dll")){
            //single dll
            instance.execute();
        }else{
            //multi dll
            final String pat = "::";
            int index = args[2].indexOf(pat);
            if(index >= 0 ){
                //dlls
                String dir = args[2].substring(0, index);
                if(!new File(dir).isDirectory()){
                    throw new IllegalArgumentException("must be dlls' dir: " + dir);
                }
                String filesStr = args[2].substring(index + pat.length());
                List<String> list = VisitServices.from(filesStr.split(",")).map(new ResultVisitor<String, String>() {
                    @Override
                    public String visit(String s, Object param) {
                        if(s.endsWith(".dll")){
                            return dir + "/" + s;
                        }
                        return dir + "/" + s + ".dll";
                    }
                }).getAsList();
                instance.execute(list);
            }else{
                //all dlls from dir
                if(!new File(args[2]).isDirectory()){
                    throw new IllegalArgumentException("must be dlls' dir");
                }
                List<String> files = FileUtils.getFiles(new File(args[2]), "dll");
                instance.execute(files);
            }
        }
        System.out.println();
        VisitServices.from(instance.toDefCmds).fire(new FireVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                System.out.println(s);
                return null;
            }
        });
    }
}
