package com.mygdx.game.WObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Utils.BiCubicSplineFast;
import com.mygdx.game.Utils.HeightField;
import com.mygdx.game.Utils.Helper;

public class Map {

    /**
     * 1 Ball
     * 2 Grass terrain
     * 3 Dirt terrain
     * 4 Sand terrain
     * 5 Water
     * 6 Wall
     * 7 Tree
     * 8 Hole
     */

    public WorldObject[][] mapObjects;
    private WorldObject ball;
    private WorldObject hole;

    //spline interpolation object
    private BiCubicSplineFast bSpline;

    //rendering 3d objects
    private Renderable ground;
    private HeightField field;
    private Texture texture;

    private float magnitude;

    public Vector2 getHolePos(){
        return new Vector2((int)hole.getPos().x/64,(int)hole.getPos().y/64 );
    }

    public Vector2 getBallPos(){
        return new Vector2((int)ball.getPos().x/64,(int)ball.getPos().y/64 );
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

        float w = 80f;float h = 0;float l = 56f;
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
        ground.environment = environment;
        ground.meshPart.mesh = field.mesh;
        ground.meshPart.primitiveType = GL20.GL_TRIANGLES;
        ground.meshPart.offset = 0;
        ground.meshPart.size = field.mesh.getNumIndices();
        ground.meshPart.update();
        ground.material = new Material(TextureAttribute.createDiffuse(texture));
        //ground.worldTransform.translate(80, 0, 56);

        //load the map with the .txt file info
        load(paths[2]);

        //set magnitude
        this.magnitude = magnitude;
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

        double[][] interpArray = new double[20][14];

        double[] x1 = new double[20];
        double[] x2 = new double[14];
        //create the x1 and x2 indices
        for(int i=0; i<20; i++) x1[i] = i;
        for (int t=0; t<14; t++) x2[t] = t;

        for(int i=1; i<mapObjects.length-1; i++) {
            for (int t = 1; t < mapObjects[0].length-1; t++) {
                interpArray[i][t] = Helper.map(mapObjects[i][t].getHeight(), 0,4,0,1);
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

        Vector2 translPos = new Vector2();
        translPos.x = Helper.map(pos.x, -80, 80, 0, 20);
        translPos.y = Helper.map(pos.y, -56, 56, 0, 14);

        float valueH = (float)bSpline.interpolate((double)translPos.x,(double)translPos.y); // + ball heigth

        if(Helper.map(valueH,0,1,0, magnitude)<0)
        return  toAdd;
        else return Helper.map(valueH,0,1,0, magnitude) + toAdd;
    }

    public void dispose(){
        texture.dispose();
        field.dispose();
    }
}
