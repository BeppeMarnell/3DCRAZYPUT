package com.mygdx.game.Physics.MovementDepartment.IntegratorCollection;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.MovementDepartment.MovementManager;
import com.mygdx.game.Physics.MovementDepartment.ODE;

public class RungeKutta4 extends ODE {
    Vector3 k1, k2, k3, k4;
    Vector3 v1, v2, v3, v4;
    @Override
    public void solve(MovementManager mm) {
        position = mm.getBody().getPosition();
        k1 = calculateAcceleration(mm.getBody(), DT, position);
//        v1 = mm.getBody().getVelocity().cpy().add(k1.cpy());

        k2 = calculateAcceleration(mm.getBody(), DT, position.cpy().add(k1.cpy().scl(mm.getDt()/2)));
//        v2 = mm.getBody().getVelocity().cpy().add(k2.cpy().scl(mm.getDt()/2));

        k3 = calculateAcceleration(mm.getBody(), DT, position.cpy().add(k2.cpy().scl(mm.getDt()/2)));
//        v3 = mm.getBody().getVelocity().cpy().add(k3.cpy().scl(mm.getDt()/2));

        k4 = calculateAcceleration(mm.getBody(), DT, position.cpy().add(k3.cpy().scl(mm.getDt())));
//        v4 = mm.getBody().getVelocity().cpy().add(k4.cpy().scl(mm.getDt()));

        velocity = mm.getBody().getVelocity().cpy().add(k1.add(k2.add(k3.add(k4))).scl(mm.getDt()/6));
        position.add(velocity.cpy().scl(mm.getDt()));
//        position.add(v1.add(v2.add(v3.add(v4))).scl(mm.getDt()/6));

        mm.getBody().setVelocity(velocity);
        mm.getBody().setPosition(position);
        mm.getBody().getTotalForce().setZero();
    }
}
