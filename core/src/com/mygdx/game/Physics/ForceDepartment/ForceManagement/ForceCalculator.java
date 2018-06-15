package com.mygdx.game.Physics.ForceDepartment.ForceManagement;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.ForceDepartment.ForceCollection.*;
import com.mygdx.game.Physics.RigidBody;
import com.mygdx.game.Utils.Helper;

public class ForceCalculator implements ForceVisitor {
    private RigidBody body;
    private Vector3 gravity;
    private Vector3 normal;
    private Vector3 staticFriction;
    private Vector3 kineticFriction;
    private Vector3 perpendicularForce;
    private float angle;

    public ForceCalculator() {
        body = null;
    }

    @Override
    public void visit(Gravity force) {
        Vector3 pastPoint = body.getPosition().sub(body.getRadius());
        Vector3 futurePoint = body.getPosition().add(body.getRadius());
        angle = Helper.angleBetweenPoints3D(pastPoint, futurePoint);
        gravity = body.getWeight().scl((float) Math.cos(angle));
    }

    @Override
    public void visit(Normal force) {
        normal = gravity.cpy().scl(-1);
    }

    @Override
    public void visit(StaticFriction force) {
        staticFriction = normal.cpy().scl(body.getMu());
    }

    @Override
    public void visit(KineticFriction force) {
        // Only if body in moving state otherwise only static
        kineticFriction = normal.cpy().scl(body.getKineticMu());
    }

    @Override
    public void visit(Perpendicular force) {
        perpendicularForce = body.getWeight().scl((float) Math.sin(angle));
    }

    public void setBody(RigidBody body) {
        this.body = body;
    }

    public void setActingForce() {
        body.setActingForce(perpendicularForce.sub(staticFriction));
    }
}
