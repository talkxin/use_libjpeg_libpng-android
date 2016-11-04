LOCAL_PATH := $(call my-dir)


include $(CLEAR_VARS)
LOCAL_MODULE    := jpegcompressjni
LOCAL_SRC_FILES := \
jpegcompress.c \
jpegthumbnail.c \
pngcompress.c
LOCAL_STATIC_LIBRARIES := libjpeg
LOCAL_STATIC_LIBRARIES += libpng
LOCAL_STATIC_LIBRARIES += zlib
LOCAL_C_INCLUDES += $(LOCAL_PATH)/jpeg
LOCAL_C_INCLUDES += $(LOCAL_PATH)/png
LOCAL_C_INCLUDES += $(LOCAL_PATH)/zlib
LOCAL_LDLIBS := -ljnigraphics -llog  
include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)
LOCAL_MODULE    := libcrypto
LOCAL_SRC_FILES := libcrypto.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := libssl
LOCAL_SRC_FILES := libssl.a
include $(PREBUILT_STATIC_LIBRARY)

include $(CLEAR_VARS)
LOCAL_MODULE    := descodejni
LOCAL_SRC_FILES := descode.c
LOCAL_STATIC_LIBRARIES := libssl
LOCAL_STATIC_LIBRARIES += libcrypto
LOCAL_C_INCLUDES += $(LOCAL_PATH)/openssl
include $(BUILD_SHARED_LIBRARY)


include $(CLEAR_VARS)

include $(LOCAL_PATH)/jpeg/Android.mk  $(LOCAL_PATH)/png/Android.mk $(LOCAL_PATH)/zlib/Android.mk
