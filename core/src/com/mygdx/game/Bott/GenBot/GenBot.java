package com.mygdx.game.Bott.GenBot;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Bott.MoveTo;
import com.mygdx.game.Utils.Helper;
import com.mygdx.game.WObjects.Ball;
import com.mygdx.game.WObjects.Map;

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

    //model instances
    public ArrayList<ModelInstance> rectanglepoints;

    public GenBot(Map map){
        //copy an instance of the map to calculate heights
        this.mapO = map;
        movingBall = false;
        solutionPath = new ArrayList<>();
        solutionIndex = 1;
        path = new ArrayList<>();

        rectanglepoints = new ArrayList<>();

        setRectanglepoint();
    }

    public void calculateGenetic(Vector2 ballPos, int millisec)throws Exception{

        //reset solution index
        solutionIndex = 1;

        ExecutorService executor = Executors.newCachedThreadPool();
        //call the new genetic thread for a certain amount of time
        Future<ArrayList<GeneDirection>> futureCall = executor.submit(new Genetic2D(mapO, ballPos, millisec));
        ArrayList<GeneDirection> result = futureCall.get(); // Here the thread will be blocked

        //shoot down the thread
        futureCall.cancel(true);
        executor.shutdownNow();

        //recreate reference points on the map
        int iter = (int) result.get(result.size()-1).getGetDirection().x;
        //remove the last one
        result.remove(result.size()-1);

        //add the first ball location
        float x = Helper.map(ballPos.x, -76,76, 32, 640);
        float y = Helper.map(ballPos.y, 52,-52, 0, 448);
        Vector2 pos = new Vector2(x,y);

        //add velocity to the previous position n times = iterations
        //pass to the next direction

        //convert the array
        path.clear();
        path.add(ballPos);
        for(GeneDirection gD: result){
            Vector2 vel = gD.getGetDirection().cpy().scl(5f);
            for(int i= 0; i<iter; i++){
                //add velocity
                pos.add(vel.cpy());
                //add friction
                vel.scl(1/1.7f);
            }

            Vector2 trspos = new Vector2();
            trspos.x = Helper.map(pos.x, 32,640, -76, 76);
            trspos.y = Helper.map(pos.y,  0, 448, 52,-52);

            path.add(trspos);
        }

        System.out.println(path.size());

        //calculate the solution vectors
        solutionPath.clear();
        for(int i=1; i<path.size(); i++){
            solutionPath.add(new MoveTo(path.get(i).cpy().sub(path.get(i-1).cpy()), 400));
            System.out.println(solutionPath.get(i-1).to.toString());
        }

        setRectanglepoint();

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
        ball.move(calculateForce(solutionPath.get(solutionIndex).to.cpy(),40));
        // decrease the number of iterations for that solutionIndex
        solutionPath.get(solutionIndex).iter--;

        Rectangle tmpRec = new Rectangle(0,0, 8f,8f);
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

    private void setRectanglepoint(){
        ModelBuilder modelBuilder = new ModelBuilder();

        Model model = modelBuilder.createBox(9, 8, 9, GL20.GL_LINES, new Material(ColorAttribute.createDiffuse(Color.YELLOW)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        for(int i =0; i < path.size(); i++){
            ModelInstance instance = new ModelInstance(model);
            instance.transform.translate(path.get(i).x,6 ,path.get(i).y);
            rectanglepoints.add(instance);
        }
    }
}
