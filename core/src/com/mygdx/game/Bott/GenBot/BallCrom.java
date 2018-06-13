package com.mygdx.game.Bott.GenBot;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.BoundingSphere;
import com.mygdx.game.WObjects.Map;

import java.util.ArrayList;

public class BallCrom extends BoundingSphere {

    //get a copy of the map
    private Map map;

    //radius
    public static final float RAD = 1f;
    public static final float MASS = 2f;

    //every index is an iteration's direction
    private ArrayList<GeneDirection> chromosome;
    private float fitness;


    private int iterations;

    /**
     * Initialize the ball 3d and add the position to it
     * @param map
     */
    public BallCrom(Map map){
        super(new Vector3(map.getInitBallPosV2().x, map.getHeight(map.getInitBallPosV2(), RAD), map.getInitBallPosV2().y), MASS, RAD);

        //initialize stuff
        this.map = map;
        iterations = 0;

        //initialize with the first direction into the chromosome
        chromosome = new ArrayList<>();
        chromosome.add(new GeneDirection());

        //first fitness
        fitness = position.dst(map.getInitHolePosV3());
    }

    /**
     * Update the position of the ball
     */
    public void update(float deltaTime){
        //move the ball with keys
        if (state == BodyState.Moving) {

            Vector3 oldpos = position.cpy();
            integrate(deltaTime);
            move3DBall(oldpos);
        }

        if (Math.abs(velocity.x) < 0.05 && Math.abs(velocity.z) < 0.05) {
            clearForces();
            state = BodyState.Stopped;
        }
    }

    /**
     * This method moves the position of the 3D instance, do not change it
     */
    private void move3DBall(Vector3 oldPos){
        //Apply the physic to the 3D object
        float err = 0.005f;

        position.y = map.getHeight(new Vector2(position.x, position.z), RAD);
        mu = map.getFriction(new Vector2(position.x, position.y));

        if (oldPos.y - position.y > err) {
            movement = Direction.Down;
        } else if (position.y - oldPos.y > err) {
            movement = Direction.Up;
        } else {
            movement = Direction.Straight;
        }
    }

    /**
     * Move the ball assigning a force
     */
    public void move(Vector2 force) {
        state = BodyState.Moving;
        addForce(new Vector3(force.x, 0, force.y));
    }

    /**
     * Call the method to know if the ball is stopped
     * @return boolean value
     */
    public boolean isStopped(){
        if(state == BodyState.Stopped)return true;
        else return false;
    }

    public void resetBall(){
        //reset position to zero
        position.set(map.getInitBallPosV3());

        //reset iterations
        iterations = 0;

        //set fitness to zero
        fitness = 0;
    }
}
