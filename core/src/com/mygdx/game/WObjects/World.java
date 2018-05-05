package com.mygdx.game.WObjects;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Utils.Helper;

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

    private Club club;

    //put the bot inside
    //private Bot bot;

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

        for(int i=1; i<map.mapObjects.length-1; i++) {
            for (int j = 1; j < map.mapObjects[0].length-1; j++) {

                if(map.mapObjects[i][j].getType() == WorldObject.ObjectType.Tree)
                    trees.add(new Tree(new Vector2(i,j), map));
                else if(map.mapObjects[i][j].getType() == WorldObject.ObjectType.Wall)
                    walls.add(new Wall(new Vector2(i,j)));
            }
        }

        //add the surrounding walls
        Wall w = new Wall();
        borders = w.getBorder();
        for (int i = 0; i < 4; i++) walls.add(borders[i]);

        //add the club
        club = new Club(map);

        //initialize the bot
        //bot =new Bot(map, ball);
    }

    /**
     * Update the main "game physic engine"
     * @param deltaTime
     */
    public void update(float deltaTime){
        //update the collision detection
        for (Wall w : walls) {
            if (collidesWithWall(w)) {
                //System.out.println("Colliding");
                solveCollision(w);
            }
        }

        //update the ball
        ball.update(deltaTime);

        //bot.render(deltaTime);
    }

    /**
     * Check if the ball collides with the wall
     * @param wall wall to check
     */
    public boolean collidesWithWall(Wall wall) {
        boolean collides = false;

        if (wall.getPosition() == null) return false;

        Vector2 max = wall.getMax().cpy();
        Vector2 min = wall.getMin().cpy();
        Vector2 ballPosition = new Vector2(ball.getPosition().x, ball.getPosition().z);

        Vector2 distance = wall.getPosition().cpy().sub(ballPosition);
        Vector2 closest = distance.cpy();

        float hx = (max.x - min.x) / 2;
        float hy = (max.y - min.y) / 2;

        float xPos = Helper.clamp(-hx, hx, closest.x);
        float yPos = Helper.clamp(-hy, hy, closest.y);

        closest.set(xPos, yPos);

        if (distance.equals(closest)) {
            collides = true;

            // find the nearest Axis
            if (Math.abs(distance.x) > Math.abs(distance.y)) {
                if (closest.x > 0) closest.x = hx;
                else closest.x = -hx;

            } else {
                if (closest.y > 0) closest.y = hy;
                else closest.y = -hy;
            }
        }

        Vector2 normal = distance.cpy().sub(closest);
        float distanceToClosestPoint = normal.cpy().len2();

        if (Math.pow(Ball.RAD, 2) < distanceToClosestPoint && !collides) return false;

        distanceToClosestPoint = normal.cpy().len();

        if (collides) {
            wall.setNormal(distance.cpy().nor().scl(-1));
            wall.setPenetration(Ball.RAD + distanceToClosestPoint);
        } else {
            wall.setNormal(distance.cpy().nor());
            wall.setPenetration(Ball.RAD - distanceToClosestPoint);
        }

        return true;
    }

    public void solveCollision(Wall wall) {
        Vector2 ballVelocity = new Vector2(ball.getVelocity().x, ball.getVelocity().z);

        // Wall's velocity
        Vector2 relativeVelocity = new Vector2().sub(ballVelocity);
        float normalizedVelocity = relativeVelocity.cpy().dot(wall.getNormal());

        if (normalizedVelocity < 0) {

            float elasticity = Math.min(ball.ELASTICITY, wall.ELASTICITY);

            float impulseScl = (-(1 + elasticity) * normalizedVelocity) / (1 / ball.getMass() + 1 / wall.MASS);

            Vector2 impulse = wall.getNormal().cpy().scl(impulseScl);

            Vector2 newVelocity = ballVelocity.cpy();
            newVelocity.sub(impulse.cpy().scl(ball.getInverseMass()));
            //System.out.println(ballVelocity + " " + newVelocity);

            ball.setVelocity(new Vector3(newVelocity.x, 0, newVelocity.y));
        }
    }

    public Wall getWall(Vector3 pos) {
        for (Wall w : walls) {
            if ((pos.x >= w.getMin().x && pos.y >= w.getMin().y) && (pos.x <= w.getMax().x && pos.y <= w.getMax().y)) {
                System.out.println(pos + " " + w.getMin() + " " + w.getMax());
                return w;
            }
        }
        return null;
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
        return new Vector2(ball.getPosition().x, ball.getPosition().y);
    }
}
