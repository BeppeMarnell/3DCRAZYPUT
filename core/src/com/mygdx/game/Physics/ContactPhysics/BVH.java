package com.mygdx.game.Physics.ContactPhysics;

import com.mygdx.game.WObjects.Obstacle;

/**
 * Bounding Volume Hierarchy
 */
public class BVH {

    public BVH() {

    }

    public class Node {
        Node[] children;
        BVH volume;
        Obstacle obstacle;

        public Node() {
        }

        public boolean isLeaf() {
            return obstacle != null;
        }

        public int getPotentialContacts(PotentialContact[] potentialContacts, int limit) {
            if (isLeaf() || limit == 0) {
                return 0;
            }

            return 0;
        }

        public boolean overlaps(Node other) {
            return this.overlaps(other);
        }


    }



}
