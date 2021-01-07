package com.heaven7.tool.gcc;

//预处理，编译，汇编, 链接
public interface IGccCompile {

    String preProcess();

    String compile();

    String asm();

    String link();

    String start();
}
