package com.daemonize.daemondevapp;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import com.daemonize.daemondevapp.imagemovers.CachedSpriteImageTranslationMover;
import com.daemonize.daemondevapp.view.DaemonView;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.LinkedList;
import java.util.List;


@Daemonize(doubleDaemonize = true)
public class Tower extends CachedSpriteImageTranslationMover {

    private float range;
    private int currentAngle;
    private AngleToBitmapArray spriteBuffer;
    private DaemonView view;
    private volatile boolean pause = false;
    private List<Bitmap> rotationSprite = new LinkedList<>();

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
    public DaemonView getView() {
        return view;
    }

    public void setView(DaemonView view) {
        this.view = view;
    }

    public Tower(List<Bitmap> initSprite, List<Bitmap> rotationSprite,  Pair<Float, Float> startingPos, float range, int scanIntervalInMillis) {
        super(initSprite, 0, startingPos);
        spriteBuffer = new AngleToBitmapArray(rotationSprite, 10);
        this.range = range;
        this.scanInterval = scanIntervalInMillis;
    }

    @Override
    public boolean pushSprite(List<Bitmap> sprite, float velocity) throws InterruptedException {
        return super.pushSprite(sprite, velocity);
    }

    public boolean sleep(int millis) throws InterruptedException {
        Thread.sleep(millis);
        return true;
    }

    private EnemyDoubleDaemon closestEnemy;

    private boolean inRange(EnemyDoubleDaemon enemy) {
        return Math.abs( lastX - enemy.getPrototype().getLastCoordinates().first) < range
                && Math.abs(lastY - enemy.getPrototype().getLastCoordinates().second) < range;
    }

    @DedicatedThread
    public Pair<Boolean, EnemyDoubleDaemon> scan (List<EnemyDoubleDaemon> activeEnemies) throws InterruptedException {

        if (closestEnemy != null && inRange(closestEnemy)) {

            pause = false;
            rotateTowards(
                    closestEnemy.getPrototype().getLastCoordinates().first,
                    closestEnemy.getPrototype().getLastCoordinates().second
            );

            pause = true;
            return Pair.create(true, closestEnemy);

        } else if (closestEnemy != null && !inRange(closestEnemy)){

            closestEnemy = null;
            pause = true;
            Thread.sleep(scanInterval);
            return Pair.create(false, null);

        } else if (closestEnemy == null) {

            for (EnemyDoubleDaemon enemy : activeEnemies) {
                if (inRange(enemy)) {
                    closestEnemy = enemy;
                    break;

                }
            }

            pause = true;
            Thread.sleep(scanInterval);
            return Pair.create(false, null);

        } else {


            pause = true;
            Thread.sleep(scanInterval);
            return Pair.create(false, null);

        }
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


    public void rotateTowards(float x, float y) throws InterruptedException {

        int targetAngle = (int) getAngle(lastX, lastY, x, y);
        int diff = targetAngle - currentAngle;
        rotationSprite.clear();

        if (Math.abs(diff) < 2 * spriteBuffer.getStep()) {
            rotationSprite.add(spriteBuffer.getByAngle(targetAngle));
            sprite = rotationSprite;
        } else {

            //rotate smoothly
            int counterAngle = currentAngle;

            while (!(Math.abs(counterAngle - targetAngle) < 10)) {

                if (diff > 0 && diff <= 180)
                    rotationSprite.add(spriteBuffer.getIncrementedByStep());
                else if (diff > 180)
                    rotationSprite.add(spriteBuffer.getDecrementedByStep());
                else if (diff < -180)
                    rotationSprite.add(spriteBuffer.getIncrementedByStep());
                else if (diff < 0)
                    rotationSprite.add(spriteBuffer.getDecrementedByStep());

                counterAngle = spriteBuffer.getCurrentAngle();
            }

            pushSprite(rotationSprite, velocity.intensity);
            sprite.clear();
            sprite.add(rotationSprite.get(rotationSprite.size() - 1));
        }

        currentAngle = targetAngle; //TODO check if this needs to go before pushSprite() call
    }

    @SideQuest(SLEEP = 25)
    @Override
    public PositionedBitmap animate() {

        if (pause)
            return null;

        PositionedBitmap ret = new PositionedBitmap();
        ret.image = iterateSprite();
        ret.positionX = lastX - ret.image.getWidth()/2;
        ret.positionY = lastY - ret.image.getWidth()/2;
        return ret;
    }
}
