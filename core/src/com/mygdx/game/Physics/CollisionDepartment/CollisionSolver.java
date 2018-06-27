package com.mygdx.game.Physics.CollisionDepartment;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.WObjects.Ball;
import com.mygdx.game.WObjects.Obstacle;

/**
 * Class responsible for solving the collisions
 */
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

    /**
     * Calculate the collision impulse
     * @param dt
     */
    private void calculateImpulse(float dt) {
        Vector3 obstacleVelocity = obstacle.getVelocity().cpy();
        Vector3 ballVelocity = ball.getVelocity().cpy();

        // get the relative velocity between the two objects
        Vector3 relativeVelocity = obstacleVelocity.sub(ballVelocity);
        // normalize it
        float normalizedVelocity = relativeVelocity.dot(normal);

        if (normalizedVelocity < 0) {
            // get the lower restitution of the two objects
            float restitution = Math.min(ball.ELASTICITY, obstacle.getElasticity());

//            float updatedNormalizedVelocity = -normalizedVelocity * restitution;
//
//            Vector3 velocityCausedByAcceleration = ball.getAcceleration();
//
//            float separationVelocityCausedByAcceleration = velocityCausedByAcceleration.dot(normal) * dt;
//
//            if (separationVelocityCausedByAcceleration < 0) {
//                updatedNormalizedVelocity += restitution * separationVelocityCausedByAcceleration;
//
//                if (updatedNormalizedVelocity < 0) {
//                    updatedNormalizedVelocity = 0;
//                }
//            }

            // get the difference in velocities
            float dv = -1f * normalizedVelocity * (restitution + 1);
//            float dv = updatedNormalizedVelocity - normalizedVelocity;

            // get inverse mass of ball and obstacle
            totalInverseMass = ball.getInverseMass() + obstacle.getInverseMass();

            // calculate the impulse scalar
            float impulseScalar = dv / totalInverseMass;

            // apply the impulse scalar to the normal
            Vector3 impulse = normal.cpy().scl(impulseScalar);

            Vector3 newVelocity = ballVelocity.cpy().sub(impulse.cpy().scl(ball.getInverseMass()));

            // update the velocity
            ball.setVelocity(newVelocity);

            ball.isCollided(true);
        }
    }

    /**
     * Checks whether the bodies penetrate each other and prevents it
     */
    private void solvePenetration() {
        // if there is penetration
        if (penetration > 0) {
            Vector3 penetrationResolution = normal.cpy().scl(-1f * penetration / totalInverseMass);

            // calculate change in position
            Vector3 dp = penetrationResolution.cpy().scl(ball.getInverseMass());

            // update ball's position
            ball.getPosition().add(dp);
        }
    }
}
