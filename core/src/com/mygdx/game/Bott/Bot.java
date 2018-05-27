package com.mygdx.game.Bott;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.WObjects.Map;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class Bot {

    public AlgorithmMap algorithmMap;
    List<Coordinate> BFS_Path;
    List<Coordinate> ASTAR_Path;



    public Bot(Map map){
        algorithmMap = new AlgorithmMap(map);
        long lStartTime = System.nanoTime();
        System.out.println("Starttime: "+lStartTime);
        BFS(algorithmMap);
        long lEndTime = System.nanoTime();
        System.out.println("Endttime: "+lEndTime);
        long output = lEndTime - lStartTime;
        System.out.println("BFS - time: " + output);


        lStartTime = System.nanoTime();
        aStar(algorithmMap);
        lEndTime = System.nanoTime();
        output = lEndTime - lStartTime;
        output/=1000;
        System.out.println("astar - time: " + output/(10^9));
    }

     private void BFS(AlgorithmMap algorithmMap){
        BreadthFirstSearch breadthFirstSearch = new BreadthFirstSearch();
        List<Coordinate> solvedPath = breadthFirstSearch.BreadFirstSearchSolve(algorithmMap);
        List<Coordinate> finalPath = separateShot(solvedPath);
        BFS_Path = finalPath;
        System.out.println("BFS - Solved: "+solvedPath.size()+ "   "+ "Final : "+ finalPath.size());
        algorithmMap.printPath(solvedPath);
        algorithmMap.printPath(finalPath); //Comment this for not printing
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

        List<Coordinate> path = aStarAlgorithm.findPath();
        List<Coordinate> finalPath = separateShot(path);
        System.out.println("Astarprinting:");
        System.out.println("PathLength: "+path.size()+" FinalPath: "+finalPath.size());
        algorithmMap.printPath(finalPath); //Comment this for not printing
        ASTAR_Path = finalPath;
        algorithmMap.reset();

    }
    public void updateStart(Vector2 start){
        algorithmMap.setStart((int)start.x, (int)start.y);
    }

    public void updatePath(){
        BFS(algorithmMap);
        aStar(algorithmMap);
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


    public List<Coordinate> getBFS_Path() {
        return BFS_Path;
    }

    public List<Coordinate> getASTAR_Path() {
        return ASTAR_Path;
    }

}
