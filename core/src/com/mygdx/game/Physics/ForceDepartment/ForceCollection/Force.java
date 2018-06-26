package com.mygdx.game.Physics.ForceDepartment.ForceCollection;

import com.mygdx.game.Physics.ForceDepartment.ForceManagement.ForceVisitor;

public interface Force {
    void accept(ForceVisitor visitor);
}
