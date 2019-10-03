package com.daemonize.androidgraphics2d;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;


public class CustomHWAView extends View {

    private CustomHWAViewRenderer renderer;

    public CustomHWAView(Context context) {
        super(context);
        setWillNotDraw(false);
    }

    CustomHWAView setRenderer(CustomHWAViewRenderer renderer) {
        this.renderer = renderer;
        return this;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (renderer != null && renderer.getDirty())
            renderer.setCanvas(canvas).drawSceneFromView();
        postInvalidateOnAnimation();
    }
}
