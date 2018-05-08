package com.mygdx.game.WObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Physics.Obstacle;
import com.mygdx.game.Utils.Helper;


public class Wall extends Obstacle {
    public final static float ELASTICITY = 0.2f;
    public final static float MASS = 10f;

    private ModelInstance wall;
    private Model model;

    //size of the wall
    float size = 8f;

    private Vector2 max, min;
    private Vector2 position;
    private Vector2 normal;

    /**
     * generates a wall in a specific position in the map
     * @param pos the position values has to be between 0-19 and 0-13
     */
    public Wall(Vector2 pos){
        super(MASS, pos);

        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(8f, 18f, 8f, new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        wall = new ModelInstance(model);

        //find the exact position in the map
        Vector2 translPos = new Vector2();
        translPos.x = Helper.map(pos.x, 0, 20,-80, 80);
        translPos.y = Helper.map(pos.y, 0, 14,-56, 56);

        translPos.x += 4f;
        translPos.y += 4f;

        wall.transform.translate(translPos.x, 5,translPos.y);

        max = new Vector2(translPos.x + size / 2, translPos.y + size / 2);
        min = new Vector2(translPos.x - size / 2, translPos.y - size / 2);
        position = new Vector2(translPos.cpy());
        normal = new Vector2();
    }

    public Wall(Vector2 pos, float[] size){
        super(MASS, pos, size);

        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(size[0], size[1], size[2], new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        wall = new ModelInstance(model);

        //find the exact position in the map
        wall.transform.translate(pos.x,5, pos.y);

        max = new Vector2(pos.x + size[0] / 2, pos.y + size[2] / 2);
        min = new Vector2(pos.x - size[0] / 2, pos.y - size[2] / 2);
        position = new Vector2(pos.cpy());
        normal = new Vector2();
    }

    public void render(ModelBatch batch, Environment environment){
        batch.render(wall, environment);
    }

    public void dispose(){
        model.dispose();
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

    public Vector2 getNormal() {
        return normal;
    }
}
