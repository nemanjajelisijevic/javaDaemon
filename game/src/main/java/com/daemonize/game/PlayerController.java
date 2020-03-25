package com.daemonize.game;

import com.daemonize.daemonengine.consumer.Consumer;
import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonengine.utils.Pair;
import com.daemonize.game.controller.MovableController;
import com.daemonize.imagemovers.Movable;

import java.util.LinkedList;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class PlayerController implements MovableController {

    private PlayerDaemon player;

    private float distanceOffset;
    private float diagonalDistanceOffset;

    private float controllerVelocity;

    private LinkedList<Direction> pressedDirections;
    private Lock queueLock;
    private Condition queueEmptyCondition;

    public PlayerController(PlayerDaemon player) {
        this.player = player;
        this.pressedDirections = new LinkedList<>();
        this.queueLock = new ReentrantLock();
        this.queueEmptyCondition = queueLock.newCondition();
        this.distanceOffset = 10;
        this.diagonalDistanceOffset = distanceOffset * 0.71F;
        this.controllerVelocity = 4.5F;
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
    public void control() throws InterruptedException {

        try {

            queueLock.lock();

            while (pressedDirections.isEmpty())
                queueEmptyCondition.await();

            Pair<Float, Float> playerCoord = player.getLastCoordinates();

            if(pressedDirections.size() == 1) {

                MovableController.Direction dir = pressedDirections.peek();

                switch (dir) {

                    case UP:
                        playerCoord.setSecond(playerCoord.getSecond() - distanceOffset);
                        break;

                    case DOWN:
                        playerCoord.setSecond(playerCoord.getSecond() + distanceOffset);
                        break;

                    case LEFT:
                        playerCoord.setFirst(playerCoord.getFirst() - distanceOffset);
                        break;

                    case RIGHT:
                        playerCoord.setFirst(playerCoord.getFirst() + distanceOffset);
                        break;

                    default:
                        throw new IllegalStateException("Unknown direction" + dir);
                }

                if (player.getEnginesQueueSizes().get(0).equals(0)) {
                    player.clearAndInterrupt().rotateTowards(playerCoord).goTo(
                            playerCoord,
                            controllerVelocity,
                            ret -> System.err.println(
                                    DaemonUtils.tag()
                                            + "Player coords after movement: "
                                            + playerCoord.toString()
                                            + ", velocity: " + player.getVelocity().intensity
                            )
                    );

//                    player.rotAndGo(
//                            playerCoord,
//                            controllerVelocity,
//                            ret -> System.err.println(DaemonUtils.tag() + "Player coords after movement: " + playerCoord.toString())
//                    );

                }

            } else if (pressedDirections.size() == 2) {

                MovableController.Direction dirOne = pressedDirections.get(0);
                MovableController.Direction dirTwo = pressedDirections.get(1);

                switch (dirOne) {
                    case UP:

                        playerCoord.setSecond(playerCoord.getSecond() - diagonalDistanceOffset);

                        if (dirTwo.equals(Direction.RIGHT))
                            playerCoord.setFirst(playerCoord.getFirst() + diagonalDistanceOffset);
                        else if (dirTwo.equals(Direction.LEFT))
                            playerCoord.setFirst(playerCoord.getFirst() - diagonalDistanceOffset);

                        break;

                    case DOWN:

                        playerCoord.setSecond(playerCoord.getSecond() + diagonalDistanceOffset);

                        if (dirTwo.equals(Direction.RIGHT))
                            playerCoord.setFirst(playerCoord.getFirst() + diagonalDistanceOffset);
                        else if (dirTwo.equals(Direction.LEFT))
                            playerCoord.setFirst(playerCoord.getFirst() - diagonalDistanceOffset);

                        break;

                    case LEFT:
                        playerCoord.setFirst(playerCoord.getFirst() - diagonalDistanceOffset);

                        if (dirTwo.equals(Direction.UP))
                            playerCoord.setSecond(playerCoord.getSecond() - diagonalDistanceOffset);
                        else if (dirTwo.equals(Direction.DOWN))
                            playerCoord.setSecond(playerCoord.getSecond() + diagonalDistanceOffset);

                        break;

                    case RIGHT:
                        playerCoord.setFirst(playerCoord.getFirst() + diagonalDistanceOffset);

                        if (dirTwo.equals(Direction.UP))
                            playerCoord.setSecond(playerCoord.getSecond() - diagonalDistanceOffset);
                        else if (dirTwo.equals(Direction.DOWN))
                            playerCoord.setSecond(playerCoord.getSecond() + diagonalDistanceOffset);

                        break;

                    default:
                        throw new IllegalStateException("Unknown direction" + dirOne + ", dirTwo " + dirTwo);

                }

                if (player.getEnginesQueueSizes().get(0).equals(0)) {
                    player.clearAndInterrupt().rotateTowards(playerCoord).goTo(
                            playerCoord,
                            controllerVelocity,
                            ret -> System.err.println(
                                    DaemonUtils.tag()
                                            + "Player coords after movement: "
                                            + playerCoord.toString()
                                            + ", velocity: " + player.getVelocity().intensity
                            )
                    );
                }
            }

        } finally {
            queueLock.unlock();
        }
    }
}
