package com.mygdx.game.WObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.Renderable;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.Utils.HeightField;

import java.util.ArrayList;
import java.util.Random;

public class Water {
    private Renderable ground;
    private HeightField field;
    private Texture texture;
    private Environment environment;

    private float datas[];
    private int width = 25;
    private int height = 25;

    private float time = 0;
    private ArrayList<WaveTrain> trains;

    public Water(Environment environment){
        //create the ground
        texture = new Texture(Gdx.files.internal("water.BMP"));

        float w = 8f;float h = 0;float l = 8f;


        datas = new float[(height*width+width)];
        //fill datas with random value between 0 an 1
        fillRandom(datas, width,height);

        field = new HeightField(true, datas, width,height,true, VertexAttributes.Usage.Position
                | VertexAttributes.Usage.Normal | VertexAttributes.Usage.ColorUnpacked | VertexAttributes.Usage.TextureCoordinates);

        field.corner00.set(-w, h, -l);
        field.corner10.set(w, h, -l);
        field.corner01.set(-w, h, l);
        field.corner11.set(w, h, l);

        //field.data[y * field.width + x] = value;

        field.magnitude.set(0, 0.5f, 0);
        field.update();

        ground = new Renderable();
        this.environment = environment;
        ground.environment = environment;
        ground.meshPart.mesh = field.mesh;
        ground.meshPart.primitiveType = GL20.GL_TRIANGLES;
        ground.meshPart.offset = 0;
        ground.meshPart.size = field.mesh.getNumIndices();
        ground.meshPart.update();
        ground.material = new Material(new TextureAttribute(TextureAttribute.Diffuse, texture));
        ground.worldTransform.translate(0, 0.3f, 0);

        //wave train
        trains = new ArrayList<>();
        for(int i=0; i<width*height; i++) trains.add(new WaveTrain());
    }

    public void render(float deltaTime, ModelBatch batch){
        batch.render(ground);

        fillWater(datas, width, height);
        field.set(datas);

        time += deltaTime;

        for (WaveTrain t:trains) t.update(deltaTime);
    }

    public void fillRandom(float[] dataMap, int width, int height){
        for(int i=0; i< width; i++)
            for(int j=0; j< height; j++)
                dataMap[j*width+i] = MathUtils.random();
    }

    public void fillWater(float[] dataMap, int width, int height){
        for(int i=0; i< width; i++){
            for(int j=0; j< height; j++){
                dataMap[j*width+i] += (float) (trains.get(j*width+i).a() * Math.sin(trains.get(j*width+i).fy() * time + trains.get(j*width+i).w() ));
            }
        }
    }

    /**
     * Class to generate better water fluidity
     */
    class WaveTrain {

        public static final int RISING = 1;
        public static final int FLOWING = 2;
        public static final int FALLING = 3;

        public static final int TRANSITION_TIME = 3;

        public static final int MEDIAN_WAVE_LENGTH = 5;
        public static final float W_AMPLITUDE_RATIO = 0.1f;
        public static final int GRAVITY = 10;
        public static final int WIND_ANGLE = 90;
        public static final double SPEED = 10;

        Random random;

        Vector2 direction;
        double a;
        double waveLength;
        double speed;
        double angle;

        double w;
        double fy;

        float life;
        float time;
        float stateFactor;
        int state;

        public WaveTrain() {
            random = new Random();
            direction = new Vector2();
            initialize();
        }

        private void initialize() {
            time = 0;
            stateFactor = 0;
            state = RISING;

            waveLength = MEDIAN_WAVE_LENGTH/2 + random.nextInt(2 * MEDIAN_WAVE_LENGTH);
            w = Math.sqrt(GRAVITY * 2 * Math.PI / waveLength);

            a = W_AMPLITUDE_RATIO / w;
            fy = SPEED * 2 * Math.PI / waveLength;
            angle = WIND_ANGLE - 20 + random.nextInt(40);
            direction.set((float) Math.cos(angle * Math.PI / 180), (float) Math.sin(angle * Math.PI / 180));

            speed = 1;
            life = 5 + random.nextInt(10);
        }

        public double w() {
            return w;
        }

        public double fy() {
            return fy;
        }

        public double a() {
            return a * stateFactor/TRANSITION_TIME;
        }

        public void update(float deltaTime) {

            switch (state) {
                case RISING:
                    stateFactor += deltaTime;
                    if (stateFactor > TRANSITION_TIME) {
                        stateFactor = 3;
                        state = FLOWING;
                    }
                    break;

                case FLOWING:
                    time += deltaTime;
                    if (time > life) {
                        state = FALLING;
                    }
                    break;

                case FALLING:
                    stateFactor -= deltaTime;
                    if (stateFactor < 0) {
                        initialize();
                    }
                    break;
            }
        }
    }

    public void setDebugMode(boolean debugMode) {

        ground = new Renderable();
        ground.environment = environment;
        ground.meshPart.mesh = field.mesh;

        if(debugMode)ground.meshPart.primitiveType = GL20.GL_LINES;
        else ground.meshPart.primitiveType = GL20.GL_TRIANGLES;

        ground.meshPart.offset = 0;
        ground.meshPart.size = field.mesh.getNumIndices();
        ground.meshPart.update();

        if(debugMode)ground.material = new Material(new ColorAttribute(ColorAttribute.Diffuse, Color.BLUE));
        else ground.material = new Material(TextureAttribute.createDiffuse(texture));
        ground.worldTransform.translate(0, 0.3f, 0);
    }
}
