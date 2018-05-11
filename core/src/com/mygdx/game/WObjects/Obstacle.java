package com.mygdx.game.WObjects;

import com.badlogic.gdx.math.Vector3;

public class Obstacle {
    public final static float ELASTICITY = 0.8f;
    public final static Vector3 VELOCITY = new Vector3(0, 0, 0);
    protected Vector3 position;
    protected float mass;
    protected float[] size;
    private float inverseMass;



    public Obstacle(float mass, Vector3 position) {
        this.position = position;
        this.mass = mass;
        inverseMass = 1 / mass;
    }

    public Obstacle(float mass, Vector3 position, float[] size) {
        this(mass, position);
        this.size = size;
    }

    public float getMass() {
        return mass;
    }

    public Vector3 getPosition() {
        return position;
    }

    public float[] getSize() {
        return size;
    }

    public float getInverseMass() {
        return inverseMass;
    }


}
