package com.daemonize.daemondevapp;


import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.SideQuest;

@Daemonize
public class DummyService {

    private int counter;

    public void reset() {
        counter = 0;
    }

    @SideQuest(SLEEP = 300)
    public Integer increment(){
        return counter++;
    }

}
