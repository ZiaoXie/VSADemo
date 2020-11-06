package com.zzy.vsa.demo.appenv;

public class AppEnv {
    public final static int RequestPermissions = 0;

    public final static String PACKAGE_NAME = "com.zzy.vsa.demo";
    public final static String FileProvider = "com.zzy.vsa.demo.fileprovider"; //获取文件时使用的authority
    public final static String DOCUMENTS = "com.zzy.vsa.demo.appcase.documents";

    public final static int TAKE_PHOTO = 1;
    public final static int TAKE_VIDEO = 2;



    public final static int SYSTEM_CAMERA = 0;
    public final static int CAMERA = 1;
    public final static int CAMERA2 = 2;

    /*
    * 录音传入的方式
    * */
    public final static int SYSTEM_AUDIO = 0;
    public final static int AUDIO_RECORDER = 1;
    public final static int MEDIA_RECORDER = 2;


    /*
    * 文件操作方式
    * */
    public final static int FILE_COPY = 0;
    public final static int FILE_MOVE = 1;
    public final static int FILE_SHARE = 2;

}
