package com.heaven7.study.dll;

import com.heaven7.java.base.util.IOUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class DllToWin32Param {

    public static final String KEY_PEXPORTS    = "pexports_dir";
    public static final String KEY_LIB_X64_DIR = "lib_x64_dir";
    public static final String KEY_LIB_X86_DIR = "lib_x86_dir";
    //xxx -arch=x86 a.dll outDir
    //xxx -arch=x64 a.dll outDir

    //pexports.exe E:\study\cpp\msys2_64\mingw64\bin\libgpr.dll > libgpr2.def
    //E:\visualstudio\ide\VC\Tools\MSVC\14.16.27023\bin\Hostx64\x64\lib.exe /def:libgpr2.def /machine:x64 /out:libgpr.lib
    private String pexportsDir;
    private String lib_x64_dir;
    private String lib_x86_dir;

    public static DllToWin32Param from(String configFile) {
        FileReader reader = null;
        try {
            reader = new FileReader(configFile);
        } catch (FileNotFoundException e) {
            return null;
        }
        List<String> lines;
        try {
            lines = IOUtils.readStringLines(reader);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            IOUtils.closeQuietly(reader);
        }
        final DllToWin32Param obj = new DllToWin32Param();

        VisitServices.from(lines).fire(new FireVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                if(!s.startsWith("#")){
                    int idx = s.indexOf("=");
                    String key = s.substring(0, idx);
                    String value = s.substring(idx + 1);
                    switch (key){
                        case KEY_PEXPORTS:
                            obj.setPexportsDir(value);
                            break;
                        case KEY_LIB_X64_DIR:
                            obj.setLib_x64_dir(value);
                            break;
                        case KEY_LIB_X86_DIR:
                            obj.setLib_x86_dir(value);
                            break;
                    }
                }
                return null;
            }
        });
        return obj;
    }

    public String getPexportsDir() {
        return pexportsDir;
    }

    public void setPexportsDir(String pexportsDir) {
        this.pexportsDir = pexportsDir;
    }

    public String getLib_x64_dir() {
        return lib_x64_dir;
    }

    public void setLib_x64_dir(String lib_x64_dir) {
        this.lib_x64_dir = lib_x64_dir;
    }

    public String getLib_x86_dir() {
        return lib_x86_dir;
    }

    public void setLib_x86_dir(String lib_x86_dir) {
        this.lib_x86_dir = lib_x86_dir;
    }
}
