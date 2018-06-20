package com.mygdx.game.Physics;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.WObjects.Ball;
import com.mygdx.game.WObjects.Obstacle;

public class CollisionSolver {
    private float penetration;
    private Vector3 normal;
    private Obstacle obstacle;
    private Ball ball;
    private float totalInverseMass;

    public CollisionSolver(Ball ball) {
        this.ball = ball;
    }

    public void solve(Obstacle obstacle, Vector3 normal, float penetration, float dt) {
        this.obstacle = obstacle;
        this.normal = normal;
        this.penetration = penetration;

        calculateImpulse(dt);
        solvePenetration();
    }

    private void calculateImpulse(float dt) {
        Vector3 obstacleVelocity = obstacle.getVelocity().cpy();
        Vector3 ballVelocity = ball.getVelocity().cpy();
        // Wall's velocity
        Vector3 relativeVelocity = obstacleVelocity.sub(ballVelocity);
        float normalizedVelocity = relativeVelocity.dot(normal);

        if (normalizedVelocity < 0) {
            // calculate difference in velocity
            float restitution = Math.min(ball.ELASTICITY, obstacle.getElasticity());

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
            ball.position.add(dp);
        }
    }
}
