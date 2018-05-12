package com.mygdx.game.Physics;

import com.badlogic.gdx.math.Vector3;

public class BoundingSphere extends Particle{
    protected float radius;

    public BoundingSphere(Vector3 position, float mass, float radius) {
        super(position, mass);
        this.radius = radius;
    }

    public float getRadius() {
        return radius;
    }
}
