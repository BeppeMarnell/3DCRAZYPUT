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
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.BoundingBox;
import com.mygdx.game.Physics.BoundingSphere;
import com.mygdx.game.Utils.Helper;

public class Club {

    private ModelInstance club;
    private Model model;

    //take an instance copy
    private Map map;

    //set the throw game screen
    public boolean throwMode = false;

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
    }

    public void render(ModelBatch batch, Environment environment, Ball ball){

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

            //call the method to throw the ball
            if(ball.isStopped()) lunchBall(ball);

        }else throwMode = false;
    }

    private void lunchBall(Ball ball){
        Circle clubC = new Circle(pos.x,pos.y, 2.4f);

        Circle ballC = new Circle(ball.getPosition().cpy().x,ball.getPosition().cpy().z,1 );

        if(clubC.overlaps(ballC)){
            //calculate direction and throw the ball
            Vector2 dir = new Vector2(new Vector2(ball.getPosition().cpy().x,ball.getPosition().cpy().z).sub(pos)).scl(2000f);

            ball.move(dir);
        }
    }

    public void dispose(){
        model.dispose();
    }
}
