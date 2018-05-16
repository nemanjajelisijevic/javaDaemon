package com.daemonize.daemondevapp;

import android.util.Pair;

import com.daemonize.daemondevapp.imagemovers.ImageMoverDaemon;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.SideQuest;

@Daemonize
public class BackgroundScroller {

    private ImageMoverDaemon target;

    public BackgroundScroller(ImageMoverDaemon target) {
        this.target = target;
    }

    @SideQuest(SLEEP = 30)
    public Pair<Integer, Integer> scroll(){
        return Pair.create(
                Math.round(target.getLastCoordinates().first + 100),
                Math.round(target.getLastCoordinates().second + 100)
        );
    };
}
