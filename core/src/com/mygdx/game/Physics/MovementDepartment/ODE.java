package com.mygdx.game.Physics.MovementDepartment;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.ForceDepartment.ForceManagement.ForceManager;
import com.mygdx.game.Physics.RigidBody;

public abstract class ODE {
    protected final static float DT = 10;
    private ForceManager fm;
    protected Vector3 position;
    protected Vector3 velocity;
    protected Vector3 acceleration;
    protected Vector3 predictedPosition;
    protected Vector3 predictedVelocity;
    protected Vector3 predictedAcceleration;

    public abstract void solve(MovementManager mm);

    protected Vector3 calculateAcceleration(RigidBody body, float dt, Vector3 newPosition) {
        body.setTmpPosition(newPosition.cpy());
        fm.manage(dt);
        body.getTmpPosition().setZero();
        return body.getTotalForce().cpy().scl(body.getInverseMass());
    }

    public void setFm(ForceManager fm) {
        this.fm = fm;
    }
}
