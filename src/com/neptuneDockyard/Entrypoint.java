package com.neptuneDockyard;

import java.awt.*;
import java.awt.event.*;

import com.threed.jpct.*;
import com.threed.jpct.util.*;

import org.lwjgl.input.*;

public class Entrypoint {
	
	public static Logger logger;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		logger = new Logger("");
		logger.log("entrypoint", "low");
		Config.glVerbose = true;
		Entrypoint ep = new Entrypoint();
		ep.init();
		ep.gameLoop();
	}
	
	public Entrypoint() {
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

	private void gameLoop() {
		// TODO Auto-generated method stub
		
	}

	private void init() {
		// TODO Auto-generated method stub
		
	}

}
