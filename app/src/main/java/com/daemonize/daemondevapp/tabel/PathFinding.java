package com.daemonize.daemondevapp.tabel;

import android.util.Log;
import android.util.Pair;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PathFinding {

    Grid grid ;

    public Grid getGrid() {
        return grid;
    }

    public PathFinding () {

    }
    public PathFinding (int row, int colon, Pair<Integer,Integer> statPoint, Pair<Integer,Integer> endPoint) {

        grid = new Grid(row,colon);

        grid.setTower(1,1);
//        grid.setTower(1,2);
//        grid.setTower(1,3);
////        grid.setTower(1,0);
//        grid.setTower(1,4);
//
//        grid.setTower(3,2);
//        grid.setTower(3,3);
//        grid.setTower(3,4);
//        grid.setTower(3,5);
//        grid.setTower(3,1);
//
//        grid.setTower(4,1);
//        grid.setTower(4,3);
//        grid.setTower(4,5);
//
//
//        grid.setTower(5,2);
//        grid.setTower(5,0);
//        grid.setTower(5,4);

//        grid.setTower(0,1);
//        grid.setTower(1,1);
//        grid.setTower(2,1);
//        grid.setTower(3,1);
//        grid.setTower(4,1);
//
//        grid.setTower(5,0);
//        grid.setTower(5,2);
//        grid.setTower(5,4);
//
//        grid.setTower(0,4);
//        grid.setTower(1,3);
//        grid.setTower(2,3);
//        grid.setTower(3,3);
//        grid.setTower(4,3);
//        grid.setTower(5,3);
//
//        grid.setTower(0,5);
//        grid.setTower(1,5);
//        grid.setTower(2,5);
//        grid.setTower(3,5);
//        grid.setTower(4,5);

        Field startNode = grid.getGrid()[endPoint.first][endPoint.second];
        Field endNode = grid.getGrid()[statPoint.first][statPoint.second];

        Field [] heapArray = new Field[ row * colon ];
//        List<Field> openSet = new ArrayList<>() ;
        Heap<Field> openSet = new Heap<Field>(heapArray) ;
        HashSet<Field> closedSet = new HashSet<Field>();

        openSet.add(startNode);

        while (openSet.size() > 0) {
            //finding min of openSet elements and remove them from open set in method HEAP.removeFirst();
//            Field currentNode = openSet.get(0);
            Field currentNode = openSet.removeFirst();
            //finding min of openSet elements
//            for (int i = 1;i < openSet.size();i++) {
//                if ( openSet.get(i).fCost() < currentNode.fCost() ||
//                     openSet.get(i).fCost() == currentNode.fCost() && openSet.get(i).hCost < currentNode.hCost){
//                    currentNode = openSet.get(i);
//                }
//            }
            //removing min from open set to closed set
//            openSet.remove(currentNode);

            closedSet.add(currentNode);

            if ( currentNode.equals(endNode)){
                grid.setPath( retracePath(startNode,endNode));
               // Log.w("Putanja", pathToString(grid.getPath()));
                Log.w("MATRICA \n", grid.gridToString());

                return;
            }

            for (Field neighbour : grid.getNeighbors(currentNode.getRow(),currentNode.getColon())) {
                if (!neighbour.isWalkable() || closedSet.contains(neighbour)) {
                    continue;
                }

                int newMovementCostToNeighbour = getDistance(currentNode,neighbour) + neighbour.getWeight();
                if ( newMovementCostToNeighbour < neighbour.gCost ||
                     !openSet.contains(neighbour)) {
                    neighbour.gCost = newMovementCostToNeighbour;
                    neighbour.hCost = getDistance(neighbour, endNode);
                    neighbour.parent = currentNode;

                    if ( !openSet.contains(neighbour)) {
                        openSet.add(neighbour);
                    } else {
                        openSet.updateItem(neighbour);
                    }
                }
            }
        }

//        grid.path = retracePath(startNode,endNode);
//                        Log.w("Putanja", pathToString(grid.getPath()));
//                        Log.w("MATRICA \n", grid.gridToString());
        if (openSet.size() == 0) {
            Log.w("PUTANJA","Ne postoji putanja !!!");
        }


    }

    public boolean dijkstra(Grid grid,Pair<Integer,Integer> statPoint, Pair<Integer,Integer> endPoint ) {

//        grid = new Grid(row,colon);
//
//        grid.setTower(1,1);
//        grid.setTower(1,2);
//        grid.setTower(1,3);
//        //        grid.setTower(1,0);
//        grid.setTower(1,4);
//        grid.setTower(2,4);
//
//        grid.setTower(3,2);
//        grid.setTower(3,3);
//        grid.setTower(3,4);
//        grid.setTower(3,5);
//        grid.setTower(3,1);
//
//        grid.setTower(4,1);
//        grid.setTower(4,3);
//        grid.setTower(4,5);
//
//
//        grid.setTower(5,2);
//        grid.setTower(5,0);
//        grid.setTower(5,4);
//
//        //        grid.setTower(0,1);
//        //        grid.setTower(1,1);
//        //        grid.setTower(2,1);
//        //        grid.setTower(3,1);
//        //        grid.setTower(4,1);
//        //
//        //        grid.setTower(5,0);
//        //        grid.setTower(5,2);
//        //        grid.setTower(5,4);
//        //
//        //        grid.setTower(0,4);
//        //        grid.setTower(1,3);
//        //        grid.setTower(2,3);
//        //        grid.setTower(3,3);
//        //        grid.setTower(4,3);
//        //        grid.setTower(5,3);
//        //
//        //        grid.setTower(0,5);
//        //        grid.setTower(1,5);
//        //        grid.setTower(2,5);
//        //        grid.setTower(3,5);
//        //        grid.setTower(4,5);

        Field startNode =  grid.getGrid()[endPoint.first][endPoint.second];
        Field endNode = grid.getGrid()[statPoint.first][statPoint.second];

        Field [] heapArray = new Field[ grid.getGrid().length * grid.getGrid()[0].length];
        //        List<Field> openSet = new ArrayList<>() ;
        Heap<Field> openSet = new Heap<Field>(heapArray) ;
        HashSet<Field> closedSet = new HashSet<Field>();

        openSet.add(startNode);

        while (openSet.size() > 0) {
            //finding min of openSet elements and remove them from open set in method HEAP.removeFirst();
            //            Field currentNode = openSet.get(0);
            Field currentNode = openSet.removeFirst();
            //finding min of openSet elements
            //            for (int i = 1;i < openSet.size();i++) {
            //                if ( openSet.get(i).fCost() < currentNode.fCost() ||
            //                     openSet.get(i).fCost() == currentNode.fCost() && openSet.get(i).hCost < currentNode.hCost){
            //                    currentNode = openSet.get(i);
            //                }
            //            }
            //removing min from open set to closed set
            //            openSet.remove(currentNode);

            closedSet.add(currentNode);

            if ( currentNode.equals(endNode)){
                //grid.path = retracePath(startNode,endNode);
                //Log.w("Putanja", pathToString(grid.getPath()));
                startNode.gCost = 0;
//                grid.getGrid()[endPoint.first][endPoint.second].gCost = 0;
                Log.w("MATRICA \n", grid.gridToString());
                return true;
            }


            for (Field neighbour : grid.getNeighbors(currentNode.getRow(),currentNode.getColon())) {
                if (!neighbour.isWalkable() || closedSet.contains(neighbour)) {
                    continue;
                }

                int newMovementCostToNeighbour = getDistance(currentNode,neighbour)  + (currentNode.gCost == Integer.MAX_VALUE ? 0 : currentNode.gCost);//+ neighbour.getWeight();
                if ( newMovementCostToNeighbour < neighbour.gCost ) {
                    neighbour.gCost = newMovementCostToNeighbour;
                    //neighbour.hCost = getDistance(neighbour, endNode);
                    //neighbour.parent = currentNode;

                    if ( !openSet.contains(neighbour)) {
                        openSet.add(neighbour);
                    } else {
                        openSet.updateItem(neighbour);
                    }
                }
            }
            //Log.w("MATRICA DIJKSTRA  \n", grid.gridToString());
        }

        grid.getGrid()[endPoint.first][endPoint.second].gCost = 0;

        return false;
        //        grid.path = retracePath(startNode,endNode);
        //                        Log.w("Putanja", pathToString(grid.getPath()));
//        Log.w("MATRICA DIJKSTRA \n", grid.gridToString());
//        if (openSet.size() == 0) {
//            Log.w("PUTANJA","Ne postoji putanja !!!");
//        }
    }

     List<Field> retracePath (Field start, Field end ){
        List<Field> path = new ArrayList<>();
        Field curNode = end;

        while ( curNode != start) {
            path.add(curNode);
            curNode = curNode.parent;
        }

        return path;
    }

    private int getDistance(Field fieldA, Field fieldB) {
        int distX = Math.abs(fieldA.getRow() - fieldB.getRow());
        int distY = Math.abs(fieldA.getColon() - fieldB.getColon());

        if (distX > distY) {
            return 14 * distY + 10 * (distX - distY);
        } else {
            return 14 * distX + 10 * (distY - distX);
        }
    }

    public String pathToString (List<Field> path){
        String str = "Path : ";
        for (Field field : path) {
            str += ( "( "+ field.getRow()+" , "+field.getColon()+ " ) \t");
        }
        return str;
    }
}
