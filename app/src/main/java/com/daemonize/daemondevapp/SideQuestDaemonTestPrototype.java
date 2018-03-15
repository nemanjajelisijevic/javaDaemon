package com.daemonize.daemondevapp;


import android.util.Log;

import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonprocessor.Daemonize;
import com.daemonize.daemonprocessor.SideQuest;

@Daemonize(className = "SideQuestDaemonTest")
public class SideQuestDaemonTestPrototype {

    @SideQuest()
    public void test() {
        Log.d(DaemonUtils.tag(), "CNT: " + Long.toString(System.currentTimeMillis()));
    }

}
