package com.mygdx.game.WObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Utils.Helper;

public class Club{

    private ModelInstance club;
    private Model model;

    //take an instance copy
    private Map map;

    //set the throw game screen
    public boolean throwMode = false;

    //touching
    private boolean touching;
    private Vector2 iniTouch;
    private Vector2 finTouch;

    private Vector2 pos;
    public Club(Map map){
        //tree
        ModelLoader loader = new ObjLoader();
        model = loader.loadModel(Gdx.files.internal("club/roofClub.obj"));
        club = new ModelInstance(model);

        club.transform.translate(0,map.getHeight(new Vector2(0,0), 17),0) ;
        club.transform.scl(25f);

        //copy the map
        this.map = map;

        pos = new Vector2();

        touching = false;
    }

    public void render(ModelBatch batch, Environment environment){

        //render and update only if the key T is pressed
        if(Gdx.input.isKeyPressed(Input.Keys.T)){
            //set the throw mode true
            throwMode = true;

            float x = Helper.map(Gdx.input.getX(), 0 ,Gdx.graphics.getWidth(), -70,70);
            float y = Helper.map(Gdx.input.getY(), 0 ,Gdx.graphics.getHeight(), -46,46);
            pos.x = x; pos.y = y;

            club.transform.setTranslation(pos.x,  map.getHeight(new Vector2(pos.x, pos.y), 17), pos.y);
            club.calculateTransforms();

            //render the club
            batch.render(club, environment);

        }else throwMode = false;
    }

    /**
     * manage the touch gesture
     */
    public void touchDrag(){

        if(Gdx.input.isTouched()){
            touching = true;


        }else if(!Gdx.input.isTouched()){
            iniTouch = new Vector2();
            finTouch = new Vector2();
            touching = false;
        }
    }

    public void dispose(){
        model.dispose();
    }
}
