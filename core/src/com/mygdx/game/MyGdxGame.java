package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class MyGdxGame extends ApplicationAdapter {
	private ModelBatch modelBatch;
	private Camera cam;
	private CameraInputController camController;

	private Model model;
	private ModelInstance ballInstance;
	private Vector3 pos;

	private Environment environment;

	private BitmapFont font;
	private SpriteBatch batch;

	static float magnitude;
	private String[] paths;

	private Map map;


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

		ModelBuilder modelBuilder = new ModelBuilder();
		model = modelBuilder.createSphere(5f, 5f, 5f,15,15,
				new Material(ColorAttribute.createDiffuse(Color.WHITE)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal |
						VertexAttributes.Usage.TextureCoordinates);
		ballInstance = new ModelInstance(model);
		ballInstance.transform.translate(5, map.getHeigth(new Vector2(5,-5)),-5);

		pos = ballInstance.transform.getTranslation(new Vector3());

		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -2f, -1.8f, -1.2f));

		//create the map and load it
		map = new Map(paths, environment, magnitude);

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
		Gdx.gl.glClearColor(0, 0,	 0, 0);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);


		pos = ballInstance.transform.getTranslation(new Vector3());
		move();

		modelBatch.begin(cam);
		//render the ball
		modelBatch.render(ballInstance, environment);
		//render the map
		map.render(modelBatch);
		modelBatch.end();


		//show the frame rate
		batch.begin();
		font.draw(batch, Gdx.graphics.getFramesPerSecond() + " fps", 3, Gdx.graphics.getHeight() - 3);
		batch.end();

	}
	
	@Override
	public void dispose () {
		modelBatch.dispose();
		model.dispose();
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

	private void move(){

		if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {

			ballInstance.transform.translate(-1, 0,0);
			ballInstance.calculateTransforms();
		}

		if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
			ballInstance.transform.translate(1f, 0, 0);
			ballInstance.calculateTransforms();
		}

		if(Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
			ballInstance.transform.translate(0, 0, 1f);
			ballInstance.calculateTransforms();
		}

		if(Gdx.input.isKeyPressed(Input.Keys.UP)) {
			ballInstance.transform.translate(0, 0, -1f);
			ballInstance.calculateTransforms();
		}

		if(Gdx.input.isKeyPressed(Input.Keys.T)) {
			ballInstance.transform.translate(0, -1, 0);
			ballInstance.calculateTransforms();
		}


		if(Gdx.input.isKeyPressed(Input.Keys.G)) {
			ballInstance.transform.translate(0, 1, 0);
			ballInstance.calculateTransforms();
		}

		Vector3 newPos = ballInstance.transform.getTranslation(new Vector3());

		float translH = map.getHeigth(new Vector2(newPos.x, newPos.z)) - map.getHeigth(new Vector2(pos.x, pos.z));
		ballInstance.transform.translate(0, translH, 0);
		ballInstance.calculateTransforms();


		System.out.println(pos.x + " " + pos.y +" " + pos.z + " height: "+ map.getHeigth(new Vector2(pos.x, pos.z)));
	}

}
