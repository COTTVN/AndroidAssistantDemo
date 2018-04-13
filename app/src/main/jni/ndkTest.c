#include <jni.h>

JNIEXPORT jstring
JNICALL
Java_net_java_utils_ndkTest_getTalk(JNIEnv *env, jobject instance) {
    // TODO
    jstring returnValue="你好，啊！";
    return (*env)->NewStringUTF(env, returnValue);
}