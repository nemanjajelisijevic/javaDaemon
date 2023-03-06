package com.daemonize.game.grid;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class AStar extends PathFinding {

    @Override
    public <T> boolean recalculate(Field<T>[][] grid, int row, int column) {
        return false;
    }

    @Override
    public <T> boolean recalculate(Grid<T> grid) {
        Field startNode =  grid.getGrid()[grid.startPoint.getFirst()][grid.startPoint.getSecond()];
        Field endNode = grid.getGrid()[grid.endPoint.getFirst()][grid.endPoint.getSecond()];

        Field<T>[] heapArray = new Field[grid.getGrid().length * grid.getGrid()[0].length];
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
                System.out.println("MATRICA \n" + grid.gridToString().toString());

                return true;
            }

            for (Field neighbour : grid.getNeighbors(currentNode.getRow(),currentNode.getColumn())) {
                if (!neighbour.isWalkable() || closedSet.contains(neighbour)) {
                    continue;
                }

                int newMovementCostToNeighbour = getDistance(currentNode,neighbour) + neighbour.getWeight();
                if ( newMovementCostToNeighbour < neighbour.gCost ||
                        !openSet.contains(neighbour)) {
                    neighbour.gCost = newMovementCostToNeighbour;
                    neighbour.hCost = getDistance(neighbour, endNode);
                    // neighbour.parent = currentNode;

                    if ( !openSet.contains(neighbour)) {
                        openSet.add(neighbour);
                    } else {
                        openSet.updateItem(neighbour);
                    }
                }
            }
        }

        System.out.println("MATRICA \n" + grid.gridToString().toString());
        if (openSet.size() == 0) {
            System.out.println("PUTANJA: " + "Ne postoji putanja !!!");
        }
        return false;

    }


    private List<Field> retracePath (Field start, Field end ){
        List<Field> path = new ArrayList<>();
        Field curNode = end;

        while ( curNode != start) {
            path.add(curNode);
            //curNode = curNode.parent;
        }

        return path;
    }
}
