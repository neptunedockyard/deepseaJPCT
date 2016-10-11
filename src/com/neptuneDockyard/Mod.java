package com.neptuneDockyard;

import com.threed.jpct.GenericVertexController;
import com.threed.jpct.IVertexController;
import com.threed.jpct.Mesh;
import com.threed.jpct.SimpleVector;

public class Mod extends GenericVertexController {

	private static final long serialVersionUID = 1L;
	public void apply() {
		// TODO Auto-generated method stub
		SimpleVector[] s = getSourceMesh();
		SimpleVector[] d = getDestinationMesh();
		for (int i = 0; i < s.length; i++) {
			d[i].z = s[i].z	- (10f * ((float) Math.sin(s[i].x / 50f) + (float) Math.cos(s[i].y / 50f)));
			d[i].x = s[i].x;
			d[i].y = s[i].y;
		}
	}
}
