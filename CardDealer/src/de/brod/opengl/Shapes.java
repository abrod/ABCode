package de.brod.opengl;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;

public class Shapes extends ArrayList<Shape> {

	/**
	 *
	 */
	private static final long serialVersionUID = 8434418783314671925L;

	public void draw(GL10 gl) {
		for (Shape mesh : this) {
			mesh.draw(gl);
		}
	}
}
