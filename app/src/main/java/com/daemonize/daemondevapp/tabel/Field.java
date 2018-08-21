package com.daemonize.daemondevapp.tabel;

import android.support.annotation.NonNull;

import com.daemonize.daemondevapp.TowerDaemon;
import com.daemonize.daemonengine.dummy.DummyDaemon;

public class Field implements  IHeapItem <Field> {
    int centerX;
    int centerY;

    int row; //i - n
    int column;//j - m


    public int gCost;
    public int hCost;

    int weight;

    boolean walkable;
    int heapIndex;

    private TowerDaemon tower;

    public TowerDaemon getTower() {
        return tower;
    }

    public Field setTower(TowerDaemon tower) {
        this.tower = tower;
        return this;
    }

    public Field(int centerX, int centerY, int row, int column, int weight, boolean walkable) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.row = row;
        this.column = column;
        this.walkable = walkable;
        this.weight = weight;
    }
    public Field (Field field) {
        this.centerX = field.centerX;
        this.centerY = field.centerY;
        this.row = field.getRow();
        this.column = field.getColumn();
        this.walkable = field.isWalkable();
        this.weight = field.getWeight();
        this.gCost = field.gCost;
        this.hCost = field.hCost;
        this.heapIndex = field.getHeapIndex();


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

    public int getColumn() {
        return column;
    }

    public void setColumn(int column) {
        this.column = column;
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



