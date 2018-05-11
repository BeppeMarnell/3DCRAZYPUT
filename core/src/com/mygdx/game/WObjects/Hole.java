package com.mygdx.game.WObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Utils.Helper;

public class Hole {
    /**
     * Interpret the hole as a rectangle ( box ), and not as the little tree
     */

    private ModelInstance hole;
    private Model model;

    /**
     * Create a hole in a specific position of the map
     * @param pos the position values has to be between 0-19 and 0-13
     * @param map pass a copy of the map
     */
    public Hole(Vector2 pos, Map map){
        //tree
        ModelLoader loader = new ObjLoader();
        model = loader.loadModel(Gdx.files.internal("hole/flagBlue.obj"));
        hole = new ModelInstance(model);

        //find the exact position in the map
        Vector2 translPos = new Vector2();

        translPos.x = Helper.map(pos.x, 1, 18,-72, 64);
        translPos.y = Helper.map(pos.y, 1, 12,40, -48);

        hole.transform.translate(translPos.x +4f,
                map.getHeight(new Vector2(translPos.x+4 ,translPos.y+4), -0.5f),translPos.y +4f ) ;
        hole.transform.scl(2f);
    }

    public void render(ModelBatch batch, Environment environment){
        batch.render(hole, environment);
    }

    public void dispose(){
        model.dispose();
    }
}
