package com.mygdx.game.WObjects;

import com.badlogic.gdx.math.Vector2;

public class WorldObject {

    public enum ObjectType{
        Grass, Dirt, Sand, Water, Wall, Tree , Ball, Hole,
    }

    private ObjectType type;

    private Vector2 pos;

    private float height;

    public WorldObject(ObjectType type, Vector2 pos, float height){
        this.type = type;
        this.pos = pos;
        this.height = height;
    }

    public float getHeight() {
        return height;
    }

    public Vector2 getPos() {
        return pos;
    }

    public ObjectType getType() {
        return type;
    }
}
