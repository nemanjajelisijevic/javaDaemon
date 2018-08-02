package com.daemonize.daemondevapp;

import android.util.Log;
import android.widget.TextView;

import com.daemonize.daemondevapp.restcliententities.DelayedGetResponse;

import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.consumer.DaemonConsumer;
import com.daemonize.daemonengine.consumer.androidconsumer.AndroidLooperConsumer;
import com.daemonize.daemonengine.daemonscript.DaemonChainScript;
import com.daemonize.daemonengine.daemonscript.DaemonSpell;
import com.daemonize.daemonengine.daemonscript.DaemonScript;
import com.daemonize.daemonengine.utils.DaemonUtils;

public class RestClientTestScript implements DaemonScript {

    private DaemonChainScript chain = new DaemonChainScript();

    private TextView textView;
    private RestClientDaemon restClientDaemon;
    private DaemonConsumer consumer = new DaemonConsumer(this.getClass().getSimpleName() + " consumer");
    private Consumer mainConsumer = new AndroidLooperConsumer();

    {
        chain.addSpell(() ->
            restClientDaemon.get(
                    "/api/users?delay=3",
                     DelayedGetResponse.class,
                     ret -> {
                        mainConsumer.consume(() -> textView.setText(ret.get().toString()));
                        Log.d(DaemonUtils.tag(), "LINK 1");

                        if (ret.get().total_pages > 0)
                            next();

                        else {
                            Log.e(
                                    DaemonUtils.tag(),
                                    "Total pages: " + Integer.toString(ret.get().total_pages)
                            );
                            consumer.stop();
                        }
                    })  
        ).addSpell(() ->
            restClientDaemon.get(
                    "/api/users?delay=3",
                    DelayedGetResponse.class,
                    aReturn -> {
                        String res = "AGAIN!!!!!\n" + aReturn.get().toString();
                        mainConsumer.consume(() -> textView.setText(res));
                        Log.d(DaemonUtils.tag(), "LINK 2");
                        next();
                    })
        ).addSpell(() ->
                restClientDaemon.get(
                        "/api/users?delay=3",
                        DelayedGetResponse.class,
                        aReturn -> {
                            String res = "AND AGAIN!!!!!\n" + aReturn.get().toString();
                            mainConsumer.consume(() -> textView.setText(res));
                            Log.d(DaemonUtils.tag(), "LINK 3");
                            next();
                        })
        ).addSpell(() ->
                restClientDaemon.get(
                        "/api/users?delay=3",
                        DelayedGetResponse.class,
                        aReturn -> {
                            String res = "LAST TIME!!!!!\n" + aReturn.get().toString();
                            mainConsumer.consume(() -> textView.setText(res));
                            Log.d(DaemonUtils.tag(), "LINK 4");
                            consumer.stop();
                        })
        );
    }

    public RestClientTestScript(TextView textView, RestClientDaemon restClientDaemon) {
        this.textView = textView;
        this.restClientDaemon = restClientDaemon;
        this.restClientDaemon.setConsumer(consumer);
        consumer.start();
    }

    @Override
    @SuppressWarnings("unchecked")
    public RestClientTestScript addSpell(DaemonSpell spell) {
        chain.addSpell(spell);
        return this;
    }

    @Override
    public void next() {
        chain.next();
    }

    @Override
    public void run() {
        consumer.consume(() -> chain.run());
    }
}
