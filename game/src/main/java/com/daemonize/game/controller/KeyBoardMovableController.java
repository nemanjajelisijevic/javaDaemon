package com.daemonize.game;

import com.daemonize.daemonengine.closure.Closure;
import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonSemaphore;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.game.controller.KeyboardMovementController;
import com.daemonize.game.controller.MovementController;
import com.daemonize.imagemovers.Movable;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class KeyBoardMovableController<M extends Movable> implements KeyboardMovementController<M> {

    public static class MovingParameterPack {
        public Pair<Float, Float> nextCoords;
        public float velocity;
        public Runnable rotationClosure;
        public Closure<Boolean> translationClosure;
    }

    @FunctionalInterface
    public interface ControllableAction<M> {
        void execute(M controllable, MovingParameterPack  movingParameterPack);
    }

    private M controllable;
    private ControllableAction<M> movingAction;
    private MovingParameterPack paramPack;

    public KeyboardMovementController<M> setMovingAction(ControllableAction<M> movingAction) {
        this.movingAction = movingAction;
        return this;
    }

    private Consumer consumer;
    private OnMovementCompleteCallback<M> movementCallback;

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

//    //first for rotation second for translation
//    private Runnable rotationControlClosure = () -> {
//        contorlMovementCondition.setFirst(true);
//        if(contorlMovementCondition.getFirst() && contorlMovementCondition.getSecond()) {
//            controlBlockingSemaphore.go();
//            if (movementCallback != null)
//                consumer.consume(() -> movementCallback.onMovementComplete(controllable));
//        }
//    };
//
//    private Runnable translationControlClosure = () -> {
//        contorlMovementCondition.setSecond(true);
//        if(contorlMovementCondition.getFirst() && contorlMovementCondition.getSecond()) {
//            controlBlockingSemaphore.go();
//            if (movementCallback != null)
//                consumer.consume(() -> movementCallback.onMovementComplete(controllable));
//        }
//    };

    public KeyBoardMovableController() {

        this.pressedDirections = new LinkedList<>();
        this.queueLock = new ReentrantLock();
        this.queueEmptyCondition = queueLock.newCondition();

        this.controllerVelocity = 4.5F;
        this.speedUpVelocity = controllerVelocity * 4;
        this.currentVelocity = controllerVelocity;

        this.controlBlockingSemaphore = new DaemonSemaphore();
        this.contorlMovementCondition = Pair.create(false, false);
        this.paramPack = new MovingParameterPack();
        this.paramPack.rotationClosure =  () -> {
            contorlMovementCondition.setFirst(true);
            if(contorlMovementCondition.getFirst() && contorlMovementCondition.getSecond()) {
                controlBlockingSemaphore.go();
                if (movementCallback != null)
                    consumer.consume(() -> movementCallback.onMovementComplete(controllable));
            }
        };

        this.paramPack.translationClosure = ret -> {
            contorlMovementCondition.setSecond(true);
            if(contorlMovementCondition.getFirst() && contorlMovementCondition.getSecond()) {
                controlBlockingSemaphore.go();
                if (movementCallback != null)
                    consumer.consume(() -> movementCallback.onMovementComplete(controllable));
            }
        };
    }

    public KeyboardMovementController<M> setConsumer(Consumer consumer) {
        this.consumer = consumer;
        return this;
    }

    public KeyboardMovementController<M> setDistanceOffset(float distanceOffset) {
        this.distanceOffset = distanceOffset;
        return this;
    }

    public KeyboardMovementController<M> setDiagonalDistanceOffset(float diagonalDistanceOffset) {
        this.diagonalDistanceOffset = diagonalDistanceOffset;
        return this;
    }

    @Override
    public void setMovementCallback(OnMovementCompleteCallback<M> movementCallback) {
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
    public void setControllable(M controllable) {
        this.controllable = controllable;
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

            //Pair<Float, Float> nextCoords = null;

            paramPack.velocity = currentVelocity;

            if(pressedDirections.size() == 1) {

                MovementController.Direction dir = pressedDirections.peek();

                paramPack.nextCoords = dirMapper.map(dir);

                controlBlockingSemaphore.stop();
                contorlMovementCondition.setFirst(false).setSecond(false);

                movingAction.execute(controllable, paramPack);

            } else if (pressedDirections.size() == 2) {

                MovementController.Direction dirOne = pressedDirections.get(0);
                MovementController.Direction dirTwo = pressedDirections.get(1);

                switch (dirOne) {
                    case UP:

                        if (dirTwo.equals(Direction.RIGHT))
                            paramPack.nextCoords = dirMapper.map(Direction.UP_RIGHT);
                        else if (dirTwo.equals(Direction.LEFT))
                            paramPack.nextCoords = dirMapper.map(Direction.UP_LEFT);
                        else
                            paramPack.nextCoords = playerCoord;

                        break;

                    case DOWN:

                        if (dirTwo.equals(Direction.RIGHT))
                            paramPack.nextCoords = dirMapper.map(Direction.DOWN_RIGHT);
                        else if (dirTwo.equals(Direction.LEFT))
                            paramPack.nextCoords = dirMapper.map(Direction.DOWN_LEFT);
                        else
                            paramPack.nextCoords = playerCoord;

                        break;

                    case LEFT:

                        if (dirTwo.equals(Direction.UP))
                            paramPack.nextCoords = dirMapper.map(Direction.UP_LEFT);
                        else if (dirTwo.equals(Direction.DOWN))
                            paramPack.nextCoords = dirMapper.map(Direction.DOWN_LEFT);
                        else
                            paramPack.nextCoords = playerCoord;

                        break;

                    case RIGHT:

                        if (dirTwo.equals(Direction.UP))
                            paramPack.nextCoords = dirMapper.map(Direction.UP_RIGHT);
                        else if (dirTwo.equals(Direction.DOWN))
                            paramPack.nextCoords = dirMapper.map(Direction.DOWN_RIGHT);
                        else
                            paramPack.nextCoords = playerCoord;

                        break;

                    default:
                        throw new IllegalStateException("Unknown direction" + dirOne + ", dirTwo " + dirTwo);

                }

                controlBlockingSemaphore.stop();
                contorlMovementCondition.setFirst(false).setSecond(false);

                movingAction.execute(controllable, paramPack);

            } else {
                throw new IllegalStateException("Dir buffer: " + pressedDirections.toString());
            }

        } finally {
            queueLock.unlock();
        }
    }
}