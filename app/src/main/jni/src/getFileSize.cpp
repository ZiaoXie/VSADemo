//
// Created by gugan on 20-12-4.
//
#include "include/common.h"
#include <string.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <fcntl.h>
#include <unistd.h>
jint getFileSize(JNIEnv * env, jclass object, jstring jfile){

    char *file = jni_jstring_to_cstring(env, jfile);
    struct stat st;
    stat(file,&st);
    return st.st_size;
}

