package com.daemonize.game.imagemovers;


import com.daemonize.daemonengine.utils.DaemonCountingSemaphore;
import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.game.AngleToBitmapArray;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.game.images.Image;

import java.util.Arrays;

public class RotatingSpriteImageMover extends CachedArraySpriteImageMover {

    protected volatile AngleToBitmapArray spriteBuffer;
    protected volatile Image[] currentRotationSprite;
    private volatile int size;
    private DaemonSemaphore rotationSpriteSemaphore = new DaemonSemaphore().setName("Rotation Sprite Regulator");

    public void setRotationSprite(Image[] rotationSprite) {

        rotationSpriteSemaphore.stop();

        int currentAngle = spriteBuffer != null ? spriteBuffer.getCurrentAngle() : 0;
        int step = 360 / rotationSprite.length;
        this.spriteBuffer = new AngleToBitmapArray(rotationSprite, step);
        this.currentRotationSprite = new Image[(180 / step) + 1];
        this.size = 0;
        popSprite();
        this.spriteBuffer.setCurrentAngle(currentAngle);
        setSprite(new Image[]{spriteBuffer.getCurrent()});

        rotationSpriteSemaphore.go();
    }

    public void setCurrentAngle(int currentAngle) {
        this.spriteBuffer.setCurrentAngle(currentAngle);
    }

    public RotatingSpriteImageMover(
            Image[] rotationSprite,
            Image startingImage,
            float velocity,
            Pair<Float, Float> startingPos,
            float dXY
    ) {
        super(new Image[]{startingImage}, velocity, startingPos, dXY);
        setRotationSprite(rotationSprite);
    }

    public RotatingSpriteImageMover(
            Image[] rotationSprite,
            DaemonCountingSemaphore animateSemaphore,
            float velocity,
            Pair<Float, Float> startingPos,
            float dXY
    ) {
        this(rotationSprite, Arrays.copyOfRange(rotationSprite, 0, 1)[0], velocity, startingPos, dXY);
        this.animateSemaphore = animateSemaphore;
    }

    public void rotateTowards(float x, float y) throws InterruptedException {
        rotate((int) getAngle(getLastCoordinates().getFirst(), getLastCoordinates().getSecond(), x, y));
    }

    public void rotate(int targetAngle) throws InterruptedException {
        Image[] rotateSprite = getRotationSprite(targetAngle);
        pushSprite(rotateSprite, velocity.intensity);
    }

    protected Image[] getRotationSprite(int targetAngle) throws InterruptedException {

        rotationSpriteSemaphore.await();

        int currentAngle = spriteBuffer.getCurrentAngle();

        if (Math.abs(targetAngle - currentAngle) <= spriteBuffer.getStep()) { //TODO check how many steps is the limit?

            spriteBuffer.setCurrentAngle(targetAngle);
            Image[] last = new Image[1];
            last[0] = spriteBuffer.getCurrent();

            return last;

        } else {//getRotationSprite smoothly

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

            while (!(Math.abs(targetAngle - spriteBuffer.getCurrentAngle()) < spriteBuffer.getStep()) && size < currentRotationSprite.length)//TODO fix this!!!!!!
                currentRotationSprite[size++] = direction ? spriteBuffer.getIncrementedByStep() : spriteBuffer.getDecrementedByStep();

            return Arrays.copyOf(currentRotationSprite, size);
        }
    }

    public static double getAngle(Pair<Float, Float> one, Pair<Float, Float> two) {
        return getAngle(one.getFirst(), one.getSecond(), two.getFirst(), two.getSecond());
    }

    public static double getAngle(float x1, float y1, float x2, float y2) {

        float dx = x2 - x1;
        float dy = y2 - y1;

        double c = Math.sqrt(dx*dx + dy*dy);
        double angle =  Math.toDegrees(Math.acos(Math.abs(dx)/c));

        if (dx == 0 && dy == 0) {
            return 0;
        } else if(dx == 0) {
            if(dy < 0)
                return 90;
            else
                return 270;
        } else if (dy == 0){
            if(dx < 0)
                return 180;
            else
                return 0;
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

    public static double getAbsoluteAngle(double angle) {
        return angle >= 0 ? angle : 360 + angle;
    }

}
