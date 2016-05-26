package de.brod.opengl;

import javax.microedition.khronos.opengles.GL10;

public interface Shape extends Comparable<Shape> {

	/**
	 * Render the mesh.
	 *
	 * @param gl
	 *            the OpenGL context to render to.
	 */
	void draw(GL10 gl);

	float getX();

	float getY();

	void moveTo(float x, float y, float z);

	/**
	 * Set the bitmap to load into a texture.
	 *
	 * @param bitmap
	 */
	void setGrid(GLGrid grid, int x1, int y1, int x2, int y2);

	void setPosition(float x, float y, float z);

	void setRotateX(float rotateX);

	void setRotateY(float rotateY);

	void setRotateZ(float rotateZ);

	boolean touch(float x, float y);

	void untouch();

}