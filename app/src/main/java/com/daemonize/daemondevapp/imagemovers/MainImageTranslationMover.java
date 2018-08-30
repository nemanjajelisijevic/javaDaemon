package com.daemonize.daemondevapp.imagemovers;


import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Pair;

import com.daemonize.daemondevapp.images.Image;
import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonengine.utils.DaemonUtils;

import java.util.List;

public class MainImageTranslationMover extends CachedSpriteImageTranslationMover {

    private final List<ImageMoverDaemon> observers;

    private int hp = 1000;
    private Handler guihandler = new Handler(Looper.getMainLooper());

    public void setHp(int hp) {
        this.hp = hp;
    }

    private Closure<Integer> hpClosure;
    private boolean fire = false;
    private Closure<Pair<Float,Float>> bulletFire;


    public boolean isFire() {
        return fire;
    }

    public void setFire(boolean fire) {
        this.fire = fire;
    }

    public MainImageTranslationMover setBulletFireClosure(Closure<Pair<Float,Float>> bulletFire) {
        this.bulletFire = bulletFire;
        return this;
    }
    public MainImageTranslationMover setHpClosure(Closure<Integer> hpClosure) {
        this.hpClosure = hpClosure;
        return this;
    }

    public MainImageTranslationMover(
            List<Image> sprite,
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
    public PositionedImage animate() {


//        if(velocity.intensity > 0 ) {


            if (observers != null)
                for (ImageMoverDaemon observer : observers) {
                float x = Math.abs( lastX - observer.getLastCoordinates().first );
                float y = Math.abs( lastY - observer.getLastCoordinates().second);
                float c = (float) Math.sqrt(x*x + y*y);
                if (c < 250.0f){
                   // Log.w("Puca","bum");
                    Log.d(DaemonUtils.tag(), "Puca bum  x: "+observer.getLastCoordinates().first+" , y: "+observer.getLastCoordinates().second);
                    fire = true;
                    guihandler.post(new ReturnRunnable<>(bulletFire).setResult(observer.getLastCoordinates()));
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
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
            return super.animate();
        //}

      // return null;
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
