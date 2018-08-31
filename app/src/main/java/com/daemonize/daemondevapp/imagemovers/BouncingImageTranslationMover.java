package com.daemonize.daemondevapp.imagemovers;


import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;

import java.util.List;

public class BouncingImageTranslationMover extends ImageTranslationMover {

    private float proximityDistance = 50;

    public BouncingImageTranslationMover(
            Image [] sprite,
            float velocity,
            Pair<Float, Float> startingPos) {
        super(sprite, velocity, startingPos);
        if (sprite != null && sprite.length > 0) {
            proximityDistance = sprite[0].getHeight() > sprite[0].getWidth()
                    ? sprite[0].getHeight() / 2 : sprite[0].getWidth() / 2;
        }
    }

    @Override
    public BouncingImageTranslationMover setBorders(float x, float y) {
        super.setBorders(x, y);
        return this;
    }

    @Override
    public PositionedImage animate() {


        PositionedImage ret = new PositionedImage();
        ret.image = iterateSprite();

        //awaitForMovement();

        if(velocity.intensity > 0) {

            velocity.intensity -= 0.3;

            //check borders and recalculate
            if (lastX <= 0) {
                velocity.direction.coeficientX = - velocity.direction.coeficientX;
                lastX = 0;
            } else if (lastX >= borderX) {
                velocity.direction.coeficientX = - velocity.direction.coeficientX;
                lastX = borderX;
            }

            if(lastY <= 0) {
                velocity.direction.coeficientY = - velocity.direction.coeficientY;
                lastY = 0;
            } else if( lastY >= borderY) {
                velocity.direction.coeficientY = - velocity.direction.coeficientY;
                lastY = borderY;
            }


            lastX += velocity.intensity * (velocity.direction.coeficientX * 0.01f);
            lastY += velocity.intensity * (velocity.direction.coeficientY * 0.01f);


            ret.positionX = lastX;
            ret.positionY = lastY;

            return ret;

        }



        return null;
    }
}
