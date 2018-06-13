package com.mygdx.game.WObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;

public class Hole {
    /**
     * Interpret the hole as a rectangle ( box ), and not as the little tree
     */

    private ModelInstance hole;
    private Model model;

    /**
     * Create a hole in a specific position of the map
     * @param map pass a copy of the map
     */
    public Hole(Map map){
        //tree
        ModelLoader loader = new ObjLoader();
        model = loader.loadModel(Gdx.files.internal("hole/flagBlue.obj"));
        hole = new ModelInstance(model);

        hole.transform.translate(map.getHolePosTranslV2().x, map.getHeight(map.getHolePosTranslV2(), -0.5f),map.getHolePosTranslV2().y ) ;
        hole.transform.scl(2f);
    }

    public void render(ModelBatch batch, Environment environment){
        batch.render(hole, environment);
    }

    public void dispose(){
        model.dispose();
    }
}
