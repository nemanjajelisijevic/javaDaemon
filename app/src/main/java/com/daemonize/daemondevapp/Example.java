package com.daemonize.daemondevapp;

import android.os.Handler;
import android.os.Looper;
import android.util.Pair;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.ReturnRunnable;
import com.daemonize.daemonprocessor.annotations.CallingThread;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.DedicatedThread;
import com.daemonize.daemonprocessor.annotations.SideQuest;


import java.util.ArrayList;
import java.util.List;

@Daemonize(eager = true, returnDaemonInstance = true)
public class Example {

    private int count = 0;

    public String increment() throws InterruptedException {
        Thread.sleep(2000);
        return Integer.toString(++count);
    }

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

    @DedicatedThread
    public int dedThreadEngineTest(boolean flag, float fp) {
        return 1;
    }

    public List<String> complicated(String text) throws InterruptedException {
        List<String> ret = new ArrayList<>();
        for (int i = 0; i < 203; ++i) {
            ret.add(text + Integer.toString(i));
            Thread.sleep(100);
        }
        return ret;
    }

    public String evenMoreComplicated(String text, Closure<String> update) throws InterruptedException {

        Handler handler = new Handler(Looper.getMainLooper());

        for (int i = 1; i < 20; ++i) {

//            if(i % 7 == 0) {
//                throw new IllegalStateException("Testing exception stack trace");
//            }

            handler.post(new ReturnRunnable<>(update).setResult(text + Integer.toString(i)));
            Thread.sleep(1000);
        }

        return "DONE!";
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

    @SideQuest(SLEEP = 5000)
    public int sqTest() {
        return 1;
    }


    @SideQuest(SLEEP = 3000)
    public float sqTest2() {
        return 1F;
    }
}
