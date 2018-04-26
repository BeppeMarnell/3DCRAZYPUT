package com.mygdx.game.WObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class Wall {

    private ModelInstance wall;
    private Model model;

    public Wall(Vector3 pos, Map map){

        ModelBuilder modelBuilder = new ModelBuilder();
        model = modelBuilder.createBox(8f, 15f, 8f,
                new Material(ColorAttribute.createDiffuse(Color.DARK_GRAY)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
        wall = new ModelInstance(model);
        wall.transform.translate(pos.x, map.getHeight(new Vector2(pos.x,pos.y), 5f),pos.z) ;
    }

    public void render(ModelBatch batch, Environment environment){
        batch.render(wall, environment);
    }

    public void dispose(){
        model.dispose();
    }
}
