package com.heaven7.study.dll;

import com.heaven7.study.api.BaseCmdParam;

public class MsvcDll2GccParam extends BaseCmdParam {

    private String pexports_dir;
    private String dllTool_x86_dir;
    private String dllTool_x64_dir;

    public static MsvcDll2GccParam from(String configFile) {
        MsvcDll2GccParam param = new MsvcDll2GccParam();
        param.populateFromConfigFile(configFile);
        return param;
    }
    public String getPexports_dir() {
        return pexports_dir;
    }

    public void setPexports_dir(String pexports_dir) {
        this.pexports_dir = pexports_dir;
    }

    public String getDllTool_x86_dir() {
        return dllTool_x86_dir;
    }

    public void setDllTool_x86_dir(String dllTool_x86_dir) {
        this.dllTool_x86_dir = dllTool_x86_dir;
    }

    public String getDllTool_x64_dir() {
        return dllTool_x64_dir;
    }

    public void setDllTool_x64_dir(String dllTool_x64_dir) {
        this.dllTool_x64_dir = dllTool_x64_dir;
    }

    @Override
    protected void applyPair(String key, String value) {
        switch (key){
            case "pexports_dir":
                setPexports_dir(value);
                break;
            case "dllTool_x86_dir":
                setDllTool_x86_dir(value);
                break;
            case "dllTool_x64_dir":
                setDllTool_x64_dir(value);
                break;
        }
    }
}
