package com.mygdx.game.Bott.GenBot;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class BallCrom {
    /** 1st PHASE
     * 1 option arraylist of (Vector2, timesteps) with a equal speed for each frame //kinda real time thing
     * - give random direction every step
     * - select the best fitness and add that one in the population (fitness = distance between the player and final position
     * - give random directions again
     */

    /** 2nd PHASE
     * - each step apply a counter force (friction) to the velocity
     * - when the ball is stopped a random direction is applied
     * - select the best fitness and add that one in the population (fitness = distance between the player and final position
     * - the chromosome is the aggregation of directions after the ball stops
     * - each ball stores its chromosomes to use it in the next generation
     * - each generation is called when all the ball are stopped
     * - adding a child means to remove from the population the lowest fitness
     */

    /**
     * - give random directions and random velocities
     */

    private Circle circle;

    private Vector2 pos;
    private Vector2 vel;

    private Vector2 end;
    private Vector2 start;

    static float MAX_VELOCITY = 4f;
    static float FRICTION = 1.7f;

    //every index is an iteration's direction
    private ArrayList<GeneDirection> chromosome;
    private float fitness;

    private int iterations;

    private boolean collides;

    public BallCrom(Vector2 start, Vector2 end){
        //circe
        circle= new Circle(0,0,4f);

        //init pos
        this.start = start;
        pos = new Vector2(start.cpy());

        //fitness and chromosome
        fitness = 0;
        chromosome = new ArrayList<>();

        //initialize with the first direction into the chromosome
        chromosome.add(new GeneDirection());

        //initialise the end of the path
        this.end = end.cpy();

        //first fitness
        fitness = pos.dst(end.cpy());

        iterations = 0;

        //velocity
        vel = new Vector2(chromosome.get(0).getGetDirection().cpy().scl(MAX_VELOCITY));
        // take the first vector iteration multiplied a random float between 0 and 10

        collides = false;
    }

    public void update(ArrayList<CromWall> cromWalls){


        //update the circle position
        circle.x = pos.x; circle.y = pos.y;

        //check for collision
        collides = false;
        for(CromWall w: cromWalls) if(w.isColliding(circle))collides = true;

        //update only if the ball is not colliding
        if(!collides){
            //add the velocity to the position
            pos.add(vel.cpy());

            //after moving calculate the fitness
            fitness = pos.dst(end.cpy());

            //at the end apply a force that decrease the velocity until 0
            if(Math.abs(vel.x) < 0.5f && Math.abs(vel.y) < 0.5f) vel.setZero();
            else vel.scl(1/FRICTION); // else apply friction

        }else{
            vel.setZero();
        }

        //check if the ball is out of bounds
        if(pos.x< 0 || pos.x> 640 || pos.y <0 || pos.y> 480)collides = true;
    }

    public void nextVel(){

        //retrieve the direction only if the chromosome size is enough long as the iterations
        if(iterations +1 < chromosome.size()){
            //set to zero and add the direction
            vel.setZero();

            //retrieve the direction to the right iteration
            vel.add(chromosome.get(iterations).getGetDirection().cpy().scl(MAX_VELOCITY));
        }else{
            //add the new direction to the chromosome
            //in this case the chromosome is not enough big

            GeneDirection newDir = new GeneDirection();

            //adjust the random direction to a smooth increment referred to the previous one
            newDir.adjGenDirections(chromosome.get(iterations).getGetDirection());

            //adjust the direction towards the hole (optionally)
            //newDir.adjGenDirections(end.scl(-1f));

            //add the direction to the chromosome
            chromosome.add(newDir);

            //set to zero and add the direction
            vel.setZero();
            vel.add(chromosome.get(iterations+1).getGetDirection().scl(MAX_VELOCITY));
        }

        //increase iterations
        iterations++;
    }

    public void resetBall(){
        //reset position to zero
        pos.set(start);

        //reset iterations
        iterations = 0;

        //set fitness to zero
        fitness = 0;
    }

    public boolean isStopped(){
        return vel.isZero();
    }

    public float getFitness() {
        return fitness;
    }

    public ArrayList<GeneDirection> getChromosome() {
        return chromosome;
    }

    public void setChromosome(ArrayList<GeneDirection> chromosome) {
        //this method is mainly applied for child reproduction

        this.chromosome.clear();
        for(GeneDirection gC: chromosome){
            //copy chromosomes
            this.chromosome.add(new GeneDirection().setGenDirections(gC));
        }
    }

    public int getIterations() {
        return iterations;
    }
}