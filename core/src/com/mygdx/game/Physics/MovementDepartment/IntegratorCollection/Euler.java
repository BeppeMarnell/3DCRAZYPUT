package com.mygdx.game.Physics.MovementDepartment.IntegratorCollection;

import com.mygdx.game.Physics.MovementDepartment.MovementManager;
import com.mygdx.game.Physics.MovementDepartment.ODE;

/**
 * Euler integration
 */
public class Euler extends ODE {

    /**
     * Applies the Euler's equations
     * @param mm
     */
    @Override
    public void solve(MovementManager mm) {
        // Get current body position, velocity and acceleration
        position = mm.getBody().getPosition().cpy();
        acceleration = calculateAcceleration(mm.getBody(), DT, position.cpy());
        velocity = mm.getBody().getVelocity().cpy();

        // Calculate new velocity based on the acceleration
        velocity.add(acceleration.cpy().scl(mm.getDt()));

        // Set new values
        mm.getBody().setVelocity(velocity.cpy());
        mm.getBody().setPosition(position.cpy().add(velocity.cpy().scl(mm.getDt())));
    }
}
