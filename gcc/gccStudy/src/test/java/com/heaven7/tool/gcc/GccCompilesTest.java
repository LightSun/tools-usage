package com.heaven7.tool.gcc;

import java.util.Arrays;

public final class GccCompilesTest {

    public static void main(String[] args) throws Exception {
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
}
