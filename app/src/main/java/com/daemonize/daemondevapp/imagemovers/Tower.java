package com.daemonize.daemondevapp.imagemovers;

import android.graphics.Bitmap;
import android.util.Pair;

import com.daemonize.daemondevapp.proba.ImageMoverM;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Tower {

    class PositionedBitmap {
        public Bitmap image;
        public float positionX;
        public float positionY;
    }

    private int row, column;
    private float x,y;
    private int level;
    private float range;
    private float fireRation;

    //
    private float targetX, targetY;
    private int angel = 0;
    private int targetAngle;

    protected Set<Bitmap> sprite;
    protected Iterator<Bitmap> spriteIterator;

    public Tower(int row, int column, float x, float y, int level, float range, float fireRation,
                 Set<Bitmap> sprite) {
        this.row = row;
        this.column = column;
        this.x = x;
        this.y = y;
        this.level = level;
        this.range = range;
        this.fireRation = fireRation;
        this.sprite = sprite;
    }

    @DedicatedThread
    public Pair<Float,Float> scan (Set<EnemyDoubleDaemon> activeEnemies) {
        for (EnemyDoubleDaemon enemy : activeEnemies) {

            Pair<Float, Float> enemyCoord = enemy.getPrototype().getLastCoordinates();

            if (Math.abs( x - enemyCoord.first) < 200
                    && Math.abs(y - enemyCoord.second) < 200) {
//                fireBullet(Pair.create((float) field.getCenterX(), (float) field.getCenterY()), enemy);
//                break;
                setDirectionForRotation(enemyCoord.first,enemyCoord.second);
                return enemyCoord;
            }
        }
        return null;
    }

    public int setDirectionForRotation(float x, float y) {

        float dtAngle = x+y;
        return angel += dtAngle;

    }

    protected Bitmap iterateSprite(int alfa) {
        if(!spriteIterator.hasNext()) {
            spriteIterator = sprite.iterator();
        }
        return spriteIterator.next();
    }

    @SideQuest(SLEEP = 25)
    public PositionedBitmap rotate() {
        PositionedBitmap ret = new PositionedBitmap();
        ret.image = iterateSprite(angel);
        ret.positionX = x;
        ret.positionY = y;
        return ret;
    }



}
