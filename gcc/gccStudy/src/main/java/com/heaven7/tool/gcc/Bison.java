package com.heaven7.tool.gcc;

import com.heaven7.java.base.util.DefaultPrinter;

import java.io.File;

public final class Bison {

    private static final String TAG = "Bison";
    private String flexFile;
    private String bisonFile;
    private String outDir;

    public Bison(String flexFile, String bisonFile) {
        this.flexFile = flexFile;
        this.bisonFile = bisonFile;
    }

    public String getOutDir() {
        return outDir;
    }

    public void setOutDir(String outDir) {
        this.outDir = outDir;
    }

    public boolean process(){
        String[] cmds = new CmdBuilder().str("flex")
                .str(flexFile)
                .toCmd();
        boolean result = new CmdHelper(cmds).execute(new CmdHelper.InhertIoCallback(){
            @Override
            public void beforeStartCmd(CmdHelper helper, ProcessBuilder pb) {
                super.beforeStartCmd(helper, pb);
                if(outDir != null){
                    pb.directory(new File(outDir));
                }
            }
        });
        DefaultPrinter.getDefault().debug(TAG, "process", "flex result = " + result);
        if(!result){
            return false;
        }

        cmds = new CmdBuilder().str("bison")
                .str("-d").str(bisonFile)
                // .str("-y").str("-b").str("test").str(bisonFile) //no header file
                .toCmd();
        result = new CmdHelper(cmds).execute(new CmdHelper.InhertIoCallback(){
            @Override
            public void beforeStartCmd(CmdHelper helper, ProcessBuilder pb) {
                super.beforeStartCmd(helper, pb);
                if(outDir != null){
                    pb.directory(new File(outDir));
                }
            }
        });
        DefaultPrinter.getDefault().debug(TAG, "process", "bison result = " + result);
        return result;
    }
}
