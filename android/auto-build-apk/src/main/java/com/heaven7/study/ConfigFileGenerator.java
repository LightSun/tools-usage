package com.heaven7.study;

import com.heaven7.java.visitor.MapFireVisitor;
import com.heaven7.java.visitor.collection.KeyValuePair;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.java.visitor.util.Map;

import java.io.*;

/**
 *
 */
public final class ConfigFileGenerator {

    private static final String OUT_FILE_NAME = "vars.properties";
    private String mTemplateFilePath;
    private String mReplaceFilePath;
    private String mOutDir;

    public ConfigFileGenerator(String mTemplateFilePath, String mReplaceFilePath, String mOutDir) {
        this.mTemplateFilePath = mTemplateFilePath;
        this.mReplaceFilePath = mReplaceFilePath;
        this.mOutDir = mOutDir;
    }

    public boolean generate(){
        Map<String, String> prop = Utils.load(mTemplateFilePath);
        Map<String, String> replaceProp = Utils.load(mReplaceFilePath);

        if(prop == null){
            System.err.println("load mTemplateFilePath/mReplaceFilePath failed");
            return false;
        }
        //replace if need
        if(replaceProp != null){
            VisitServices.from(replaceProp).fire(new MapFireVisitor<String, String>() {
                @Override
                public Boolean visit(KeyValuePair<String, String> pair, Object param) {
                    if(prop.containsKey(pair.getKey())){
                        prop.put(pair.getKey(), pair.getValue());
                    }
                    return null;
                }
            });
        }

        File outFile = new File(mOutDir, OUT_FILE_NAME);
        if(outFile.exists()){
            outFile.delete();
        }
        BufferedWriter fos = null;
        try {
            fos = new BufferedWriter(new FileWriter(outFile));
            fos.write("#the final variable properties for build android app.");
            fos.newLine();
            final BufferedWriter temp = fos;
            VisitServices.from(prop).fire(new MapFireVisitor<String, String>() {
                @Override
                public Boolean visit(KeyValuePair<String, String> pair, Object param) {
                    try {
                        temp.write(pair.getKey() +"=" + pair.getValue());
                        temp.newLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            });
            fos.flush();
        } catch (Exception e) {
            System.err.println("save properties to file failed. path = " + outFile.getAbsolutePath());
            return false;
        }finally {
            if(fos != null){
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
        return true;
    }

}
