//
// Created by xieziao on 20-9-2.
//
#include "jni.h"
#include "JNITest.h"
JNIEXPORT jstring JNICALL Java_com_zzy_vsa_demo_appcase_JNITest_getJniTestString
  (JNIEnv * env, jclass object){
    return (*env)->NewStringUTF(env,"测试 jni");
  }

JNIEXPORT jstring JNICALL Java_com_zzy_vsa_demo_appcase_JNITest_HelloWorld
  (JNIEnv * env, jclass object){
    return (*env)->NewStringUTF(env,"HelloWorld");
  }

