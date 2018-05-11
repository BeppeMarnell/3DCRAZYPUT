package com.mygdx.game.Bott;

import com.mygdx.game.WObjects.Ball;
import com.mygdx.game.WObjects.Map;

import java.util.ArrayList;
import java.util.List;

public class Bot {



    public Ball ball;

    public Bot(Map map){

        BotMap botMap = new BotMap(map);
       // bfs(botMap);
        astar(botMap);

    }


    private void bfs(BotMap botMap) {
        PathFinder pathFinder = new PathFinder();
        List<Coordinate> path = pathFinder.solve(botMap);
        List<Coordinate> straightPath = separateShot(path);

        botMap.printPath(path);
        botMap.printPath(straightPath);

        System.out.println(straightPath.size());

        botMap.reset();
    }


    private void astar(BotMap botMap){
        Node start = new Node(botMap.start.getX(),botMap.start.getY() );

        Node end = new Node(botMap.end.getX(), botMap.end.getY());



        AStar aStar = new AStar(botMap.maze.length, botMap.maze[0].length, start, end);

       for(int i =0; i < botMap.maze.length; i++){
           for(int j = 0; j < botMap.maze[0].length; j++){
               if(botMap.maze[i][j]==1){
                   aStar.setBlock(i,j);
               }
           }
       }

        for(int i =7; i < botMap.maze.length-7; i++){
            for(int j = 7; j < botMap.maze[0].length-7; j++){
                if(botMap.maze[i][j]==1){
                    aStar.setBlock(i+2, j);
                    aStar.setBlock(i-2, j);
                    aStar.setBlock(i, j+2);
                    aStar.setBlock(i ,j-2);
                    aStar.setBlock(i+2, j+2);
                    aStar.setBlock(i-2, j+2);
                    aStar.setBlock(i-2, j-2);
                    aStar.setBlock(i+2 ,j-2);

                }
            }
        }


        List<Node> path = aStar.findPath();

        System.out.println(path.size());
        System.out.println(separateShotAStar(path).size());


        botMap.printPathNode(path);

        botMap.reset();
    }




    public  List<Coordinate> separateShot(List<Coordinate> path) throws IndexOutOfBoundsException {
       try { List<Coordinate> shotsLV = new ArrayList<>();
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
       catch (Exception e ){
           System.out.println("No Solution!");
           List<Coordinate> emptyList = new ArrayList<>();
           return emptyList;
       }


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



    public  List<Node> separateShotAStar(List<Node> path) throws IndexOutOfBoundsException {
        try {
            List<Node> shotsLV = new ArrayList<>();
            Node origin = path.get(0);
            double slope;

            if (path.size() > 1) {
                slope = calculateSlopeAStar(origin, path.get(1));

                for (int x = 2; x < path.size(); x++) {
                    double tempSlope = calculateSlopeAStar(origin, path.get(x));

                    if (tempSlope != slope) {

                        origin = path.get(x - 1);
                        shotsLV.add(origin);
                        slope = calculateSlopeAStar(origin, path.get(x));
                    }
                }
            }

            return shotsLV;
        }
        catch (Exception e ){
            System.out.println("No Solution!");
            List<Node> emptyList = new ArrayList<>();
            return emptyList;
        }


    }

    public  double calculateSlopeAStar(Node c1, Node c2) {
        if (calculateXAStar(c1, c2) != 0)
            return calculateYAStar(c1, c2)/(calculateXAStar(c1, c2));
        else
            return -1;
    }

    public  double calculateYAStar(Node c1, Node c2) {
        return (c2.getCol() - c1.getCol());
    }

    public  double calculateXAStar(Node c1, Node c2) {
        return (c2.getRow() - c1.getRow());
    }
}

