LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
#编译生成的文件的类库叫什么名字，就是刚才我前面提到的名字
LOCAL_MODULE    := JNITest
LOCAL_C_INCLUDES    += $(LOCAL_PATH)
#要编译的c文件，上面咱们刚创建的
LOCAL_SRC_FILES := JNITest.c
include $(BUILD_SHARED_LIBRARY)