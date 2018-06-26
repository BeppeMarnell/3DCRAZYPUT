package com.mygdx.game.Physics.MovementDepartment;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.ForceDepartment.ForceManagement.ForceManager;
import com.mygdx.game.Physics.RigidBody;

public abstract class ODE {
    protected final static float DT = 10;
    private ForceManager fm;
    protected Vector3 position = new Vector3();
    protected Vector3 velocity = new Vector3();
    protected Vector3 acceleration = new Vector3();
    protected Vector3 predictedPosition = new Vector3();
    protected Vector3 predictedVelocity = new Vector3();
    protected Vector3 predictedAcceleration = new Vector3();

    public abstract void solve(MovementManager mm);

    protected Vector3 calculateAcceleration(RigidBody body, float dt, Vector3 newPosition) {
        System.out.println("[*] Calculating acceleration at new position: " + newPosition + " oldPos: " + body.getPosition());
        body.setTmpPosition(newPosition.cpy());
        fm.manage(dt);
        body.getTmpPosition().setZero();
        body.setAcceleration(body.getTotalForce().cpy().scl(body.getInverseMass()));
        return body.getAcceleration();
    }

    public void setFm(ForceManager fm) {
        this.fm = fm;
    }

    protected void clear() {
        position.setZero();
        velocity.setZero();
        acceleration.setZero();
        predictedAcceleration.setZero();
        predictedVelocity.setZero();
        predictedPosition.setZero();
    }
}
