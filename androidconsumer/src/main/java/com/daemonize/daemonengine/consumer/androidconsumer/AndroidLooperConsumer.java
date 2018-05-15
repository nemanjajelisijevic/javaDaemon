package com.daemonize.daemonengine.consumer.androidconsumer;

import android.os.Handler;
import android.os.Looper;

import com.daemonize.daemonengine.consumer.Consumer;


public class AndroidLooperConsumer implements Consumer {

    private Handler handler = new Handler(Looper.getMainLooper());

    public AndroidLooperConsumer(){}

    public AndroidLooperConsumer(Handler handler) {
        this.handler = handler;
    }

    @Override
    public boolean enqueue(Runnable runnable) {
        return handler.post(runnable);
    }
}
