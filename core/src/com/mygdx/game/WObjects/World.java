package com.mygdx.game.WObjects;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;

public class World {

    /**
     * class that manages all the physics components in the game
     */

    private Map map;
    private Ball ball;
    private ArrayList<Tree> trees;
    private ArrayList<Wall> walls;

    /**
     * INITIALIZE ALL THE COMPONENTS OF THE MAP
     * @param map
     */
    public World(Map map){
        //copy the map
        this.map = map;

        //generate the rest of the world objects
        //BALL
        //ball = new Ball(map.getHolePos(),map);
        ball = new Ball(new Vector2(5,-5),map);

        //TREES and WALLS
        trees = new ArrayList<>();
        walls = new ArrayList<>();

        for(int i=1; i<map.mapObjects.length-1; i++) {
            for (int j = 1; j < map.mapObjects[0].length-1; j++) {

                if(map.mapObjects[i][j].getType() == WorldObject.ObjectType.Tree)
                    trees.add(new Tree(new Vector2(i,j), map));
                else if(map.mapObjects[i][j].getType() == WorldObject.ObjectType.Wall)
                    walls.add(new Wall(new Vector2(i,j)));
            }
        }

        walls.add(new Wall());
    }

    public void update(float deltaTime){
        ball.update(deltaTime);
    }

    public void render(ModelBatch batch, Environment environment){
        //render the map
        map.render(batch);

        //render the ball
        ball.render(batch, environment);

        //render the walls
        for(Wall w: walls) w.render(batch, environment);

        //render the trees
        for(Tree t: trees) t.render(batch, environment);
    }

    public void dispose(){
        ball.dispose();
        for(Wall w: walls) w.dispose();
        for(Tree t: trees) t.dispose();
    }
}
