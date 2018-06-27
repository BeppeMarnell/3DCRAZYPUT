package com.mygdx.game.Physics.ForceDepartment.ForceManagement;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.ForceDepartment.ForceCollection.*;
import com.mygdx.game.Physics.RigidBody;
import com.mygdx.game.Utils.Helper;
import com.mygdx.game.WObjects.Map;

/**
 * Class responsible for calculating the forces acting on the object at a specific position in time
 * Works based on a Visitor pattern
 */
public class ForceCalculator implements ForceVisitor {
    // Drag force coefficients
    private final static float DRAG1 = 0.2f;
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

    // slope angle
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

    /**
     * Calculates the gravity, applies it if the object is in flight
     * @param force
     */
    @Override
    public void visit(Gravity force) {
        gravity = body.getWeight().cpy();
        if (body.getState() == RigidBody.BodyState.Flying) {
            totalForce.add(gravity);
        }
    }

    /**
     * Calculates the normal of the ground and the slope angle at the ball's position
     * @param force
     */
    @Override
    public void visit(Normal force) {
        // Cast a ray in front of the ball and on it's side
        body.setFrontPosition(body.getTmpPosition().cpy().add(0, 0, 3));
        body.setSidePosition(body.getTmpPosition().cpy().add(3, 0, 0));
        body.getFrontPos().y = map.getHeight(new Vector2(body.getFrontPos().x, body.getFrontPos().z), body.getRadius());
        body.getSidePos().y = map.getHeight(new Vector2(body.getSidePos().x, body.getSidePos().z), body.getRadius());

        // Get the cross product of the ray vectors to get the normal
        normal = body.getFrontPos().cpy().crs(body.getSidePos().cpy()).nor();
        // Prevention of negative normal
        if (normal.y < 0) normal.scl(-1);

        // Angle of the slope is the acos of the height value of the normal vector
        theta = (float) Math.toDegrees(Math.acos(normal.cpy().y));

        body.setSlopeAngle(theta);
        body.setNormal(normal);

        //TODO: REMOVE
        tmpNorm = normal.cpy();
    }

    /**
     * Calculates the force perpendicular to the normal, i.e. the force acting on the body at all times
     * used  to simulate motion on slopes
     * @param force
     */
    @Override
    public void visit(Perpendicular force) {
        // Project gravity on the normal
        Vector3 projection = normal.cpy().scl(gravity.cpy().dot(normal));

        perpendicularForce = gravity.cpy().sub(projection);
    }

    /**
     * Calculates the static friction when the body is idle
     * @param force
     */
    @Override
    public void visit(StaticFriction force) {
        // acts opposite to the perpendicular force pushing the body, scaled by the friction coefficient of the terrain
        staticFriction = perpendicularForce.cpy().nor().scl(body.getMu() * body.getWeight().y);
    }

    /**
     * Calculates the kinetic friction when the body is moving
     * @param force
     */
    @Override
    public void visit(KineticFriction force) {
        // acts opposite to the direction of the total force pushing the body
        // scaled by the kinetic friction coefficient
        kineticFriction = totalForce.cpy().nor().scl(body.getKineticMu() * body.getWeight().y);
    }

    /**
     * Simulates the air drag force, which doesn't allow the body to exceed a certain speed
     * @param force
     */
    @Override
    public void visit(Drag force) {
        // get the velocity magnitude
        float drag = body.getVelocity().cpy().len();

        // apply the drag coefficients
        // squared velocity causes drag to increase exponentially on higher velocities
        drag = DRAG1 * drag + DRAG2 * drag * drag;
        dragForce = body.getVelocity().cpy().nor().scl(-drag);
    }

    /**
     * Calculates the total force applied on the object
     * @param force
     */
    public void visit(Total force) {
        // if the body is stopped, only consider the perpendicular force
        // add the force of the hit on the object to the total force
        if (body.getState() == RigidBody.BodyState.Stopped) {
            totalForce = hitForce.cpy().add(perpendicularForce);

            // if the force applied exceeds the static friction the object starts moving
            if (totalForce.len() > staticFriction.len()) {
                totalForce.add(staticFriction);
                body.setTotalForce(totalForce.cpy());
                body.setState(RigidBody.BodyState.Moving);
            }
        // if the body is in motion keep summing the forces
        } else if (body.getState() == RigidBody.BodyState.Moving) {
            totalForce.add(hitForce.cpy().add(perpendicularForce));
            // if the kinetic friction is bigger that the force applied, stop
            if (totalForce.len() < kineticFriction.len() + dragForce.len()) {
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
        // remove the force of the hit the put the ball in motion
        hitForce.setZero();
    }


    /**
     * Draws the force vectors on the screen
     * @param shapeRenderer
     */
    public void drawForces(ShapeRenderer shapeRenderer) {
//        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(tmpNorm.cpy().scl(1.5f)), 3, Color.YELLOW, shapeRenderer);
//        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(tmpForce), 3, Color.CYAN, shapeRenderer);
//        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(perpendicularForce.cpy()), 3, Color.CYAN, shapeRenderer);
//        Helper.DrawDebugLine(body.getPosition(), body.getFrontPos(), 2, Color.RED, shapeRenderer);
//        Helper.DrawDebugLine(body.getPosition(), body.getSidePos(), 2, Color.BLACK, shapeRenderer);
//        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(staticFriction), 2, Color.CHARTREUSE, shapeRenderer);
//        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(kineticFriction), 2, Color.RED, shapeRenderer);
//        Helper.DrawDebugLine(body.getPosition(), body.getPosition().cpy().add(hitForce), 2, Color.BLACK, shapeRenderer);
    }

    /**
     * If the object is hit, set the force in order to add it to the total force
     * @param hitForce
     */
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

    /**
     * Clear the forces
     */
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

    /**
     * Print forces on console
     */
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
