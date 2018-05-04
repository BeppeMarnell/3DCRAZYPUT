package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Utils.Helper;
import com.mygdx.game.WObjects.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class Bot {

    /**
     * 1 walls - water - tree
     * 7 ball
     * 9 hole
     */

    private Map map;
    private Ball ball;

    private Coordinate start;
    private Coordinate end;

    private static final int[][] DIRECTIONS = { { 0, 1 }, { 1, 0 }, { 0, -1 }, { -1, 0 } };

    private boolean[][] visited;

    private int[][] array;

    private ArrayList<Coordinate> coordinates;

    public Bot(Map map , Ball ball){
        this.ball = ball;
        this.map = map;
        this.start = setStart();
        this.end = setEnd();
        this.visited = new boolean[20][20];
        this.array = new int[20][20];
        this.coordinates = new ArrayList<>();
        solve();
    }

    public Coordinate setStart(){
        int x = (int)ball.getPos().x;
        int y = (int)ball.getPos().y;

        return start = new Coordinate(x,y);
    }

    public Coordinate setEnd(){
        int x = (int)map.getHolePos().x;
        int y = (int)map.getHolePos().y;

        return end = new Coordinate(x,y);
    }


    public void render(float deltaTime){

        if(Gdx.input.isKeyJustPressed(Input.Keys.P))
            throwBall(deltaTime);

        if(Gdx.input.isKeyPressed(Input.Keys.G)){

            Collections.reverse(coordinates);
            for(Coordinate c: coordinates){
                Vector2 coor = new Vector2(Helper.map(c.x,0,20,-80,80) ,
                        Helper.map(c.y, 0,14, -56,56));
                Vector2 dir = coor.sub(ball.getPos().cpy()).nor();
                ball.setLinearVelocity(dir.scl(100f).cpy());
            }
        }
    }


    public void throwBall(float deltaTime){
        Vector2 ballpos = new Vector2(ball.getPos().cpy());
        Vector2 holePos = new Vector2();
        holePos.x = Helper.map(map.getHolePos().x, 0,20,-80, 80);
        holePos.y = Helper.map(map.getHolePos().y, 0,14,-56, 56);

        Vector2 dir = holePos.sub(ballpos);
        float amount = holePos.dst(ballpos);

        ball.setLinearVelocity(dir.cpy().scl(amount*deltaTime*2));
    }


    public Coordinate getEntry() {
        return start;
    }

    public boolean isExit(int x, int y) {
        return x == end.getX() && y == end.getY();
    }

    public boolean isExplored(int row, int col) {
        return visited[row][col];
    }

    public boolean isWall(int x, int y){
       boolean isWall;
        if(map.mapObjects[x][y].getType() == WorldObject.ObjectType.Water){ isWall=true; }
        else {isWall = false;}
        return isWall;
    }

    public void setVisited(int row, int col, boolean value) {
        visited[row][col] = value;
    }

    public boolean isValidLocation(int row, int col) {
        if (row < 0 || row >= map.mapObjects.length || col < 0 || col >= map.mapObjects[0].length) {
            return false;
        }
        return true;
    }

    public List<Coordinate> solve() {
        LinkedList<Coordinate> nextToVisit = new LinkedList<>();
        Coordinate start = getEntry();
        nextToVisit.add(start);

        while (!nextToVisit.isEmpty()) {
            Coordinate cur = nextToVisit.remove();

            if (!isValidLocation(cur.getX(), cur.getY()) || isExplored(cur.getX(), cur.getY())) {
                continue;
            }

            if (isWall(cur.getX(), cur.getY())) {
                setVisited(cur.getX(), cur.getY(), true);
                continue;
            }

            if (isExit(cur.getX(), cur.getY())) {
                return backtrackPath(cur);
            }

            for (int[] direction : DIRECTIONS) {
                Coordinate coordinate = new Coordinate(cur.getX() + direction[0], cur.getY() + direction[1], cur);
                nextToVisit.add(coordinate);
                setVisited(cur.getX(), cur.getY(), true);
            }
        }
        return Collections.emptyList();
    }

    private List<Coordinate> backtrackPath(Coordinate cur) {
        List<Coordinate> path = new ArrayList<>();
        Coordinate iter = cur;

        while (iter != null) {
            path.add(iter);
            iter = iter.parent;
        }
        System.out.println("Solved");
        for(int i = 0; i < path.size(); i++) {
            System.out.println(path.get(i).getStringX() + " : " + path.get(i).getStringY());
            array[path.get(i).y][path.get(i).y] = 1;
        }

        for(int i = 0; i < path.size(); i++){

        }

        for(int i = 0; i < path.size(); i++){
            coordinates.add(path.get(i));
        }

        System.out.println("-----------------");

        for (int i = 0; i < path.size() - 1; i++) {
            System.out.println((path.get(i + 1).x - path.get(i).x) + " : " + (path.get(i + 1).y - path.get(i).y));
        }

        return path;
    }

    public void printArray() {
        for (int i = 0; i < array.length; i++) {
            System.out.println();
            for (int j = 0; j < array[i].length; j++) {
                if (array[i][j] == 1) { System.out.print(". "); }
                else { System.out.print("X "); }
            }
        }
    }

    public void printCoordiantes(){
        for(int i = 0; i< coordinates.size(); i++){
            System.out.println(coordinates.get(i).getStringX()+ " "+coordinates.get(i).getStringY());
        }
    }

    


}
