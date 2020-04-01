package com.daemonize.game;

import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.game.controller.MouseController;
import com.daemonize.graphics2d.camera.Camera2D;

public class ClickController implements MouseController {

    private ClickCoordinateClosure clickCoordinateClosure;
    private Camera2D camera;


    private volatile float clickedX, clickedY;
    private volatile MouseButton currentClickedButton;

    private DaemonSemaphore clickSemaphore = new DaemonSemaphore().setName("Click Semaphore");


    public void setCamera(Camera2D camera) {
        this.camera = camera;
    }

    @Override
    public void setOnClick(ClickCoordinateClosure clickCoordinateClosure) {
        this.clickCoordinateClosure = clickCoordinateClosure;
    }

    @Override
    public void setOnHoover(HooverCoordinateClosure hooverCoordinateClosure) {}

    @Override
    public void onClick(MouseButton mouseButton, float x, float y) {
        currentClickedButton = mouseButton;
        clickedX = x;
        clickedY = y;
        clickSemaphore.go();
    }

    @Override
    public void onRelease(MouseButton mouseButton) {
        if (currentClickedButton.equals(mouseButton)) {
            currentClickedButton = null;
            clickSemaphore.stop();
        }
    }

    @Override
    public void onMove(float x, float y) {
        if (currentClickedButton != null) {
            clickedX = x;
            clickedY = y;
        }
    }

    @Override
    public void control() throws InterruptedException {
        try{
            while(currentClickedButton == null)
                clickSemaphore.await();

            clickCoordinateClosure.onClick(camera.getX() + clickedX, camera.getY() + clickedY, currentClickedButton);
        } finally {}
    }
}
