package com.mygdx.game.Bott;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Utils.Helper;
import com.mygdx.game.WObjects.Ball;
import com.mygdx.game.WObjects.Map;
import org.lwjgl.Sys;
import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.ArrayList;
import java.util.List;

public class Bot {

    private Map mapO;

    public boolean movingBall;

    //calculated path that has to be applied to the ball
    private ArrayList<MoveTo> solutionPath;
    private int solutionIndex;

    //calculate path from the algorithm
    private ArrayList<Vector2> path;

    public Bot(Map map){
        //copy an instance of the map to calculate heights
        this.mapO = map;
        movingBall = false;
        solutionPath = new ArrayList<>();
        solutionIndex = 1;
        path = new ArrayList<>();
    }

    public void CalculateAStar(int[][] map){
        //reset to initial state of the path
        solutionPath.clear();
        solutionIndex = 1;

        //set up AStar algorithm
        AlgorithmMap algorithmMap = new AlgorithmMap(map);
        AStarAlgorithm aStarAlgorithm =
                new AStarAlgorithm(algorithmMap.map.length, algorithmMap.map[0].length, algorithmMap.start,algorithmMap.end);

        for(int i =0; i < algorithmMap.map.length; i++){
            for(int j = 0; j < algorithmMap.map[0].length; j++){
                if(algorithmMap.map[i][j]==1){
                    aStarAlgorithm.setBlock(i, j);
                }
            }
        }

        //get the list of coordinates
        List<Coordinate> finalPath = aStarAlgorithm.findPath();
        algorithmMap.printPath(finalPath); //uncomment to see the printed
        algorithmMap.reset();


        //add the start
        float x = Helper.map(algorithmMap.getStart().x, 0, 20,-80, 80);
        float y = Helper.map(algorithmMap.getStart().y, 0, 14,-56, 56);
        path.add(new Vector2(x, y));

        for(Coordinate c: finalPath){
            //translate array coordinates to world coordinates
            x = Helper.map(c.x, 0, 20,-80, 80);
            y = Helper.map(c.y, 0, 14,-56, 56);
            path.add(new Vector2(x, y));

        }
        //add the hole position
        path.add(mapO.getHolePosTranslV2());

        //calculate the solution vectors
        for(int i=1; i<path.size(); i++){

            solutionPath.add(new MoveTo(path.get(i).cpy().sub(path.get(i-1).cpy()), 10));

            //calculate the scalar multiplicand
            float scalarM = forceToPoint(path.get(i-1).cpy(), path.get(i).cpy());
            //System.out.println(scalarM);

            solutionPath.get(i-1).to.scl(scalarM);
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
            System.out.println("ARRIVED");
            movingBall = false;
            return;
        }

        //move the ball in the direction
        ball.move(calculateForce(solutionPath.get(solutionIndex).to.cpy(),4));
        // decrease the number of iterations for that solutionIndex
        solutionPath.get(solutionIndex).iter--;

        Rectangle tmpRec = new Rectangle(0,0, 9f,9f);
        tmpRec.setCenter(path.get(solutionIndex+1).x, path.get(solutionIndex+1).y);

        //if the ball has arrived near the determined path point, then pass to the next solutionindex
        if(tmpRec.contains(new Vector2(ball.getPosition().x, ball.getPosition().z))) solutionIndex++;
    }

    public Vector2 calculateForce(Vector2 distanceTo, float time) {
        float inverseTime = 1 / time;
        return new Vector2(distanceTo.scl(Ball.MASS * inverseTime));
    }

    public float forceToPoint(Vector2 from, Vector2 to){
        //first vector starting from the intial position
        Vector2 ballPos = new Vector2( 0 , mapO.getHeight(new Vector2(from.x, from.y),0));

        //final point is the distance between the two points
        Vector2 posTo = new Vector2( from.cpy().dst(to), mapO.getHeight(new Vector2(to.x, to.y),0));

        //calculate the angle
        float angle = Helper.angleBetweenPoints(ballPos, posTo);

        //remove marginal errors
        if(angle < 0.1)angle =0;

        float force = Ball.MASS*9.81f*(float)Math.sin(angle) +
                Ball.MASS*9.81f*(float)Math.cos(angle)*mapO.getFriction(new Vector2(from.x, from.y));

        return Math.abs(force);
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
