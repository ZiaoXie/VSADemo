#include "include/native2java.h"
#include "include/common.h"

jclass gNativeClass = NULL;
int native2javaInit(JNIEnv * env) {
    if (NULL != gNativeClass) return 0;

    jclass clazz = env->FindClass("com/zzy/vsa/demo/util/Native");
    if (NULL == clazz) return -1;

    gNativeClass = (jclass) env->NewGlobalRef(clazz);
    if (NULL == gNativeClass) return -2;

    return 0;
}

void native2javaUpdateCopyStatus(JNIEnv * env, jstring src, jint fileSize, jint blockSize) {
    if (NULL == gNativeClass) return;

    jmethodID method = env->GetStaticMethodID(gNativeClass, "updateCopyStatus", "(Ljava/lang/String;II)V");
    if (NULL == method) return;

    env->CallStaticVoidMethod(gNativeClass, method, src, fileSize, blockSize);
}