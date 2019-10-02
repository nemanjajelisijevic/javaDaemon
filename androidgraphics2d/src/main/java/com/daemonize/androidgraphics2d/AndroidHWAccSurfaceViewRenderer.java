package com.daemonize.androidgraphics2d;


import android.graphics.Canvas;
import android.view.SurfaceView;

public class AndroidHWAccSurfaceViewRenderer extends AndroidSurfaceViewRenderer {

    @Override
    protected Canvas getCanvas() {
        return surfaceHolder.lockHardwareCanvas();
    }

    public AndroidHWAccSurfaceViewRenderer(SurfaceView surfaceView) {
        super(surfaceView);
    }
}
