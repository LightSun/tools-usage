package com.heaven7.study;

import org.junit.Test;

public class CmdBuilderTest {

    @Test
    public void test1(){
        String proxy = "127.0.0.1:1080";
        String[] cmds = new CmdBuilder().cmd("cmd set http_proxy=http://" + proxy)
                .and()
                .cmd("https_proxy=http://" + proxy)
                .toCmd();

        CmdHelper cmd = new CmdHelper(cmds);
        System.out.println(" >>> start execute cmd: " + cmd.getCmdActually());
        if(!cmd.execute(new CmdHelper.InhertIoCallback())){
            System.err.println(">>> execute faile.");
        }
    }
}
