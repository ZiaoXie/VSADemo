//
// Created by xieziao on 20-9-2.
//
#include "include/native.h"
#include "include/common.h"
#include "include/file_operation.h"
#include "include/getFileSize.h"
#include "include/native2java.h"
#include "include/copytype.h"
static JNINativeMethod g_methods[] = {
        {"getJniTestString", "()Ljava/lang/String;", (void *) getJniTestString},
        {"HelloWorld", "()Ljava/lang/String;", (void *) HelloWorld},
        {"copyFile", "(Ljava/lang/String;Ljava/lang/String;JJI)I", (void *) copyFile},
        {"truncateFile","(Ljava/lang/String;J)I",(void *) truncateFile},
        {"getFileSize","(Ljava/lang/String;)I",(void *) getFileSize},

};

/*
 * @ brief: JNI_OnLoad which will be called by JVM when java perform System.loadLibrary.
 * */
jint JNI_OnLoad(JavaVM *vm, void *reserved) {
    if (NULL == vm) {
        return -1;
    }

    /* Check GetEnv return value */
    JNIEnv *env = NULL;
    JNIEnv **ppenv = &env;
    int ret = vm->GetEnv((void **) ppenv, JNI_VERSION_1_6);
    if (ret != JNI_OK) {
        return -1;
    }

    if (NULL == env) {
        return -1;
    }

    jclass clazz = env->FindClass("com/zzy/vsa/demo/util/Native");
    if (clazz == NULL) {
        return -1;
    }

    ret = env->RegisterNatives(clazz, g_methods, NELEM(g_methods));
    if (ret < 0) {
        return -1;
    }

    native2javaInit(env);
    return JNI_VERSION_1_6;
}



jstring getJniTestString(JNIEnv * env, jclass object) {
    LOGD("native getJniTestString")
    return env->NewStringUTF("测试 jni");
}

jstring HelloWorld(JNIEnv * env, jclass object) {
    LOGD("native HelloWorld")
    return env->NewStringUTF("HelloWorld");
}
