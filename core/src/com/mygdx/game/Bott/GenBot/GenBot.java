package com.mygdx.game.Bott.GenBot;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Utils.Helper;
import com.mygdx.game.WObjects.Ball;
import com.mygdx.game.WObjects.Map;
import com.mygdx.game.Bott.MoveTo;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GenBot {
    private Map mapO;

    public boolean movingBall;

    //calculated path that has to be applied to the ball
    private ArrayList<MoveTo> solutionPath;
    private int solutionIndex;

    //calculate path from the algorithm
    private ArrayList<Vector2> path;

    public GenBot(Map map){
        //copy an instance of the map to calculate heights
        this.mapO = map;
        movingBall = false;
        solutionPath = new ArrayList<>();
        solutionIndex = 1;
        path = new ArrayList<>();
    }

    public void calculateGenetic(Vector2 ballPos, int millisec)throws Exception{

        ExecutorService executor = Executors.newCachedThreadPool();
        //call the new genetic thread for a certain amount of time
        Future<ArrayList<GeneDirection>> futureCall = executor.submit(new Genetic2D(mapO, ballPos, millisec));
        ArrayList<GeneDirection> result = futureCall.get(); // Here the thread will be blocked

        //convert the array
        for(GeneDirection gD: result)solutionPath.add(new MoveTo(gD.getGetDirection().cpy().nor(), 10));

        //shoot down the thread
        futureCall.cancel(true);
        executor.shutdownNow();
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
}
