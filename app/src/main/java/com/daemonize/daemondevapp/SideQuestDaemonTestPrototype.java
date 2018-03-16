package com.daemonize.daemondevapp;


import android.util.Log;

import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.SideQuest;

@Daemonize(className = "SideQuestDaemonTest")
public class SideQuestDaemonTestPrototype {

    @SideQuest()
    public void test() {
        Log.d(DaemonUtils.tag(), "CNT: " + Long.toString(System.currentTimeMillis()));
    }

}
