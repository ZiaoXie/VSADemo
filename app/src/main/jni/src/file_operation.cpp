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
#include "include/copytype.h"

jint copyFile(JNIEnv * env, jclass object, jstring jsrc, jstring jdes, jlong offset, jlong length, jint choose) {
    LOGD("copyFile: %d", choose)
    LOGD("offset =%lld", offset)
    char *src = NULL, *des = NULL;
    int fr = -1, fw = -1, size = 0;
    do {
        src = jni_jstring_to_cstring(env, jsrc);
        if (NULL == src) {
            LOGD("conversion src failed")
            break;
        }
        des = jni_jstring_to_cstring(env, jdes);
        if (NULL == des) {
            LOGD("conversion src failed")
            break;
        }

        size = getFileSize_t(src);
        LOGD("src ==%s, des == %s, size == %d", src, des, size)

        fr = open(src, O_RDONLY);
        LOGD("fr ==%d", fr)
        if (fr < 0) {
            LOGD("open fr failed")
            break;
        }
        fw = open(des, O_WRONLY | O_CREAT);
        LOGD("fw ==%d", fw)
        if (fw < 0) {
            LOGD("open fw failed")
            break;
        }

        switch (choose) {
            case COPYBY_RW:
                copyFilePart(env, jsrc, jdes, offset, length, fr, fw, size);
                break;
            case COPYBY_SEND:
                sendFilePart(env, jsrc, jdes, offset, length, fr, fw, size);
                break;
            case COPYBY_PRW:
                copyByP(env, jsrc, jdes, length, offset, fr, fw, size);
                break;
            default:
                break;
        }
    } while (false);

    MEM_FREE(src);
    MEM_FREE(des);
    CLOSE_FILE(fr);
    CLOSE_FILE(fw);
    return 0;
}

    /*
     * 设置文件大小
     * @param file 目标文件
     * @param length 指定大小
     * @return 0 成功, 其他值 失败
     */
jint truncateFile(JNIEnv * env, jclass object, jstring jfile,jlong length){


     char *file = jni_jstring_to_cstring(env,jfile);
     int f=open(file,O_WRONLY|O_CREAT);
     truncate (file,length);
     int a =  truncate (file,length);
     return a;
}

















































































