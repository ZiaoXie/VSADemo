#include "include/file_operation.h"
#include "include/common.h"
#include "include/getFileSize.h"
#include <stdlib.h>
#include "include/native2java.h"
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/sendfile.h>
#include <cstring>
#include <errno.h>
#include "include/copytype.h"
//
// Created by gugan on 20-12-22.
//

  /*
   *复制文件的一部分到目标文件
   * @param src 源文件
   * @param des 目标文件
   * @param startOffset 起始的offset
   * @param length 要复制的长度
   * @return 0 成功, 其他值 失败
   */
jint copyFilePart(JNIEnv * env, jstring jsrc, jstring jdes, jint startOffset, jlong length, int fr, int fw, int size){

    LOGD("copyFilePart, fr %d, fw %d, sizebyrw %d, length %lld", fr, fw, size, length)
    lseek(fr, startOffset,SEEK_SET);
    lseek(fw, startOffset,SEEK_SET);

    char str[BUF_SIZE];
    int count = 0, sum = 0, sizepercent = size / 100;
    while (true) {
        count = read(fr,str,1024);

        LOGD("count = %d", count)
        if (count <= 0){
            LOGD("read failed: %s", strerror(errno))
            return READ_FAILED;
        }
        if(length > count){
            if (write(fw,str,count) < 0) {
                LOGD("write failed: %s", strerror(errno))
                return WRITE_FAILED;
            }
            length = length-count;
            sum = sum + count;
            if (sum >= sizepercent) {
                native2javaUpdateCopyStatus(env, jdes, size, sum);
                sum = 0;
            }
            continue;
        }
        else {
            native2javaUpdateCopyStatus(env, jdes, size, sum + length);

            if (write(fw,str,length) < 0) {
                LOGD("wrend failed: %s", strerror(errno))
                return WREND_FAILED;
            }
            break;
        }
    }
    return 0;
}


/*
调用sendfile接口复制文件的一部分
指定偏移量
指定大小
指定输入输出文件
*/
jint sendFilePart(JNIEnv * env, jstring jsrc, jstring jdes, jlong offset, jlong length, int fr, int fw, int size){

    int n = 0;
    off_t off = offset;
    n = (size / SENDBUF);
    for (int i = 0; i < n; i++) {
        if (sendfile(fw, fr, &off, SENDBUF) < 0) {
           return SEND_FAILED;
        }
    }
    if (size % SENDBUF != 0) {
        if (sendfile(fw, fr, &off, size % SENDBUF) < 0) {
            LOGD("sendfailed %s", strerror(errno))
            return SEND_FAILED;
        }
    }
    return 0;
}
jint copyByP(JNIEnv * env, jstring jsrc, jstring jdes, jlong length, jlong offset, int fr, int fw, int size){
    LOGD("copyByP")
    char buf[BUF_SIZE];
    int count = 0, sum = 0, sizepercent = size / 100;
    off_t off = (off_t)offset;
    while (true) {
        count = pread(fr, buf, BUF_SIZE, off);

        if (count <= 0) {
            LOGD("pread failed:%s", strerror(errno))
            return PREND_FAILED;
        }
        if (length > BUF_SIZE) {
            LOGD("count %d, sum %d", count, sum)
            if ( pwrite(fw, buf, BUF_SIZE, off) < 0) {
                LOGD("pwrite failed:%s", strerror(errno))
                return PWRITE_FAILED;
            }
            sum = sum + count;
            length = length - BUF_SIZE;
            off = off + BUF_SIZE;
            if (sum >= sizepercent) {
                native2javaUpdateCopyStatus(env, jdes, size, sum);
                sum = 0;
            }
            continue;
        }
        else {
            if (pwrite(fw, buf, length, off) < 0) {
                LOGD("pwrend failed:%s", strerror(errno))
                return PWREND_FAILED;
            }
            native2javaUpdateCopyStatus(env, jdes, size, sum + length);
            break;
        }
    }
    return 0;
}




