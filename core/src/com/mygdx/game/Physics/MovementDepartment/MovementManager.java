package com.mygdx.game.Physics.MovementDepartment;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.ForceDepartment.ForceManagement.ForceManager;
import com.mygdx.game.Physics.RigidBody;
import com.mygdx.game.WObjects.Map;

import java.util.HashMap;

public class MovementManager {
    private ForceManager fm;
    private ODE ode;
    private Map map;
    private RigidBody body;
    private float dt;
    private HashMap<RigidBody, Vector3> db;

    public MovementManager(Map map) {
        this.map = map;
        db = new HashMap<>();
        fm = new ForceManager(map, db);
    }

    public void manage(float dt) {
        fm.inputController();
        this.dt = dt;
        for (RigidBody b : db.keySet()) {
            body = b;
            fm.setBody(body);
            if (body.isMovedByBot()) {
               fm.hit(body.botForce.cpy());
               body.setMovedByBot(false);
            }
            if (!body.isStopped()) {
                move();
                ode.solve(this);
                ode.clear();
            } else {

            }
        }
    }

    private void move() {
        body.setFrontPosition(body.getPosition().cpy().add(0, 0, 3));
        body.setSidePosition(body.getPosition().cpy().add(3, 0, 0));
        body.getFrontPos().y = map.getHeight(new Vector2(body.getFrontPos().x, body.getFrontPos().z), body.getRadius());
        body.getSidePos().y = map.getHeight(new Vector2(body.getSidePos().x, body.getSidePos().z), body.getRadius());
        float height = map.getHeight(new Vector2(body.getPosition().x, body.getPosition().z), body.getRadius());

//        if (body.getPosition().y - height > 0.5) {
//            body.setState(RigidBody.BodyState.Flying);
//        }
        body.getPosition().y = height;

        if (body.getPosition().y < height) {
            body.getPosition().y = height;
//            body.setState(RigidBody.BodyState.Moving);
        }

        body.setMu(map.getFriction(new Vector2(body.getPosition().x, body.getPosition().z)));
        body.setKineticMu(body.getMu() - 0.2f);
        System.out.println("--> Moving body, state " + body.getState().toString() + " height: " + body.getPosition().y);
        System.out.println("--> Terrain height: " + height);
    }

    public void addBody(RigidBody body) {
        db.put(body, new Vector3());
    }

    public void setOde(ODE ode) {
        this.ode = ode;
        ode.setFm(fm);
    }

    public RigidBody getBody() {
        return body;
    }

    public void setBody(RigidBody body) {
        this.body = body;
    }

    public float getDt() {
        return dt;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        fm.draw(shapeRenderer);
    }
}
