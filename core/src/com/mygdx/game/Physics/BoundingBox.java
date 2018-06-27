package com.mygdx.game.Physics;

import com.badlogic.gdx.math.Vector3;

/**
 * Bounding box used in collision detection, contains basic information
 */
public class BoundingBox {
    protected Vector3 position;
    protected Vector3 size;
    protected Vector3 max;
    protected Vector3 min;
    protected Vector3 halfSize;
    protected float mass;
    protected float inverseMass;

    public BoundingBox(Vector3 position, Vector3 size, float mass) {
        this.position = position;
        this.size = size;
        this.mass = mass;
        max = new Vector3(position.x + size.x * 0.5f, position.y + size.y * 0.5f, position.z + size.z * 0.5f);
        min = new Vector3(position.x - size.x * 0.5f, position.y - size.y * 0.5f, position.z - size.z * 0.5f);
        halfSize = new Vector3((max.x - min.x) * 0.5f, (max.y - min.y) * 0.5f, (max.z - min.z) * 0.5f);
        inverseMass = 1 / mass;
    }

    public float getMass() {
        return mass;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Vector3 getHalfSize() {
        return halfSize;
    }

    public Vector3 getMax() {
        return max;
    }

    public Vector3 getMin() {
        return min;
    }

    public Vector3 getSize() {
        return size;
    }

    public float getInverseMass() {
        return inverseMass;
    }
}
