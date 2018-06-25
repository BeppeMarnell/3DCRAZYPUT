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

    private boolean debugMode = true;

    //radius
    public static final float RAD = 1f;
    public static final float MASS = 1f; // kg
    public static final float ELASTICITY = 0.4f;

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
            model = modelBuilder.createSphere(RAD * 2, RAD * 2, RAD * 2,15,15,
                    new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                    VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal |
                            VertexAttributes.Usage.TextureCoordinates);
        else
            model = modelBuilder.createSphere(RAD * 2, RAD * 2, RAD * 2,15,15, GL20.GL_LINES,
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
        ballInstance.transform.setTranslation(position);
        ballInstance.calculateTransforms();
        batch.render(ballInstance, environment);
    }

    /**
     * Update the position of the ball
     */
    public void update(float deltaTime){
        //move the ball with keys
//        if (state == BodyState.Moving) {
//            move3DBall();
//            integrate(deltaTime);
//        }

//        moveByKeys();


    }

    /**
     * This method moves the position of the 3D instance, do not change it
     */
    private void move3DBall(){
        //Apply the physic to the 3D object

        frontPos = position.cpy().add(0, 0, 3);
        sidePos = position.cpy().add(3, 0, 0);
        frontPos.y = map.getHeight(new Vector2(frontPos.x, frontPos.z), RAD);
        sidePos.y = map.getHeight(new Vector2(sidePos.x, sidePos.z), RAD);
        float height = map.getHeight(new Vector2(position.x, position.z), RAD);

        if (position.y - height > 1) {
//            state = BodyState.Flying;
            updateGravity();
        }

        if (position.y < height) {
            position.y = height;
//            state = BodyState.Moving;
        }

        mu = map.getFriction(new Vector2(position.x, position.z));
        kineticMu = mu - 0.2f;

        //in order to move the ball i've to apply the translation amount
        ballInstance.transform.setTranslation(position);
        ballInstance.calculateTransforms();
        Vector3 axisOfRotation = velocity.cpy().crs(new Vector3(0, -1, 0));
//        ballInstance.transform.rotate(axisOfRotation, 30);
    }

    /**
     * Move the ball assigning a force
     */
    public void move(Vector2 force) {
        state = BodyState.Moving;
        addForce(new Vector3(force.x, 0, force.y));
    }

    public void dispose(){
        model.dispose();
    }

    /**
     * Call the method to know if the ball is stopped
     * @return boolean value
     */
}