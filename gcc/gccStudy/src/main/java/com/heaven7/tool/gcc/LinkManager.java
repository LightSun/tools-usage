package com.heaven7.tool.gcc;

import java.util.ArrayList;
import java.util.List;

//https://www.cnblogs.com/fnlingnzb-learner/p/8119854.html
public final class LinkManager {

    private final List<String> includeDirs = new ArrayList<>(); //-I
    private final List<String> linkDirs = new ArrayList<>();    //-L
    private final List<LibParam> linkLibs = new ArrayList<>(); //-lfreetype

    //-Wl,-Bstatic和-Wl,-Bdynamic。这两个选项是gcc的特殊选项，它会将选项的参数传递给链接器，作为链接器的选项。

    public LinkManager withIncludeDir(String dir){
        includeDirs.add(dir);
        return this;
    }
    public LinkManager withLinkDir(String dir){
        linkDirs.add(dir);
        return this;
    }
    public LinkManager withStaticLib(String name){
        linkLibs.add(new LibParam(name, true));
        return this;
    }
    public LinkManager withSharedLib(String name){
        linkLibs.add(new LibParam(name, false));
        return this;
    }

    public String toIncludeStr(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < includeDirs.size() ; i ++){
            sb.append("-I").append(includeDirs.get(i));
            if(i != includeDirs.size() - 1){
                sb.append(" ");
            }
        }
        return sb.toString();
    }
    public String toLinkDirStr(){
        StringBuilder sb = new StringBuilder();
        for (int i = 0 ; i < linkDirs.size() ; i ++){
            sb.append("-L").append(linkDirs.get(i));
            if(i != linkDirs.size() - 1){
                sb.append(" ");
            }
        }
        return sb.toString();
    }
    public String toLibsStr(){
        StringBuilder sb = new StringBuilder();
        LibParam last = null;
        for (int i = 0 ; i < linkLibs.size() ; i ++){
            LibParam lp = linkLibs.get(i);
            if(last == null){
                sb.append(lp._static ? "-Wl,-Bstatic ": "-Wl,-Bdynamic ");
            }else {
                if((last._static && lp._static) || (!last._static && !lp._static)){
                    //with the same to last
                }else {
                    sb.append(lp._static ? "-Wl,-Bstatic ": "-Wl,-Bdynamic ");
                }
            }
            sb.append("-l").append(lp);
            if(i != linkLibs.size() - 1){
                sb.append(" ");
            }
            last = lp;
        }
        return sb.toString();
    }

    public String toStr(StringBuilder sb){
        if(sb == null){
            sb = new StringBuilder();
        }
        String s = toIncludeStr();
        if(s.length() > 0){
            sb.append(s);
        }

        s = toLinkDirStr();
        if(s.length() > 0){
            sb.append(" ").append(s);
        }

        s = toLibsStr();
        if(s.length() > 0){
            sb.append(" ").append(s);
        }
        return sb.toString();
    }

    public String toStr(){
        return toStr(null);
    }

    private static class LibParam{
        String name;
        boolean _static;

        public LibParam(String name, boolean _static) {
            this.name = name;
            this._static = _static;
        }
    }

}
