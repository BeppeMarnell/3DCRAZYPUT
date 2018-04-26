package com.mygdx.game.WObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Utils.Helper;

import java.util.ArrayList;

public class Wall {

    private ModelInstance wall;
    private Model model;

    /**
     * generates a wall in a specific position in the map
     * @param pos the position values has to be between 0-19 and 0-13
     */
    public Wall(Vector2 pos){

        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(8f, 15f, 8f,
                new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        wall = new ModelInstance(model);

        //find the exact position in the map

        Vector2 translPos = new Vector2();
        translPos.x = Helper.map(pos.x, 0, 20,-80, 80);
        translPos.y = Helper.map(pos.y, 0, 14,-56, 56);

        wall.transform.translate(translPos.x +4f, +8,translPos.y +4f);
    }

    /**
     * Costructor to build the around walls
     */
    private ArrayList<ModelInstance> boxWalls;
    private boolean boxMode = false;
    public Wall(){
        boxWalls = new ArrayList<>();

        //first wall up
        ModelBuilder modelBuilder = new ModelBuilder();
        Model model1 = modelBuilder.createBox(160f, 15f, 8f,
                new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        boxWalls.add(new ModelInstance(model1));
        boxWalls.get(0).transform.translate(0,0,(56-4));

        //first wall up
        Model model2 = modelBuilder.createBox(8f, 15f, 96f,
                new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        boxWalls.add(new ModelInstance(model2));
        boxWalls.get(1).transform.translate(-(80-4),0,0);

        //first wall up
        Model model3 = modelBuilder.createBox(8f, 15f, 96f,
                new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        boxWalls.add(new ModelInstance(model3));
        boxWalls.get(2).transform.translate((80-4),0,0);

        //first wall up
        Model model4 = modelBuilder.createBox(160f, 15f, 8f,
                new Material(ColorAttribute.createDiffuse(Color.LIGHT_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        boxWalls.add(new ModelInstance(model4));
        boxWalls.get(3).transform.translate(0,0,-(56-4));

        boxMode = true;
    }

    public void render(ModelBatch batch, Environment environment){
        if(!boxMode) batch.render(wall, environment);
        else for (ModelInstance mI: boxWalls) batch.render(mI, environment);
    }

    public void dispose(){
        model.dispose();
    }
}
