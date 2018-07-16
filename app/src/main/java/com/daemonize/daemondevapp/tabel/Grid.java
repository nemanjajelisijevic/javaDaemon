package com.daemonize.daemondevapp.tabel;

import android.util.Pair;

import com.daemonize.daemondevapp.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class Grid {
    PathFinding pathFinding;

   private Field[][] grid;
   int fieldWith = 140;

    private List<Field> path;

    public  Grid (int row, int colon) {
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

    public  void  setTower (int row, int colon ) {

        Field[][] gridTemp = new Field[grid.length][grid[0].length];

        gridTemp[row][colon].setWalkable(false);

        boolean acceptTower = pathFinding.dijkstra(gridTemp, new Pair<>(0,0), new Pair<>(row - 1, colon - 1));
        if (acceptTower) {

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
            if (currentMinField.fCost() >= field.fCost()) {
                if (currentMinField.fCost() == field.fCost()){
                    //they are same, we choose random one
                    int randomNum = (int) (Math.random() * 101);
                    currentMinField = ( randomNum < 50 ? currentMinField : field);
                } else {
                  currentMinField = field;
                }
            }
        }
        return currentMinField;
    }

    public String gridToString(){
        String str = "";
        for (int i=0;i<grid.length;i++){
            for(int j = 0;j<grid[i].length;j++){

                String pomstr = grid[i][j].isWalkable() ? (grid[i][j].fCost() == Integer.MAX_VALUE ? "H" : grid[i][j].fCost() + "") : "T";
                str +="\t"+pomstr+"\t";
            }
            str+='\n';
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
}
