package com.daemonize.game;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.daemonprocessor.annotations.Daemon;
import com.daemonize.daemonprocessor.annotations.Daemonize;
import com.daemonize.daemonprocessor.annotations.GenerateRunnable;
import com.daemonize.game.controller.KeyboardMovementController;
import com.daemonize.game.controller.MovementController;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KeyBoardMovementControllerImpl implements KeyboardMovementController<PlayerDaemon> {

    @Daemon
    public static class SpeedBoostHolder {

        private long boostDureationMs;

        public SpeedBoostHolder(long boostDureationMs) {
            this.boostDureationMs = boostDureationMs;
        }

        @Daemonize
        @GenerateRunnable
        public void holdBoost() throws InterruptedException {
            Thread.sleep(boostDureationMs);
        }
    }

    private SpeedBoostHolderDaemon boostHolder;

    private PlayerDaemon controllable;
    private Consumer consumer;
    private OnMovementCompleteCallback<PlayerDaemon> movementCallback;

    private DirectionToCoordinateMapper dirMapper;

    private float distanceOffset;
    private float diagonalDistanceOffset;

    private final float controllerVelocity;
    private final float speedUpVelocity;

    private volatile float currentVelocity;
    private LinkedList<Direction> pressedDirections;

    private Lock queueLock;
    private Condition queueEmptyCondition;

    private Pair<Boolean, Boolean> contorlMovementCondition;
    private DaemonSemaphore controlBlockingSemaphore;

    //first for rotation second for translation
    private Runnable rotationControlClosure = () -> {
        contorlMovementCondition.setFirst(true);
        if(contorlMovementCondition.getFirst() && contorlMovementCondition.getSecond()) {
            controlBlockingSemaphore.go();
            if (movementCallback != null)
                consumer.consume(() -> movementCallback.onMovementComplete(controllable));
        }
    };

    private Closure<Boolean> translationControlClosure = ret -> {
        contorlMovementCondition.setSecond(true);
        if(contorlMovementCondition.getFirst() && contorlMovementCondition.getSecond()) {
            controlBlockingSemaphore.go();
            if (movementCallback != null)
                consumer.consume(() -> movementCallback.onMovementComplete(controllable));
        }
    };

    public KeyBoardMovementControllerImpl() {

        this.pressedDirections = new LinkedList<>();
        this.queueLock = new ReentrantLock();
        this.queueEmptyCondition = queueLock.newCondition();

        this.controllerVelocity = 4.5F;
        this.speedUpVelocity = controllerVelocity * 4;
        this.currentVelocity = controllerVelocity;

        this.controlBlockingSemaphore = new DaemonSemaphore();
        this.contorlMovementCondition = Pair.create(false, false);
        //this.boostHolder = new SpeedBoostHolderDaemon(null, new SpeedBoostHolder(1000)).setName("Boost Holder").start();
    }

    public KeyBoardMovementControllerImpl setConsumer(Consumer consumer) {
        this.consumer = consumer;
        //this.boostHolder.setConsumer(consumer);
        return this;
    }

    public KeyBoardMovementControllerImpl setDistanceOffset(float distanceOffset) {
        this.distanceOffset = distanceOffset;
        return this;
    }

    public KeyBoardMovementControllerImpl setDiagonalDistanceOffset(float diagonalDistanceOffset) {
        this.diagonalDistanceOffset = diagonalDistanceOffset;
        return this;
    }

    @Override
    public void setMovementCallback(OnMovementCompleteCallback<PlayerDaemon> movementCallback) {
        this.movementCallback = movementCallback;
    }

    @Override
    public void setDirMapper(DirectionToCoordinateMapper dirMapper) {
        this.dirMapper = dirMapper;
    }

    @Override
    public void pressDirection(Direction dir) {

        queueLock.lock();

        if (pressedDirections.isEmpty()) {

            pressedDirections.add(dir);
            queueEmptyCondition.signalAll();

        } else {

            if (pressedDirections.size() == 1 && !pressedDirections.get(0).equals(dir))
                pressedDirections.add(dir);
            else if (pressedDirections.size() == 2 && !pressedDirections.contains(dir)) {
                pressedDirections.poll();
                pressedDirections.add(dir);
            }
        }

        queueLock.unlock();
    }

    @Override
    public void releaseDirection(Direction dir) {

        queueLock.lock();

        if(!pressedDirections.isEmpty() && pressedDirections.get(0).equals(dir)) {

            pressedDirections.poll();

            if (!pressedDirections.isEmpty() && pressedDirections.get(0).equals(dir))
                pressedDirections.poll();

        } else if (pressedDirections.size() == 2 && pressedDirections.get(1).equals(dir))
            pressedDirections.remove(1);

        queueLock.unlock();
    }

    @Override
    public void setControllable(PlayerDaemon player) {
        this.controllable = player;
    }

    @Override
    public void speedUp() {
        currentVelocity = speedUpVelocity;
        controllable.setVelocity(currentVelocity);
//
//        boostHolder.holdBoost(() -> {
//
//            currentVelocity = controllerVelocity;
//            controllable.setVelocity(currentVelocity);
//
//        });

    }

    @Override
    public void speedDown() {
        currentVelocity = controllerVelocity;
        controllable.setVelocity(currentVelocity);
    }

    @Override
    public void control() throws InterruptedException {

        try {

            controlBlockingSemaphore.await();

            queueLock.lock();

            while (pressedDirections.isEmpty())
                queueEmptyCondition.await();

            Pair<Float, Float> playerCoord = controllable.getLastCoordinates();

            Pair<Float, Float> nextCoords = null;

            if(pressedDirections.size() == 1) {

                MovementController.Direction dir = pressedDirections.peek();
                nextCoords = dirMapper.map(dir);

                controlBlockingSemaphore.stop();
                contorlMovementCondition.setFirst(false).setSecond(false);

                controllable.rotateTowards(nextCoords, rotationControlClosure)
                        .goTo(nextCoords, currentVelocity, translationControlClosure);

            } else if (pressedDirections.size() == 2) {

                MovementController.Direction dirOne = pressedDirections.get(0);
                MovementController.Direction dirTwo = pressedDirections.get(1);

                switch (dirOne) {
                    case UP:

                        if (dirTwo.equals(Direction.RIGHT))
                            nextCoords = dirMapper.map(Direction.UP_RIGHT);
                        else if (dirTwo.equals(Direction.LEFT))
                            nextCoords = dirMapper.map(Direction.UP_LEFT);
                        else
                            nextCoords = playerCoord;

                        break;

                    case DOWN:

                        if (dirTwo.equals(Direction.RIGHT))
                            nextCoords = dirMapper.map(Direction.DOWN_RIGHT);
                        else if (dirTwo.equals(Direction.LEFT))
                            nextCoords = dirMapper.map(Direction.DOWN_LEFT);
                        else
                            nextCoords = playerCoord;

                        break;

                    case LEFT:

                        if (dirTwo.equals(Direction.UP))
                            nextCoords = dirMapper.map(Direction.UP_LEFT);
                        else if (dirTwo.equals(Direction.DOWN))
                            nextCoords = dirMapper.map(Direction.DOWN_LEFT);
                        else
                            nextCoords = playerCoord;

                        break;

                    case RIGHT:

                        if (dirTwo.equals(Direction.UP))
                            nextCoords = dirMapper.map(Direction.UP_RIGHT);
                        else if (dirTwo.equals(Direction.DOWN))
                            nextCoords = dirMapper.map(Direction.DOWN_RIGHT);
                        else
                            nextCoords = playerCoord;

                        break;

                    default:
                        throw new IllegalStateException("Unknown direction" + dirOne + ", dirTwo " + dirTwo);


                }

                controlBlockingSemaphore.stop();
                contorlMovementCondition.setFirst(false).setSecond(false);
                controllable.rotateTowards(nextCoords, rotationControlClosure)
                        .goTo(nextCoords, currentVelocity, translationControlClosure);

            } else {
                throw new IllegalStateException("Dir buffer: " + pressedDirections.toString());
            }

        } finally {
            queueLock.unlock();
        }
    }
}