package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Utils.Helper;
import com.mygdx.game.WObjects.Ball;
import com.mygdx.game.WObjects.Map;

public class Bot {

    private Map map;
    private Ball ball;

    public Bot(Map map , Ball ball){
        this.ball = ball;
        this.map = map;
    }

    public void render(float deltaTime){

        if(Gdx.input.isKeyJustPressed(Input.Keys.P))
            throwBall(deltaTime);
    }


    public void throwBall(float deltaTime){
        Vector2 ballpos = new Vector2(ball.getPos().cpy());
        Vector2 holePos = new Vector2();
        holePos.x = Helper.map(map.getHolePos().x, 0,20,-80, 80);
        holePos.y = Helper.map(map.getHolePos().y, 0,14,-56, 56);

        Vector2 dir = holePos.sub(ballpos);
        float amount = holePos.dst(ballpos);

        ball.setLinearVelocity(dir.cpy().scl(amount*deltaTime*10));
    }
}
