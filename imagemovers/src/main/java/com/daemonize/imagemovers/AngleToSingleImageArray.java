package com.daemonize.imagemovers;

import com.daemonize.graphics2d.images.Image;

public class AngleToSingleImageArray implements AngleToImageArray {

    private Image[] array = new Image[360];
    private int step;
    private int pointer = 0;

    @Override
    public void setCurrentAngle(int degrees) {

        if (degrees >= 360)
            degrees = degrees - 360;

        if (degrees < 0 || degrees > 359) {
            throw new IllegalArgumentException("Arg degrees must be > 0 && < 360, set: " + degrees);
        }
        pointer = degrees;
    }

    @Override
    public int getStep() {
        return step;
    }

    public AngleToSingleImageArray(Image[] sprite) {
        this(sprite, 360 / sprite.length);
    }

    public AngleToSingleImageArray(Image[] sprite, int step) {
        this.step = step;
        for (int i = 0; i < array.length; ++i) {
            int it = i / step;
            if (it < sprite.length)
                array[i] = sprite[it];
            else
                break;
        }
    }

    @Override
    public Image getCurrent() {
        return array[pointer];
    }

    @Override
    public int getCurrentAngle() {
        return pointer;
    }

    @Override
    public Image getIncrementedByStep() {
        int diff = pointer + step - array.length;
        if(diff >= 0) {
            pointer = diff;
        } else {
            pointer += step;
        }
        return array[pointer];
    }

    @Override
    public Image getDecrementedByStep() {
        int diff = pointer - step;
        if (diff < 0) {
            pointer = array.length + diff;
        } else if (diff == 0){
            pointer = 0;
        } else {
            pointer -= step;
        }
        return array[pointer];
    }

    @Override
    public Image getByAngle(int degrees) {
        return array[degrees];
    }

}
