package com.mygdx.game.Bott;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class ThetaStarAlgorithm {

    private List<Coordinate> path;
    private final int[][] DIRECTIONS = { {0, 1 }, {0, -1 }, {1, 0 }, {-1, 0 }};

    private Stack<Coordinate> openList, closedList;

    public List<Coordinate> calculateTheta(Coordinate start, Coordinate end) {
        start.setG(0);
        start.setParent(start);

        openList = new Stack<>();
        openList.add(start);

        closedList = new Stack<>();

        while (!openList.isEmpty()) {
            Coordinate s = openList.pop();
            if (s == end)
                return reconstructPath(s);
            closedList.push(start);

            for (int[] direction: DIRECTIONS) {
                Coordinate neighb = new Coordinate(s.x + direction[0], s.y + direction[1]);
                if (!closedList.contains(neighb)) {
                    if (!openList.contains(neighb)) {
                        neighb.setG(100000);
                        neighb.setParent(null);
                    }
                    updatePath(s, neighb);
                }
            }
        }
        return null;
    }

    private List<Coordinate> reconstructPath(Coordinate s) {
        List<Coordinate> path = new LinkedList<>();
        path.add(s);

        if (s.getParent() != s)
            ((LinkedList<Coordinate>) path).addAll(reconstructPath(s.parent));

        else return path;

        return null;
    }


    public int calculateEuclidianH(Coordinate c1, Coordinate c2){
        int dx = Math.abs(c2.getX() - c1.getX());
        int dy = Math.abs(c1.getY() - c2.getX());
        return (int) Math.sqrt(dx*dx - dy*dy);
    }

    public void updatePath(Coordinate start, Coordinate neighbour) {
        Coordinate parentS = start.getParent();
        if (lineOfSight(parentS, neighbour)) {
            if (parentS.getG() + calculateEuclidianH(parentS, neighbour) < neighbour.getG()) {
                neighbour.setG(parentS.getG() + calculateEuclidianH(parentS, neighbour));
                neighbour.setParent(start);
                if (openList.contains(neighbour))
                    openList.remove(neighbour);
                openList.push(neighbour);
            }
        }
        else {
            if (start.getG() + calculateEuclidianH(start , neighbour) < neighbour.getG()) {
                neighbour.setG(start.getG() + calculateEuclidianH(start, neighbour));
                neighbour.setParent(start);
                if (openList.contains(neighbour))
                    openList.remove(neighbour);
                openList.push(neighbour);
            }
        }
    }

    public boolean lineOfSight(Coordinate start, Coordinate end) {
        int x1 = start.x;
        int y1 = start.y;
        int x2 = end.x;
        int y2 = end.y;

        int dy = y2 - y1;
        int dx = x2 - x1;
        int f = 0;
        int signY = 1;
        int signX = 1;
        int offsetX = 0;
        int offsetY = 0;

        if (dy < 0) {
            dy *= -1;
            signY = -1;
            offsetY = -1;
        }

        if (dx < 0) {
            dx *= -1;
            signX = -1;
            offsetX = -1;
        }

        if (dx >= dy) {
            while (x1 != x2) {
                f += dy;
                if (f >= dx) {
                    if (isBlocked(x1 + offsetX, y1 + offsetY))
                        return false;
                    y1 += signY;
                    f -= dx;
                }
                if (f != 0 && isBlocked(x1 + offsetX, y1 + offsetY))
                    return false;
                if (dy == 0 && isBlocked(x1 + offsetX, y1) && isBlocked(x1 + offsetX, y1 - 1))
                    return false;

                x1 += signX;
            }
        } else {
            while (y1 != y2) {
                f += dx;
                if (f >= dy) {
                    if (isBlocked(x1 + offsetX, y1 + offsetY))
                        return false;
                    x1 += signX;
                    f -= dy;
                }

                if (f != 0 && isBlocked(x1 + offsetX, y1 + offsetY))
                    return false;

                if (dx == 0 && isBlocked(x1, y1 + offsetY) && isBlocked(x1 - 1, y1 + offsetY))
                    return false;

                y1 += signY;

            }
        }

        return true;
    }

    private boolean isBlocked(int i, int i1) {
        return false;
    }
}

