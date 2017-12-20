

#ifndef FACEDETECTIONANDROID_TRAINABLEDETECTOR_H
#define FACEDETECTIONANDROID_TRAINABLEDETECTOR_H

#include <string>

class TrainAbleDetector {
public:
    virtual bool loadTrainData(std::string trainDataFilePath) = 0;
    virtual ~TrainAbleDetector();
};


#endif //FACEDETECTIONANDROID_TRAINABLEDETECTOR_H
