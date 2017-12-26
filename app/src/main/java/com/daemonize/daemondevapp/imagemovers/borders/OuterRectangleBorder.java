package com.daemonize.daemondevapp.imagemovers.borders;


import android.util.Pair;

public class OuterRectangleBorder implements Border {


    private float minX;
    private float maxX;

    private float minY;
    private float maxY;

    public float getMinX() {
        return minX;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMinY() {
        return minY;
    }

    public float getMaxY() {
        return maxY;
    }

    public OuterRectangleBorder(float minX, float maxX, float minY, float maxY) {
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public Pair<Pair<Boolean, Boolean>, Pair<Boolean, Boolean>> checkBorder(float objectsX, float objectsY) {

        boolean hitMinX = false;
        boolean hitMaxX = false;

        boolean hitMinY = false;
        boolean hitMaxY = false;

        if (objectsX >= minX) {
            hitMinX = true;
        } else if (objectsX <= maxX){
            hitMaxX = true;
        }


        if (objectsY >= minY) {
            hitMinY = true;
        } else if (objectsY <= maxY){
            hitMaxY = true;
        }


        return Pair.create(Pair.create(hitMinX, hitMaxX), Pair.create(hitMinY, hitMaxY));
    }

}
