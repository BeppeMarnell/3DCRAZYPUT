package com.mygdx.game.Physics.CollisionDepartment;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Physics.BoundingBox;
import com.mygdx.game.Physics.BoundingSphere;
import com.mygdx.game.WObjects.Ball;
import com.mygdx.game.WObjects.Obstacle;
import com.mygdx.game.WObjects.Wall;

import static com.badlogic.gdx.math.MathUtils.clamp;

/**
 * Class in charge of detecting collision and solving them if they happen
 */
public class CollisionDetector {
    private BoundingSphere ball;
    private Obstacle obstacle;
    private CollisionSolver collisionSolver;

    public CollisionDetector(Ball ball) {
        this.ball = ball;
        collisionSolver = new CollisionSolver(ball);
    }

    /**
     * Checks whether the ball collides with a wall
     * If it does, solve it
     * @param wall - a given wall
     * @param dt
     * @return TRUE / FALSE
     */
    public boolean collidesWithWall(BoundingBox wall, float dt) {
        obstacle = (Wall) wall;

        // get the distance between the ball and the wall
        Vector3 ballCenter = ball.getPosition();
        Vector3 distance = obstacle.getPosition().cpy().sub(ballCenter);
        Vector3 halfSize = obstacle.getHalfSize();

        // clamp against all axes to get the closest point of intersection
        float x = clamp(distance.x, -halfSize.x, halfSize.x);
        float y = clamp(distance.y, -halfSize.y, halfSize.y);
        float z = clamp(distance.z, -halfSize.z, halfSize.z);

        Vector3 closest = new Vector3(x, y, z);

        distance.sub(ball.getRadius());

        // get the distance to the closest point
        closest = wall.getPosition().cpy().sub(closest);

        // get the magnitude of the distance
        float distanceToClosestPoint = closest.cpy().sub(ballCenter).len2();

        // check whether it's inside the ball
        if (Math.pow(ball.getRadius(), 2) < distanceToClosestPoint) return false;

        // get the collision normal and the penetration depth
        Vector3 normal = closest.cpy().sub(ball.getPosition()).nor();
        float penetration = ball.getRadius() - (float) Math.sqrt(distanceToClosestPoint);

        // solve the collision
        collisionSolver.solve(obstacle, normal, penetration, dt);

        return true;
    }
}
