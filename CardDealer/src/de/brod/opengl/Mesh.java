package de.brod.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.opengl.GLUtils;
import android.opengl.Matrix;

public class Mesh {

	private final float[] calculationatrix = new float[16];

	private FloatBuffer vertexBuffer = null;

	private ShortBuffer indexBuffer = null;

	private FloatBuffer textureBuffer;

	private int textureId = -1;

	private Bitmap bitmap;

	private boolean loadTexture = false;

	private int numberOfIndices = -1;

	private final float[] color = new float[] { 1.0f, 1.0f, 1.0f, 1.0f };

	private FloatBuffer colorBuffer = null;

	private float[] position = { 0, 0, 0 };

	private float[] angles = { 0, 0, 0 };

	private float[] vertices, verticesCalc;

	private boolean dirty, doRotate;

	private float[] resultVec = { 0, 0, 0, 0 };

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

	/**
	 * Render the mesh.
	 *
	 * @param gl
	 *            the OpenGL context to render to.
	 */
	public void draw(GL10 gl) {

		if (dirty) {
			calculateVertexBuffer();
		}
		drawEnable(gl);

		// set the vertexBuffer
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

		drawSetColor(gl);

		drawSetTexture(gl);

		// set the x,y, position
		// gl.glTranslatef(x, y, z);

		// finally draw the elements
		gl.glDrawElements(GL10.GL_TRIANGLES, numberOfIndices, GL10.GL_UNSIGNED_SHORT, indexBuffer);

		drawDisable(gl);
	}

	private void drawDisable(GL10 gl) {
		// Disable the vertices buffer.
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);

		if (textureId != -1 && textureBuffer != null) {
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}

		// Disable face culling.
		gl.glDisable(GL10.GL_CULL_FACE);
	}

	private void drawEnable(GL10 gl) {
		// Counter-clockwise winding.
		gl.glFrontFace(GL10.GL_CCW);
		// Enable face culling.
		gl.glEnable(GL10.GL_CULL_FACE);
		// What faces to remove with the face culling.
		gl.glCullFace(GL10.GL_BACK);
		// Enabled the vertices buffer for writing and to be used during
		// rendering.
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
	}

	private void drawSetColor(GL10 gl) {
		// Set flat color
		gl.glColor4f(color[0], color[1], color[2], color[3]);
		// Smooth color
		if (colorBuffer != null) {
			// Enable the color array buffer to be used during rendering.
			gl.glEnableClientState(GL10.GL_COLOR_ARRAY);
			gl.glColorPointer(4, GL10.GL_FLOAT, 0, colorBuffer);
		}
	}

	private void drawSetTexture(GL10 gl) {
		if (loadTexture) {
			loadGLTexture(gl);
			loadTexture = false;
		}
		if (textureId != -1 && textureBuffer != null) {
			gl.glEnable(GL10.GL_TEXTURE_2D);
			// Enable the texture state
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			// Point to our buffers
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);
		}
	}

	/**
	 * Set the bitmap to load into a texture.
	 *
	 * @param bitmap
	 */
	public void loadBitmap(Bitmap bitmap) { // New function.
		this.bitmap = bitmap;
		loadTexture = true;
	}

	/**
	 * Loads the texture.
	 *
	 * @param gl
	 */
	private void loadGLTexture(GL10 gl) { // New function
		// Generate one texture pointer...
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		textureId = textures[0];

		// ...and bind it to our array
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		// Create Nearest Filtered Texture
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		// Different possible texture parameters, e.g. GL10.GL_CLAMP_TO_EDGE
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_REPEAT);

		// Use the Android GLUtils to specify a two-dimensional texture image
		// from our bitmap
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
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
	protected void setColors(float... colors) {
		colorBuffer = createFloatBuffer(colors);
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
		indexBuffer = ibb.asShortBuffer();
		indexBuffer.put(indices);
		indexBuffer.position(0);
		numberOfIndices = indices.length;
	}

	public void setPosition(float x, float y, float z) {
		position[0] = x;
		position[1] = y;
		position[2] = z;
		dirty = true;
	}

	private void setRotateFlag() {
		dirty = true;
		doRotate = false;
		for (float f : angles) {
			if (f != 0f) {
				doRotate = true;
				break;
			}
		}
	}

	public void setRotateX(float rotateX) {
		this.angles[0] = rotateX;
		setRotateFlag();
	}

	public void setRotateY(float rotateY) {
		this.angles[1] = rotateY;
		setRotateFlag();
	}

	public void setRotateZ(float rotateZ) {
		this.angles[2] = rotateZ;
		setRotateFlag();
	}

	/**
	 * Set the texture coordinates.
	 *
	 * @param textureCoords
	 */
	protected void setTextureCoordinates(float[] textureCoords) {
		textureBuffer = createFloatBuffer(textureCoords);
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
}