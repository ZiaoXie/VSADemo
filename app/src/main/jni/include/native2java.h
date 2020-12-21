#ifndef VSADEMO_NATIVE2JAVA_H
#define VSADEMO_NATIVE2JAVA_H
#include <jni.h>

int native2javaInit(JNIEnv * env);
void native2javaUpdateCopyStatus(JNIEnv * env, jstring src, jint blockSize);

#endif //VSADEMO_NATIVE2JAVA_H
