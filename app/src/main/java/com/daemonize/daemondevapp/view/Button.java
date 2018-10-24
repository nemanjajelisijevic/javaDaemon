package com.daemonize.daemondevapp.view;

import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;

public class Button extends CompositeImageViewImpl implements ClickableImageView {

    private Runnable onClickCallback;

    public Button(int relX, int relY, Image image) {
        super(relX, relY, image);
    }

    public Button(float absX, float absY, int z, Image image) {
        super(absX, absY, z, image);
    }

    @Override
    public void addChild(Image image, Pair<Integer, Integer> coordinates) {
        throw new IllegalStateException("Button can not have nested views!");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Button onClick(Runnable onclick) {
        this.onClickCallback = onclick;
        return this;
    }

    @Override
    public boolean checkCoordinates(float x, float y) {
        if (x >= (getAbsoluteX() - getxOffset()) && x <= (getAbsoluteX() + getxOffset())) {
            if (y >= (getAbsoluteY() - getyOffset()) && y <= (getAbsoluteY() + getyOffset())) {
                onClickCallback.run();//TODO should this be here?????
                return true;
            }
        }
        return false;
    }
}
