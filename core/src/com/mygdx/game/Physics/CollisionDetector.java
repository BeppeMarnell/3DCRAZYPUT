package com.mygdx.game.Physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.WObjects.Ball;
import com.mygdx.game.WObjects.Wall;

import static com.badlogic.gdx.math.MathUtils.clamp;

public class CollisionDetector {
    private Ball ball;
    private Obstacle obstacle;
    private CollisionSolver collisionSolver;

    public CollisionDetector(Ball ball, Obstacle obstacle) {
        this(ball);
        this.obstacle = obstacle;
    }

    public CollisionDetector(Ball ball) {
        this.ball = ball;
        collisionSolver = new CollisionSolver(ball);
    }


    public boolean collidesWithWall(Wall wall) {
        obstacle = wall;
//        Wall wall = (Wall) obstacle;

//        if (wall == null) {
//            return false;
//        }

        if (obstacle.getPosition() == null) {
            System.out.println("Obstacle has no position");
            return false;
        }

        Vector2 max = wall.getMax().cpy();
        Vector2 min = wall.getMin().cpy();
        Vector2 ballPosition = new Vector2(ball.getPosition().x, ball.getPosition().z);

        boolean collides = false;

        Vector2 distance = obstacle.getPosition().cpy().sub(ballPosition);

        Vector2 closest = distance.cpy();

        float hx = (max.x - min.x) / 2;
        float hy = (max.y - min.y) / 2;

        float xPos = clamp(closest.x, -hx, hx);
        float yPos = clamp(closest.y, -hy, hy);

        closest.set(xPos, yPos);

        //System.out.println(distance + " " + closest);


        if (distance.equals(closest.cpy().add(ball.RAD, ball.RAD))) {
//        if (distance.equals(closest)) {
            collides = true;

            // find the nearest Axis
            if (Math.abs(distance.x) > Math.abs(distance.y)) {
                if (closest.x > 0) {
                    closest.x = hx;
                } else {
                    closest.x = -hx;
                }
            } else {
                if (closest.y > 0) {
                    closest.y = hy;
                } else {
                    closest.y = -hy;
                }
            }
        }

        Vector2 normal = distance.cpy().sub(closest);
        float distanceToClosestPoint = normal.cpy().len2();

        if (Math.pow(Ball.RAD, 2) < distanceToClosestPoint && !collides) {
            return false;
        }

        distanceToClosestPoint = normal.cpy().len();

        Vector3 normal3 = new Vector3(distance.x, 0, distance.y);
        float penetration = Ball.RAD + distanceToClosestPoint;

        if (collides) {
            wall.setNormal(normal.scl(-1));
            collisionSolver.solveCollision(wall);
//            collisionSolver.solve(obstacle, normal3.scl(-1), penetration);
        } else {
            wall.setNormal(normal);
            penetration = Ball.RAD - distanceToClosestPoint;
            collisionSolver.solveCollision(wall);
//            collisionSolver.solve(obstacle, normal3, penetration);
        }

        return true;
    }

    public CollisionSolver getCollisionSolver() {
        return collisionSolver;
    }
}
