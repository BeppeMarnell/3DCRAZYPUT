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
import com.mygdx.game.Physics.BoundingSphere;

public class Ball extends BoundingSphere {
    private int iter = 0;

    private static final float G = 9.81f;

    /**
     * IMPORTANT
     * Because the map is over the y axis,
     * the game axes are :
     * x axis = x
     * y axis = z
     * z axis = y
     */

    //enum to set up the world state
    public enum BallState { Moving, Stopped, }
    public enum MovingState { Up, Down, Straight, }
    //Ball state
    private BallState state;
    private MovingState movement;

    private Model model;
    private ModelInstance ballInstance;
    //get a copy of the map
    private Map map;

    //radius
    public static final float RAD = 2.5f;
    public static final float MASS = 2f;
    public static final float ELASTICITY = 0.6f;
    public static final float MAX_VELOCITY = 94;

    /**
     * Initialize the ball 3d and add the position to it
     * @param initPos
     */
    public Ball(Vector2 initPos, Map map){
//        super(new Vector3(initPos.x, map.getHeight(initPos, RAD), initPos.y), MASS, map);
        super(new Vector3(initPos.x, map.getHeight(initPos, RAD), initPos.y), MASS, RAD);

        //create the ball object
        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createSphere(2 * RAD, 2 * RAD, 2 * RAD,15,15,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.TextureCoordinates);
        ballInstance = new ModelInstance(model);

//        pos = new Vector2(initPos.x, initPos.y);
        ballInstance.transform.translate(initPos.x, map.getHeight(new Vector2(initPos.x,initPos.y), RAD), initPos.y);

        //copy the instance of the map
        this.map = map;

        //set the ball state
        state = BallState.Stopped;
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

        if (Math.abs(velocity.x) < 0.05 && Math.abs(velocity.z) < 0.05) {
            velocity.setZero();
            state = BallState.Stopped;
        } else state = BallState.Moving;

        move3DBall();

        if (state == BallState.Moving) {
            switch(movement) {
                case Up:
                    addForce(velocity.cpy().nor().scl(-1f * G * position.y));
//                    totalForce.set(velocity.nor().scl(-1f*G*position.y));
                    break;
                case Down:
                    addForce(velocity.cpy().nor().scl(G * position.y));
//                    totalForce.set(velocity.nor().scl(G*position.y));
                    break;
                case Straight:
//                    updateForces();
                    addForce(velocity.cpy().nor().scl(-1));
                    break;
                default:
                    break;
            }
//            updateForces();
            integrate(deltaTime, map.getFriction(new Vector2(position.x, position.z)));
        }

        // print out the position of the ball
        if (iter >20){
//            System.out.println(" height: "+ map.getHeight(new Vector2(pos.x, pos.y), RAD) + " vel: " + linearVelocity.toString());
            System.out.println(" height: "+ map.getHeight(new Vector2(position.x, position.z), RAD) + " vel: " + velocity.toString());
            iter = 0;
        }else{
            iter++;
        }
    }

    /**
     * This method moves the position of the 3D instance, do not change it
     */
    private void move3DBall(){
//        Apply the physic to the 3D object
        Vector3 oldPos = ballInstance.transform.getTranslation(new Vector3());
        position.y = map.getHeight(new Vector2(position.x, position.z), RAD);

        if (oldPos.y > position.y) {
            movement = MovingState.Down;
        } else if (oldPos.y < position.y) {
            movement = MovingState.Up;
        } else {
            movement = MovingState.Straight;
        }

        //in order to move the ball i've to apply the translation amount
        ballInstance.transform.setTranslation(position);
        ballInstance.calculateTransforms();
        setPosition(position);
    }

    /**
     * Move the ball by using the keyboards
     */
    private void moveByKeys(){
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            velocity.add(-5,0, 0);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            velocity.add(5,0, 0);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            velocity.add(0,0, 5);
        }

        if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
            velocity.add(0,0, -5);
        }
    }


    public void dispose(){
        model.dispose();
    }
}
