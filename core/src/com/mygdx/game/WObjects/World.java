package com.mygdx.game.WObjects;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.CollisionDetector;

import java.util.ArrayList;

public class World {

    /**
     * class that manages all the physics components in the game
     */

    private Map map;
    private Ball ball;
    private Hole hole;
    private ArrayList<Tree> trees;
    private ArrayList<Wall> walls;
    private Wall[] borders;
    private CollisionDetector collisionDetector;
    
    private Club club;

    /**
     * INITIALIZE ALL THE COMPONENTS OF THE MAP
     * @param map
     */
    public World(Map map){
        //copy the map
        this.map = map;

        //generate the rest of the world objects

        //BALL
        ball = new Ball(map.getBallPos(),map);

        //Hole
        hole = new Hole(map.getHolePos(), map);

        //TREES and WALLS
        trees = new ArrayList<>();
        walls = new ArrayList<>();
        
        //create the club
        club = new Club(map);

        //add the surrounding walls
        generateBorders(walls);

        for(int i=1; i<map.mapObjects.length-1; i++) {
            for (int j = 1; j < map.mapObjects[0].length-1; j++) {

                if(map.mapObjects[i][j].getType() == WorldObject.ObjectType.Tree)
                    trees.add(new Tree(new Vector2(i,j), map));
                else if(map.mapObjects[i][j].getType() == WorldObject.ObjectType.Wall)
                    walls.add(new Wall(new Vector3(i, 0, j), new Vector3(8f, 15f, 8f), Color.DARK_GRAY, false));
            }
        }

        // Instantiate the collision detector
        collisionDetector = new CollisionDetector(ball);

    }

    public void update(float deltaTime){
        for (Wall w : walls) {
            collisionDetector.collidesWithWall(w, deltaTime);
        }

        ball.update(deltaTime);
    }

    public void render(ModelBatch batch, Environment environment){
        //render the map
        map.render(batch);

        //render the ball
        ball.render(batch, environment);

        //render the hole
        hole.render(batch, environment);

        //render the walls
        for(Wall w: walls) w.render(batch, environment);

        //render the trees
        for(Tree t: trees) t.render(batch, environment);
        
        //render the club
        club.render(batch, environment);
    }

    public void dispose(){
        ball.dispose();
        hole.dispose();
        for(Wall w: walls) w.dispose();
        for(Tree t: trees) t.dispose();
        club.dispose();
    }
    
        
    public void setDebugMode(boolean debugMode) {
        map.setDebugMode(debugMode);
    }

    public Vector2 getBallPos(){
        return new Vector2(ball.getPosition().x, ball.getPosition().z);
    }

    public boolean isThrowMode(){
        return club.throwMode;
    }

    private void generateBorders(ArrayList<Wall> walls){
        borders = new Wall[4];

        Vector3 yBorder = new Vector3(8, 15, 96);
        Vector3 xBorder = new Vector3(160, 15, 8);
        borders[0] = new Wall(new Vector3(0, 0, 56-4), xBorder, Color.LIGHT_GRAY, true);
        borders[1] = new Wall(new Vector3(-(80-4), 0, 0), yBorder, Color.LIGHT_GRAY, true);
        borders[2] = new Wall(new Vector3((80-4), 0,  0), yBorder, Color.LIGHT_GRAY, true);
        borders[3] = new Wall(new Vector3(0,0, -(56-4)), xBorder, Color.LIGHT_GRAY, true);

        for (int i = 0; i < 4; i++) {
            walls.add(borders[i]);
        }
    }
}
