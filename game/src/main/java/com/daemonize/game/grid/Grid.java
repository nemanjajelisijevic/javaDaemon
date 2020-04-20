package com.daemonize.game.grid;

import com.daemonize.daemonengine.utils.DaemonUtils;
import com.daemonize.daemonengine.utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Grid<T> {

    PathFinding pathFinding;

    Pair<Integer, Integer> startPoint;
    Pair<Integer, Integer> endPoint;

    public Set<Field> multipleZFieldSet = Collections.synchronizedSet(new HashSet<>());

    private int mapWidth;
    private int mapHeight;

    private String mapName;

    public String getMapName() {
        return mapName;
    }

    public Grid<T> setMapName(String mapName) {
        this.mapName = mapName;
        return this;
    }

    public Grid<T> setMapWidth(int mapWidth) {
        this.mapWidth = mapWidth;
        return this;
    }

    public Grid<T> setMapHeight(int mapHeight) {
        this.mapHeight = mapHeight;
        return this;
    }

    public int getMapWidth() {return mapWidth;}

    public int getMapHeight() {return mapHeight;}

    private float xCoordinateInReal;
    private float yCoordinateInReal;

    public float getStartingX() {
        return xCoordinateInReal;
    }

    public float getStartingY() {
        return yCoordinateInReal;
    }

    private Field<T>[][] grid;
    private int fieldWith;

    public int getFieldWith() {
        return fieldWith;
    }

    private List<Field> path;

    private List<Field<T>[]> walkableFields;

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

    public Grid<T> calculateFieldWidth() {
        this.fieldWith = mapWidth / grid[0].length;
        return this;
    }

    public Grid(){
        this.pathFinding = new Dijkstra();
    }

    public Grid(int rows, int columns, int mapWidth, int mapHeight){
        this.startPoint = Pair.create(0,0);
        this.endPoint = Pair.create(0,0);
        this.xCoordinateInReal = 0;
        this.yCoordinateInReal = 0;
        this.fieldWith = mapWidth / columns;
        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
        this.pathFinding = new Dijkstra();
        this.grid = createFieldGround(rows, columns,xCoordinateInReal,yCoordinateInReal);
    }

    public Grid(
            int rows,
            int columns,
            Pair<Integer, Integer> startPoint,
            Pair<Integer, Integer> endPoint,
            float realCoordX,
            float realCoordY,
            int fieldWith,
            int mapWidth,
            int mapHeight
    ) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.xCoordinateInReal = realCoordX;
        this.yCoordinateInReal = realCoordY;
        this.fieldWith = fieldWith;
        pathFinding = new Dijkstra();
        grid = createFieldGround(rows, columns,realCoordX,realCoordY);
        pathFinding.recalculate(this);//new Pair<>(0,0),new Pair<>(row - 1,column - 1)

        this.mapWidth = mapWidth;
        this.mapHeight = mapHeight;
    }

    Field<T>[][] createFieldGround(int row, int column,float realCoordX, float realCoordY) {
        Field<T>[][] gridtemp = new Field[row][column];

        for (int i = 0; i < row; i++) {
            float y = realCoordY + fieldWith / 2 + i * fieldWith;

            for (int j = 0; j < column; j++) {
                float x = realCoordX + fieldWith / 2 + j * fieldWith;

                Field<T> field = new Field<T>(x, y, i, j, 0, true);
                field.gCost = Integer.MAX_VALUE;
                gridtemp[i][j] = field;
            }
        }
        return gridtemp;
    }

    public Field<T> getField(int row, int column) {
        return grid[row][column];
    }

    public Field<T> getField(float x, float y) {

        Field<T> ret = null;

        if (isInsideOfGrid(x,y)) {
            int row = (int)Math.floor((y - yCoordinateInReal) / fieldWith);
            int column = (int) Math.floor((x - xCoordinateInReal) / fieldWith);
            ret = grid[row][column];
        }

        return ret;

    }

    public Field[][] getGrid() {
        return grid;
    }

    public void setPath(List<Field> path) {
        this.path = path;
    }

    public boolean setObject(float x, float y) {
        int row = (int) ((y) / fieldWith);
        int column = (int) ((x) / fieldWith);
        return setObject(row, column);
    }

    public synchronized boolean setObject(int row, int column) {

        if (!grid[row][column].isWalkable() ) return false;
        if (row == grid.length - 1 && column == grid[row].length - 1) return false;

        //copy of grid
        Field<T>[][] gridTemp = new Field[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Field<T> field = new Field(grid[i][j]);
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


    public void setStartAndRecalculate(int row, int column) {

        startPoint = Pair.create(row, column);
        endPoint = Pair.create(0, 0);

        //copy of grid
        Field<T>[][] gridTemp = new Field[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Field<T> field = new Field(grid[i][j]);
                gridTemp[i][j] = field;
                gridTemp[i][j].gCost = Integer.MAX_VALUE;
                //grid[i][j].gCost = Integer.MAX_VALUE;
            }
        }

        pathFinding.recalculate(gridTemp, row, column);

        grid = gridTemp;
        //System.out.println(gridToString(grid).toString());
    }

    public void setCoordsAndRecalculate(int row, int column) {
        startPoint = Pair.create(row, column);
        endPoint = Pair.create(0, 0);
        pathFinding.recalculate(this);
    }

    public boolean destroyObject(float x, float y) {
        int row = (int) ((y) / fieldWith);
        int column = (int) ((x) / fieldWith);

        return destroyObject(row, column);
    }

    public boolean destroyObject(int row, int column) {

        if (row == grid.length - 1 && column == grid[row].length - 1) {
            System.err.println(DaemonUtils.timedTag() + "Accessing bad field[" + row + "][" + column + "]");
            return false;
        }

        boolean acceptDestroyTower = false;
        if (!grid[row][column].isWalkable()) {
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[0].length; j++) {
                    Field<T> field = new Field(grid[i][j]);
//                    gridTemp[i][j] = field;
                    grid[i][j].gCost = Integer.MAX_VALUE;
                }
            }
            grid[row][column].setWalkable(true);
            acceptDestroyTower = pathFinding.recalculate(this);

            if (!acceptDestroyTower)
                System.err.println(DaemonUtils.timedTag() + "Path finding could not recalculate for destroying field[" + row + "][" + column + "]");
        } else
            System.err.println(DaemonUtils.timedTag() + "Field still walkable [" + row + "][" + column + "]");
        return acceptDestroyTower;
    }


    public List<Field<T>> getNeighbors(Field<T> field) {
        return getNeighbors(field.row, field.column);
    }

    protected List<Field<T>> getNeighbors(int row, int column) {
        List<Field<T>> neighbors = new ArrayList<>();

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

    public synchronized Field<T> getMinWeightOfNeighbors(Field<T> field) {
        return getMinWeightOfNeighbors(field.getRow(), field.getColumn());
    }

    private List<Field<T>> neighbors = new ArrayList<>(8);

    public synchronized Field<T> getMinWeightOfNeighbors(int row, int column) {
        //List<Field> neighbors = new ArrayList<>(8);
        neighbors.clear();
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

        Collections.sort(neighbors);

        return neighbors.get(0);
    }



    public static StringBuilder gridToString(Field[][] grid) {
        StringBuilder str = new StringBuilder("");
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {

                String pomstr = grid[i][j].isWalkable() ? (grid[i][j].fCost() == Integer.MAX_VALUE ? "H" : grid[i][j].fCost() + "") : "NW";
                str.append("\t" + pomstr + "\t");
            }
            str.append('\n');
        }
        return str;
    }

    public StringBuilder gridToString() {
        return gridToString(grid);
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