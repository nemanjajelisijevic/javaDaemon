package com.daemonize.imagemovers;

import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.graphics2d.images.Image;

import java.util.ArrayList;
import java.util.List;

public class AngleToSpriteArray implements AngleToImageArray {

    @FunctionalInterface
    public interface AngleToSpriteMapper {
        Image[] map(int angle);
    }

    private Image[][] roationSpriteList;
    private int noOfDirections;
    private int step;

    private volatile int pointerRot, pointerTra;

    private int currentAngleSetter;

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " noOfDirections: " + noOfDirections
                + ", Angle step size: " + step
                + ", Last angle set: " + currentAngleSetter;
    }

    @Override
    public void setCurrentAngle(int degrees) {
        if (degrees >= 360)
            degrees = degrees - 360;

        if (degrees < 0 || degrees > 359) {
            throw new IllegalArgumentException("Arg degrees must be > 0 && < 360, set: " + degrees);
        }

        pointerRot = degrees;
        pointerTra = 0;
    }

    @Override
    public int getStep() {
        return step;
    }

    public AngleToSpriteArray(int directionsNo) {

        if (directionsNo < 4 && directionsNo % 4 != 0)
            throw new IllegalArgumentException("Arg directionsNo cant be less than 4 and must be divisible by 4. Passed: " + directionsNo);

        this.roationSpriteList = new Image[360][];
        this.noOfDirections = directionsNo;
        this.step = 360 / noOfDirections;
        this.currentAngleSetter = 0;
    }

    public AngleToSpriteArray mapAllAngles(AngleToSpriteMapper mapper) {
        for (int i = 0; i < noOfDirections; i++)
            addSprite(mapper);

        for(int j = 0; j < 360; j++)
            System.out.println(DaemonUtils.tag() + "Image[" + j + "][]" + roationSpriteList[j][0]);


        return this;
    }

    public AngleToSpriteArray addSprite(AngleToSpriteMapper mapper) {

        if (currentAngleSetter > 360 - step / 2)
            return this;

        Image[] sprite = mapper.map(currentAngleSetter);//TODO

        int startIndex, endIndex;

        startIndex = step % 2 != 0 ? currentAngleSetter - step / 2 : currentAngleSetter - (step / 2 - 1);
        endIndex = currentAngleSetter + step / 2;


        if(currentAngleSetter == 0)

            for (int i = startIndex; i <= endIndex; i++) {
                if(i < 0)
                    roationSpriteList[360 + i] = sprite;
                else
                    roationSpriteList[i] = sprite;
            }

        else

            for (int i = startIndex; i <= endIndex; i++) {
                roationSpriteList[i] = sprite;
            }

        currentAngleSetter += step;
        //TODO
        return this;
    }

    @Override
    public Image getCurrent() {

        Image[] currentSprite = roationSpriteList[pointerRot];

        if (pointerTra >= currentSprite.length)
            pointerTra = 0;

        Image ret = currentSprite[pointerTra++];

        pointerTra %= currentSprite.length;

        return ret;
    }

    @Override
    public int getCurrentAngle() {
        return pointerRot;
    }

    @Override
    public Image getIncrementedByStep() {
        int diff = pointerRot + step - roationSpriteList.length;

        if(diff >= 0) {
            pointerRot = diff;
        } else {
            pointerRot += step;
        }

        pointerTra = 0;

        return roationSpriteList[pointerRot][pointerTra];
    }

    @Override
    public Image getDecrementedByStep() {
        int diff = pointerRot - step;
        if (diff < 0) {
            pointerRot = roationSpriteList.length + diff;
        } else if (diff == 0){
            pointerRot = 0;
        } else {
            pointerRot -= step;
        }

        pointerTra = 0;

        return roationSpriteList[pointerRot][pointerTra];
    }

    @Override
    public Image getByAngle(int degrees) {
        return roationSpriteList[degrees][0];
    }
}
