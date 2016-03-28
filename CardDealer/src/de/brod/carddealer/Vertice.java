package de.brod.carddealer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Arrays;

import android.opengl.GLES20;
import android.opengl.Matrix;

public class Vertice {

	/**
	 * Store the model matrix. This matrix is used to move models from object
	 * space (where each model can be thought of being located at the center of
	 * the universe) to world space.
	 */
	private float[] mModelMatrix = new float[16];
	/**
	 * Allocate storage for the final combined matrix. This will be passed into
	 * the shader program.
	 */
	// private float[] mMVPMatrix = new float[16];

	/** How many bytes per float. */
	private final int bytesPerFloat = 4;

	FloatBuffer positionBuffer, colorBuffer;

	private float[] angle = { 0.0f, 0.0f, 0.0f };

	private float x, y, z;

	private boolean dirtyFlag = true;
	private int amountOfTriangleEdges;
	private float[] initPositionsData, initColorsData;
	private float[] trianglePositions, trianglesColors;
	private float size = 1f;

	public Vertice(float[] newPoints) {
		initPositionsData = newPoints;

		initColorsData = new float[(newPoints.length / 3) * 4];
		Arrays.fill(initColorsData, 1f);

		initPositions();
		initColors();
	}

	private void calculateModelMatrix() {
		Matrix.setIdentityM(mModelMatrix, 0);
		// set the position
		Matrix.translateM(mModelMatrix, 0, x, y, z);
		// execute transformation
		if (angle[2] != 0.0f) {
			Matrix.rotateM(mModelMatrix, 0, angle[2], 0.0f, 0.0f, 1.0f);
		}
		if (angle[0] != 0.0f) {
			Matrix.rotateM(mModelMatrix, 0, angle[0], 0.1f, 0.0f, 0.0f);
		}
		if (angle[1] != 0.0f) {
			Matrix.rotateM(mModelMatrix, 0, angle[1], 0.0f, 0.1f, 0.0f);
		}

		// This multiplies the modelview matrix by the projection matrix, and
		// stores the result in the MVP matrix
		// (which now contains model * view * projection).
		Matrix.multiplyMM(mModelMatrix, 0, GLProgram.translationMatrix, 0, mModelMatrix, 0);

		dirtyFlag = false;
	}

	private float[] createTriangles(float[] verticesData, int itemSize) {
		int countPoints = verticesData.length / itemSize;

		// if there are more than 3 items
		if (countPoints > 3) {
			int verticesSize = itemSize * 3;
			int verticesAmount = (countPoints - 2) * verticesSize;
			// copy first vertices
			float[] f = Arrays.copyOf(verticesData, verticesAmount);
			int offsetNew = verticesSize;
			int offsetOrg = verticesSize;
			while (offsetNew < f.length) {
				// copy last item
				System.arraycopy(f, offsetNew - itemSize, f, offsetNew, itemSize);
				offsetNew += itemSize;
				// copy next item
				System.arraycopy(verticesData, offsetOrg, f, offsetNew, itemSize);
				offsetNew += itemSize;
				offsetOrg += itemSize;
				// copy initial item
				System.arraycopy(f, 0, f, offsetNew, itemSize);
				offsetNew += itemSize;
			}
			return f;
		}
		return verticesData;
	}

	void draw() {

		// Pass in the position information
		enableVertexAttributes(GLProgram.a_position_id, 3, positionBuffer);

		// Pass in the color information
		enableVertexAttributes(GLProgram.a_color_id, 4, colorBuffer);

		GLES20.glUniformMatrix4fv(GLProgram.u_MVPMatrix_id, 1, false, getModelMatrix(), 0);
		GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, getAmountOfEdges());
	}

	private void enableVertexAttributes(int handleId, int size, FloatBuffer verticeBuffer) {
		verticeBuffer.position(0);
		GLES20.glVertexAttribPointer(handleId, size, GLES20.GL_FLOAT, false, size * 4, verticeBuffer);
		GLES20.glEnableVertexAttribArray(handleId);
	}

	private void fillPositionBuffer() {
		positionBuffer.position(0);
		positionBuffer.put(resize(trianglePositions, size));
		positionBuffer.position(0);
	}

	public int getAmountOfEdges() {
		return amountOfTriangleEdges;
	}

	float[] getModelMatrix() {
		if (dirtyFlag) {
			calculateModelMatrix();
		}

		return mModelMatrix;
	}

	private void initColors() {
		trianglesColors = createTriangles(initColorsData, 4);
		if (colorBuffer == null) {
			colorBuffer = ByteBuffer.allocateDirect(trianglesColors.length * bytesPerFloat)
					.order(ByteOrder.nativeOrder()).asFloatBuffer();
		}
		// fill the buffer
		colorBuffer.position(0);
		colorBuffer.put(trianglesColors).position(0);
	}

	private void initPositions() {
		trianglePositions = createTriangles(initPositionsData, 3);

		amountOfTriangleEdges = trianglePositions.length / 3;
		// fill the buffer
		positionBuffer = ByteBuffer.allocateDirect(trianglePositions.length * bytesPerFloat)
				.order(ByteOrder.nativeOrder()).asFloatBuffer();
		fillPositionBuffer();
	}

	private float[] resize(float[] verticeData, float f) {
		if (f == 1f) {
			return verticeData;
		} else {
			float[] copyOf = Arrays.copyOf(verticeData, verticeData.length);
			for (int j = 0; j < copyOf.length; j++) {
				copyOf[j] *= f;
			}
			return copyOf;
		}
	}

	public Vertice setAngle(float x, float y, float z) {
		if (angle[0] != x) {
			angle[0] = x;
			dirtyFlag = true;
		}
		if (angle[1] != y) {
			angle[1] = y;
			dirtyFlag = true;
		}
		if (angle[2] != z) {
			angle[2] = z;
			dirtyFlag = true;
		}
		return this;
	}

	public Vertice setColors(float... newPoints) {
		if (newPoints.length >= 4) {
			for (int i = 0; i < initColorsData.length; i++) {
				initColorsData[i] = newPoints[i % newPoints.length];
			}
			initColors();
		}
		return this;
	}

	public void setDirtyFlag() {
		dirtyFlag = true;
	}

	public Vertice setPosition(float x, float y, float z) {
		if (this.x != x) {
			this.x = x;
			dirtyFlag = true;
		}
		if (this.y != y) {
			this.y = y;
			dirtyFlag = true;
		}
		if (this.z != z) {
			this.z = z;
			dirtyFlag = true;
		}
		return this;
	}

	public Vertice setSize(float f) {
		size = f;
		fillPositionBuffer();
		return this;
	}
}
