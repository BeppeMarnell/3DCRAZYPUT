package com.mygdx.game.Physics.ForceDepartment.ForceManagement;

import com.mygdx.game.Physics.ForceDepartment.ForceCollection.*;

/**
 * The interface of the Visitor pattern, contains methods that visit each of the forces
 */
public interface ForceVisitor {
    void visit(Gravity force);
    void visit(Normal force);
    void visit(StaticFriction force);
    void visit(KineticFriction force);
    void visit(Perpendicular force);
    void visit(Drag drag);
    void visit(Total total);
}
