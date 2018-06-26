package com.mygdx.game.Physics.ForceDepartment.ForceManagement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.ForceDepartment.ForceCollection.*;
import com.mygdx.game.Physics.RigidBody;
import com.mygdx.game.WObjects.Map;

import java.util.HashMap;

public class ForceManager {
//    public final static Force[] forces = new Force[]{new Gravity(), new Normal(), new Perpendicular(), new StaticFriction(), new Total(), new Drag(), new KineticFriction()};
    public final static Force[] forces = new Force[]{new Gravity(), new Normal(), new Perpendicular(), new StaticFriction(), new Drag(), new KineticFriction(), new Total()};
    private ForceCalculator calculator;
    private float time;
    private RigidBody body;
    private HashMap<RigidBody, Vector3> db;

    public ForceManager(Map map, HashMap<RigidBody, Vector3> db) {
        calculator = new ForceCalculator(map);
        this.db = db;
        time = 0;
    }

    //TODO: throw exception "No body found")
    public void manage(float dt) {
        if (time == 0) {
            time = dt;
            System.out.println("[*] Managing forces - pulling force from db: " + db.get(body));
            if (body.isCollided()) {
//                calculator.setTotalForce(body.getTmpVelocity().cpy().nor().scl(db.get(body).len()));
                calculator.setTotalForce(body.getVelocity().cpy().nor().scl(db.get(body).len()));
                body.isCollided(false);
            } else {
                calculator.setTotalForce(db.get(body).cpy());
            }
            for (Force f : forces) {
                f.accept(calculator);
            }
            System.out.println("------> Setting new force: " + calculator.getTotalForce());
            db.get(body).set(calculator.getTotalForce().cpy());
            calculator.clear();
        }
        time--;
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

    public RigidBody getBody() {
        return calculator.getBody();
    }

    public void setBody(RigidBody body) {
        this.body = body;
        calculator.setBody(body);
    }
}
