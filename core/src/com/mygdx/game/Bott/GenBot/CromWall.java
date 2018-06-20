package com.mygdx.game.Bott.GenBot;

import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;

public class CromWall {
    //set position inside it
    private Rectangle rec;
    private final float width = 32;

    public CromWall(float x, float y){
        //set a rectangle
        this.rec = new Rectangle(x,y,width,width);
        this.rec.setCenter(x + width/2 ,y + width/2);
    }

    public boolean isColliding(Circle circle){
        return this.rec.contains(circle);
    }

    public Rectangle getRec() {
        return rec;
    }
}
