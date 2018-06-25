package com.mygdx.game.Physics.MovementDepartment;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.ForceDepartment.ForceManagement.ForceManager;
import com.mygdx.game.Physics.RigidBody;

public abstract class ODE {

    public abstract void solve(MovementManager mm);

    protected Vector3 calculateAcceleration(ForceManager fm, RigidBody body, Vector3 position) {
        body.setPosition(position);
        fm.manage();
        return body.getActingForce().scl(body.getInverseMass());
    }

    protected Vector3 calculateAcceleration(RigidBody body) {
        return body.getTotalForce().cpy().scl(body.getInverseMass());
    }
}
