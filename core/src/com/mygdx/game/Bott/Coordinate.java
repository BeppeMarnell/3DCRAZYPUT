package com.mygdx.game.Bott;

import com.badlogic.gdx.math.Vector2;

/**
 * The coordinates class is equivalent to
 * a node class.
 */
public class Coordinate extends Vector2 {

    public int x;
    public int y;
    Coordinate parent;
    /*
     *Fields below used for methods for A*.
     */
    int g;
    int f;
    int h;
    boolean isBlock;


    Coordinate(int x, int y){
        this.x = x;
        this.y = y;
        this.parent = null;
    }

    Coordinate(int x, int y, Coordinate parent){
        this.x = x;
        this.y = y;
        this.parent = parent;
    }


    Coordinate getParent() { return parent; }

    /*
     *Methods below used by A* algorithm.
     */

    /**
     * Since the algorithm works on a grid,
     * we choose the Manhattan distance.
     * @param finalCoordinate
     */
    public void calculateHeuristic( Coordinate finalCoordinate ){

        this.h = Math.abs(finalCoordinate.getX() - getX()) + Math.abs(finalCoordinate.getY() - getY());

    }

    public void setCoordinateData(Coordinate currentCoordinate, int cost) {
        int gCost = currentCoordinate.getG() + cost;
        setParent(currentCoordinate);
        setG(gCost);
        calculateFinalCost();
    }


    public boolean checkBetterPath(Coordinate currentCoordinate, int cost) {
        int gCost = currentCoordinate.getG() + cost;
        if (gCost < getG()) {
            setCoordinateData(currentCoordinate, cost);
            return true;
        }
        return false;
    }

    private void calculateFinalCost() {
        int finalCost = getG() + getF();
        setF(finalCost);
    }


    @Override
    public boolean equals(Object arg0) {
        Coordinate other = (Coordinate) arg0;
        return this.getX() == other.getX() && this.getY() == other.getY();
    }


    /*
     * Below are getters and setters for each field.
     */

    public int getX(){ return x; }

    public int getY(){ return  y; }

    public void setX(int x) { this.x = x; }

    public void setY(int y) { this.y = y; }

    public void setParent(Coordinate parent) { this.parent = parent; }

    public int getG() { return g; }

    public void setG(int g) { this.g = g; }

    public int getF() { return f; }

    public void setF(int f) { this.f = f; }

    public int getH() { return h; }

    public void setH(int h) { this.h = h; }

    public void setBlock(boolean block){ this.isBlock = block; }

    public boolean isBlock(){ return isBlock; }

}