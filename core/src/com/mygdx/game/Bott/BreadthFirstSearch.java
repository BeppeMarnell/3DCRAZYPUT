package com.mygdx.game.Bott;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BreadthFirstSearch {

    private final int[][] DIRECTIONS = { {0, 1 }, {0, -1 }, {1, 0 }, {-1, 0 }};
    private final int[][] DIAGONAL_DIRECTIONS = { {1,1}, {-1,-1}, {1,-1}, {-1,1}};
    private final int[][] DIAGONAL_DIRECTIONS_1 = { {-1,-1}, {1,-1}, {-1,1}};
    private final int[][] DIAGONAL_DIRECTIONS_2 = { {1,1}, {1,-1}, {-1,1}};
    private final int[][] DIAGONAL_DIRECTIONS_3 = { {1,1}, {-1,-1}, {1,-1}};
    private final int[][] DIAGONAL_DIRECTIONS_4 = { {1,1}, {-1,-1}, {-1,1} };



    public List<Coordinate> BreadFirstSearchSolve(AlgorithmMap map){

        LinkedList<Coordinate> openList = new LinkedList<>(); //A FIFO set
        //initialize
        Coordinate start = map.getStart();
        openList.add(start);

        //For each node on the current level expand and process, if no children (leaf, then unwind)
        while(!openList.isEmpty()){
            Coordinate current = openList.remove();

            if (!map.isValidLocation(current.getX(), current.getY()) || map.isExplored(current.getX(), current.getY())) {
                continue;
            }

            if (map.isWall(current.getX(), current.getY())) {
                map.setVisited(current.getX(), current.getY(), true);
                continue;
            }

            if (map.isExit(current.getX(), current.getY())) {
                return constructPath(current);
            }
            for (int[] direction : DIRECTIONS) {
                Coordinate coordinate = new Coordinate(current.getX() + direction[0], current.getY() + direction[1], current);
                openList.add(coordinate);
                map.setVisited(current.getX(), current.getY(), true);
            }

            for(int[] diagonalDirection : DIAGONAL_DIRECTIONS){

                if ((   (map.isWall(current.getX()+1,current.getY())) || (map.isWall(current.getX(),current.getY()+1)   )  ) == true){
                    for(int[] diagonaldirection1 : DIAGONAL_DIRECTIONS_1) {
                        Coordinate coordinate = new Coordinate(current.getX() + diagonaldirection1[0], current.getY() + diagonaldirection1[1], current);
                        openList.add(coordinate);
                        map.setVisited(current.getX(), current.getY(), true);
                    }
                }

                else if((   (map.isWall(current.getX(),current.getY()-1)))  || (map.isWall(current.getX()-1,current.getY())  ) == true   ){
                    for(int[] diagonalDirections2 : DIAGONAL_DIRECTIONS_2) {
                        Coordinate coordinate = new Coordinate(current.getX() + diagonalDirections2[0], current.getY() + diagonalDirections2[1], current);
                        openList.add(coordinate);
                        map.setVisited(current.getX(), current.getY(), true);
                    }
                }
                else if((   (map.isWall(current.getX()-1,current.getY())) || (map.isWall(current.getX(),current.getY()+1) )   ) == true){
                    for(int[] diagonalDirections3 : DIAGONAL_DIRECTIONS_3) {
                        Coordinate coordinate = new Coordinate(current.getX() + diagonalDirections3[0], current.getY() + diagonalDirections3[1], current);
                        openList.add(coordinate);
                        map.setVisited(current.getX(), current.getY(), true);
                    }
                }
                else if((    (map.isWall(current.getX()+1,current.getY())) || (map.isWall(current.getX(),current.getY()-1))   ) == true){
                    for(int[] diagonalDirections4 : DIAGONAL_DIRECTIONS_4) {
                        Coordinate coordinate = new Coordinate(current.getX() + diagonalDirections4[0], current.getY() + diagonalDirections4[1], current);
                        openList.add(coordinate);
                        map.setVisited(current.getX(), current.getY(), true);
                    }
                }
                else{
                    Coordinate coordinate = new Coordinate(current.getX() + diagonalDirection[0], current.getY() + diagonalDirection[1], current);
                    openList.add(coordinate);
                    map.setVisited(current.getX(), current.getY(), true);
                }
            }
        }
        return Collections.emptyList();
    }

    private List<Coordinate> constructPath(Coordinate current){
        List<Coordinate> path = new ArrayList<>();
        Coordinate iter = current;

        while (iter != null) {
            path.add(iter);
            iter = iter.parent;
        }


        return path;

    }

}
