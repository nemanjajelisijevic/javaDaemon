package com.daemonize.graphics2d.scene.views;

import com.daemonize.graphics2d.images.Image;

public class FixedButton extends FixedView {


    private Runnable onClickCallback;
    private boolean enabled = true;

    public FixedButton(String name, int startingX, int startingY, int zIndex, Image image) {
        super(name, startingX, startingY, zIndex, ((float) image.getWidth()), ((float) image.getHeight()));
        this.setImageWithoutOffset(image);
    }

    //@Override
    public FixedButton onClick(Runnable onclick) {
        this.onClickCallback = onclick;
        return this;
    }

    public FixedButton click() {
        onClickCallback.run();
        return this;
    }

    @Override
    public boolean checkCoordinates(float x, float y) {

        if (!enabled || onClickCallback == null)
            return false;

        if (x >= getRenderingX() && x <= getEndX()) {
            if (y >= getRenderingY() && y <= getEndY() && isShowing()) {
                onClickCallback.run();//TODO should this be here?????
                return true;
            }
        }

        return false;
    }


    public FixedButton disable() {
        enabled = false;
        return this;
    }

    public FixedButton enable() {
        enabled = true;
        return this;
    }
}
