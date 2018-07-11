package com.daemonize.daemondevapp.tabel;

import android.util.Pair;

import com.daemonize.daemondevapp.MainActivity;

import java.util.ArrayList;
import java.util.List;

public class Grid {
    PathFinding pathFinding;

   private Field[][] grid;

    private List<Field> path;

    public  Grid (int row, int colon) {
        pathFinding = new PathFinding();
        grid = new Field[row][colon];

        for (int i = 0; i < row; i++) {
            int y = 70 + i*140;

            for (int j=0; j<colon; j++) {
                int x = 70 + j*140;

                Field field = new Field(x, y, i, j,0,true);
                field.gCost = Integer.MAX_VALUE;
                grid[i][j] = field;
            }
        }
        pathFinding.dijkstra(this,new Pair<>(0,0),new Pair<>(row - 1,colon - 1));

    }

    public Field[][] getGrid() {
        return grid;
    }

    public void setPath(List<Field> path) {
        this.path = path;
    }

    public  void  setTower (int row, int colon ) {
        grid[row][colon].setWalkable(false);
        for (int i = -1 ; i <= 1; i++ ) {
            for (int j = -1; j <= 1; j++) {
                if (i==0 && j==0) {
                    continue;
                }
                int realX = row + i;
                int realY = colon + j;

                if( realX >= 0 && realX < grid.length && realY >=0 && realY < grid[row].length){
                   grid[realX][realY].setWeight(grid[realX][realY].getWeight() + 1);
                }
            }
        }
    }

    public List<Field> getNeighbors(int row,int colon){
        List<Field> neighbors = new ArrayList<>();

        for (int i = -1 ; i <= 1; i++ ) {
            for (int j = -1; j <= 1; j++) {
                if (i==0 && j==0) {
                    continue;
                }
                int realX = row + i;
                int realY = colon + j;

                if( realX >= 0 && realX < grid.length && realY >=0 && realY < grid[row].length){
                    neighbors.add(grid[realX][realY]);
                }
            }
        }

        return neighbors;
    }

    public Field getMinWeightOfNeighbors2(Field[][] playGround, int i, int j){
        int minWeight = Integer.MAX_VALUE ;
        Field minNeighbor = null;
        int iMax = playGround.length;
        int jMax = playGround[0].length;
        //List<Integer> neighborWight = new ArrayList<>(4);
        if(j%2==0){ //parni
            if (j+1 < jMax) {
                int weight = playGround[i][j+1].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                    minNeighbor = playGround[i][j+1];
                }
                if (i+1 < iMax  ){
                    weight = playGround[i+1][j+1].weight;
                    if(weight != 0 && weight < minWeight) {
                        minWeight = weight;
                        minNeighbor = playGround[i+1][j+1];
                    }
                }
            }
            if ( i+1 < iMax ) {
                int weight = playGround[i+1][j].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                    minNeighbor = playGround[i+1][j];
                }
            }
            if ( i-1 > -1 ) {
                int weight = playGround[i-1][j].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                    minNeighbor = playGround[i-1][j];
                }
            }
            if (j-1 > -1) {
                int weight = playGround[i][j-1].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                    minNeighbor = playGround[i][j-1];
                }
                if (i+1 < iMax  ){
                    weight = playGround[i+1][j-1].weight;
                    if(weight != 0 && weight < minWeight) {
                        minWeight = weight;
                        minNeighbor = playGround[i+1][j-1];
                    }
                }
            }
        } else { //neparni

            if ( j+1 < jMax ){
                int weight = playGround[i][j+1].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                    minNeighbor = playGround[i][j+1];
                }
                if ( i-1 > -1 ){
                    weight = playGround[i-1][j+1].weight;
                    if(weight != 0 && weight < minWeight) {
                        minWeight = weight;
                        minNeighbor = playGround[i-1][j+1];
                    }
                }
            }
            if ( i+1 < iMax ) {
                int weight = playGround[i+1][j].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                    minNeighbor = playGround[i+1][j];
                }
            }
            if ( i-1 > -1 ) {
                int weight = playGround[i-1][j].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                    minNeighbor = playGround[i-1][j];
                }
            }
            if ( j-1 > -1 ){
                int weight = playGround[i][j-1].weight;
                if(weight != 0 && weight < minWeight) {
                    minWeight = weight;
                    minNeighbor = playGround[i][j-1];
                }
                if ( i-1 > -1 ){
                    weight = playGround[i-1][j-1].weight;
                    if(weight != 0 && weight < minWeight) {
                        minWeight = weight;
                        minNeighbor = playGround[i-1][j-1];
                    }
                }
            }
        }

        return minNeighbor;
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
