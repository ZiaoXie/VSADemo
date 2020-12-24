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
#include <cstdlib>
#include <include/getFileSize.h>

jint getFileSize(JNIEnv * env, jclass object, jstring jfile){

    char *file = NULL;
    file = jni_jstring_to_cstring(env, jfile);
    if (NULL == file) {
        return GETFAILED;
    }
    struct stat st;
    stat(file,&st);
    free(file);
    return st.st_size;
}

