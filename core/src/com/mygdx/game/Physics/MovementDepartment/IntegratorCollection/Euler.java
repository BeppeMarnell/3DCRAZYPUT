package com.mygdx.game.Physics.MovementDepartment.IntegratorCollection;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.MovementDepartment.MovementManager;
import com.mygdx.game.Physics.MovementDepartment.ODE;

public class Euler extends ODE {


    @Override
    public void solve(MovementManager mm) {
        Vector3 velocity = mm.getBody().getVelocity();
        Vector3 position = mm.getBody().getPosition();
        velocity.add(calculateAcceleration(mm.getFm(), mm.getBody()).scl(mm.getDt()));
        mm.getBody().setVelocity(velocity);
        mm.getBody().setPosition(position.add(velocity.scl(mm.getDt())));
    }
}
