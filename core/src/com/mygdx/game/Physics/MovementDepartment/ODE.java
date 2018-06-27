package com.mygdx.game.Physics.MovementDepartment;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.ForceDepartment.ForceManagement.ForceManager;
import com.mygdx.game.Physics.RigidBody;

/**
 * Class responsible for solving differential equations using the Strategy pattern
 */
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

    /**
     * Calculates the acceleration of a object at a given position and time
     * @param body - the required object
     * @param dt - the given time
     * @param tmpPosition - the given position
     * @return - the acceleration of the object
     */
    protected Vector3 calculateAcceleration(RigidBody body, float dt, Vector3 tmpPosition) {
//        System.out.println("[*] Calculating acceleration at new position: " + tmpPosition + " oldPos: " + body.getPosition());
        body.setTmpPosition(tmpPosition.cpy());

        // Launch the Force Manager to calculates the forces at the given position
        fm.manage(dt, false);

        body.setAcceleration(body.getTotalForce().cpy().scl(body.getInverseMass()));
        return body.getAcceleration();
    }

    protected void evaluate() {

    }

    public void setFm(ForceManager fm) {
        this.fm = fm;
    }

    /**
     * Clear all the variables
     */
    protected void clear() {
        position.setZero();
        velocity.setZero();
        acceleration.setZero();
        predictedAcceleration.setZero();
        predictedVelocity.setZero();
        predictedPosition.setZero();
    }
}
