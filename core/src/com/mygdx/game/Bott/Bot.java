package com.mygdx.game.Bott;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.WObjects.Map;

import java.util.ArrayList;
import java.util.List;

public class Bot {

    public Bot(Map map, int option){
        AlgorithmMap algorithmMap = new AlgorithmMap(map);

        long startTime = System.currentTimeMillis();
        BFS(algorithmMap);
        //aStar(algorithmMap);
        long endTime = System.currentTimeMillis();
        System.out.println("That took " + (endTime - startTime) + " milliseconds");
    }

    private void BFS(AlgorithmMap algorithmMap){
        BreadthFirstSearch breadthFirstSearch = new BreadthFirstSearch();
        List<Coordinate> solvedPath = breadthFirstSearch.BreadFirstSearchSolve(algorithmMap);
        List<Coordinate> finalPath = separateShot(solvedPath);
        List<Vector2> path = toVector2(finalPath);
        printVector(path);
       // algorithmMap.printPath(solvedPath);
        algorithmMap.reset();
    }

    private void aStar(AlgorithmMap algorithmMap){
        AStarAlgorithm aStarAlgorithm =
                new AStarAlgorithm(algorithmMap.map.length, algorithmMap.map[0].length, algorithmMap.start,algorithmMap.end);

        for(int i =0; i < algorithmMap.map.length; i++){
            for(int j = 0; j < algorithmMap.map[0].length; j++){
                if(algorithmMap.map[i][j]==1){
                    aStarAlgorithm.setBlock(i,j);
                }
            }
        }

        for(int i =64; i < algorithmMap.map.length-64; i++){
            for(int j = 64; j < algorithmMap.map[0].length-64; j++){
                if(algorithmMap.map[i][j]==1){
                    aStarAlgorithm.setBlock(i+31, j);
                    aStarAlgorithm.setBlock(i-31, j);
                    aStarAlgorithm.setBlock(i, j+31);
                    aStarAlgorithm.setBlock(i ,j-31);
                    aStarAlgorithm.setBlock(i+31, j+31);
                    aStarAlgorithm.setBlock(i-31, j+31);
                    aStarAlgorithm.setBlock(i-31, j-31);
                    aStarAlgorithm.setBlock(i+31 ,j-31);

                }
            }
        }
        List<Coordinate> path = aStarAlgorithm.findPath();
        System.out.println("Solved");
        List<Coordinate> finalPath = separateShot(path);
        List<Vector2> Vpath = toVector2(finalPath);
        algorithmMap.printPath(path);
       // algorithmMap.printPath(finalPath);
        algorithmMap.reset();

    }

    /**
     * Method which removes the nodes in a straight path.
     * @param path the current path
     * @return the path with points.
     * @throws IndexOutOfBoundsException if the maze hasn't a solution.
     */
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

    public List<Vector2> toVector2(List<Coordinate> path){
       List<Vector2> vector2s = new ArrayList<>();
        for(int i = path.size()-1; i>-1; i--){
            Vector2 cur = new Vector2(path.get(i).x, path.get(i).y);
            vector2s.add(cur);
        }
        return vector2s;
    }

    public void printVector(List<Vector2> pathInVector){
        for(int i = 0; i<pathInVector.size(); i++){
            System.out.println(pathInVector.get(i).x+ " : "+ pathInVector.get(i).y);
        }
    }



}
