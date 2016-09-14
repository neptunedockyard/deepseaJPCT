package com.neptuneDockyard;

import org.lwjgl.input.Mouse;

import com.threed.jpct.Camera;
import com.threed.jpct.Logger;
import com.threed.jpct.Object3D;
import com.threed.jpct.SimpleVector;

public class MouseMapper {
	
	private Logger logger;
	private Camera camera;
	private Object3D player;
	
	public MouseMapper(Camera cam, Object3D obj) {
		this.camera = cam;
		this.player = obj;
	}

	public void cameraUpdate() {
		int dx = Mouse.getDX();
		int dy = Mouse.getDY();
		
		if(dx != 0) {
			camera.rotateAxis(camera.getYAxis(), dx / 500f);
		}
		if(dy != 0) {
			camera.rotateX(dy / 500f);
		}
//		Logger.log("camera location: " + Mouse.getX() + "," + Mouse.getY());
//		if(zoomLock) {
//			camera.lookAt(player.getTransformedCenter());
//		}
	}

}
