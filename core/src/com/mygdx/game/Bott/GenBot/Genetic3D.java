package com.mygdx.game.Bott.GenBot;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Physics.CollisionDetector;
import com.mygdx.game.WObjects.Map;
import com.mygdx.game.WObjects.Wall;
import org.lwjgl.Sys;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Genetic3D {

    //copy of the map
    Map map;

    //Array list of Balls
    public ArrayList<BallCrom> ballCroms;

    static final float mutationRate = 0.8f;
    static final int sonRate = 10;

    private boolean reached = false;

    //take counts of generations
    int iter = 0;
    int fixedIter =1;
    int ballIter = 1;
    int gen = 0;

    //temporary
    ArrayList<CollisionDetector> detectors;

    /**
     * Constructor of the genetic algorithm class
     * @param map copy instance of the map
     */
    public Genetic3D(Map map){

        //load flyweight assets
        FlyWeightAsset.load();

        this.map = map;

        //create an initial population
        ballCroms = new ArrayList<>();
        //put all the ball in the physic engine
        detectors = new ArrayList<>();
        create(100);
    }

    public void update(ArrayList<Wall> walls, ModelBatch batch, Environment env){

        //update collision
        for(CollisionDetector cD: detectors){
            for (Wall w : walls) {
                cD.collidesWithWall(w, Gdx.graphics.getDeltaTime());
            }
        }

        //setting a backup
        if(Gdx.input.isKeyJustPressed(Input.Keys.F))reached = true;
        if(iter> 100000)reached = true;

        if(!reached){

            boolean stopped = true;
            for (BallCrom b: ballCroms){
                //move all the ball
                b.update(Gdx.graphics.getDeltaTime());
                if(!b.isStopped()) stopped = false;

                //see if fitness <10 then reached = true
                if(b.getFitness() <10) reached = true;
            }

            //only if all the balls have stopped then assign a new velocity
            if(stopped) for (BallCrom b: ballCroms)b.nextDir();

            iter++;

        }else{
            //in case it's reached start with the next generation
            //reproduce
            draw();

            //get ball iterations
            ballIter = ballCroms.get(0).getIterations();

            //set position of the population to 0 and iteration
            for (BallCrom b: ballCroms){
                //reset values of the ball
                b.resetBall();
                //assign the velocity to begin with in the next generation
                b.nextDir();
            }

            System.out.println("Generation: "+gen+" ,iterations :" + iter + " ,medium iter per ball: "+ ballIter);

            //set the fixed iterations
            fixedIter = iter;

            //reset the iterations and agg a generations
            iter = 0;
            gen++;

            reached = false;

            //after the reproduction is done, attach the collision detection
            for(int i=0; i<ballCroms.size(); i++) detectors.set(i, new CollisionDetector(ballCroms.get(i)));
        }

        for (BallCrom b: ballCroms) b.render(batch, env);
    }

    //Method to actuate the genetic algorithm
    public void draw(){

        //SELECTION

        //sort the list by fitness
        Collections.sort(ballCroms, (o1, o2) -> {

            if (o1.getFitness()> o2.getFitness())
                return 1;
            if (o1.getFitness()< o2.getFitness())
                return -1;

            return 0;
        });


        for(int i=1; i<sonRate+1; i++){
            //REMOVE THE WORSE CHROMOSOME
            ballCroms.remove(ballCroms.size()-i);

            //REPRODUCTION
            //chose the parents and reproduce
            BallCrom child = new BallCrom(map);
            child.setChromosome(reproduce(i));

            //add the child to the array
            ballCroms.add(child);
        }
    }

    private ArrayList<GeneDirection> reproduce(int n){
        ArrayList<GeneDirection> newCrom = new ArrayList<>();

        //get the size of the chromosome
        int size = ballCroms.get(0).getChromosome().size();

        //crossover
        for (int i = 0; i < size; i++) {
            if(i<size/2) newCrom.add(new GeneDirection(new Vector2(ballCroms.get(n-1).getChromosome().get(i).getGetDirection().nor())));
            else newCrom.add(new GeneDirection(new Vector2(ballCroms.get(n).getChromosome().get(i).getGetDirection().nor())));
        }

        //mutation
        for (GeneDirection vec: newCrom)
            if(MathUtils.random()> mutationRate)
                vec.setRandom();

        return newCrom;
    }

    /**
     * create a n amount of balls
     * @param amount
     */
    private void create(int amount) {
        for(int i=0; i<amount; i++) ballCroms.add(new BallCrom(map));
        for(int i=0; i<amount; i++) detectors.add(new CollisionDetector(ballCroms.get(i)));
    }
}
