package com.daemonize.game;

import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.game.appstate.DaemonState;
import com.daemonize.game.appstate.TransientState1;
import com.daemonize.game.controller.TouchController;
import com.daemonize.graphics2d.images.Image;
import com.daemonize.graphics2d.renderer.Renderer2D;
import com.daemonize.graphics2d.scene.Scene2D;
import com.daemonize.graphics2d.scene.views.Button;
import com.daemonize.graphics2d.scene.views.ImageViewImpl;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.GenerateRunnable;

public class InitState extends DaemonState<InitState> {

    @Daemonize
    public static class DummyStatePreparer {
        @GenerateRunnable
        public void prepareSummyScene() throws InterruptedException {
            Thread.sleep(10000);
        }
    }

    private Renderer2D renderer;
    private Scene2D initScene;
    private Button startButton;

    private TouchController controller;

    private int borderX;
    private int borderY;

    private Image backgroundImage;
    private Image startButtonImage;

    public InitState(
            Renderer2D renderer,
            TouchController controller,
            Image backgroundImage,
            Image startButtonImage,
            Pair<Integer, Integer> borders
    ) {
        this.renderer = renderer;
        this.controller = controller;
        this.initScene = new Scene2D();
        this.backgroundImage = backgroundImage;
        this.startButtonImage = startButtonImage;
        this.borderX = borders.getFirst();
        this.borderY = borders.getSecond();
    }

    @Override
    protected void onEnter() {

        initScene.addImageView(new ImageViewImpl("background").setImageWithoutOffset(backgroundImage).setAbsoluteX(0).setAbsoluteY(0).setZindex(0).show());





        startButton = (Button) initScene.addImageView(new Button("Start", borderX / 2, borderY / 2, 1, startButtonImage).hide());
        startButton.onClick(()->transition(new TransientState1(1)));

        initScene.lockViews();
        renderer.setScene(initScene).start();
    }

    @Override
    protected void onExit() {
        initScene.unlockViews();
        renderer.stop();
    }
}
