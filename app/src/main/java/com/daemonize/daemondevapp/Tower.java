package com.daemonize.daemondevapp;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

import com.daemonize.daemondevapp.imagemovers.CachedSpriteImageTranslationMover;
import com.daemonize.daemondevapp.view.DaemonView;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.ArrayList;
import java.util.List;


@Daemonize(doubleDaemonize = true)
public class Tower extends CachedSpriteImageTranslationMover {

    private float range;
    private int currentAngle;
    private AngleToBitmapArray spriteBuffer;
    private DaemonView view;
    private volatile boolean pause = false;

    private List<Bitmap> rotationSprite;

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
        this.spriteBuffer = new AngleToBitmapArray(rotationSprite, 10);
        this.rotationSprite = new ArrayList<>(19);
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

    @DedicatedThread
    public Pair<Boolean, EnemyDoubleDaemon> scan (List<EnemyDoubleDaemon> activeEnemies) throws InterruptedException {

        for (EnemyDoubleDaemon enemy : activeEnemies) {
            if (Math.abs( lastX - enemy.getPrototype().getLastCoordinates().first) < range
                    && Math.abs(lastY - enemy.getPrototype().getLastCoordinates().second) < range) {
                pause = false;
                rotateTowards(
                        enemy.getPrototype().getLastCoordinates().first,
                        enemy.getPrototype().getLastCoordinates().second
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


    public void rotateTowards(float x, float y) throws InterruptedException {

        int targetAngle = (int) getAngle(lastX, lastY, x, y);
        rotationSprite.clear();

        if (Math.abs(targetAngle - currentAngle) < 2 * spriteBuffer.getStep()) {

            rotationSprite.add(spriteBuffer.getByAngle(targetAngle));
            spriteBuffer.setCurrentAngle(targetAngle);
            sprite = rotationSprite;

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

            int counterAngle = currentAngle;
            int diff = targetAngle - counterAngle;

            Log.e(DaemonUtils.tag(), "START Angle diff: " + diff);

            while (!(Math.abs(targetAngle - spriteBuffer.getCurrentAngle()) < 10)) {

                int diff2 = targetAngle - /*counterAngle*/spriteBuffer.getCurrentAngle();

                Log.w(DaemonUtils.tag(), "****************************");
                Log.w(DaemonUtils.tag(), "Target Angle: " + targetAngle);
                Log.w(DaemonUtils.tag(), "Current Angle: " + /*counterAngle*/spriteBuffer.getCurrentAngle());
                Log.w(DaemonUtils.tag(), "Angle diff: " + diff2);
                Log.w(DaemonUtils.tag(), "****************************");

                rotationSprite.add(
                        direction ?
                                spriteBuffer.getIncrementedByStep()
                                : spriteBuffer.getDecrementedByStep()
                );

                counterAngle = spriteBuffer.getCurrentAngle();
            }

            Log.e(DaemonUtils.tag(), "END Angle diff: " + (targetAngle - counterAngle));

            pushSprite(rotationSprite, velocity.intensity);
            sprite.clear();
            sprite.add(rotationSprite.get(rotationSprite.size() - 1));
        }

        currentAngle = spriteBuffer.getCurrentAngle(); //TODO check if this needs to go before pushSprite() call
    }

    @SideQuest(SLEEP = 30)
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
