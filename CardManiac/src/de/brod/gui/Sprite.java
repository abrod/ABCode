package de.brod.gui;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.List;
import java.util.Vector;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;

public class Sprite implements Comparable<Sprite> {

	private static final int[] pos = { 0, 6, 9, 3 };

	private Texture tex;

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

	private final List<Sprite> lstChildren = new Vector<Sprite>();

	private float x, y, w, h, touchX, touchY;
	private float dx, dy, xSave, ySave;

	private float[] position = new float[12];;

	private int id, sid;

	private boolean moving, moveable = true;

	private boolean center = true;

	private boolean sliding;

	private float len;

	public Sprite() {
		// this(null, 0, 0);
		tex = null;
		moveable = false;
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
		setCell(0, 0);

		for (int i = 0; i < 12; i++) {
			position[i] = 0;
		}

		setSize(width, height);

		short[] indices = new short[] { 0, 1, 2, 1, 3, 2 };
		setIndices(indices);
	}

	/**
	 * @param location
	 * @param object
	 * @see java.util.Vector#add(int, java.lang.Object)
	 */
	public void add(int location, Sprite object) {
		lstChildren.add(location, object);
	}

	/**
	 * @param object
	 * @return
	 * @see java.util.Vector#add(java.lang.Object)
	 */
	public boolean add(Sprite object) {
		return lstChildren.add(object);
	}

	/**
	 * 
	 * @see java.util.Vector#clear()
	 */
	public void clear() {
		lstChildren.clear();
	}

	@Override
	public int compareTo(Sprite another) {
		if (sliding != another.sliding) {
			if (!another.sliding) {
				return 1;
			}
			return -1;
		}
		if (moving != another.moving) {
			if (!another.moving) {
				return 1;
			}
			return -1;
		}
		if (moveable != another.moveable) {
			if (!another.moveable) {
				return 1;
			}
			return -1;
		}

		int c = sid - another.sid;
		if (c == 0) {
			c = id - another.id;
		}
		return c;
	}

	/**
	 * Render the mesh.
	 * 
	 * @param gl
	 *            the OpenGL context to render to.
	 */
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
		// ... end new part.
		// draw the children
		for (Sprite child : lstChildren) {
			child.draw(gl);
		}
	}

	/**
	 * @param location
	 * @return
	 * @see java.util.Vector#get(int)
	 */
	public Sprite get(int location) {
		return lstChildren.get(location);
	}

	protected void getAllSprites(List<Sprite> plstSprites) {
		for (Sprite child : lstChildren) {
			if (child.tex != null) {
				plstSprites.add(child);
			}
			child.getAllSprites(plstSprites);
		}
	}

	public List<Sprite> getChildren() {
		return lstChildren;
	}

	public boolean isCenter() {
		return center;
	}

	public boolean isMoveable() {
		return moveable;
	}

	public boolean isMoving() {
		return moving;
	}

	/**
	 * @param location
	 * @return
	 * @see java.util.Vector#remove(int)
	 */
	public Sprite remove(int location) {
		return lstChildren.remove(location);
	}

	/**
	 * @param object
	 * @return
	 * @see java.util.Vector#remove(java.lang.Object)
	 */
	public boolean remove(Object object) {
		return lstChildren.remove(object);
	}

	public boolean resetPosition() {
		sliding = x != xSave || y != ySave;
		if (sliding) {
			dx = x - xSave;
			dy = y - ySave;
			len = Math.max(0.01f,
					Math.min(1, (float) Math.sqrt(dx * dx + dy * dy)));
			setPosition(xSave, ySave);
		}
		return sliding;
	}

	public void savePosition() {
		xSave = x;
		ySave = y;
		sliding = false;
	}

	/**
	 * Set the texture coordinates.
	 * 
	 * @param textureCoords
	 */
	public void setCell(float px, float py) {
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
		mTextureId = tex.setBuffer(mTextureBuffer, px, py);
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
	protected void setColor(float red, float green, float blue, float alpha) {
		mRGBA[0] = red;
		mRGBA[1] = green;
		mRGBA[2] = blue;
		mRGBA[3] = alpha;
		mColorBuffer = null;
		for (Sprite child : lstChildren) {
			child.setColor(red, green, blue, alpha);
		}
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
	protected void setColors(float[] colors) {
		// float has 4 bytes.
		ByteBuffer cbb = ByteBuffer.allocateDirect(colors.length * 4);
		cbb.order(ByteOrder.nativeOrder());
		mColorBuffer = cbb.asFloatBuffer();
		mColorBuffer.put(colors);
		mColorBuffer.position(0);
		for (Sprite child : lstChildren) {
			child.setColors(colors);
		}
	}

	public void setDimension(float px, float py, float width, float height) {
		x = px;
		y = py;
		setSize(width, height);
	}

	public void setId(int pId) {
		id = pId;
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

	public void setMoveable(boolean moveable) {
		this.moveable = moveable;
	}

	public void setMoving(boolean moving) {
		this.moving = moving;
	}

	public void setPosition(float px, float py) {
		x = px;
		y = py;
		sid = (int) ((x - y * 2) * 1000);
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
		w = width / 2;
		h = height / 2;

		setPosition(x, y);
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

	/**
	 * @return
	 * @see java.util.Vector#size()
	 */
	public int size() {
		return lstChildren.size();
	}

	public boolean slide(float d) {
		d = d / len;
		if (d > 1) {
			d = 1;
		}
		setPosition(xSave + d * dx, ySave + d * dy);

		sliding = d < 1;
		return !sliding;
	}

	boolean touches(float px, float py) {
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
