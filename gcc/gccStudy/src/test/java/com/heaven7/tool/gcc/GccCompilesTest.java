package com.heaven7.tool.gcc;

import org.junit.Test;

import java.util.Arrays;

public final class GccCompilesTest {

    @Test
    public void testBison(){
        String[] files = {
                "lex.yy.c",
                "calc.c",
                "bison.tab.c"
        };
        String dir = "D:\\study\\tools\\GnuWin32\\study";
        GccCompiles gcc = new GccCompiles(false, dir, Arrays.asList(files));
        gcc.setOutDir(dir);
        gcc.compileExecutable("study_calculator");
    }
    //gcc main.c syntax.tab.c -lfl -ly -o parser
    @Test
    public void testBisonDirect(){
        String[] files = {
                "lex.yy.c",
                "calc.c",
                "bison.tab.c"
        };
        String dir = "D:\\study\\tools\\GnuWin32\\study";
        GccCompiles gcc = new GccCompiles(false, dir, Arrays.asList(files));
        gcc.setOutDir(dir);
        gcc.compileBisonDirectly("study_calculator");
    }
}
