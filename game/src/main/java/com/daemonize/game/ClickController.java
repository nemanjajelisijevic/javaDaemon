package com.daemonize.game;

import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.game.controller.MouseController;

public class ClickController implements MouseController {

    ClickCoordinateClosure clickCoordinateClosure;

    private volatile float clickedX, clickedY;
    private volatile MouseButton currentClickedButton;


    private DaemonSemaphore clickSemaphore = new DaemonSemaphore().setName("Click Semaphore");

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

            clickCoordinateClosure.onClick(clickedX, clickedY, currentClickedButton);

        } finally {}
    }
}
