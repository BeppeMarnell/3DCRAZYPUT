package com.mygdx.game.Physics.MovementDepartment.IntegratorCollection;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.MovementDepartment.MovementManager;
import com.mygdx.game.Physics.MovementDepartment.ODE;

/**
 * Runge-Kutta 4 Method
 */
public class RungeKutta4 extends ODE {
    // acceleration coefficients
    private Vector3 k1, k2, k3, k4, k;

    // velocity coefficients
    private Vector3 v1, v2, v3, v4, v;

    /**
     * Apply Runge-Kutta 4
     * @param mm
     */
    @Override
    public void solve(MovementManager mm) {
        position = mm.getBody().getPosition().cpy();

        // Calculate acceleration and velocity at the initial position
        k1 = calculateAcceleration(mm.getBody(), DT, position);
        v1 = mm.getBody().getVelocity().cpy().add(k1.cpy().scl(0));
//        System.out.println("k1: " + k1 + " v1: " + v1);

        // Calculate acceleration and velocity at the next position based on the slope of the previous values
        // Done at half step
        k2 = calculateAcceleration(mm.getBody(), DT, position.cpy().add(v1.cpy().scl(mm.getDt()/2)));
        v2 = mm.getBody().getVelocity().cpy().add(k2.cpy().scl(mm.getDt()/2));
//        System.out.println("k2: " + k2 + " v2: " + v2);

        // Calculate acceleration and velocity at the next position based on the slope of the previous values
        // Done at half step
        k3 = calculateAcceleration(mm.getBody(), DT, position.cpy().add(v2.cpy().scl(mm.getDt()/2)));
        v3 = mm.getBody().getVelocity().cpy().add(k3.cpy().scl(mm.getDt()/2));
//        System.out.println("k3: " + k3 + " v3: " + v3);

        // Calculate acceleration and velocity at the last predicted position at full step
        k4 = calculateAcceleration(mm.getBody(), DT, position.cpy().add(v3.cpy().scl(mm.getDt())));
        v4 = mm.getBody().getVelocity().cpy().add(k4.cpy().scl(mm.getDt()));
//        System.out.println("k4: " + k4 + " v4: " + v4);

        // Apply the Runge-Kutta equation to acceleration and velocity
        k = k1.cpy();
        k.add(k2.cpy().scl(2));
        k.add(k3.cpy().scl(2));
        k.add(k4);
        k.scl(0.16666f);

        v = v1.cpy();
        v.add(v2.cpy().scl(2));
        v.add(v3.cpy().scl(2));
        v.add(v4);
        v.scl(0.16666f);

//        System.out.println("k: " + k + " v: " + v);

        // Calculate the final new velocity and position
        velocity = mm.getBody().getVelocity().cpy().add(k.cpy().scl(mm.getDt()));
        position.add(v.cpy().scl(mm.getDt()));
//        System.out.println("=============================================================================V: " + velocity + " p: " + position + " a: " + k);

        mm.getBody().setVelocity(velocity.cpy());
        mm.getBody().setPosition(position.cpy());

        // Clear all the coefficients
        clear();
    }

    /**
     * Clear all the variables
     */
    public void clear() {
        v.setZero();
        v1.setZero();
        v2.setZero();
        v3.setZero();
        v4.setZero();
        k.setZero();
        k1.setZero();
        k2.setZero();
        k3.setZero();
        k4.setZero();
    }
}
