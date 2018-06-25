package com.mygdx.game.Physics.ForceDepartment.ForceManagement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.ForceDepartment.ForceCollection.*;
import com.mygdx.game.Physics.RigidBody;
import com.mygdx.game.WObjects.Map;

public class ForceManager {
    public final static Force[] forces = new Force[]{new Gravity(), new Normal(), new Perpendicular(), new StaticFriction(), new KineticFriction(), new Drag()};
    private ForceCalculator calculator;
    private float time;
    private RigidBody[] bodies;

    public ForceManager(Map map) {
        calculator = new ForceCalculator(map);
    }

    //TODO: throw exception "No body found")
    public void manage(float dt) {
//        inputController();
        time += 1;
        if (time == dt) {
            time = 0;
//            System.out.println("[*] Managing forces: ");
            for (Force f : forces) {
                f.accept(calculator);
            }
            calculator.setActingForce();
        }
    }

    public void draw(ShapeRenderer shapeRenderer) {
        calculator.drawForces(shapeRenderer);
    }

    public void inputController() {
        if(Gdx.input.isKeyJustPressed(Input.Keys.LEFT)) {
            calculator.setHitForce(new Vector3(-100,0,0));
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) {
            calculator.setHitForce(new Vector3(100,0,0));
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            calculator.setHitForce(new Vector3(0,0,100));
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            calculator.setHitForce(new Vector3(0,0,-100));
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_8)) {
            calculator.setHitForce(new Vector3(0,100,0));
        }
        if(Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_2)) {
            calculator.setHitForce(new Vector3(0,-100,0));
        }
//        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
//            lastVelocity = velocity.cpy();
//            velocity.setZero();
//            state = BodyState.Stopped;
//        }

    }

    public void setBodies(RigidBody[] bodies) {
        this.bodies = bodies;
    }

    public void setBody(RigidBody body) {
        calculator.setBody(body);
    }

    public RigidBody[] getBodies() {
        return bodies;
    }

    public RigidBody getBody() {
        return calculator.getBody();
    }

    public Map getMap() {
        return calculator.getMap();
    }

}
