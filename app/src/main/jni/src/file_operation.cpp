#include "include/file_operation.h"
#include "include/common.h"
#include "include/getFileSize.h"
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

jint copyFile(JNIEnv * env, jclass object, jstring jsrc, jstring jdes) {
    LOGD("copy File")


     char *src = NULL;
     char *des = NULL ;
     src = jni_jstring_to_cstring(env, jsrc);
     des = jni_jstring_to_cstring(env, jdes);
     if (NULL == src ){
        return -1;
     }
     if (NULL == des){
        free(src);
        return -1;
     }

     int fr = open(src, O_RDONLY);
     if (fr < 0) {
        LOGD("Failed open file");
        return -2;
     }

     int fw = open(des, O_WRONLY | O_CREAT, S_IRWXU);
     if (fw < 0)
     {
     LOGD("Failed open file");
     close(fr);
     return -3;
     }

     char str[1024];
     int count=0;
     while (count = read(fr,str,1024)) {
        write(fw,str,count);
     }

     close(fr);
     close(fw);
     free(src);
     free(des);
    return 0;
}

jint copyFilePart(JNIEnv * env, jclass object, jstring jsrc,jstring jdes,jint startOffset,jint length){
    /*
     *复制文件的一部分到目标文件
     * @param src 源文件
     * @param des 目标文件
     * @param startOffset 起始的offset
     * @param length 要复制的长度
     * @return 0 成功, 其他值 失败
     */
     char *src,*des;
     src = jni_jstring_to_cstring(env, jsrc);
     des = jni_jstring_to_cstring(env, jdes);
     if (NULL == src ){
        return -1;
     }
     if (NULL == des){
        free(src);
        return -1;
     }

     int fr = open(src, O_RDONLY);
     if (fr < 0) {
        LOGD("fr failed open");
        return -2;
     }

     int fw = open(des, O_WRONLY | O_CREAT);
     if (fw < 0)
     {
        LOGD("fw failed open ");
     close(fr);
     return -3;
     }

     lseek(fr, startOffset,SEEK_SET);
     lseek(fw, startOffset,SEEK_SET);
     char str[1024];
     int count=0;


     LOGD("copyFilePart %s -> %s, %d %d", src, des, startOffset,length);
    while(true){
        count = read(fr,str,1024);
        LOGD("1: %d %d", count,length);

        if(count <= 0){
            break;
        }

        if(length > count){
            write(fw,str,count);
            length = length-count;
            LOGD("2: %d %d", count,length);
            continue;
       }
       else{
        write(fw,str,length);
        LOGD("3: %d %d", count,length);
        break;
       }
    }
     close(fr);
     close(fw);
     free(src);
     free(des);
    return 0;



}

jint truncateFile(JNIEnv * env, jclass object, jstring jfile,jint length){

    /*
     * 设置文件大小
     * @param file 目标文件
     * @param length 指定大小
     * @return 0 成功, 其他值 失败
     */
     char *file = jni_jstring_to_cstring(env,jfile);
     int f=open(file,O_WRONLY|O_CREAT);
     truncate (file,length);
     int a =  truncate (file,length);
     return a;
}
