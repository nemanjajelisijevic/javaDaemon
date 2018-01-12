package com.daemonize.daemondevapp;

import android.util.Pair;

import com.daemonize.daemonprocessor.CallingThread;
import com.daemonize.daemonprocessor.Daemonize;
import com.daemonize.daemonprocessor.SideQuest;


import java.util.ArrayList;
import java.util.List;

@Daemonize(eager = true, returnDaemonInstance = true)
public class Example {


    @CallingThread
    public Integer add (Integer i, Integer k) {
        return i + k;
    }

    public void dummy(String dummyString, List<Float> floats) {

    }

    @CallingThread
    public static int subtract(int i, int k) {
        return i - k;
    }

    private void shouldNotBeHere(){}

    protected Integer shouldNorBeHere() {
        return  1;
    }

    public List<String> complicated(String text) throws InterruptedException {
        return new ArrayList<>();
    }

    @CallingThread
    public Pair<Integer, String> pairThem() {
        return Pair.create(5, "12");
    }

    public void voidIt(){}

    public void voidIt(int a) {}
    public void voidIt(boolean a) {}
    public boolean voidIt(boolean a, boolean b) {
        return a & b;
    }
}
