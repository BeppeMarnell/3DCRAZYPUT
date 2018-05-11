package com.mygdx.game.Physics;

import com.badlogic.gdx.math.Vector3;

public interface BoundingBox {
    Vector3 getPosition();
    Vector3 getMax();
    Vector3 getMin();
    Vector3 getHalfSize();
}
