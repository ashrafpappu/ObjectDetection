

#ifndef FACEDETECTIONANDROID_RECT_H
#define FACEDETECTIONANDROID_RECT_H

namespace facedetection {
class Rect {
public:
    Rect(){}
    Rect(int left, int top, int right, int bottom) : left{left}, top{top},right{right}, bottom{bottom} {}
    int left;
    int top;
    int right;
    int bottom;
};
}


#endif //FACEDETECTIONANDROID_RECT_H
