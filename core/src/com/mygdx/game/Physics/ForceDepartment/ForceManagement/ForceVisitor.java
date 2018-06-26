package com.mygdx.game.Physics.ForceDepartment.ForceManagement;

import com.mygdx.game.Physics.ForceDepartment.ForceCollection.*;

public interface ForceVisitor {
    void visit(Gravity force);
    void visit(Normal force);
    void visit(StaticFriction force);
    void visit(KineticFriction force);
    void visit(Perpendicular force);
    void visit(Drag drag);
    void visit(Total total);
}
