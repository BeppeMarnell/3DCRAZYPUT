package com.mygdx.game.Bott;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PathFinding2 {
    private static final int[][] DIRECTIONS = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

    public List<Coordinate> solve(BotMap botMap) {
        List<Coordinate> path = new ArrayList<>();
        if (explore(botMap, botMap.getEntry().getX(), botMap.getEntry()
                        .getY(), path)) {
            return path;
        }
        return Collections.emptyList();
    }

    private boolean explore(BotMap botMap, int row, int col, List<Coordinate> path) {
        if (!botMap.isValidLocation(row, col) || botMap.isWall(row, col) || botMap.isExplored(row, col)) {
            return false;
        }

        path.add(new Coordinate(row, col));
        botMap.setVisited(row, col, true);

        if (botMap.isExit(row, col)) {
            return true;
        }

        for (int[] direction : DIRECTIONS) {
            Coordinate coordinate = getNextCoordinate(row, col, direction[0], direction[1]);
            if (explore(botMap, coordinate.getX(), coordinate.getY(), path)) {
                return true;
            }
        }

        path.remove(path.size() - 1);
        return false;
    }

    private Coordinate getNextCoordinate(int row, int col, int i, int j) {
        return new Coordinate(row + i, col + j);
    }
}
