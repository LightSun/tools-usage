package com.heaven7.study.potree;

import com.heaven7.java.base.util.Platforms;
import com.heaven7.study.CmdBuilder;
import com.heaven7.study.api.BaseCmdExecutor;

/**
 * steps:
 * 1, use pdal to convert pcd/others to las
 * 2, use potreeconverter convert las to potree files.
 * 3, generate write html.
 */
public final class EntryHtmlGenerator extends BaseCmdExecutor{

    private String m_PotreeConverterPath;

    public EntryHtmlGenerator() {
        super("");
    }

    public void convertToLas(String inputPath, String outPath){
        //pdal translate D:\360Downloads\pcl_files\71_final_stl.pcd D:\360Downloads\pcl_files\71_final.las
        //cd /d %~dp0
        //PotreeConverter.exe D:/360Downloads/pcl_files/71_final.las -o h7_test --generate-page page_h7
        //@PAUSE
        CmdBuilder builder = new CmdBuilder();
        if(Platforms.isWindows()){
            builder.cmd("cmd.exe /c \"pdal translate " + inputPath + " " + outPath + "\"");
        }else{
            builder.cmd("pdal translate " + inputPath + " " + outPath);
        }
        doExecuteCmd(builder.toCmd());
    }

    public void convertToPotree(){
        CmdBuilder builder = new CmdBuilder();
        if(m_PotreeConverterPath == null){
           //TODO builder.str(m_PotreeConverterPath);
        }
    }

    @Override
    public void execute(String input) {

    }
}
