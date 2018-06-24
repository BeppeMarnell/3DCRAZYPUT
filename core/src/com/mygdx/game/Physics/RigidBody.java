package com.mygdx.game.Physics;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.Rotation.AMatrix3;
import com.mygdx.game.Physics.Rotation.AMatrix4;
import com.mygdx.game.Physics.Rotation.AQuaternion;
import com.mygdx.game.Physics.State.Motion;
import com.mygdx.game.Physics.State.State;

public class RigidBody {
    protected static final float G = 9.81f;

    public void setFrontPosition(Vector3 frontPosition) {
        this.frontPos = frontPosition;
    }

    public void setSidePosition(Vector3 sidePos) {
        this.sidePos = sidePos;
    }

    public void setMu(float mu) {
        this.mu = mu;
    }

    public void setKineticMu(float kineticMu) {
        this.kineticMu = kineticMu;
    }


    //enum to set up the world state
    public enum BodyState { Moving, Stopped, Flying}
    public enum Direction { Up, Down, Straight, }
    //Ball state
    protected BodyState state;
    protected Direction movement;

    // Starting State pattern implementation
    State motion;
    State idle;
    State flight;
    State trackingState;



    protected Vector3 position;
    protected Vector3 weight;
    protected Vector3 velocity;
    // Old velocity required for Velocity Verlet Integration
    protected Vector3 oldVelocity;
    protected Vector3 acceleration;
    protected Vector3 oldAcceleration;
    protected Vector3 angularAcceleration;
    protected Vector3 gravity;
    protected Vector3 totalForce;
    protected Vector3 totalTorque;
    protected Vector3 friction;
    protected Vector3 oldRotation;
    protected Vector3 rotation;
    protected AQuaternion orientation;
    protected AMatrix4 transform;
    protected AMatrix4 inverseInertiaTensor;
    protected AMatrix3 inverseInertiaTensorWorld;
    protected AMatrix4 inertiaTensor;
    protected float mu;
    protected float kineticMu;
    protected float inverseMass;
    protected float mass;
    protected float damping;
    protected float radius;
    protected Vector3 actingForce;
    protected Vector3 lastTotalForce;
    protected float slopeAngle;
    protected Vector3 appliedForce;
    protected Vector3 lastVelocity;
    protected Vector3 frontPos;
    protected Vector3 sidePos;
    private Vector3 normal;


    public RigidBody(Vector3 position, float mass, float radius) {
        motion = new Motion(this);
        this.position = position;
        this.mass = mass;
        velocity = new Vector3();
        oldVelocity = velocity;
        acceleration = new Vector3();
        oldAcceleration = acceleration;
        angularAcceleration = new Vector3();
        rotation = new Vector3();
        oldRotation = rotation;
        inverseMass = 1 / mass;
        gravity = new Vector3(0, -G, 0);
        friction = new Vector3();
        totalForce = new Vector3();
        totalTorque = new Vector3();
        inverseInertiaTensorWorld = new AMatrix3();
        inverseInertiaTensor = new AMatrix4();
        orientation = new AQuaternion();
        transform = new AMatrix4();
        this.radius = radius;
        state = BodyState.Stopped;
        movement = Direction.Straight;
        weight = new Vector3(0, -G * mass, 0); // (Newtons)
        frontPos = new Vector3();
        sidePos = new Vector3();

        kineticMu = 0.6f;
        mu = 0.7f;
        lastTotalForce = new Vector3();
        appliedForce = new Vector3();
        lastVelocity = velocity.cpy();
        normal = new Vector3();
        frontPos = position.cpy().add(10, 0, 0);
        sidePos = position.cpy().add(0, 0, -10);

//        generateInertiaTensorSphere();
    }

    protected void integrate(float dt) {
        velocityVerletIntegration(dt);
//        eulerIntegration(dt);
//        semiImplicitEulerIntegration(dt);
//        updateMatrices();
        clearForces();
    }

    private void eulerIntegration(float dt) {
        acceleration.set(totalForce.cpy().scl(inverseMass));
//        velocity.add(acceleration.cpy().scl(dt)).scl(mu);
        velocity.add(acceleration.cpy().scl(dt));
        position.add(velocity.cpy().scl(dt));
        if (velocity.len() != 0 || lastVelocity.len() == 0) {
            lastVelocity.set(velocity.cpy());
        }
        System.out.println("Vel: " + velocity + " pos: " + position + " acc: " + acceleration);
    }

