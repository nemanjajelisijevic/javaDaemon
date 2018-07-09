package com.daemonize.daemondevapp.imagemovers;


import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import com.daemonize.daemondevapp.MainActivity;
import com.daemonize.daemondevapp.proba.Enemy;
import com.daemonize.daemondevapp.proba.ImageMoverMDaemon;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.utils.DaemonUtils;

import java.util.List;

public class MainImageTranslationMover extends StackedSpriteImageTranslationMover {

    private final List<ImageMoverMDaemon> observers;

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
            List<ImageMoverMDaemon> observers) {
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
            Log.d(DaemonUtils.tag(), "TEST");


            if (observers != null)
                for (ImageMoverMDaemon observer : observers) {
                float x = lastX - observer.getLastCoordinates().first;
                float y = lastY - observer.getLastCoordinates().second;
                float c = (float) Math.sqrt(x*x + y*y);
                if (c < 150.0f){
                    Log.w("Puca","bum");
                }
                //                    Velocity vel = new Velocity(velocity.intensity * 0.3F, velocity.direction);
//                    observerobserver.setDirectionAndMove(lastX, lastY, vel.intensity); //TODO CHASER
//                    //observer.checkCollisionAndBounce(Pair.create(lastX, lastY), vel); //TODO Collisions
//                    Pair<Float, Float> obsLastCoord = observer.getLastCoordinates();
//                    if (!((Enemy) observer.getPrototype()).isExploading()
//                            && Math.abs(lastX - obsLastCoord.first) < 40
//                            && Math.abs(lastY - obsLastCoord.second) < 40) {
//                      guihandler.post(new ReturnRunnable<>(hpClosure).setResult((--hp)));
//                    }
//
//                    if (hp < 0)
//                        return null;
                }

           velocity.intensity -= 0.1;
            return super.move();
        }

       return null;
    }

    @Override
    public void setDirectionAndMove(float x, float y, float velocityInt) {

        float dX = x - lastX;
        float dY = y - lastY;

        float a;
        boolean signY = dY >= 0;
        boolean signX = dX >= 0;

        if (Math.abs(dY) >= Math.abs(dX)) {
            a = Math.abs((100*dX)/dY);
            float aY =  100 - a;
            setDirection(new Direction(signX ? a : - a, signY ? aY : - aY));
        } else {
            a = Math.abs((100*dY)/dX);
            float aX =  100 - a;
            setDirection(new Direction(signX ? aX : -aX, signY ? a : -a));
        }

        setVelocity(initVelocity);
    }
}
