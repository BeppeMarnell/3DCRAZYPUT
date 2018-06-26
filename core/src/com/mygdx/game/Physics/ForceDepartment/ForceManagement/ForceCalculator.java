package com.mygdx.game.Physics.ForceDepartment.ForceManagement;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.ForceDepartment.ForceCollection.*;
import com.mygdx.game.Physics.RigidBody;
import com.mygdx.game.Utils.Helper;
import com.mygdx.game.WObjects.Map;

public class ForceCalculator implements ForceVisitor {
    private final static float DRAG1 = 0.3f;
    private final static float DRAG2 = 0.01f;
    private RigidBody body;
    private Vector3 gravity;
    private Vector3 normal;
    private Vector3 staticFriction;
    private Vector3 kineticFriction;
    private Vector3 perpendicularForce;
    private Vector3 dragForce;
    private Vector3 totalForce;
    private Vector3 totalTorque;
    private Vector3 hitForce;

    //TODO: REMOVE
    private Vector3 tmpNorm = new Vector3();
    private Vector3 tmpForce = new Vector3();

    private float theta;
    private Map map;

    public ForceCalculator(Map map) {
        body = null;
        this.map = map;
        gravity = new Vector3();
        normal = new Vector3();
        staticFriction = new Vector3();
        kineticFriction = new Vector3();
        perpendicularForce = new Vector3();
        totalForce = new Vector3();
        dragForce = new Vector3();
        hitForce = new Vector3();
        totalTorque = new Vector3();
        theta = 0;
    }

    @Override
    public void visit(Gravity force) {
        // TODO: if ball is in flying state, add gravity to the acting force
        gravity = body.getWeight().cpy();
        if (body.getState() == RigidBody.BodyState.Flying) {
//            body.getTotalForce().add(gravity);
            totalForce.add(gravity);
        }
    }

    @Override
    public void visit(Normal force) {
//        if (!body.getTmpPosition().isZero()) {
        body.setFrontPosition(body.getTmpPosition().cpy().add(0, 0, 3));
        body.setSidePosition(body.getTmpPosition().cpy().add(3, 0, 0));
//        } else {
//            body.setFrontPosition(body.getPosition().cpy().add(0, 0, 3));
//            body.setSidePosition(body.getPosition().cpy().add(3, 0, 0));
//        }
        body.getFrontPos().y = map.getHeight(new Vector2(body.getFrontPos().x, body.getFrontPos().z), body.getRadius());
        body.getSidePos().y = map.getHeight(new Vector2(body.getSidePos().x, body.getSidePos().z), body.getRadius());
        normal = body.getFrontPos().cpy().crs(body.getSidePos().cpy()).nor();
        if (normal.y < 0) normal.scl(-1);
        theta = (float) Math.toDegrees(Math.acos(normal.cpy().y));
        body.setSlopeAngle(theta);
        body.setNormal(normal);

        //TODO: REMOVE
        tmpNorm = normal.cpy();

    }

    @Override
    public void visit(Perpendicular force) {
        Vector3 projection = normal.cpy().scl(gravity.cpy().dot(normal));
        perpendicularForce = gravity.cpy().sub(projection);
    }

    @Override
    public void visit(StaticFriction force) {
//        if (body.getState() == RigidBody.BodyState.Stopped) {
            staticFriction = perpendicularForce.cpy().nor().scl(body.getMu() * body.getWeight().y);
//        }
    }

    @Override
    public void visit(KineticFriction force) {
        if (body.getState() == RigidBody.BodyState.Moving) {
            kineticFriction = totalForce.cpy().nor().scl(body.getKineticMu() * body.getWeight().y);
        }
    }

    @Override
    public void visit(Drag force) {
        if (body.getState() == RigidBody.BodyState.Moving || body.getState() == RigidBody.BodyState.Flying) {
            float drag = body.getVelocity().cpy().len();
            drag = DRAG1 * drag + DRAG2 * drag * drag;
            dragForce = body.getVelocity().cpy().nor().scl(-drag);
//            totalForce.add(body.getVelocity().cpy().nor().scl(-drag));
//            totalForce.add(totalForce.cpy().nor().scl(-drag));
        }
    }

