package com.daemonize.daemondevapp;


import android.graphics.Bitmap;

import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.SideQuest;

import java.util.Iterator;
import java.util.List;

@Daemonize(className = "RotaterDaemonCustom")
public class ImageRotater {

    public static class PositionedBitmap {
        public Bitmap image;
        public float positionX;
        public float positionY;
    }

    List<Bitmap> sprite;
    Iterator<Bitmap> spriteIterator;

    private float touchX;
    private float touchY;

    private float lastX;
    private float lastY;

    private float movement = 30;
    private float a;

    private float moveX;
    private float moveY;

    public void setTouchCoordinates(float x, float y) {
        touchX = x;
        touchY = y;
    }

    public ImageRotater(List<Bitmap> sprite) {
        this.sprite = sprite;
        spriteIterator = sprite.iterator();
    }

    @SideQuest(SLEEP = 40)
    public PositionedBitmap rotateImage() {

        PositionedBitmap ret = new PositionedBitmap();

        if(!spriteIterator.hasNext()) {
            spriteIterator = sprite.iterator();
        }

        ret.image = spriteIterator.next();
        float diffX = (touchX - lastX);
        float diffY = (touchY - lastY);

        a = Math.abs((movement*diffX)/diffY);

        moveX =  diffX > 0 ? a : - a;
        moveY =  diffY > 0 ? movement : - movement;

        float border = movement + 60;
        //if ((diffX > border || diffX < - border) || (diffY > border || diffY < - border)) {

        if (Math.abs(diffX) > border || Math.abs(diffY) > border) {
            lastX += moveX;
            lastY += moveY;
        }

        ret.positionX = lastX;
        ret.positionY = lastY;

        return ret;

    }

}
