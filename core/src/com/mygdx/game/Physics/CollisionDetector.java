package com.mygdx.game.Physics;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.WObjects.Ball;
import com.mygdx.game.WObjects.Map;
import com.mygdx.game.WObjects.Obstacle;
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


    public boolean collidesWithWall(Wall wall, float dt) {
        obstacle = wall;

        Vector3 max = wall.getMax().cpy();
        Vector3 min = wall.getMin().cpy();
        Vector3 ballPosition = ball.getCenter();
//        System.out.println("============== ballPos: " + ballPosition + " " + max + " " + min);

        boolean collides = false;

        Vector3 distance = obstacle.getPosition().cpy().sub(ballPosition);//.sub(ball.getRadius());

        Vector3 closest = distance.cpy();
//        System.out.println("======Preclamp: " + distance + " " + closest);

        float hx = (max.x - min.x)*0.5f;
        float hy = (max.y - min.y)*0.5f;
        float hz = (max.z - min.z)*0.5f;

        float xPos = clamp(closest.x, -hx, hx);
        float yPos = clamp(closest.y, -hy, hy);
        float zPos = clamp(closest.z, -hz, hz);

        closest.set(xPos, yPos, zPos);

        distance.sub(ball.getRadius());

        closest = wall.getPosition().cpy().sub(closest);

        Vector3 normal = closest.cpy().sub(ball.getCenter()).nor();
        float distanceToClosestPoint = closest.cpy().sub(ballPosition).len2();

        if (Math.pow(ball.getRadius(), 2) < distanceToClosestPoint && !collides) {
            return false;
        }

        float penetration = ball.getRadius() - (float) Math.sqrt(distanceToClosestPoint);

        collisionSolver.solve(obstacle, normal.cpy().scl(1), penetration, dt);

        return true;
    }

    public CollisionSolver getCollisionSolver() {
        return collisionSolver;
    }
}
