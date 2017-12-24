package com.daemonize.daemondevapp;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.daemonize.daemondevapp.restcliententities.DelayedGetResponse;
import com.daemonize.daemonengine.daemonscript.DaemonChainScript;
import com.daemonize.daemonengine.daemonscript.DaemonLink;
import com.daemonize.daemonengine.daemonscript.DaemonScript;
import com.daemonize.daemonengine.daemonscript.LinkedClosure;
import com.daemonize.daemonengine.exceptions.DaemonException;
import com.daemonize.daemonengine.implementations.basedaemon.BaseDaemonEngine;

public class RestClientTestScript implements DaemonScript {

    private DaemonChainScript chain = new DaemonChainScript();

    private Activity activity;
    private TextView textView;
    private RestClientDaemon restClientDaemon;

    {
        chain.addLink(new DaemonLink() {
                    @Override
                    public void execute() {
                        restClientDaemon.get(
                                "/api/users?delay=3",
                                DelayedGetResponse.class,
                                new LinkedClosure<DelayedGetResponse>(activity, chain) {
                                    @Override
                                    public void doTheGuiStuff() {
                                        try {
                                            Log.d(BaseDaemonEngine.tag(), "LINK 11111111111111111111111111111111111111111111111111111111111");
                                            textView.append(getResult().toString());
                                        } catch (DaemonException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }}
        ).addLink(new DaemonLink() {
                    @Override
                    public void execute() {
                        restClientDaemon.get(
                                "/api/users?delay=3",
                                DelayedGetResponse.class,
                                new LinkedClosure<DelayedGetResponse>(activity, chain) {
                                    @Override
                                    public void doTheGuiStuff() {
                                        try {
                                            //b.dismiss();
                                            Log.d(BaseDaemonEngine.tag(), "LINK 222222222222222222222222222222222222222222222222222222222222");
                                            textView.append(getResult().toString());
                                        } catch (DaemonException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }}
        ).addLink(new DaemonLink() {
                    @Override
                    public void execute() {
                        restClientDaemon.get(
                                "/api/users?delay=3",
                                DelayedGetResponse.class,
                                new LinkedClosure<DelayedGetResponse>(activity, chain) {
                                    @Override
                                    public void doTheGuiStuff() {
                                        try {
                                            //b.dismiss();
                                            Log.d(BaseDaemonEngine.tag(), "LINK 3333333333333333333333333333333333333333333333333333333333333");
                                            textView.append(getResult().toString());
                                        } catch (DaemonException e) {
                                            breakScript();
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }}
        ).addLink(new DaemonLink() {
                    @Override
                    public void execute() {
                        restClientDaemon.get(
                                "/api/users?delay=3",
                                DelayedGetResponse.class,
                                new LinkedClosure<DelayedGetResponse>(activity, chain) {
                                    @Override
                                    public void doTheGuiStuff() {
                                        try {
                                            //b.dismiss();
                                            Log.d(BaseDaemonEngine.tag(), "LINK 444444444444444444444444444444444444444444444444444444444444");
                                            textView.append(getResult().toString());
                                        } catch (DaemonException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                    }}
        );
    }

    public RestClientTestScript(Activity activity, TextView textView, RestClientDaemon restClientDaemon) {
        this.activity = activity;
        this.textView = textView;
        this.restClientDaemon = restClientDaemon;
    }

    @Override
    @SuppressWarnings("unchecked")
    public RestClientTestScript addLink(DaemonLink link) {
        chain.addLink(link);
        return this;
    }

    @Override
    public void next() {
        chain.next();
    }

    @Override
    public void run() {
        chain.run();
    }
}
