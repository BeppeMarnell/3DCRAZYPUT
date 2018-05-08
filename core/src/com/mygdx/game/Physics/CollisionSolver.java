package com.mygdx.game.Physics;

import com.badlogic.gdx.math.Vector2;
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

    public void solveCollision(Wall wall) {
        Vector2 ballVelocity = new Vector2(ball.getVelocity().x, ball.getVelocity().z);
        // Wall's velocity
        Vector2 relativeVelocity = new Vector2(0, 0).sub(ballVelocity);
        float normalizedVelocity = relativeVelocity.cpy().dot(wall.getNormal());

        if (normalizedVelocity < 0) {

            float elasticity = Math.min(ball.ELASTICITY, wall.ELASTICITY);

            float impulseScl = (-(1 + elasticity) * normalizedVelocity) / (ball.getInverseMass() + wall.getInverseMass());

            Vector2 impulse = wall.getNormal().cpy().scl(impulseScl);

            Vector2 newVelocity = ballVelocity.cpy();
            newVelocity.sub(impulse.cpy().scl(ball.getInverseMass()));
//            System.out.println(ballVelocity + " " + newVelocity);

            ball.setVelocity(new Vector3(newVelocity.x, 0, newVelocity.y));
        }
    }

    public void solve(Obstacle obstacle, Vector3 normal, float penetration) {
        this.obstacle = obstacle;
        this.normal = normal;
        this.penetration = penetration;

        calculateImpulse();
//        solvePenetration();

    }

    private void calculateImpulse() {
        // normalize ball's velocity
        float normalizedVelocity =  ball.getVelocity().cpy().dot(normal);

        if (normalizedVelocity < 0) {
            // calculate difference in velocity
            float restitution = Math.min(ball.ELASTICITY, obstacle.ELASTICITY);

            float updatedNormalizedVelocity = -normalizedVelocity * restitution;

//            Vector3






            float dv = -normalizedVelocity * (restitution + 1);


            // get inverse mass of ball and obstacle
            totalInverseMass = ball.getInverseMass() + obstacle.getInverseMass();

            // calculate the impulse scalar
            float impulseScalar = dv / totalInverseMass;

            // apply the impulse scalar to the normal
            Vector3 impulse = normal.cpy().scl(impulseScalar);

            // update the velocity
            ball.setVelocity(ball.getVelocity().cpy().add(impulse.cpy().scl(ball.getInverseMass())));

        }
    }

    private void solvePenetration() {
        if (penetration > 0) {

            Vector3 penetrationResolution = normal.cpy().scl(penetration / totalInverseMass);

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
