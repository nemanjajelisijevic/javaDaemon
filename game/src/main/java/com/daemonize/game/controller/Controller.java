package com.daemonize.game.controller;

public interface Controller {



    void control() throws InterruptedException;

//    default <T extends Controller> T getController(Class<T> controllerClass) {
//
//
//        Class<MovementController> claz = MovementController.class;
//
//        controllerClass.ins
//    }
}
