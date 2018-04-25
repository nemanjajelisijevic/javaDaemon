package com.daemonize.daemondevapp.imagemovers;


import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonprocessor.annotations.CallingThread;

import java.util.List;

public class MainImageTranslationMover extends ImageTranslationMover {

    private final List<ImageMoverDaemon> observers;

    private int hp = 1000;
    private Handler guihandler = new Handler(Looper.getMainLooper());

    public void setHp(int hp) {
        this.hp = hp;
    }

    private Closure<Integer> hpClosure;

    public MainImageTranslationMover setHpClosure(Closure<Integer> hpClosure) {
        this.hpClosure = hpClosure;
        return this;
    }

    public MainImageTranslationMover(
            List<Bitmap> sprite,
            float velocity,
            Pair<Float, Float> startingPos,
            List<ImageMoverDaemon> observers) {
        super(sprite, velocity, startingPos);
        this.observers = observers;
    }

    @Override
    public MainImageTranslationMover setBorders(float x, float y) {
        super.setBorders(x, y);
        return this;
    }

    @Override
    public PositionedBitmap move() {

        if(velocity.intensity > 0 ) {

            if (observers != null)
                for (ImageMoverDaemon observer : observers) {
                    Velocity vel = new Velocity(velocity.intensity * 0.3F, velocity.direction);
                    observer.setDirectionAndMove(lastX, lastY, vel.intensity); //TODO CHASER
                    //observer.checkCollisionAndBounce(Pair.create(lastX, lastY), vel); //TODO Collisions
                    Pair<Float, Float> obsLastCoord = observer.getLastCoordinates();
                    if (!((ImageTranslationMover) observer.getPrototype()).isExploading()
                            && Math.abs(lastX - obsLastCoord.first) < 40
                            && Math.abs(lastY - obsLastCoord.second) < 40) {
                      guihandler.post(new ReturnRunnable<>(hpClosure).setResult((--hp) / 10));
                    }
                }

            velocity.intensity -= 0.1;
            return super.move();
        }

        return null;
    }

    @Override
    public void setDirectionAndMove(float x, float y, float velocityInt) {

        float diffX = x - lastX;
        float diffY = y - lastY;

        float a;
        boolean signY = diffY >= 0;
        boolean signX = diffX >= 0;

        if (Math.abs(diffY) >= Math.abs(diffX)) {
            a = Math.abs((100*diffX)/diffY);
            float aY =  100 - a;
            setDirection(new Direction(signX ? a : - a, signY ? aY : - aY));
        } else {
            a = Math.abs((100*diffY)/diffX);
            float aX =  100 - a;
            setDirection(new Direction(signX ? aX : -aX, signY ? a : -a));
        }

        setVelocity(initVelocity);
    }
}
