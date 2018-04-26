package com.mygdx.game.WObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Utils.Helper;

public class Tree {

    private ModelInstance tree;
    private Model model;

    /**
     * Create a little tree in a specific position of the map
     * @param pos the position values has to be between 0-19 and 0-13
     * @param map pass a copy of the map
     */
    public Tree(Vector2 pos, Map map){
        //tree
        ModelLoader loader = new ObjLoader();
        model = loader.loadModel(Gdx.files.internal("tree/Tree low.obj"));// set material color to white
        model.materials.get(0).set(ColorAttribute.createDiffuse(Color.OLIVE));
        model.materials.get(1).set(ColorAttribute.createDiffuse(Color.BROWN));
        tree = new ModelInstance(model);

        //find the exact position in the map
        Vector2 translPos = new Vector2();
        translPos.x = Helper.map(pos.x, 0, 20,-80, 80);
        translPos.y = Helper.map(pos.y, 0, 14,-56, 56);

        tree.transform.translate(translPos.x + 4f,
                map.getHeight(new Vector2(translPos.x + 4f,translPos.y+ 4f), -0.5f),translPos.y + 4f) ;
        tree.transform.scl(0.1f);
    }

    public void render(ModelBatch batch, Environment environment){
        batch.render(tree, environment);
    }

    public void dispose(){
        model.dispose();
    }
}
