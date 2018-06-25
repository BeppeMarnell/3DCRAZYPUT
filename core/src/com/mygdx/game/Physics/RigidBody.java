package com.mygdx.game.Physics;

import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.State.Motion;
import com.mygdx.game.Physics.State.State;

public class RigidBody {
    protected static final float G = 9.81f;

    //enum to set up the world state
    public enum BodyState { Moving, Stopped, Flying}
    //Ball state
    protected BodyState state;
    // Starting State pattern implementation
    State motion;
    State idle;
    State flight;
    State trackingState;

    protected Vector3 position;
    protected Vector3 acceleration;
    protected Vector3 weight;
    protected Vector3 velocity;
    protected Vector3 frontPos;
    protected Vector3 sidePos;
    protected Vector3 totalForce;
    protected Vector3 normal;
    protected Vector3 tmpPosition;
    protected Matrix3 inverseInertiaTensor;
    protected float mu;
    protected float kineticMu;
    protected float inverseMass;
    protected float mass;
    protected float radius;
    protected float slopeAngle;


    public RigidBody(Vector3 position, float mass, float radius) {
        float tensorSphereData = 2 / 5 * mass * radius * radius;
        inverseInertiaTensor = new Matrix3(new float[]{1/ tensorSphereData, 0, 0, 0, 1/ tensorSphereData, 0, 0, 0, 1/ tensorSphereData});
        motion = new Motion(this);
        this.position = position;
        this.mass = mass;
        velocity = new Vector3();
        acceleration = new Vector3();
        inverseMass = 1 / mass;
        this.radius = radius;
        state = BodyState.Stopped;
        weight = new Vector3(0, -G * mass, 0); // (Newtons)
        frontPos = position.cpy().add(10, 0, 0);
        sidePos = position.cpy().add(0, 0, -10);
        tmpPosition = new Vector3();
        totalForce = new Vector3();
        normal = new Vector3();
        kineticMu = 0.6f;
        mu = 0.7f;
    }

    public Vector3 getAcceleration() {
        return acceleration;
    }

    public void setAcceleration(Vector3 acceleration) {
        this.acceleration = acceleration;
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
    public Vector3 getWeight() {
        return weight;
    }

    public float getMu() {
        return mu;
    }

    public float getKineticMu() {
        return kineticMu;
    }

    public float getRadius() {
        return radius;
    }

    public float getSlopeAngle() {
        return slopeAngle;
    }

    public BodyState getState() {
        return state;
    }
    public void setState(BodyState state) {
        this.state = state;
    }

    public Vector3 getFrontPos() {
        return frontPos;
    }

    public Vector3 getSidePos() {
        return sidePos;
    }
    public boolean isStopped(){
        if(state == BodyState.Stopped)return true;
        else return false;
    }

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

    public void setSlopeAngle(float slopeAngle) {
        this.slopeAngle = slopeAngle;
    }

    public Vector3 getTotalForce() {
        return totalForce;
    }

    public void setTotalForce(Vector3 totalForce) {
        this.totalForce = totalForce;
    }

    public Vector3 getNormal() {
        return normal;
    }

    public void setNormal(Vector3 normal) {
        this.normal = normal;
    }

    public void setTmpPosition(Vector3 tmpPosition) {
        this.tmpPosition = tmpPosition;
    }

    public Vector3 getTmpPosition() {
        return tmpPosition;
    }
}

