package com.mygdx.game.Physics.MovementDepartment.IntegratorCollection;

import com.mygdx.game.Physics.MovementDepartment.MovementManager;
import com.mygdx.game.Physics.MovementDepartment.ODE;

public class Midpoint extends ODE {

    @Override
    public void solve(MovementManager mm) {
        position = mm.getBody().getPosition().cpy();
        predict(mm);
        correct(mm);
        mm.getBody().setVelocity(velocity.cpy());
        mm.getBody().setPosition(position.cpy().add(velocity.cpy().scl(mm.getDt())));
//        System.out.println("V: " + velocity + " p: " + position + " a: " + acceleration);
//        mm.getBody().getTotalForce().setZero();
    }

    private void predict(MovementManager mm) {
        predictedAcceleration = calculateAcceleration(mm.getBody(), DT, position);
        predictedVelocity = mm.getBody().getVelocity().cpy();
        predictedVelocity.add(predictedAcceleration.cpy().scl(mm.getDt()/2));
        predictedPosition = position.cpy().add(predictedVelocity.cpy().scl(mm.getDt()/2));
    }

    private void correct(MovementManager mm) {
        acceleration = calculateAcceleration(mm.getBody(), DT, predictedPosition);
        velocity = mm.getBody().getVelocity().cpy();
        velocity.add(acceleration.cpy().scl(mm.getDt()));
        position.add(velocity.cpy().scl(mm.getDt()));
    }
}
