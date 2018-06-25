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

    public float getFriction(){
        /**
         * 2 Grass terrain, friction 0.99f;
         * 3 Dirt terrain, friction 0.97f;
         * 4 Sand terrain, friction 0.95f;
         */

        if (type == ObjectType.Grass) return 0.99f;
        else if(type == ObjectType.Dirt) return 0.97f;
        else if(type == ObjectType.Sand) return 0.95f;
//        if (type == ObjectType.Grass) return 0.8f;
//        else if(type == ObjectType.Dirt) return 0.85f;
//        else if(type == ObjectType.Sand) return 0.9f;
        else return 1f;
    }
}
