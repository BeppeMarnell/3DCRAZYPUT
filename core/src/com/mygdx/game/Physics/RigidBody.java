package com.mygdx.game.Physics;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;

public class RigidBody {
    protected static final float G = 9.81f;

    protected Vector3 position;
    protected Vector3 velocity;
    // Old velocity required for Velocity Verlet Integration
    protected Vector3 oldVelocity;
    protected Vector3 acceleration;
    protected Vector3 gravity;
    protected Vector3 totalForce;
    protected Vector3 friction;
    protected Vector3 rotation;
    protected AQuaternion orientation;
    protected Matrix4 transform;
    protected Matrix3 inverseInertiaTensor;
    protected float mu;
    protected float inverseMass;
    protected float mass;
    protected float damping;


    public RigidBody(Vector3 position, float mass) {
        this.position = position;
        this.mass = mass;
        velocity = new Vector3();
        oldVelocity = velocity;
        acceleration = new Vector3();
        inverseMass = 1 / mass;
        gravity = new Vector3(0, -G, 0);
        friction = new Vector3();
        totalForce = new Vector3();
    }

    protected void integrate(float dt, float mu) {
        this.mu = mu;
        updateDrag(0.5f, 0.3f);
        orientation.nor();
        generateTransformMatrix();
        velocityVerletIntegration(dt);
//        semiImplicitEulerIntegration(dt);
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
        Vector3 newVelocity = velocity.cpy().scl(mu).add(acceleration.cpy().scl(dt));
        velocity.set(newVelocity);
        position.add((oldVelocity.cpy().add(velocity.cpy())).scl(0.5f * dt));
    }

    private void generateTransformMatrix() {
        float[] updatedData = new float[12];
        updatedData[0] = 1 - 2 * orientation.z * orientation.z - 2 * orientation.w * orientation.w;
        updatedData[1] = 2 * orientation.y * orientation.z - 2 * orientation.x * orientation.w;
        updatedData[2] = 2 * orientation.y * orientation.w + 2 * orientation.x * orientation.z;
        updatedData[3] = position.x;
        updatedData[4] = 2 * orientation.y * orientation.z + 2 * orientation.x * orientation.w;
        updatedData[5] = 1 - 2 * orientation.x * orientation.x - 2 * orientation.w * orientation.w;
        updatedData[6] = 2 * orientation.z * orientation.w - 2 * orientation.x * orientation.y;
        updatedData[7] = position.y;
        updatedData[8] = 2 * orientation.x * orientation.w - 2 * orientation.x * orientation.z;
        updatedData[9] = 2 * orientation.z * orientation.w + 2 * orientation.x * orientation.y;
        updatedData[10] = 1 - 2 * orientation.x * orientation.x - 2 * orientation.z * orientation.z;
        updatedData[11] = position.z;
        transform.set(updatedData);
    }

    protected void addForce(Vector3 force) {
        totalForce.add(force);
    }

    protected void clearForces() {
        totalForce.setZero();
    }

    protected void updateForces(float scalar) {
        update2DGravity(scalar);
        updateGravity();
        updateFriction();
    }

    private void update2DGravity(float scalar) {
        addForce(velocity.cpy().scl(scalar * G * position.y));
    }

    private void updateGravity() {
        addForce(gravity);
    }

    private void updateFriction() {
        addForce(friction);
    }

    private void updateDrag(float k1, float k2) {
        Vector3 force = velocity.cpy();
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
        this.velocity.set(velocity);
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Vector3 getAcceleration() {
        return acceleration;
    }
}
