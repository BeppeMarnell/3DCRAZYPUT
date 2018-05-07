package com.mygdx.game.Bott;

import com.mygdx.game.WObjects.Map;

import java.util.HashMap;
import java.util.List;

public class Bot {



    public Bot(Map map){

        BotMap botMap = new BotMap(map);
        dfs(botMap);
    }


    private void dfs(BotMap botMap) {
        PathFinder pathFinder = new PathFinder();
        List<Coordinate> path = pathFinder.solve(botMap);
        for(int i = 0; i < path.size(); i++) {
            System.out.println(path.get(i).getStringX() + " : "+path.get(i).getStringY());
        }
        separateShot(path);

        System.out.println(path.size());
        System.out.println(separateShot(path).toString());
        botMap.printPath(path);

        botMap.reset();
    }

    public static HashMap<Double, Double> separateShot(List<Coordinate> path) {
        try {
            HashMap<Double, Double> shotsLV = new HashMap<Double, Double>();
            Coordinate origin = path.get(0);
            double slope;

            shotsLV.put(1.0, 2.2);

            if (path.size() > 1) {
                slope = calculateSlope(origin, path.get(1));

                for (int x = 2; x < path.size(); x++) {
                    double tempSlope = calculateSlope(origin, path.get(x));
                    if (tempSlope != slope) {
                        shotsLV.put(calculateX(origin, path.get(x - 1)), calculateY(origin, path.get(x - 1)));
                        origin = path.get(x - 1);
                        slope = calculateSlope(origin, path.get(x));
                    }
                }
            }

            return shotsLV;
        }
        catch (Exception e){
            System.out.println("Not solution available!");
            HashMap<Double,Double> empty = new HashMap<Double, Double>();
            return empty;
        }

    }

    public static double calculateSlope(Coordinate c1, Coordinate c2) {

        if(c2.x - c1.x != 0 ){
        return (c2.y - c1.y)/(c2.x - c1.x);
        }
        else return 0;
    }

    public static double calculateY(Coordinate c1, Coordinate c2) {
        return (c2.y - c1.y);
    }

    public static double calculateX(Coordinate c1, Coordinate c2) {
        return (c2.x - c1.x);
    }


    public void removeStraight(List<Coordinate> path) {


        }






}