    public void visit(Total force) {
//        if (body.isCollided()) {
//            totalForce = body.getVelocity().cpy().nor().scl(totalForce.len());
//            body.isCollided(false);
//        }
        if (body.getState() == RigidBody.BodyState.Stopped) {
            totalForce = hitForce.cpy().add(perpendicularForce);
//            System.out.println("[**] Resting acting force: " + totalForce + " " + totalForce.len());
            if (totalForce.len() > staticFriction.len()) {
//                totalForce.y = 0;
//                System.out.println("Moving");
                totalForce.add(staticFriction);
                body.setTotalForce(totalForce.cpy());
                body.setState(RigidBody.BodyState.Moving);
            }
        } else if (body.getState() == RigidBody.BodyState.Moving) {


//            totalTorque.add(new Vector3(0, 0, 1).crs(totalForce));
//            System.out.println("=== torq: " + totalTorque);

            totalForce.add(hitForce.cpy().add(perpendicularForce));
//            System.out.println("[**] Moving acting force: " + totalForce + " - " + totalForce.len());
            if (totalForce.len() < kineticFriction.len() + dragForce.len()) {
//                totalForce.y = 0;
//                System.out.println("Stopping");
                body.setState(RigidBody.BodyState.Stopped);
                body.getTotalForce().setZero();
                body.getVelocity().setZero();
            } else {
                totalForce.add(kineticFriction);
                totalForce.add(dragForce);
                body.setTotalForce(totalForce.cpy());

                //TODO:REMOVE
                tmpForce = totalForce.cpy();
            }
        }
        hitForce.setZero();
    }


    public void drawForces(ShapeRenderer shapeRenderer) {
        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(tmpNorm.cpy().scl(1.5f)), 3, Color.YELLOW, shapeRenderer);
        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(tmpForce), 3, Color.CYAN, shapeRenderer);
//        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(perpendicularForce.cpy()), 3, Color.CYAN, shapeRenderer);
        Helper.DrawDebugLine(body.getPosition(), body.getFrontPos(), 2, Color.RED, shapeRenderer);
        Helper.DrawDebugLine(body.getPosition(), body.getSidePos(), 2, Color.BLACK, shapeRenderer);
//        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(staticFriction), 2, Color.CHARTREUSE, shapeRenderer);
//        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(kineticFriction), 2, Color.RED, shapeRenderer);
//        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(hitForce), 2, Color.BLACK, shapeRenderer);
    }

    public void setHitForce(Vector3 hitForce) {
        System.out.println("Hit! " + hitForce);
        body.setState(RigidBody.BodyState.Moving);
        this.hitForce = hitForce;
    }

    public RigidBody getBody() {
        return body;
    }

    public Map getMap() {
        return map;
    }

    public void clear() {
        gravity.setZero();
        totalForce.setZero();
        staticFriction.setZero();
        kineticFriction.setZero();
        perpendicularForce.setZero();
        normal.setZero();
        dragForce.setZero();
        theta = 0;
    }

    public void setTotalForce(Vector3 totalForce) {
        this.totalForce = totalForce;
    }

    public Vector3 getTotalForce() {
        return totalForce;
    }

    public void setBody(RigidBody body) {
        this.body = body;
    }

    public void printDebug() {
        System.out.println("+ Gravity: " + gravity + " - " + gravity.len());
        System.out.println("+ Normal: " + normal + " - " + normal.len() + " ang: " + theta);
        System.out.println("+ Perpf: " + perpendicularForce + " - " + perpendicularForce.len() + " sin " + Math.abs(Math.sin(Math.toRadians(theta))));
        System.out.println("+ staticFr: " + staticFriction + " - " + staticFriction.len());
        System.out.println("+ kineticFr: " + kineticFriction + " - " + kineticFriction.len());
        System.out.println("+ dragFr: " + dragForce + " - " + dragForce.len());
        System.out.println("+ totalF: " + body.getTotalForce() + " - " + body.getTotalForce().len());
    }
}
