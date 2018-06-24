package com.mygdx.game.WObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.BoundingSphere;
import com.mygdx.game.Utils.Helper;

public class Ball extends BoundingSphere {
    /**
     * IMPORTANT
     * Because the map is over the y axis,
     * the game axes are :
     * x axis = x
     * y axis = z
     * z axis = y
     */

    private Model model;
    private ModelInstance ballInstance;
    //get a copy of the map
    private Map map;

    private boolean debugMode = false;

    //radius
    public static final float RAD = 1f;
    public static final float MASS = 2f;
    public static final float ELASTICITY = 0.3f;

    public Vector3 normal;
    public Vector3 perpforce;

    /**
     * Initialize the ball 3d and add the position to it
     * @param map
     */
    public Ball(Map map){
        super(new Vector3(map.getInitBallPosV2().x, map.getHeight(map.getInitBallPosV2(), RAD), map.getInitBallPosV2().y), MASS, RAD);
        ModelBuilder modelBuilder = new ModelBuilder();

        //create the ball object
        if(!debugMode)
            model = modelBuilder.createSphere(2, 2, 2,15,15,
                    new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal |
                            VertexAttributes.Usage.TextureCoordinates);
        else
            model = modelBuilder.createSphere(2, 2, 2,15,15, GL20.GL_LINES,
                    new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal |
                            VertexAttributes.Usage.TextureCoordinates);

        ballInstance = new ModelInstance(model);

        ballInstance.transform.translate(map.getInitBallPosV2().x, map.getHeight(map.getInitBallPosV2(), RAD), map.getInitBallPosV2().y);

        normal = position.cpy().add(0, 10, 0);


        //copy the instance of the map
        this.map = map;
        perpforce = new Vector3();
    }

    /**
     * render the 3D ball with a specific environment
     * @param batch
     * @param environment
     */
    public void render(ModelBatch batch, Environment environment){
        //render the ball
        batch.render(ballInstance, environment);
    }

    /**
     * Update the position of the ball
     */
    public void update(float deltaTime){
        //move the ball with keys

        if (state == BodyState.Moving) {
            move3DBall();
            integrate(deltaTime);
//            lastVelocity = velocity;
        }

        if (Math.abs(velocity.x) < 0.05 && Math.abs(velocity.z) < 0.05) {
//            clearForces();
//            state = BodyState.Stopped;
        }

//        moveByKeys();


    }

    /**
     * This method moves the position of the 3D instance, do not change it
     */
    private void move3DBall(){
        //Apply the physic to the 3D object
        float err = 0.005f;

        Vector3 oldPos = ballInstance.transform.getTranslation(new Vector3());
//        System.out.println("=== Last velocity: " + lastVelocity);

//        Vector3 frontPos = position.cpy().add(modifiedVelocity.cpy().nor().scl(2));
//        Vector3 sidePos = position.cpy().add(modifiedVelocity.cpy().rotate(new Vector3(0, 1, 0), 90).nor().scl(2));
//        frontPos = position.cpy().add(lastVelocity.cpy().nor().scl(2));
//        sidePos = position.cpy().add(lastVelocity.cpy().rotate(new Vector3(0, 1, 0), 90).nor().scl(2));
        frontPos = position.cpy().add(0, 0, 3);
        sidePos = position.cpy().add(3, 0, 0);
        frontPos.y = map.getHeight(new Vector2(frontPos.x, frontPos.z), RAD);
        sidePos.y = map.getHeight(new Vector2(sidePos.x, sidePos.z), RAD);
        System.out.println("=============== fp: " + frontPos + " sp: " + sidePos + " norvel: " + velocity.cpy().nor());
//        Vector3 normal = frontPos.cpy().crs(sidePos).nor();
        float height = map.getHeight(new Vector2(position.x, position.z), RAD);

        if (position.y - height > 1) {
            updateGravity();
        }

        if (position.y < height) {
            position.y = height;
        }

        mu = map.getFriction(new Vector2(position.x, position.z));
        kineticMu = mu - 0.1f;

        if (oldPos.y - position.y > err) {
            movement = Direction.Down;
        } else if (position.y - oldPos.y > err) {
            movement = Direction.Up;
        } else {
            movement = Direction.Straight;
        }

        //in order to move the ball i've to apply the translation amount
        ballInstance.transform.setTranslation(position);
        ballInstance.calculateTransforms();
    }

    /**
     * Move the ball by using the keyboards
     */
    private void moveByKeys(){
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            addForce(new Vector3(-100,0,0));
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            addForce(new Vector3(100,0,0));
        }

        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            addForce(new Vector3(0,0,100));
        }

        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            addForce(new Vector3(0,0,-100));
        }

        if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_8)) {
            addForce(new Vector3(0,100,0));
        }
        if(Gdx.input.isKeyPressed(Input.Keys.NUMPAD_2)) {
            addForce(new Vector3(0,-100,0));
        }
        if(Gdx.input.isKeyPressed(Input.Keys.S)) {
            lastVelocity = velocity.cpy();
            velocity.setZero();
            state = BodyState.Stopped;
        }
    }

    /**
     * Move the ball assigning a force
     */
    public void move(Vector2 force) {
        state = BodyState.Moving;
        addForce(new Vector3(force.x, 0, force.y));
    }

    public Vector2 calculateForce(Vector2 distance, float time) {
        float inverseTime = 1 / time;
        Vector2 oldVelocity = new Vector2(velocity.x, velocity.z);
        Vector2 updatedVelocity = distance.cpy().scl(inverseTime);
        Vector2 updatedAcceleration = updatedVelocity.cpy().sub(oldVelocity).scl(inverseTime);
        return new Vector2(updatedAcceleration.scl(mass));
    }

    public void dispose(){
        model.dispose();
    }

    /**
     * Call the method to know if the ball is stopped
     * @return boolean value
     */
    public boolean isStopped(){
        if(state == BodyState.Stopped)return true;
        else return false;
    }

    public Vector3 getNormal() {
        return normal;
    }
}