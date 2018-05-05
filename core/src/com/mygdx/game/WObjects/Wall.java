package com.mygdx.game.WObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Utils.Helper;

import java.util.ArrayList;

public class Wall {

    //static variables
    public final static float ELASTICITY = 0.8f;
    public final static float MASS = 20f;

    //3d instances
    private ModelInstance wall;
    private Model model;

    //size of the wall
    float size = 8f;

    private Vector2 max, min;
    private Vector2 position;
    private Vector2 normal;
    private float penetration;

    /**
     * generates a wall in a specific position in the map
     * @param pos the position values has to be between 0-19 and 0-13
     */
    public Wall(Vector2 pos){

        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(8f, 18f, 8f, new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        wall = new ModelInstance(model);

        //find the exact position in the map
        Vector2 translPos = new Vector2();
        translPos.x = Helper.map(pos.x, 0, 20,-80, 80) + 4f;
        translPos.y = Helper.map(pos.y, 0, 14,-56, 56) + 4f;

        wall.transform.translate(translPos.x, 5,translPos.y);

        max = new Vector2(translPos.x + size / 2, translPos.y + size / 2);
        min = new Vector2(translPos.x - size / 2, translPos.y - size / 2);

        //set position, penetration and the normal
        position = new Vector2(translPos.cpy());
        normal = new Vector2();
        penetration = 0;
    }

    public Wall(Vector2 pos, float[] size){

        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(size[0], size[1], size[2], new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        wall = new ModelInstance(model);
        wall.transform.translate(pos.x,5, pos.y);

        max = new Vector2(pos.x + size[0] / 2, pos.y + size[2] / 2);
        min = new Vector2(pos.x - size[0] / 2, pos.y - size[2] / 2);

        //set position, penetration and the normal
        position = new Vector2(pos.cpy());
        normal = new Vector2();
        penetration = 0;
    }

    /**
     * Constructor to build the around walls
     */
    private ArrayList<ModelInstance> boxWalls;
    private Wall[] border;
    private boolean boxMode = false;
    public Wall(){
        boxWalls = new ArrayList<>();
        border = new Wall[4];

        //first wall up

        border[0] = new Wall(new Vector2(0, 56-4), new float[]{160f, 15f, 8f});
        //first wall up

        border[1] = new Wall(new Vector2(-(80-4), 0), new float[]{8f, 15f, 96f});

        //first wall up

        border[2] = new Wall(new Vector2((80-4), 0), new float[]{8f, 15f, 96f});

        //first wall up

        border[3] = new Wall(new Vector2(0, -(56-4)), new float[]{160f, 15f, 8f});

        boxMode = true;
    }

    public void render(ModelBatch batch, Environment environment){
        if(!boxMode) batch.render(wall, environment);
        else for (Wall mI: border) mI.render(batch, environment);
    }

    public void dispose(){
        if(!boxMode) model.dispose();
    }

    public Vector2 getMax() {
        return max;
    }

    public Vector2 getMin() {
        return min;
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setNormal(Vector2 normal) {
        this.normal = normal;
    }

    public void setPenetration(float penetration) {
        this.penetration = penetration;
    }

    public Vector2 getNormal() {
        return normal;
    }

    public float getPenetration() {
        return penetration;
    }

    public Wall[] getBorder() {
        return border;
    }
}
