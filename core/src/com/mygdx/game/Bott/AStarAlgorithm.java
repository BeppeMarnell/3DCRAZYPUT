package com.mygdx.game.Bott;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class AStarAlgorithm {
    private static int DEFAULT_HV_COST = 10; // Horizontal - Vertical Cost
    private static int DEFAULT_DIAGONAL_COST = 14;
    private int hvCost;
    private int diagonalCost;
    private Coordinate[][] searchArea;
    private PriorityQueue<Coordinate> openList;
    private List<Coordinate> closedList;
    private Coordinate initialCoordinate;
    private Coordinate finalCoordinate;

    public AStarAlgorithm(int rows, int cols, Coordinate initialCoordinate, Coordinate finalCoordinate, int hvCost, int diagonalCost) {
        this.hvCost = hvCost;
        this.diagonalCost = diagonalCost;
        setInitialCoordinate(initialCoordinate);
        setFinalCoordinate(finalCoordinate);
        this.searchArea = new Coordinate[rows][cols];
        this.openList = new PriorityQueue<Coordinate>(new Comparator<Coordinate>() {
            @Override
            public int compare(Coordinate Coordinate0, Coordinate Coordinate1) {
                return Coordinate0.getF() < Coordinate1.getF() ? -1 : Coordinate0.getF() > Coordinate1.getF() ? 1 : 0;
            }
        });
        setCoordinates();
        this.closedList = new ArrayList<Coordinate>();
    }

    public AStarAlgorithm(int rows, int cols, Coordinate initialCoordinate, Coordinate finalCoordinate) {
        this(rows, cols, initialCoordinate, finalCoordinate, DEFAULT_HV_COST, DEFAULT_DIAGONAL_COST);
    }

    private void setCoordinates() {
        for (int i = 0; i < searchArea.length; i++) {
            for (int j = 0; j < searchArea[0].length; j++) {
                Coordinate Coordinate = new Coordinate(i, j);
                Coordinate.calculateHeuristic(getFinalCoordinate());
                this.searchArea[i][j] = Coordinate;
            }
        }
    }

    public void setBlocks(int[][] blocksArray) {
        for (int i = 0; i < blocksArray.length; i++) {
            int row = blocksArray[i][0];
            int col = blocksArray[i][1];
            setBlock(row, col);
        }
    }

    public List<Coordinate> findPath() {
        openList.add(initialCoordinate);
        while (!isEmpty(openList)) {
            Coordinate currentCoordinate = openList.poll();
            closedList.add(currentCoordinate);
            if (isFinalCoordinate(currentCoordinate)) {
                return getPath(currentCoordinate);
            } else {
                addAdjacentCoordinates(currentCoordinate);
            }
        }
        return new ArrayList<Coordinate>();
    }

    private List<Coordinate> getPath(Coordinate currentCoordinate) {
        List<Coordinate> path = new ArrayList<Coordinate>();
        path.add(currentCoordinate);
        Coordinate parent;
        while ((parent = currentCoordinate.getParent()) != null) {
            path.add(0, parent);
            currentCoordinate = parent;
        }
        return path;
    }

    private void addAdjacentCoordinates(Coordinate currentCoordinate) {
        addAdjacentUpperRow(currentCoordinate);
        addAdjacentMiddleRow(currentCoordinate);
        addAdjacentLowerRow(currentCoordinate);
    }

    private void addAdjacentLowerRow(Coordinate currentCoordinate) {
        int row = currentCoordinate.getX();
        int col = currentCoordinate.getY();
        int lowerRow = row + 1;
        if (lowerRow < getSearchArea().length) {
            if (col - 1 >= 0) {
                checkCoordinate(currentCoordinate, col - 1, lowerRow, getDiagonalCost()); // Comment this line if diagonal movements are not allowed
            }
            if (col + 1 < getSearchArea()[0].length) {
                checkCoordinate(currentCoordinate, col + 1, lowerRow, getDiagonalCost()); // Comment this line if diagonal movements are not allowed
            }
            checkCoordinate(currentCoordinate, col, lowerRow, getHvCost());
        }
    }

    private void addAdjacentMiddleRow(Coordinate currentCoordinate) {
        int row = currentCoordinate.getX();
        int col = currentCoordinate.getY();
        int middleRow = row;
        if (col - 1 >= 0) {
            checkCoordinate(currentCoordinate, col - 1, middleRow, getHvCost());
        }
        if (col + 1 < getSearchArea()[0].length) {
            checkCoordinate(currentCoordinate, col + 1, middleRow, getHvCost());
        }
    }

    private void addAdjacentUpperRow(Coordinate currentCoordinate) {
        int row = currentCoordinate.getX();
        int col = currentCoordinate.getY();
        int upperRow = row - 1;
        if (upperRow >= 0) {
            if (col - 1 >= 0) {
                checkCoordinate(currentCoordinate, col - 1, upperRow, getDiagonalCost()); // Comment this if diagonal movements are not allowed
            }
            if (col + 1 < getSearchArea()[0].length) {
                checkCoordinate(currentCoordinate, col + 1, upperRow, getDiagonalCost()); // Comment this if diagonal movements are not allowed
            }
            checkCoordinate(currentCoordinate, col, upperRow, getHvCost());
        }
    }

    private void checkCoordinate(Coordinate currentCoordinate, int col, int row, int cost) {
        Coordinate adjacentCoordinate = getSearchArea()[row][col];
        if (!adjacentCoordinate.isBlock() && !getClosedList().contains(adjacentCoordinate)) {
            if (!getOpenList().contains(adjacentCoordinate)) {
                adjacentCoordinate.setCoordinateData(currentCoordinate, cost);
                getOpenList().add(adjacentCoordinate);
            } else {
                boolean changed = adjacentCoordinate.checkBetterPath(currentCoordinate, cost);
                if (changed) {
                    // Remove and Add the changed Coordinate, so that the PriorityQueue can sort again its
                    // contents with the modified "finalCost" value of the modified Coordinate
                    getOpenList().remove(adjacentCoordinate);
                    getOpenList().add(adjacentCoordinate);
                }
            }
        }
    }

    private boolean isFinalCoordinate(Coordinate currentCoordinate) {
        return currentCoordinate.equals(finalCoordinate);
    }

    private boolean isEmpty(PriorityQueue<Coordinate> openList) {
        return openList.size() == 0;
    }

    public void setBlock(int row, int col) {
        this.searchArea[row][col].setBlock(true);
    }

    public Coordinate getInitialCoordinate() {
        return initialCoordinate;
    }

    public void setInitialCoordinate(Coordinate initialCoordinate) {
        this.initialCoordinate = initialCoordinate;
    }

    public Coordinate getFinalCoordinate() {
        return finalCoordinate;
    }

    public void setFinalCoordinate(Coordinate finalCoordinate) {
        this.finalCoordinate = finalCoordinate;
    }

    public Coordinate[][] getSearchArea() {
        return searchArea;
    }

    public void setSearchArea(Coordinate[][] searchArea) {
        this.searchArea = searchArea;
    }

    public PriorityQueue<Coordinate> getOpenList() {
        return openList;
    }

    public void setOpenList(PriorityQueue<Coordinate> openList) {
        this.openList = openList;
    }

    public List<Coordinate> getClosedList() {
        return closedList;
    }

    public void setClosedList(List<Coordinate> closedList) {
        this.closedList = closedList;
    }

    public int getHvCost() {
        return hvCost;
    }

    public void setHvCost(int hvCost) {
        this.hvCost = hvCost;
    }

    private int getDiagonalCost() {
        return diagonalCost;
    }

    private void setDiagonalCost(int diagonalCost) {
        this.diagonalCost = diagonalCost;
    }
}
