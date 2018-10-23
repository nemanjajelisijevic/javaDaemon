package com.daemonize.daemondevapp.view;

import com.daemonize.daemondevapp.Pair;
import com.daemonize.daemondevapp.images.Image;

public class Button extends CompositeImageViewImpl implements ClickableImageView {

    private Runnable onClickCallback;

    public Button(int x, int y, int z, Image image) {
        super(x, y, z, image);
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
