#ifndef VSADEMO_FILE_OPERATION_H
#define VSADEMO_FILE_OPERATION_H

#include "jni.h"

jint copyFile(JNIEnv * env, jclass object, jstring jsrc, jstring jdes);

#endif // VSADEMO_FILE_OPERATION_H
