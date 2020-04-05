package com.daemonize.game;

import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.GenerateRunnable;
import com.daemonize.daemonprocessor.annotations.SideQuest;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.scene.views.ImageView;
import com.daemonize.imagemovers.CoordinatedImageTranslationMover;
import com.daemonize.imagemovers.Movable;
import com.daemonize.imagemovers.RotatingSpriteImageMover;

import java.beans.IntrospectionException;
import java.util.Arrays;

@Daemon(doubleDaemonize = true, implementPrototypeInterfaces = true)
public class DummyPlayer extends CoordinatedImageTranslationMover implements Movable {

    public DummyPlayer(
            Image sprite,
            Pair<Float, Float> startingPos,
            float dXY
    ) {
        super(new Image[]{sprite}, startingPos, dXY);
    }
//
//    public void pause() {
//        this.animateSemaphore.stop();
//    }
//
//    public void go() {
//        this.animateSemaphore.subscribe();
//    }

    @Daemonize
    @Override
    public boolean goTo(float x, float y, float velocity) throws InterruptedException {
        return super.goTo(x, y, velocity);
    }

    @Daemonize
    @GenerateRunnable
    public void goTo(Pair<Float, Float> coords, float velocity) throws InterruptedException {
        super.goTo(coords.getFirst(), coords.getSecond(), velocity);
    }

    @Daemonize
    public void go(float x, float y, float velocity) throws InterruptedException {
        goTo(x, y, velocity);
    }

    @Daemonize
    public void go(Pair<Float, Float> coords, float velocity) throws InterruptedException {
        go(coords.getFirst(), coords.getSecond(), velocity);
    }

    @SideQuest(SLEEP = 30)
    public PositionedImage animateDummyPlayer() throws InterruptedException {
        return super.animate();
    }
}
