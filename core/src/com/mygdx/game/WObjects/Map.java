package com.mygdx.game.WObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.Utils.BiCubicSplineFast;
import com.mygdx.game.Utils.HeightField;
import com.mygdx.game.Utils.Helper;

public class Map {

    /**
     * Class containing all the information about the map
     * 3D map between -80 and 80 on the x axis
     * -56 and 56 on the z axis
     * y axis used for the height
     */

    public WorldObject[][] mapObjects;
    private WorldObject ball;
    private static WorldObject hole;

    //spline interpolation object
    private BiCubicSplineFast bSpline;

    //rendering 3d objects
    private Renderable ground;
    private HeightField field;
    private Texture texture;
    private Environment environment;

    private float magnitude;

    private Vector2 getHolePos(){
        return new Vector2((int)hole.getPos().x, 13 - (int)hole.getPos().y);
    }

    public Vector2 getHolePosTranslV2(){
        float x = Helper.map((int)hole.getPos().x, 1, 18,-72, 64);
        float y = Helper.map((int)hole.getPos().y, 1, 12,40, -48);

        return new Vector2(x +4f, y+4f);
    }

    public Vector2 getInitBallPosV2(){
        float x = Helper.map((int)ball.getPos().x, 1, 18,-72, 64);
        float y = Helper.map((int)ball.getPos().y, 1, 12,40, -48);
        return new Vector2(x + 4f, y + 4f);
    }
    
    public Vector3 getInitBallPosV3(){
        return new Vector3(this.getInitBallPosV2().x,getHeight(getInitBallPosV2(),Ball.RAD), getInitBallPosV2().y);
    }

    /**
     * main constructor to load the map
     * @param paths
     * @param environment
     * @param magnitude
     */
    public Map(String[] paths, Environment environment, float magnitude){
        //create the ground
        texture = new Texture(Gdx.files.internal(paths[0]));

        float w = 72f;float h = 0;float l = 48f;
        Pixmap data = new Pixmap(Gdx.files.internal(paths[1]));
        field = new HeightField(true, data, true, VertexAttributes.Usage.Position
                | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates);
        data.dispose();
        field.corner00.set(-w, h, -l);
        field.corner10.set(w, h, -l);
        field.corner01.set(-w, h, l);
        field.corner11.set(w, h, l);


        field.magnitude.set(0, magnitude, 0);
        field.update();

        ground = new Renderable();
        this.environment = environment;
        ground.environment = environment;
        ground.meshPart.mesh = field.mesh;
        ground.meshPart.primitiveType = GL20.GL_TRIANGLES;
        ground.meshPart.offset = 0;
        ground.meshPart.size = field.mesh.getNumIndices();
        ground.meshPart.update();
        ground.material = new Material(TextureAttribute.createDiffuse(texture));

        //set magnitude
        this.magnitude = magnitude;

        //load the map with the .txt file info
        load(paths[2]);
    }

    /**
     * Draw the ground
     * @param batch
     */
    public void render(ModelBatch batch){
        batch.render(ground);
    }

    /**
     * Method to load the map objects from the .txt file and store them into the mapObjects array
     * @param mapInfo
     */
    private void load(String mapInfo){
        mapObjects = new WorldObject[20][14];

        FileHandle file = Gdx.files.local(mapInfo);
        String text = file.readString();

        //split the string for objects
        String[] objects = text.split(";");

        int j = 0,l = 0;

        for(int i= 0; i<objects.length -2; i++){ // -2 for the ball and the hole

            //for each string, create a object for the map
            String[] parameters = objects[i].split(",");

            float height = Float.parseFloat(parameters[1]);
            Vector2 pos = new Vector2(Float.parseFloat(parameters[2]), Float.parseFloat(parameters[3]));

            int type = Integer.parseInt(parameters[0]);

            if (type == 2)
                mapObjects[j][l] = new WorldObject(WorldObject.ObjectType.Grass, pos, height);
            else if (type == 3)
                mapObjects[j][l] = new WorldObject(WorldObject.ObjectType.Dirt, pos, height);
            else if (type == 4)
                mapObjects[j][l] = new WorldObject(WorldObject.ObjectType.Sand, pos, height);
            else if (type == 5)
                mapObjects[j][l] = new WorldObject(WorldObject.ObjectType.Water, pos, -1);
            else if (type == 6)
                mapObjects[j][l] = new WorldObject(WorldObject.ObjectType.Wall, pos, 0);
            else if (type == 7)
                mapObjects[j][l] = new WorldObject(WorldObject.ObjectType.Tree, pos, height);

            l++;
            if(l>13 && j< 20){
                l = 0;
                j++;
            }
        }

        //get the hole and the ball position
        String[] holeInfo = objects[objects.length-2].split(",");
        hole = new WorldObject(WorldObject.ObjectType.Hole, new Vector2(Float.parseFloat(holeInfo[2]), Float.parseFloat(holeInfo[3])), 0);

        String[] ballInfo = objects[objects.length-1].split(",");
        ball = new WorldObject(WorldObject.ObjectType.Ball, new Vector2(Float.parseFloat(ballInfo[2]), Float.parseFloat(ballInfo[3])), 0);

        double[][] interpArray = new double[36][24];

        double[] x1 = new double[36];
        double[] x2 = new double[24];
        //create the x1 and x2 indices
        for(int i=0; i<x1.length; i++) x1[i] = i;
        for (int t=0; t<x2.length; t++) x2[t] = t;

        for(int i=0; i<x1.length; i++) {
            for (int t = 0; t < x2.length; t++) {
                interpArray[i][t] = Helper.map(field.getPositionAt(new Vector3(),i*4, t*4).y, 0,magnitude,0,1);
            }
        }

        //send all to interpolate
        bSpline = new BiCubicSplineFast(x1,x2,interpArray);
    }

