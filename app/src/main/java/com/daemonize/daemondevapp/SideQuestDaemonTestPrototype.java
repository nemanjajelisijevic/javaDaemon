package com.daemonize.daemondevapp;


import android.util.Log;

import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonprocessor.Daemonize;
import com.daemonize.daemonprocessor.SideQuest;

@Daemonize(className = "SideQuestDaemonTest")
public class SideQuestDaemonTestPrototype {

    private int cnt;

    @SideQuest(SLEEP = 5000)
    public void test() {
        Log.d(DaemonUtils.tag(), "CNT: " + Integer.toString(cnt++));
    }

}
