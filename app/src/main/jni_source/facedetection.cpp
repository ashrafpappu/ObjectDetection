//
// Created by Pappu on 11/7/17.
//

#include "pappu_com_objectdetection_java_jni_Facedetection.h"

#include <chrono>
#include "open_cv_facedetector.h"
#include <thread>
#include "FaceDetectionLogger.h"
#include <opencv2/opencv.hpp>
#include <list>

using namespace std;
using namespace cv;



static jclass java_util_ArrayList;
static jmethodID java_util_ArrayList_;
jmethodID java_util_ArrayList_add;
static thread_local JNIEnv* env1;

std::vector<ObjectDetectionResult> objectDetectionVector;

std::vector<OpenCvFaceDetector *> openCvFaceDetectors;
typedef struct _thread_data_t {
    unsigned char *imageBufSource;
    unsigned char *imageBufDestination;
    unsigned long rowstart;
    unsigned long imageWidth;
    unsigned long imageHeight;
    unsigned long rowinterval;
    int index;
} thread_data_t;


bool createNativeShapeDetector(JNIEnv *env, jobject instance, jintArray jids, jobjectArray openCvXmls) {
    int count = env->GetArrayLength(jids);
    LOGI("Number of detector : %d", count);
    jint * ids = env->GetIntArrayElements(jids, 0);
    bool flag = true;
    openCvFaceDetectors.clear();
    for (int i = 0; i < count; ++i) {
        jstring jfilePath = (jstring) env->GetObjectArrayElement(openCvXmls, i);
        const char *filepath = env->GetStringUTFChars(jfilePath, 0);
        openCvFaceDetectors.push_back(new OpenCvFaceDetector(ids[i]));
        TrainAbleDetector *trainAbleDetector = dynamic_cast<TrainAbleDetector *>(openCvFaceDetectors[i]);
        flag = trainAbleDetector->loadTrainData(filepath);
        LOGI("%s load Successfully : %d", filepath, flag);
        env->ReleaseStringUTFChars(jfilePath, filepath);
    }
    return flag;
}


void *rotateImageData(void *arg) {

    thread_data_t *data = (thread_data_t *) arg;
    int height = data->rowstart + data->rowinterval;

    for (int i = data->rowstart; i < height; i++) {
        for (int j = 0; j < data->imageWidth; j++) {
            data->imageBufDestination[data->index++] = data->imageBufSource[
                    ((data->imageWidth - 1) - j) * data->imageHeight +
                    (data->imageHeight - i - 1)];
        }
    }

    pthread_exit(NULL);

}




JNIEXPORT void JNICALL Java_pappu_com_objectdetection_java_1jni_Facedetection_initialize
        (JNIEnv *, jobject){
}

JNIEXPORT jboolean JNICALL Java_pappu_com_objectdetection_java_1jni_Facedetection_deserialize
        (JNIEnv *env, jobject object, jintArray ids, jobjectArray openCvXmls){
    return createNativeShapeDetector(env,object, ids, openCvXmls);

}


void init() {
    java_util_ArrayList      = static_cast<jclass>(env1->NewGlobalRef(env1->FindClass("java/util/ArrayList")));
    java_util_ArrayList_     = env1->GetMethodID(java_util_ArrayList, "<init>", "(I)V");
    java_util_ArrayList_add  = env1->GetMethodID(java_util_ArrayList, "add", "(Ljava/lang/Object;)Z");
}


jobject cpp2java(std::list<jlong* > &rectface1) {
    jobject result = env1->NewObject(java_util_ArrayList, java_util_ArrayList_, rectface1.size());

    LOGI("..............check............... second %lld",rectface1.front()[0]);
    for (jlong* rect: rectface1) {

        jlongArray element = env1->NewLongArray(4);
        env1->SetLongArrayRegion(element, 0, 4, rect);
        env1->CallBooleanMethod(result, java_util_ArrayList_add, element);
        env1->DeleteLocalRef(element);
        delete [] rect;
    }
    return result;
}


