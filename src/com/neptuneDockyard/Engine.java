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

import org.lwjgl.input.Mouse;
import org.lwjgl.openal.AL;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.util.ResourceLoader;

public class Engine {

	public Logger logger = new Logger();
	
	// JPCT controllers
	
	private KeyMapper keyMap = null;
	private MouseMapper mouseMap = null;
	private KeyState state = null;
	
	// private controllers
	
	private Chunk chunkCon = null;

	private boolean fullscreen = false;
	private boolean openGL = false;
	private boolean wireframe = false;

	// JPCT variables

	private Object3D playerShip = null;
	private Object3D cell = null;
	private Object3D virus = null;
	private Object3D microbe = null;
	private Object3D bacteria = null;
	private Object3D phage = null;
	private Object3D dna = null;
	private Object3D mitochondria = null;
	
	private FrameBuffer buffer = null;
	private World theWorld = null;
	private TextureManager texMan = null;
	private Camera camera = null;
	private SkyBox skyBox = null;
	private Object3D skyDome = null;
	
	//TODO remove test object
	private Object3D testPlane = null;
	private Projector projector = null;
	private ShadowHelper sh = null;

	// textures

	private Texture[] textures[] = null;

	// player location

	private Matrix playerDirection = new Matrix();
	private SimpleVector tempVector = new SimpleVector();
	
	// framebuffer size

	private int width = 800;
	private int height = 600;

	// key flags

	private boolean left = false;
	private boolean right = false;
	private boolean up = false;
	private boolean down = false;
	private boolean forward = false;
	private boolean back = false;
	private boolean zoomLock = false;
	private boolean sprint = false;
	private float camSpeed = (float) 0.1;

	// audio and music
	
	private Audio oggStream = null;

	public void logging_init() {
		Logger.setLogLevel(Logger.LL_VERBOSE);
		Logger.log("Log level set: " + Integer.toString(Logger.getLogLevel()));
	}

