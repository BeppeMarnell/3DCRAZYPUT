package com.mygdx.game.Physics.State;

import com.mygdx.game.Physics.RigidBody;

public class Motion extends State {

    private RigidBody body;

    public Motion(RigidBody rigidBody) {
        body = rigidBody;
    }

    public void handle() {

    }
}
