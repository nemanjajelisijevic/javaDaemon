package com.daemonize.androidgraphics2d;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.daemonize.daemonengine.utils.DaemonUtils;

public class CustomHWAView extends View {

    private CustomHWAViewRenderer renderer;

    public CustomHWAView(Context context) {
        super(context);
    }

    CustomHWAView setRenderer(CustomHWAViewRenderer renderer) {
        this.renderer = renderer;
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (renderer.getDirty())
            renderer.drawSceneFromView(canvas);
    }
}
