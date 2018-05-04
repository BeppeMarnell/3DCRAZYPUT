package com.mygdx.game.Bott;

public class Coordinate {

        int x;
        int y;
        Coordinate parent;

        public Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
            this.parent = null;
        }

        public Coordinate(int x, int y, Coordinate parent) {
            this.x = x;
            this.y = y;
            this.parent = parent;
        }

        int getX() {
            return x;
        }

        int getY() {
            return y;
        }

    public String getStringX(){
        String strx = Integer.toString(x);
        return strx;
    }

    public String getStringY(){
        String stry = Integer.toString(y);
        return stry;
    }

        Coordinate getParent() {
            return parent;
        }



}

