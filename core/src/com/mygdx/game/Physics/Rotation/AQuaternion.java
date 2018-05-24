package com.mygdx.game.Physics.Rotation;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class AQuaternion extends Quaternion {

    public AQuaternion(float x, float y, float z, float w) {
        super(x, y, z, w);
    }

    public AQuaternion() {
        super();
    }

    public void updateByScaledVector(Vector3 v, float k) {
        Vector3 scaledVector = v.scl(k);
        Quaternion q = new Quaternion(0, scaledVector.x, scaledVector.y, scaledVector.z);
        mul(q);
        add(q.mul(0.5f));
    }

    public void rotateByVector(Vector3 v) {
        mul(0, v.x, v.y, v.z);
    }
}
