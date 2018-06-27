package com.mygdx.game.Physics.ForceDepartment.ForceManagement;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.ForceDepartment.ForceCollection.*;
import com.mygdx.game.Physics.RigidBody;
import com.mygdx.game.WObjects.Map;

import java.util.HashMap;

/**
 * Class in charge of managing the process of force calculation
 */
public class ForceManager {
    public final static Force[] KINETIC_FORCES = new Force[]{new Gravity(), new Normal(), new Perpendicular(), new KineticFriction(), new Drag(), new Total()};
    public final static Force[] STATIC_FORCES = new Force[]{new Gravity(), new Normal(), new Perpendicular(), new StaticFriction(), new Total()};
    private ForceCalculator calculator;
    private float time;
    private RigidBody body;
    // Database holding the body and the current force acting on it
    private HashMap<RigidBody, Vector3> db;

    public ForceManager(Map map, HashMap<RigidBody, Vector3> db) {
        calculator = new ForceCalculator(map);
        this.db = db;
        time = 0;
    }

    /**
     * Main method, manages the bodies, calculates and applies the forces acting on them
     * @param dt
     * @param isIdle
     */
    //TODO: throw exception "No body found")
    public void manage(float dt, boolean isIdle) {
        // The timer makes sure the physics are calculated at a fixed step
        if (time == 0) {
            time = dt;

            // If the body is in collision, reverse the total force based on the new, post-collision velocity
            if (body.isCollided()) {
                calculator.setTotalForce(body.getVelocity().cpy().nor().scl(db.get(body).len()));
                body.isCollided(false);
            } else {
                // Otherwise get the current force from the database
                calculator.setTotalForce(db.get(body).cpy());
            }

            // If the body is in motion or flying, apply the respective forces
            if (!isIdle) {
                for (Force f : KINETIC_FORCES) {
                    f.accept(calculator);
                }
            } else {
                // otherwise apply the forces that act on a static object
                calculator.clear();
                for (Force f : STATIC_FORCES) {
                    f.accept(calculator);
                }
            }

            // Set the new force in the database
            db.get(body).set(calculator.getTotalForce().cpy());

            calculator.printDebug();

            // Clear the forces in the calculator
            calculator.clear();
        }
        time--;
    }

    /**
     * Draws on screen
     * @param shapeRenderer
     */
    public void draw(ShapeRenderer shapeRenderer) {
        calculator.drawForces(shapeRenderer);
    }

    /**
     * Listens for key inputs
     */
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
    }

    public RigidBody getBody() {
        return calculator.getBody();
    }

    public void setBody(RigidBody body) {
        this.body = body;
        calculator.setBody(body);
    }

    public void hit(Vector3 force) {
        calculator.setHitForce(force);
    }
}
