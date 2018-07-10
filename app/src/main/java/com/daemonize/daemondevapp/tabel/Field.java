package com.daemonize.daemondevapp.tabel;

import android.support.annotation.NonNull;

public class Field implements  IHeapItem <Field> {
    int centerX;
    int centerY;

    int row; //i - n
    int colon;//j - m


    public int gCost;
    public int hCost;

    int weight;

    boolean walkable;
    Field parent;

    int heapIndex;

    public Field(int centerX, int centerY, int row, int colon, int weight, boolean walkable) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.row = row;
        this.colon = colon;
        this.walkable = walkable;
        this.weight = weight;
    }

    public int getCenterX() {
        return centerX;
    }

    public void setCenterX(int centerX) {
        this.centerX = centerX;
    }

    public int getCenterY() {
        return centerY;
    }

    public void setCenterY(int centerY) {
        this.centerY = centerY;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getColon() {
        return colon;
    }

    public void setColon(int colon) {
        this.colon = colon;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int fCost(){
        return gCost+hCost;
    }

    public boolean isWalkable() {
        return walkable;
    }

    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }

    @Override
    public int getHeapIndex() {
        return heapIndex;
    }

    @Override
    public int setHeapIndex(int index) {
        return heapIndex = index;
    }

    @Override
    public int compareTo(Field fieldToCompare) {
        if (this.fCost() == fieldToCompare.fCost()) {
            if (this.hCost == fieldToCompare.hCost) {
                return 0;
            } else {
                if (this.hCost < fieldToCompare.hCost) {
                    return -1;
                } else {
                    return 1;
                }
            }
        } else {
            if (this.fCost() < fieldToCompare.fCost()) {
                return -1;
            } else {
                return 1;
            }
        }
    }

}



