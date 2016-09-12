/**
 * 
 */
package com.neptuneDockyard;

import java.io.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import com.threed.jpct.*;
import com.threed.jpct.util.*;

import org.lwjgl.openal.AL;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.ResourceLoader;

public class Engine {

	public Logger logger = new Logger();
	
	// JPCT controllers
	
	private KeyMapper keyMap = null;

	private boolean fullscreen = false;
	private boolean openGL = false;
	private boolean wireframe = false;

	// JPCT variables

	private Object3D playerShip = null;
	private Object3D cell = null;
	private Object3D virus = null;
	private Object3D bacteria = null;
	private Object3D phage = null;
	private Object3D dna = null;
	private Object3D mitochondria = null;
	
	private FrameBuffer buffer = null;
	private World theWorld = null;
	private TextureManager texMan = null;
	private Camera camera = null;

	// textures

	private Texture[] textures[] = null;

	// player location

	private Matrix playerDirection = new Matrix();
	private SimpleVector tempVector = new SimpleVector();

	// framebuffer size

	private int width = 800;
	private int height = 600;

	// AWT variables

	private Frame frame = null;
	private Graphics gFrame = null;
	private BufferStrategy buffStrat = null;
	private GraphicsDevice device = null;
	private int titleBarHeight = 0;
	private int leftBorderWidth = 0;
	private int switchMode = 0;

	private int fps;
	private int lastFps;
	private long totalFps;

	private int pps;
	private int lastPps;

	private boolean isIdle = false;
	private boolean exit = false;

	// key flags

	private boolean left = false;
	private boolean right = false;
	private boolean up = false;
	private boolean down = false;
	private boolean forward = false;
	private boolean back = false;

	// audio and music
	
	private Audio oggStream = null;

	public void logging_init() {
		logger.setLogLevel(logger.LL_VERBOSE);
		logger.log("Log level set: " + Integer.toString(logger.getLogLevel()));
	}

	public Engine(String[] args) {
		// TODO Auto-generated constructor stub
		// evaluate args here

		logging_init();
		Logger.log("Starting Engine");

		isIdle = false;
		switchMode = 0;
		totalFps = 0;
		fps = 0;
		lastFps = 0;

		// init world instance and get TextureManager

		theWorld = new World();
		texMan = TextureManager.getInstance();
		
		// set up config
		
		Config.glVerbose = true;
		Config.glAvoidTextureCopies = true;
		Config.maxPolysVisible = 1000;
		Config.glColorDepth = 24;
		Config.glFullscreen = false;
		Config.farPlane = 4000;
		Config.glShadowZBias = 0.8f;
		Config.lightMul = 1;
		Config.collideOffset = 500;
		Config.glTrilinear = true;
		org.lwjgl.opengl.Display.setTitle("DeepSea JPCT");

		// set up lighting

		Config.fadeoutLight = false;
		Config.linearDiv = 100;
		Config.lightDiscardDistance = 350;
		theWorld.getLights().setOverbrightLighting(
				Lights.OVERBRIGHT_LIGHTING_DISABLED);
		theWorld.getLights().setRGBScale(Lights.RGB_SCALE_2X);
		theWorld.setAmbientLight(100, 150, 150);

		// place light sources

		theWorld.addLight(new SimpleVector(0, 0, 0), 5, 10, 15);

		// add fog

		theWorld.setFogging(World.FOGGING_ENABLED);
		theWorld.setFogParameters(500, 0, 0, 0);
		
		// add textures
		
		try {
			logger.log("loading textures");
			Texture wallTex = null;
//			texMan.getInstance().addTexture("textures/artery_cells.jpg", wallTex);
			texMan.getInstance().addTexture("wallTex", new Texture("assets/textures/artery_cells.jpg"));
			Texture cells1Tex = null;
//			texMan.getInstance().addTexture("textures/cells_test1.jpg", cells1Tex);
			texMan.getInstance().addTexture("cells1Tex", new Texture("assets/textures/cells_test1.jpg"));
			Texture cells2Tex = null;
//			texMan.getInstance().addTexture("textures/cells_test2.jpg", cells2Tex);
			texMan.getInstance().addTexture("cells2Tex", new Texture("assets/textures/cells_test2.jpg"));
			Texture cells3Tex = null;
//			texMan.getInstance().addTexture("textures/cells_test3.jpg", cells3Tex);
			texMan.getInstance().addTexture("cells3Tex", new Texture("assets/textures/cells_test3.jpg"));
			Texture cells4Tex = null;
//			texMan.getInstance().addTexture("textures/cells_test4.jpg", cells4Tex);
			texMan.getInstance().addTexture("cells4Tex", new Texture("assets/textures/cells_test4.jpg"));
			Texture cells5Tex = null;
//			texMan.getInstance().addTexture("textures/cells_test5.jpg", cells5Tex);
			texMan.getInstance().addTexture("cells5Tex", new Texture("assets/textures/cells_test5.jpg"));
			logger.log("finished loading textures");
		} catch(Exception ex) {
			logger.log("error: textures not loaded");
			logger.log(ex.getMessage());
		}
		
		// add models
		
		try {
			logger.log("loading models");
			playerShip = Primitives.getSphere(10f);
//			playerShip = Loader.loadOBJ("assets/models/WarpShip.obj", null, .1f)[0];
			playerShip.setTexture("wallTex");
			playerShip.setEnvmapped(Object3D.ENVMAP_ENABLED);
			playerShip.build();
			playerShip.compile();
			theWorld.addObject(playerShip);
			logger.log("finished loading models");
		} catch(Exception ex) {
			logger.log("error: models not loaded");
			logger.log(ex.getMessage());
		}
		
		// add sounds
		
		try {
			logger.log("loading sounds");
			oggStream = AudioLoader.getStreamingAudio("OGG", ResourceLoader.getResource("assets/audio/infini1.ogg"));
		} catch(Exception ex) {
			logger.log("error: sounds not loaded");
			logger.log(ex.getMessage());
		}
		
		// add camera
		
		logger.log("adding camera, setting position");
		camera = theWorld.getCamera();
		camera.setPosition(50, -50, -5);
		camera.lookAt(playerShip.getTransformedCenter());
		
		// add buffer
		
		logger.log("adding framebuffer");
		buffer = new FrameBuffer(width, height, FrameBuffer.SAMPLINGMODE_NORMAL);
		buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);
		buffer.enableRenderer(IRenderer.RENDERER_OPENGL);
		
