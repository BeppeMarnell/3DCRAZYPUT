package com.mygdx.game.Bott.GenBot;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.WObjects.Map;

import java.util.ArrayList;

public class Genetic3D {
    //set the end
    public final Vector2 end;

    //copy of the map
    Map map;

    //Array list of Balls
    public ArrayList<BallCrom> ballCroms;

    static final float mutationRate = 0.8f;
    static final int sonRate = 10;

    private boolean reached = false;

    public Genetic3D(Vector2 end, Map map){
        this.end = end;
        this.map = map;

        //create an initial population
        ballCroms = new ArrayList<>();
        create(200);
    }

    private void create(int amount) {
        for(int i=0; i<amount; i++) ballCroms.add(new BallCrom(map));
    }
}
