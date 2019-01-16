package com.daemonize.daemondevapp.renderer;

import com.daemonize.daemonengine.consumer.DaemonConsumer;

public class DrawConsumer extends DaemonConsumer {

    private Renderer2D renderer;

    public DrawConsumer(Renderer2D renderer, String name) {
        super(name);
        this.renderer = renderer;
    }

    @Override
    public boolean consume(Runnable runnable) {
        super.consume(runnable);
        this.renderer.setDirty();
        return true;
    }
}
