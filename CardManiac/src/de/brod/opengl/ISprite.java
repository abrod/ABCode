package de.brod.opengl;

import javax.microedition.khronos.opengles.GL10;

public interface ISprite<E> extends Comparable<ISprite<E>> {

	void draw(GL10 gl);

	E getReference();

	boolean isPositionChanged();

	void moveTo(float eventX, float eventY);

	void savePosition();

	void setColor(int pr, int pg, int pb, int pa);

	ISprite<E> setGrid(int piPosX, int piPosY);

	void setId(int piId);

	boolean setMovePosition(float f);

	void setOffset(float eventX, float eventY);

	void setPosition(float x, float y, float rotX);

	void setReference(E rect);

	boolean touches(float eventX, float eventY);

}