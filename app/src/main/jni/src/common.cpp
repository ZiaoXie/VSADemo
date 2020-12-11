#include "include/common.h"
#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>

// jstring to char*
char* jni_jstring_to_cstring(JNIEnv* env, jstring jstr) {
    char* utf = NULL;
    if (env && jstr) {
        const char* str = env->GetStringUTFChars(jstr, NULL);
        if (str) {
            utf = strdup(str);
            env->ReleaseStringUTFChars(jstr, str);
        }
    }
    return utf;
}

jint getFileSize_t(char * file){
    struct stat st;
   int a = stat(file,&st);

    if(a == 0){
    return st.st_size;
    }
    else if(-1 ==a ){
    return -1;
    }
}