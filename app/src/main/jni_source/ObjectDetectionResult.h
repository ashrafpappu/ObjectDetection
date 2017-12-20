

#ifndef FACEDETECTIONANDROID_FACEDETECTIONRESULT_H
#define FACEDETECTIONANDROID_FACEDETECTIONRESULT_H


#include <vector>
#include "Rect.h"

class ObjectDetectionResult {
public:
    int imageRotateAngle = 0;
    std::vector<facedetection::Rect> facesRect;
    int detecttionType = -1;
    bool isSuccessFull = false;
};


#endif //FACEDETECTIONANDROID_FACEDETECTIONRESULT_H
