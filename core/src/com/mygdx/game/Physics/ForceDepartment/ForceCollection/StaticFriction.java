package com.mygdx.game.Physics.ForceDepartment.ForceCollection;

import com.mygdx.game.Physics.ForceDepartment.ForceManagement.ForceVisitor;

public class StaticFriction implements Force {
    @Override
    public void accept(ForceVisitor visitor) {
        visitor.visit(this);
    }
}
