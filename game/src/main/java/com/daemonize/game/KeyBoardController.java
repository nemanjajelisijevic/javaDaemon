package com.daemonize.game;

import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.game.controller.DirectionController;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KeyBoardController implements DirectionController<PlayerDaemon> {

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

    private Runnable translationControlClosure = () -> {
        contorlMovementCondition.setSecond(true);
        if(contorlMovementCondition.getFirst() && contorlMovementCondition.getSecond()) {
            controlBlockingSemaphore.go();
            if (movementCallback != null)
                consumer.consume(() -> movementCallback.onMovementComplete(controllable));
        }
    };

    public KeyBoardController() {

        this.pressedDirections = new LinkedList<>();
        this.queueLock = new ReentrantLock();
        this.queueEmptyCondition = queueLock.newCondition();

        this.controllerVelocity = 2.5F;
        this.speedUpVelocity = controllerVelocity * 4;
        this.currentVelocity = controllerVelocity;

        this.controlBlockingSemaphore = new DaemonSemaphore();
        this.contorlMovementCondition = Pair.create(false, false);
    }

    public KeyBoardController setConsumer(Consumer consumer) {
        this.consumer = consumer;
        return this;
    }

    public KeyBoardController setDistanceOffset(float distanceOffset) {
        this.distanceOffset = distanceOffset;
        return this;
    }

    public KeyBoardController setDiagonalDistanceOffset(float diagonalDistanceOffset) {
        this.diagonalDistanceOffset = diagonalDistanceOffset;
        return this;
    }

    public KeyBoardController setMovementCallback(OnMovementCompleteCallback<PlayerDaemon> movementCallback) {
        this.movementCallback = movementCallback;
        return this;
    }

    public KeyBoardController setDirMapper(DirectionToCoordinateMapper dirMapper) {
        this.dirMapper = dirMapper;
        return this;
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

                DirectionController.Direction dir = pressedDirections.peek();
                nextCoords = dirMapper.map(dir);

            } else if (pressedDirections.size() == 2) {

                DirectionController.Direction dirOne = pressedDirections.get(0);
                DirectionController.Direction dirTwo = pressedDirections.get(1);

                switch (dirOne) {
                    case UP:

                        if (dirTwo.equals(Direction.RIGHT))
                            nextCoords = dirMapper.map(Direction.UP_RIGHT);
                        else if (dirTwo.equals(Direction.LEFT))
                            nextCoords = dirMapper.map(Direction.UP_LEFT);

                        break;

                    case DOWN:

                        if (dirTwo.equals(Direction.RIGHT))
                            nextCoords = dirMapper.map(Direction.DOWN_RIGHT);
                        else if (dirTwo.equals(Direction.LEFT))
                            nextCoords = dirMapper.map(Direction.DOWN_LEFT);

                        break;

                    case LEFT:

                        if (dirTwo.equals(Direction.UP))
                            nextCoords = dirMapper.map(Direction.UP_LEFT);
                        else if (dirTwo.equals(Direction.DOWN))
                            nextCoords = dirMapper.map(Direction.DOWN_LEFT);

                        break;

                    case RIGHT:

                        if (dirTwo.equals(Direction.UP))
                            nextCoords = dirMapper.map(Direction.UP_RIGHT);
                        else if (dirTwo.equals(Direction.DOWN))
                            nextCoords = dirMapper.map(Direction.DOWN_RIGHT);

                        break;

                    default:
                        throw new IllegalStateException("Unknown direction" + dirOne + ", dirTwo " + dirTwo);

                }
            }

            controlBlockingSemaphore.stop();
            contorlMovementCondition.setFirst(false).setSecond(false);
            controllable.rotateTowards(nextCoords, rotationControlClosure)
                    .goTo(nextCoords, currentVelocity, translationControlClosure);

        } finally {
            queueLock.unlock();
        }
    }
}