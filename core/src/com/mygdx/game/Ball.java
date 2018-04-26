package com.mygdx.game;

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
    private Vector3 pos;

    //get a copy of the map
    private Map map;

    /**
     * Initialize the ball 3d and add the position to it
     * @param initPos
     */
    public Ball(Vector3 initPos, Map map){

        //create the ball object
        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createSphere(5f, 5f, 5f,15,15,
                new Material(ColorAttribute.createDiffuse(Color.WHITE)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal |
                        VertexAttributes.Usage.TextureCoordinates);
        ballInstance = new ModelInstance(model);
        ballInstance.transform.translate(initPos);

        pos = new Vector3(initPos.cpy());

        this.map = map;
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

        //calculate the ball height in which the ball is
        Vector3 newPos = ballInstance.transform.getTranslation(new Vector3());
        float translH = map.getHeigth(new Vector2(newPos.x, newPos.z)) - map.getHeigth(new Vector2(pos.x, pos.z));
        ballInstance.transform.translate(0, translH, 0);
        ballInstance.calculateTransforms();

        //System.out.println(pos.x + " " + pos.y +" " + pos.z + " height: "+ map.getHeigth(new Vector2(pos.x, pos.z)));
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

}
