package com.daemonize.daemondevapp.imagemovers;

import android.util.Pair;

import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.List;

@Daemonize(returnDaemonInstance = true)
public interface MassiveImageMover {


    void addMover(ImageMover mover);

    void setDirectionAndMove(float x, float y, float velocityInt);

    void breakFormation(float velocityInt);

    @CallingThread
    List<Pair<Float, Float>> getLastCoordinates();

    @CallingThread
    List<ImageMover.Velocity> getVelocities();

    @SideQuest(SLEEP = 20)
    List<ImageMover.PositionedBitmap> move();

}