    private void semiImplicitEulerIntegration(float dt) {
        acceleration.set(totalForce.scl(inverseMass));
        velocity.add(acceleration.cpy().scl(dt)).scl(mu);
        position.add(velocity.cpy().scl(dt));
    }

    private void velocityVerletIntegration(float dt) {
//        System.out.println(",,,,,,,,,,,,,,,,, tForce: " + totalForce);
//        if (totalForce.x != 0 || totalForce.z != 0) lastTotalForce = totalForce.cpy();
//        System.out.println(",,,,,,,,,,,,,,,,, lastForce: " + lastTotalForce);
//        oldAcceleration.set(acceleration);
        acceleration.set(totalForce.cpy().scl(inverseMass));
//        oldAcceleration.add(totalForce.cpy().scl(inverseMass));
        oldVelocity.set(velocity);

//        lastTotalForce = velocity.nor();
        Vector3 newVelocity = velocity.cpy().add(acceleration.cpy().scl(dt));
        velocity.set(newVelocity);
        position.add((oldVelocity.cpy().add(velocity.cpy())).scl(0.5f * dt));
        System.out.println("========== p: " + position + " v: " + velocity + " a: " + acceleration);
        System.out.println("===== tf: " + totalForce + " ltf: " + lastTotalForce);


////        angularAcceleration = inverseInertiaTensorWorld.transform(totalTorque);
//        angularAcceleration = inertiaTensor.transform(totalTorque);
////        System.out.println("iitw: " + inertiaTensor + " " + totalTorque);
//        oldRotation.set(rotation);
//        rotation.add(angularAcceleration.cpy().scl(dt));
//        Vector3 newRotation = (oldRotation.cpy().add(rotation.cpy())).scl(0.5f);
//        orientation.updateByScaledVector(newRotation, dt);
////        System.out.println("========= " + angularAcceleration + " " + orientation);

    }

    private void updateMatrices() {
        orientation.nor();
        generateTransformMatrix();
//        generateInertiaTensor();
    }

    private void generateTransformMatrix() {
        transform.set(position, orientation);
    }

    private void generateInertiaTensor() {
        float[] updatedData = new float[9];
        updatedData[0] = transform.val[0] * inverseInertiaTensor.val[0] + transform.val[1] * inverseInertiaTensor.val[3] + transform.val[2] * inverseInertiaTensor.val[6];
        updatedData[1] = transform.val[0] * inverseInertiaTensor.val[1] + transform.val[1] * inverseInertiaTensor.val[4] + transform.val[2] * inverseInertiaTensor.val[7];
        updatedData[2] = transform.val[0] * inverseInertiaTensor.val[2] + transform.val[1] * inverseInertiaTensor.val[5] + transform.val[2] * inverseInertiaTensor.val[8];
        updatedData[3] = transform.val[4] * inverseInertiaTensor.val[0] + transform.val[5] * inverseInertiaTensor.val[3] + transform.val[6] * inverseInertiaTensor.val[6];
        updatedData[4] = transform.val[4] * inverseInertiaTensor.val[1] + transform.val[5] * inverseInertiaTensor.val[4] + transform.val[6] * inverseInertiaTensor.val[7];
        updatedData[5] = transform.val[4] * inverseInertiaTensor.val[2] + transform.val[5] * inverseInertiaTensor.val[5] + transform.val[6] * inverseInertiaTensor.val[8];
        updatedData[6] = transform.val[8] * inverseInertiaTensor.val[0] + transform.val[9] * inverseInertiaTensor.val[3] + transform.val[10] * inverseInertiaTensor.val[6];
        updatedData[7] = transform.val[8] * inverseInertiaTensor.val[1] + transform.val[9] * inverseInertiaTensor.val[4] + transform.val[10] * inverseInertiaTensor.val[7];
        updatedData[8] = transform.val[8] * inverseInertiaTensor.val[2] + transform.val[9] * inverseInertiaTensor.val[5] + transform.val[10] * inverseInertiaTensor.val[8];

        float[] data = new float[9];
        data[0] = updatedData[0] * transform.val[0] + updatedData[1] * transform.val[1] + updatedData[2] * transform.val[2];
        data[1] = updatedData[0] * transform.val[4] + updatedData[1] * transform.val[5] + updatedData[2] * transform.val[6];
        data[2] = updatedData[0] * transform.val[8] + updatedData[1] * transform.val[9] + updatedData[2] * transform.val[10];
        data[3] = updatedData[3] * transform.val[0] + updatedData[4] * transform.val[1] + updatedData[5] * transform.val[2];
        data[4] = updatedData[3] * transform.val[4] + updatedData[4] * transform.val[5] + updatedData[5] * transform.val[6];
        data[5] = updatedData[3] * transform.val[8] + updatedData[4] * transform.val[9] + updatedData[5] * transform.val[10];
        data[6] = updatedData[6] * transform.val[0] + updatedData[7] * transform.val[1] + updatedData[8] * transform.val[2];
        data[7] = updatedData[6] * transform.val[4] + updatedData[7] * transform.val[5] + updatedData[8] * transform.val[6];
        data[8] = updatedData[6] * transform.val[8] + updatedData[7] * transform.val[9] + updatedData[8] * transform.val[10];

        inverseInertiaTensorWorld.set(data).inv();
    }

