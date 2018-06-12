package com.mygdx.game.Bott.GenBot;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

class GeneDirection{
    private Vector2 dir;
    private float force;

    public GeneDirection(){
        this.dir = new Vector2().setToRandomDirection(); // normalized
        force = MathUtils.random()*5;
    }

    public GeneDirection(Vector2 dir){
        this.dir = dir.cpy().nor(); // normalized
        force = MathUtils.random()*5;
    }

    public GeneDirection setGenDirections(GeneDirection gen){
        this.force = gen.force;
        this.dir = gen.dir.cpy();

        return this;
    }

    public GeneDirection adjGenDirections(GeneDirection gen){
        this.dir = dir.sub(gen.dir.cpy()).nor();
        return this;
    }

    public Vector2 getGetDirection(){
        return dir.cpy().scl(force);
    }

    public void setRandom(){
        this.dir.setToRandomDirection();
    }
}

