package com.mygdx.game.WObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.loader.ObjLoader;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Utils.Helper;

public class Club {

    private ModelInstance club;
    private Model model;

    //take an instance copy
    private Map map;

    //set the throw game screen
    public boolean throwMode = false;
    public boolean canDraw = false;
    public boolean orientationSet = false;
    public boolean down = false;

    private Vector3 hitForce;
    private Vector3 direction;
    private Vector3 pos;
    private Vector3 orientationVector;
    private Vector3 rotationVector;
    private Vector3 ballPos;


    public Club(Map map){
        //tree
        ModelLoader loader = new ObjLoader();
        model = loader.loadModel(Gdx.files.internal("club/roofClub.obj"));
        club = new ModelInstance(model);

        club.transform.translate(0,map.getHeight(new Vector2(0,0), 17),0) ;
        club.transform.scl(25f);

        //copy the map
        this.map = map;

        pos = new Vector3();

        hitForce = new Vector3();
        direction = new Vector3();
        orientationVector = new Vector3();
        rotationVector = new Vector3(0, 0, 1);
        ballPos = new Vector3();

    }

    public void render(ModelBatch batch, Environment environment, Ball ball){
        ballPos = ball.getPosition().cpy();

        //render and update only if the key T is pressed
        if(Gdx.input.isKeyPressed(Input.Keys.T)){
            //set the throw mode true
            throwMode = true;


            float x = Helper.map(Gdx.input.getX(),0, Gdx.graphics.getWidth(), -70,70);
            float y = Helper.map(Gdx.input.getY(), 0 ,Gdx.graphics.getHeight(), -46,46);
            pos.x = x; pos.y = y;

            club.transform.setTranslation(pos.x,  map.getHeight(new Vector2(pos.x, pos.y), 17), pos.y);
            club.calculateTransforms();

            //render the club
            batch.render(club, environment);

            //call the method to throw the ball
            if(ball.isStopped()) {
                lunchBall(ball);
            }

        }else {
            throwMode = false;
            orientationSet = false;
            canDraw = false;
            hitForce.setZero();
        }
    }

    private void lunchBall(Ball ball){
        Circle clubC = new Circle(pos.x,pos.y, 2.4f);

        Circle ballC = new Circle(ball.getPosition().cpy().x,ball.getPosition().cpy().z,1 );

        if(clubC.overlaps(ballC)){
            if (!orientationSet) pickOrientation();

            if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                orientationSet = true;
                canDraw = true;
                direction = orientationVector.cpy().nor().scl(-1);

                if (hitForce.len() > 500) down = true;
                else if (hitForce.len() < 30) down = false;

                if (!down) hitForce.add(new Vector3(direction.x, 0, direction.y).scl(10));
                else hitForce.add(new Vector3(direction.x, 0, direction.y).scl(-10));
                System.out.println("=============: " + hitForce);





                if(Gdx.input.justTouched()) {
                    canDraw = false;
                    orientationSet = false;
                    ball.setIsHit(true);
                    ball.hitForce.set(hitForce.cpy());
                }

            }

        }
    }

    private void pickOrientation() {
        System.out.println("ROTATING ============================");
        rotationVector.rotate(new Vector3(0, 1, 0), 5);
        orientationVector = ballPos.cpy().add(rotationVector.cpy().scl(10));
    }


    public void drawPowerBar(ShapeRenderer rectangleRenderer) {
        if (canDraw) {
            Color c = new Color(Color.GREEN);
            if (hitForce.len() > 100) c = Color.YELLOW;
            if (hitForce.len() > 200) c = Color.RED;

            Helper.DrawRectangle(c, rectangleRenderer, pos.x, pos.z, pos.y, 4, hitForce.len()/30, 2 );
        }
        if (!canDraw && throwMode) {
            Helper.DrawDebugLine(ballPos, orientationVector, 4, Color.RED, rectangleRenderer);
        }
        Helper.DrawDebugLine(ballPos, ballPos.cpy().add(new Vector3(direction.x, 0, direction.y).cpy().scl(3)), rectangleRenderer);

    }

    public void dispose(){
        model.dispose();
    }
}
