package de.brod.opengl;

import javax.microedition.khronos.opengles.GL10;

public class Button implements Shape {

	private Rectangle[]	buttonItems;
	private float		posX, posY;
	private boolean		landscape;

	public Button(float width, float height, float x, float y, float z) {
		// create 9 items
		buttonItems = new Rectangle[3];
		float size = Math.min(width, height) / 3;
		landscape = height < width;
		if (landscape) {
			float wd = width - size * 2;
			// create items
			buttonItems[0] = new Rectangle(size, height);
			buttonItems[1] = new Rectangle(wd, height);
			buttonItems[2] = new Rectangle(size, height);
			posX = (wd + size) / 2;
			posY = 0;
		} else {
			float hg = height - size * 2;
			buttonItems[0] = new Rectangle(width, size);
			buttonItems[1] = new Rectangle(width, hg);
			buttonItems[2] = new Rectangle(width, size);
			posY = (hg + size) / 2;
			posX = 0;
		}
		setPosition(x, y, z);
	}

	@Override
	public void draw(GL10 gl) {
		for (Rectangle rectangle : buttonItems) {
			rectangle.draw(gl);
		}
	}

	@Override
	public float getPosition() {
		return buttonItems[1].getPosition();
	}

	@Override
	public float getX() {
		return buttonItems[1].getX();
	}

	@Override
	public float getY() {
		return buttonItems[1].getY();
	}

	@Override
	public void moveTo(float x, float y) {
		for (Rectangle rectangle : buttonItems) {
			rectangle.moveTo(x, y);
		}
	}

	public void setGrid(GLGrid grid) {
		if (landscape) {
			grid.setCount(6, 1);
			buttonItems[0].setGrid(grid, 0, 0, 3, 0);
			buttonItems[1].setGrid(grid, 1, 0, 4, 0);
			buttonItems[2].setGrid(grid, 2, 0, 5, 0);
		} else {
			grid.setCount(2, 3);
			buttonItems[0].setGrid(grid, 0, 0, 1, 0);
			buttonItems[1].setGrid(grid, 0, 1, 1, 1);
			buttonItems[2].setGrid(grid, 0, 2, 1, 2);
		}
	}

	@Override
	public void setPosition(float x, float y, float z) {
		int k = 0;
		for (int i = -1; i <= 1; i++) {
			buttonItems[k].setPosition(x + posX * i, y + posY * i, z);
			k++;
		}
	}

	@Override
	public void setRotateX(float rotateX) {
		for (Rectangle rectangle : buttonItems) {
			rectangle.setRotateX(rotateX);
		}
	}

	@Override
	public void setRotateY(float rotateY) {
		for (Rectangle rectangle : buttonItems) {
			rectangle.setRotateY(rotateY);
		}
	}

	@Override
	public void setRotateZ(float rotateZ) {
		for (Rectangle rectangle : buttonItems) {
			rectangle.setRotateZ(rotateZ);
		}
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
