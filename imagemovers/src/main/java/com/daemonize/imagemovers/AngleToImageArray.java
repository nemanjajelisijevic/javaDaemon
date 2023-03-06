package com.daemonize.imagemovers;

import com.daemonize.graphics2d.images.Image;

public interface AngleToImageArray {
    void setCurrentAngle(int degrees);
    int getStep();
    Image getCurrent();
    int getCurrentAngle();
    Image getIncrementedByStep();
    Image getDecrementedByStep();
    Image getByAngle(int degrees);
}
