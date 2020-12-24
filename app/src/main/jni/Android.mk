LOCAL_PATH := $(call my-dir)

LOCAL_PROJECTS_SRC_FILES := \
        src/common.cpp \
        src/native.cpp \
        src/file_operation.cpp \
        src/getFileSize.cpp \
        src/native2java.cpp \
        src/copytype.cpp\

########################### build for shared library
include $(CLEAR_VARS)
LOCAL_MODULE        := JNITest
LOCAL_SRC_FILES     += $(LOCAL_PROJECTS_SRC_FILES)
LOCAL_C_INCLUDES    += $(LOCAL_PATH)/inc
LOCAL_CFLAGS        := -Os -fvisibility=hidden
LOCAL_LDLIBS        += -llog -lz
ifneq ($(TARGET_ARCH),arm64)
LOCAL_CFLAGS        += -DHAVE_PTHREADS
endif
include $(BUILD_SHARED_LIBRARY)