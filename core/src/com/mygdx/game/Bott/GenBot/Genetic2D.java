package com.mygdx.game.Bott.GenBot;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Utils.Helper;
import com.mygdx.game.WObjects.Map;
import com.mygdx.game.WObjects.WorldObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Genetic2D {

    //set the end
    private Vector2 end;
    private ArrayList<CromWall> cromWalls;

    private ArrayList<BallCrom> ballCroms;
    private final float mutationRate = 0.8f;
    private final int sonRate = 10;

    private boolean reached = false;

    //take counts of generations
    int iter = 0;
    int fixedIter =1;
    int ballIter = 1;
    int gen = 0;

    public Genetic2D(Map map){
        //create an initial population
        ballCroms = new ArrayList<>();
        create(200);

        //create obstacles
        cromWalls = new ArrayList<>();
        createObstacles(map);
    }

    public void update(SpriteBatch batch){
        //setting backup
        if(iter> 2000)reached = true;

        if(!reached){

            boolean stopped = true;
            for (BallCrom b: ballCroms){
                //move all the ball
                b.update(cromWalls);
                if(!b.isStopped()) stopped = false;

                //see if fitness <10 then reached = true
                if(b.getFitness() <20) reached = true;
            }

            //only if all the balls have stopped then assign a new velocity
            if(stopped) for (BallCrom b: ballCroms)b.nextVel();

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
                b.nextVel();
            }

            System.out.println("Generation: "+gen+" ,iterations :" + iter + " ,medium iter per ball: "+ ballIter);

            //set the fixed iterations
            fixedIter = iter;

            //reset the iterations and agg a generations
            iter = 0;
            gen++;

            reached = false;
        }
    }

    //Method to actuate the genetic algorithm
    public void draw(){

        //SELECTION

        //sort the list by fitness
        Collections.sort(ballCroms, new Comparator<BallCrom>() {
            @Override
            public int compare(BallCrom o1, BallCrom o2) {
                if (o1.getFitness()> o2.getFitness())
                    return 1;
                if (o1.getFitness()< o2.getFitness())
                    return -1;

                return 0;
            }
        });


        for(int i=1; i<sonRate+1; i++){
            //REMOVE THE WORSE CHROMOSOME
            ballCroms.remove(ballCroms.size()-i);

            //REPRODUCTION
            //chose the parents and reproduce
            BallCrom child = new BallCrom(end);
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
     * create the amount of ballCroms
     * @param amount
     */
    private void create(int amount) {
        for(int i=0; i<amount; i++) ballCroms.add(new BallCrom(end));
    }

    public void dispose(){
        for (BallCrom b: ballCroms)b.dispose();
    }

    private void createObstacles(Map map){
        //create the obstacle that are needed for the genetic and store them
        for(int i=0; i<map.mapObjects.length; i++) {
            for (int j = 0; j < map.mapObjects[0].length; j++) {
                if(map.mapObjects[i][j].getType() == WorldObject.ObjectType.Wall || map.mapObjects[i][j].getType() == WorldObject.ObjectType.Tree){
                    //create a wall in a specified position

                    float x = Helper.map(i, 0,19, 0, 640);
                    float y = Helper.map(j, 0,13, 0, 448);
                    cromWalls.add(new CromWall(x +16,y +16));
                }
            }
        }

        //set the end point
        end = new Vector2(Helper.map(map.getHolePosTranslV2().x, -80,80, 0, 640),
                Helper.map(map.getHolePosTranslV2().y, -56,56, 0, 448));
    }
}
