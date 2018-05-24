package com.mygdx.game.Physics.Rotation;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class AMatrix3 extends Matrix3 {

    public AMatrix3(float[] val) {
        super(val);
    }

    public AMatrix3() {
        super();
    }

    public void setOrientation(Quaternion q) {
        val[0] = 1 - (2 * q.z * q.z + 2 * q.w * q.w);
        val[1] = 2 * q.y * q.z + 2 * q.x * q.w;
        val[2] = 2 * q.y * q.w - 2 * q.x * q.z;
        val[3] = 2 * q.y * q.z - 2 * q.w * q.x;
        val[4] = 1 - (2 * q.y * q.y + 2 * q.w * q.w);
        val[5] = 2 * q.z * q.w + 2 * q.x * q.y;
        val[6] = 2 * q.y * q.w + 2 * q.z * q.x;
        val[7] = 2 * q.z * q.w - 2 * q.x * q.y;
        val[8] = 1 - (2 * q.y * q.y + 2 * q.z * q.z);
    }

    public Vector3 transform(Vector3 v) {
        return new Vector3(val[0] * v.x + val[1] * v.y + val[2] * v.z,
                           val[3] * v.x + val[4] * v.y + val[5] * v.z,
                           val[6] * v.x + val[7] * v.y + val[8] * v.z);
    }
}
