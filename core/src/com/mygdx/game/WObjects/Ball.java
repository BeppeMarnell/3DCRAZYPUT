package com.mygdx.game.WObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Ball {
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
    private Vector3 pos; // ( x , z, y ) think the position like this

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

        pos = new Vector3(initPos.x, map.getHeight(new Vector2(initPos.x,initPos.y), Ball.RAD), initPos.y);
        ballInstance.transform.translate(pos);

        //copy the instance of the map
        this.map = map;

        //set the ball state
        state = BallState.Stopped;

        //initialize velocity and acceleration
        linearVelocity = new Vector2();
        acceleration = new Vector2();
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

        //get the position of the ball
        pos = ballInstance.transform.getTranslation(new Vector3());

        //move the ball with keys
        moveByKeys();

        /* i've tried to add this methods but them didn't work so much

        //move the ball
        linearVelocity.add(acceleration.cpy().scl(deltaTime));
        linearVelocity.add(acceleration.cpy().scl(2 * deltaTime));

        pos.add(linearVelocity.x *deltaTime,0,linearVelocity.y *deltaTime );

        //set the acceleration to zero
        acceleration.set(new Vector2(0,0));

        //stop the ball
        if (Math.abs(linearVelocity.x) < 5 && Math.abs(linearVelocity.y) < 5) linearVelocity.set(0,0);*/

        //calculate the ball height in which the ball is
        Vector3 newPos = ballInstance.transform.getTranslation(new Vector3());

        //translation for the y axis
        float translH = map.getHeight(new Vector2(newPos.x, newPos.z), RAD) - map.getHeight(new Vector2(pos.x, pos.z), RAD);
        ballInstance.transform.translate(0, translH, 0);
        ballInstance.calculateTransforms();

        // print out the position of the ball
        //System.out.println(pos.x + " " + pos.y +" " + pos.z + " height: "+ map.getHeight(new Vector2(pos.x, pos.z), RAD));
    }

    /**
     * Move the ball by using the keyboards
     */
    private void moveByKeys(){
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

            ballInstance.transform.translate(-1, 0,0);
            ballInstance.calculateTransforms();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            ballInstance.transform.translate(1f, 0, 0);
            ballInstance.calculateTransforms();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            ballInstance.transform.translate(0, 0, 1f);
            ballInstance.calculateTransforms();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            ballInstance.transform.translate(0, 0, -1f);
            ballInstance.calculateTransforms();
        }

        if(Gdx.input.isKeyPressed(Input.Keys.T)) {
            ballInstance.transform.translate(0, -1, 0);
            ballInstance.calculateTransforms();
        }


        if(Gdx.input.isKeyPressed(Input.Keys.G)) {
            ballInstance.transform.translate(0, 1, 0);
            ballInstance.calculateTransforms();
        }
    }

    public void dispose(){
        model.dispose();
    }

    public Vector3 getPos() {
        return pos;
    }
}
