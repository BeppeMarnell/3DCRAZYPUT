package com.mygdx.game.Physics.ForceDepartment.ForceManagement;

import com.mygdx.game.Physics.ForceDepartment.ForceCollection.Force;
import com.mygdx.game.Physics.RigidBody;

import java.util.HashMap;

public class ForceDatabase {
    private HashMap<RigidBody, Force[]> db;

    public ForceDatabase() {
        db = new HashMap<>();
    }

    public void add(RigidBody body, Force[] forces) {
        db.put(body, forces);
    }

    public void remove(RigidBody body) {
        db.remove(body);
    }

    public void clear() {
        db.clear();
    }

    public Force[] getForces(RigidBody body) {
        return db.get(body);
    }
}
