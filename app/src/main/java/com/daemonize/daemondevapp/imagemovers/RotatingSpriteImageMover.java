package com.daemonize.daemondevapp.imagemovers;

import android.util.Log;

import com.daemonize.daemondevapp.AngleToBitmapArray;
import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonprocessor.annotations.CallingThread;

import java.util.Arrays;

public class RotatingSpriteImageMover extends CachedArraySpriteImageMover {

    //private int currentAngle;
    private AngleToBitmapArray spriteBuffer;
    private Image[] currentRotationSprite;
    private volatile int size;

    public synchronized void setRotationSprite(Image[] rotationSprite) {
        //setSprite(Arrays.copyOf(rotationSprite, 1));
        int step = 360 / rotationSprite.length;
        this.spriteBuffer = new AngleToBitmapArray(rotationSprite, step);
        this.currentRotationSprite = new Image[(180 / step) + 1];
        this.size = 0;
        setSprite(new Image[]{spriteBuffer.getCurrent()});
    }

    public void setCurrentAngle(int currentAngle) {
        this.spriteBuffer.setCurrentAngle(currentAngle);
        //this.currentAngle = currentAngle;
    }

    public RotatingSpriteImageMover(Image[] rotationSprite, float velocity, Pair<Float, Float> startingPos) {
        super(Arrays.copyOf(rotationSprite, 1), velocity, startingPos);
        setRotationSprite(rotationSprite);
    }

    public void rotateTowards(float x, float y) throws InterruptedException {
        int targetAngle = (int) getAngle(lastX, lastY, x, y);
        rotate(targetAngle);
    }

    public synchronized void rotate(int targetAngle) throws InterruptedException {

        int currentAngle = spriteBuffer.getCurrentAngle();

        if (Math.abs(targetAngle - currentAngle) <= spriteBuffer.getStep()) { //TODO check how many steps is the limit?

            spriteBuffer.setCurrentAngle(targetAngle);
            Image[] last = new Image[1];
            last[0] = spriteBuffer.getCurrent();
            setSprite(last);

        } else {//rotate smoothly

            size = 0;
            int mirrorAngle;
            boolean direction; //true for increasing angle
            if (targetAngle < 180) {
                mirrorAngle = targetAngle + 180;
                direction = !(currentAngle < mirrorAngle && currentAngle > targetAngle);
            } else {
                mirrorAngle = targetAngle - 180;
                direction = currentAngle < targetAngle && currentAngle > mirrorAngle;
            }

            while (!(Math.abs(targetAngle - spriteBuffer.getCurrentAngle()) < 10) && size < currentRotationSprite.length) {//TODO fix this!!!!!!
                currentRotationSprite[size++] = direction ? spriteBuffer.getIncrementedByStep() : spriteBuffer.getDecrementedByStep();
            }

            pushSprite(Arrays.copyOf(currentRotationSprite, size), velocity.intensity);
        }
    }

    public static double getAngle(float x1, float y1, float x2, float y2) {

        float dx = x2 - x1;
        float dy = y2 - y1;

        double c = Math.sqrt(dx*dx + dy*dy);
        double angle =  Math.toDegrees(Math.acos(Math.abs(dx)/c));

        if (dx == 0 && dy == 0) {
            return 0;
        } else if(dx == 0) {
            if(dy < 0) {
                return 90;
            } else {
                return 270;
            }
        } else if (dy == 0){
            if(dx < 0) {
                return 180;
            } else {
                return 0;
            }
        } else if (dx > 0 && dy > 0) {
            return 360 - angle;
        } else if (dx < 0 && dy > 0) {
            return 180 + angle;
        } else if (dx < 0 && dy < 0) {
            return 180 - angle;
        } else if (dx > 0 && dy < 0) {
            return angle;
        } else {
            return 0;
        }
    }

}
