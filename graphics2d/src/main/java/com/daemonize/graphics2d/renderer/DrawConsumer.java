package com.daemonize.graphics2d.renderer;

import com.daemonize.daemonengine.consumer.DaemonConsumer;
import com.daemonize.daemonengine.utils.BoundedBufferQueue;

public class DrawConsumer extends DaemonConsumer {

    private Renderer2D renderer;

    public DrawConsumer(Renderer2D renderer, String name) {
        super(name, new BoundedBufferQueue<Runnable>(200));
        this.renderer = renderer;
    }

    @Override
    public boolean consume(Runnable runnable) {
        boolean ret = super.consume(runnable);
        this.renderer.setDirty();
        return ret;
    }
}
