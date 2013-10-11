package de.brod.gui.shape;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import de.brod.gui.Texture;

public class Sprite extends Container {

	private static final int[] pos = { 0, 6, 9, 3 };

	// Our vertex buffer.
	private FloatBuffer mVerticesBuffer = null;

	// Our index buffer.
	private ShortBuffer mIndicesBuffer = null;
	// Our UV texture buffer.
	private FloatBuffer mTextureBuffer; // New variable.

	// The number of indices.
	private int mNumOfIndices = -1, mTextureId;

	// Flat Color
	private final float[] mRGBA = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

	// Smooth Colors
	private FloatBuffer mColorBuffer = null;

	private float x, y, w, wd, h, touchX, touchY;
	private float dx, dy, xSave, ySave;

	private float[] position = new float[12];;

	private boolean center = true;

	private float len;

	private float da, angle = 0, angleSave = 0;

	private boolean bShowBackSide = false;

	public Sprite() {
		// this(null, 0, 0);
		tex = null;
		setMoveable(false);
	}

	/**
	 * Create a plane.
	 *
	 * @param width
	 *            the width of the plane.
	 * @param height
	 *            the height of the plane.
	 * @param pTex
	 */
	public Sprite(Texture pTex, float width, float height) {
		tex = pTex;
		setCell(0, 0, 0, 0);

		for (int i = 0; i < 12; i++) {
			position[i] = 0;
		}

		setSize(width, height);

		short[] indices = new short[] { 0, 1, 2, 1, 3, 2 };
		setIndices(indices);
	}

