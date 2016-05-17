package de.brod.opengl;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class Meshes extends ArrayList<Mesh> {

	/**
	 *
	 */
	private static final long serialVersionUID = 8434418783314671925L;

	public void draw(GL10 gl) {
		for (Mesh mesh : this) {
			mesh.draw(gl);
		}
	}
}
