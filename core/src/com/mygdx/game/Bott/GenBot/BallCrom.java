package com.mygdx.game.Bott.GenBot;

import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.BoundingSphere;
import com.mygdx.game.WObjects.Map;

import java.util.ArrayList;

public class BallCrom extends BoundingSphere {

    private ModelInstance ballInstance;

    //get a copy of the map
    private Map map;

    //radius
    public static final float RAD = 1f;
    public static final float MASS = 2f;

    private float MAXVEL = 5000f;

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

        //create the ball object

        ballInstance = new ModelInstance(FlyWeightAsset.model);

        ballInstance.transform.translate(map.getInitBallPosV2().x, map.getHeight(map.getInitBallPosV2(), RAD), map.getInitBallPosV2().y);

        //initialize stuff
        this.map = map;
        iterations = 0;

        //initialize with the first direction into the chromosome
        chromosome = new ArrayList<>();
        chromosome.add(new GeneDirection());

        //first fitness
        this.fitness = position.dst(map.getInitHolePosV3());

        //move the ball
        nextDir();
    }

    /**
     * Update the position of the ball
     */
    public void update(float deltaTime){
        //move the ball with keys
        if (state == BodyState.Moving) {
            move3DBall();
            integrate(deltaTime);
        }

        if (Math.abs(velocity.x) < 0.05 && Math.abs(velocity.z) < 0.05) {
            clearForces();
            state = BodyState.Stopped;
        }

        //after moving calculate the fitness
        fitness = position.dst(map.getInitHolePosV3());

        //in case the ball is out the it returns to the origin
        if(outOfBox()) position = map.getInitBallPosV3();
    }

    /**
     * This method moves the position of the 3D instance, do not change it
     */
    private void move3DBall(){
        //Apply the physic to the 3D object
        float err = 0.005f;

        Vector3 oldPos = ballInstance.transform.getTranslation(new Vector3());
        position.y = map.getHeight(new Vector2(position.x, position.z), RAD);
        mu = map.getFriction(new Vector2(position.x, position.y));

        if (oldPos.y - position.y > err) {
            movement = Direction.Down;
        } else if (position.y - oldPos.y > err) {
            movement = Direction.Up;
        } else {
            movement = Direction.Straight;
        }

        //in order to move the ball i've to apply the translation amount
        ballInstance.transform.setTranslation(position);
        ballInstance.calculateTransforms();
    }

    /**
     * Move the ball assigning a force
     */
    public void move(Vector2 force) {
        state = BodyState.Moving;
        addForce(new Vector3(force.x, 0, force.y));
    }


    public void nextDir(){
        //retrieve the direction only if the chromosome size is enough long as the iterations
        if(iterations +1 < chromosome.size()){

            //retrieve the direction to the right iteration
            move(chromosome.get(iterations).getGetDirection().scl(MAXVEL));
        }else{
            //add the new direction to the chromosome
            //in this case the chromosome is not enough big
            //and adjust the random direction
            chromosome.add(new GeneDirection().adjGenDirections(chromosome.get(iterations)));

            move(chromosome.get(iterations+1).getGetDirection().scl(MAXVEL));
        }

        //increase iterations
        iterations++;
    }

    /**
     * render the 3D ball with a specific environment
     * @param batch
     * @param environment
     */
    public void render(ModelBatch batch, Environment environment){
        //render the ball
        batch.render(ballInstance, environment);
    }

    /**
     * Call the method to know if the ball is stopped
     * @return boolean value
     */
    public boolean isStopped(){
        if(state == BodyState.Stopped)return true;
        else return false;
    }

    public void setChromosome(ArrayList<GeneDirection> chromosome) {
        //this method is mainly applied for child reproduction

        this.chromosome.clear();
        for(GeneDirection gC: chromosome){
            //copy chromosomes
            this.chromosome.add(new GeneDirection().setGenDirections(gC));
        }
    }

    public float getFitness() {
        if(fitness == Float.NaN){
            position.set(map.getInitBallPosV3());
            fitness = position.dst(map.getInitHolePosV3());

        }
        return fitness;
    }

    public ArrayList<GeneDirection> getChromosome() {
        return chromosome;
    }

    public int getIterations() {
        return iterations;
    }

    public void resetBall(){
        //reset position to zero
        position.set(map.getInitBallPosV3());

        //reset iterations
        iterations = 0;

        //set fitness to zero
        fitness = 0;
    }

    public boolean outOfBox(){
        if((position.x > -72 && position.x < 72)&&(position.z > -48 && position.y < 48)) return false;
        else return true;
    }
}
