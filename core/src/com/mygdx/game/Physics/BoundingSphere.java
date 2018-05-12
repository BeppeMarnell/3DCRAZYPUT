package com.mygdx.game.Physics;

import com.badlogic.gdx.math.Vector3;

public class BoundingSphere extends Particle{
    protected Vector3 position;
    protected float radius;
    protected float mass;

    public BoundingSphere(Vector3 position, float mass, float radius) {
        super(position, mass);
        this.mass = mass;
        this.position = position;
        this.radius = radius;
    }

    public Vector3 getCenter() {
        return super.getPosition();
    }

    public float getRadius() {
        return radius;
    }
}
