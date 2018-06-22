package com.mygdx.game.Bott;

import com.badlogic.gdx.math.Vector2;

public class MoveTo{
    public int iter;
    public Vector2 to;

    public MoveTo(Vector2 to, int iter){
        this.iter = iter;
        this.to = to;
    }
}
