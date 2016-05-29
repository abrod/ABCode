package de.brod.opengl;

import javax.microedition.khronos.opengles.GL10;

public class Button implements Shape {

	private Rectangle[]	buttonItems;
	private float		posX, posY;

	public Button(float width, float height, float x, float y, float z) {
		// create 9 items
		buttonItems = new Rectangle[9];
		float edgeSize = Math.min(width, width) / 3;
		float wd = width - edgeSize * 2;
		float hg = height - edgeSize * 2;
		// create items
		buttonItems[0] = new Rectangle(edgeSize, edgeSize);
		buttonItems[1] = new Rectangle(wd, edgeSize);
		buttonItems[2] = new Rectangle(edgeSize, edgeSize);
		buttonItems[3] = new Rectangle(edgeSize, hg);
		buttonItems[4] = new Rectangle(wd, hg);
		buttonItems[5] = new Rectangle(edgeSize, hg);
		buttonItems[6] = new Rectangle(edgeSize, edgeSize);
		buttonItems[7] = new Rectangle(wd, edgeSize);
		buttonItems[8] = new Rectangle(edgeSize, edgeSize);
		posX = (wd + edgeSize) / 2;
		posY = (hg + edgeSize) / 2;
		setPosition(x, y, z);
	}

	@Override
	public void draw(GL10 gl) {
		// TODO Auto-generated method stub

	}

	@Override
	public float getPosition() {
		return buttonItems[4].getPosition();
	}

	@Override
	public float getX() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getY() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void moveTo(float x, float y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGrid(GLGrid grid, int x1, int y1, int x2, int y2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPosition(float x, float y, float z) {
		int k = 0;
		for (int j = 1; j >= -1; j--) {
			for (int i = -1; i <= 1; i++) {
				buttonItems[k].setPosition(x + posX * i, y + posY * j, z);
				k++;
			}
		}
	}

	@Override
	public void setRotateX(float rotateX) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRotateY(float rotateY) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRotateZ(float rotateZ) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean touch(float x, float y) {
		boolean touch = false;
		for (Rectangle rectangle : buttonItems) {
			if (rectangle.touch(x, y)) {
				touch = true;
			}
		}
		return touch;
	}

	@Override
	public void untouch() {
		for (Rectangle rectangle : buttonItems) {
			rectangle.untouch();
		}
	}

}
