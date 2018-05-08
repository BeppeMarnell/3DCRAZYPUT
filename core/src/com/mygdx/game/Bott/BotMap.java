package com.mygdx.game.Bott;

import com.mygdx.game.WObjects.Map;

import java.util.Arrays;
import java.util.List;


/**
 * A class that takes the 2D array of the map.
 * Then it finds the start and the end.
 * Also it contains import methods for the 
 * pathfinder algorithm to work.
 */
public class BotMap {

    /**
     * 1 walls - water - tree
     * 7 ball
     * 9 hole
     */
    private static final int ROAD = 0;
    private static final int WALL = 1;
    private static final int START = 7;
    private static final int EXIT = 9;
    private static final int PATH = 4;

    private int[][] maze;
    private boolean[][] visited;
    private Coordinate start;
    private Coordinate end;


    BotMap(Map map){
        this.maze = map.getArrayMap();
        this.visited = new boolean[maze.length][maze[0].length];
        setStartAndEnd();
    }
    
    /**
     * Find's the start and the end of the maze.
     * The position of the ball and hole.
     */
    public void setStartAndEnd(){
        for(int i = 0; i<maze.length; i++){
            for(int j = 0; j<maze[i].length; j++){
                if(maze[i][j]==START){
                    start = new Coordinate(i,j);
                }
                else if(maze[i][j]==EXIT){
                    end = new Coordinate(i,j);
                }
            }
        }
    }


    public int getHeight() {
        return maze.length;
    }

    public int getWidth() {
        return maze[0].length;
    }

    public Coordinate getEntry() {
        return start;
    }

    public Coordinate getExit() {
        return end;
    }

    public boolean isExit(int x, int y) {
        return x == end.getX() && y == end.getY();
    }

    public boolean isStart(int x, int y) {
        return x == start.getX() && y == start.getY();
    }
    
    public boolean isExplored(int row, int col) {
        return visited[row][col];
    }

    public boolean isWall(int row, int col) {
        return maze[row][col] == WALL;
    }

    public void setVisited(int row, int col, boolean value) {
        visited[row][col] = value;
    }

    public boolean isValidLocation(int row, int col) {
        if (row < 0 || row >= getHeight() || col < 0 || col >= getWidth()) {
            return false;
        }
        return true;
    }

    public void reset() {
        for (int i = 0; i < visited.length; i++)
            Arrays.fill(visited[i], false);
    }



    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";
    
    

    public void printPath(List<Coordinate> path) {
        int[][] tempMaze = Arrays.stream(maze).map(int[]::clone).toArray(int[][]::new);
        for (Coordinate coordinate : path) {
            if (isStart(coordinate.getX(), coordinate.getY()) || isExit(coordinate.getX(), coordinate.getY())) {
                continue;
            }
            tempMaze[coordinate.getX()][coordinate.getY()] = PATH;
        }
        System.out.println(toString(tempMaze));

    }



    public String toString(int[][] maze) {
        StringBuilder result = new StringBuilder(getWidth() * (getHeight() + 1));
        for (int col = 0; col < getWidth(); col++) {
            for (int row = 0; row < getHeight(); row++) {
                if (maze[row][col] == ROAD) {
                    result.append("  ");
                } else if (maze[row][col] == WALL) {
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
}
