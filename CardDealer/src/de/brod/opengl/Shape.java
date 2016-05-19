package de.brod.opengl;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;

public interface Shape {

	/**
	 * Render the mesh.
	 *
	 * @param gl
	 *            the OpenGL context to render to.
	 */
	void draw(GL10 gl);

	/**
	 * Set the bitmap to load into a texture.
	 *
	 * @param bitmap
	 */
	void loadBitmap(Bitmap bitmap);

	void moveTo(float x, float y, float z);

	void setPosition(float x, float y, float z);

	void setRotateX(float rotateX);

	void setRotateY(float rotateY);

	void setRotateZ(float rotateZ);

	boolean touch(float x, float y);

	void untouch();

}