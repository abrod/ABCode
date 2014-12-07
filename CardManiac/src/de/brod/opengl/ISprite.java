package de.brod.opengl;

import javax.microedition.khronos.opengles.GL10;

public interface ISprite extends Comparable<ISprite> {

	void draw(GL10 gl);

	void moveTo(float eventX, float eventY);

	void setColor(int pr, int pg, int pb, int pa);

	ISprite setGrid(int piPosX, int piPosY);

	void setId(int piId);

	void mouseDown(float eventX, float eventY);

	void mouseUp();

	void setPosition(float x, float y, float rotX);

	boolean touches(float eventX, float eventY);

}