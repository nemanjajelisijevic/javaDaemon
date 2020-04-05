package com.daemonize.game;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.closure.Return;
import com.daemonize.graphics2d.scene.views.ImageView;
import com.daemonize.imagemovers.ImageMover;

public class PlayerCameraClosure implements Closure<ImageMover.PositionedImage[]> {

    private ImageView mainView, hpView, searchlightView;

    public PlayerCameraClosure setMainView(ImageView mainView) {
        this.mainView = mainView;
        return this;
    }

    public PlayerCameraClosure setHpView(ImageView hpView) {
        this.hpView = hpView;
        return this;
    }

    public PlayerCameraClosure setSearchlightView(ImageView searchlightView) {
        this.searchlightView = searchlightView;
        return this;
    }

    public PlayerCameraClosure(ImageView mainView, ImageView hpView, ImageView searchlightView) {
        this.mainView = mainView;
        this.hpView = hpView;
        this.searchlightView = searchlightView;
    }

    @Override
    public void onReturn(Return<ImageMover.PositionedImage[]> ret) {
        ImageMover.PositionedImage[] result = ret.runtimeCheckAndGet();
        mainView.setAbsoluteX(result[0].positionX)
                .setAbsoluteY(result[0].positionY)
                .setImage(result[0].image);
        hpView.setAbsoluteX(result[1].positionX)
                .setAbsoluteY(result[1].positionY)
                .setImage(result[1].image);
        searchlightView.setAbsoluteX(result[2].positionX)
                .setAbsoluteY(result[2].positionY)
                .setImage(result[2].image);
    }
}