package com.mygdx.game.Bott;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class PathFinder {
    private  final int[][] DIRECTIONS = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } //, {1,1}, {-1,-1}, {1,-1}, {-1,1}
     };

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
