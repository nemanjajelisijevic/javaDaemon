package com.daemonize.daemondevapp.tabel;

import android.util.Pair;

import com.daemonize.daemondevapp.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class Grid {

    PathFinding pathFinding;

    Pair<Integer, Integer> startPoint;
    Pair<Integer, Integer> endPoint;

    private Field[][] grid;
    int fieldWith = 80;

    private List<Field> path;

    public Grid(int row, int column, Pair<Integer, Integer> startPoint, Pair<Integer, Integer> endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        pathFinding = new Dijkstra();
        grid = createFieldGround(row, column);
        pathFinding.recalculate(this);//new Pair<>(0,0),new Pair<>(row - 1,column - 1)

    }

    Field[][] createFieldGround(int row, int column) {
        Field[][] gridtemp = new Field[row][column];

        for (int i = 0; i < row; i++) {
            int y = fieldWith / 2 + i * fieldWith;

            for (int j = 0; j < column; j++) {
                int x = fieldWith / 2 + j * fieldWith;

                Field field = new Field(x, y, i, j, 0, true);
                field.gCost = Integer.MAX_VALUE;
                gridtemp[i][j] = field;
            }
        }
        return gridtemp;
    }

    public Field getField(int row, int column) {
        return grid[row][column];
    }

    public Field getField(float x, float y) {
        int row = (int) ((y /*- 40*/) / fieldWith);
        int column = (int) ((x /*- 40*/) / fieldWith);

        return grid[row][column];
    }

    public Field[][] getGrid() {
        return grid;
    }

    public void setPath(List<Field> path) {
        this.path = path;
    }

    public Pair<Integer,Integer>  getFieldCoord(float x, float y) {
        int row = (int) ((y) / fieldWith);
        int column = (int) ((x) / fieldWith);

        return new Pair<>(row,column);
    }

    public boolean setTower(float x, float y) {
        int row = (int) ((y) / fieldWith);
        int column = (int) ((x) / fieldWith);

        return setTower(row, column);
    }

    public boolean setTower(int row, int column) {

        if (!grid[row][column].isWalkable()) return false;

        //copy of grid
        Field[][] gridTemp = new Field[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Field field = new Field(grid[i][j]);
                gridTemp[i][j] = field;
                grid[i][j].gCost = Integer.MAX_VALUE;
            }
        }

        grid[row][column].setWalkable(false);

        boolean acceptTower = pathFinding.recalculate(this);
        if (acceptTower) {
            return true;
        } else {
            grid = gridTemp;
            pathFinding.recalculate(this);
            return false;
        }
    }

    public List<Field> getNeighbors(int row, int column) {
        List<Field> neighbors = new ArrayList<>();

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int realX = row + i;
                int realY = column + j;

                if (realX >= 0 && realX < grid.length && realY >= 0 && realY < grid[row].length) {
                    neighbors.add(grid[realX][realY]);
                }
            }
        }

        return neighbors;
    }

    public Field getMinWeightOfNeighbors(Field field) {
        return getMinWeightOfNeighbors(field.getRow(), field.getColumn());
    }

    public Field getMinWeightOfNeighbors(int row, int column) {
        List<Field> neighbors = new ArrayList<>();
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int realX = row + i;
                int realY = column + j;

                if (realX >= 0 && realX < grid.length && realY >= 0 && realY < grid[row].length) {
                    neighbors.add(grid[realX][realY]);
                }
            }
        }
        Field currentMinField = neighbors.get(0);
        for (Field field : neighbors) {
            if (field.isWalkable()) {
                if (currentMinField.fCost() >= field.fCost()) {
                    if (currentMinField.fCost() == field.fCost()) {
                        //they are same, we choose random one
                        int randomNum = (int) (Math.random() * 101);
                        currentMinField = (randomNum < 50 ? currentMinField : field);
                    } else {
                        currentMinField = field;
                    }
                }
            }
        }
        return currentMinField;
    }

    public StringBuilder gridToString() {
        StringBuilder str = new StringBuilder("");
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {

                String pomstr = grid[i][j].isWalkable() ? (grid[i][j].fCost() == Integer.MAX_VALUE ? "H" : grid[i][j].fCost() + "") : "T";
                str.append("\t" + pomstr + "\t");
            }
            str.append('\n');
        }
        return str;
    }

    public Pair<Integer, Integer> getEndPoint() {
        return endPoint;
    }

    public int getGridWidth() {
        return grid.length * fieldWith;
    }

    public int getGridHeight() {
        return grid[0].length * fieldWith;
    }

}