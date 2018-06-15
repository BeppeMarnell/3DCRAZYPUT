package com.mygdx.game.Physics.MovementDepartment;

import com.mygdx.game.Physics.ForceDepartment.ForceManagement.ForceManager;
import com.mygdx.game.Physics.RigidBody;

public class MovementManager {
    private ForceManager fm;
    private ODE ode;
    private RigidBody body;
    float dt;

    public MovementManager(ODE ode) {
        this.ode = ode;
    }

    public void manage(RigidBody body, ForceManager fm, float dt) {
        this.body = body;
        this.fm = fm;
        this.dt = dt;
        ode.solve(this);
    }

    public void setForceManager(ForceManager fm) {
        this.fm = fm;
    }

    public void setOde(ODE ode) {
        this.ode = ode;
    }

    public RigidBody getBody() {
        return body;
    }

    public ForceManager getFm() {
        return fm;
    }

    public float getDt() {
        return dt;
    }
}
