package de.brod.opengl;

import javax.microedition.khronos.opengles.GL10;

public interface Shape {

	/**
	 * Render the mesh.
	 *
	 * @param gl
	 *            the OpenGL context to render to.
	 */
	void draw(GL10 gl);

	float getPosition();

	float getX();

	float getY();

	void moveTo(float x, float y);

	void setPosition(float x, float y, float z);

	void setRotateX(float rotateX);

	void setRotateY(float rotateY);

	void setRotateZ(float rotateZ);

	boolean touch(float x, float y);

	void untouch();
}