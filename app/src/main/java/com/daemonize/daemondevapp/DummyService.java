package com.daemonize.daemondevapp;


import com.daemonize.daemonprocessor.Daemonize;
import com.daemonize.daemonprocessor.SideQuest;

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
