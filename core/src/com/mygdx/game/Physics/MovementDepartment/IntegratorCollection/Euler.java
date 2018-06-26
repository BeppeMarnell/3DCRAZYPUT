package com.mygdx.game.Physics.MovementDepartment.IntegratorCollection;

import com.mygdx.game.Physics.MovementDepartment.MovementManager;
import com.mygdx.game.Physics.MovementDepartment.ODE;

public class Euler extends ODE {

    @Override
    public void solve(MovementManager mm) {
        position = mm.getBody().getPosition().cpy();
        acceleration = calculateAcceleration(mm.getBody(), DT, position);
        velocity = mm.getBody().getVelocity().cpy();
        velocity.add(acceleration.cpy().scl(mm.getDt()));
        mm.getBody().setVelocity(velocity.cpy());
        mm.getBody().setPosition(position.cpy().add(velocity.cpy().scl(mm.getDt())));
//        System.out.println("V: " + velocity + " p: " + position + " a: " + acceleration);
        mm.getBody().getTotalForce().setZero();
    }
}
