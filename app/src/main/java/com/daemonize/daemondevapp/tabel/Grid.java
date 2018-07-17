package com.daemonize.daemondevapp.tabel;

import android.util.Pair;

import com.daemonize.daemondevapp.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class Grid {
    PathFinding pathFinding;

    Pair<Integer,Integer> startPoint;
    Pair<Integer,Integer> endPoint;

   private Field[][] grid;
   int fieldWith = 80;


    private List<Field> path;

    public  Grid (int row, int colon,Pair<Integer,Integer> startPoint, Pair<Integer,Integer> endPoint) {
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        pathFinding = new PathFinding();
        grid = createFieldGround(row, colon);
        //pathFinding.dijkstra(this,new Pair<>(0,0),new Pair<>(row - 1,colon - 1));

    }

    Field[][] createFieldGround(int row, int colon){
        Field[][] gridtemp = new Field[row][colon];

        for (int i = 0; i < row; i++) {
            int y = fieldWith/2 + i*fieldWith;

            for (int j=0; j<colon; j++) {
                int x = fieldWith/2 + j*fieldWith;

                Field field = new Field(x, y, i, j,0,true);
                field.gCost = Integer.MAX_VALUE;
                gridtemp[i][j] = field;
            }
        }
        return gridtemp;
    }

    public Field getField(int row, int colon){
        return grid[row][colon];
    }
    public Field getField(float x, float y){
        int row = (int) (y / fieldWith);
        int colon = (int) (x / fieldWith);
        return grid[row][colon];
    }

    public Field[][] getGrid() {
        return grid;
    }

    public void setPath(List<Field> path) {
        this.path = path;
    }

    public  boolean  setTower (int row, int colon ) {

        if (!grid[row][colon].isWalkable()) return false;

        //copy of grid
        Field[][] gridTemp = new Field[grid.length][grid[0].length];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                Field field = new Field(grid[i][j]);
                gridTemp[i][j] = field;
                grid[i][j].gCost = Integer.MAX_VALUE;
            }
        }

        grid[row][colon].setWalkable(false);

        boolean acceptTower = pathFinding.dijkstra(this);
        if (acceptTower) {
            return true;
        } else {
            grid = gridTemp;
            pathFinding.dijkstra(this);
            return false;
        }
//        for (int i = -1 ; i <= 1; i++ ) {
//            for (int j = -1; j <= 1; j++) {
//                if (i==0 && j==0) {
//                    continue;
//                }
//                int realX = row + i;
//                int realY = colon + j;
//
//                if( realX >= 0 && realX < grid.length && realY >=0 && realY < grid[row].length){
//                   grid[realX][realY].setWeight(grid[realX][realY].getWeight() + 1);
//                }
//            }
//        }
    }

    public List<Field> getNeighbors(int row,int colon){
        List<Field> neighbors = new ArrayList<>();

        for (int i = -1 ; i <= 1; i++ ) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int realX = row + i;
                int realY = colon + j;

                if( realX >= 0 && realX < grid.length && realY >= 0 && realY < grid[row].length){
                    neighbors.add(grid[realX][realY]);
                }
            }
        }

        return neighbors;
    }
    public List<Field> getNeighbors(Field [][] grid, int row,int colon){
        List<Field> neighbors = new ArrayList<>();

        for (int i = -1 ; i <= 1; i++ ) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int realX = row + i;
                int realY = colon + j;

                if( realX >= 0 && realX < grid.length && realY >= 0 && realY < grid[row].length){
                    neighbors.add(grid[realX][realY]);
                }
            }
        }

        return neighbors;
    }



    public Field getMinWeightOfNeighbors(Field field) {
        return getMinWeightOfNeighbors(field.getRow(), field.getColon());
    }

    public Field getMinWeightOfNeighbors(int row, int colon) {
        List<Field> neighbors = new ArrayList<>();
        for (int i = -1 ; i <= 1; i++ ) {
            for (int j = -1; j <= 1; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                int realX = row + i;
                int realY = colon + j;

                if( realX >= 0 && realX < grid.length && realY >= 0 && realY < grid[row].length){
                    neighbors.add(grid[realX][realY]);
                }
            }
        }
        Field currentMinField = neighbors.get(0);
        for (Field field: neighbors){
            if(field.isWalkable()) {
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

    public StringBuilder gridToString(){
        StringBuilder str = new StringBuilder("");
        for (int i=0;i<grid.length;i++){
            for(int j = 0;j<grid[i].length;j++){

                String pomstr = grid[i][j].isWalkable() ? (grid[i][j].fCost() == Integer.MAX_VALUE ? "H" : grid[i][j].fCost() + "") : "T";
                str.append("\t"+pomstr+"\t");
            }
            str.append('\n');
        }
        return str;
    }

    public List<Field> getPath(Pair<Integer,Integer> statPoint, Pair<Integer,Integer> endPoint) {
        List<Field> path = new ArrayList<>();
        Field startNode = grid[endPoint.first][endPoint.second];
        Field endNode = grid[statPoint.first][statPoint.second];
//        Field currentField =
//        while ()
        return path;
    }

    public Pair<Integer, Integer> getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Pair<Integer, Integer> startPoint) {
        this.startPoint = startPoint;
    }

    public Pair<Integer, Integer> getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Pair<Integer, Integer> endPoint) {
        this.endPoint = endPoint;
    }
}
