package com.mygdx.game.Physics;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.WObjects.Ball;
import com.mygdx.game.WObjects.Obstacle;
import com.mygdx.game.WObjects.Wall;

import static com.badlogic.gdx.math.MathUtils.clamp;

public class CollisionDetector {
    private BoundingSphere ball;
    private Obstacle obstacle;
    private CollisionSolver collisionSolver;

    public CollisionDetector(Ball ball) {
        this.ball = ball;
        collisionSolver = new CollisionSolver(ball);
    }

    public boolean collidesWithWall(BoundingBox wall, float dt) {
        obstacle = (Wall) wall;

        Vector3 ballCenter = ball.getPosition();
        Vector3 distance = obstacle.getPosition().cpy().sub(ballCenter);
        Vector3 halfSize = obstacle.getHalfSize();

        float x = clamp(distance.x, -halfSize.x, halfSize.x);
        float y = clamp(distance.y, -halfSize.y, halfSize.y);
        float z = clamp(distance.z, -halfSize.z, halfSize.z);

        Vector3 closest = new Vector3(x, y, z);

        distance.sub(ball.getRadius());

        closest = wall.getPosition().cpy().sub(closest);

        float distanceToClosestPoint = closest.cpy().sub(ballCenter).len2();

        if (Math.pow(ball.getRadius(), 2) < distanceToClosestPoint) return false;

        Vector3 normal = closest.cpy().sub(ball.getPosition()).nor();
        float penetration = ball.getRadius() - (float) Math.sqrt(distanceToClosestPoint);

        collisionSolver.solve(obstacle, normal, penetration, dt);

        return true;
    }
}
