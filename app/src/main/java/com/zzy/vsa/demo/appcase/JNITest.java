package com.zzy.vsa.demo.appcase;

public class JNITest {
    static {
        System.loadLibrary("JNITest");
    }
    public static native String getJniTestString();
    public static native String HelloWorld();
}
