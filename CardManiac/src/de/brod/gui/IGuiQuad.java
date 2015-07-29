package de.brod.gui;

import javax.microedition.khronos.opengles.GL10;

public interface IGuiQuad {

	void close();

	void draw(GL10 pGL10);

	float getXY();

	boolean touches(float eventX, float eventY);

	void moveTo(float eventX, float eventY);

}
