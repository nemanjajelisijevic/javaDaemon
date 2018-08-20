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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Daemonize(doubleDaemonize = true)
public class Tower extends CachedSpriteImageTranslationMover {

    private float range;
    private int currentAngle;
    private int targetAngle;
    private Map<Integer, Bitmap> angleToImageMap = new HashMap<>(360);
    private DaemonView view;

    private int scanInterval;

    public void setScanInterval(int scanInterval) {
        this.scanInterval = scanInterval;
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

        //TODO validate sprite size (36)
        for (int i = 0; i < 360; ++i) {
            angleToImageMap.put(i,  rotationSprite.get(i / 10));
        }

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



        Log.d(DaemonUtils.tag(), "SCANNING.....");

        for (EnemyDoubleDaemon enemy : activeEnemies) {
            if (Math.abs( lastX - enemy.getPrototype().getLastCoordinates().first) < range
                    && Math.abs(lastY - enemy.getPrototype().getLastCoordinates().second) < range) {

                Log.i(DaemonUtils.tag(), "ENEMY FOUND: " + enemy.getName() + ", at coordinates: " + enemy.getPrototype().getLastCoordinates());

                setDirectionForRotation(
                        enemy.getPrototype().getLastCoordinates().first,
                        enemy.getPrototype().getLastCoordinates().second
                );
                return Pair.create(true, enemy);
            }
        }

        Log.e(DaemonUtils.tag(), "NO ENEMIES FOUND");

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


    public void setDirectionForRotation(float x, float y) throws InterruptedException {

//        float dx = x - lastX;
//        float dy = y - lastY;
//
//        double c = Math.sqrt(dx*dx + dy*dy);
//
//        int alpha = (int) Math.asin(dx/c);
//
//        if (dx > 0 && dy > 0){
//            // II kvadrant
//            targetAngle = 90 + alpha;
//        } else if (dx < 0 && dy > 0){
//            // I KVADRANT
//            targetAngle = 90 - alpha;
//        } else if (dx > 0 && dy < 0){
//            // III KVADRANT
//            targetAngle = 180 + alpha;
//        }else if (dx < 0 && dy < 0){
//            // IV KVADRANT
//            targetAngle = 270 + alpha;
//        }

        int angle = (int) getAngle(lastX, lastY, x, y);

        //boolean plus = true;// targetAngle >= currentAngle;//TODO cover when target - current <10

        //int deltaAlpha = targetAngle -  currentAngle;

        //Log.e(DaemonUtils.tag(), "DELTA ALPHA: " + deltaAlpha);

//        if ( deltaAlpha < 10 ){
//            currentAngle = targetAngle;
//            return;
//        }

//        if (Math.abs(deltaAlpha) > 180) {
//            if (deltaAlpha > 0){
//               plus = false;
//            } else {
//               plus = true;
//            }
//        } else {
//            if (deltaAlpha > 0){
//                plus = true;
//            } else {
//                plus = false;
//            }
//        }

//        List<Bitmap> rotationSprite = new LinkedList<>();
//        int counterAngle = currentAngle;
//        while (!(Math.abs(counterAngle - targetAngle) < 10)) {
//            rotationSprite.add(angleToImageMap.get(counterAngle));
//            if (plus) {
//                if (counterAngle >= 350){
//                    counterAngle = 0;
//                }else {
//                    counterAngle += 10;
//                }
//            }
//            else{
//                if (counterAngle <= 10){
//                    counterAngle = 359;
//                }else {
//                    counterAngle -= 10;
//                }
//            }
//
//        }

        ///currentAngle = targetAngle;

        //pushSprite(rotationSprite, velocity.intensity);
        List<Bitmap> finalPositionSprite = new ArrayList<>(1);
        finalPositionSprite.add(angleToImageMap.get(angle));
        sprite = finalPositionSprite;
    }

    @SideQuest(SLEEP = 25)
    @Override
    public PositionedBitmap animate() {
        PositionedBitmap ret = new PositionedBitmap();
        ret.image = iterateSprite();
        ret.positionX = lastX;
        ret.positionY = lastY;
        return ret;
    }
}
