package com.mygdx.game.Bott;

import com.mygdx.game.WObjects.Map;

import java.util.Arrays;
import java.util.List;


/**
 * This class is used to get the real map of the game,
 * turn it into a 2d array, and finding the start point,
 * end point and everything that blocks the path.
 */
public class AlgorithmMap {


    public final int WALL_TREE_WATER = 1;
    public final int ROAD = 0;
    public final int START = 7;
    public final int EXIT = 9;
    public final int PATH = 4;

    int[][] map;
    public boolean[][] visited;
    Coordinate start;
    Coordinate end;

    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";

    public AlgorithmMap(Map map){
        this.map = map.getArrayMap(1);
        this.visited = new boolean[this.map.length][this.map[0].length];
        setStartAndEnd();
    }

    /**
     * Finds the start(ball) and the end(hole) coordinates.
     */
    public void setStartAndEnd(){
        for(int i = 0; i<map.length; i++){
            for(int j = 0; j<map[0].length; j++){
                if(map[i][j]==7){
                   this.start = new Coordinate(i,j);
                }
                else if(map[i][j]==9){
                    this.end = new Coordinate(i,j);
                }
            }
        }
    }



    public String toString(int[][] maze) {
        StringBuilder result = new StringBuilder(getWidth() * (getHeight() + 1));
        for (int col = 0; col < getWidth(); col++) {
            for (int row = 0; row < getHeight(); row++) {
                if (maze[row][col] == ROAD) {
                    result.append("  ");
                } else if (maze[row][col] == WALL_TREE_WATER) {
                    result.append("■ ");
                } else if (maze[row][col] == START) {
                    result.append(ANSI_GREEN+"S "+ANSI_RESET);
                } else if (maze[row][col] == EXIT) {
                    result.append(ANSI_RED+"E "+ANSI_RESET);
                } else {
                    result.append(ANSI_BLUE+ ". "+ANSI_RESET);
                }
            }
            result.append('\n');
        }
        return result.toString();
    }

    public void printPath(List<Coordinate> path) {
        int[][] tempMaze = Arrays.stream(map).map(int[]::clone).toArray(int[][]::new);
        for (Coordinate coordinate : path) {
            if (isStart(coordinate.getX(), coordinate.getY()) || isExit(coordinate.getX(), coordinate.getY())) {
                continue;
            }
            tempMaze[coordinate.getX()][coordinate.getY()] = PATH;
        }
        System.out.println(toString(tempMaze));
    }

    public void reset() {
        for (int i = 0; i < visited.length; i++)
            Arrays.fill(visited[i], false);
    }


    public int getHeight(){ return map.length; }

    public int getWidth(){ return map[0].length; }

    public boolean isStart(int x, int y){ return x==start.getX() && y==start.getY(); }

    public boolean isExit(int x, int y){ return x==end.getX() && y==end.getY(); }

    public Coordinate getStart() { return start; }

    public void setStart(int x, int y) { this.start = new Coordinate(x,y); }

    public Coordinate getEnd() { return end; }

    public boolean isExplored(int row, int col) { return visited[row][col]; }

    public boolean isWall(int row, int col) { return map[row ][col] == WALL_TREE_WATER; }

    public void setVisited(int row, int col, boolean value) { visited[row][col] = value; }

    public boolean isValidLocation(int row, int col) {
        if (row < 0 || row >= getHeight() || col < 0 || col >= getWidth()) { return false; }
        else return true;
    }



}
