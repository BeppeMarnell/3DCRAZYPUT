package com.mygdx.game.Physics.MovementDepartment.IntegratorCollection;

import com.mygdx.game.Physics.MovementDepartment.MovementManager;
import com.mygdx.game.Physics.MovementDepartment.ODE;

/**
 * Midpoint integration
 */
public class Midpoint extends ODE {

    /**
     * Solves the movement equations based on the Midpoint method, uses predictor & corrector
     * @param mm
     */
    @Override
    public void solve(MovementManager mm) {
        position = mm.getBody().getPosition().cpy();
        predict(mm);
        correct(mm);
        mm.getBody().setVelocity(velocity.cpy());
        mm.getBody().setPosition(position.cpy().add(velocity.cpy().scl(mm.getDt())));
    }

    /**
     * Predicts the next acceleration, velocity and position at half step
     * @param mm
     */
    private void predict(MovementManager mm) {
        predictedAcceleration = calculateAcceleration(mm.getBody(), DT, position);
        predictedVelocity = mm.getBody().getVelocity().cpy();
        predictedVelocity.add(predictedAcceleration.cpy().scl(mm.getDt()/2));
        predictedPosition = position.cpy().add(predictedVelocity.cpy().scl(mm.getDt()/2));
    }

    /**
     * Corrects the predicted value at full step
     * @param mm
     */
    private void correct(MovementManager mm) {
        acceleration = calculateAcceleration(mm.getBody(), DT, predictedPosition);
        velocity = mm.getBody().getVelocity().cpy();
        velocity.add(acceleration.cpy().scl(mm.getDt()));
        position.add(velocity.cpy().scl(mm.getDt()));
    }
}
