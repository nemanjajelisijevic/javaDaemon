package com.daemonize.game.tabel;

public abstract class PathFinding {

    public abstract boolean recalculate(Grid grid);

    protected int getDistance(Field fieldA, Field fieldB) {
        int distX = Math.abs(fieldA.getRow() - fieldB.getRow());
        int distY = Math.abs(fieldA.getColumn() - fieldB.getColumn());

        if (distX > distY) {
            return 14 * distY + 10 * (distX - distY);
        } else {
            return 14 * distX + 10 * (distY - distX);
        }
    }

}
