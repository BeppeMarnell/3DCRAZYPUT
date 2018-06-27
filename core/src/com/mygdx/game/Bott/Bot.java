package com.mygdx.game.Bott;

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
import com.mygdx.game.Utils.Helper;
import com.mygdx.game.WObjects.Ball;
import com.mygdx.game.WObjects.Map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Bot {

    private Map mapO;

    public boolean movingBall;

    //calculated path that has to be applied to the ball
    private ArrayList<MoveTo> solutionPath;
    private int solutionIndex;

    //calculate path from the algorithm
    private ArrayList<Vector2> path;

    //model instances
    public ArrayList<ModelInstance> rectanglepoints;

    public Bot(Map map){
        //copy an instance of the map to calculate heights
        this.mapO = map;
        movingBall = false;
        solutionPath = new ArrayList<>();
        solutionIndex = 1;
        path = new ArrayList<>();

        rectanglepoints = new ArrayList<>();

        setRectanglepoint();
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
        float x = Helper.map(algorithmMap.getStart().x, 0, 19,-76, 76);
        float y = Helper.map(algorithmMap.getStart().y, 0, 13,-52, 52);
        path.add(new Vector2(x, y));

        for(Coordinate c: finalPath){
            //translate array coordinates to world coordinates
            x = Helper.map(c.x, 0, 19,-76, 76);
            y = Helper.map(c.y, 0, 13,-52, 52);
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

        setRectanglepoint();
    }

    public void CalculateBeadthFirst(int[][] map){
        //reset to initial state of the path
        solutionPath.clear();
        solutionIndex = 1;

        //set up AStar algorithm
        AlgorithmMap algorithmMap = new AlgorithmMap(map);
        BreadthFirstSearch breadthFirstAlgorithm = new BreadthFirstSearch();


        //get the list of coordinates
        List<Coordinate> finalPath = breadthFirstAlgorithm.BreadFirstSearchSolve(algorithmMap);
        Collections.reverse(finalPath);

        algorithmMap.printPath(finalPath); //uncomment to see the printed
        algorithmMap.reset();


        //add the start
        float x = Helper.map(algorithmMap.getStart().x, 0, 19,-76, 76);
        float y = Helper.map(algorithmMap.getStart().y, 0, 13,-52, 52);
        path.add(new Vector2(x, y));

        for(Coordinate c: finalPath){
            //translate array coordinates to world coordinates
            x = Helper.map(c.x, 0, 19,-76, 76);
            y = Helper.map(c.y, 0, 13,-52, 52);
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

            System.out.println(solutionPath.get(i-1).to.toString());
        }

        //for(Vector2 c: path)System.out.println(c.toString());

        //set true to start moving the ball to the hole
        movingBall = true;


        setRectanglepoint();
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
            ball.setMovedByBot(false);
            return;
        }
//        ball.setMovedByBot(true);

        //move the ball in the direction
//        ball.move(calculateForce(solutionPath.get(solutionIndex).to.cpy(),3));
        if (ball.isStopped()) {
            ball.move(calculateForce(solutionPath.get(solutionIndex).to.cpy(),1));
        }
        // decrease the number of iterations for that solutionIndex
        solutionPath.get(solutionIndex).iter--;

//        Rectangle tmpRec = new Rectangle(0,0, 9f,9f);
        Rectangle tmpRec = new Rectangle(0,0, 20,20);
        tmpRec.setCenter(path.get(solutionIndex+1).x, path.get(solutionIndex+1).y);

        //if the ball has arrived near the determined path point, then pass to the next solutionindex
        if(tmpRec.contains(new Vector2(ball.getPosition().x, ball.getPosition().z))) {
            solutionIndex++;
            System.out.println("==========================CONTAINS BALL");
        }
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
