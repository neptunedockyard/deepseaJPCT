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

public class Engine {

	public static Logger logger = new Logger();

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

	private Texture numbers = null;

	// player location

	private Matrix playerDirection = new Matrix();
	private SimpleVector tempVector = new SimpleVector();

	// framebuffer size

	private int width = 1024;
	private int height = 768;

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

	// key mapper variables

	private KeyMapper keyMapper = null;

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

		// set up lighting

		Config.fadeoutLight = true;
		Config.linearDiv = 100;
		Config.lightDiscardDistance = 350;
		theWorld.getLights().setOverbrightLighting(
				Lights.OVERBRIGHT_LIGHTING_DISABLED);
		theWorld.getLights().setRGBScale(Lights.RGB_SCALE_2X);
		theWorld.setAmbientLight(10, 15, 15);

		// place light sources

		theWorld.addLight(new SimpleVector(0, 0, 0), 5, 10, 15);

		// add fog

		theWorld.setFogging(World.FOGGING_ENABLED);
		theWorld.setFogParameters(500, 0, 0, 0);
		Config.farPlane = 500;

	}

	public void init() {
		// TODO Auto-generated method stub
		logger.log("Engine init");

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
	}

	public void run() {
		// TODO Auto-generated method stub
		logger.log("Engine running");
	}

	public void gameLoop() {
		// TODO Auto-generated method stub
		logger.log("Game loop");
	}

}
