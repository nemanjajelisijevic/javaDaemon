package com.daemonize.daemondevapp;


import com.daemonize.daemondevapp.imagemovers.CachedArraySpriteImageMover;
import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemondevapp.view.ImageView;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.ArrayList;
import java.util.List;


@Daemonize(doubleDaemonize = true)
public class Tower extends CachedArraySpriteImageMover {

    private float range;
    private int currentAngle;
    private AngleToBitmapArray spriteBuffer;
    private ImageView view;
    private volatile boolean pause = false;

    private Image[] rotationSprite;
    private int size = 0;

    private Game.TowerScanClosure scanClosure;

    @CallingThread
    public void setScanClosure(Game.TowerScanClosure scanClosure) {
        this.scanClosure = scanClosure;
    }

    @CallingThread
    public Game.TowerScanClosure getScanClosure() {
        return scanClosure;
    }

    private volatile int scanInterval;

    public void setScanInterval(int scanInterval) {
        this.scanInterval = scanInterval;
    }

    @CallingThread
    public int getScanInterval() {
        return scanInterval;
    }

    @CallingThread
    public ImageView getView() {
        return view;
    }

    public void setView(ImageView view) {
        this.view = view;
    }

    public Tower(Image[] initSprite, Image[] rotationSprite,  Pair<Float, Float> startingPos, float range, int scanIntervalInMillis) {
        super(initSprite, 0, startingPos);
        this.spriteBuffer = new AngleToBitmapArray(rotationSprite, 10);
        this.rotationSprite = new Image[19];
        this.range = range;
        this.scanInterval = scanIntervalInMillis;
    }

    @Override
    public boolean pushSprite(Image[] sprite, float velocity) throws InterruptedException {
        return super.pushSprite(sprite, velocity);
    }

    public boolean sleep(int millis) throws InterruptedException {
        Thread.sleep(millis);
        return true;
    }

    @DedicatedThread
    public Pair<Boolean, EnemyDoubleDaemon> scan (List<EnemyDoubleDaemon> activeEnemies) throws InterruptedException {

        for (EnemyDoubleDaemon enemy : activeEnemies) {
            if (Math.abs( lastX - enemy.getPrototype().getLastCoordinates().getFirst()) < range
                    && Math.abs(lastY - enemy.getPrototype().getLastCoordinates().getSecond()) < range) {
                pause = false;
                rotateTowards(
                        enemy.getPrototype().getLastCoordinates().getFirst(),
                        enemy.getPrototype().getLastCoordinates().getSecond()
                );
                return Pair.create(true, enemy);
            }
        }

        pause = true;
        Thread.sleep(scanInterval);
        return Pair.create(false, null);
    }

    private static double getAngle(float x1, float y1, float x2, float y2) {

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

    private  List<Image> convertArrayInList(Image [] array, int size) {
        ArrayList<Image> arrayList = new ArrayList<>(size);
        for (int i=0;i<size;i++){
            arrayList.add(array[i]);
        }
        return arrayList;
    }

    public void rotateTowards(float x, float y) throws InterruptedException {

        int targetAngle = (int) getAngle(lastX, lastY, x, y);
        size = 0;

        if (Math.abs(targetAngle - currentAngle) <= 2 * spriteBuffer.getStep()) {
            rotationSprite[size++] = spriteBuffer.getByAngle(targetAngle);
            spriteBuffer.setCurrentAngle(targetAngle);

            Image[] last = new Image[1];
            last[0] = rotationSprite[rotationSprite.length - 1];
            setSprite(last);

        } else {

            //rotate smoothly
            int mirrorAngle;
            boolean direction; //true for increasing angle

            if (targetAngle < 180) {
                mirrorAngle = targetAngle + 180;
                direction = !(currentAngle < mirrorAngle && currentAngle > targetAngle);
            } else {
                mirrorAngle = targetAngle - 180;
                direction = currentAngle < targetAngle && currentAngle > mirrorAngle;
            }

            while (!(Math.abs(targetAngle - spriteBuffer.getCurrentAngle()) < 10)) {
                rotationSprite[size++] = direction ? spriteBuffer.getIncrementedByStep() : spriteBuffer.getDecrementedByStep();
            }

            //getSprite().clear();
            //getSprite().add(rotationSprite[size - 1]);
            pushSprite(rotationSprite,velocity.intensity);
            //setSprite(rotationSprite[size - 1]);

        }

        currentAngle = spriteBuffer.getCurrentAngle(); //TODO check if this needs to go before pushSprite() call
    }

    @SideQuest(SLEEP = 25)
    @Override
    public PositionedImage animate() {

        if (pause)
            return null;

        PositionedImage ret = new PositionedImage();
        ret.image = iterateSprite();

        if (ret.image == null) {
            return null;//TODO check this null
        }

        ret.positionX = lastX;
        ret.positionY = lastY;
        return ret;
    }
}
