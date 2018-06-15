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
        System.out.println("[!] Managing forces");
        calculator.setBody(body);
        Force[] forces = db.getForces(body);
        for (Force f : forces) {
            f.accept(calculator);
        }
        System.out.println("[!!] Setting acting force ... ");
        calculator.setActingForce();
        System.out.print(body.getActingForce());
    }
}
