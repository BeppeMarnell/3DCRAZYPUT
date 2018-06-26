package com.mygdx.game.WObjects;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Bott.Bot;
import com.mygdx.game.Physics.CollisionDepartment.CollisionDetector;
import com.mygdx.game.Physics.ForceDepartment.ForceManagement.ForceManager;
import com.mygdx.game.Physics.MovementDepartment.IntegratorCollection.Euler;
import com.mygdx.game.Physics.MovementDepartment.IntegratorCollection.Midpoint;
import com.mygdx.game.Physics.MovementDepartment.IntegratorCollection.RungeKutta4;
import com.mygdx.game.Physics.MovementDepartment.MovementManager;
import com.mygdx.game.Bott.GenBot.GenBot;
import com.mygdx.game.Bott.GenBot.Genetic2D;

import java.math.BigDecimal;
import java.text.DecimalFormat;
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
    private MovementManager movementManager;

    private Club club;
    private Bot bot;
    //private GenBot bot2;

    /**
     * INITIALIZE ALL THE COMPONENTS OF THE MAP
     * @param map
     */
    public World(Map map){
        //copy the map
        this.map = map;

        //generate the rest of the world objects

        //BALL
        ball = new Ball(map);

        //Hole
        hole = new Hole(map);

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

        //initialize the bot
        bot = new Bot(map);


        // Initializing the Movement Manager
        movementManager = new MovementManager(map);

        // Setting the default integration method
//        movementManager.setOde(new Euler());
//        movementManager.setOde(new Midpoint());
        movementManager.setOde(new RungeKutta4());

        // Adding the ball to the database
        movementManager.addBody(ball);

        //create the genetic bot
        //bot2 = new GenBot(map);
    }

    public void update(float deltaTime){
        for (Wall w : walls) {
            collisionDetector.collidesWithWall(w, deltaTime);
        }

        // Moving the ball
        movementManager.manage(deltaTime);

        // Winning condition
        if(map.isInHole(new Vector2(ball.getPosition().x,ball.getPosition().z))) {
            System.out.println("Hole");
        }
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
        
        //render the club only when the bot is not moving the ball
        if(!bot.movingBall) club.render(batch, environment, ball);
        
        if(Gdx.input.isKeyJustPressed(Input.Keys.B)){
             //calculate the path with the selected method and move the ball
             //bot.CalculateAStar(map.getArrayMap( new Vector2(ball.getPosition().x,ball.getPosition().z)));
             bot.CalculateBeadthFirst(map.getArrayMap( new Vector2(ball.getPosition().x,ball.getPosition().z)));
            try {
                //bot2.calculateGenetic(new Vector2(ball.getPosition().x, ball.getPosition().z), 5000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(bot.movingBall)bot.act(ball);

        //genBot.update(walls, batch, environment);

        for(ModelInstance mI: bot.rectanglepoints)batch.render(mI,environment);

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

    public Ball getBall() {
        return ball;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        movementManager.draw(shapeRenderer);
    }

}
