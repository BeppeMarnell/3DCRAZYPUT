package com.mygdx.game.WObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Utils.Helper;

public class Club implements InputProcessor {

    private ModelInstance club;
    private Model model;

    //take an instance copy
    private Map map;

    private Vector2 pos;
    public Club(Map map){
        //tree
        ModelLoader loader = new ObjLoader();
        model = loader.loadModel(Gdx.files.internal("club/roofClub.obj"));
        club = new ModelInstance(model);


        float x = Helper.map(Gdx.input.getX(), Gdx.graphics.getWidth(), 0, -80,80);
        float y = Helper.map(Gdx.input.getY(), Gdx.graphics.getHeight(), 0, -56,56);

        club.transform.translate(x,map.getHeight(new Vector2(x, y), 20),y) ;
        club.transform.scl(25f);

        //copy the map
        this.map = map;

        pos = new Vector2(x, y);
    }

    public void render(ModelBatch batch, Environment environment){

        if(Gdx.input.isKeyPressed(Input.Keys.T)){
            float x = Helper.map(Gdx.input.getX(), 0 ,Gdx.graphics.getWidth(), -70,70);
            float y = Helper.map(Gdx.input.getY(), 0 ,Gdx.graphics.getHeight(), -46,46);
            pos = new Vector2(x,y);


            Vector3 oldPos = club.transform.getTranslation(new Vector3());

            //translation for the all the axes from where the ball is now to where the vector "pos" tells
            float translH = map.getHeight(new Vector2(oldPos.x, oldPos.z), 20) - map.getHeight(new Vector2(x, y), 20);
            float translX = pos.x - oldPos.x;
            float translY = pos.y - oldPos.z;

            //in order to move the ball i've to apply the translation amount
            club.transform.translate(translX * 0.007f , 0, translY * 0.007f);
            club.calculateTransforms();

            //render the club
            batch.render(club, environment);
        }
    }

    public void dispose(){
        model.dispose();
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }
}
