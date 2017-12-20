
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_MODULE := facedetection

#LOCAL_C_INCLUDES :=  $(LOCAL_PATH)/opencv/include

LOCAL_C_INCLUDES :=  /Users/pappu/mycomputer/opencv/OpenCV-android-sdk-2/sdk/native/jni/include

LOCAL_LDLIBS += -llog -ldl -lz -latomic
LOCAL_LDFLAGS += -ljnigraphics
# import openCV
LOCAL_STATIC_LIBRARIES += openCV

LOCAL_SRC_FILES := $(LOCAL_PATH)/facedetection.cpp\

LOCAL_EXPORT_C_INCLUDES := $(LOCAL_C_INCLUDES)
include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)
OpenCV_INSTALL_MODULES := on
OPENCV_CAMERA_MODULES := off
OPENCV_LIB_TYPE := STATIC
include  /Users/pappu/mycomputer/opencv/OpenCV-android-sdk-2/sdk/native/jni/OpenCV.mk

LOCAL_SRC_FILES = test.cpp

LOCAL_MODULE := openCV
include $(BUILD_STATIC_LIBRARY)







