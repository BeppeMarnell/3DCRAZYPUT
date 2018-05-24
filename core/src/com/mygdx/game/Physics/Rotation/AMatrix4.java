package com.mygdx.game.Physics.Rotation;

import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class AMatrix4 extends Matrix4 {

    public AMatrix4(float[] val) {
        super(val);
    }

    public AMatrix4() {
        super();
    }

    public Vector3 transform(Vector3 v) {
        return new Vector3(val[0] * v.x + val[1] * v.y + val[2] * v.z + val[3],
                           val[4] * v.x + val[5] * v.y + val[6] * v.z + val[7],
                           val[8] * v.x + val[9] * v.y + val[10] * v.z + val[11]);
    }

    public Vector3 transformDirection(Vector3 v) {
        return new Vector3(val[0] * v.x + val[1] * v.y + val[2] * v.z,
                val[4] * v.x + val[5] * v.y + val[6] * v.z,
                val[8] * v.x + val[9] * v.y + val[10] * v.z);
    }

    public Vector3 getWorldCoordinates(Vector3 localCoordinates, boolean direction) {
        if (!direction) return transform(localCoordinates);
        else return transformDirection(localCoordinates);
    }

    public Vector3 getLocalCoordinates(Vector3 worldCoordinates, boolean direction) {
        AMatrix4 m = (AMatrix4) this.cpy().inv();
        if (!direction) return m.transform(worldCoordinates);
        else return m.transformDirection(worldCoordinates);
    }
}
