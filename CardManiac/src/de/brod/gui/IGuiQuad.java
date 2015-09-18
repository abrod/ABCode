package de.brod.gui;

import javax.microedition.khronos.opengles.GL10;

public interface IGuiQuad {

	void close();

	boolean draw(GL10 pGL10, long currentTime);

	float getXY();

	boolean touches(float eventX, float eventY);

	void moveTo(float eventX, float eventY);

	void slideTo(float X, float Y);

	void setColor(int a, int r, int g, int b);

	boolean slideTo(long l);
}
