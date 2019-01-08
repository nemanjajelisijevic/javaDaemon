package com.daemonize.daemondevapp.tabel;

import com.daemonize.daemondevapp.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Grid {

    PathFinding pathFinding;

    Pair<Integer, Integer> startPoint;
    Pair<Integer, Integer> endPoint;

    private float xCoordinateInReal;
    private float yCoordinateInReal;

    public float getStartingX() {
        return xCoordinateInReal;
    }

    public float getStartingY() {
        return yCoordinateInReal;
    }

    Lock gridLock  = new ReentrantLock();

    private Field[][] grid;
    int fieldWith ;

    private List<Field> path;

//    public Grid(float row, float column, Pair<Integer, Integer> startPoint, Pair<Integer, Integer> endPoint) {
//        this.startPoint = startPoint;
//        this.endPoint = endPoint;
//        pathFinding = new Dijkstra();
//        grid = createFieldGround(row, column);
//        pathFinding.recalculate(this);//new Pair<>(0,0),new Pair<>(row - 1,column - 1)
//
//    }
    public boolean isInsideOfGrid (float x, float y){
        boolean in = false;
        float x2 = xCoordinateInReal + grid[0].length * fieldWith;
        float y2 = yCoordinateInReal + grid.length * fieldWith;
        if ( x >= xCoordinateInReal && x <= x2 &&
             y >= yCoordinateInReal && y <= y2 ){
            in = true;
        }
        return in;
    }

    public Grid(int row, int column, Pair<Integer, Integer> startPoint, Pair<Integer, Integer> endPoint, float realCoordX, float realCoordY, int fieldWith) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.xCoordinateInReal = realCoordX;
        this.yCoordinateInReal = realCoordY;
        this.fieldWith = fieldWith;
        pathFinding = new Dijkstra();
        grid = createFieldGround(row, column,realCoordX,realCoordY);
        pathFinding.recalculate(this);//new Pair<>(0,0),new Pair<>(row - 1,column - 1)

    }

    Field[][] createFieldGround(int row, int column,float realCoordX, float realCoordY) {
        Field[][] gridtemp = new Field[row][column];

        for (int i = 0; i < row; i++) {
            float y = realCoordY + fieldWith / 2 + i * fieldWith;

            for (int j = 0; j < column; j++) {
                float x = realCoordX + fieldWith / 2 + j * fieldWith;

                Field field = new Field(x, y, i, j, 0, true);
                field.gCost = Integer.MAX_VALUE;
                gridtemp[i][j] = field;
            }
        }
        return gridtemp;
    }

    public Field getField(int row, int column) {
//        gridLock.lock();
        Field ret = grid[row][column];
//        gridLock.unlock();
        return ret;
    }

    public Field getField(float x, float y) {
//        gridLock.lock();
        Field ret = null;

        if (isInsideOfGrid(x,y)) {
            int row = (int) ((y - yCoordinateInReal) / fieldWith);//TODO this shit right here
            int column = (int) ((x - xCoordinateInReal) / fieldWith);
            ret = grid[row][column];
            //        gridLock.unlock();

        }

        return ret;

    }

    public Field[][] getGrid() {
        return grid;
    }

    public void setPath(List<Field> path) {
        this.path = path;
    }

//    public Pair<Integer,Integer>  getFieldCoord(float x, float y) {
//        int row = (int) ((y) / fieldWith);
//        int column = (int) ((x) / fieldWith);
//
//        return new Pair<>(row,column);
//    }

    public boolean setTower(float x, float y) {
        int row = (int) ((y) / fieldWith);
        int column = (int) ((x) / fieldWith);

        return setTower(row, column);
    }

    public boolean setTower(int row, int column) {

    //  gridLock.lock();
        if (!grid[row][column].isWalkable() ) return false;
        if (row == grid.length - 1 && column == grid[row].length - 1) return false;

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
//            gridLock.unlock();
            return true;
        } else {
            grid = gridTemp;
            pathFinding.recalculate(this);
//            gridLock.unlock();
            return false;
        }
    }

    public boolean destroyTower(float x, float y) {
        int row = (int) ((y) / fieldWith);
        int column = (int) ((x) / fieldWith);

        return destroyTower(row, column);
    }

    public boolean destroyTower(int row, int column) {

        if (row == grid.length - 1 && column == grid[row].length - 1) return false;
        boolean acceptDestroyTower = false;
        if (!grid[row][column].isWalkable()) {
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[0].length; j++) {
                    Field field = new Field(grid[i][j]);
//                    gridTemp[i][j] = field;
                    grid[i][j].gCost = Integer.MAX_VALUE;
                }
            }
            grid[row][column].setWalkable(true);
            acceptDestroyTower = pathFinding.recalculate(this);
        }
        return acceptDestroyTower;
    }


        public List<Field> getNeighbors(Field field) {
        return getNeighbors(field.row, field.column);
    }

    protected List<Field> getNeighbors(int row, int column) {
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
//        gridLock.lock();
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
//        gridLock.unlock();
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
        return grid[0].length * fieldWith;
    }

    public int getGridHeight() {
        return grid.length * fieldWith;
    }

}