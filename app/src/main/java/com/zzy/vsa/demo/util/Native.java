package com.zzy.vsa.demo.util;


import android.os.Message;
import android.util.Log;

import com.zzy.vsa.demo.view.copy.CopyActivity;

import java.util.HashMap;


public class Native {
    static {
        System.loadLibrary("JNITest");
    }
    public static native String getJniTestString();
    public static native String HelloWorld();


    public static native int copyFile(String src, String des, long offset, long length, int choose);

    /*
     * 设置文件大小
     * @param file 目标文件
     * @param length 指定大小
     * @return 0 成功, 其他值 失败
     */
    public static native int truncateFile(String file, long length);

    /*
     * 获取文件大小
     * @param file 文件路径
     * @return >= 0 文件大小, < 0 错误码
     */
    public static native int getFileSize(String file);


    public static void updateCopyStatus(String srcFile, int fileSize, int count) {

        Message msg = new Message();
        msg.what = UHandler.MSG_UPDATE_COPY_STATUS;
        msg.arg1 = fileSize;
        msg.arg2 = count;

        UHandler.getHandler().sendMessage(msg);
    }

}