package com.mygdx.game.WObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Utils.Helper;


public class Wall extends Obstacle {
    public final static Vector3 VELOCITY = new Vector3(0, 0, 0);
    public final static float ELASTICITY = 0.8f;
    public final static float MASS = 100f;

    private ModelInstance wall;
    private Model model;

    /**
     * generates a wall in a specific position in the map
     * @param pos the position values has to be between 0-19 and 0-13
     */
    public Wall(Vector3 pos, Vector3 size, Color c, boolean isBorder){
        super(pos, size, MASS, ELASTICITY, VELOCITY);

        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(size.x, size.y, size.z, new Material(ColorAttribute.createDiffuse(c)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        wall = new ModelInstance(model);

        position = pos.cpy();
        position.y = 7.5f;

        //find the exact position in the map
        if (!isBorder) {
            position.x = Helper.map(pos.x, 0, 20,-80, 80);
            position.x += 4f;
            position.z = Helper.map(pos.z, 0, 14,-56, 56);
            position.z += 4f;
            position.y = size.y * 0.5f;// + 4f;
        }
        wall.transform.translate(position);
    }

    public void render(ModelBatch batch, Environment environment){
        batch.render(wall, environment);
    }

    public void dispose(){
        model.dispose();
    }

}
