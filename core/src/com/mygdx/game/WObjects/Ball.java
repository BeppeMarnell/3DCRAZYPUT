package com.mygdx.game.WObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Ball {
    private int iter = 0;

    /**
     * IMPORTANT
     * Because the map is over the y axis,
     * the game axes are :
     * x axis = x
     * y axis = z
     * z axis = y
     */

    //enum to set up the world state
    public enum BallState {
        Moving, Stopped,
    }
    //Ball state
    private BallState state;

    private Model model;
    private ModelInstance ballInstance;
    //get a copy of the map
    private Map map;

    //position of the ball (center of the object)
    private Vector2 pos;

    //linear velocity of the body
    private Vector2 linearVelocity;

    //acceleration
    private Vector2 acceleration;

    //mass
    private float mass;

    //radius
    public static final float RAD = 2.5f;

    /**
     * Initialize the ball 3d and add the position to it
     * @param initPos
     */
    public Ball(Vector2 initPos, Map map){

        //create the ball object
        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createSphere(5f, 5f, 5f,15,15,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.TextureCoordinates);
        ballInstance = new ModelInstance(model);

        pos = new Vector2(initPos.x, initPos.y);
        ballInstance.transform.translate(pos.x, map.getHeight(new Vector2(initPos.x,initPos.y), Ball.RAD), pos.y);

        //copy the instance of the map
        this.map = map;

        //set the ball state
        state = BallState.Stopped;

        //initialize velocity and acceleration
        linearVelocity = new Vector2();
        acceleration = new Vector2();

        //initialize the mass of the ball
        mass = 2f;
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
        moveByKeys();

        //move the ball adding all the velocity and acceleration
        pos.add(linearVelocity.cpy().scl(deltaTime));
        linearVelocity.add(acceleration.cpy().scl(deltaTime)); // add delta time

        //set the acceleration to zero
        acceleration.set(new Vector2(0,0));

        //stop the ball and set the state of the ball
        if (Math.abs(linearVelocity.x) < 1 && Math.abs(linearVelocity.y) < 1){
            linearVelocity.set(0,0);
            state = BallState.Stopped;
        }else state = BallState.Moving;


        if(state == BallState.Moving){
            linearVelocity.scl(map.getFriction(pos));
        }

        move3DBall();

        // print out the position of the ball
        if (iter >20){
            System.out.println(" height: "+ map.getHeight(new Vector2(pos.x, pos.y), RAD) + " vel: " + linearVelocity.toString());
            iter = 0;
        }else{
            iter++;
        }
    }

    /**
     * This method moves the position of the 3D instance, do not change it
     */
    private void move3DBall(){
        //Apply the physic to the 3D object
        Vector3 oldPos = ballInstance.transform.getTranslation(new Vector3());

        //translation for the all the axes from where the ball is now to where the vector "pos" tells
        float translH = map.getHeight(new Vector2(oldPos.x, oldPos.z), RAD) - map.getHeight(new Vector2(pos.x, pos.y), RAD);
        float translX = pos.x - oldPos.x;
        float translY = pos.y - oldPos.z;

        //in order to move the ball i've to apply the translation amount
        ballInstance.transform.translate(translX, -translH , translY);
        ballInstance.calculateTransforms();
    }

    /**
     * Move the ball by using the keyboards
     */
    private void moveByKeys(){
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            linearVelocity.add(-5,0);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            linearVelocity.add(5,0);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            linearVelocity.add(0,5);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            linearVelocity.add(0,-5);
        }
    }

    public void dispose(){
        model.dispose();
    }

    public Vector2 getPos() {
        return pos;
    }

    public void setLinearVelocity(Vector2 linearVelocity) {
        this.linearVelocity = linearVelocity;
    }

    public BallState getState() {
        return state;
    }
}
