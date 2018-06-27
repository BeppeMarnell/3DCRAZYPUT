package com.mygdx.game.Physics.MovementDepartment;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.ForceDepartment.ForceManagement.ForceManager;
import com.mygdx.game.Physics.RigidBody;
import com.mygdx.game.WObjects.Map;

import java.util.HashMap;

import static com.mygdx.game.Physics.MovementDepartment.ODE.DT;

/**
 * Class in charge of controlling movement
 */
public class MovementManager {
    private ForceManager fm;
    private ODE ode;
    private Map map;
    private RigidBody body;
    private float dt;
    /**
     * Database, holds the body and it's current force
     */
    private HashMap<RigidBody, Vector3> db;

    /**
     * Constructor of the Movement Manager
     * Instantiates the Force Manager and the Database
     * @param map - game map
     */
    public MovementManager(Map map) {
        this.map = map;
        db = new HashMap<>();
        fm = new ForceManager(map, db);
    }

    /**
     * Use force manager to calculate the current KINETIC_FORCES
     * and then integrate the body
     * @param dt
     */
    public void manage(float dt) {
        // listen for key inputs
        fm.inputController();
        this.dt = dt;
        for (RigidBody b : db.keySet()) {
            body = b;
            // give the force manager the body to work on
            fm.setBody(body);
            if (body.isHit()) {
               fm.hit(body.hitForce.cpy());
               body.setIsHit(false);
            }
            // if the body is in motion, keep moving and integrate
            if (!body.isStopped()) {
                move();
                ode.solve(this);
                ode.clear();
            } else {
                fm.manage(DT, true);
            }
        }
    }

    private void move() {
        // Get a position in front of the ball and one on the side in order to be able to calculate the slope later
        body.setFrontPosition(body.getPosition().cpy().add(0, 0, 3));
        body.setSidePosition(body.getPosition().cpy().add(3, 0, 0));

        // Set their height to the height of the map at their points
        body.getFrontPos().y = map.getHeight(new Vector2(body.getFrontPos().x, body.getFrontPos().z), body.getRadius());
        body.getSidePos().y = map.getHeight(new Vector2(body.getSidePos().x, body.getSidePos().z), body.getRadius());

        // Get the map height at the ball position
        float height = map.getHeight(new Vector2(body.getPosition().x, body.getPosition().z), body.getRadius());

//        if (body.getPosition().y - height > 0.5) {
//            body.setState(RigidBody.BodyState.Flying);
//        }
        // Make the ball always stick to the ground
        body.getPosition().y = height;

//        if (body.getPosition().y < height) {
//            body.getPosition().y = height;
////            body.setState(RigidBody.BodyState.Moving);
//        }

        // Set the static and kinetic friction coefficients according to the type of terrain at the ball's position
        body.setMu(map.getFriction(new Vector2(body.getPosition().x, body.getPosition().z)));
        body.setKineticMu(body.getMu() - 0.2f);

//        System.out.println("--> Moving body, state " + body.getState().toString() + " height: " + body.getPosition().y);
//        System.out.println("--> Terrain height: " + height);
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
