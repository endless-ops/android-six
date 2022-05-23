#include <jni.h>
#include "cn_dreamchase_android_six_JNITest.h"
#ifdef __cplusplus // 最后有这个，否则可能会被编译器改了函数名
extern "C" {
#endif

    JNIEXPORT jint JNICALL Java_cn_dreamchase_android_six_JNITest_plus(JNIEnv * env,jclass cla,jint x,jint y) {
        return x + y;
    }
#ifdef __cplusplus
}
#endif