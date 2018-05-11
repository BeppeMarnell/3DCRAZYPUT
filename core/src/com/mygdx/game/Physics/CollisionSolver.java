package com.mygdx.game.Physics;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.WObjects.Ball;
import com.mygdx.game.WObjects.Obstacle;
import com.mygdx.game.WObjects.Wall;

public class CollisionSolver {
    private float penetration;
    private Vector3 normal;
    private Obstacle obstacle;
    private Ball ball;
    private float totalInverseMass;

    public CollisionSolver(Ball ball) {
        this.ball = ball;
    }

    public void solveCollision(Wall wall, Vector3 normal) {
        Vector3 ballVelocity = ball.getVelocity().cpy();
        // Wall's velocity
        float normalizedVelocity = ballVelocity.cpy().dot(normal);

        if (normalizedVelocity < 0) {

            float elasticity = Math.min(ball.ELASTICITY, wall.ELASTICITY);

            float impulseScl = (-(1 + elasticity) * normalizedVelocity) / (ball.getInverseMass() + wall.getInverseMass());
            //System.out.println(impulseScl);

            Vector3 impulse = normal.cpy().scl(impulseScl);

            Vector3 newVelocity = ballVelocity.cpy();
            newVelocity.sub(impulse.cpy().scl(ball.getInverseMass()));
            //System.out.println(ballVelocity + " " + newVelocity);

            ball.setVelocity(new Vector3(newVelocity.x, 0, newVelocity.y));
        }
    }

    public void solve(Obstacle obstacle, Vector3 normal, float penetration, float dt) {
        this.obstacle = obstacle;
        this.normal = normal;
        this.penetration = penetration;

        calculateImpulse(dt);
        solvePenetration();

    }

    private void calculateImpulse(float dt) {
        Vector3 obstacleVelocity = obstacle.VELOCITY.cpy();
        Vector3 ballVelocity = ball.getVelocity().cpy();
        // Wall's velocity
        Vector3 relativeVelocity = obstacleVelocity.sub(ballVelocity);
//        float normalizedVelocity = ballVelocity.cpy().dot(normal.cpy());
        float normalizedVelocity = relativeVelocity.dot(normal);
        // normalize ball's velocity
//        float normalizedVelocity =  ball.getVelocity().cpy().dot(normal);

        if (normalizedVelocity < 0) {
            // calculate difference in velocity
            float restitution = Math.min(ball.ELASTICITY, obstacle.ELASTICITY);

            float updatedNormalizedVelocity = -normalizedVelocity * restitution;

            Vector3 velocityCausedByAcceleration = ball.getAcceleration();

            float separationVelocityCausedByAcceleration = velocityCausedByAcceleration.dot(normal) * dt;

            if (separationVelocityCausedByAcceleration < 0) {
                updatedNormalizedVelocity += restitution * separationVelocityCausedByAcceleration;

                if (updatedNormalizedVelocity < 0) {
                    updatedNormalizedVelocity = 0;
                }
            }

//            float dv = -1f * normalizedVelocity * (restitution + 1);
            float dv = updatedNormalizedVelocity - normalizedVelocity;

            // get inverse mass of ball and obstacle
            totalInverseMass = ball.getInverseMass() + obstacle.getInverseMass();

            // calculate the impulse scalar
            float impulseScalar = dv / totalInverseMass;

            // apply the impulse scalar to the normal
            Vector3 impulse = normal.cpy().scl(impulseScalar);

            Vector3 newVelocity = ballVelocity.cpy().sub(impulse.cpy().scl(ball.getInverseMass()));
            //System.out.println(impulseScalar + " " + impulse.cpy().scl(ball.getInverseMass()) + " " + normal + " " + newVelocity + " " + ballVelocity + " " + relativeVelocity);

            // update the velocity
            ball.setVelocity(newVelocity);

        }
    }

    private void solvePenetration() {
        if (penetration > 0) {

            Vector3 penetrationResolution = normal.cpy().scl(-1f * penetration / totalInverseMass);

            // calculate change in position
            Vector3 dp = penetrationResolution.cpy().scl(ball.getInverseMass());

            // update ball's position
            ball.setPosition(ball.getPosition().cpy().add(dp));
        }


    }

    public Vector3 getNormal() {
        return normal;
    }

    public float getPenetration() {
        return penetration;
    }
}
