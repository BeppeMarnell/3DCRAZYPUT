package com.mygdx.game.WObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.BoundingSphere;

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

    private boolean debugMode = false;
    public static final float RAD = 1f;
    public static final float MASS = 1f; // kg
    public static final float ELASTICITY = 0.4f;


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

    public void dispose(){
        model.dispose();
    }
}