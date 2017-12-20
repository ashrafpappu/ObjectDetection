
#include <android/log.h>

#ifndef FACEDETECTIONANDROID_FACEDETECTIONLOGGER_H
#define FACEDETECTIONANDROID_FACEDETECTIONLOGGER_H

//#define DEBUG

#ifdef DEBUG

#define LOGD(...) __android_log_print(ANDROID_LOG_DEBUG,"FDLogger",__VA_ARGS__)
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO,"FDLogger",__VA_ARGS__)

#else

#define LOGD(...)
#define LOGI(...)

#endif

#endif //FACEDETECTIONANDROID_FACEDETECTIONLOGGER_H





//include $(CLEAR_VARS)
//LOCAL_MODULE := openCV
//LOCAL_SRC_FILES := $(LOCAL_PATH)/opencv/libs/$(TARGET_ARCH_ABI)/libopenCV.a
//LOCAL_STATIC_LIBRARIES := tbb
//include $(PREBUILT_STATIC_LIBRARY)
//
//include $(CLEAR_VARS)
//LOCAL_MODULE := tbb
//LOCAL_SRC_FILES := $(LOCAL_PATH)/3rdparty/libs/$(TARGET_ARCH_ABI)/libtbb.a
//LOCAL_EXPORT_C_INCLUDES := $(LOCAL_PATH)/3rdparty/include
//include $(PREBUILT_STATIC_LIBRARY)