	public Engine(String[] args) {
		// TODO evaluate args here

		logging_init();
		Logger.log("Starting Engine");

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
		
		// set up mouse
		
		Mouse.setGrabbed(true);

		// set up lighting

		Config.fadeoutLight = true;
		Config.linearDiv = 100;
		Config.lightDiscardDistance = 35;
		theWorld.getLights().setOverbrightLighting(
				Lights.OVERBRIGHT_LIGHTING_DISABLED);
		theWorld.getLights().setRGBScale(Lights.RGB_SCALE_2X);
		theWorld.setAmbientLight(100, 150, 150);

		// place light sources

		theWorld.addLight(new SimpleVector(-10, -10, -10), 5, 10, 15);

		// add fog

		theWorld.setFogging(World.FOGGING_ENABLED);
		theWorld.setFogParameters(100, 0, 0, 0);
		
		// add textures
		
		try {
			Logger.log("loading textures");
			Texture wallTex = null;
//			texMan.getInstance().addTexture("textures/artery_cells.jpg", wallTex);
			TextureManager.getInstance().addTexture("wallTex", new Texture("assets/textures/artery_cells.jpg"));
			Texture cells1Tex = null;
//			texMan.getInstance().addTexture("textures/cells_test1.jpg", cells1Tex);
			TextureManager.getInstance().addTexture("cells1Tex", new Texture("assets/textures/cells_test1.jpg"));
			Texture cells2Tex = null;
//			texMan.getInstance().addTexture("textures/cells_test2.jpg", cells2Tex);
			TextureManager.getInstance().addTexture("cells2Tex", new Texture("assets/textures/cells_test2.jpg"));
			Texture cells3Tex = null;
//			texMan.getInstance().addTexture("textures/cells_test3.jpg", cells3Tex);
			TextureManager.getInstance().addTexture("cells3Tex", new Texture("assets/textures/cells_test3.jpg"));
			Texture cells4Tex = null;
//			texMan.getInstance().addTexture("textures/cells_test4.jpg", cells4Tex);
			TextureManager.getInstance().addTexture("cells4Tex", new Texture("assets/textures/cells_test4.jpg"));
			Texture cells5Tex = null;
//			texMan.getInstance().addTexture("textures/cells_test5.jpg", cells5Tex);
			TextureManager.getInstance().addTexture("cells5Tex", new Texture("assets/textures/cells_test5.jpg"));
			Logger.log("finished loading textures");
		} catch(Exception ex) {
			Logger.log("error: textures not loaded");
			Logger.log(ex.getMessage());
		}
		
		// add model
		
		try {
			//load player model
			Logger.log("loading player model");
			playerShip = Primitives.getSphere(2f);
//			playerShip = Loader.loadOBJ("assets/models/WarpShip.obj", null, .1f)[0];
//			playerShip = Loader.load3DS("assets/models/virus_smooth.3ds", 1f)[0];
			playerShip.setTexture("wallTex");
			playerShip.setEnvmapped(Object3D.ENVMAP_ENABLED);
			playerShip.build();
			playerShip.compile();
			theWorld.addObject(playerShip);
			
			//load enemy model
			Logger.log("loading virus model");
			//virus = Loader.load3DS("assets/models/virus_smooth.3ds", 3f)[0];
			virus = Primitives.getEllipsoid(2f, 3f);
			virus.setTexture("cells4Tex");
			virus.setEnvmapped(Object3D.ENVMAP_ENABLED);
			virus.build();
			virus.compile();
			virus.translate(10f, 10f, 10f);
			theWorld.addObject(virus);
			
			//load enemy model
			Logger.log("loading microbe model");
			microbe = Loader.load3DS("assets/models/microbe_smooth.3ds", 3f)[0];
			microbe.setTexture("cells4Tex");
			microbe.setEnvmapped(Object3D.ENVMAP_ENABLED);
			microbe.build();
			microbe.compile();
			microbe.translate(-10f, -10f, -10f);
			//theWorld.addObject(microbe);
			
			//load surface
			testPlane = Primitives.getPlane(20, 30);
			testPlane.rotateX((float) (Math.PI/2f));
			testPlane.setSpecularLighting(true);
			testPlane.setCollisionMode(Object3D.COLLISION_CHECK_OTHERS);
			testPlane.setTexture("cells3Tex");
			testPlane.compileAndStrip();
			Mesh planeMesh = testPlane.getMesh();
			planeMesh.setVertexController(new Mod(), false);
			planeMesh.applyVertexController();
			planeMesh.removeVertexController();
			testPlane.translate(100, 100, 100);
			theWorld.addObject(testPlane);
			
			//load shadows and projector
			projector = new Projector();
			projector.setFOV(1.5f);
			projector.setYFOV(1.5f);
			sh = new ShadowHelper(theWorld, buffer, projector, 2048);
			sh.setCullingMode(false);
			sh.setAmbientLight(new Color(30, 30, 30));
			sh.setLightMode(true);
			sh.setBorder(1);
			sh.addCaster(virus);
			sh.addCaster(playerShip);
			sh.addReceiver(testPlane);
			
			Loader.clearCache();
			Logger.log("finished loading models");
		} catch(Exception ex) {
			//TODO fix loader error, check if model loaded instead of the try catch method
			Logger.log("error: models not loaded");
			Logger.log(ex.getMessage());
		}
		
		// add sounds
		
		try {
			Logger.log("loading sounds");
			oggStream = AudioLoader.getStreamingAudio("OGG", ResourceLoader.getResource("assets/audio/infini1.ogg"));
		} catch(Exception ex) {
			Logger.log("error: sounds not loaded");
			Logger.log(ex.getMessage());
		}
		
		// add skybox
		// TODO figure out skybox issue
		skyBox = new SkyBox("cells5Tex", "cells5Tex", "cells5Tex", "cells5Tex", "cells2Tex", "cells2Tex", 1000f);
		skyBox.compile();
		
//		skyDome = Primitives.getSphere(100f);
//		skyDome.setTexture("cells1Tex");
//		skyDome.setEnvmapped(Object3D.ENVMAP_ENABLED);
//		skyDome.invert();
//		skyDome.setCulling(false);
//		skyDome.calcTextureWrapSpherical();
//		skyDome.build();
//		skyDome.compile();
//		theWorld.addObject(skyDome);
		
		// add camera
		
		Logger.log("adding camera, setting position");
		camera = theWorld.getCamera();
		camera.setFOV(80);
		camera.setPosition(50, -50, -5);
		camera.lookAt(playerShip.getTransformedCenter());
		
		// add buffer
		
		Logger.log("adding framebuffer");
		buffer = new FrameBuffer(width, height, FrameBuffer.SAMPLINGMODE_NORMAL);
		buffer.disableRenderer(IRenderer.RENDERER_SOFTWARE);
		buffer.enableRenderer(IRenderer.RENDERER_OPENGL);
		
		// now go to game loop
	}

	public void init() {
		Logger.log("Engine init");

		keyMap = new KeyMapper();
		mouseMap = new MouseMapper(camera, playerShip);
	}

	public void run() {
		Logger.log("Engine running");
		
		//oggStream.playAsMusic(1.0f, 1.0f, true);
		gameLoop();
	}
	
