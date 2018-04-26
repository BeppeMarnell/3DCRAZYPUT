package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.WObjects.Ball;
import com.mygdx.game.WObjects.Map;
import com.mygdx.game.WObjects.Tree;

public class MyGdxGame extends ApplicationAdapter {
	private ModelBatch modelBatch;
	private Camera cam;
	private CameraInputController camController;

	private BitmapFont font;
	private SpriteBatch batch;

	static float magnitude;
	private String[] paths;

	private Map map;
	private Ball ball;
	private Tree tree;


	private Environment environment;

	@Override
	public void create () {
		magnitude = readSettings();

		modelBatch = new ModelBatch();
		cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		cam.position.set(0, 100f, 0);
		cam.lookAt(0,0,0);
		cam.near = 1f;
		cam.far = 300f;
		cam.update();

		//create the environment lights
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -2f, -1.8f, -1.2f));

		//create the map and load it
		map = new Map(paths, environment, magnitude);

		//create the ball and send a copy of the map
		ball = new Ball(new Vector3(5, map.getHeight(new Vector2(5,-5), Ball.RAD), -5), map);

		//create the tree
		tree = new Tree(new Vector3(0,0,0), map);

		//manage some camera controls
		camController = new CameraInputController(cam);
		Gdx.input.setInputProcessor(camController);

		//for the FPS
		font = new BitmapFont();
		batch = new SpriteBatch();


	}

	@Override
	public void render () {
		camController.update();

		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClearColor(1,1,1,0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		//update the ball
		ball.update(Gdx.graphics.getDeltaTime());

		modelBatch.begin(cam);

		//render the ball
		ball.render(modelBatch, environment);
		//render the map
		map.render(modelBatch);
		//render the tree
		tree.render(modelBatch,environment);

		modelBatch.end();


		//show the frame rate
		batch.begin();
		font.draw(batch, Gdx.graphics.getFramesPerSecond() + " fps", 3, Gdx.graphics.getHeight() - 3);
		batch.end();

	}
	
	@Override
	public void dispose () {
		modelBatch.dispose();
		ball.dispose();
		map.dispose();
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
