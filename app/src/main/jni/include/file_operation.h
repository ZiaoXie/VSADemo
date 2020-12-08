#ifndef VSADEMO_FILE_OPERATION_H
#define VSADEMO_FILE_OPERATION_H

#include "jni.h"

jint copyFile(JNIEnv * env, jclass object, jstring jsrc, jstring jdes);
jint copyFilePart(JNIEnv * env, jclass object, jstring jsrc,jstring jdes,jint startOffset,jint length);
jint truncateFile(JNIEnv * env, jclass object, jstring jfile,jint length);

#endif // VSADEMO_FILE_OPERATION_H
