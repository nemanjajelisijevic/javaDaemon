package com.daemonize.daemondevapp;

import android.util.Log;
import android.widget.TextView;

import com.daemonize.daemondevapp.restcliententities.DelayedGetResponse;

import com.daemonize.daemonengine.daemonscroll.DaemonChainScroll;
import com.daemonize.daemonengine.daemonscroll.DaemonSpell;
import com.daemonize.daemonengine.daemonscroll.DaemonScroll;
import com.daemonize.daemonengine.utils.DaemonUtils;

public class RestClientTestScript implements DaemonScroll {

    private DaemonChainScroll chain = new DaemonChainScroll();

    private TextView textView;
    private RestClientDaemon restClientDaemon;

    {
        chain.addSpell(() ->
                restClientDaemon.get(
                        "/api/users?delay=3",
                        DelayedGetResponse.class,
                        ret -> {

                            textView.setText(ret.get().toString());
                            Log.d(DaemonUtils.tag(), "LINK 1");

                            if (ret.get().total_pages > 0)
                                next();
                            else
                                Log.e(
                                        DaemonUtils.tag(),
                                        "Total pages: " + Integer.toString(ret.get().total_pages)
                                );
                        }
                )
        ).addSpell(() ->
                restClientDaemon.get(
                        "/api/users?delay=3",
                        DelayedGetResponse.class,
                        aReturn -> {
                            textView.append(aReturn.get().toString());
                            Log.d(DaemonUtils.tag(), "LINK 2");
                            chain.next();
                        }
                )
        ).addSpell(() ->
                restClientDaemon.get(
                        "/api/users?delay=3",
                        DelayedGetResponse.class,
                        aReturn -> {
                            textView.append(aReturn.get().toString());
                            Log.d(DaemonUtils.tag(), "LINK 3");
                            chain.next();
                        }
                )
        ).addSpell(() ->
                restClientDaemon.get(
                        "/api/users?delay=3",
                        DelayedGetResponse.class,
                        aReturn -> {
                            textView.append(aReturn.get().toString());
                            Log.d(DaemonUtils.tag(), "LINK 4");
                            chain.next();
                        }
                )
        );
    }

    public RestClientTestScript(TextView textView, RestClientDaemon restClientDaemon) {
        this.textView = textView;
        this.restClientDaemon = restClientDaemon;
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
        chain.run();
    }
}
