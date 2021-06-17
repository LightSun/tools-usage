package com.heaven7.study.api;

import com.heaven7.java.visitor.FireVisitor;
import com.heaven7.java.visitor.collection.VisitServices;
import com.heaven7.study.CmdHelper;

import java.util.List;

public abstract class BaseCmdExecutor{

    private final String input;

    public BaseCmdExecutor(String input) {
        this.input = input;
    }
    public String getInput() {
        return input;
    }
    public void execute(){
        execute(input);
    }
    public void execute(List<String> inputs){
        VisitServices.from(inputs).fire(new FireVisitor<String>() {
            @Override
            public Boolean visit(String s, Object param) {
                execute(s);
                return null;
            }
        });
    }
    public abstract void execute(String input);

    public void doExecuteCmd(String[] str){
        CmdHelper cmd = new CmdHelper(str);
        System.out.println(" >>> start execute cmd: " + cmd.getCmdActually());
        onPreExecuteCmd(cmd.getCmdActually());
        if(!cmd.execute(new CmdHelper.InhertIoCallback())){
            System.err.println(">>> execute failed.");
        }else{
            System.err.println(">>> execute success.");
        }
    }

    protected void onPreExecuteCmd(String cmd){

    }
}
