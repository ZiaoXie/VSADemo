package com.zzy.vsa.demo.util;

import android.os.Message;

public class Native {
    static {
        System.loadLibrary("JNITest");
    }
    public static native String getJniTestString();
    public static native String HelloWorld();
    // file operation
    public static native int copyFile(String src, String des);
    public static native int copyFilePart(String src, String des, int startOffset, int length);
    /*
     * 复制文件的一部分到目标文件
     * @param src 源文件
     * @param des 目标文件
     * @param startOffset 起始的offset
     * @param length 要复制的长度
     * @return 0 成功, 其他值 失败

     */
    public static native int truncateFile(String file, int length);
    /*
     * 设置文件大小
     * @param file 目标文件
     * @param length 指定大小
     * @return 0 成功, 其他值 失败
     */
    public static native int getFileSize(String file);
    /*
     * 获取文件大小
     * @param file 文件路径
     * @return >= 0 文件大小, < 0 错误码
     */

    public static void updateCopyStatus(String srcFile, int blockSize) {
        Message msg = UHandler.getHandler().obtainMessage();
        msg.what = UHandler.MSG_UPDATE_COPY_STATUS;
        msg.arg1 = blockSize;
        msg.obj = srcFile;

        UHandler.getHandler().sendMessage(msg);
    }
}