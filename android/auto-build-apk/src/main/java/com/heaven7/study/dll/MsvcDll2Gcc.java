package com.heaven7.study.dll;

import com.heaven7.java.base.util.FileUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.ResultVisitor;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.study.CmdBuilder;
import com.heaven7.study.api.BaseCmdExecutor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public final class MsvcDll2Gcc extends BaseCmdExecutor {

    //pexport xxx.dll > xxx.def
    //dlltool.exe -D xxx.dll -d xxx.def -l xxx.dll.a -k

    private MsvcDll2GccParam param;
    private String arch;
    private String outDir;
    private final List<String> toDefCmds = new ArrayList<>();

    public MsvcDll2Gcc(String configFile, String arch, String inputDll, String outDir) {
        super(inputDll);
        this.param = MsvcDll2GccParam.from(configFile);
        this.arch = arch;
        this.outDir = outDir;
        File dir = new File(outDir);
        if(!dir.exists()){
            dir.mkdirs();
        }
    }
    @Override
    public void execute(String input) {
        String fileName = FileUtils.getFileName(input);
        String defFilePath = outDir + "/" + fileName + ".def";
        //pexport xxx.dll > xxx.def
        CmdBuilder cmdBuilder = new CmdBuilder()
                .str(param.getPexports_dir() + "/pexports.exe")
                .str(input)
                .str("1>" + defFilePath);
        doExecuteCmd(cmdBuilder.toCmd());

        //dlltool.exe -D xxx.dll -d xxx.def -l xxx.dll.a -k
        String libDir = arch.equals("x64") ? param.getDllTool_x64_dir() : param.getDllTool_x86_dir();
        String outFile = outDir + "/" + fileName + ".dll.a";
        cmdBuilder = new CmdBuilder()
                .str(libDir + "/dlltool.exe")
                .str("-D")
                .str(input)
                .str("-d")
                .str(defFilePath)
                .str("-l")
                .str(outFile)
                .str("-k");
        doExecuteCmd(cmdBuilder.toCmd());
    }
    @Override
    protected void onPreExecuteCmd(String cmd) {
        if(cmd.startsWith(param.getPexports_dir())){
            toDefCmds.add(cmd);
        }
    }

    public static void main(String[] args) {
        //xxx xxx.properties -arch=x86 a.dll outDir
        //xxx xxx.properties -arch=x64 a.dll outDir
        if(args.length < 4){
            throw new IllegalArgumentException("param error, should be: java -jar xxx.jar xxx.properties -arch=<x64/x86> <input dll/dll dir> <outDir>.");
        }
        if(!args[1].startsWith("-arch")){
            throw new IllegalArgumentException("arch param error, should be: java -jar xxx.jar xxx.properties -arch=<x64/x86> <input dll/dll dir> <outDir>");
        }
        if(!new File(args[3]).isDirectory()){
            throw new IllegalArgumentException("outdir param error, should be: java -jar xxx.jar xxx.properties -arch=<x64/x86> <input dll/dll dir> <outDir>");
        }
        //args[2] can be a dll or a dir. 'c:xxx/a.dll' or 'c:xxx/xx::a.dll,b.dll' or 'c:xxx'
       /* if(!args[2].endsWith("dll")){
            throw new IllegalArgumentException("param error, should be: java -jar xxx.jar xxx.properties -arch=<x64/x86> <input dll> <outDir>");
        }*/
        String arch = args[1].substring(6);
        MsvcDll2Gcc instance = new MsvcDll2Gcc(args[0], arch, args[2], args[3]);
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
