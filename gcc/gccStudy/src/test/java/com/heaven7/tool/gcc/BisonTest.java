package com.heaven7.tool.gcc;

import org.junit.Test;

public final class BisonTest {

    @Test
    public void test1(){
        String flexFile = "D:\\study\\tools\\GnuWin32\\study/flex.l";
        String bisonFile = "D:\\study\\tools\\GnuWin32\\study/bison.y";
        new Bison(flexFile, bisonFile).process();
    }
}
