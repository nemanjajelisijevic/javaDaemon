package com.daemonize.daemondevapp;

import android.util.Log;
import android.widget.TextView;

import com.daemonize.daemondevapp.restcliententities.DelayedGetResponse;

import com.daemonize.daemonengine.daemonscript.DaemonChainScript;
import com.daemonize.daemonengine.daemonscript.DaemonLink;
import com.daemonize.daemonengine.daemonscript.DaemonScript;
import com.daemonize.daemonengine.utils.DaemonUtils;

public class RestClientTestScript implements DaemonScript {

    private DaemonChainScript chain = new DaemonChainScript();

    private TextView textView;
    private RestClientDaemon restClientDaemon;

    {
        chain.addLink(() ->
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
        ).addLink(() ->
                restClientDaemon.get(
                        "/api/users?delay=3",
                        DelayedGetResponse.class,
                        aReturn -> {
                            textView.append(aReturn.get().toString());
                            Log.d(DaemonUtils.tag(), "LINK 2");
                            chain.next();
                        }
                )
        ).addLink(() ->
                restClientDaemon.get(
                        "/api/users?delay=3",
                        DelayedGetResponse.class,
                        aReturn -> {
                            textView.append(aReturn.get().toString());
                            Log.d(DaemonUtils.tag(), "LINK 3");
                            chain.next();
                        }
                )
        ).addLink(() ->
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