	/**
	 * Render the mesh.
	 *
	 * @param gl
	 *            the OpenGL context to render to.
	 */
	@Override
	public void draw(GL10 gl) {
		if (mVerticesBuffer != null) {
			// coordinates to use when rendering.
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mVerticesBuffer);

			// Smooth color
			if (mColorBuffer != null) {
				// Enable the color array buffer to be used during rendering.
				gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
				gl.glColorPointer(4, GL10.GL_FLOAT, 0, mColorBuffer);
			} else {
				// Set flat color
				gl.glColor4f(mRGBA[0], mRGBA[1], mRGBA[2], mRGBA[3]);
			}

			if (mTextureBuffer != null) {

				// Point to our buffers
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTextureBuffer);
				gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureId);
			} else {
				gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			}
			// ... end new part.

			// Point out the where the color buffer is.
			gl.glDrawElements(GL10.GL_TRIANGLES, mNumOfIndices,
					GL10.GL_UNSIGNED_SHORT, mIndicesBuffer);
			// New part...
			if (mTextureBuffer == null) {
				gl.glEnable(GL10.GL_TEXTURE_2D);
				// Enable the texture state
				gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			}
		}

		super.draw(gl);
	}

	public boolean isCenter() {
		return center;
	}

	@Override
	public boolean resetPosition() {
		setSliding(x != xSave || y != ySave || angle != angleSave);
		if (isSliding()) {
			dx = x - xSave;
			dy = y - ySave;
			da = angle - angleSave;

			len = Math.max(0.001f,
					Math.min(1, (float) Math.sqrt(dx * dx + dy * dy)));
			if (da != 0) {
				setInternalAngle(angleSave);
				len = 1;
			}
			setPosition(xSave, ySave);
		}
		return isSliding();
	}

	@Override
	public void savePosition() {
		xSave = x;
		ySave = y;
		angleSave = angle;
		setSliding(false);
	}

	private float pxCell, pyCell, pxCellBack, pyCellBack;

	private float cos = 1;

	/**
	 * Set the texture coordinates.
	 *
	 * @param textureCoords
	 */
	public void setCell(float px, float py, float pxBack, float pyBack) {

		pxCell = px;
		pyCell = py;
		pxCellBack = pxBack;
		pyCellBack = pyBack;
		setCell();
	}

	private void setCell() {
		if (tex == null) {
			return;
		}

		// float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		if (mTextureBuffer == null) {
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(8 * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			mTextureBuffer = byteBuf.asFloatBuffer();
		} else {
			mTextureBuffer.position(0);
		}
		if (bShowBackSide) {
			mTextureId = tex.setBuffer(mTextureBuffer, pxCellBack, pyCellBack);
		} else {
			mTextureId = tex.setBuffer(mTextureBuffer, pxCell, pyCell);
		}
		mTextureBuffer.position(0);
	}

	public void setCenter(boolean center) {
		this.center = center;
	}

	/**
	 * Set one flat color on the mesh.
	 *
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	@Override
	public void setColor(float red, float green, float blue, float alpha) {
		mRGBA[0] = red;
		mRGBA[1] = green;
		mRGBA[2] = blue;
		mRGBA[3] = alpha;
		mColorBuffer = null;
		super.setColor(red, green, blue, alpha);
	}

	public void setColor(int color) {
		float r = Color.red(color);
		float g = Color.green(color);
		float b = Color.blue(color);
		float a = Color.alpha(color);
		setColor(r / 255, g / 255, b / 255, a / 255);
	}

	/**
	 * Set the colors
	 *
	 * @param colors
	 */
	@Override
	public void setColors(float[] colors) {
		// float has 4 bytes.
		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		cbb.order(ByteOrder.nativeOrder());
		mColorBuffer = cbb.asFloatBuffer();
		mColorBuffer.put(colors);
		mColorBuffer.position(0);
		super.setColors(colors);
	}

	public void setDimension(float px, float py, float width, float height) {
		x = px;
		y = py;
		setSize(width, height);
	}

	/**
	 * Set the indices.
	 *
	 * @param indices
	 */
	protected void setIndices(short[] indices) {
		// short is 2 bytes, therefore we multiply the number if
		// vertices with 2.
		ByteBuffer ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		mIndicesBuffer = ibb.asShortBuffer();
		mIndicesBuffer.put(indices);
		mIndicesBuffer.position(0);
		mNumOfIndices = indices.length;
	}

	public void setPosition(float px, float py) {
		x = px;
		y = py;
		setSid((int) ((x - y * 2) * 1000));
		if (center) {
			position[0] = x - w;
			position[1] = y - h;

			position[3] = x + w;
			position[4] = position[1];

			position[6] = position[0];
			position[7] = y + h;

			position[9] = position[3];
			position[10] = position[7];
		} else {
			position[0] = x;
			position[1] = y;

			position[3] = x + w * 2;
			position[4] = position[1];

			position[6] = position[0];
			position[7] = y + h * 2;

			position[9] = position[3];
			position[10] = position[7];
		}
		setVertices(position);
	}

	public void setSize(float width, float height) {
		wd = width / 2;
		h = height / 2;

		setAngle(angle);
	}

	public void setAngle(float angle2) {
		setInternalAngle(angle2);
		setPosition(x, y);
	}

	public void setInternalAngle(float angle2) {
		if (angle != angle2) {
			angle = angle2;
			cos = (float) Math.cos(Math.toRadians(angle2));
			boolean bCos0 = cos < 0;
			if (bCos0 != bShowBackSide) {
				bShowBackSide = bCos0;
				setCell();
			}
			if (bCos0) {
				cos = -cos;
			}
		}
		w = wd * cos;
	}

	protected void setTextureBuffer(int x1, int y1, int x2, int y2) {
		if (tex == null) {
			return;
		}
		// float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		if (mTextureBuffer == null) {
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(8 * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			mTextureBuffer = byteBuf.asFloatBuffer();
		} else {
			mTextureBuffer.position(0);
		}
		mTextureId = tex.setBuffer(mTextureBuffer, x1, y1, x2, y2);
		mTextureBuffer.position(0);

	}

	/**
	 * Set the vertices.
	 *
	 * @param vertices
	 */
	protected void setVertices(float[] vertices) {
		// a float is 4 bytes, therefore we multiply the number if
		// vertices with 4.
		if (mVerticesBuffer == null) {
			ByteBuffer vbb = ByteBuffer.allocateDirect(vertices.length * 4);
			vbb.order(ByteOrder.nativeOrder());
			mVerticesBuffer = vbb.asFloatBuffer();
		} else {
			mVerticesBuffer.position(0);
		}
		mVerticesBuffer.put(vertices);
		mVerticesBuffer.position(0);
	}

	@Override
	public boolean slide(float d) {
		d = d / len;
		if (d > 1) {
			d = 1;
		}
		if (da != 0) {
			setInternalAngle(angleSave + d * da);
		}
		setPosition(xSave + d * dx, ySave + d * dy);

		setSliding(d < 1);
		return !isSliding();
	}

	@Override
	public boolean touches(float px, float py) {
		if (tex == null) {
			return false;
		}
		touchX = px - x;
		touchY = py - y;
		boolean oddNodes = false;
		float x2 = position[3];
		float y2 = position[4];
		float x1, y1;

		for (int i = 0; i < 4; i++) {
			x1 = position[pos[i]];
			y1 = position[pos[i] + 1];
			if (((y1 < py) && (y2 >= py)) || (y1 >= py) && (y2 < py)) {
				if ((py - y1) / (y2 - y1) * (x2 - x1) < (px - x1)) {
					oddNodes = !oddNodes;
				}
			}
			x2 = x1;
			y2 = y1;
		}
		return oddNodes;
	}

	public void touchMove(float px, float py) {
		setPosition(px - touchX, py - touchY);
	}

}
