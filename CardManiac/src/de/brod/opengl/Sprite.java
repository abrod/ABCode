package de.brod.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

public class Sprite<E> implements Comparable<Sprite<E>> {

	float[] vertices = new float[] { -1.0f, -1.0f, 0.0f, -1.0f, 1.0f, 0.0f,
			1.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f };

	float[] range = new float[4];
	protected FloatBuffer vertexBuffer;
	private int mFrame = 0;
	private boolean visible = true;
	Grid<E> grid;

	private float _width, _height;
	private E reference;

	private float r = 1, g = 1, b = 1, a = 1;

	private float _x, _y, ox, oy;
	private float _sx, _sy, _ex, _ey, _dx, _dy, _d;
	private int _xy, _id;

	private boolean _bMoving;

	public Sprite(Grid<E> pGrid, float x, float y, float width, float height) {
		this.grid = pGrid;
		// vertices buffer
		ByteBuffer bb1 = ByteBuffer.allocateDirect(vertices.length * 4);
		bb1.order(ByteOrder.nativeOrder());
		vertexBuffer = bb1.asFloatBuffer();

		_width = width / 2;
		_height = height / 2;
		setXY(x, y);
	}

	private void setXY(float px, float py) {
		_x = px;
		_y = py;
		_xy = (int) (px * 1000f - py * 4000f);
		int iPos = 0;
		for (int i = -1; i <= 1; i += 2) {
			for (int j = -1; j <= 1; j += 2) {
				float x = (px + _width * i);
				float y = getY(py + _height * j, true);
				x = getX(x, y, true);
				vertices[iPos] = x;
				iPos++;
				vertices[iPos] = y;
				iPos += 2;
			}
		}
		vertexBuffer.position(0);
		vertexBuffer.put(vertices);
		vertexBuffer.position(0);

		// min max X
		range[0] = vertices[0];
		range[1] = vertices[0];
		for (int i = 3; i <= 9; i += 3) {
			range[0] = Math.min(range[0], vertices[i]);
			range[1] = Math.max(range[1], vertices[i]);
		}

		// min max y
		range[2] = vertices[1];
		range[3] = vertices[1];
		for (int i = 4; i <= 10; i += 3) {
			range[2] = Math.min(range[2], vertices[i]);
			range[3] = Math.max(range[3], vertices[i]);
		}

	}

	private float getX(float x, float y, boolean pbIn) {
		if (OpenGLView.LANDSCAPE) {
			if (pbIn) {
				return x * (5f - y) / 6f;
			}
			return x / (5f - y) * 6f;
		}
		return x;
	}

	public void setPosition(float px, float py) {
		if (_x != px || _y != py) {
			setXY(px, py);
		}
	}

	public void draw(GL10 gl) {
		if (!visible) {
			return;
		}
		grid.drawTexCoordPointer(gl, mFrame);

		gl.glColor4f(r, g, b, a);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);

	}

	public Sprite<E> setGrid(int piPosX, int piPosY) {
		mFrame = grid.getFrame(piPosX, piPosY);
		return this;
	}

	public boolean touches(float px, float py) {
		if (!visible) {
			return false;
		}
		if (px < range[0] || px > range[1]) {
			return false;
		}
		if (py < range[2] || py > range[3]) {
			return false;
		}

		boolean oddNodes = false;
		int[] points = { 0, 1, 3, 2, 0 };
		float x1, y1, x2 = 0, y2 = 0;
		for (int a = 0; a < points.length; a++) {
			int i = points[a] * 3;
			x1 = vertices[i];
			y1 = vertices[i + 1];
			if (a > 0) {
				if (((y1 < py) && (y2 >= py)) || (y1 >= py) && (y2 < py)) {
					if ((py - y1) / (y2 - y1) * (x2 - x1) < (px - x1)) {
						oddNodes = !oddNodes;
					}
				}
			}
			x2 = x1;
			y2 = y1;
		}
		return oddNodes;

	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public E getReference() {
		return reference;
	}

	public void setReference(E reference) {
		this.reference = reference;
	}

	public void setColor(int color) {
		int r = Color.red(color);
		int g = Color.green(color);
		int b = Color.blue(color);
		int a = Color.alpha(color);
		setColor(r, g, b, a);
	}

	public void setColor(int pr, int pg, int pb, int pa) {
		r = pr / 255f;
		g = pg / 255f;
		b = pb / 255f;
		a = pa / 255f;
	}

	public void setOffset(float eventX, float eventY) {
		float x = eventX;
		float y = getY(eventY, false);
		x = getX(x, y, false);
		ox = _x - x;
		oy = _y - y;
	}

	public void moveTo(float eventX, float eventY) {
		float x = eventX;
		float y = getY(eventY, false);
		x = getX(x, y, false);
		setPosition(x + ox, y + oy);
		savePosition();
	}

	private static final float countY;
	private static final float maxY;

	static {
		countY = 8;
		maxY = 2 / ((2 / countY + 1) * (2 / countY + 1) - 1);
	}

	private float getY(float y, boolean pbIn) {
		if (pbIn) {
			y = (1 - y); // [0,2]
			y = y / countY + 1; // [1,1.5]
			y = y * y; // [1,2.25]
			y = 1 - (y - 1) * maxY;
		} else {
			y = (1 - y) / maxY + 1;
			y = (float) Math.sqrt(y);
			y = (y - 1) * countY;
			y = 1 - y;
		}

		return y;
	}

	@Override
	public int compareTo(Sprite<E> another) {
		if (_bMoving != another._bMoving) {
			if (_bMoving) {
				return 1;
			}
			return -1;
		}
		int iComp = _xy - another._xy;
		if (iComp == 0) {
			iComp = _id - another._id;
		}
		return iComp;
	}

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		this._id = id;
	}

	public void savePosition() {
		_sx = _x;
		_sy = _y;
	}

	public boolean isPositionChanged() {
		_ex = _x;
		_ey = _y;
		_dx = _ex - _sx;
		_dy = _ey - _sy;
		_d = Math.min(1, _dx * _dx + _dy * _dy);
		_bMoving = _d > 0.01f;
		return _bMoving;
	}

	public boolean setMovePosition(float f) {
		f = f / _d;
		if (f < 1) {
			setPosition(_sx + _dx * f, _sy + _dy * f);
			return true;
		}
		_d = 0;
		_bMoving = false;
		setPosition(_ex, _ey);
		return false;
	}

}