    /**
     * After loading the map, each point gets interpolated and from this method
     * we can easily get the height of the terrain
     * @param pos
     * @return height of the terrain in that position
     */
    public float getHeight(Vector2 pos, float toAdd){

        //find the exact position in the map
        Vector2 translPos = new Vector2();
        translPos.x = Helper.map(pos.x, -72, 72, 0, 36);
        translPos.y = Helper.map(pos.y, -48, 48, 0, 24);

        float valueH = (float)bSpline.interpolate((double)translPos.x,(double)translPos.y); // + ball heigth

        return Helper.map(valueH,0,1,0, magnitude) + toAdd;
    }

    /**
     * Function to get the friction in a specific position in the map
     * @param pos
     * @return the friction in the exact tile
     */
    public float getFriction(Vector2 pos){

        //in order to not crash outofbounds
        int i = (int)(pos.x +80)/8;
        int j = (int)(pos.y+56)/8;

        return mapObjects[(i>=0 &&i<20)? i: 1][(j>=0 &&j<14)? j: 1].getFriction();
    }

    /**
     * helper method to check whether the ball is in the hole
     * @param pos
     * @return
     */
    public boolean isInHole(Vector2 pos){
        //in order to not crash outofbounds
        int i = (int)(pos.x +80)/8;
        int j = (int)(pos.y+56)/8;

         if(i == getHolePos().x && j == getHolePos().y){
             //Assets.Scored();
             return true;
         }
         return false;
    }

    /**
     * Helper method to translate the map into a array map readable by the bot
     * @return
     */
    public int[][] getArrayMap( Vector2 ballPos){
        //Increments the Map Array 
       
        int[][] map = new int[20][14];

        //Convert the walls, trees, and water
        for(int i=0; i<map.length; i++)
            for (int j = 0; j < map[0].length; j++)
                if(mapObjects[i][j].getType() == WorldObject.ObjectType.Tree || mapObjects[i][j].getType() == WorldObject.ObjectType.Water
                        || mapObjects[i][j].getType() == WorldObject.ObjectType.Wall) map[i][j] = 1;

        //Convert hole and ball position
        int x = (int)Helper.map(ballPos.x, -80, 80,0, 20);
        int y = (int )Helper.map(ballPos.y, -56, 56,0, 14);
        map[x][y] = 7;
        map[(int)getHolePos().x][(int)getHolePos().y] = 9;

        return map;
    }

    /**
     * set a debug mode for the map, without textures
     * @param debugMode
     */
    public void setDebugMode(boolean debugMode) {

        ground = new Renderable();
        ground.environment = environment;
        ground.meshPart.mesh = field.mesh;

        if (debugMode) ground.meshPart.primitiveType = GL20.GL_LINES;
        else ground.meshPart.primitiveType = GL20.GL_TRIANGLES;

        ground.meshPart.offset = 0;
        ground.meshPart.size = field.mesh.getNumIndices();
        ground.meshPart.update();

        if (debugMode) ground.material = new Material(new ColorAttribute(ColorAttribute.Diffuse, Color.BROWN));
        else ground.material = new Material(TextureAttribute.createDiffuse(texture));
    }

    public void dispose(){
        texture.dispose();
        field.dispose();
    }
}
