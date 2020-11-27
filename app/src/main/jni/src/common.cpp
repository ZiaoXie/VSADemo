#include "include/common.h"
#include <string.h>

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

