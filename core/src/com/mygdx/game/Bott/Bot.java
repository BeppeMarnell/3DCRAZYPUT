package com.mygdx.game.Bott;

import com.mygdx.game.WObjects.Map;

import java.util.HashMap;
import java.util.List;

public class Bot {
 public Ball ball;

    public Bot(Map map){

        BotMap botMap = new BotMap(map);
        dfs(botMap);
    }


    private void dfs(BotMap botMap) {
        PathFinder pathFinder = new PathFinder();
        List<Coordinate> path = pathFinder.solve(botMap);
        System.out.println(path.size());
        botMap.printPath(path);
        botMap.printPath(separateShot(path));
        botMap.reset();
    }

    public  List<Coordinate> separateShot(List<Coordinate> path) {
        List<Coordinate> shotsLV = new ArrayList<>();
        Coordinate origin = path.get(0);
        double slope;

        if (path.size() > 1) {
            slope = calculateSlope(origin, path.get(1));

            for (int x = 2; x < path.size(); x++) {
                double tempSlope = calculateSlope(origin, path.get(x));

                if (tempSlope != slope) {

                    origin = path.get(x - 1);
                    shotsLV.add(origin);
                    slope = calculateSlope(origin, path.get(x));
                }
            }
        }

        return shotsLV;
    }

    public  double calculateSlope(Coordinate c1, Coordinate c2) {
        if (calculateX(c1, c2) != 0)
            return calculateY(c1, c2)/(calculateX(c1, c2));
        else
            return -1;
    }

    public  double calculateY(Coordinate c1, Coordinate c2) {
        return (c2.y - c1.y);
    }

    public  double calculateX(Coordinate c1, Coordinate c2) {
        return (c2.x - c1.x);
    }
}
