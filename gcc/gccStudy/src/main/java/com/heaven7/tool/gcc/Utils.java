package com.heaven7.tool.gcc;

import com.heaven7.java.base.util.FileUtils;

/*public*/ class Utils {

    public static String getFilePrefix(String src){
        String dir = FileUtils.getFileDir(src, 1, true);
        String name = FileUtils.getFileName(src);
        return dir + "/" + name;
    }
}
