package com.daemonize.game.interactables.health;


import com.daemonize.game.Mortal;
import com.daemonize.game.interactables.Interactable;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.scene.Scene2D;
import com.daemonize.graphics2d.scene.views.ImageView;
import com.daemonize.graphics2d.scene.views.ImageViewImpl;

public class HealthPack implements Interactable {

    private final int hpBoost;
    private final ImageView view;

    private Mortal interactor;

    public HealthPack(Mortal interactor,int hpBoost, ImageView view) {
        this.interactor = interactor;
        this.hpBoost = hpBoost;
        this.view = view;
    }

    @Override
    public ImageView getView() {
        return view;
    }

    @Override
    public void interact() {
        interactor.setHp(interactor.getHp() + hpBoost);
    }

    public static HealthPack generateHealthPack(Mortal interactor, int hpBoost, int x, int y, Image image, Scene2D scene2D) {
        return new HealthPack(
                interactor,
                hpBoost,
                scene2D.addImageView(new ImageViewImpl("Health Pack view")
                        .setAbsoluteX(x)
                        .setAbsoluteY(y)
                        .setImage(image)
                        .setZindex(7)
                        .show()
                )
        );
    }
}
