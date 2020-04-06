package com.daemonize.imagemovers;

import com.daemonize.graphics2d.images.Image;

import java.util.ArrayList;
import java.util.List;

public class AngleToSpriteArray implements AngleToImageArray {

    @FunctionalInterface
    public interface AngleToSpriteMapper {
        Image[] map(int angle);
    }

    private List<Image[]> roationSpriteList;
    private int noOfDirections;
    private int step;
    private int currentAngleSetter;


    @Override
    public void setCurrentAngle(int degrees) {

    }

    @Override
    public int getStep() {
        return 0;
    }

    public AngleToSpriteArray(int directionsNo) {

        if (directionsNo < 4 && directionsNo % 4 != 0)
            throw new IllegalArgumentException("directionsNo cant be less than 4 and must be divisible by 4. Argument: " + directionsNo);
        this.roationSpriteList = new ArrayList<>(360);
        this.noOfDirections = directionsNo;
        this.step = 360 / noOfDirections;
        this.currentAngleSetter = 0;

    }

    public AngleToSpriteArray addSprite(AngleToSpriteMapper mapper) {

        if (currentAngleSetter > 360 - step)
            return this;

        Image[] sprite = mapper.map(currentAngleSetter);
        currentAngleSetter += step;
        //TODO
        return this;
    }

    @Override
    public Image getCurrent() {
        return null;
    }

    @Override
    public int getCurrentAngle() {
        return 0;
    }

    @Override
    public Image getIncrementedByStep() {
        return null;
    }

    @Override
    public Image getDecrementedByStep() {
        return null;
    }

    @Override
    public Image getByAngle(int degrees) {
        return null;
    }
}
