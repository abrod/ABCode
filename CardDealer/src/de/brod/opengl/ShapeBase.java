package de.brod.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.opengl.Matrix;

abstract class ShapeBase implements Shape {

	private final float[]	calculationatrix	= new float[16];

	private FloatBuffer		vertexBuffer		= null;

	private ShortBuffer		indexBufferUp, indexBufferDown = null;

	private FloatBuffer		textureBufferUp, textureBufferDown;

	GLGrid					grid				= null;

	private int				numberOfIndices		= -1;

	private final float[]	color				= new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

	private FloatBuffer		colorBuffer			= null;

	private float[]			position			= { 0, 0, 0 };

	private float[]			angles				= { 0, 0, 0 };

	private float[]			vertices, verticesCalc;

	private boolean			dirty, doRotate;

	private float[]			resultVec			= { 0, 0, 0, 0 };
	private float[]			range				= { 0, 0, 0, 0 };

	private float			dx, dy;

	private boolean			useUpSide;

	private void calculateVertexBuffer() {
		if (doRotate) {
			Matrix.setIdentityM(calculationatrix, 0);
			for (int i = 0; i < angles.length; i++) {
				if (angles[i] != 0) {
					Matrix.rotateM(calculationatrix, 0, angles[i], i == 0 ? 1 : 0, i == 1 ? 1 : 0, i == 2 ? 1 : 0);
				}
			}
		}

		for (int i = 0; i < vertices.length;) {
			System.arraycopy(vertices, i, resultVec, 0, 3);
			if (doRotate) {
				Matrix.multiplyMV(resultVec, 0, calculationatrix, 0, resultVec, 0);
			}
			for (int j = 0; j < 3; j++) {
				verticesCalc[i] = resultVec[j] + position[j];
				i++;
			}
		}
		if (vertexBuffer == null) {
			ByteBuffer byteBuf = ByteBuffer.allocateDirect(vertices.length * 4);
			byteBuf.order(ByteOrder.nativeOrder());
			vertexBuffer = byteBuf.asFloatBuffer();
		} else {
			vertexBuffer.position(0);
		}
		vertexBuffer.put(verticesCalc);
		for (int i = 0; i < verticesCalc.length; i += 3) {
			float x = verticesCalc[i];
			float y = verticesCalc[i + 1];
			if (i == 0) {
				range[0] = x;
				range[1] = x;
				range[2] = y;
				range[3] = y;
			} else {
				range[0] = Math.min(range[0], x);
				range[1] = Math.max(range[1], x);
				range[2] = Math.min(range[2], y);
				range[3] = Math.max(range[3], y);
			}
		}
		vertexBuffer.position(0);
		dirty = false;
	}

	private FloatBuffer createFloatBuffer(float[] values) {
		ByteBuffer byteBuf = ByteBuffer.allocateDirect(values.length * 4);
		byteBuf.order(ByteOrder.nativeOrder());
		FloatBuffer floatBuffer = byteBuf.asFloatBuffer();
		floatBuffer.put(values);
		floatBuffer.position(0);
		return floatBuffer;
	}

	private FloatBuffer createFloatBuffer(GLGrid grid, int x1, int y1) {
		float xMin = grid.getX(x1);
		float xMax = grid.getX(x1 + 1);
		float yMin = grid.getY(y1);
		float yMax = grid.getY(y1 + 1);
		FloatBuffer fb = createFloatBuffer(getTextureCoords(xMin, yMin, xMax, yMax));
		return fb;
	}

	/*
	 * (non-Javadoc)
	 * @see de.brod.opengl.IMesh#draw(javax.microedition.khronos.opengles.GL10)
	 */
	@Override
	public void draw(GL10 gl) {

		if (dirty) {
			calculateVertexBuffer();
		}

		// set the vertexBuffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

		drawSetColor(gl);

		drawSetTexture(gl);

		// set the x,y, position
		// gl.glTranslatef(x, y, z);

		// finally draw the elements
		if (useUpSide) {
			gl.glDrawElements(GL10.GL_TRIANGLES, numberOfIndices, GL10.GL_UNSIGNED_SHORT, indexBufferUp);
		} else {
			gl.glDrawElements(GL10.GL_TRIANGLES, numberOfIndices, GL10.GL_UNSIGNED_SHORT, indexBufferDown);
		}

		drawDisable(gl);
	}

	private void drawDisable(GL10 gl) {

	}

