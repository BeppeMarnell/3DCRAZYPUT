package com.mygdx.game.Physics.ForceDepartment.ForceManagement;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.ForceDepartment.ForceCollection.*;
import com.mygdx.game.Physics.RigidBody;
import com.mygdx.game.Utils.Helper;
import com.mygdx.game.WObjects.Map;

public class ForceCalculator implements ForceVisitor {
    private RigidBody body;
    private Vector3 gravity;
    private Vector3 normal;
    private Vector3 staticFriction;
    private Vector3 kineticFriction;
    private Vector3 perpendicularForce;
    private Vector3 actingForce;
    private Vector3 hitForce;
    private Vector3 storedForce;
    private float theta;
    private Map map;
    private boolean signHasChanged;

    public ForceCalculator(Map map) {
        body = null;
        this.map = map;
        gravity = new Vector3();
        normal = new Vector3();
        staticFriction = new Vector3();
        kineticFriction = new Vector3();
        perpendicularForce = new Vector3();
        actingForce = new Vector3();
        hitForce = new Vector3();
        theta = 0;
        signHasChanged = false;
    }

    @Override
    public void visit(Gravity force) {
        // TODO: if ball is in flying state, add gravity to the acting force
        gravity = body.getWeight().cpy();
        System.out.println("+ Gravity: " + gravity + " - " + gravity.len());
    }

    @Override
    public void visit(Normal force) {
//        normal = body.getFrontPos().cpy().crs(body.getSidePos()).nor();
        normal = body.getFrontPos().cpy().crs(body.getSidePos().cpy()).nor();
        if (normal.y < 0) normal.scl(-1);
        theta = (float) Math.toDegrees(Math.acos(normal.cpy().y));
//        normal.scl(body.getWeight().cpy().scl(-1));
        body.setNormal(normal);
        System.out.println("+ Normal: " + normal + " - " + normal.len() + " ang: " + theta);
    }

    @Override
    public void visit(Perpendicular force) {
//        perpendicularForce = (gravity.cpy().nor().add(normal)).scl(-body.getWeight().y * (float) Math.abs(Math.sin(Math.toRadians(theta))));
//        perpendicularForce = (gravity.cpy().add(normal)).nor().scl(-body.getWeight().y * (float) Math.abs(Math.sin(Math.toRadians(theta))));
        Vector3 projection = normal.cpy().scl(gravity.cpy().dot(normal));
        perpendicularForce = gravity.cpy().sub(projection);
        System.out.println("+ Perpf: " + perpendicularForce + " - " + perpendicularForce.len() + " sin " + Math.abs(Math.sin(Math.toRadians(theta))));
    }

    @Override
    public void visit(StaticFriction force) {
        if (body.getState() == RigidBody.BodyState.Stopped) {
            staticFriction = perpendicularForce.cpy().nor().scl(body.getMu() * body.getWeight().y);
//            staticFriction = perpendicularForce.cpy().scl(body.getMu() * body.getWeight().y);
        }
        System.out.println("+ staticFr: " + staticFriction + " - " + staticFriction.len());
    }

    @Override
    public void visit(KineticFriction force) {
        if (body.getState() == RigidBody.BodyState.Moving) {
//            kineticFriction = body.getVelocity().cpy().nor().scl(body.getKineticMu() * body.getWeight().y);
//            kineticFriction = (hitForce.cpy().sub(perpendicularForce)).nor().scl(body.getKineticMu() * body.getWeight().y);
//            kineticFriction = (hitForce.cpy().add(perpendicularForce)).nor().scl(body.getKineticMu() * body.getWeight().y);
//            kineticFriction = (hitForce.cpy().add(perpendicularForce)).scl(body.getKineticMu() * body.getWeight().y);
            kineticFriction = actingForce.cpy().nor().scl(body.getKineticMu() * body.getWeight().y);
        }
        System.out.println("+ kineticFr: " + kineticFriction + " - " + kineticFriction.len());
    }

    public void setBody(RigidBody body) {
        this.body = body;
    }

    public void setActingForce() {
        if (body.getState() == RigidBody.BodyState.Stopped) {
            actingForce = hitForce.cpy().add(perpendicularForce);
            System.out.println("[**] Resting acting force: " + actingForce + " " + actingForce.len());
            if (actingForce.len() > staticFriction.len()) {
                actingForce.y = 0;
                System.out.println("Moving");
                actingForce.add(staticFriction);
                body.setTotalForce(actingForce.cpy());
                body.setState(RigidBody.BodyState.Moving);
            }
        } else if (body.getState() == RigidBody.BodyState.Moving) {
            actingForce.add(hitForce.cpy().add(perpendicularForce));
            System.out.println("[**] Moving acting force: " + actingForce + " " + actingForce.len());
            if (actingForce.len() < kineticFriction.len()) {
                actingForce.y = 0;
                System.out.println("Stopping");
                body.setState(RigidBody.BodyState.Stopped);
                body.getTotalForce().setZero();
                body.getVelocity().setZero();
            } else {
                actingForce.add(kineticFriction);
                body.setTotalForce(actingForce.cpy());
            }
        }
        hitForce.setZero();
    }



    public void drawForces(ShapeRenderer shapeRenderer) {
        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(normal.scl(4)), 3, Color.YELLOW, shapeRenderer);
        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(actingForce), 3, Color.PURPLE, shapeRenderer);
        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(perpendicularForce.cpy()), 3, Color.CYAN, shapeRenderer);
//        Helper.DrawDebugLine(body.getPosition(), body.getFrontPos(), 2, Color.RED, shapeRenderer);
//        Helper.DrawDebugLine(body.getPosition(), body.getSidePos(), 2, Color.BLACK, shapeRenderer);
        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(staticFriction), 2, Color.CHARTREUSE, shapeRenderer);
        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(kineticFriction), 2, Color.RED, shapeRenderer);
        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(hitForce), 2, Color.BLACK, shapeRenderer);
    }

    public void setHitForce(Vector3 hitForce) {
        System.out.println("[*} Setting hit force: " + hitForce);
//        body.setState(RigidBody.BodyState.Moving);
        this.hitForce = hitForce;
    }
}
