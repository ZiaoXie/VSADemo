//
// Created by xieziao on 20-9-2.
//

#ifndef ONEAPPLICATION_COM_ZZY_VSA_DEMO_APPCASE_JNITEST_H
#define ONEAPPLICATION_COM_ZZY_VSA_DEMO_APPCASE_JNITEST_H
#ifdef __cplusplus
extern “C” {
#endif

    JNIEXPORT jstring JNICALL Java_com_zzy_vsa_demo_appcase_JNITest_getJniTestString
    (JNIEnv *, jobject);

    JNIEXPORT jstring JNICALL Java_com_zzy_vsa_demo_appcase_JNITest_HelloWorld
        (JNIEnv *, jobject);

#ifdef __cplusplus
}
#endif

#endif //ONEAPPLICATION_COM_EXAMPLE_ONEAPPLICATION_JNITEST_H

