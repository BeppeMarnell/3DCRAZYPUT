package com.mygdx.game.Physics;

import com.badlogic.gdx.math.Vector2;

public class CollisionData {
    private boolean collides;
    private float penetration;
    private Vector2 normal;

    public CollisionData(boolean collides, float penetration) {
        this.collides = collides;
        this.penetration = penetration;
    }

    public CollisionData(boolean collides, float penetration, Vector2 normal) {
        this.collides = collides;
        this.penetration = penetration;
        this.normal = normal;
    }

    public Vector2 getNormal() {
        return normal;
    }

    public float getPenetration() {
        return penetration;
    }
}
