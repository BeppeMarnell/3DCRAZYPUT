package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.mygdx.game.WObjects.Ball;
import com.mygdx.game.WObjects.Map;
import com.mygdx.game.WObjects.Water;
import com.mygdx.game.WObjects.World;

public class MyGdxGame extends ApplicationAdapter {
	private ModelBatch modelBatch;
	private Camera cam;
	private CameraInputController camController;

	private BitmapFont font;
	private SpriteBatch batch;

	static float magnitude;
	private String[] paths;

	public boolean tracking = true;

	private Map map;

	World world;

	private Environment environment;

	private Water water;

	@Override
	public void create () {
		magnitude = readSettings();

		modelBatch = new ModelBatch();


		//create the environment lights
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -2f, -1.8f, -1.2f));

		//create the map and load it
		map = new Map(paths, environment, magnitude);

		//create the world instance
		world = new World(map);
		world.setDebugMode(false);


		//manage some camera and controls
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 100f, 0);
		cam.lookAt(0,0,0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();
		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);

		//for the FPS
		font = new BitmapFont();
		batch = new SpriteBatch();

		//ex water
		water = new Water(environment);
		water.setDebugMode(false);
	}

	@Override
	public void render () {
		camController.update();

		Ball ball = world.getBall();
		if(tracking) {
			cam.position.x = ball.getPos().x;
			cam.position.z = ball.getPos().y;
		}else {
			cam.position.set(0, 100f, 0);
			cam.lookAt(ball.getPos().x, 0, ball.getPos().y ); //look at the ball

		}
		cam.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(1,1,1,0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		//update the world
		world.update(Gdx.graphics.getDeltaTime());

		modelBatch.begin(cam);

		//render the map
		map.render(modelBatch);

		//render all the objects in the world
		world.render(modelBatch, environment);

		//render the water
		//water.render(Gdx.graphics.getDeltaTime(), modelBatch);

		modelBatch.end();

		//show the frame rate
		batch.begin();
		font.draw(batch, Gdx.graphics.getFramesPerSecond() + " fps", 3, Gdx.graphics.getHeight() - 3);
		batch.end();

		camSet();

	}

	public void camSet(){
		if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)){
			tracking = true;
			camController = new CameraInputController(cam);
			Gdx.input.setInputProcessor(camController);
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)){
			tracking = false;
			camController = new CameraInputController(cam);
			Gdx.input.setInputProcessor(camController);
		}
	}
	
	@Override
	public void dispose () {
		modelBatch.dispose();
		map.dispose();
		world.dispose();
	}

	public float readSettings(){
		float ret = 8;
		paths = new String[3];

		boolean exists = Gdx.files.local("MapInfo.txt").exists();

		paths[0] = (exists)? "MapTexture.png":"rndMapTexture.png";
		paths[1] = (exists)? "MapHeight.png":"rndMapHeight.png";
		paths[2] = (exists)? "MapInfo.txt":"rndMapInfo.txt";

		if(Gdx.files.local("settings.txt").exists()){
			FileHandle file = Gdx.files.internal("settings.txt");
			String text = file.readString();
			ret = Float.parseFloat(text);
		}

		return ret;
	}



}
