package de.brod.opengl;

import javax.microedition.khronos.opengles.GL10;

public class Button implements Shape {

	private Rectangle[]		buttonItems;
	private float			posX, posY;

	private boolean			landscape, pressed;
	private ButtonAction	action;

	public Button(float width, float height, float x, float y, float z, GLGrid grid, ButtonAction action) {
		this.action = action;
		pressed = false;
		// create 9 items
		buttonItems = new Rectangle[3];
		landscape = height < width;
		initButtonItems(width, height);
		setPosition(x, y, z);
		setGrid(grid);
	}

	public final void doAction() {
		action.doAction();
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
	public float getZ() {
		return buttonItems[1].getZ();
	}

	private void initButtonItems(float width, float height) {
		float size = Math.min(width, height) / 3;
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
	}

	public boolean isPressed() {
		return pressed;
	}

	@Override
	public void moveTo(float x, float y) {
		for (Rectangle rectangle : buttonItems) {
			rectangle.moveTo(x, y);
		}
	}

	@Override
	public void setColor(float red, float green, float blue, float alpha) {
		for (Rectangle rectangle : buttonItems) {
			rectangle.setColor(red, green, blue, alpha);
		}
	}

	private void setGrid(GLGrid grid) {
		if (landscape) {
			grid.setCount(6, 1);
			buttonItems[0].setGrid(grid, 0, 0, 3, 0);
			buttonItems[1].setGrid(grid, 1, 0, 4, 0);
			buttonItems[2].setGrid(grid, 2, 0, 5, 0);
		} else {
			grid.setCount(2, 3);
			buttonItems[0].setGrid(grid, 0, 2, 1, 0);
			buttonItems[1].setGrid(grid, 0, 1, 1, 1);
			buttonItems[2].setGrid(grid, 0, 0, 1, 2);
		}
	}

	@Override
	public void setPosition(float x, float y, float z) {
		for (int i = -1; i <= 1; i++) {
			buttonItems[i + 1].setPosition(x + posX * i, y + posY * i, z);
		}
	}

	public void setPressed(boolean pressed) {
		this.pressed = pressed;
		int angle = pressed ? 180 : 0;
		if (landscape) {
			for (Rectangle rectangle : buttonItems) {
				rectangle.setRotateX(angle);
			}
		} else {
			for (Rectangle rectangle : buttonItems) {
				rectangle.setRotateY(angle);
			}
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
	public void setZ(float zValue) {
		for (Rectangle rectangle : buttonItems) {
			rectangle.setZ(zValue);
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