	public void update() {
		SoundStore.get().poll(0);
		
		mouseMap.cameraUpdate();
		updatePosition();

		while((state = keyMap.poll()) != KeyState.NONE) {
		
			if(state.getKeyCode() == KeyEvent.VK_A && state.getState() == KeyState.PRESSED) {
				left = state.getState();
				Logger.log("key pressed: a " + camera.getPosition().toString());
			} else if(state.getKeyCode() == KeyEvent.VK_A && state.getState() == KeyState.RELEASED) {
				left = false;
				Logger.log("key released: a " + camera.getPosition().toString());
			}
			if(state.getKeyCode() == KeyEvent.VK_D && state.getState() == KeyState.PRESSED) {
				right = state.getState();
				Logger.log("key pressed: d " + camera.getPosition().toString());
			} else if(state.getKeyCode() == KeyEvent.VK_D && state.getState() == KeyState.RELEASED) {
				right = false;
				Logger.log("key released: d " + camera.getPosition().toString());
			}
			if(state.getKeyCode() == KeyEvent.VK_SPACE && state.getState() == KeyState.PRESSED) {
				up = state.getState();
				Logger.log("key pressed: space " + camera.getPosition().toString());
			} else if(state.getKeyCode() == KeyEvent.VK_SPACE && state.getState() == KeyState.RELEASED) {
				up = false;
				Logger.log("key released: space " + camera.getPosition().toString());
			}
			if(state.getKeyCode() == KeyEvent.VK_Z && state.getState() == KeyState.PRESSED) {
				down = state.getState();
				Logger.log("key pressed: z " + camera.getPosition().toString());
			} else if(state.getKeyCode() == KeyEvent.VK_Z && state.getState() == KeyState.RELEASED) {
				down = false;
				Logger.log("key released: z " + camera.getPosition().toString());
			}
			if(state.getKeyCode() == KeyEvent.VK_W && state.getState() == KeyState.PRESSED) {
				forward = state.getState();
				Logger.log("key pressed: w " + camera.getPosition().toString());
			} else if(state.getKeyCode() == KeyEvent.VK_W && state.getState() == KeyState.RELEASED) {
				forward = false;
				Logger.log("key released: w " + camera.getPosition().toString());
			}
			if(state.getKeyCode() == KeyEvent.VK_S && state.getState() == KeyState.PRESSED) {
				back = state.getState();
				Logger.log("key pressed: s " + camera.getPosition().toString());
			} else if(state.getKeyCode() == KeyEvent.VK_S && state.getState() == KeyState.RELEASED) {
				back = false;
				Logger.log("key released: s " + camera.getPosition().toString());
			}
			if(state.getKeyCode() == KeyEvent.VK_SHIFT && state.getState() == KeyState.PRESSED) {
				sprint = state.getState();
				Logger.log("key pressed: shift " + camera.getPosition().toString());
			} else if(state.getKeyCode() == KeyEvent.VK_SHIFT && state.getState() == KeyState.RELEASED) {
				sprint = false;
				Logger.log("key released: shift " + camera.getPosition().toString());
			}
			
			// lock in camera
			if(state.getState() == KeyState.PRESSED && state.getKeyCode() == KeyEvent.VK_L) zoomLock ^= true;
			
			// exit game
			if(state.getState() == KeyState.PRESSED && state.getKeyCode() == KeyEvent.VK_ESCAPE) gameShutdown();
		}
		
	}

	public void gameLoop() {
		Logger.log("Game loop");
		
		while(!org.lwjgl.opengl.Display.isCloseRequested()) {
			update();
			buffer.clear(java.awt.Color.BLACK);
			
			// render skybox
			skyBox.render(theWorld, buffer);
			
			theWorld.renderScene(buffer);
			theWorld.drawWireframe(buffer, java.awt.Color.BLACK);
			theWorld.draw(buffer);
			buffer.update();
			buffer.displayGLOnly();
//			try {
//				Thread.sleep(10);
//			} catch (InterruptedException e) {
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
	
	public void updatePosition() {
		if(sprint) camSpeed = 1;
		else camSpeed = (float) 0.01;
		
		if(left) 	camera.moveCamera(Camera.CAMERA_MOVELEFT, 	camSpeed);
		if(right) 	camera.moveCamera(Camera.CAMERA_MOVERIGHT, 	camSpeed);
		if(up) 		camera.moveCamera(Camera.CAMERA_MOVEUP, 	camSpeed);
		if(down) 	camera.moveCamera(Camera.CAMERA_MOVEDOWN, 	camSpeed);
		if(forward) camera.moveCamera(Camera.CAMERA_MOVEIN, 	camSpeed);
		if(back) 	camera.moveCamera(Camera.CAMERA_MOVEOUT, 	camSpeed);
	}


	public void getCollisions() {
		
	}
}
