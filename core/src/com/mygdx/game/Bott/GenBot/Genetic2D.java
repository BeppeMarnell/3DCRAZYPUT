package com.mygdx.game.Bott.GenBot;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Utils.Helper;
import com.mygdx.game.WObjects.Map;
import com.mygdx.game.WObjects.WorldObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.Callable;

public class Genetic2D implements Callable<ArrayList<GeneDirection>> {

    //set the end
    private Vector2 end;
    private Vector2 start;

    private ArrayList<CromWall> cromWalls;

    private ArrayList<BallCrom> ballCroms;

    private ArrayList<GeneDirection> bestChrom;

    private final float mutationRate = 0.8f;
    private final int sonRate = 80;

    private boolean reached = false;

    //take counts of generations
    int iter = 0;
    int fixedIter =1;
    int ballIter = 1;
    int gen = 0;

    int millisec;

    @Override
    public ArrayList<GeneDirection> call() {

        long oldT = new java.util.Date().getTime();
        long diff = new java.util.Date().getTime() - oldT;

        while(diff < millisec){

            //run the genetic algorithm
            update();

            //calculate the time passed
            diff = new java.util.Date().getTime() - oldT;
        }

        return bestChrom;
    }

    public Genetic2D(Map map,  Vector2 ballPos, int millisec){
        //create an initial population
        ballCroms = new ArrayList<>();
        start = getBallposTranl(ballPos);
        end = getBallposTranl(map.getHolePosTranslV2());

        create(400);

        //create obstacles
        cromWalls = new ArrayList<>();
        bestChrom = new ArrayList<>();
        createObstacles(map);

        //set the millisecond time
        this.millisec = millisec;
    }

    public void update(){
        //setting backup
        if(iter> 1000)reached = true;

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

            //get the best chromosome
            bestChrom.clear();
            for(int i=0; i<ballIter ; i++){
                bestChrom.add(ballCroms.get(0).getChromosome().get(i));
            }
            //add number of iter
            bestChrom.add(new GeneDirection(new Vector2(iter, iter)));

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
            BallCrom child = new BallCrom(start ,end);
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
        for(int i=0; i<amount; i++) ballCroms.add(new BallCrom(start ,end));
    }

    private void createObstacles(Map map){
        //create the obstacle that are needed for the genetic and store them
        for(int i=0; i<map.mapObjects.length; i++) {
            for (int j = 0; j < map.mapObjects[0].length; j++) {
                if(map.mapObjects[i][j].getType() == WorldObject.ObjectType.Wall || map.mapObjects[i][j].getType() == WorldObject.ObjectType.Tree){
                    //create a wall in a specified position

                    float x = Helper.map(i, 0,19, 0, 640);
                    float y = Helper.map(j, 13,0, 0, 448);
                    cromWalls.add(new CromWall(x,y));
                }
            }
        }

        //set the end point
        end = new Vector2(Helper.map(map.getHolePosTranslV2().x, -76,76, 0, 640),
                Helper.map(map.getHolePosTranslV2().y, 52,-52, 0, 448));
    }

    private Vector2 getBallposTranl(Vector2 Ballpos){
        float x = Helper.map(Ballpos.x, -76,76, 32, 640);
        float y = Helper.map(Ballpos.y, 52,-52, 0, 448);

        return new Vector2(x,y);
    }
}