    private void generateInertiaTensorSphere() {
        float data = (2f / 5f) * mass * radius * radius;
        inertiaTensor = new AMatrix4(new float[]{data, 0, 0, 0, 0, data, 0, 0, 0, 0, data, 0, 0, 0, 0, data});
//        inverseInertiaTensor.set(inertiaTensor.inv());
    }

    public void addForce(Vector3 force) {
//        state = BodyState.Moving;
//        lastTotalForce.set(totalForce);
        totalForce.add(force);
//        totalForce.set(force);
//        lastTotalForce.set(force);
//        System.out.println("!!!!!!!!!!!!!******************* setting last total force inside addForce() to: " + lastTotalForce);
    }

    protected void addForceAtPoint(Vector3 force, Vector3 point) {
//        point = transform.getWorldCoordinates(point, false);
//        point.sub(position);
//        point.add(position);
//        System.out.println("post-transform: " + point + " ball pos: " + position);
        addForce(force);
        totalTorque.add(point.cpy().crs(force));
        System.out.println(point.cpy().crs(force) + " Total torque: " + totalTorque);
    }

    protected void clearForces() {
        totalForce.setZero();
        totalTorque.setZero();
    }


    protected void updateGravity() {
        addForce(gravity.cpy().scl(mass));
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

    public Vector3 getWeight() {
        return weight;
    }

    public float getMu() {
        return mu;
    }

    public float getKineticMu() {
        return kineticMu;
    }

    public void setActingForce(Vector3 actingForce) {
        this.actingForce = actingForce;
    }

    public Vector3 getOldVelocity() {
        return oldVelocity;
    }

    public Vector3 getLastTotalForce() {
        return lastTotalForce;
    }

    public float getRadius() {
        return radius;
    }

    public Vector3 getActingForce() {
        return actingForce;
    }

    public void setAcceleration(Vector3 acceleration) {
        this.acceleration = acceleration;

    }

    public float getSlopeAngle() {
        return slopeAngle;
    }

    public BodyState getState() {
        return state;
    }

    public Vector3 getAppliedForce() {
        return appliedForce;
    }

    public void setAppliedForce(Vector3 appliedForce) {
        this.appliedForce = appliedForce;
    }

    public void setTotalForce(Vector3 totalForce) {
        this.totalForce = totalForce;
    }

    public Vector3 getTotalForce() {
        return totalForce;
    }

    public void setState(BodyState state) {
        this.state = state;
    }

    public void setLastTotalForce(Vector3 lastTotalForce) {
        this.lastTotalForce = lastTotalForce;
    }

    public Vector3 getLastVelocity() {
        return lastVelocity;
    }

    public Vector3 getFrontPos() {
        return frontPos;
    }

    public Vector3 getSidePos() {
        return sidePos;
    }

    public Vector3 getNormal() {
        return normal;
    }

    public void setNormal(Vector3 normal) {
        this.normal = normal;
    }

    public void setLastVelocity(Vector3 lastVelocity) {
        this.lastVelocity = lastVelocity;
    }

    public boolean isStopped(){
        if(state == BodyState.Stopped)return true;
        else return false;
    }
}

