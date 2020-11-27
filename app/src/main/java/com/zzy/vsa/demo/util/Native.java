package com.zzy.vsa.demo.util;

public class Native {
    static {
        System.loadLibrary("JNITest");
    }
    public static native String getJniTestString();
    public static native String HelloWorld();

    // file operation
    public static native int copyFile(String src, String des);
}