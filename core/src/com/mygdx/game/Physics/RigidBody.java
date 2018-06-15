package com.mygdx.game.Physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.Rotation.AMatrix3;
import com.mygdx.game.Physics.Rotation.AMatrix4;
import com.mygdx.game.Physics.Rotation.AQuaternion;
import com.mygdx.game.Utils.Helper;

public class RigidBody {
    protected static final float G = 9.81f;


    //enum to set up the world state
    public enum BodyState { Moving, Stopped, }
    public enum Direction { Up, Down, Straight, }
    //Ball state
    protected BodyState state;
    protected Direction movement;

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
    protected Vector3 lastOrientation;


    public RigidBody(Vector3 position, float mass, float radius) {
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
        weight = new Vector3(0, -G * mass, 0);

        kineticMu = 0.6f;

//        generateInertiaTensorSphere();
    }

    protected void integrate(float dt) {
        updateForces();
        velocityVerletIntegration(dt);
//        semiImplicitEulerIntegration(dt);
//        updateMatrices();
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
//        oldAcceleration.set(acceleration);
        acceleration.set(totalForce.cpy().scl(inverseMass));
//        oldAcceleration.add(totalForce.cpy().scl(inverseMass));
        oldVelocity.set(velocity);
        Vector3 newVelocity = velocity.cpy().add(acceleration.cpy().scl(dt));
        velocity.set(newVelocity);
        position.add((oldVelocity.cpy().add(velocity.cpy())).scl(0.5f * dt));
        System.out.println("Vel: " + velocity + " pos: " + position + " acc: " + acceleration);

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
        state = BodyState.Moving;
        totalForce.add(force);
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

    protected void updateForces() {
//        update2DGravity();
        updateGravity();
        updateDrag(0.5f, 0.3f);
        updateFriction();
    }

    private void update2DGravity() {
        if (movement == Direction.Down) addForce(velocity.cpy().nor().scl(G * position.y));
        else addForce(velocity.cpy().nor().scl(-G * position.y));
    }

    private void updateGravity() {
//        System.out.println("Updating gravity");
        addForce(gravity.cpy().scl(mass));
//        System.out.println("====== " + totalForce);
    }

    private void updateFriction() {
//        float f = G * mu * mass * 1 / position.y;
//        Vector2 fr = new Vector2(position.x, position.z);
//        fr.nor().scl(G * mu * mass);
//        if (movement == Direction.Down) addForce(velocity.cpy().nor().scl(-G * (1/position.y) * mu * mass));
//        else addForce(velocity.cpy().nor().scl(mass * G * (1/position.y) * mu));
//        Vector3 nPos = position.cpy().add(velocity.cpy().nor().scl(2));
//        float angle = Helper.angleBetweenPoints(new Vector2(position.x, position.y), new Vector2(nPos.x, nPos.y));
//        System.out.println("========== npos: " + nPos + " ang: " + angle + " cos: " + Math.cos(angle));

        addForce(gravity.cpy().scl(mass * mu));

//        System.out.println("========= " + f + " " + fr);
//        Vector2 tmpPosition = new Vector2(velocity.x, velocity.y);
//        tmpPosition.scl(mass * G * (1 / position.y));
//        friction.set(tmpPosition.x, 0, tmpPosition.y);
//        System.out.println("============= Friction: " + friction);
//        totalForce.sub(friction);
//        velocity.scl(mu);
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

    public Vector3 getLastOrientation() {
        return lastOrientation;
    }

    public float getRadius() {
        return radius;
    }
}

