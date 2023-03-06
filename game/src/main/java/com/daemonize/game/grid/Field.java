package com.daemonize.game.grid;

import com.daemonize.daemonengine.utils.Pair;

import java.util.ArrayList;
import java.util.List;

public class Field<T> implements  IHeapItem , Comparable {

    public List<Integer> zElevation;
    public volatile int zElevator;

    private Pair<Float, Float> centerCoordinates = Pair.create(Float.NaN, Float.NaN);


//
//    float centerX;
//    float centerY;

    int row; //i - n
    int column;//j - m

    public int gCost;
    public int hCost;

    int weight;

    boolean walkable;
    int heapIndex;

    private T object;


    public T getObject() {
        return object;
    }

    public Field setObject(T object) {
        this.object = object;
        return this;
    }

    public synchronized int zElevationsSize() {
        return zElevation.size();
    }

    public synchronized Field<T> addZElevation(int z) {
        if (!zElevation.contains(z))
            zElevation.add(z);
        return this;
    }

    public synchronized void clearElevations() {
        zElevation.clear();
    }

    public synchronized int iterateElevate() {

        zElevator = ++zElevator % zElevation.size();

        return zElevation.get(zElevator);

    }

    public Field(){
        this.zElevation = new ArrayList<>(1);
    }

    public Field(float centerX, float centerY, int row, int column, int weight, boolean walkable) {
        this.centerCoordinates.setFirst(centerX);
        this.centerCoordinates.setSecond(centerY);
        this.row = row;
        this.column = column;
        this.walkable = walkable;
        this.weight = weight;
        this.zElevation = new ArrayList<>(1);
    }

    public Field (Field<T> field) { // copy constructor
        this.centerCoordinates.setFirst(field.centerCoordinates.getFirst());
        this.centerCoordinates.setSecond(field.centerCoordinates.getSecond());
        this.row = field.getRow();
        this.column = field.getColumn();
        this.walkable = field.isWalkable();
        this.weight = field.getWeight();
        this.gCost = field.gCost;
        this.hCost = field.hCost;
        this.heapIndex = field.getHeapIndex();
        this.object = field.object;
        this.zElevation = new ArrayList<>(field.zElevation.size());
        //this.zElevation.clear();

        for(Integer eleveation : field.zElevation) {
            this.zElevation.add(eleveation);
        }
    }

    public float getCenterX() {
        return centerCoordinates.getFirst();
    }

    public void setCenterX(int centerX) {
        this.centerCoordinates.setFirst(((float) centerX));
    }

    public float getCenterY() {
        return centerCoordinates.getSecond();
    }

    public void setCenterY(int centerY) {
        this.centerCoordinates.setSecond(((float) centerY));
    }

    public Pair<Float, Float> getCenterCoords() {
        return centerCoordinates;
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
    public int compareTo(Object fieldToCompare) {

        if (fieldToCompare instanceof Field) {

            Field<T> other = (Field<T>) fieldToCompare;

            if (this.fCost() == other.fCost()) {
                if (this.hCost == other.hCost) {
                    return 0;
                } else {
                    if (this.hCost < other.hCost) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            } else {
                if (this.fCost() < other.fCost()) {
                    return -1;
                } else {
                    return 1;
                }
            }
        } else
            return 0;
    }


    @Override
    public String toString() {
        return "Field[" + row +"][" + column + "]" + " - CenterX: " + centerCoordinates.getFirst()
                + ", CenterY: " + centerCoordinates.getSecond()
                + ", Walkable : " + walkable
                + ", Weight: " + weight
                + ", GCost: " + gCost
                + ", HCost: " + hCost;
    }
}



