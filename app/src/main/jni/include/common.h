#ifndef VSADEMO_COMMON_H
#define VSADEMO_COMMON_H

#include <stddef.h>
#include <android/log.h>
#include "jni.h"

#ifndef NELEM
#define NELEM(x) ((int) (sizeof(x) / sizeof((x)[0])))
#endif// end of NELEM

#define TAG "vsaDemo"
#define LOGD(...) {__android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__);}

char* jni_jstring_to_cstring(JNIEnv* env, jstring jstr);
jint getFileSize_t(char* file);
#endif //VSADEMO_COMMON_H