JNIEXPORT jobject JNICALL Java_pappu_com_objectdetection_java_1jni_Facedetection_getRectangle
        (JNIEnv *env, jobject obj,jint detectorId){

    env1 = env;
    std::list<jlong* > rectface;

    for(int i = 0;i<objectDetectionVector.size();i++){
        if(objectDetectionVector[i].isSuccessFull== true && objectDetectionVector[i].detecttionType==detectorId){
            for(int j=0;j<objectDetectionVector[i].facesRect.size();j++){
                jlong *cRect = new jlong[4];
                cRect[0] = objectDetectionVector[i].facesRect[j].left;
                cRect[1] = objectDetectionVector[i].facesRect[j].top;
                cRect[2] = objectDetectionVector[i].facesRect[j].right;
                cRect[3] = objectDetectionVector[i].facesRect[j].bottom;
                rectface.push_back(cRect);
            }
            break;

        }
    }


    LOGI("..............check face............... %lld",rectface.front()[0]);
    init();
    return cpp2java(rectface);

}
JNIEXPORT jintArray JNICALL Java_pappu_com_objectdetection_java_1jni_Facedetection_faceDetect
        (JNIEnv *env, jobject object, jbyteArray image, jlong imageWidth, jlong imageHeight, jint orientation,jint coreNumber,jboolean applyHistogram){

    objectDetectionVector.clear();
    long height,width;
    int len = imageHeight * imageWidth;
    unsigned char *imageBuf = new unsigned char[len];

    if (orientation == 1) {
        height = imageWidth;
        width = imageHeight;

//        unsigned char *imageBufSource = new unsigned char[len];
        env->GetByteArrayRegion(image, 0, len, reinterpret_cast<jbyte *>(imageBuf));
#ifdef DEBUG
        auto start = std::chrono::high_resolution_clock::now();
#endif

//        unsigned long rowstart = 0,rowinterval = height/coreNumber,index=0,indexinterval = (width*height)/coreNumber;
//
//        pthread_t thr[coreNumber];
//        int i;
//        thread_data_t thr_data[coreNumber] ;
//
//        for (i = 0; i < coreNumber; i++) {
//            thr_data[i].imageBufDestination = imageBuf;
//            thr_data[i].imageBufSource = imageBufSource;
//            thr_data[i].imageWidth = width;
//            thr_data[i].imageHeight = height;
//            thr_data[i].rowinterval = rowinterval;
//            if(i == 0){
//                thr_data[i].rowstart = rowstart;
//                thr_data[i].index = index;
//            }
//            else{
//
//                thr_data[i].rowstart = rowstart+rowinterval;
//                rowstart = thr_data[i].rowstart;
//                thr_data[i].index = index+indexinterval;
//                index =  thr_data[i].index;
//            }
//            pthread_create(&thr[i],NULL,rotateImageData,(void *) &thr_data[i]);
//        }
//        for (i = 0; i < coreNumber; ++i) {
//            pthread_join(thr[i], NULL);
//        }
//
//        delete[] imageBufSource;
    }
    else {

        height = imageHeight;
        width = imageWidth;
        env->GetByteArrayRegion(image, 0, len, reinterpret_cast<jbyte *>(imageBuf));
    }


    bool  succes = false;
    jint cRect[4];
    int inc = 0;

    auto start = std::chrono::high_resolution_clock::now();
    for(int i=0;i<openCvFaceDetectors.size();i++){
        objectDetectionVector.push_back(openCvFaceDetectors[i]->detectFaces(imageBuf,width,height,applyHistogram));
        if(objectDetectionVector[i].isSuccessFull ==  true){
            LOGI("..............success............... ");
            succes = true;
            cRect[inc++] = objectDetectionVector[i].detecttionType;
        }
    }


    if(succes == false){

        delete[] imageBuf;
        return nullptr;
    }
    auto end = std::chrono::high_resolution_clock::now();
    auto duration = std::chrono::duration_cast<std::chrono::milliseconds>(end - start);
    // LOGI("..............detect............... %lld",duration.count());


    jintArray rect = env->NewIntArray(inc);
    env->SetIntArrayRegion(rect, 0, inc, cRect);


    return rect;
}