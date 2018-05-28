package com.mygdx.game.Bott;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Utils.Helper;
import com.mygdx.game.WObjects.Ball;
import com.mygdx.game.WObjects.Map;

import java.util.ArrayList;
import java.util.List;

public class Bot {

    private Map mapO;

    public boolean movingBall;
    private ArrayList<MoveTo> solutionPath;
    private int solutionIndex;

    public Bot(Map map){
        //copy an instance of the map to calculate heights
        this.mapO = map;
        movingBall = false;
        solutionPath = new ArrayList<>();
        solutionIndex = 0;
    }

    public void CalculateAStar(int[][] map){
        //reset to initial state of the path
        ArrayList<Vector2> path = new ArrayList<>();
        solutionPath.clear();
        solutionIndex = 0;

        //set up AStar algorithm
        AlgorithmMap algorithmMap = new AlgorithmMap(map);
        AStarAlgorithm aStarAlgorithm =
                new AStarAlgorithm(algorithmMap.map.length, algorithmMap.map[0].length, algorithmMap.start,algorithmMap.end);

        for(int i =3; i < algorithmMap.map.length-4; i++){
            for(int j = 3; j < algorithmMap.map[0].length-4; j++){
                if(algorithmMap.map[i][j]==1){
                    aStarAlgorithm.setBlock(i+1, j);
                    aStarAlgorithm.setBlock(i,j+1);
                    aStarAlgorithm.setBlock(i-1,j);
                    aStarAlgorithm.setBlock(i,j-1);
                }
            }
        }

        //get the list of coordinates
        List<Coordinate> finalPath = separateShot(aStarAlgorithm.findPath());
        //algorithmMap.printPath(finalPath); //uncomment to see the printed
        algorithmMap.reset();

        int division = map.length/20;

        //add the start
        float x = Helper.map(algorithmMap.getStart().x, 0, 20*division - division/2,-80, 80);
        float y = Helper.map(algorithmMap.getStart().y, 0, 14*division - division/2,-56, 56);
        path.add(new Vector2(x, y));

        for(Coordinate c: finalPath){
            //translate array coordinates to world coordinates
            x = Helper.map(c.x, 0, 20*division - division/2,-80, 80);
            y = Helper.map(c.y, 0, 14*division - division/2,-56, 56);
            path.add(new Vector2(x, y));

        }
        //add the hole position
        path.add(Map.getHolePosTransl());

        //calculate the solution vectors
        for(int i=1; i<path.size(); i++){
          //  float diffHeigh = Math.abs(mapO.getHeight(path.get(i).cpy(),0) - mapO.getHeight(path.get(i).cpy(),0));

            solutionPath.add(new MoveTo(path.get(i).cpy().sub(path.get(i-1).cpy()), 10));
        }

        //for(Vector2 c: path)System.out.println(c.toString());

        //set true to start moving the ball to the hole
        movingBall = true;
    }

    /**
     * Each time the ball updates its position referred to the solution
     * array calculated
     */
    public void act(Ball ball){
        //if i've finished the directions then the ball has arrived (supposed to)
        if(solutionIndex == solutionPath.size()){
            movingBall = false;
            return;
        }

        //move the ball in the direction and decrease the iterations
        ball.move(solutionPath.get(solutionIndex).to.cpy().scl(210f));
        solutionPath.get(solutionIndex).iter--;

        //if the iteration is done then i switch to the next
        if(solutionPath.get(solutionIndex).iter == 0) {
            solutionIndex++;
        }
    }

    /**
     * Method which removes the nodes in a straight path.
     * @param path the current path
     * @return the path with points.
     * @throws IndexOutOfBoundsException if the maze hasn't a solution.
     */
    public  List<Coordinate> separateShot(List<Coordinate> path){
        //initialize the array
        List<Coordinate> shotsLV = new ArrayList<>();

        try {
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
        } catch (Exception e ){
            System.out.println("No Solution!");
        }finally {
            return shotsLV;
        }
    }

    public  double calculateSlope(Coordinate c1, Coordinate c2) {
        if ((c2.x - c1.x) != 0)
            return (c2.y - c1.y)/(c2.x - c1.x);
        else
            return -1;
    }

    public Vector2 calculateForce(Vector2 distanceTo, float time) {
        float inverseTime = 1 / time;
        return new Vector2(distanceTo.scl(Ball.MASS * inverseTime));
    }

    class MoveTo{
        public int iter;
        public Vector2 to;

        public MoveTo(Vector2 to, int iter){
            this.iter = iter;
            this.to = to;
        }
    }
}
