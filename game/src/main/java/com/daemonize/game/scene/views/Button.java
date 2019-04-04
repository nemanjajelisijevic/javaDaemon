package com.daemonize.game.scene.views;


import com.daemonize.game.images.Image;

public class Button extends CompositeImageViewImpl implements ClickableImageView<Button> {

    private Runnable onClickCallback;
    private boolean enabled = true;


    public Button(String name, Image image) {
        super(name, image);
    }

    public Button(String name, int relX, int relY, Image image) {
        super(name, relX, relY, image);
    }

    public Button(String name, float absX, float absY, int z, Image image) {
        super(name, absX, absY, z, image);
    }

    @Override
    public Button onClick(Runnable onclick) {
        this.onClickCallback = onclick;
        return this;
    }

    @Override
    public boolean checkCoordinates(float x, float y) {

        if (!enabled)
            return false;

        if (x >= getStartingX() && x <= getEndX()) {
            if (y >= getStartingY() && y <= getEndY() && isShowing()) {
                onClickCallback.run();//TODO should this be here?????
                return true;
            }
        }

        return false;
    }


    public Button disable() {
        enabled = false;
        return this;
    }

    public Button enable() {
        enabled = true;
        return this;
    }
}
