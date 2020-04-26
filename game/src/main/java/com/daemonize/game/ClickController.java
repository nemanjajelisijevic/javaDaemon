package com.daemonize.game;

import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.game.controller.MouseController;
import com.daemonize.graphics2d.camera.Camera2D;
import com.daemonize.graphics2d.scene.views.FixedButton;

import java.util.ArrayList;
import java.util.List;

public class ClickController implements MouseController {

    private Consumer consumer;

    private ClickCoordinateClosure clickCoordinateClosure;
    private HooverCoordinateClosure hooverCoordinateClosure;
    private Camera2D camera;

    private volatile float clickedX, clickedY;
    private volatile float currentX, currentY;

    private volatile MouseButton currentClickedButton;

    //private DaemonSemaphore clickSemaphore = new DaemonSemaphore().setName("Click Semaphore");
    private List<FixedButton> buttons = new ArrayList<>();

    public ClickController addButton(FixedButton button) {
        this.buttons.add(button);
        return this;
    }

    @Override
    public void setConsumer(Consumer consumer) {
        this.consumer = consumer;
    }

    public void setCamera(Camera2D camera) {
        this.camera = camera;
    }

    @Override
    public void setOnClick(ClickCoordinateClosure clickCoordinateClosure) {
        this.clickCoordinateClosure = clickCoordinateClosure;
    }

    @Override
    public void setOnHoover(HooverCoordinateClosure hooverCoordinateClosure) {
        this.hooverCoordinateClosure  = hooverCoordinateClosure;
    }

    @Override
    public void onClick(MouseButton mouseButton, float x, float y) {
        currentClickedButton = mouseButton;
        clickedX = x;
        clickedY = y;
        //clickSemaphore.go();
        control();
    }

    @Override
    public void onRelease(MouseButton mouseButton) {
        if (currentClickedButton.equals(mouseButton)) {
            currentClickedButton = null;
            //clickSemaphore.stop();
        }
        control();
    }

    @Override
    public void onMove(float x, float y) {

        currentX = x;
        currentY = y;

        if (currentClickedButton != null) {
            clickedX = x;
            clickedY = y;
        }

        control();
    }

    @Override
    public void control() {
        try{
            consumer.consume(() -> {

                for (FixedButton button : buttons) {
                    if (button.checkCoordinates(clickedX, clickedY))
                        return;
                }

                if (currentClickedButton != null)
                    clickCoordinateClosure.onClick(camera.getRenderingX() + clickedX, camera.getRenderingY() + clickedY, currentClickedButton);
                else
                    hooverCoordinateClosure.onHoover(camera.getRenderingX() + currentX, camera.getRenderingY() + currentY);
            });

        } finally {}
    }
}
