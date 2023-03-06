package com.daemonize.game.grid;

import com.daemonize.daemonengine.utils.DaemonUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class Dijkstra extends PathFinding {


    @Override
    public <T> boolean recalculate(Field<T>[][] grid, int row, int column) {

        Field startNode =  grid[row][column];

        Field<T>[] heapArray = new Field[grid.length * grid[0].length];
        Heap<Field<T>> openSet = new Heap<>(heapArray);
        HashSet<Field<T>> closedSet = new HashSet<>();

        openSet.add(startNode);

        while (openSet.size() > 0) {

            Field currentNode = openSet.removeFirst();

            closedSet.add(currentNode);

            List<Field> neighbors = new ArrayList<>();

            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) {
                        continue;
                    }
                    int realX = currentNode.getRow() + i;
                    int realY = currentNode.getColumn() + j;

                    if (realX >= 0 && realX < grid.length && realY >= 0 && realY < grid[row].length) {
                        neighbors.add(grid[realX][realY]);
                    }
                }
            }

            for (Field neighbour : neighbors) {

                if (!neighbour.isWalkable() || closedSet.contains(neighbour)) {
                    continue;
                }

                int distance = getDistance(currentNode,neighbour);
                int newMovementCostToNeighbour = distance
                        + (currentNode.gCost == Integer.MAX_VALUE ? 0 : currentNode.gCost);//+ neighbour.getWeight();


                if ( newMovementCostToNeighbour < neighbour.gCost ) {

                    neighbour.gCost = newMovementCostToNeighbour;
                    if (!openSet.contains(neighbour)) {
                        openSet.add(neighbour);
                    } else {
                        openSet.updateItem(neighbour);
                    }
                }
            }
        }

        startNode.gCost = 0;
        //System.out.println("MATRICA \n" +  Grid.gridToString(grid));
        return true;
    }

    @Override
    public <T> boolean recalculate(Grid<T> grid) {
        Field startNode =  grid.getGrid()[grid.endPoint.getFirst()][grid.endPoint.getSecond()];
        Field endNode = grid.getGrid()[grid.startPoint.getFirst()][grid.startPoint.getSecond()];

        Field<T>[] heapArray = new Field[grid.getGrid().length * grid.getGrid()[0].length];
        Heap<Field<T>> openSet = new Heap<>(heapArray);
        HashSet<Field> closedSet = new HashSet<Field>();

        openSet.add(startNode);

        while (openSet.size() > 0) {

            Field currentNode = openSet.removeFirst();

            closedSet.add(currentNode);

            if (currentNode.equals(endNode)) {
                startNode.gCost = 0;
                System.out.println("MATRICA \n" +  grid.gridToString().toString());
                return true;
            }

            for (Field neighbour : grid.getNeighbors(currentNode.getRow(),currentNode.getColumn())) {

                if (!neighbour.isWalkable() || closedSet.contains(neighbour)) {
                    continue;
                }

                int newMovementCostToNeighbour = getDistance(currentNode,neighbour)
                        + (currentNode.gCost == Integer.MAX_VALUE ? 0 : currentNode.gCost);//+ neighbour.getWeight();
                if ( newMovementCostToNeighbour < neighbour.gCost ) {
                    neighbour.gCost = newMovementCostToNeighbour;
                    if (!openSet.contains(neighbour)) {
                        openSet.add(neighbour);
                    } else {
                        openSet.updateItem(neighbour);
                    }
                }
            }
        }

        grid.getGrid()[grid.endPoint.getFirst()][grid.endPoint.getSecond()].gCost = 0;

        return false;
    }
}
