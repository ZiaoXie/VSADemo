#include "include/file_operation.h"
#include "include/common.h"
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
     if (src = NULL | des = NULL){
        return -1
     }

     int fr = open(src, O_RDONLY);
     if(fr < 0) {
        LOGD("Failed open file");
        return -2;
     }

     int fw = open(des,O_WRONLY|O_CREAT,S_IRWXU);
     if(fw < 0)
     {
     LOGD("Failed open file");
     close(fr);
     return -3
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
