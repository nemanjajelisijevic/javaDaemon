package com.daemonize.androidgraphics2d;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public class AndroidCustomHWAView extends View {

    private AndroidCustomHWAViewRenderer renderer;

    public AndroidCustomHWAView(Context context) {
        super(context);
    }

    AndroidCustomHWAView setRenderer(AndroidCustomHWAViewRenderer renderer) {
        this.renderer = renderer;
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (renderer.getDirty())
            renderer.drawSceneFromView(canvas);
    }
}
