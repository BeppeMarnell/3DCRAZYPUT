package com.mygdx.game.Bott;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PathFinder {
   private  final int[][] DIRECTIONS = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 }};
    private final int[][] DDIRECTIONS = { {1,1}, {-1,-1}, {1,-1}, {-1,1}};
    private final int[][] DDIRECTIONS1 = { {-1,-1}, {1,-1}, {-1,1}};
    private final int[][] DDIRECTIONS2 = { {1,1}, {1,-1}, {-1,1}};
    private final int[][] DDIRECTIONS3 = { {1,1}, {-1,-1}, {1,-1}};
    private final int[][] DDIRECTIONS4 = { {1,1}, {-1,-1}, {-1,1} };

    public List<Coordinate> solve(BotMap botMap) {
        LinkedList<Coordinate> nextToVisit = new LinkedList<>();
        Coordinate start = botMap.getEntry();
        nextToVisit.add(start);

        while (!nextToVisit.isEmpty()) {
            Coordinate cur = nextToVisit.remove();

            if (!botMap.isValidLocation(cur.getX(), cur.getY()) || botMap.isExplored(cur.getX(), cur.getY())) {
                continue;
            }

            if (botMap.isWall(cur.getX(), cur.getY())) {
                botMap.setVisited(cur.getX(), cur.getY(), true);
                continue;
            }

            if (botMap.isExit(cur.getX(), cur.getY())) {
                return backtrackPath(cur);
            }

            for (int[] direction : DIRECTIONS) {
                Coordinate coordinate = new Coordinate(cur.getX() + direction[0], cur.getY() + direction[1], cur);
                nextToVisit.add(coordinate);
                botMap.setVisited(cur.getX(), cur.getY(), true);
            }

            for(int[] diagonaldirection : DDIRECTIONS){

                if ((   (botMap.isWall(cur.getX()+1,cur.getY())) || (botMap.isWall(cur.getX(),cur.getY()+1)   )  ) == true){
                    for(int[] diagonaldirection1 : DDIRECTIONS1) {
                        Coordinate coordinate = new Coordinate(cur.getX() + diagonaldirection1[0], cur.getY() + diagonaldirection1[1], cur);
                        nextToVisit.add(coordinate);
                        botMap.setVisited(cur.getX(), cur.getY(), true);
                    }
                }


                else if((   (botMap.isWall(cur.getX(),cur.getY()-1)))  || (botMap.isWall(cur.getX()-1,cur.getY())  ) == true   ){
                    for(int[] diagonaldirection2 : DDIRECTIONS2) {
                        Coordinate coordinate = new Coordinate(cur.getX() + diagonaldirection2[0], cur.getY() + diagonaldirection2[1], cur);
                        nextToVisit.add(coordinate);
                        botMap.setVisited(cur.getX(), cur.getY(), true);
                    }
                }
                else if((   (botMap.isWall(cur.getX()-1,cur.getY())) || (botMap.isWall(cur.getX(),cur.getY()+1) )   ) == true){
                    for(int[] diagonaldirection3 : DDIRECTIONS3) {
                        Coordinate coordinate = new Coordinate(cur.getX() + diagonaldirection3[0], cur.getY() + diagonaldirection3[1], cur);
                        nextToVisit.add(coordinate);
                        botMap.setVisited(cur.getX(), cur.getY(), true);
                    }
                }
                else if((    (botMap.isWall(cur.getX()+1,cur.getY())) || (botMap.isWall(cur.getX(),cur.getY()-1))   ) == true){
                    for(int[] diagonaldirection4 : DDIRECTIONS4) {
                        Coordinate coordinate = new Coordinate(cur.getX() + diagonaldirection4[0], cur.getY() + diagonaldirection4[1], cur);
                        nextToVisit.add(coordinate);
                        botMap.setVisited(cur.getX(), cur.getY(), true);
                    }
                }
                else{
                    Coordinate coordinate = new Coordinate(cur.getX() + diagonaldirection[0], cur.getY() + diagonaldirection[1], cur);
                    nextToVisit.add(coordinate);
                    botMap.setVisited(cur.getX(), cur.getY(), true);
                }
            }
        }
        return Collections.emptyList();
    }

    private List<Coordinate> backtrackPath(Coordinate cur) {
        List<Coordinate> path = new ArrayList<>();
        Coordinate iter = cur;

        while (iter != null) {
            path.add(iter);
            iter = iter.parent;
        }
        System.out.println("Solved");

        return path;
    }



}
