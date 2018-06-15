package com.mygdx.game.Physics.ForceDepartment.ForceManagement;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.ForceDepartment.ForceCollection.*;
import com.mygdx.game.Physics.RigidBody;

public class ForceCalculator implements ForceVisitor {
    private RigidBody body;
    private Vector3 gravity;
    private Vector3 normal;
    private Vector3 staticFriction;
    private Vector3 kineticFriction;
    private Vector3 perpendicularForce;

    public ForceCalculator() {
        body = null;
    }

    @Override
    public void visit(Gravity force) {
        float theta_cos = (float) Math.cos(body.getSlopeAngle());
        gravity = body.getWeight().cpy().scl(theta_cos);
        System.out.println("++++ Gravity: " + gravity);
    }

    @Override
    public void visit(Normal force) {
        normal = gravity.cpy().scl(-1);
        System.out.println("++++ Normal: " + normal);
    }

    @Override
    public void visit(StaticFriction force) {
        if (body.getState() == RigidBody.BodyState.Stopped) staticFriction = normal.cpy().scl(body.getMu());
        System.out.println("++++ staticFr: " + staticFriction);
    }

    @Override
    public void visit(KineticFriction force) {
        // Only if body in moving state otherwise only static
        if (body.getState() == RigidBody.BodyState.Moving) kineticFriction = normal.cpy().scl(body.getKineticMu());
        System.out.println("++++ kineticFr: " + kineticFriction);
    }

    @Override
    public void visit(Perpendicular force) {
        float theta_sin = (float) Math.sin(body.getSlopeAngle());
        perpendicularForce = body.getWeight().cpy().scl(theta_sin);
        System.out.println("++++ Perpf: " + perpendicularForce);
    }

    public void setBody(RigidBody body) {
        this.body = body;
    }

    public void setActingForce() {
        body.setActingForce(perpendicularForce.cpy().sub(staticFriction));
    }
}
