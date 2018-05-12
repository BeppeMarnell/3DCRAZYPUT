package com.mygdx.game.WObjects;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.BoundingBox;

public class Obstacle extends BoundingBox{
    protected float elasticity;
    protected Vector3 velocity;

    public Obstacle(Vector3 position, Vector3 size, float mass, float elasticity, Vector3 velocity) {
        super(position, size, mass);
        this.elasticity = elasticity;
        this.velocity = velocity;
    }

    public float getElasticity() {
        return elasticity;
    }

    public Vector3 getVelocity() {
        return velocity;
    }
}