		// now go to game loop

	}

	public void init() {
		// TODO Auto-generated method stub
		logger.log("Engine init");

		keyMap = new KeyMapper();
	}

	public void run() {
		// TODO Auto-generated method stub
		logger.log("Engine running");
		
		oggStream.playAsMusic(1.0f, 1.0f, true);
		gameLoop();
	}
	
	public void update() {
		// TODO Auto-generated method stub
		SoundStore.get().poll(0);

		KeyState state = keyMap.poll();
		
		// keyboard controls
		if(state.getState() == KeyState.PRESSED && state.getKeyCode() == KeyEvent.VK_LEFT) {
			left = true;
			logger.log("key pressed: left");
		} else left = false;
		if(state.getState() == KeyState.PRESSED && state.getKeyCode() == KeyEvent.VK_RIGHT) {
			right = true;
			logger.log("key pressed: right");
		} else right = false;
		if(state.getState() == KeyState.PRESSED && state.getKeyCode() == KeyEvent.VK_UP) {
			up = true;
			logger.log("key pressed: up");
		} else up = false;
		if(state.getState() == KeyState.PRESSED && state.getKeyCode() == KeyEvent.VK_DOWN) {
			down = true;
			logger.log("key pressed: down");
		} else down = false;
		if(state.getState() == KeyState.PRESSED && state.getKeyCode() == KeyEvent.VK_W) {
			forward = true;
			logger.log("key pressed: forward");
		} else forward = false;
		if(state.getState() == KeyState.PRESSED && state.getKeyCode() == KeyEvent.VK_S) {
			back = true;
			logger.log("key pressed: back");
		} else back = false;
		
		// exit game
		if(state.getState() == KeyState.PRESSED && state.getKeyCode() == KeyEvent.VK_ESCAPE) gameShutdown();
	}

	public void gameLoop() {
		// TODO Auto-generated method stub
		logger.log("Game loop");
		
		while(!org.lwjgl.opengl.Display.isCloseRequested()) {
			update();
			buffer.clear(java.awt.Color.WHITE);
			theWorld.renderScene(buffer);
			theWorld.draw(buffer);
			buffer.update();
			buffer.displayGLOnly();
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
		
		gameShutdown();
	}
	
	public void gameShutdown() {
		buffer.disableRenderer(IRenderer.RENDERER_OPENGL);
		buffer.dispose();
		oggStream.stop();
		keyMap.destroy();
		AL.destroy();
		System.exit(0);
	}

}
