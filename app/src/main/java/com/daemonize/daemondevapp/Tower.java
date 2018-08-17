package com.daemonize.daemondevapp;

import android.graphics.Bitmap;
import android.util.Pair;

import com.daemonize.daemondevapp.imagemovers.CachedSpriteImageTranslationMover;
import com.daemonize.daemondevapp.view.DaemonView;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.SideQuest;

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

    @CallingThread
    public DaemonView getView() {
        return view;
    }

    public void setView(DaemonView view) {
        this.view = view;
    }

    public Tower(List<Bitmap> sprite,  Pair<Float, Float> startingPos, float range) {
        super(sprite, 0, startingPos);

        //TODO validate sprite size (36)
        for (int i = 0; i < 360; ++i) {
            angleToImageMap.put(i,  sprite.get(i / 10));
        }

        this.range = range;
    }

    @Override
    public boolean pushSprite(List<Bitmap> sprite, float velocity) throws InterruptedException {
        return super.pushSprite(sprite, velocity);
    }

    @DedicatedThread
    public EnemyDoubleDaemon scan (List<EnemyDoubleDaemon> activeEnemies) throws InterruptedException {
        for (EnemyDoubleDaemon enemy : activeEnemies) {
            if (Math.abs( lastX - enemy.getPrototype().getLastCoordinates().first) < range
                    && Math.abs(lastY - enemy.getPrototype().getLastCoordinates().second) < range) {
                setDirectionForRotation(
                        enemy.getPrototype().getLastCoordinates().first,
                        enemy.getPrototype().getLastCoordinates().second
                );
                return enemy;
            }
        }

        return null;
    }

    public void setDirectionForRotation(float x, float y) throws InterruptedException {

        //TODO math: calculate the currentAngle
        float dtAngle = x+y;
        targetAngle += dtAngle;


        boolean plus = targetAngle >= currentAngle;//TODO cover when target - current <10

        List<Bitmap> rotationSprite = new LinkedList<>();
        int counterAngle = currentAngle;
        while (!(Math.abs(counterAngle - targetAngle) < 10)) {
            rotationSprite.add(angleToImageMap.get(counterAngle));
            if (plus)
                counterAngle += 10;
            else
                counterAngle -= 10;
        }

        currentAngle = targetAngle;
        pushSprite(rotationSprite, velocity.intensity);
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
