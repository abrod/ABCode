package de.brod.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

public abstract class Mesh<E> {

	private static final float	countY;
	private static final float	maxY;
	static {
		countY = 8;
		maxY = 2 / ((2 / countY + 1) * (2 / countY + 1) - 1);
	}

	protected int				_id;

	private float				_width, _height;
	float						_x, _y;
	int							_xy;
	private FloatBuffer			mTextureBuffer;

	private int					mTextureId;

	private float				r			= 1, g = 1, b = 1, a = 1;
	float[]						range		= new float[4];
	private FloatBuffer			vertexBuffer;

	private float[]				vertices	= new float[] { -1.0f, -1.0f, 0.0f,
			-1.0f, 1.0f, 0.0f, 1.0f, -1.0f, 0.0f, 1.0f, 1.0f, 0.0f };

	private boolean				visible		= true;
	private int					iTextureCounter;
	private float				_rotX;

	public Mesh() {
		ByteBuffer bb1 = ByteBuffer.allocateDirect(vertices.length * 4);
		bb1.order(ByteOrder.nativeOrder());
		vertexBuffer = bb1.asFloatBuffer();
	}

	public void draw(GL10 gl) {
		if (!visible) {
			return;
		}

		gl.glColor4f(r, g, b, a);

		gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);

	}

	float getX(float x, float y, boolean pbIn) {
		if (OpenGLView.LANDSCAPE) {
			if (pbIn) {
				return x * (5f - y) / 6f;
			}
			return x / (5f - y) * 6f;
		}
		return x;
	}

	float getY(float y, boolean pbIn) {
		if (!OpenGLView.LANDSCAPE) {
			return y;
		} else if (pbIn) {
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

	public boolean isVisible() {
		return visible;
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

	public void setId(int piId) {
		_id = piId;
	}

	public void setPosition(float px, float py, float rotX) {
		if (_x != px || _y != py || rotX != _rotX) {
			setXY(px, py, rotX);
		}
	}

	protected void setSize(float wd, float hg) {
		_width = wd;
		_height = hg;
	}

	/**
	 * Set the texture coordinates.
	 *
	 * @param textureCoords
	 * @param piTextureId
	 */
	protected void setTextureCoordinates(int piCounter, int piTextureId,
			float[] textureCoords) {
		if (mTextureBuffer == null || mTextureId != piTextureId
				|| iTextureCounter != piCounter) {
			// New
			// function.
			// float is 4 bytes, therefore we multiply the number if
			// vertices with 4.
			ByteBuffer byteBuf = ByteBuffer
					.allocateDirect(textureCoords.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			mTextureBuffer = byteBuf.asFloatBuffer();
			mTextureBuffer.put(textureCoords);
			mTextureBuffer.position(0);
			mTextureId = piTextureId;
			iTextureCounter = piCounter;
		}
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	class Line {
		float	x, y, dx, dy;

		void set(float x1, float y1, float x2, float y2) {
			x = x1;
			y = y1;
			dx = x2 - x1;
			dy = y2 - y1;
		}

		float getX(float d) {
			return x + dx * d;
		}

		float getY(float d) {
			return y + dy * d;
		}
	}

	private Line	line	= new Line();

	protected void setXY(float px, float py, float rotX) {
		_x = px;
		_y = py;
		_rotX = rotX;
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
		if (_rotX > 0) {
			float r2 = 1 - _rotX;
			for (int i = 0; i <= 3; i += 3) {
				int i1 = i + 1;
				int i2 = i + 6;
				int i3 = i2 + 1;
				line.set(vertices[i], vertices[i1], vertices[i2], vertices[i3]);
				vertices[i] = line.getX(_rotX);
				vertices[i1] = line.getY(_rotX);
				vertices[i2] = line.getX(r2);
				vertices[i3] = line.getY(r2);
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
}
