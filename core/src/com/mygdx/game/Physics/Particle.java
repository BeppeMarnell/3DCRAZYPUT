package com.mygdx.game.Physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.WObjects.Map;

public class Particle {
    protected static final float G = 9.81f;
    protected Vector3 position;
    protected Vector3 velocity;
    // Old velocity required for Velocity Verlet Integration
    protected Vector3 oldVelocity;
    protected Vector3 acceleration;
    protected Vector3 gravity;
    protected Vector3 totalForce;
    protected Vector3 friction;
    protected float mu;
    protected float inverseMass;
    protected float mass;
    private Map map;


    public Particle(Vector3 position, float mass, Map map) {
       this.position = position;
       this.mass = mass;
       velocity = new Vector3();
       oldVelocity = velocity;
       acceleration = new Vector3();
       inverseMass = 1 / mass;
       gravity = new Vector3(0, -G, 0);
       friction = new Vector3();
       totalForce = new Vector3();
       this.map = map;
    }

    public void integrate(float dt) {
        mu = map.getFriction(new Vector2(position.x, position.z));
//        totalForce.scl(mu);
        velocityVerletIntegration(dt);
        clearForces();
    }

    private void eulerIntegration(float dt) {
        acceleration.set(totalForce.scl(inverseMass));
        position.add(velocity.cpy().scl(dt));
        velocity.add(acceleration.cpy().scl(dt)).scl(mu);
    }

    private void semiImplicitEulerIntegration(float dt) {
        acceleration.set(totalForce.scl(inverseMass));
        velocity.add(acceleration.cpy().scl(dt)).scl(mu);
        position.add(velocity.cpy().scl(dt));
    }

    private void velocityVerletIntegration(float dt) {
        acceleration.set(totalForce.cpy().scl(inverseMass));
        oldVelocity.set(velocity);
        velocity.set((velocity.cpy().scl(mu)).add(acceleration.cpy().scl(dt)));
//        velocity.add(acceleration.cpy().scl(dt));
        position.add((oldVelocity.cpy().add(velocity.cpy())).scl(0.5f * dt));
    }

    protected void addForce(Vector3 force) {
        totalForce.add(force);
    }

    protected void clearForces() {
        totalForce.setZero();
    }

    protected void updateForces() {
        updateGravity();
        updateFriction();
    }

    private void updateGravity() {
//        gravity = new Vector3(-1 * mass * G * position.y, -1 * G, -1 * mass * G * position.y);
        addForce(gravity);
    }

    private void updateFriction() {
//        friction.set(velocity.cpy().nor().scl(-1 * mu * G));
        addForce(friction);
    }

    private void updateDrag(float k1, float k2) {
        Vector3 force = velocity;
        float drag = force.len();
        drag = k1 * drag + k2 * drag * drag;
        force.nor().scl(-drag);
        addForce(force);
    }

    public Vector3 getVelocity() {
        return velocity;
    }

    public Vector3 getPosition() {
        return position;
    }

    public float getMass() {
        return mass;
    }

    public float getInverseMass() {
        return inverseMass;
    }

    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }
}