	private void drawSetColor(GL10 gl) {
		// Set flat color
		gl.glColor4f(color[0], color[1], color[2], color[3]);
		// Smooth color
		if (colorBuffer != null) {
			// Enable the color array buffer to be used during rendering.
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
		} else {
			gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
		}
	}

	private void drawSetTexture(GL10 gl) {
		FloatBuffer textureBuffer = useUpSide ? textureBufferUp : textureBufferDown;
		if (grid != null && textureBuffer != null) {
			int textureId = grid.getTextureId(gl);
			gl.glEnable(GL10.GL_TEXTURE_2D);
			// Enable the texture state
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			// Point to our buffers
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
		} else {
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
			gl.glDisable(GL10.GL_TEXTURE_2D);
		}
	}

	@Override
	public float getPosition() {
		return position[1] - position[0] * 2 + position[2] * 10;
	}

	protected abstract float[] getTextureCoords(float xMin, float yMin, float xMax, float yMax);

	@Override
	public float getX() {
		return position[0];
	}

	@Override
	public float getY() {
		return position[1];
	}

	/*
	 * (non-Javadoc)
	 * @see de.brod.opengl.IMesh#moveTo(float, float, float)
	 */
	@Override
	public void moveTo(float x, float y) {
		position[0] = x + dx;
		position[1] = y + dy;
		dirty = true;
	}

	/**
	 * Set one flat color on the mesh.
	 *
	 * @param red
	 * @param green
	 * @param blue
	 * @param alpha
	 */
	public void setColor(float red, float green, float blue, float alpha) {
		color[0] = red;
		color[1] = green;
		color[2] = blue;
		color[3] = alpha;
	}

	/**
	 * Set the colors
	 *
	 * @param colors
	 */
	public void setColors(float... colors) {
		colorBuffer = createFloatBuffer(colors);
	}

	protected void setGrid(GLGrid grid, int x1, int y1, int x2, int y2) {
		this.grid = grid;
		textureBufferUp = createFloatBuffer(grid, x1, y1);
		textureBufferDown = createFloatBuffer(grid, x2, y2);
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
		indexBufferUp = ibb.asShortBuffer();
		indexBufferUp.put(indices);
		indexBufferUp.position(0);

		ibb = ByteBuffer.allocateDirect(indices.length * 2);
		ibb.order(ByteOrder.nativeOrder());
		indexBufferDown = ibb.asShortBuffer();
		for (int i = indices.length - 1; i >= 0; i--) {
			indexBufferDown.put(indices[i]);
		}
		indexBufferDown.position(0);

		numberOfIndices = indices.length;
	}

	/*
	 * (non-Javadoc)
	 * @see de.brod.opengl.IMesh#setPosition(float, float, float)
	 */
	@Override
	public void setPosition(float x, float y, float z) {
		position[0] = x;
		position[1] = y;
		position[2] = z;
		dirty = true;
	}

	private void setRotateFlag() {
		dirty = true;
		doRotate = false;
		useUpSide = true;
		for (int i = 0; i < angles.length; i++) {
			float f = angles[i];
			if (i < 2 && f > 90 && f < 270) {
				useUpSide = !useUpSide;
			}
			if (f != 0f) {
				doRotate = true;
				break;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see de.brod.opengl.IMesh#setRotateX(float)
	 */
	@Override
	public void setRotateX(float rotateX) {
		this.angles[0] = rotateX % 360;
		setRotateFlag();
	}

	/*
	 * (non-Javadoc)
	 * @see de.brod.opengl.IMesh#setRotateY(float)
	 */
	@Override
	public void setRotateY(float rotateY) {
		this.angles[1] = rotateY % 360;
		setRotateFlag();
	}

	/*
	 * (non-Javadoc)
	 * @see de.brod.opengl.IMesh#setRotateZ(float)
	 */
	@Override
	public void setRotateZ(float rotateZ) {
		this.angles[2] = rotateZ % 360;
		setRotateFlag();
	}

	/**
	 * Set the vertices.
	 *
	 * @param vertices
	 */
	protected void setVertices(float[] vertices) {
		this.vertices = vertices;
		verticesCalc = new float[vertices.length];
		dirty = true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.brod.opengl.IMesh#touch(float, float)
	 */
	@Override
	public boolean touch(float x, float y) {
		dx = position[0] - x;
		dy = position[1] - y;
		if (x < range[0] || x > range[1]) {
			return false;
		}
		if (y < range[2] || y > range[3]) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see de.brod.opengl.IMesh#untouch()
	 */
	@Override
	public void untouch() {
		dx = 0;
		dy = 0;
	}
}