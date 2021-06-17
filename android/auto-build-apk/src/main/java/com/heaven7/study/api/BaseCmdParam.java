package com.heaven7.study.api;

import com.heaven7.java.base.util.IOUtils;
import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.collection.VisitServices;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public abstract class BaseCmdParam {

    public void populateFromConfigFile(String configFile){
        FileReader reader = null;
        try {
            reader = new FileReader(configFile);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        List<String> lines;
        try {
            lines = IOUtils.readStringLines(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(reader);
        }
        VisitServices.from(lines).fire(new FireVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                if(s.trim().isEmpty()){
                    return false;
                }
                if(!s.startsWith("#")){
                    int idx = s.indexOf("=");
                    String key = s.substring(0, idx);
                    String value = s.substring(idx + 1);
                    applyPair(key, value);
                }
                return null;
            }
        });
    }
    protected abstract void applyPair(String key, String value);
}
