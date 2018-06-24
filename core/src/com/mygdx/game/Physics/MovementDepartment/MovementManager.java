package com.mygdx.game.Physics.MovementDepartment;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Physics.ForceDepartment.ForceManagement.ForceManager;
import com.mygdx.game.Physics.RigidBody;
import com.mygdx.game.WObjects.Map;

public class MovementManager {
    private ForceManager fm;
    private ODE ode;
    private RigidBody body;
    private Map map;
    private float dt;

    public MovementManager(ODE ode, Map map) {
        this.ode = ode;
        this.map = map;
    }

    public void manage(ForceManager fm, float dt) {
        this.fm = fm;
        this.dt = dt;
        if (!body.isStopped()) {
            move();
            ode.solve(this);
        }
    }

    private void move() {
        body.setFrontPosition(body.getPosition().cpy().add(0, 0, 3));
        body.setSidePosition(body.getPosition().cpy().add(3, 0, 0));
        body.getFrontPos().y = map.getHeight(new Vector2(body.getFrontPos().x, body.getFrontPos().z), body.getRadius());
        body.getSidePos().y = map.getHeight(new Vector2(body.getSidePos().x, body.getSidePos().z), body.getRadius());
        float height = map.getHeight(new Vector2(body.getPosition().x, body.getPosition().z), body.getRadius());

        if (body.getPosition().y - height > 1) {
            body.setState(RigidBody.BodyState.Flying);
        }

        if (body.getPosition().y < height) {
            body.getPosition().y = height;
        }

        body.setMu(map.getFriction(new Vector2(body.getPosition().x, body.getPosition().z)));
        body.setKineticMu(body.getMu() - 0.1f);
    }

    public void setForceManager(ForceManager fm) {
        this.fm = fm;
    }

    public void setOde(ODE ode) {
        this.ode = ode;
    }

    public RigidBody getBody() {
        return body;
    }

    public ForceManager getFm() {
        return fm;
    }

    public void setBody(RigidBody body) {
        this.body = body;
    }

    public float getDt() {
        return dt;
    }
}
