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
import com.badlogic.gdx.math.Vector3;

public class Tree {

    private ModelInstance tree;
    private Model model;

    public Tree(Vector3 pos, Map map){
        //tree
        ModelLoader loader = new ObjLoader();
        model = loader.loadModel(Gdx.files.internal("tree/Tree low.obj"));// set material color to white
        model.materials.get(0).set(ColorAttribute.createDiffuse(Color.OLIVE));
        model.materials.get(1).set(ColorAttribute.createDiffuse(Color.BROWN));
        tree = new ModelInstance(model);
        tree.transform.translate(pos.x, map.getHeight(new Vector2(pos.x,pos.y), -0.5f),pos.z) ;
        tree.transform.scl(0.1f);
    }

    public void render(ModelBatch batch, Environment environment){
        batch.render(tree, environment);
    }

    public void dispose(){
        model.dispose();
    }
}
