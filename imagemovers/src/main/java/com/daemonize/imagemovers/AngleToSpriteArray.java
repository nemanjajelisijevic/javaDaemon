package com.daemonize.imagemovers;

import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.graphics2d.images.Image;

public class AngleToSpriteArray implements AngleToImageArray {

    @FunctionalInterface
    public interface AngleToSpriteMapper {
        Image[] map(int angle);
    }

    private Image[][] rotationSpriteList;
    private final int noOfDirections;
    private final int step;

    private volatile int pointerRot, pointerTra;

    private int currentAngleSetter;

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " noOfDirections: " + noOfDirections
                + ", Angle step size: " + step
                + ", Last angle set: " + currentAngleSetter;
    }

    @Override
    public synchronized void setCurrentAngle(int degrees) {

        if (degrees >= 360)
            degrees = degrees - 360;

        if (degrees < 0 || degrees > 359)
            throw new IllegalArgumentException("Arg degrees must be > 0 && < 360, set: " + degrees);

        pointerRot = degrees;
        pointerTra = 0;
    }

    @Override
    public int getStep() {
        return step;
    }

    public AngleToSpriteArray(int directionsNo) {
        this(new Image[360][], directionsNo);
    }

    private AngleToSpriteArray(Image[][] rotationSpriteList, int directionsNo) {

        if (directionsNo < 4 && directionsNo % 4 != 0)
            throw new IllegalArgumentException("Arg directionsNo cant be less than 4 and must be divisible by 4. Passed: " + directionsNo);

//        if (this.rotationSpriteList == null) {
        System.err.println(DaemonUtils.timedTag() + "[AngleToSpriteArray] Rotation List Initialization!");
        this.rotationSpriteList = rotationSpriteList;
        //      }
        this.noOfDirections = directionsNo;
        this.step = 360 / noOfDirections;
        this.currentAngleSetter = 0;
        this.pointerRot = 0;
        this.pointerTra = 0;
    }

    public AngleToSpriteArray mapAllAngles(AngleToSpriteMapper mapper) {
        for (int i = 0; i < noOfDirections; i++)
            addSprite(mapper);

//        for(int j = 0; j < 360; j++) {
//            for (int k = 0; k < rotationSpriteList[j].length; k++)
//                System.out.print(DaemonUtils.timedTag() + "Image[" + j + "][" + k + "]" + rotationSpriteList[j][k]);
//            System.out.println("");
//        }

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
                    rotationSpriteList[360 + i] = sprite;
                else
                    rotationSpriteList[i] = sprite;
            }

        else
            for (int i = startIndex; i <= endIndex; i++)
                rotationSpriteList[i] = sprite;

        currentAngleSetter += step;
        return this;
    }

    @Override
    public synchronized Image getCurrent() {

        Image[] currentSprite = rotationSpriteList[pointerRot];

        if (pointerTra > currentSprite.length - 1)
            pointerTra = 0;

        Image ret = currentSprite[pointerTra++];
        pointerTra = pointerTra % currentSprite.length;

        return ret;
    }

    @Override
    public synchronized int getCurrentAngle() {
        return pointerRot;
    }

    @Override
    public synchronized Image getIncrementedByStep() {

        int diff = pointerRot + step - 360;
        if(diff >= 0) {
            pointerRot = diff;
        } else {
            pointerRot += step;
        }

        pointerTra = 0;

        return rotationSpriteList[pointerRot][pointerTra];
    }

    @Override
    public synchronized Image getDecrementedByStep() {

        int diff = pointerRot - step;
        if (diff < 0) {
            pointerRot = 360 + diff;
        } else if (diff == 0){
            pointerRot = 0;
        } else {
            pointerRot -= step;
        }

        pointerTra = 0;

        return rotationSpriteList[pointerRot][pointerTra];
    }

    public synchronized Image[] getSpriteByAngle(int angle) {
        return rotationSpriteList[angle];
    }

    @Override
    public Image getByAngle(int degrees) {
        return rotationSpriteList[degrees][0];
    }

    @Override
    public AngleToSpriteArray clone() throws CloneNotSupportedException {
        return new AngleToSpriteArray(this.rotationSpriteList, this.noOfDirections);
    }
}
