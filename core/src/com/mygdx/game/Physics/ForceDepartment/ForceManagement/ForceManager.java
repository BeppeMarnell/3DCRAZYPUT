package com.mygdx.game.Physics.ForceDepartment.ForceManagement;

import com.mygdx.game.Physics.ForceDepartment.ForceCollection.Force;
import com.mygdx.game.Physics.RigidBody;

public class ForceManager {
    private ForceDatabase db;
    private ForceCalculator calculator;

    public ForceManager() {
        db = new ForceDatabase();
        calculator = new ForceCalculator();
    }

    public void add(RigidBody body, Force[] forces) {
        db.add(body, forces);
    }

    public void manage(RigidBody body) {
        calculator.setBody(body);
        Force[] forces = db.getForces(body);
        for (Force f : forces) {
            f.accept(calculator);
            calculator.setActingForce();
        }
    }
}
