package com.daemonize.game.grid;

import java.util.ArrayList;
import java.util.List;

public abstract class PathFinding {

    public abstract <T> boolean recalculate(Field<T>[][] grid, int row, int column);

    public abstract <T> boolean recalculate(Grid<T> grid);

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
