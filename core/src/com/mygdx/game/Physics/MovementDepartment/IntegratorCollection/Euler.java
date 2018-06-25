package com.mygdx.game.Physics.MovementDepartment.IntegratorCollection;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.MovementDepartment.MovementManager;
import com.mygdx.game.Physics.MovementDepartment.ODE;

public class Euler extends ODE {


    @Override
    public void solve(MovementManager mm) {
        Vector3 velocity = mm.getBody().getVelocity().cpy();
        Vector3 position = mm.getBody().getPosition().cpy();
        velocity.add(calculateAcceleration(mm.getBody()).scl(mm.getDt()));
        mm.getBody().setVelocity(velocity.cpy());
        mm.getBody().setPosition(position.add(velocity.cpy().scl(mm.getDt())));
        System.out.println("V: " + velocity + " p: " + position + " a: " + calculateAcceleration(mm.getBody()));
        mm.getBody().getTotalForce().setZero();
    }
}
