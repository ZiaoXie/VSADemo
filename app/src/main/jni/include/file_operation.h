#ifndef VSADEMO_FILE_OPERATION_H
#define VSADEMO_FILE_OPERATION_H
#include <jni.h>

jint copyFile(JNIEnv * env, jclass object, jstring jsrc, jstring jdes, jlong offset, jlong length, jint choose);
jint truncateFile(JNIEnv * env, jclass object, jstring jfile, jlong length);

enum{
    COPYBY_RW = 1,
    COPYBY_SEND = 2,
    COPYBY_PRW = 3
};
#endif // VSADEMO_FILE_OPERATION_H
